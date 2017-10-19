package com.worldventures.core.ui.view.adapter;

import java.util.List;

public interface ListAdapter<T> {

   int getCount();

   void clear();

   void addItems(List<T> baseItemClasses);

   void notifyDataSetChanged();
}
