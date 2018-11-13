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
package org.scribble.ext.go.parser.scribble.ast.global;

import org.antlr.runtime.tree.CommonTree;
import org.scribble.ast.AstFactory;
import org.scribble.ast.NonRoleParamDeclList;
import org.scribble.ast.RoleDeclList;
import org.scribble.ast.global.GProtocolHeader;
import org.scribble.ast.name.qualified.GProtocolNameNode;
import org.scribble.ext.go.ast.RPAstFactory;
import org.scribble.parser.scribble.AntlrToScribParser;
import org.scribble.parser.scribble.ast.AntlrExtIdentifier;
import org.scribble.parser.scribble.ast.name.AntlrSimpleName;
import org.scribble.util.ScribParserException;

public class RPAntlrGProtocolHeader
{
	public static final int ANNOT_CHILD_INDEX = 3;

	public static final int NAME_CHILD_INDEX = 0;
	public static final int PARAMETERDECLLIST_CHILD_INDEX = 1;
	public static final int ROLEDECLLIST_CHILD_INDEX = 2;

	public static GProtocolHeader parseGProtocolHeader(AntlrToScribParser parser, CommonTree ct, AstFactory af) throws ScribParserException
	{
		GProtocolNameNode name = AntlrSimpleName.toGProtocolNameNode(getNameChild(ct), af);
		RoleDeclList rdl = (RoleDeclList) parser.parse(getRoleDeclListChild(ct), af);
		NonRoleParamDeclList pdl = (NonRoleParamDeclList) parser.parse(getParamDeclListChild(ct), af);
		String annot = AntlrExtIdentifier.getName(getAnnotChild(ct));
		return ((RPAstFactory) af).RPGProtocolHeader(ct, name, rdl, pdl, annot);
	}

	public static CommonTree getNameChild(CommonTree ct)
	{
		return (CommonTree) ct.getChild(NAME_CHILD_INDEX);
	}
	
	public static CommonTree getRoleDeclListChild(CommonTree ct)
	{
		return (CommonTree) ct.getChild(ROLEDECLLIST_CHILD_INDEX);
	}

	public static CommonTree getParamDeclListChild(CommonTree ct)
	{
		return (CommonTree) ct.getChild(PARAMETERDECLLIST_CHILD_INDEX);
	}

	/*public static boolean hasAnnotChild(CommonTree ct)
	{
		return ct.getChildCount() == 4;
	}*/

	public static CommonTree getAnnotChild(CommonTree ct)
	{
		return (CommonTree) ct.getChild(ANNOT_CHILD_INDEX);
	}
}

