package com.worldventures.dreamtrips.modules.bucketlist.view.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.worldventures.dreamtrips.R;

import java.util.Collections;
import java.util.List;

public class AutoCompleteAdapter<T> extends ArrayAdapter<T> implements Filterable {

   protected Loader<T> loader;

   public AutoCompleteAdapter(Context context) {
      super(context, R.layout.item_dropdown);
   }

   @Override
   public Filter getFilter() {
      return new AutoCompleteFilter<>(this);
   }

   public void setLoader(Loader<T> loader) {
      this.loader = loader;
   }

   public Loader<T> getLoader() {
      return loader;
   }

   public static abstract class Loader<T> {

      public List<T> load(String query) {
         try {
            return request(query);
         } catch (Exception e) {
            handleError(e);
            return Collections.emptyList();
         }
      }

      protected abstract List<T> request(String query);

      public abstract void handleError(Exception e);
   }
}