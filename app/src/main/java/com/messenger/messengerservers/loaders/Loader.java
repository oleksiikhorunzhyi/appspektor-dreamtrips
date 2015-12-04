package com.messenger.messengerservers.loaders;

import java.util.List;

import com.messenger.messengerservers.Persister;
import com.messenger.messengerservers.listeners.OnLoadedListener;

public abstract class Loader<T> {
    protected Persister<List<T>> persister;
    protected OnLoadedListener<T> onEntityLoadedListener;

    public void setPersister(Persister<List<T>> persister) {
        this.persister = persister;
    }

    public void setOnEntityLoadedListener(OnLoadedListener onEntityLoadedListener) {
        this.onEntityLoadedListener = onEntityLoadedListener;
    }

    public abstract void load();
}
