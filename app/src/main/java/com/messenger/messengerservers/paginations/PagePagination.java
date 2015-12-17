package com.messenger.messengerservers.paginations;

import com.messenger.messengerservers.Persister;
import com.messenger.messengerservers.listeners.OnLoadedListener;

import java.util.List;


public abstract class PagePagination<T> {

    protected Persister<List<T>> persister;
    protected OnLoadedListener<T> onEntityLoadedListener;
    protected int sizePerPage;

    public PagePagination(int sizePerPage) {
        this.sizePerPage = sizePerPage;
    }

    public void setPersister(Persister<List<T>> persister) {
        this.persister = persister;
    }

    public void setOnEntityLoadedListener(OnLoadedListener onEntityLoadedListener) {
        this.onEntityLoadedListener = onEntityLoadedListener;
    }

    public abstract void loadPage(int page);

    public int getSizePerPage(){
        return sizePerPage;
    }
}
