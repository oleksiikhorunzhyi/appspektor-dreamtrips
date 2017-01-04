package com.worldventures.dreamtrips.modules.bucketlist.view.cell;

import android.content.Context;
import android.support.annotation.IntDef;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.common.view.adapter.DraggableArrayListAdapter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

import static com.worldventures.dreamtrips.core.utils.ViewUtils.dpFromPx;

@Layout(R.layout.adapter_item_bucket_cell)
public class BucketItemCell extends DraggableArrayListAdapter.DraggableCell<BucketItem, BucketItemCell.Delegate> implements SwipeLayout.SwipeListener {

   private static final int ACTION_DONE = 1;
   private static final int ACTION_NONE = -1;
   private static final int ACTION_SETTLING = -2;

   @InjectView(R.id.container_main) protected RelativeLayout container;
   @InjectView(R.id.textViewName) protected TextView tvName;
   @InjectView(R.id.swipeLayout) protected SwipeLayout swipeLayout;
   @InjectView(R.id.imageViewStatusDone) protected ImageView imageViewStatusDone;
   @InjectView(R.id.imageViewStatusClose) protected ImageView imageViewStatusClose;

   @Inject protected Context context;

   private int mDragStateFlags;
   private boolean afterSwipe = false;
   private int swipeVelocityTrigger;

   private int lastOffset;

   public BucketItemCell(View view) {
      super(view);
      swipeVelocityTrigger = ViewConfiguration.get(view.getContext()).getScaledMinimumFlingVelocity();
   }

   @Override
   public void prepareForReuse() {
      swipeLayout.close(false, false);
      swipeLayout.removeSwipeListener(this);
      swipeLayout.addSwipeListener(this);
   }

   @Override
   protected void syncUIStateWithModel() {
      tvName.setText(getModelObject().getName());

      renderAction(ACTION_SETTLING);
      renderData();

      int dragState = getDragStateFlags();
      if (getModelObject().isDone()) {
         container.setBackgroundResource(R.drawable.bucket_item_selector);
      } else if ((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_UPDATED) != 0) {
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

      container.setActivated(getModelObject().isSelected());

      afterSwipe = true;
   }

   private void renderData() {
      if (getModelObject().isDone()) {
         tvName.setTextColor(context.getResources().getColor(R.color.grey));
      } else {
         tvName.setTextColor(context.getResources().getColor(R.color.black));
      }
   }

   ///////////////////////////////////////////////////////////////////////////
   // Interaction
   ///////////////////////////////////////////////////////////////////////////

   @OnClick(R.id.swipeLayout)
   void onItemClicked() {
      if (!isSwiping()) cellDelegate.onCellClicked(getModelObject());
   }

   ///////////////////////////////////////////////////////////////////////////
   // Dragging
   ///////////////////////////////////////////////////////////////////////////

   private boolean isDragging() {
      return (mDragStateFlags & RecyclerViewDragDropManager.STATE_FLAG_DRAGGING) != 0 || (mDragStateFlags & RecyclerViewDragDropManager.STATE_FLAG_IS_ACTIVE) != 0;
   }

   @Override
   public boolean onCheckCanStartDrag(int position, int x, int y) {
      return !(getModelObject().isDone() || isSwiping());
   }

   @Override
   public int getDragStateFlags() {
      return mDragStateFlags;
   }

   @Override
   public void setDragStateFlags(int flags) {
      mDragStateFlags = flags;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Swiping
   ///////////////////////////////////////////////////////////////////////////

   private boolean isSwiping() {
      return swipeLayout.getOpenStatus() != SwipeLayout.Status.Close;
   }

   @Override
   public void onStartOpen(SwipeLayout swipeLayout) {
      renderData();
      afterSwipe = true;
      renderAction(ACTION_NONE);
   }

   @Override
   public void onOpen(SwipeLayout swipeLayout) {
      //do nothing
   }

   @Override
   public void onStartClose(SwipeLayout swipeLayout) {
      renderAction(ACTION_SETTLING);
   }

   @Override
   public void onClose(SwipeLayout swipeLayout) {
      //do nothing
   }

   @Override
   public void onUpdate(SwipeLayout swipeLayout, int leftOffset, int topOffset) {
      if (afterSwipe) {
         lastOffset = leftOffset;
      }

      renderAction(getAction(lastOffset, 0));
   }

   @Override
   public void onHandRelease(SwipeLayout swipeLayout, float xvel, float yVel) {
      if (!afterSwipe) {
         return;
      }

      afterSwipe = false;

      int action = getAction(lastOffset, xvel);
      renderAction(action);
      int closeDelay = 0;
      if (isFling(xvel)) {
         closeDelay = 250;
      }
      itemView.postDelayed(() -> swipeLayout.close(true, false), closeDelay);
      itemView.postDelayed(() -> processAction(action), 300);
   }

   private void renderAction(@SwipeAction int action) {
      switch (action) {
         case ACTION_DONE:
         case ACTION_NONE:
            imageViewStatusDone.setVisibility(View.VISIBLE);
            imageViewStatusClose.setVisibility(View.INVISIBLE);
            swipeLayout.setBackgroundColor(context.getResources().getColor(R.color.bucket_green));
            break;
         case ACTION_SETTLING:
            imageViewStatusDone.setVisibility(View.INVISIBLE);
            imageViewStatusClose.setVisibility(View.INVISIBLE);
            swipeLayout.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            break;
      }
   }

   private void processAction(@SwipeAction int action) {
      if (action == ACTION_DONE) {
         renderData();
         cellDelegate.onDoneClicked(getModelObject(), getAdapterPosition());
      }
      lastOffset = 0;
   }

   @SwipeAction
   private int getAction(int offset, float velocity) {
      if (isFling(velocity) || offset > swipeLayout.getWidth() / 3.f) {
         return ACTION_DONE;
      }
      return ACTION_NONE;
   }

   private boolean isFling(float velocity) {
      return dpFromPx(context, velocity) > swipeVelocityTrigger;
   }

   @IntDef({ACTION_DONE, ACTION_SETTLING, ACTION_NONE})
   @Retention(RetentionPolicy.SOURCE)
   @interface SwipeAction {}

   public interface Delegate extends CellDelegate<BucketItem> {
      abstract void onDoneClicked(BucketItem bucketItem, int position);
   }
}