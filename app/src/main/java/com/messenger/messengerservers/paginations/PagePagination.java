package com.messenger.messengerservers.paginations;

import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;


public abstract class PagePagination<T> {
    protected PublishSubject<List<T>> publishSubject = PublishSubject.create();
    protected int pageSize;

    public PagePagination(int sizePerPage) {
        this.pageSize = sizePerPage;
    }

    public abstract void loadPage(String conversationId, int page, long offset);

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Observable<List<T>> getPageObservable() {
        return publishSubject.asObservable();
    }
}
