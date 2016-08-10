package com.messenger.messengerservers.paginations;

import rx.Observable;
import rx.subjects.PublishSubject;


public abstract class PagePagination<T> {
    protected PublishSubject<PaginationResult<T>> paginationPublishSubject = PublishSubject.create();
    protected int pageSize;

    public PagePagination(int sizePerPage) {
        this.pageSize = sizePerPage;
    }

    public abstract Observable<PaginationResult<T>> loadPage(String conversationId, int page, long offset);

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
