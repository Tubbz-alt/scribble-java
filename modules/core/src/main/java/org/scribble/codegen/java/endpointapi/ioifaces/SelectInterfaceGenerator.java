package org.scribble.codegen.java.endpointapi.ioifaces;

import java.util.Map;
import java.util.Set;

import org.scribble.codegen.java.endpointapi.StateChannelApiGenerator;
import org.scribble.codegen.java.util.InterfaceBuilder;
import org.scribble.model.local.EndpointState;
import org.scribble.model.local.IOAction;

public class SelectInterfaceGenerator extends IOStateInterfaceGenerator
{
	public SelectInterfaceGenerator(StateChannelApiGenerator apigen, EndpointState curr, Map<IOAction, InterfaceBuilder> actions, Set<InterfaceBuilder> preds)
	{
		super(apigen, curr, actions, preds);
	}
}
