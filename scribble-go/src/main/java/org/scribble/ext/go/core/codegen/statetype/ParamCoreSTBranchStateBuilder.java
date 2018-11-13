package org.scribble.ext.go.core.codegen.statetype;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.scribble.codegen.statetype.STBranchStateBuilder;
import org.scribble.codegen.statetype.STStateChanApiBuilder;
import org.scribble.ext.go.core.type.RPInterval;
import org.scribble.ext.go.core.type.RPIndexedRole;
import org.scribble.ext.go.main.GoJob;
import org.scribble.ext.go.type.index.RPIndexExpr;
import org.scribble.ext.go.type.index.RPIndexInt;
import org.scribble.ext.go.type.index.RPIndexVar;
import org.scribble.model.endpoint.EState;
import org.scribble.model.endpoint.actions.EAction;
import org.scribble.model.endpoint.actions.EReceive;
import org.scribble.type.name.GProtocolName;
import org.scribble.type.name.Role;

public class ParamCoreSTBranchStateBuilder extends STBranchStateBuilder
{
	public ParamCoreSTBranchStateBuilder(ParamCoreSTBranchActionBuilder bb)
	{
		super(bb);
	}

	@Override
	public String getPreamble(STStateChanApiBuilder api, EState s)
	{
		String sEpRecv = 
				 ParamCoreSTApiGenConstants.GO_IO_FUN_RECEIVER
				+ "." + ParamCoreSTApiGenConstants.GO_SCHAN_ENDPOINT
				+ "." + ParamCoreSTApiGenConstants.GO_ENDPOINT_ENDPOINT
				+ "." + ParamCoreSTApiGenConstants.GO_ENDPOINT_READALL;
		String sEpProto =
				//"s.ep.Proto"
				ParamCoreSTApiGenConstants.GO_IO_FUN_RECEIVER + "."
					+ ParamCoreSTApiGenConstants.GO_SCHAN_ENDPOINT + "." + ParamCoreSTApiGenConstants.GO_ENDPOINT_PROTO;

		//return ((ParamCoreSTStateChanApiBuilder) api).getStateChanPremable(s);
		ParamCoreSTStateChanApiBuilder apigen = (ParamCoreSTStateChanApiBuilder) api;
		Role r = apigen.actual.getName();
		GProtocolName simpname = apigen.apigen.proto.getSimpleName();
		String tname = apigen.getStateChanName(s);
		String epType = ParamCoreSTEndpointApiGenerator.getGeneratedEndpointType(simpname, r); 
		String res =
				  apigen.apigen.generateRootPackageDecl() + "\n"
				+ "\n"
				+ apigen.apigen.generateScribbleRuntimeImports() + "\n"
				
				+ (((GoJob) api.job).noCopy ? "" :
						Stream.of(ParamCoreSTApiGenConstants.GO_SCRIBBLERUNTIME_BYTES_PACKAGE, ParamCoreSTApiGenConstants.GO_SCRIBBLERUNTIME_GOB_PACKAGE)
							.map(x -> "import \"" + x + "\"").collect(Collectors.joining("\n")))

				+ "\n"
				+ "type " + tname + " struct{\n"
				//+ ParamCoreSTApiGenConstants.GO_SCHAN_ENDPOINT + " *" + ParamCoreSTApiGenConstants.GO_ENDPOINT_TYPE + "\n" 
				+ ParamCoreSTApiGenConstants.GO_SCHAN_ENDPOINT + " *" + epType + "\n" 
				+ ParamCoreSTApiGenConstants.GO_SCHAN_LINEARRESOURCE + " *" + ParamCoreSTApiGenConstants.GO_LINEARRESOURCE_TYPE +"\n"
				+ s.getActions().stream().map(a -> "_" + a.mid + "_Chan chan chan *" + apigen.getStateChanName(s.getSuccessor(a)) + "\n")
						.collect(Collectors.joining(""))
				+ (((GoJob) apigen.job).noCopy ? "data chan []interface{}" : "data chan [][]byte\n" )
				+ "}\n";

		res += "\n"
				+ "func (ep *" + epType + ") New" 
						+ ((s.id != api.graph.init.id) ? tname
								: ParamCoreSTEndpointApiGenerator.getGeneratedActualRoleName(((ParamCoreSTStateChanApiBuilder) api).actual) + "_1")  // cf. ParamCoreSTStateChanApiBuilder::getStateChanPremable init state case
						+ "() *" + tname + " {\n"  // FIXME: factor out
				+ "s := &" + tname + " { " + ParamCoreSTApiGenConstants.GO_SCHAN_ENDPOINT + ": ep"
						+ ", " + ParamCoreSTApiGenConstants.GO_SCHAN_LINEARRESOURCE + ": new(" + ParamCoreSTApiGenConstants.GO_LINEARRESOURCE_TYPE + "), "
						+ s.getActions().stream().map(a -> "_" + a.mid + "_Chan: make(chan chan *" + apigen.getStateChanName(s.getSuccessor(a))+ ", 1)")
								.collect(Collectors.joining(", ")) + ", "

						+ "data: make(chan " + (((GoJob) api.job).noCopy ? "[]interface{}" : "[][]byte") + ", 1)"

						+ "}\n"
				+ "s.foo()\n"
				+ "return s\n"
				+ "}\n";

		res += "\n"
				+ "func (s *" + tname + ") foo() {\n"
				+ "s." + ParamCoreSTApiGenConstants.GO_SCHAN_LINEARRESOURCE + "." + ParamCoreSTApiGenConstants.GO_LINEARRESOURCE_USE + "()\n";

		RPIndexedRole peer = (RPIndexedRole) s.getActions().iterator().next().peer;
		RPInterval g = peer.intervals.iterator().next();
		Function<RPIndexExpr, String> foo = e ->
		{
			if (e instanceof RPIndexInt)
			{
				return e.toString();
			}
			else if (e instanceof RPIndexVar)
			{
				return ParamCoreSTApiGenConstants.GO_IO_FUN_RECEIVER + "."
					+ ParamCoreSTApiGenConstants.GO_SCHAN_ENDPOINT + ".Params[\"" + e + "\"]";
			}
			else
			{
				throw new RuntimeException("[param-core] TODO: " + e);
			}
		};
		
		if (((GoJob) apigen.job).noCopy)
		{
		res += 
				  "label := " + sEpRecv + "Raw(" + sEpProto + "." + peer.getName() + ", "
				  		+ foo.apply(g.start) + ", " + foo.apply(g.end) + ")\n"
				+ "op := *label[0].(*string)\n";  // FIXME: cast for safety?
		}
		else
		{
		res +=
				  "label := " + sEpRecv + "(" + sEpProto + "." + peer.getName() + ", "
				  		+ foo.apply(g.start) + ", " + foo.apply(g.end) + ")\n"
				+ "op := string(label[0])\n";  // FIXME: cast for safety?
		}

		List<EAction> as = s.getActions();
		boolean allEmpty = as.stream().allMatch(a -> a.payload.elems.isEmpty());
		if (!allEmpty)  // FIXME:
		{
			res +=
						"b := " + sEpRecv + (((GoJob) api.job).noCopy ? "Raw" : "")
						+ "(" + sEpProto + "." + peer.getName() + ", "
						+ foo.apply(g.start) + ", " + foo.apply(g.end) + ")\n";
						// See below for sending b to s.data
						// FIXME: arg0  // FIXME: args depends on label  // FIXME: store args in s.args
		}

		res+= "switch op {\n"
				+ s.getActions().stream().map(a ->
					{
						String sEp = 
								ParamCoreSTApiGenConstants.GO_IO_FUN_RECEIVER + "." + ParamCoreSTApiGenConstants.GO_SCHAN_ENDPOINT;
						return
								"\tcase \"" + a.mid + "\":\n"
								+ "\t\tch := make(chan *" + apigen.getStateChanName(s.getSuccessor(a)) + ", 1)\n"
								+ "\t\tch <- " + apigen.getSuccStateChan(this.bb, s, s.getSuccessor(a), sEp) + "\n"
								+ "\t\ts._" + a.mid + "_Chan <- ch\n"
								+ "\t\t" + s.getActions().stream()
										.filter(otheract -> otheract.mid != a.mid)
										.map(otheract -> { return "close(s._" + otheract.mid + "_Chan)"; })
										.collect(Collectors.joining("\n\t")) + "\n";
					}).collect(Collectors.joining("\n"))
				+ "\n"
				+ "\tdefault:\n" // default case captures unrecognised choice label
				+ "\t\t" + ParamCoreSTApiGenConstants.GO_IO_FUN_RECEIVER + "." + ParamCoreSTApiGenConstants.GO_SCHAN_ENDPOINT + "."
				+ "Errors <- session.UnknownChoiceLabelError{Label: op}\n"
				+ "\t}\n"; // End of switch

		if (!allEmpty)
		{
			// b is result of a sEpRecv/sEpRecvRaw
			res += "\ts.data <- b\n";
		}

        res += "}\n"; // End of func foo

		return res;
	}
	
	@Override
	public String build(STStateChanApiBuilder api, EState s)
	{
		String out = getPreamble(api, s);
		
		for (EAction a : s.getActions())
		{
			out += "\n\n";
			if (a instanceof EReceive)  // FIXME: factor out action kind
			{
				out += this.bb.build(api, s, a);  // Getting 1 checks non-unary
			}
			else
			{
				throw new RuntimeException("Shouldn't get in here: " + a);
			}
		}

		return out;
	}
}
