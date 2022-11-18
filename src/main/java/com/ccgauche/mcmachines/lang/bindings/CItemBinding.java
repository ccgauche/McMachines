package com.ccgauche.mcmachines.lang.bindings;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.lang.TypeAlias;

@TypeAlias(name = "Item")
public class CItemBinding {

	public static CItem of(String name) {
		return new CItem(name);
	}
}
