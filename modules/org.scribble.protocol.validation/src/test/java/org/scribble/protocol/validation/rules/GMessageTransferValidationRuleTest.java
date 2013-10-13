/*
 * Copyright 2009-11 www.scribble.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.scribble.protocol.validation.rules;

import static org.junit.Assert.*;

import java.text.MessageFormat;

import org.scribble.protocol.model.FullyQualifiedName;
import org.scribble.protocol.model.Message;
import org.scribble.protocol.model.Module;
import org.scribble.protocol.model.Role;
import org.scribble.protocol.model.RoleDecl;
import org.scribble.protocol.model.global.GBlock;
import org.scribble.protocol.model.global.GMessageTransfer;
import org.scribble.protocol.model.global.GProtocolDefinition;
import org.scribble.protocol.validation.TestValidationLogger;
import org.scribble.protocol.validation.ValidationMessages;

public class GMessageTransferValidationRuleTest {

    private static final String TEST_ROLE1 = "TestRole1";
    private static final String TEST_ROLE2 = "TestRole2";
	
	@org.junit.Test
    public void testFromRoleFound() {
    	GMessageTransferValidationRule rule=new GMessageTransferValidationRule();
    	TestValidationLogger logger=new TestValidationLogger();
    	
    	Module module=new Module();
    	module.setFullyQualifiedName(new FullyQualifiedName("test"));
    	
    	GProtocolDefinition gpd=new GProtocolDefinition();
    	
    	RoleDecl rd=new RoleDecl();
    	rd.setName(TEST_ROLE1);
    	gpd.getRoleDeclarations().add(rd);
    	
    	module.getProtocols().add(gpd);
    	
    	GBlock block=new GBlock();
    	gpd.setBlock(block);
    	
    	GMessageTransfer gm=new GMessageTransfer();
    	gm.setFromRole(new Role(TEST_ROLE1));
    	block.add(gm);
    	
    	Message message=new Message();    	
    	gm.setMessage(message);
    	
     	rule.validate(null, gm, logger);
    	
    	if (logger.isErrorsOrWarnings()) {
    		fail("Errors detected");
    	}
    }
	
	@org.junit.Test
    public void testFromRoleNotFound() {
    	GMessageTransferValidationRule rule=new GMessageTransferValidationRule();
    	TestValidationLogger logger=new TestValidationLogger();
    	
    	Module module=new Module();
    	module.setFullyQualifiedName(new FullyQualifiedName("test"));
    	
    	GProtocolDefinition gpd=new GProtocolDefinition();
    	
    	RoleDecl rd=new RoleDecl();
    	rd.setName(TEST_ROLE1);
    	gpd.getRoleDeclarations().add(rd);
    	
    	module.getProtocols().add(gpd);
    	
    	GBlock block=new GBlock();
    	gpd.setBlock(block);
    	
    	GMessageTransfer gm=new GMessageTransfer();
    	gm.setFromRole(new Role(TEST_ROLE2));
    	block.add(gm);
    	
    	Message message=new Message();    	
    	gm.setMessage(message);
    	
     	rule.validate(null, gm, logger);
   	
    	if (!logger.isErrorsOrWarnings()) {
    		fail("Errors not detected");
    	}
    	
    	if (!logger.getErrors().contains(MessageFormat.format(ValidationMessages.getMessage("UNKNOWN_ROLE"), TEST_ROLE2))) {
    		fail("Error UNKNOWN_ROLE not detected");
    	}
    }

	@org.junit.Test
    public void testToRoleFound() {
    	GMessageTransferValidationRule rule=new GMessageTransferValidationRule();
    	TestValidationLogger logger=new TestValidationLogger();
    	
    	Module module=new Module();
    	module.setFullyQualifiedName(new FullyQualifiedName("test"));
    	
    	GProtocolDefinition gpd=new GProtocolDefinition();
    	
    	RoleDecl rd=new RoleDecl();
    	rd.setName(TEST_ROLE1);
    	gpd.getRoleDeclarations().add(rd);
    	
    	module.getProtocols().add(gpd);
    	
    	GBlock block=new GBlock();
    	gpd.setBlock(block);
    	
    	GMessageTransfer gm=new GMessageTransfer();
    	gm.getToRoles().add(new Role(TEST_ROLE1));
    	block.add(gm);
    	
    	Message message=new Message();    	
    	gm.setMessage(message);
    	
     	rule.validate(null, gm, logger);
    	
    	if (logger.isErrorsOrWarnings()) {
    		fail("Errors detected");
    	}
    }
	
	@org.junit.Test
    public void testToRoleNotFound() {
    	GMessageTransferValidationRule rule=new GMessageTransferValidationRule();
    	TestValidationLogger logger=new TestValidationLogger();
    	
    	Module module=new Module();
    	module.setFullyQualifiedName(new FullyQualifiedName("test"));
    	
    	GProtocolDefinition gpd=new GProtocolDefinition();
    	
    	RoleDecl rd=new RoleDecl();
    	rd.setName(TEST_ROLE1);
    	gpd.getRoleDeclarations().add(rd);
    	
    	module.getProtocols().add(gpd);
    	
    	GBlock block=new GBlock();
    	gpd.setBlock(block);
    	
    	GMessageTransfer gm=new GMessageTransfer();
    	gm.getToRoles().add(new Role(TEST_ROLE2));
    	block.add(gm);
    	
    	Message message=new Message();    	
    	gm.setMessage(message);
    	
     	rule.validate(null, gm, logger);
   	
    	if (!logger.isErrorsOrWarnings()) {
    		fail("Errors not detected");
    	}
    	
    	if (!logger.getErrors().contains(MessageFormat.format(ValidationMessages.getMessage("UNKNOWN_ROLE"), TEST_ROLE2))) {
    		fail("Error UNKNOWN_ROLE not detected");
    	}
    }
}
