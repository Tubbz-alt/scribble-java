package org.scribble.del.global;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.scribble.ast.AstFactoryImpl;
import org.scribble.ast.MessageNode;
import org.scribble.ast.ScribNode;
import org.scribble.ast.global.GMessageTransfer;
import org.scribble.ast.local.LInteractionNode;
import org.scribble.ast.local.LNode;
import org.scribble.ast.local.LReceive;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.del.MessageTransferDel;
import org.scribble.main.ScribbleException;
import org.scribble.model.global.ModelAction;
import org.scribble.model.local.Receive;
import org.scribble.model.local.Send;
import org.scribble.sesstype.Message;
import org.scribble.sesstype.Payload;
import org.scribble.sesstype.kind.RoleKind;
import org.scribble.sesstype.name.MessageId;
import org.scribble.sesstype.name.Role;
import org.scribble.visit.InlinedWFChoiceChecker;
import org.scribble.visit.GlobalModelBuilder;
import org.scribble.visit.Projector;
import org.scribble.visit.env.InlinedWFChoiceEnv;
import org.scribble.visit.env.ModelEnv;

// FIXME: make base MessageTransferDelegate?
public class GMessageTransferDel extends MessageTransferDel implements GSimpleInteractionNodeDel
{
	public GMessageTransferDel()
	{
		
	}

	@Override
	public GMessageTransfer leaveInlinedWFChoiceCheck(ScribNode parent, ScribNode child, InlinedWFChoiceChecker checker, ScribNode visited) throws ScribbleException
	{
		GMessageTransfer msgtrans = (GMessageTransfer) visited;
		
		Role src = msgtrans.src.toName();
		if (!checker.peekEnv().isEnabled(src))
		{
			throw new ScribbleException("Role not enabled: " + src);
		}
		Message msg = msgtrans.msg.toMessage();
		InlinedWFChoiceEnv env = checker.popEnv();
		for (Role dest : msgtrans.getDestinations().stream().map((rn) -> rn.toName()).collect(Collectors.toList()))
		{
			env = env.addMessage(src, dest, msg);
			
			//System.out.println("a: " + src + ", " + dest + ", " + msg);
		}
		checker.pushEnv(env);
		return msgtrans;
	}

	@Override
	//public GMessageTransfer leaveProjection(ScribNode parent, ScribNode child, Projector proj, ScribNode visited) throws ScribbleException //throws ScribbleException
	public ScribNode leaveProjection(ScribNode parent, ScribNode child, Projector proj, ScribNode visited) throws ScribbleException //throws ScribbleException
	{
		GMessageTransfer gmt = (GMessageTransfer) visited;

		Role self = proj.peekSelf();
		Role srcrole = gmt.src.toName();
		List<Role> destroles = gmt.getDestinationRoles();
		LNode projection = null;
		if (srcrole.equals(self) || destroles.contains(self))
		{
			RoleNode src = (RoleNode) AstFactoryImpl.FACTORY.SimpleNameNode(RoleKind.KIND, gmt.src.toName().toString());
			MessageNode msg = (MessageNode) gmt.msg;  // FIXME: need namespace prefix update?
			List<RoleNode> dests =
					destroles.stream().map((d) ->
							(RoleNode) AstFactoryImpl.FACTORY.SimpleNameNode(RoleKind.KIND, d.toString())).collect(Collectors.toList());
			if (srcrole.equals(self))
			{
				projection = AstFactoryImpl.FACTORY.LSend(src, msg, dests);
			}
			if (destroles.contains(self))
			{
				if (projection == null)
				{
					projection = AstFactoryImpl.FACTORY.LReceive(src, msg, dests);
				}
				else
				{
					LReceive lr = AstFactoryImpl.FACTORY.LReceive(src, msg, dests);
					List<LInteractionNode> lis = Arrays.asList(new LInteractionNode[]{(LInteractionNode) projection, lr});
					projection = AstFactoryImpl.FACTORY.LInteractionSeq(lis);
				}
			}
		}

		proj.pushEnv(proj.popEnv().setProjection(projection));
		return (GMessageTransfer) GSimpleInteractionNodeDel.super.leaveProjection(parent, child, proj, gmt);
	}
	
	@Override
	public GMessageTransfer leaveModelBuilding(ScribNode parent, ScribNode child, GlobalModelBuilder builder, ScribNode visited) throws ScribbleException
	{
		GMessageTransfer gmt = (GMessageTransfer) visited;
		ModelEnv env = builder.popEnv();
		Set<ModelAction> actions = env.getActions();
		Map<Role, ModelAction> leaves = new HashMap<>();
		if (gmt.getDestinations().size() > 1)
		{
			throw new RuntimeException("TODO: " + gmt);
		}
		Role src = gmt.src.toName();
		Role dest = gmt.getDestinations().get(0).toName();
		MessageId<?> mid = gmt.msg.toMessage().getId();
		ModelAction send = new ModelAction(src, new Send(dest, mid, Payload.EMPTY_PAYLOAD));  // FIXME: payload hack
		ModelAction receive = new ModelAction(dest, new Receive(src, mid, Payload.EMPTY_PAYLOAD));  // FIXME: payload hack
		receive.addDependency(send);
		actions.add(send);
		actions.add(receive);
		leaves.put(src, send);
		leaves.put(dest, receive);
		env = env.setActions(actions, leaves);
		builder.pushEnv(env);
		return (GMessageTransfer) GSimpleInteractionNodeDel.super.leaveModelBuilding(parent, child, builder, visited);
	}
}