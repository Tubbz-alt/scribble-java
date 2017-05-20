package org.scribble.codegen.statetype;

import org.scribble.model.endpoint.EState;
import org.scribble.model.endpoint.actions.EAction;

public abstract class STActionBuilder
{
	public abstract String getSTActionName(STStateChanAPIBuilder api, EAction a);
	public abstract String buildArgs(EAction a);
	public abstract String buildBody(STStateChanAPIBuilder api, EState curr, EAction a, EState succ);

	public String getReturnType(EState curr, STStateChanAPIBuilder api, EState succ)
	{
		return api.getStateChanName(succ);
	}

	public String buildReturn(EState curr, STStateChanAPIBuilder api, EState succ)
	{
		return "return " + getReturnType(curr, api, succ) + "{ ep: s.ep }";
	}
	
	public String build(STStateChanAPIBuilder api, EState curr, EAction a)
	{
		return api.buildAction(this, curr, a);  // Because action builder hierarchy not suitable (extended by action kinds, not by target language) 
	}
	
	public String getStateChanType(STStateChanAPIBuilder api, EState curr, EAction a)
	{
		return api.getStateChanName(curr);
	}
}
