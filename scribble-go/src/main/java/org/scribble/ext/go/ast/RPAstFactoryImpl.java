package org.scribble.ext.go.ast;

import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactoryImpl;
import org.scribble.ast.MessageNode;
import org.scribble.ast.NonRoleParamDeclList;
import org.scribble.ast.RoleDeclList;
import org.scribble.ast.global.GProtocolBlock;
import org.scribble.ast.name.qualified.GProtocolNameNode;
import org.scribble.ast.name.simple.RoleNode;
import org.scribble.ext.go.ast.global.RPGChoice;
import org.scribble.ext.go.ast.global.RPGCrossMessageTransfer;
import org.scribble.ext.go.ast.global.RPGDelegationElem;
import org.scribble.ext.go.ast.global.RPGDotMessageTransfer;
import org.scribble.ext.go.ast.global.RPGForeach;
import org.scribble.ext.go.ast.global.RPGMultiChoices;
import org.scribble.ext.go.ast.global.RPGMultiChoicesTransfer;
import org.scribble.ext.go.ast.global.RPGProtocolHeader;
import org.scribble.ext.go.ast.name.simple.RPIndexedRoleNode;
import org.scribble.ext.go.del.global.RPGChoiceDel;
import org.scribble.ext.go.del.global.RPGDelegationElemDel;
import org.scribble.ext.go.del.global.RPGForeachDel;
import org.scribble.ext.go.del.global.RPGMessageTransferDel;
import org.scribble.ext.go.del.global.RPGMultiChoicesDel;
import org.scribble.ext.go.type.index.RPForeachVar;
import org.scribble.ext.go.type.index.RPIndexExpr;
import org.scribble.ext.go.type.index.RPIndexVar;


public class RPAstFactoryImpl extends AstFactoryImpl implements RPAstFactory
{
	
	
	// Instantiating existing node classes with new dels

	/*@Override
	public GProtocolDecl GProtocolDecl(CommonTree source, List<GProtocolDecl.Modifiers> mods, GProtocolHeader header, GProtocolDef def)
	{
		GProtocolDecl gpd = new GProtocolDecl(source, mods, header, def);
		gpd = del(gpd, new AssrtGProtocolDeclDel());
		return gpd;
	}*/
	
	
	// Returning new node classes in place of existing -- FIXME: do for GMessageTransfer and GChoice

	/*@Override
	public RPRoleDecl RoleDecl(CommonTree source, RoleNode namenode)
	{
		RPRoleDecl rd = new RPRoleDecl(source, namenode);
		rd = del(rd, new RoleDeclDel());
		return rd;
	}*/


	// Explicitly creating new Assrt nodes

	/*@Override
	//public ParamRoleDecl ParamRoleDecl(CommonTree source, RoleNode namenode, List<ParamRoleParamNode> params)
	public RPRoleDecl ParamRoleDecl(CommonTree source, RoleNode namenode, List<RPIndexVar> params)
	{
		RPRoleDecl rd = new RPRoleDecl(source, namenode, params);
		rd = del(rd, new RoleDeclDel());
		return rd;
	}*/

