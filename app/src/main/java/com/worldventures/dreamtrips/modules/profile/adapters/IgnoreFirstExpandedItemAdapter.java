package com.worldventures.dreamtrips.modules.profile.adapters;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.IgnoreFirstItemAdapter;

public class IgnoreFirstExpandedItemAdapter extends IgnoreFirstItemAdapter {

   private boolean firstExpanded;

   public IgnoreFirstExpandedItemAdapter(Context context, Injector injector) {
      super(context, injector);
      firstExpanded = false;
   }

   @Override
   public void onBindViewHolder(AbstractCell cell, int position) {
      super.onBindViewHolder(cell, position);
      if (cell instanceof Expandable && position == 0) {
         Expandable expandableCell = ((Expandable) cell);
         expandableCell.setExpanded(firstExpanded);
         expandableCell.setListener(expanded -> firstExpanded = expanded);
      }
   }
}
