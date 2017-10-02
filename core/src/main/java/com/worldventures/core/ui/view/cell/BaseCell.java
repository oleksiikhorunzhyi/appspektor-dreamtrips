package com.worldventures.core.ui.view.cell;

public interface BaseCell<T> {

   void fillWithItem(T item);

   void prepareForReuse();

   void clearResources();
}