package com.ccgauche.mcmachines.lang;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Enables changing the name of a type
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeAlias {
	String name();
}
