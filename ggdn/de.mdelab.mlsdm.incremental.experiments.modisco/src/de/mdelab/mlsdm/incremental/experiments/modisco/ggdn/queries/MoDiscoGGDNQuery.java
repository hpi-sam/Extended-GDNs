package de.mdelab.mlsdm.incremental.experiments.modisco.ggdn.queries;

import java.io.IOException;

import de.mdelab.mlexpressions.MLExpression;
import de.mdelab.mlsdm.incremental.SDMQuery;
import de.mdelab.mlsdm.incremental.SDMQueryManager;
import de.mdelab.sdm.icl.iCL.ICLConstraint;
import de.mdelab.sdm.interpreter.core.SDMException;
import de.mdelab.sdm.interpreter.icl.ICLExpressionInterpreter;

public abstract class MoDiscoGGDNQuery {

	public static enum GGDNQueryType {CLASS_FIELD_COUNT, TYPE_METHOD_COUNT, PKG_METHOD_SUM}
	
	public static String PATTERN_DIR = "resource/patterns";

	public static SDMQuery createQuery(GGDNQueryType queryType, SDMQueryManager queryManager) throws SDMException, IOException {
		switch(queryType) {
		case CLASS_FIELD_COUNT:
			return new MoDiscoClassFieldCountQuery().createQuery(queryManager);
		case TYPE_METHOD_COUNT:
			return new MoDiscoTypeMethodCountQuery().createQuery(queryManager);
		case PKG_METHOD_SUM:
			return new MoDiscoPackageMethodSumQuery().createQuery(queryManager);
		default:
			return null;
		}
	}
	
	public abstract SDMQuery createQuery(SDMQueryManager queryManager) throws SDMException, IOException;
	
	protected ICLConstraint createICLConstraint(String constraintString) {
		ICLExpressionInterpreter<MLExpression> iclExpressionInterpreter = new ICLExpressionInterpreter<MLExpression>();
		ICLConstraint iclConstraint = null;
		try {
			iclConstraint = (ICLConstraint) iclExpressionInterpreter.parseExpression(constraintString);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return iclConstraint;
	}

}
