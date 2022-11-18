package com.ccgauche.mcmachines.lang.bindings;

import com.ccgauche.mcmachines.json.Dual;
import com.ccgauche.mcmachines.lang.Method;
import com.ccgauche.mcmachines.lang.TypeAlias;

@TypeAlias(name = "Dual")
public class DualBinding {

	public static Dual $new(Object t, Object u) {
		return new Dual(t, u);
	}

	@Method
	public static Object get0(Dual dual) {
		return dual.first();
	}

	@Method
	public static Object get1(Dual dual) {
		return dual.second();
	}

}
