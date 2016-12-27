package org.scribble.f17.ast.global.action;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.scribble.sesstype.Payload;
import org.scribble.sesstype.name.Op;
import org.scribble.sesstype.name.Role;


public class F17GConnect extends F17GAction
{
	public final Role dest;
	public final Op op;
	public final Payload pay;
	
	public F17GConnect(Role src, Role dest, Op op, Payload pay)
	{
		super(src);
		this.op = op;
		this.dest = dest;
		this.pay = pay;
	}
	
	@Override
	public Set<Role> getRoles()
	{
		return Collections.unmodifiableSet(new HashSet<Role>() {{ add(src); add(dest); }});
	}
	
	@Override
	public String toString()
	{
		return super.toString() + "->>" + this.dest + ":" + this.op + this.pay;  // Payload.toString includes parentheses
	} 

	@Override
	public int hashCode()
	{
		int hash = 29;
		hash = 31 * hash + dest.hashCode();
		hash = 31 * hash + this.op.hashCode();
		hash = 31 * hash + this.pay.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!(obj instanceof F17GConnect))
		{
			return false;
		}
		F17GConnect them = (F17GConnect) obj;
		return super.equals(obj)  // super does canEquals
				&& this.dest.equals(them.dest) && this.op.equals(them.op) && this.pay.equals(them.pay);
	}
	
	@Override
	protected boolean canEquals(Object o)
	{
		return o instanceof F17GConnect;
	}
}
