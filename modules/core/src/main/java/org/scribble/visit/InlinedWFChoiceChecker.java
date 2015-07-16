package org.scribble.visit;

import java.util.HashSet;
import java.util.Set;

import org.scribble.ast.Choice;
import org.scribble.ast.InteractionSeq;
import org.scribble.ast.ProtocolDecl;
import org.scribble.ast.ScribNode;
import org.scribble.main.ScribbleException;
import org.scribble.sesstype.kind.ProtocolKind;
import org.scribble.visit.env.InlinedWFChoiceEnv;

public class InlinedWFChoiceChecker extends UnfoldedVisitor<InlinedWFChoiceEnv>
{
	private Set<Choice<?>> visited = new HashSet<>();	
	
	public InlinedWFChoiceChecker(Job job)
	{
		super(job);
	}

	@Override
	public ScribNode visit(ScribNode parent, ScribNode child) throws ScribbleException
	{
		if (child instanceof Choice<?>)
		{
			return visitOverrideForChoice((InteractionSeq<?>) parent, (Choice<?>) child);
		}
		else
		{
			return super.visit(parent, child);
		}
	}

	private ScribNode visitOverrideForChoice(InteractionSeq<?> parent, Choice<?> child) throws ScribbleException
	{
		if (child instanceof Choice<?>)
		{
			Choice<?> cho = (Choice<?>) child;
			if (!this.visited.contains(cho))
			{
				this.visited.add(cho);
				//ScribNode n = cho.visitChildren(this);
				ScribNode n = super.visit(parent, child);
				this.visited.remove(cho);
				return n;
			}
			else
			{
				return cho;
			}
		}
		else
		{
			return super.visit(parent, child);
		}
	}

	@Override
	protected InlinedWFChoiceEnv makeRootProtocolDeclEnv(ProtocolDecl<? extends ProtocolKind> pd)
	{
		return new InlinedWFChoiceEnv();
	}
	
	@Override
	protected void unfoldedEnter(ScribNode parent, ScribNode child) throws ScribbleException
	{
		super.unfoldedEnter(parent, child);
		child.del().enterInlinedWFChoiceCheck(parent, child, this);
	}
	
	@Override
	protected ScribNode unfoldedLeave(ScribNode parent, ScribNode child, ScribNode visited) throws ScribbleException
	{
		visited = visited.del().leaveInlinedWFChoiceCheck(parent, child, this, visited);
		return super.unfoldedLeave(parent, child, visited);
	}
}