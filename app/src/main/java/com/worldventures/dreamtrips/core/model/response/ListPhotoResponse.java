package com.worldventures.dreamtrips.core.model.response;

import com.worldventures.dreamtrips.core.model.Photo;

import java.util.ArrayList;

public class ListPhotoResponse {
    long total;
    long perPage;
    long currentPage;
    long lastPage;
    long from;
    long to;

    ArrayList<Photo> data;

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

    public ArrayList<Photo> getData() {
        if (data == null) return new ArrayList<>();
        return data;
    }

    public void setData(ArrayList<Photo> data) {
        this.data = data;
    }
}
