package com.worldventures.dreamtrips.modules.bucketlist.view.adapter;

import android.widget.Filter;

import java.util.List;

public class AutoCompleteFilter<T> extends Filter {

   protected AutoCompleteAdapter<T> adapter;

   public AutoCompleteFilter(AutoCompleteAdapter<T> adapter) {
      this.adapter = adapter;
   }

   @Override
   protected FilterResults performFiltering(CharSequence constraint) {
      FilterResults filterResults = new FilterResults();
      if (constraint != null) {
         if (adapter.getLoader() == null) {
            throw new IllegalStateException("Loader can't be null");
         }
         List<T> items = adapter.getLoader().load(constraint.toString());
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
         adapter.clear();
         adapter.addAll((List<T>) results.values);
         adapter.notifyDataSetChanged();
      } else {
         adapter.notifyDataSetInvalidated();
      }
   }
}