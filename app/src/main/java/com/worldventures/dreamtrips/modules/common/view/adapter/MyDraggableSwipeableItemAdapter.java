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

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketHeader;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketItemCell;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

public class MyDraggableSwipeableItemAdapter<V>
        extends BaseArrayListAdapter<V>
        implements DraggableItemAdapter<BucketItemCell> {

    private MoveListener moveListener;

    public MyDraggableSwipeableItemAdapter(Context context, Injector injector) {
        super(context, injector);
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        V baseItemClass = getItem(position);
        if (baseItemClass instanceof BaseEntity) {
            return ((BaseEntity) baseItemClass).getId();
        }
        return 0;
    }

    @Override
    public boolean onCheckCanStartDrag(BucketItemCell bucketItemCell, int x, int y) {
        return bucketItemCell.isLongPressed();
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(BucketItemCell bucketItemCell) {
        int startPosition = getStartDragPosition(bucketItemCell.getPosition());
        int endPosition = getEndDragPosition(bucketItemCell.getPosition());

        return new ItemDraggableRange(startPosition, endPosition);
    }

    private int getStartDragPosition(int currentPosition) {
        int position = currentPosition;
        Object item = getItem(position);

        if (item instanceof BucketHeader) {
            throw new IllegalStateException("section item is expected");
        }

        while (position > 0) {
            Object prevItem = getItem(position - 1);

            if (prevItem instanceof BucketHeader) {
                break;
            }

            position -= 1;
        }

        return position;
    }

    private int getEndDragPosition(int currentPosition) {
        int position = currentPosition;
        Object item = getItem(position);

        if (item instanceof BucketHeader) {
            throw new IllegalStateException("section item is expected");
        }

        final int lastIndex = getItemCount() - 1;

        while (position < lastIndex) {
            Object nextItem = getItem(position + 1);

            if (nextItem instanceof BucketHeader) {
                break;
            }

            position += 1;
        }

        return position;
    }


    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        V item = getItem(fromPosition);

        if (item instanceof BucketItem
                && !((BucketItem) item).isDone()
                && moveListener != null) {
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

}