	@Override
	public RPGCrossMessageTransfer ParamGCrossMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, RoleNode dest,
			//ParamRoleParamNode srcRangeStart, ParamRoleParamNode srcRangeEnd, ParamRoleParamNode destRangeStart, ParamRoleParamNode destRangeEnd)
			RPIndexExpr srcRangeStart, RPIndexExpr srcRangeEnd, RPIndexExpr destRangeStart, RPIndexExpr destRangeEnd)
	{
		RPGCrossMessageTransfer mt = new RPGCrossMessageTransfer(source, src, msg, dest,
				srcRangeStart, srcRangeEnd, destRangeStart, destRangeEnd);
		mt = del(mt, new RPGMessageTransferDel());  // FIXME: parameterised self connection check
		return mt;
	}

	// FIXME: deprecate -- pipe/pair instead
	@Override
	public RPGDotMessageTransfer ParamGDotMessageTransfer(CommonTree source, RoleNode src, MessageNode msg, RoleNode dest,
			//ParamRoleParamNode srcRangeStart, ParamRoleParamNode srcRangeEnd, ParamRoleParamNode destRangeStart, ParamRoleParamNode destRangeEnd)
			RPIndexExpr srcRangeStart, RPIndexExpr srcRangeEnd, RPIndexExpr destRangeStart, RPIndexExpr destRangeEnd)
	{
		RPGDotMessageTransfer mt = new RPGDotMessageTransfer(source, src, msg, dest,
				srcRangeStart, srcRangeEnd, destRangeStart, destRangeEnd);
		mt = del(mt, new RPGMessageTransferDel());  // FIXME: parameterised self connection check
		return mt;
	}
	
	@Override
	public RPGChoice ParamGChoice(CommonTree source, RoleNode subj, RPIndexExpr expr, List<GProtocolBlock> blocks)
	{
		RPGChoice gc = new RPGChoice(source, subj, expr, blocks);
		gc = del(gc, new RPGChoiceDel());
		return gc;
	}

	@Override
	public RPGForeach RPGForeach(CommonTree source, List<RoleNode> subjs,
			List<RPForeachVar> params, List<RPIndexExpr> starts, List<RPIndexExpr> ends, GProtocolBlock block)
	{
		RPGForeach gf = new RPGForeach(source, subjs, params, starts, ends, block);
		gf = del(gf, new RPGForeachDel());
		return gf;
	}

	@Override
	public RPIndexedRoleNode RPIndexedRoleNode(CommonTree source, String identifier, RPIndexExpr start, RPIndexExpr end)
	{
		RPIndexedRoleNode irn = new RPIndexedRoleNode(source, identifier, start, end);
		irn = del(irn, createDefaultDelegate());
		return irn;
	}

	// FIXME: deprecate -- explicit foreach instead
	@Override
	public RPGMultiChoices ParamGMultiChoices(CommonTree source, RoleNode subj, RPIndexVar var,
			RPIndexExpr start, RPIndexExpr end, List<GProtocolBlock> blocks)
	{
		RPGMultiChoices gc = new RPGMultiChoices(source, subj, var, start, end, blocks);
		gc = del(gc, new RPGMultiChoicesDel());
		return gc;
	}
	
	// FIXME: deprecate -- explicit foreach instead
	@Override
	public RPGMultiChoicesTransfer ParamGMultiChoicesTransfer(CommonTree source, RoleNode src, MessageNode msg, RoleNode dest, 
			RPIndexVar var, RPIndexExpr destRangeStart, RPIndexExpr destRangeEnd)
	{
		RPGMultiChoicesTransfer mt = new RPGMultiChoicesTransfer(source, src, msg, dest,
				var, destRangeStart, destRangeEnd);
		mt = del(mt, new RPGMessageTransferDel());  // FIXME: not a ParamGMessageTransfer
		return mt;
	}
	
	/*@Override
	public <K extends Kind> NameNode<K> SimpleNameNode(CommonTree source, K kind, String identifier)
	{
		NameNode<? extends Kind> snn = null;
		
		// Default del's
		if (kind.equals(ParamRoleParamKind.KIND))
		{
			snn = new ParamRoleParamNode(source, identifier);
			return castNameNode(kind, del(snn, createDefaultDelegate()));
		}
		else
		{
			return super.SimpleNameNode(source, kind, identifier);
		}
	}*/

	@Override
	public RPGProtocolHeader RPGProtocolHeader(CommonTree source, GProtocolNameNode name, RoleDeclList roledecls, NonRoleParamDeclList paramdecls, String annot)
	{
		RPGProtocolHeader gph = new RPGProtocolHeader(source, name, roledecls, paramdecls, annot);
		gph = del(gph, createDefaultDelegate());
		return gph;
	}



	// param-core

	// Explicitly creating new Assrt nodes

	/*public RPCoreDelegDecl ParamCoreDelegDecl(CommonTree source, String schema, String extName, String extSource, DataTypeNode name)
	{
		RPCoreDelegDecl dtd = new RPCoreDelegDecl(source, schema, extName, extSource, name);
		dtd = del(dtd, createDefaultDelegate());
		return dtd;
	}*/

	@Override
	public RPGDelegationElem RPGDelegationElem(CommonTree source, GProtocolNameNode root, GProtocolNameNode state, RoleNode role)
	{
		RPGDelegationElem de = new RPGDelegationElem(source, root, state, role);
		de = del(de, new RPGDelegationElemDel());
		return de;
	}

	/*@Override
	public RPCoreLDelegationElem RPCoreLDelegationElem(CommonTree source, LProtocolNameNode proto)
	{
		RPCoreLDelegationElem de = new RPCoreLDelegationElem(source, proto);
		de = del(de, createDefaultDelegate());
		return de;
	}*/

	
	
	
	

	@Override
	protected RPDel createDefaultDelegate()
	{
		return new RPDefaultDel();
	}
	
	
	
	// Extra parsing checks

	/*@Override
	public RoleDecl RoleDecl(CommonTree source, RoleNode namenode)
	{
		// Check here?  Or in API gen -- cf. RPIndexFactory#ParamIntVar
		char c = namenode.toString().charAt(0);
		if (c < 'A' || c > 'Z')
		{
			throw new RuntimeException("[param] Role names must start uppercase for Go accessibility: " + namenode);  
					// FIXME: return proper parsing error -- refactor as param API gen errors
		}
		return super.RoleDecl(source, namenode);
	}

	@Override
	public <K extends Kind> NameNode<K> SimpleNameNode(CommonTree source, K kind, String identifier)
	{
		// Check here?  Or in API gen
		if (kind.equals(OpKind.KIND))
		{
			char c;
			if (identifier.length() == 0
					|| ((c = identifier.charAt(0))) < 'A' || c > 'Z')
			{
				throw new RuntimeException("[param] Op names must start uppercase for Go accessibility: " + identifier);
					// FIXME: return proper parsing error -- refactor as param API gen errors
			}
		}
		return super.SimpleNameNode(source, kind, identifier);
	}

	@Override
	public <K extends Kind> QualifiedNameNode<K> QualifiedNameNode(CommonTree source, K kind, String... elems)
	{
		// Check here?  Or in API gen
		if (kind.equals(SigKind.KIND))
		{
			char c;
			if (elems[elems.length-1].length() == 0
					|| ((c = elems[elems.length-1].charAt(0))) < 'A' || c > 'Z')
			{
				throw new RuntimeException("[param] Op names must start uppercase for Go accessibility: " + elems[elems.length-1]);
					// FIXME: return proper parsing error -- refactor as param API gen errors
			}
		}
		return super.QualifiedNameNode(source, kind, elems);
	}*/
}
