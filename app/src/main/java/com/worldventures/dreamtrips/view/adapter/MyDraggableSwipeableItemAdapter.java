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

package com.worldventures.dreamtrips.view.adapter;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.model.BaseEntity;
import com.worldventures.dreamtrips.core.model.bucket.BucketHeader;
import com.worldventures.dreamtrips.core.model.bucket.BucketItem;
import com.worldventures.dreamtrips.view.cell.BucketItemCellOld;
import com.worldventures.dreamtrips.view.util.ViewUtils;

public class MyDraggableSwipeableItemAdapter<BaseItemClass>
        extends BaseArrayListAdapter<BaseItemClass>
        implements DraggableItemAdapter<BucketItemCellOld>{

    private DeleteListener deleteListener;
    private MoveListener moveListener;
    private View.OnClickListener mItemViewOnClickListener;

    public MyDraggableSwipeableItemAdapter(Context context, Injector injector) {
        super(context, injector);
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        BaseItemClass baseItemClass = getItem(position);
        if (baseItemClass instanceof BaseEntity) {
            return ((BaseEntity) baseItemClass).getId();
        }
        return 0;
    }

    @Override
    public boolean onCheckCanStartDrag(BucketItemCellOld bucketItemCell, int x, int y) {
        // x, y --- relative from the itemView's top-left
        final View containerView = bucketItemCell.getContainerView();
        final View dragHandleView = bucketItemCell.getDraggableView();

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(BucketItemCellOld bucketItemCell) {
        int startPosition = getStartDragPosition(bucketItemCell.getPosition());
        int endPosition = getEndDragPosition(bucketItemCell.getPosition());

        return new ItemDraggableRange(startPosition, endPosition);
    }

    private int getStartDragPosition(int position) {
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

        return position;    }

    private int getEndDragPosition(int position) {
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
        Log.d("Move", "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");

        if (fromPosition == toPosition) {
            return;
        }

        BaseItemClass item = getItem(fromPosition);

        if (item instanceof BucketItem) {
            if (!((BucketItem) item).isDone()) {
                if (moveListener != null) {
                    moveListener.onItemMoved(fromPosition - 1, toPosition - 1);
                }
            } else {
                BaseItemClass firstItem = getItem(1);
                if (firstItem instanceof BucketItem) {
                    if (((BucketItem) firstItem).isDone()) {
                        moveListener.onItemMoved(fromPosition - 1, toPosition - 1);
                    } else {
                        moveListener.onItemMoved(fromPosition - 2, toPosition - 2);
                    }
                }
            }
        }

        moveItem(fromPosition, toPosition);

        notifyItemMoved(fromPosition, toPosition);
    }

    public void setEventListener(DeleteListener eventListener) {
        deleteListener = eventListener;
    }

    public void setMoveListener(MoveListener moveListener) {
        this.moveListener = moveListener;
    }

    public interface DeleteListener {
        void onItemRemoved(int position);
    }

    public interface MoveListener {
        void onItemMoved(int from, int to);
    }

}