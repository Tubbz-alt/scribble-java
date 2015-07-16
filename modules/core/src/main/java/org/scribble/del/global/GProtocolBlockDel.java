package org.scribble.del.global;

import java.util.Map;
import java.util.Set;

import org.scribble.ast.AstFactoryImpl;
import org.scribble.ast.ScribNode;
import org.scribble.ast.global.GInteractionSeq;
import org.scribble.ast.global.GProtocolBlock;
import org.scribble.ast.local.LInteractionSeq;
import org.scribble.ast.local.LProtocolBlock;
import org.scribble.del.ProtocolBlockDel;
import org.scribble.del.ScribDelBase;
import org.scribble.main.ScribbleException;
import org.scribble.model.global.ModelAction;
import org.scribble.sesstype.name.Role;
import org.scribble.visit.GlobalModelBuilder;
import org.scribble.visit.Projector;
import org.scribble.visit.ProtocolDefInliner;
import org.scribble.visit.env.InlineProtocolEnv;
import org.scribble.visit.env.ModelEnv;
import org.scribble.visit.env.ProjectionEnv;

public class GProtocolBlockDel extends ProtocolBlockDel
{
	@Override
	public void enterProjection(ScribNode parent, ScribNode child, Projector proj) throws ScribbleException
	{
		ScribDelBase.pushVisitorEnv(this, proj);
	}

	@Override
	public ScribNode leaveProtocolInlining(ScribNode parent, ScribNode child, ProtocolDefInliner inl, ScribNode visited) throws ScribbleException
	{
		GProtocolBlock gpd = (GProtocolBlock) visited;
		GInteractionSeq seq = (GInteractionSeq) ((InlineProtocolEnv) gpd.seq.del().env()).getTranslation();	
		GProtocolBlock inlined = AstFactoryImpl.FACTORY.GProtocolBlock(seq);
		inl.pushEnv(inl.popEnv().setTranslation(inlined));
		return (GProtocolBlock) ScribDelBase.popAndSetVisitorEnv(this, inl, gpd);
	}

	@Override
	public GProtocolBlock leaveProjection(ScribNode parent, ScribNode child, Projector proj, ScribNode visited) throws ScribbleException
	{
		GProtocolBlock gpd = (GProtocolBlock) visited;
		LInteractionSeq seq = (LInteractionSeq) ((ProjectionEnv) gpd.seq.del().env()).getProjection();	
		LProtocolBlock projection = AstFactoryImpl.FACTORY.LProtocolBlock(seq);
		proj.pushEnv(proj.popEnv().setProjection(projection));
		return (GProtocolBlock) ScribDelBase.popAndSetVisitorEnv(this, proj, gpd);
	}
	
	@Override
	public void enterModelBuilding(ScribNode parent, ScribNode child, GlobalModelBuilder builder) throws ScribbleException
	{
		ScribDelBase.pushVisitorEnv(this, builder);
	}

	@Override
	public GProtocolBlock leaveModelBuilding(ScribNode parent, ScribNode child, GlobalModelBuilder builder, ScribNode visited) throws ScribbleException
	{
		GProtocolBlock gpb = (GProtocolBlock) visited;
		Set<ModelAction> as = ((ModelEnv) gpb.seq.del().env()).getActions();
		Map<Role, ModelAction> leaves = ((ModelEnv) gpb.seq.del().env()).getLeaves();
		builder.pushEnv(builder.popEnv().setActions(as, leaves));
		return (GProtocolBlock) ScribDelBase.popAndSetVisitorEnv(this, builder, gpb);
	}
}