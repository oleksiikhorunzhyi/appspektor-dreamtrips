package com.worldventures.dreamtrips.view.cell;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.bucket.BucketItem;
import com.worldventures.dreamtrips.utils.busevents.DeleteBucketItemEvent;
import com.worldventures.dreamtrips.utils.busevents.MarkBucketItemDoneEvent;
import com.worldventures.dreamtrips.view.custom.Airy;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_bucket_item)
public class BucketItemCell extends AbstractCell<BucketItem> implements DraggableItemViewHolder, SwipeLayout.SwipeListener {

    @InjectView(R.id.container)
    RelativeLayout container;
    @InjectView(R.id.bottom_wrapper)
    LinearLayout linearLayout;
    @InjectView(R.id.textViewName)
    TextView tvName;
    @InjectView(R.id.button_cancel)
    ImageView buttonCancel;
    @InjectView(R.id.drag_handle)
    View drag_handle;
    @InjectView(R.id.swipeLayout)
    SwipeLayout swipeLayout;
    @InjectView(R.id.imageViewStatus)
    ImageView imageViewStatus;
    @InjectView(R.id.crossing)
    View crossing;

    @Inject
    Context context;

    private int mDragStateFlags;
    private int mSwipeStateFlags;
    private int mSwipeResult = RecyclerViewSwipeManager.RESULT_NONE;
    private int mAfterSwipeReaction = RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_DEFAULT;
    private float mSwipeAmount;

    private boolean requestWasSent = false;
    private boolean handRelease = false;

    public BucketItemCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        tvName.setText(getModelObject().getName() + " id =" + getModelObject().getId());

        update();

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
            tvName.setTextColor(context.getResources().getColor(R.color.bucket_text_done));
            crossing.setVisibility(View.VISIBLE);
            buttonCancel.setImageResource(0);
        } else {
            tvName.setTextColor(context.getResources().getColor(R.color.bucket_text_to_do));
            crossing.setVisibility(View.GONE);
            buttonCancel.setImageResource(R.drawable.ic_keyboard_arrow_right);
        }

    }

    @Override
    public void prepareForReuse() {
        //set show mode.
        swipeLayout.close(false);
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        //set drag edge.
        swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);
        swipeLayout.removeSwipeListener(this);
        swipeLayout.addSwipeListener(this);
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

    private int lastOffset;

    @Override
    public void onStartOpen(SwipeLayout swipeLayout) {
        requestWasSent = false;
        handRelease = false;
    }

    @Override
    public void onOpen(SwipeLayout swipeLayout) {
        swipeLayout.close();
    }

    @Override
    public void onStartClose(SwipeLayout swipeLayout) {
    }

    @Override
    public void onClose(SwipeLayout swipeLayout) {
        onCancel();
    }

    @Override
    public void onUpdate(SwipeLayout swipeLayout, int leftOffset, int topOffset) {
        onUpdate(leftOffset);
    }

    @Override
    public void onHandRelease(SwipeLayout swipeLayout, float xvel, float yVel) {
        handRelease = true;
        swipeLayout.close();
    }

    private void onUpdate(int leftOffset) {
        if (!handRelease)
            lastOffset = leftOffset;

        if (leftOffset > swipeLayout.getWidth() * 2 / 3f) {
            imageViewStatus.setImageResource(R.drawable.delete);
            linearLayout.setBackgroundColor(context.getResources().getColor(R.color.bucket_red));
        } else {
            imageViewStatus.setImageResource(R.drawable.done);
            linearLayout.setBackgroundColor(context.getResources().getColor(R.color.bucket_green));
        }
    }

    private void onCancel() {
        if (!requestWasSent)
            if (lastOffset > swipeLayout.getWidth() * 2 / 3f) {
                requestWasSent = true;
                Log.d("TAG_BucketListPM", "Sending delete event");
                swipeLayout.post(() -> getEventBus().post(new DeleteBucketItemEvent(getModelObject(), getPosition())));
            } else if (lastOffset > swipeLayout.getWidth() / 3f) {
                Log.d("TAG_BucketListPM", "Sending complete event");
                requestWasSent = true;
                getModelObject().setDone(!getModelObject().isDone());
                update();
                swipeLayout.post(() -> getEventBus().post(new MarkBucketItemDoneEvent(getModelObject(), getPosition())));
            }
    }
}
