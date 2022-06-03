package com.ccgauche.mcmachines.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ccgauche.mcmachines.data.IItem;
import com.ccgauche.mcmachines.handler.events.BlockInteractListener;
import com.ccgauche.mcmachines.handler.events.Listener;
import com.ccgauche.mcmachines.handler.events.PlayerTickListener;

public class Registry {

	public static final Map<String, Listener> listeners = new HashMap<>();

	static {
		addListener(new AtomicDisassembler());
		addListener(new Magnetic());
		addListener(new Effect());
	}

	public static void addListener(Listener listener) {
		listeners.put(listener.handlerId(), listener);
	}

	public static void addListeners(IItem item) {
		for (String handlercommand : item.handlers()) {
			String handlername = handlercommand.split(":")[0];
			String handlerargument = (handlercommand.contains(":") ? handlercommand.substring(handlername.length() + 1)
					: "").trim();
			handlername = handlername.trim();
			Listener listener = listeners.get(handlername);
			if (listener == null) {
				System.out.println("Handler " + handlername + " doesn't exist");
				System.exit(0);
				return;
			}
			listener = listener.derive(handlerargument);
			boolean k = true;
			if (listener instanceof BlockInteractListener lst) {
				k = false;
				blockInteract.putIfAbsent(item.id(), new ArrayList<>());
				blockInteract.get(item.id()).add(lst);
			}
			if (listener instanceof PlayerTickListener lst) {
				k = false;
				playerTick.putIfAbsent(item.id(), new ArrayList<>());
				playerTick.get(item.id()).add(lst);
			}
			if (k) {
				System.out.println("Unknown listener type");
			}
		}
	}

	public static final Map<String, List<BlockInteractListener>> blockInteract = new HashMap<>();
	public static final Map<String, List<PlayerTickListener>> playerTick = new HashMap<>();

}
