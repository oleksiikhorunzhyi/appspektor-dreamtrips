package com.worldventures.dreamtrips.modules.bucketlist.view.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.List;

public class AutoCompleteAdapter<T> extends ArrayAdapter<T> implements Filterable {

    protected Loader<T> loader;

    public AutoCompleteAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<T> items = findBucketItems(constraint.toString());
                    if (items != null) {
                        filterResults.values = items;
                        filterResults.count = items.size();
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    clear();
                    addAll((List<T>) results.values);
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    private List<T> findBucketItems(String query) {
        if (loader == null) {
            throw new IllegalStateException("Loader can't be null");
        }
        return loader.load(query);
    }

    public void setLoader(Loader<T> loader) {
        this.loader = loader;
    }

    public static abstract class Loader<T> {

        public List<T> load(String query) {
            try {
                return request(query);
            } catch (Exception e) {
                handleError(e);
                return null;
            }
        }

        protected abstract List<T> request(String query);

        public abstract void handleError(Exception e);

    }
}