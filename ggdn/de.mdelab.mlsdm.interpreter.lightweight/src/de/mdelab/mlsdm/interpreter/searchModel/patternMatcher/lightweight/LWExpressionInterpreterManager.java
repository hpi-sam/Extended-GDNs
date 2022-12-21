package de.mdelab.mlsdm.interpreter.searchModel.patternMatcher.lightweight;

import java.util.HashMap;
import java.util.Map;

import de.mdelab.mlsdm.interpreter.MLSDMExpressionInterpreterManager;

public class LWExpressionInterpreterManager extends MLSDMExpressionInterpreterManager {

	private Map<String, MLSDMLWExpressionInterpreter> interpreters;
	
	public LWExpressionInterpreterManager() {
		super(LWExpressionInterpreterManager.class.getClassLoader());
		this.interpreters = new HashMap<String, MLSDMLWExpressionInterpreter>();
	}
	
	public void registerExpressionInterpreter(String language, MLSDMLWExpressionInterpreter interpreter) {
		interpreters.put(language, interpreter);
	}
	
	public MLSDMLWExpressionInterpreter getInterpreter(String language) {
		return interpreters.get(language);
	}
}
