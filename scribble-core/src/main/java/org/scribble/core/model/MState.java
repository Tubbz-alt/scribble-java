/**
 * Copyright 2008 The Scribble Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.scribble.core.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.scribble.core.type.kind.ProtoKind;
import org.scribble.util.RuntimeScribException;
import org.scribble.util.ScribException;

public abstract class MState
<
		L,                             // Node label type (cosmetic)
		A extends MAction<K>,          // Edge type
		S extends MState<L, A, S, K>,  // State type
		K extends ProtoKind            // Global/Local
>
{
	private static int count = 1;
	
	public final int id;

	protected final Set<L> labs;  // Was RecVar and SubprotocolSigs, now using inlined protocol for FSM building so just RecVar

	// **: clients should use the pair of getAllAcceptable/getSuccessors for correctness -- getAcceptable/accept don't support non-det
	//protected final LinkedHashMap<A, S> edges;  // Want predictable ordering of entries for e.g. API generation (state enumeration)*/
	protected final List<A> actions;
	protected final List<S> succs;
	
	//Pair ?  rename getAll...
	
	public MState(Set<L> labs)  // Immutable singleton node
	{
		this.id = MState.count++;
		this.labs = new HashSet<>(labs);
		this.actions = new LinkedList<>();
		this.succs = new LinkedList<>();
	}
	
	protected final void addLabel(L lab)
	{
		this.labs.add(lab);
	}
	
	public final Set<L> getLabels()
	{
		return Collections.unmodifiableSet(this.labs);
	}
	
	// Mutable (can also overwrite edges)
	protected void addEdge(A a, S s)
	{
		//this.edges.put(a, s);
		Iterator<A> as = this.actions.iterator();  // Needed?..
		Iterator<S> ss = this.succs.iterator();
		while (as.hasNext())  // Duplicate edges preemptively pruned here, but could leave to later minimisation
		{
			A tmpa = as.next();
			S tmps = ss.next();
			if (tmpa.equals(a) && tmps.equals(s))
			{
				return;
			}
		}  // ..needed?
		this.actions.add(a);
		this.succs.add(s);
	}
	
	protected final void removeEdge(A a, S s) throws ScribException
	{
		Iterator<A> ia = this.actions.iterator();
		Iterator<S> is = this.succs.iterator();
		while (ia.hasNext())
		{
			A tmpa = ia.next();
			S tmps = is.next();
			if (tmpa.equals(a) && tmps.equals(s))
			{
				ia.remove();
				is.remove();
				return;
			}
		}
		//throw new RuntimeException("No such transition to remove: " + a + "->" + s);
		throw new ScribException(
				"No such transition to remove: " + a + "->" + s);
				// Hack? EFSM building on bad-reachability protocols now done before actual reachability check
	}
	
	// The "deterministic" variant, cf., getAllActions
	public final List<A> getActions()
	{
		Set<A> as = new HashSet<>(this.actions);
		if (as.size() != this.actions.size())
		{
			throw new RuntimeScribException("[TODO] Non-deterministic state: "
					+ this.actions + "  (Try -minlts if available)");
					// This getter checks for determinism -- affects e.g. API generation  
		}
		//return as;
		return getAllActions();
	}

	public final List<A> getAllActions()
	{
		return Collections.unmodifiableList(this.actions);
	}
	
	public final boolean hasAction(A a)
	{
		return this.actions.contains(a);
	}

	public S getSuccessor(A a)
	{
		Set<A> as = new HashSet<>(this.actions);
		if (as.size() != this.actions.size())
		{
			throw new RuntimeException("FIXME: " + this.actions);
		}
		return getSuccessors(a).get(0);
	}
	
	public final List<S> getSuccessors()
	{
		Set<A> as = new HashSet<>(this.actions);
		if (as.size() != this.actions.size())
		{
			throw new RuntimeScribException("[TODO] Non-deterministic state: "
					+ this.actions + "  (Try -minlts if available)");
					// This getter checks for determinism -- affects e.g. API generation  
		}
		return getAllSuccessors();
	}

	// For non-deterministic actions
	public final List<S> getSuccessors(A a)
	{
		return IntStream.range(0, this.actions.size())
			.filter(i -> this.actions.get(i).equals(a))
			.mapToObj(i -> this.succs.get(i))
			.collect(Collectors.toList());
	}

	public final List<S> getAllSuccessors()
	{
		return Collections.unmodifiableList(this.succs);
	}

	public final boolean isTerminal()
	{
		return this.actions.isEmpty();
	}

	// CHECKME: add "caching" versions to, e.g., Graphs?
	// N.B. doesn't implicitly include start (only if start is reachable from start by at least one transition)
  // Concrete subclass implementation should call, e.g., getReachableStatesAux(this) -- for S param, putting "this" into Map
	public abstract Set<S> getReachableStates();

	// N.B. doesn't implicitly include start (only if start is explicitly reachable from start by at least one transition)
	protected Set<S> getReachableStatesAux(S start)
	{
		Map<Integer, S> all = new HashMap<>();
		Map<Integer, S> todo = new HashMap<>(); //LinkedHashMap<>(); -- ?
		todo.put(this.id, start);  // Suppressed: assumes ModelState subclass correctly instantiates S parameter
		while (!todo.isEmpty())
		{
			Iterator<Entry<Integer, S>> i = todo.entrySet().iterator();
			Entry<Integer, S> next = i.next();
			i.remove();
			for (S s : next.getValue().getAllSuccessors())
			{
				if (!all.containsKey(s.id))
				{	
					all.put(s.id, s);
					todo.put(s.id, s);
				}
			}
		}
		return new HashSet<>(all.values());
	}

	public S getTerminal()
	{
		//getReachableStates().stream().filter(x -> x.isTerminal()).findFirst();
		Set<S> terms = getReachableStates().stream()
				.filter(s -> s.isTerminal()).collect(Collectors.toSet());
		if (terms.size() > 1)
		{
			throw new RuntimeException("Shouldn't get in here: " + terms);
		}
		return terms.isEmpty() //.isPresent()
				? null : terms.iterator().next();  // CHECKME: return empty Set instead of null?
	}
	public Set<A> getReachableActions()
	{
		return getReachableStates().stream().flatMap(x -> x.getActions().stream())
				.collect(Collectors.toSet());
	}
	
	public boolean canReach(MState<L, A, S, K> s)
	{
		return getReachableStates().contains(s);
	}

	@Override
	public String toString()
	{
		return Integer.toString(this.id);  // CHECKME: ?
	}

	@Override
	public int hashCode()
	{
		int hash = 73;
		hash = 31 * hash + this.id;  // N.B. using state ID only
		return hash;
	}

	// N.B. Based only on state ID
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof MState))
		{
			return false;
		}
		return ((MState<?, ?, ?, ?>) o).canEquals(this)
				&& this.id == ((MState<?, ?, ?, ?>) o).id;
				// Good to use id, due to edge mutability
	}
	
	protected abstract boolean canEquals(MState<?, ?, ?, ?> s);
}













	/*
	// TODO: make protected
	public static <L, A extends MAction<K>, S extends MState<L, A, S, K>, K extends ProtoKind>
			S getTerminal(S start)
	{
		if (start.isTerminal())
		{
			return start;
		}
		Set<S> terms = start.getReachableStates().stream()
				.filter(s -> s.isTerminal()).collect(Collectors.toSet());
		if (terms.size() > 1)
		{
			throw new RuntimeException("Shouldn't get in here: " + terms);
		}
		return (terms.isEmpty()) ? null : terms.iterator().next();  // FIXME: return empty Set instead of null?
	}

	// Note: doesn't implicitly include start (only if start is explicitly reachable from start, of course)
	/*public static <A extends ModelAction<K>, S extends ModelState<A, S, K>, K extends ProtocolKind>
			Set<S> getAllReachable(S start)* /
	// TODO: make protected
	// CHECKME: cache results?
	@SuppressWarnings("unchecked")
	public static <L, A extends MAction<K>, S extends MState<L, A, S, K>, K extends ProtoKind>
			Set<S> getReachableStates(MState<L, A, S, K> start)
	{
		Map<Integer, S> all = new HashMap<>();
		Map<Integer, S> todo = new LinkedHashMap<>();
		todo.put(start.id, (S) start);  // Suppressed: assumes ModelState subclass correctly instantiates S parameter
		while (!todo.isEmpty())
		{
			Iterator<S> i = todo.values().iterator();
			S next = i.next();
			todo.remove(next.id);
			/*if (all.containsKey(next.id))
			{
				continue;
			}
			all.put(next.id, next);* /
			for (S s : next.getAllSuccessors())
			{
				/*if (!all.containsKey(s.id) && !todo.containsKey(s.id))
				{
					todo.put(s.id, s);
				}* /
				if (!all.containsKey(s.id))
				{	
					all.put(s.id, s);
					//if (!todo.containsKey(s.id))  // Redundant
					{
						todo.put(s.id, s);
					}
				}
			}
		}
		return new HashSet<>(all.values());
	}
	
	@SuppressWarnings("unchecked")
	// TODO: make protected
	// CHECKME: cache results?
	public static <L, A extends MAction<K>, S extends MState<L, A, S, K>, K extends ProtoKind>
			//Set<A> getAllReachableActions(S start)
			Set<A> getReachableActions(MState<L, A, S, K> start)
	{
		Set<S> all = new HashSet<>();
		all.add((S) start);  // Suppressed: assumes ModelState subclass correctly instantiates S parameter
		all.addAll(MState.getReachableStates(start));
		Set<A> as = new HashSet<>();
		for (S s : all)
		{
			as.addAll(s.getAllActions());
		}
		return as;
	}*/