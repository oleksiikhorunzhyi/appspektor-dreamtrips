package com.messenger.messengerservers.loaders;


import com.messenger.messengerservers.Persister;
import com.messenger.messengerservers.listeners.OnLoadedListener;

import java.util.List;


public abstract class Loader<T> {
    protected Persister<List<T>> persister;
    protected OnLoadedListener<T> onEntityLoadedListener;

    public void setPersister(Persister<List<T>> persister) {
        this.persister = persister;
    }

    public void setOnEntityLoadedListener(OnLoadedListener<T> onEntityLoadedListener) {
        this.onEntityLoadedListener = onEntityLoadedListener;
    }

    public abstract void load();
}
