package com.techery.spares.ui.view.cell;

import android.content.Context;
import android.os.Bundle;

import de.greenrobot.event.EventBus;

public interface BaseCell<T> {

   void fillWithItem(T item);

   void prepareForReuse();

   void setEventBus(EventBus eventBus);

   void saveState(Bundle b);

   void restoreState(Bundle bundle);

   void clearResources();

   interface CellBuilder<T> {
      BaseCell<T> build(Context c, Class<T> item);
   }
}