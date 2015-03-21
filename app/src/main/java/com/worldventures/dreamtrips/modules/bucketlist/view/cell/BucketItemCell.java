package com.worldventures.dreamtrips.modules.bucketlist.view.cell;

import android.content.Context;
import android.support.annotation.IntDef;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.core.utils.events.DeleteBucketItemEvent;
import com.worldventures.dreamtrips.core.utils.events.MarkBucketItemDoneEvent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnLongClick;
import butterknife.OnTouch;
import timber.log.Timber;

import static com.worldventures.dreamtrips.core.utils.ViewUtils.dpFromPx;

@Layout(R.layout.adapter_item_bucket_cell)
public class BucketItemCell extends AbstractCell<BucketItem> implements DraggableItemViewHolder, SwipeLayout.SwipeListener {

    static final int ACTION_DEL = 0;
    static final int ACTION_DONE = 1;
    //
    static final int ACTION_NONE = -1;
    static final int ACTION_SETTLING = -2;
    @InjectView(R.id.container)
    RelativeLayout container;
    @InjectView(R.id.bottom_wrapper)
    ViewGroup bottomContainer;
    @InjectView(R.id.textViewName)
    TextView tvName;
    @InjectView(R.id.button_cancel)
    ImageView buttonCancel;
    @InjectView(R.id.drag_handle)
    View drag_handle;
    @InjectView(R.id.swipeLayout)
    SwipeLayout swipeLayout;
    @InjectView(R.id.imageViewStatusDone)
    ImageView imageViewStatusDone;
    @InjectView(R.id.imageViewStatusClose)
    ImageView imageViewStatusClose;
    @InjectView(R.id.crossing)
    View crossing;
    @Inject
    Context context;
    private int mDragStateFlags;
    private int mSwipeStateFlags;
    private int mSwipeResult = RecyclerViewSwipeManager.RESULT_NONE;
    private int mAfterSwipeReaction = RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_DEFAULT;
    private float mSwipeAmount;
    private boolean afterSwipe = false;
    private int swipeVelocityTrigger;
    private boolean longPressed;
    /**
     * Swipe handling, @see SwipeListener
     */

    private int lastOffset;

    public BucketItemCell(View view) {
        super(view);
        swipeVelocityTrigger = ViewConfiguration.get(view.getContext()).getScaledMinimumFlingVelocity();
    }

    @Override
    protected void syncUIStateWithModel() {
        tvName.setText(getModelObject().getName() + " id =" + getModelObject().getId());

        renderAction(ACTION_SETTLING);
        render();

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

    @OnTouch(R.id.swipeLayout)
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if ((mDragStateFlags & RecyclerViewDragDropManager.STATE_FLAG_DRAGGING) == 0)
                    longPressed = false;
                break;
        }
        return swipeLayout.onTouchEvent(event);
    }

    @OnLongClick(R.id.swipeLayout)
    boolean onLongClick(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        longPressed = true;
        return false;
    }

    private void render() {
        if (getModelObject().isDone()) {
            tvName.setTextColor(context.getResources().getColor(R.color.bucket_text_done));
            crossing.setVisibility(View.VISIBLE);
            buttonCancel.setImageResource(0);
        } else {
            tvName.setTextColor(context.getResources().getColor(R.color.bucket_text_to_do));
            crossing.setVisibility(View.INVISIBLE);
            buttonCancel.setImageResource(R.drawable.ic_keyboard_arrow_right);
        }
    }

    @Override
    public void prepareForReuse() {
        //set show mode.
        swipeLayout.close(false, false);

        //set drag edge.
        swipeLayout.removeSwipeListener(this);
        swipeLayout.addSwipeListener(this);
    }

    public boolean isLongPressed() {
        return longPressed;
    }

    @Override
    public int getDragStateFlags() {
        return mDragStateFlags;
    }

    /**
     * Drag handling
     *
     * @see DraggableItemViewHolder
     */

    @Override
    public void setDragStateFlags(int flags) {
        mDragStateFlags = flags;
    }

    public View getContainerView() {
        return swipeLayout;
    }

    public View getDraggableView() {
        return container;
    }

    @Override
    public void onStartOpen(SwipeLayout swipeLayout) {
        afterSwipe = true;
    }

    @Override
    public void onOpen(SwipeLayout swipeLayout) {
    }

    ///////////////////////////////////////////////////////////////////////////
    // Swipe action handling
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onStartClose(SwipeLayout swipeLayout) {
        renderAction(ACTION_SETTLING);
    }

    @Override
    public void onClose(SwipeLayout swipeLayout) {
    }

    @Override
    public void onUpdate(SwipeLayout swipeLayout, int leftOffset, int topOffset) {
        if (!afterSwipe) return;
        lastOffset = leftOffset;
        renderAction(getAction(leftOffset, 0));
    }

    @Override
    public void onHandRelease(SwipeLayout swipeLayout, float xvel, float yVel) {
        if (!afterSwipe) return;
        afterSwipe = false;

        int action = getAction(lastOffset, xvel);
        renderAction(action);
        int closeDelay = 0;
        if (isFling(xvel)) closeDelay = 250; // due to swipe layout inner invalidations
        itemView.postDelayed(() -> swipeLayout.close(true, false), closeDelay);
        itemView.postDelayed(() -> processAction(action), 300);
    }

    private void renderAction(@SwipeAction int action) {
        switch (action) {
            case ACTION_DEL:
                imageViewStatusDone.setVisibility(View.INVISIBLE);
                imageViewStatusClose.setVisibility(View.VISIBLE);
                bottomContainer.setBackgroundColor(context.getResources().getColor(R.color.bucket_red));
                break;
            case ACTION_DONE:
                imageViewStatusDone.setVisibility(View.VISIBLE);
                imageViewStatusClose.setVisibility(View.INVISIBLE);
                bottomContainer.setBackgroundColor(context.getResources().getColor(R.color.bucket_green));
                break;
            case ACTION_NONE:
                imageViewStatusDone.setVisibility(View.VISIBLE);
                imageViewStatusClose.setVisibility(View.INVISIBLE);
                bottomContainer.setBackgroundColor(context.getResources().getColor(R.color.bucket_green));
                break;
            case ACTION_SETTLING:
                imageViewStatusDone.setVisibility(View.INVISIBLE);
                imageViewStatusClose.setVisibility(View.INVISIBLE);
                bottomContainer.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
                break;
        }
    }

    private void processAction(@SwipeAction int action) {
        switch (action) {
            case ACTION_DEL:
                Timber.d("Sending delete event");
                getEventBus().post(new DeleteBucketItemEvent(getModelObject(), getPosition()));
                break;
            case ACTION_DONE:
                getModelObject().setDone(!getModelObject().isDone());
                render();
                Timber.d("Sending done event");
                getEventBus().post(new MarkBucketItemDoneEvent(getModelObject(), getPosition()));
                break;
        }
    }

    @SwipeAction
    private int getAction(int offset, float velocity) {
        if (isFling(velocity)) return ACTION_DONE;

        if (offset > swipeLayout.getWidth() * 2 / 3.f) return ACTION_DEL;
        else if (offset > swipeLayout.getWidth() / 3.f) return ACTION_DONE;
        else return ACTION_NONE;
    }

    private boolean isFling(float velocity) {
        return dpFromPx(context, velocity) > swipeVelocityTrigger;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Additional helpers
    ///////////////////////////////////////////////////////////////////////////

    @IntDef({ACTION_DEL, ACTION_DONE, ACTION_SETTLING, ACTION_NONE})
    @Retention(RetentionPolicy.SOURCE)
    @interface SwipeAction {
    }

}