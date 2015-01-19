package com.worldventures.dreamtrips.core.repository;


import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

import static com.google.common.base.Preconditions.checkNotNull;

public class Repository<T extends RealmObject> {

    public interface Consumer<T> {
        public void consume(T object);
    }

    public interface TransactionCallback {
        public void consume(Realm realm);
    }

    private final Realm realm;
    private final Class<T> itemClazz;

    public Repository(Realm realm, Class<T> itemClazz) {
        checkNotNull(realm);
        checkNotNull(itemClazz);

        this.realm = realm;
        this.itemClazz = itemClazz;
    }

    public T create(Consumer<T> consumer) {
        checkNotNull(consumer);

        this.realm.beginTransaction();

        T object = this.realm.createObject(this.itemClazz);
        consumer.consume(object);

        this.realm.commitTransaction();

        return object;
    }

    public RealmQuery<T> query() {
        return this.realm.where(this.itemClazz);
    }

    public void remove(T item) {
        checkNotNull(item);
        transaction((r) -> item.removeFromRealm());
    }

    public void transaction(TransactionCallback callback) {
        checkNotNull(callback);

        try {
            this.realm.beginTransaction();

            callback.consume(this.realm);

            this.realm.commitTransaction();

        } catch (Throwable t) {
            this.realm.cancelTransaction();
        }
    }
}
