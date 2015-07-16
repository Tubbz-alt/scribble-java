package org.scribble.ast.local;

import org.scribble.ast.AstFactoryImpl;
import org.scribble.ast.Do;
import org.scribble.ast.NonRoleArgList;
import org.scribble.ast.RoleArgList;
import org.scribble.ast.context.ModuleContext;
import org.scribble.ast.name.qualified.LProtocolNameNode;
import org.scribble.ast.name.qualified.ProtocolNameNode;
import org.scribble.del.ScribDel;
import org.scribble.sesstype.kind.Local;
import org.scribble.sesstype.name.LProtocolName;
import org.scribble.sesstype.name.Role;
import org.scribble.visit.JobContext;
import org.scribble.visit.ProjectedChoiceSubjectFixer;

public class LDo extends Do<Local> implements LSimpleInteractionNode
{
	public LDo(RoleArgList roleinstans, NonRoleArgList arginstans, LProtocolNameNode proto)
	{
		super(roleinstans, arginstans, proto);
	}

	@Override
	protected LDo copy()
	{
		return new LDo(this.roles, this.args, getProtocolNameNode());
	}
	
	@Override
	public LDo clone()
	{
		RoleArgList roles = this.roles.clone();
		NonRoleArgList args = this.args.clone();
		LProtocolNameNode proto = this.getProtocolNameNode().clone();
		return AstFactoryImpl.FACTORY.LDo(roles, args, proto);
	}
	
	@Override
	public LDo reconstruct(RoleArgList roles, NonRoleArgList args, ProtocolNameNode<Local> proto)
	{
		ScribDel del = del();
		LDo ld = new LDo(roles, args, (LProtocolNameNode) proto);
		ld = (LDo) ld.del(del);
		return ld;
	}

	@Override
	public LProtocolNameNode getProtocolNameNode()
	{
		return (LProtocolNameNode) this.proto;
	}

	@Override
	public LProtocolName getTargetFullProtocolName(ModuleContext mcontext)
	{
		return (LProtocolName) super.getTargetFullProtocolName(mcontext);
	}

	@Override
	public LProtocolDecl getTargetProtocolDecl(JobContext jcontext, ModuleContext mcontext)
	{
		return (LProtocolDecl) super.getTargetProtocolDecl(jcontext, mcontext);
	}

	@Override
	public Role inferLocalChoiceSubject(ProjectedChoiceSubjectFixer fixer)
	{
		ModuleContext mc = fixer.getModuleContext();
		JobContext jc = fixer.getJobContext();
		return getTargetProtocolDecl(jc, mc).getDef().getBlock().getInteractionSeq().getInteractions().get(0).inferLocalChoiceSubject(fixer);
	}

	// FIXME: shouldn't be needed, but here due to Eclipse bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=436350
	@Override
	public Local getKind()
	{
		return LSimpleInteractionNode.super.getKind();
	}
}