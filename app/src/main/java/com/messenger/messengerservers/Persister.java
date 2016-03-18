package com.messenger.messengerservers;

public interface Persister<T> {

    void save(T t);
}
