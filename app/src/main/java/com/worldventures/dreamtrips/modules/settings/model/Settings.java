package com.worldventures.dreamtrips.modules.settings.model;

public class Settings<T> {

    private int id;
    private String name;
    private Type type;
    protected T value;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public T getValue() {
        return value;
    }

    public enum Type {
        FLAG, SELECT, UNKNOWN
    }
}
