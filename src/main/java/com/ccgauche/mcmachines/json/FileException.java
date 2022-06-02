package com.ccgauche.mcmachines.json;

public class FileException extends Exception{
    private final String name;

    public FileException(String name) {
        super(name);
        this.name = name;
    }

    @Override
    public String getMessage() {
        return name;
    }

    public String getName() {
        return name;
    }

}
