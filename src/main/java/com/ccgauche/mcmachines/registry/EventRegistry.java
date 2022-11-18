package com.ccgauche.mcmachines.registry;

import java.util.HashMap;

import com.ccgauche.mcmachines.registry.events.MachineTickListener;

/**
 * The registry in which all events are stored. Used by the executables to
 * register events.
 */
public class EventRegistry {

	public static final HashMap<String, MachineTickListener> machineTickListeners = new java.util.HashMap<>();

}
