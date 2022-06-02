package com.ccgauche.mcmachines.json.parser;

import com.ccgauche.mcmachines.json.FileException;
import com.ccgauche.mcmachines.json.JSONContext;
import com.ccgauche.mcmachines.json.conditions.ICondition;

public class IConditionParser {

	public static ICondition parse(JSONContext context) throws FileException {
		return ICondition.parse(StringParser.parse(context));
	}
}
