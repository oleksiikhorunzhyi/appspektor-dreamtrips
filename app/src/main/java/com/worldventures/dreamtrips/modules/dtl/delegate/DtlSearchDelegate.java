package com.worldventures.dreamtrips.modules.dtl.delegate;

import java.util.ArrayList;
import java.util.List;

public class DtlSearchDelegate {

    private List<SearchListener> listeners = new ArrayList<>();

    public void addListener(SearchListener filterListener) {
        listeners.add(filterListener);
    }

    public void removeListener(SearchListener filterListener) {
        listeners.remove(filterListener);
    }

    public void applySearch(String query) {
        for (SearchListener searchListener : listeners) {
            searchListener.onSearch(query);
        }
    }

    public interface SearchListener {
        void onSearch(String query);
    }
}
