package com.worldventures.dreamtrips.core.model.response;

import com.worldventures.dreamtrips.core.model.Photo;

import java.util.Collections;
import java.util.List;

public class ListPhotoResponse {
    long total;
    long perPage;
    long currentPage;
    long lastPage;
    long from;
    long to;
    List<Photo> data;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPerPage() {
        return perPage;
    }

    public void setPerPage(long perPage) {
        this.perPage = perPage;
    }

    public long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(long currentPage) {
        this.currentPage = currentPage;
    }

    public long getLastPage() {
        return lastPage;
    }

    public void setLastPage(long lastPage) {
        this.lastPage = lastPage;
    }

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getTo() {
        return to;
    }

    public void setTo(long to) {
        this.to = to;
    }

    public List<Photo> getData() {
        if (data == null) return Collections.emptyList();
        return data;
    }

    public void setData(List<Photo> data) {
        this.data = data;
    }
}
