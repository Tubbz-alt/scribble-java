package org.scribble.ext.go.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import org.scribble.ast.ProtocolDecl;
import org.scribble.ast.global.GProtocolDecl;
import org.scribble.ext.go.core.ast.global.RPCoreGType;
import org.scribble.ext.go.core.type.RPIndexedRole;
import org.scribble.ext.go.main.GoJob;
import org.scribble.ext.go.type.index.RPIndexIntPair;
import org.scribble.main.ScribbleException;
import org.scribble.type.kind.Global;
import org.scribble.util.ScribUtil;

// "Native" Z3 -- not Z3 Java API
public class Z3Wrapper
{

	public static Smt2Translator getSmt2Translator(GoJob job, GProtocolDecl gpd, RPCoreGType gt)
	{
		Set<RPIndexedRole> irs = gt.getIndexedRoles();
		if (irs.isEmpty() ||  // FIXME: hardcoded default
				irs.stream().allMatch(x -> x.intervals.stream().allMatch(y -> y.getIndexVals().stream().noneMatch(z -> z instanceof RPIndexIntPair))))
		{
			return new IntSmt2Translator(job, gpd);
		}
		else if (irs.stream().allMatch(x -> x.intervals.stream().allMatch(y -> y.getIndexVals().stream().allMatch(z -> z instanceof RPIndexIntPair))))
		{
			return new IntPairSmt2Translator(job, gpd);
		}
		else
		{
			throw new RuntimeException("Shouldn't get in here: " + irs);
		}
	}

	// Based on CommandLine::runDot, JobContext::runAut, etc
	public static boolean checkSat(GoJob job, ProtocolDecl<Global> gpd, String smt2) //throws ScribbleException
	{
		//String tmpName = gpd.header.name + "_" + ".smt2.tmp";
		File tmp = null; //= new File(tmpName);
		/*if (tmp.exists())  // Factor out with CommandLine.runDot (file exists check)  // Now redundant, using createTempFile
		{
			throw new RuntimeException("Cannot overwrite: " + tmp.getAbsolutePath());
		}*/
		smt2 = "(declare-datatypes (T1 T2) ((Pair (mk-pair (fst T1) (snd T2)))))\n"
			+ "(define-fun pair_max ((p!1 (Pair Int Int))) Int (ite (< (fst p!1) (snd p!1)) (snd p!1) (fst p!1) ) )\n"
			+	"(define-fun pair_min ((p!1 (Pair Int Int))) Int (ite (< (fst p!1) (snd p!1)) (fst p!1) (snd p!1)))\n"
			+	"(define-fun twopair_max ((p!1 (Pair Int Int)) (p!2 (Pair Int Int))) Int (ite (< (pair_max p!1) (pair_max p!2)) (pair_max p!2) (pair_max p!1)))\n"
			+ "(define-fun twopair_min ((p!1 (Pair Int Int)) (p!2 (Pair Int Int))) Int (ite (< (pair_min p!1) (pair_min p!2)) (pair_min p!1) (pair_min p!2)))\n"
			+ "(define-fun pair_lte ((p!1 (Pair Int Int)) (p!2 (Pair Int Int))) Bool (and (<= (fst p!1) (fst p!2)) (<= (snd p!1) (snd p!2)) ))\n"
			+ "(define-fun pair_lt ((p!1 (Pair Int Int)) (p!2 (Pair Int Int))) Bool (and (pair_lte p!1 p!2) (not (= p!1 p!2))))\n"
			+ "(define-fun pair_gte ((p!1 (Pair Int Int)) (p!2 (Pair Int Int))) Bool (pair_lte p!2 p!1))\n"
			+ "(define-fun pair_gt ((p!1 (Pair Int Int)) (p!2 (Pair Int Int))) Bool (pair_lt p!2 p!1))\n"
			+ "(define-fun pair_plus ((p!1 (Pair Int Int)) (p!2 (Pair Int Int))) (Pair Int Int) (mk-pair (+ (fst p!1) (fst p!2)) (+ (snd p!1) (snd p!2))))\n"
			+ "(define-fun pair_sub ((p!1 (Pair Int Int)) (p!2 (Pair Int Int))) (Pair Int Int) (mk-pair (- (fst p!1) (fst p!2)) (- (snd p!1) (snd p!2))))\n"
			+ smt2;
		smt2 = smt2 + "\n(check-sat)\n(exit)";
		try
		{
			tmp = File.createTempFile(gpd.header.name.toString(), ".smt2.tmp");
			ScribUtil.writeToFile(tmp, smt2);
			String[] res = ScribUtil.runProcess("z3", tmp.getAbsolutePath());//, "-T:60");
			String trim = res[0].trim();
			if (trim.equals("sat"))  // FIXME: factor out
			{
				return true;
			}
			else if (trim.equals("unsat"))
			{
				return false;
			}
			else
			{
				throw new RuntimeException("[assrt] Z3 error: " + Arrays.toString(res));
			}
		}
		catch (IOException x)
		{
			throw new RuntimeException(x);
		}
		catch (ScribbleException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			if (tmp != null)
			{
				tmp.delete();
			}
		}
	}
}
