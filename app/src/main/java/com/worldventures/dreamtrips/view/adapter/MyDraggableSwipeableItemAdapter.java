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
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Injector;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.view.adapter.item.Swipeable;
import com.worldventures.dreamtrips.view.cell.BucketItemCell;
import com.worldventures.dreamtrips.view.util.AdapterUtils;
import com.worldventures.dreamtrips.view.util.ViewUtils;

public class MyDraggableSwipeableItemAdapter<BaseItemClass extends Swipeable>
        extends BaseArrayListAdapter<BaseItemClass>
        implements DraggableItemAdapter<BucketItemCell>,
        SwipeableItemAdapter<BucketItemCell> {

    private EventListener mEventListener;
    private View.OnClickListener mItemViewOnClickListener;

    public MyDraggableSwipeableItemAdapter(Context context, Injector injector) {
        super(context, injector);
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getItemId();
    }

    @Override
    public boolean onCheckCanStartDrag(BucketItemCell bucketItemCell, int x, int y) {
        // x, y --- relative from the itemView's top-left
        final View containerView = bucketItemCell.getContainerView();
        final View dragHandleView = bucketItemCell.getDraggableView();

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(BucketItemCell bucketItemCell) {
        return null;
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        Log.d("Move", "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");

        if (fromPosition == toPosition) {
            return;
        }

        moveItem(fromPosition, toPosition);

        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public int onGetSwipeReactionType(BucketItemCell bucketItemCell, int x, int y) {
        if (onCheckCanStartDrag(bucketItemCell, x, y)) {
            return RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_BOTH;
        } else {
            return items.get(bucketItemCell.getPosition()).getSwipeReactionType();
        }
    }

    @Override
    public void onSetSwipeBackground(BucketItemCell holder, int type) {
        int bgRes = 0;
        switch (type) {
            case RecyclerViewSwipeManager.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_neutral;
                break;
            case RecyclerViewSwipeManager.DRAWABLE_SWIPE_LEFT_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_left;
                break;
        }

        holder.getContainerView().setBackgroundResource(bgRes);
    }

    @Override
    public int onSwipeItem(BucketItemCell bucketItemCell, int result) {
        switch (result) {
            // swipe left --- remove
            case RecyclerViewSwipeManager.RESULT_SWIPED_LEFT:
                return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM;
            // other --- do nothing
            case RecyclerViewSwipeManager.RESULT_CANCELED:
            default:
                return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_DEFAULT;
        }
    }

    @Override
    public void onPerformAfterSwipeReaction(BucketItemCell holder, int result, int reaction) {
        Log.d("Swipe", "onPerformAfterSwipeReaction(result = " + result + ", reaction = " + reaction + ")");

        final int position = holder.getPosition();
        final Swipeable item = getItem(position);

        if (reaction == RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM) {
            remove(position);
            notifyItemRemoved(position);
            if (mEventListener != null) {
                mEventListener.onItemRemoved(position);
            }

        } else if (reaction == RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION) {
            item.setPinnedToSwipeLeft(true);
            if (mEventListener != null) {
                mEventListener.onItemPinned(position);
            }
            notifyItemChanged(position);
        } else {
            item.setPinnedToSwipeLeft(false);
        }
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    public interface EventListener {
        void onItemRemoved(int position);

        void onItemPinned(int position);

        void onItemViewClicked(View v, boolean pinned);
    }

}