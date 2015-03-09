package com.worldventures.dreamtrips.view.cell;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.BucketItem;
import com.worldventures.dreamtrips.utils.busevents.DeleteBucketItemEvent;
import com.worldventures.dreamtrips.utils.busevents.MarkBucketItemDoneEvent;
import com.worldventures.dreamtrips.view.custom.Airy;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_bucket_item)
public class BucketItemCell extends AbstractCell<BucketItem> implements DraggableItemViewHolder {

    @InjectView(R.id.container)
    RelativeLayout container;
    @InjectView(R.id.textViewName)
    TextView tvName;
    @InjectView(R.id.button_cancel)
    ImageView buttonCancel;
    @InjectView(R.id.drag_handle)
    View drag_handle;

    @Inject
    Context context;

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

        update();

        Airy airy = new Airy(context) {
            @Override
            public void onGesture(View pView, int pGestureId) {
                switch (pGestureId) {
                    case SWIPE_RIGHT:
                        getModelObject().setDone(true);
                        getEventBus().post(new MarkBucketItemDoneEvent(getModelObject()));
                        update();
                        break;
                    case SWIPE_LEFT:
                        getModelObject().setDone(false);
                        update();
                        getEventBus().post(new MarkBucketItemDoneEvent(getModelObject()));
                        break;
                }
            }
        };

        container.setOnTouchListener(airy);

        // set background resource (target view ID: container)
        final int dragState = getDragStateFlags();

        if (((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;

            if ((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;
            } else if ((dragState & RecyclerViewDragDropManager.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bucket_item_selector;
            }

            container.setBackgroundResource(bgResId);
        }
    }

    private void update() {
        if (getModelObject().isDone()) {
            tvName.setPaintFlags(tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvName.setTextColor(context.getResources().getColor(R.color.bucket_text_done));
            buttonCancel.setImageResource(R.drawable.ic_cancel_grey);
        } else {
            tvName.setPaintFlags(tvName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            tvName.setTextColor(context.getResources().getColor(R.color.bucket_text_to_do));
            buttonCancel.setImageResource(R.drawable.ic_keyboard_arrow_right);
        }

    }

    @OnClick(R.id.button_cancel)
    void delete() {
        if (getModelObject().isDone()) {
            getEventBus().post(new DeleteBucketItemEvent(getModelObject(), getPosition()));
        } else {

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

    public View getContainerView() {
        return container;
    }

    public View getDraggableView() {
        return drag_handle;
    }

}
