package org.scribble2.model.local;

import org.scribble2.model.ModelNodeBase;
import org.scribble2.model.AbstractProtocolDecl;
import org.scribble2.model.del.ModelDelegate;
import org.scribble2.sesstype.name.Role;

// FIXME: LocalProtocolDefinition
public class LocalProtocolDecl extends AbstractProtocolDecl<LocalProtocolHeader, LocalProtocolDefinition> implements LocalNode
{
	//public LocalProtocolDecl(SimpleProtocolNameNode name, RoleDeclList roledecls, ParameterDeclList paramdecls, LocalProtocolDefinition def)
	public LocalProtocolDecl(LocalProtocolHeader header, LocalProtocolDefinition def)
	{
		super(header, def);
	}
	
	/*@Override
	public GraphBuilder enterGraphBuilding(GraphBuilder builder)
	{
		ProtocolState root = new ProtocolState();
		ProtocolState term = new ProtocolState();
		builder.setEntry(root);
		builder.setExit(term);
		return builder;
	}
	
	@Override
	public LocalProtocolDecl leaveGraphBuilding(GraphBuilder builder)
	{
		builder.addGraph(getFullProtocolName(builder.getModuleContext().root), builder.getEntry());
		return this;
	}

	/*@Override
	public LocalProtocolDecl visitChildren(NodeVisitor nv) throws ScribbleException
	{
		ProtocolDecl<GlobalProtocolDefinition> pd = super.visitChildren(nv);
		return new LocalProtocolDecl(pd.ct, pd.name, pd.roledecllist, pd.paramdecllist, pd.def);
	}*/

	@Override
	public boolean isLocal()
	{
		return true;
	}
	
	public Role getSelfRole()
	{
		return this.header.getSelfRole();
	}
	
	/*@Override
	public ProtocolName getFullProtocolName(Env env) throws ScribbleException
	{
		return env.getFullGlobalProtocolName(this.name.toName());
	}*/

	@Override
	protected LocalProtocolDecl reconstruct(LocalProtocolHeader header, LocalProtocolDefinition def)
	{
		ModelDelegate del = del();
		LocalProtocolDecl lpd = new LocalProtocolDecl(header, def);
		lpd = (LocalProtocolDecl) lpd.del(del);
		return lpd;
	}

	@Override
	protected ModelNodeBase copy()
	{
		return new LocalProtocolDecl(this.header, this.def);
	}
}
