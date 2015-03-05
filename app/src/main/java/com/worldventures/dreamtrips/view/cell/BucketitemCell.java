package com.worldventures.dreamtrips.view.cell;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.gc.materialdesign.views.LayoutRipple;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemViewHolder;
import com.techery.spares.annotations.Layout;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.BucketItem;
import com.worldventures.dreamtrips.core.repository.BucketListSelectionStorage;
import com.worldventures.dreamtrips.utils.busevents.DeleteBucketItemEvent;
import com.worldventures.dreamtrips.utils.busevents.MarkBucketItemDoneEvent;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_bucket_item)
public class BucketItemCell extends AbstractCell<BucketItem> implements DraggableItemViewHolder, SwipeableItemViewHolder{

    @InjectView(R.id.container)
    RelativeLayout container;
    @InjectView(R.id.textViewName)
    TextView tvName;
    @InjectView(R.id.checkBox)
    CheckBox checkBoxDone;
    @InjectView(R.id.draggable)
    View draggable;

    private int mDragStateFlags;
    private int mSwipeStateFlags;
    private int mSwipeResult = RecyclerViewSwipeManager.RESULT_NONE;
    private int mAfterSwipeReaction = RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_DEFAULT;
    private float mSwipeAmount;

    public BucketItemCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        tvName.setText(getModelObject().getName() + " id =" + getModelObject().getItemId());
        checkBoxDone.setChecked(getModelObject().isDone());
        checkBoxDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
                getModelObject().setDone(isChecked);
                getEventBus().post(new MarkBucketItemDoneEvent(getModelObject()));
        });

        // set background resource (target view ID: container)
        final int dragState = getDragStateFlags();
        final int swipeState = getSwipeStateFlags();

        if (((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_UPDATED) != 0) ||
                ((swipeState & RecyclerViewSwipeManager.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;

            if ((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;
            } else if ((dragState & RecyclerViewDragDropManager.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            container.setBackgroundResource(bgResId);
        }
    }

    @Override
    public void prepareForReuse() {

    }

    @Override
    public void setDragStateFlags(int flags) {
        mDragStateFlags = flags;
    }

    @Override
    public int getDragStateFlags() {
        return mDragStateFlags;
    }

    @Override
    public void setSwipeStateFlags(int flags) {
        mSwipeStateFlags = flags;
    }

    @Override
    public int getSwipeStateFlags() {
        return mSwipeStateFlags;
    }

    @Override
    public void setSwipeResult(int result) {
        mSwipeResult = result;
    }

    @Override
    public int getSwipeResult() {
        return mSwipeResult;
    }

    @Override
    public int getAfterSwipeReaction() {
        return mAfterSwipeReaction;
    }

    @Override
    public void setAfterSwipeReaction(int reaction) {
        mAfterSwipeReaction = reaction;
    }

    @Override
    public float getSwipeItemSlideAmount() {
        return mSwipeAmount;
    }

    @Override
    public void setSwipeItemSlideAmount(float amount) {
        mSwipeAmount = amount;
    }

    @Override
    public View getSwipeableContainerView() {
        return container;
    }

    public View getContainerView() {
        return container;
    }

    public View getDraggableView() {
        return draggable;
    }
}
