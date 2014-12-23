package com.techery.spares.storage.complex_objects;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.techery.spares.storage.ObjectStorage;
import com.techery.spares.storage.preferences.ObjectPreferenceStorage;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;

public class ComplexObjectStorage<T> implements ObjectStorage<T> {

    private final Gson gson = new Gson();
    private final ObjectStorage<String> storage;
    private final Class<T> typeClass;
    private Optional<T> cachedInstance = Optional.absent();

    public ComplexObjectStorage(SimpleKeyValueStorage storage, String key, Class<T> objectClass) {
        this(new ObjectPreferenceStorage(storage, key), objectClass);
    }

    public ComplexObjectStorage(ObjectStorage<String> storage, Class<T> objectClass) {
        this.storage = storage;
        this.typeClass = objectClass;
    }

    public Optional<T> get() {
        if (!cachedInstance.isPresent()) {
            Optional<String> value = this.storage.get();

            if (value.isPresent()) {
                cachedInstance = Optional.of(this.gson.fromJson(value.get(), typeClass));
            }
        }

        return cachedInstance;
    }

    public void put(T obj) {
        this.storage.put(this.gson.toJson(obj));
        this.cachedInstance = Optional.of(obj);
    }

    @Override
    public void destroy() {
        this.storage.destroy();
        this.cachedInstance = Optional.absent();
    }
}
