package org.scribble.ext.go.core.ast;

import org.scribble.type.name.RecVar;


public abstract class ParamCoreRecVar implements ParamCoreType
{
	public final RecVar recvar;
	
	public ParamCoreRecVar(RecVar var)
	{
		this.recvar = var;
	}

	@Override 
	public String toString()
	{
		return this.recvar.toString();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof ParamCoreRecVar))
		{
			return false;
		}
		ParamCoreRecVar them = (ParamCoreRecVar) obj;
		return them.canEquals(this) && this.recvar.equals(them.recvar);
	}
	
	@Override
	public abstract boolean canEquals(Object o);

	@Override
	public int hashCode()
	{
		int hash = 6733;
		hash = 31*hash + this.recvar.hashCode();
		return hash;
	}
}
