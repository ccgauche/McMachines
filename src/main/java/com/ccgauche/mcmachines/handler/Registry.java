package com.ccgauche.mcmachines.handler;

import com.ccgauche.mcmachines.handler.events.Listener;
import com.ccgauche.mcmachines.data.IItem;
import com.ccgauche.mcmachines.handler.events.BlockInteractListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Registry {

    public static final Map<String, Listener> listeners = new HashMap<>();

    static {
        addListener(new AtomicDisassembler());
    }
    public static void addListener(Listener listener) {
        listeners.put(listener.handlerId(), listener);
    }
    public static void addListeners(IItem item) {
        for (String handler : item.handlers()) {
            Listener listener = listeners.get(handler);
            if (listener == null) {
                System.out.println("Handler "+handler+" doesn't exist");
                System.exit(0);
                return;
            }
            boolean k = true;
            if (listener instanceof BlockInteractListener lst) {
                k = false;
                blockInteract.putIfAbsent(item.id(), new ArrayList<>());
                blockInteract.get(item.id()).add(lst);
            }
            if (k) {
                System.out.println("Unknown listener type");
            }
        }
    }
    public static final Map<String, List<BlockInteractListener>> blockInteract = new HashMap<>();

}
