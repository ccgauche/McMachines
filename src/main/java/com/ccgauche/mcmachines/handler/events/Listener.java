package com.ccgauche.mcmachines.handler.events;

public interface Listener {

	String handlerId();

	default Listener derive(String argument) {
		return this;
	}
}
