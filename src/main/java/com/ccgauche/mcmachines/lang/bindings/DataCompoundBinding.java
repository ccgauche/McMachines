package com.ccgauche.mcmachines.lang.bindings;

import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.lang.Method;
import com.ccgauche.mcmachines.lang.TypeAlias;

@TypeAlias(name = "DataCompound")
public class DataCompoundBinding {

	public static DataCompound empty() {
		return new DataCompound();
	}

	@Method
	public static DataCompound clone(DataCompound obj) {
		return obj.clone();
	}

	@Method
	public static DataCompound set(String key, Object value, DataCompound obj) {
		obj.set(key, value);
		return obj;
	}

	@Method
	public static Object get(String key, DataCompound obj) {
		return obj.get(key);
	}

}
