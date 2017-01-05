/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.worldventures.dreamtrips.modules.common.view.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.module.Injector;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

public abstract class DraggableArrayListAdapter<V> extends BaseDelegateAdapter<V> implements DraggableItemAdapter<DraggableArrayListAdapter.DraggableCell> {

   private MoveListener moveListener;
   private SparseBooleanArray dragMarkers;

   public DraggableArrayListAdapter(Context context, Injector injector) {
      super(context, injector);
      setHasStableIds(true);
      dragMarkers = new SparseBooleanArray();
   }

   @Override
   public abstract long getItemId(int position);

   @Override
   public boolean onCheckCanStartDrag(DraggableCell bucketItemCell, int position, int x, int y) {
      return bucketItemCell.onCheckCanStartDrag(position, x, y);
   }

   @Override
   public ItemDraggableRange onGetItemDraggableRange(DraggableCell bucketItemCell, int position) {
      int startPosition = getStartDragPosition(position);
      int endPosition = getEndDragPosition(position);

      return new ItemDraggableRange(startPosition, endPosition);
   }

   private int getStartDragPosition(int currentPosition) {
      int position = 0;
      for (int i = currentPosition; i >= 0; i--) {
         if (dragMarkers.get(i - 1)) {
            position = i;
            break;
         }
      }
      return position;
   }

   private int getEndDragPosition(int currentPosition) {
      int position = getCount() - 1;
      for (int i = currentPosition; i < getCount(); i++) {
         if (dragMarkers.get(i + 1)) {
            position = i;
            break;
         }
      }
      return position;
   }

   public void setDragMarker(int position, boolean enabled) {
      dragMarkers.clear();
      dragMarkers.put(position, enabled);
   }

   @Override
   public void onMoveItem(int fromPosition, int toPosition) {
      if (fromPosition == toPosition) {
         return;
      }

      V item = getItem(fromPosition);

      if (item instanceof BucketItem && !((BucketItem) item).isDone() && moveListener != null) {
         moveListener.onItemMoved(fromPosition, toPosition);
      }

      moveItem(fromPosition, toPosition);
      notifyItemMoved(fromPosition, toPosition);
   }

   public void setMoveListener(MoveListener moveListener) {
      this.moveListener = moveListener;
   }

   public interface MoveListener {
      void onItemMoved(int from, int to);
   }

   public static abstract class DraggableCell<T, V extends CellDelegate<T>> extends AbstractDelegateCell<T, V> implements DraggableItemViewHolder {
      public DraggableCell(View view) {
         super(view);
      }

      public abstract boolean onCheckCanStartDrag(int position, int x, int y);
   }

}