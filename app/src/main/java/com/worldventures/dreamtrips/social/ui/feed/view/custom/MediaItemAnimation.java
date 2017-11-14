package com.worldventures.dreamtrips.social.ui.feed.view.custom;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.functions.Action1;

public class MediaItemAnimation extends SimpleItemAnimator {

   private static final float INITIAL_ANIMATION_SCALE = 0.4f;
   private static final long ADD_ANIMATION_DURATION = 1000L;

   private ArrayList<ViewHolder> mPendingRemovals = new ArrayList<ViewHolder>();
   private ArrayList<ViewHolder> mPendingAdditions = new ArrayList<ViewHolder>();
   private ArrayList<MoveInfo> mPendingMoves = new ArrayList<MoveInfo>();
   private ArrayList<ChangeInfo> mPendingChanges = new ArrayList<ChangeInfo>();


   private ArrayList<ArrayList<ViewHolder>> mAdditionsList = new ArrayList<ArrayList<ViewHolder>>();
   private ArrayList<ArrayList<MoveInfo>> mMovesList = new ArrayList<ArrayList<MoveInfo>>();
   private ArrayList<ArrayList<ChangeInfo>> mChangesList = new ArrayList<ArrayList<ChangeInfo>>();


   private ArrayList<ViewHolder> mAddAnimations = new ArrayList<ViewHolder>();
   private ArrayList<ViewHolder> mMoveAnimations = new ArrayList<ViewHolder>();
   private ArrayList<ViewHolder> mRemoveAnimations = new ArrayList<ViewHolder>();
   private ArrayList<ViewHolder> mChangeAnimations = new ArrayList<ViewHolder>();

   private Action1<Integer> scrollAction;
   private float notifyIfViewHigher;

   volatile Map<ViewHolder, ChangeInfo> notYetAnimated = new HashMap<>();

   private final static class MoveInfo {
      public ViewHolder holder;
      public int fromX, fromY, toX, toY;

      private MoveInfo(ViewHolder holder, int fromX, int fromY, int toX, int toY) {
         this.holder = holder;
         this.fromX = fromX;
         this.fromY = fromY;
         this.toX = toX;
         this.toY = toY;
      }
   }

   private final static class ChangeInfo {
      public ViewHolder oldHolder, newHolder;
      public int fromX, fromY, toX, toY;

      private ChangeInfo(ViewHolder oldHolder, ViewHolder newHolder) {
         this.oldHolder = oldHolder;
         this.newHolder = newHolder;
      }

      private ChangeInfo(ViewHolder oldHolder, ViewHolder newHolder,
            int fromX, int fromY, int toX, int toY) {
         this(oldHolder, newHolder);
         this.fromX = fromX;
         this.fromY = fromY;
         this.toX = toX;
         this.toY = toY;
      }
   }

   public MediaItemAnimation(Action1<Integer> scrollAction, float notifyIfViewHigher) {
      this.scrollAction = scrollAction;
      this.notifyIfViewHigher = notifyIfViewHigher;
      setAddDuration(ADD_ANIMATION_DURATION);
   }

   @Override
   public void runPendingAnimations() {
      boolean removalsPending = !mPendingRemovals.isEmpty();
      boolean movesPending = !mPendingMoves.isEmpty();
      boolean changesPending = !mPendingChanges.isEmpty();
      boolean additionsPending = !mPendingAdditions.isEmpty();
      if (!removalsPending && !movesPending && !additionsPending && !changesPending) {
         // nothing to animate
         return;
      }
      // First, remove stuff
      for (ViewHolder holder : mPendingRemovals) {
         animateRemoveImpl(holder);
      }
      mPendingRemovals.clear();
      // Next, move stuff
      if (movesPending) {
         final ArrayList<MoveInfo> moves = new ArrayList<MoveInfo>();
         moves.addAll(mPendingMoves);
         mMovesList.add(moves);
         mPendingMoves.clear();
         Runnable mover = new Runnable() {
            @Override
            public void run() {
               for (MoveInfo moveInfo : moves) {
                  animateMoveImpl(moveInfo.holder, moveInfo.fromX, moveInfo.fromY,
                        moveInfo.toX, moveInfo.toY);
               }
               moves.clear();
               mMovesList.remove(moves);
            }
         };
         if (removalsPending) {
            View view = moves.get(0).holder.itemView;
            ViewCompat.postOnAnimationDelayed(view, mover, getRemoveDuration());
         } else {
            mover.run();
         }
      }
      // Next, change stuff, to run in parallel with move animations
      if (changesPending) {
         final ArrayList<ChangeInfo> changes = new ArrayList<ChangeInfo>();
         changes.addAll(mPendingChanges);
         mChangesList.add(changes);
         mPendingChanges.clear();
         Runnable changer = new Runnable() {
            @Override
            public void run() {
               for (ChangeInfo change : changes) {
                  animateChangeImpl(change);
               }
               changes.clear();
               mChangesList.remove(changes);
            }
         };
         if (removalsPending) {
            ViewHolder holder = changes.get(0).oldHolder;
            ViewCompat.postOnAnimationDelayed(holder.itemView, changer, getRemoveDuration());
         } else {
            changer.run();
         }
      }
      // Next, add stuff
      if (additionsPending) {
         final ArrayList<ViewHolder> additions = new ArrayList<ViewHolder>();
         additions.addAll(mPendingAdditions);
         mAdditionsList.add(additions);
         mPendingAdditions.clear();
         Runnable adder = new Runnable() {
            public void run() {
               for (ViewHolder holder : additions) {
                  animateAddImpl(holder);
               }
               additions.clear();
               mAdditionsList.remove(additions);
            }
         };
         if (removalsPending || changesPending) {
            long removeDuration = removalsPending ? getRemoveDuration() : 0;
            long moveDuration = movesPending ? getMoveDuration() : 0;
            long changeDuration = changesPending ? getChangeDuration() : 0;
            long totalDelay = removeDuration + Math.max(moveDuration, changeDuration);
            View view = additions.get(0).itemView;
            ViewCompat.postOnAnimationDelayed(view, adder, totalDelay);
         } else {
            adder.run();
         }
      }
   }

   @Override
   public boolean animateRemove(final ViewHolder holder) {
      endAnimation(holder);
      mPendingRemovals.add(holder);
      return true;
   }

   private void animateRemoveImpl(final ViewHolder holder) {
      final View view = holder.itemView;
      final ViewPropertyAnimatorCompat animation = ViewCompat.animate(view);
      animation.setDuration(getRemoveDuration())
            .alpha(0).setListener(new VpaListenerAdapter() {
         @Override
         public void onAnimationStart(View view) {
            dispatchRemoveStarting(holder);
         }

         @Override
         public void onAnimationEnd(View view) {
            animation.setListener(null);
            ViewCompat.setAlpha(view, 1);
            dispatchRemoveFinished(holder);
            mRemoveAnimations.remove(holder);
            dispatchFinishedWhenDone();
         }
      }).start();
      mRemoveAnimations.add(holder);
   }

   @Override
   public boolean animateAdd(final ViewHolder holder) {
      endAnimation(holder);
      ViewCompat.setAlpha(holder.itemView, 0f);
      mPendingAdditions.add(holder);
      return true;
   }

   private void animateAddImpl(final ViewHolder holder) {
      final View view = holder.itemView;
      mAddAnimations.add(holder);

      // Set the pivot to width so that the element scale from left
      // Also changed the animation to scaleX
      ViewCompat.setScaleY(view, INITIAL_ANIMATION_SCALE);
      ViewCompat.setScaleX(view, INITIAL_ANIMATION_SCALE);
      ViewCompat.setAlpha(view, 0f);

      final ViewPropertyAnimatorCompat animation = ViewCompat.animate(view);
      animation.scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setDuration(getAddDuration()).
            setListener(new VpaListenerAdapter() {
               @Override
               public void onAnimationStart(View view) {
                  dispatchAddStarting(holder);
                  animateScroll(holder);
               }

               @Override
               public void onAnimationEnd(View view) {
                  ViewCompat.setScaleY(view, 1f);
                  ViewCompat.setScaleX(view, 1f);
                  ViewCompat.setAlpha(view, 1f);

                  animation.setListener(null);
                  dispatchAddFinished(holder);
                  mAddAnimations.remove(holder);
                  dispatchFinishedWhenDone();

                  if (notYetAnimated.containsKey(holder)) {
                     animateChangeImpl(notYetAnimated.remove(holder));
                  }
               }
            }).start();
   }

   private void animateScroll(ViewHolder viewHolder) {
      viewHolder.itemView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
         @Override
         public boolean onPreDraw() {
            if (viewHolder.itemView.getHeight() > notifyIfViewHigher) {
               viewHolder.itemView.getViewTreeObserver().removeOnPreDrawListener(this);
               scrollAction.call(viewHolder.getAdapterPosition());
            }
            return true;
         }
      });
   }

   @Override
   public boolean animateMove(final ViewHolder holder, int fromX, int fromY,
         int toX, int toY) {
      final View view = holder.itemView;
      fromX += ViewCompat.getTranslationX(holder.itemView);
      fromY += ViewCompat.getTranslationY(holder.itemView);
      endAnimation(holder);
      int deltaX = toX - fromX;
      int deltaY = toY - fromY;
      if (deltaX == 0 && deltaY == 0) {
         dispatchMoveFinished(holder);
         return false;
      }
      if (deltaX != 0) {
         ViewCompat.setTranslationX(view, -deltaX);
      }
      if (deltaY != 0) {
         ViewCompat.setTranslationY(view, -deltaY);
      }
      mPendingMoves.add(new MoveInfo(holder, fromX, fromY, toX, toY));
      return true;
   }

   private void animateMoveImpl(final ViewHolder holder, int fromX, int fromY, int toX, int toY) {
      final View view = holder.itemView;
      final int deltaX = toX - fromX;
      final int deltaY = toY - fromY;
      if (deltaX != 0) {
         ViewCompat.animate(view).translationX(0);
      }
      if (deltaY != 0) {
         ViewCompat.animate(view).translationY(0);
      }
      // TODO: make EndActions end listeners instead, since end actions aren't called when
      // vpas are canceled (and can't end them. why?)
      // need listener functionality in VPACompat for this. Ick.
      mMoveAnimations.add(holder);
      final ViewPropertyAnimatorCompat animation = ViewCompat.animate(view);
      animation.setDuration(getMoveDuration()).setListener(new VpaListenerAdapter() {
         @Override
         public void onAnimationStart(View view) {
            dispatchMoveStarting(holder);
         }

         @Override
         public void onAnimationCancel(View view) {
            if (deltaX != 0) {
               ViewCompat.setTranslationX(view, 0);
            }
            if (deltaY != 0) {
               ViewCompat.setTranslationY(view, 0);
            }
         }

         @Override
         public void onAnimationEnd(View view) {
            animation.setListener(null);
            dispatchMoveFinished(holder);
            mMoveAnimations.remove(holder);
            dispatchFinishedWhenDone();
         }
      }).start();
   }

   @Override
   public boolean animateChange(ViewHolder oldHolder, ViewHolder newHolder,
         int fromX, int fromY, int toX, int toY) {
      final float prevTranslationX = ViewCompat.getTranslationX(oldHolder.itemView);
      final float prevTranslationY = ViewCompat.getTranslationY(oldHolder.itemView);
      final float prevAlpha = ViewCompat.getAlpha(oldHolder.itemView);
      if (!mAddAnimations.contains(oldHolder)) {
         endAnimation(oldHolder);
      }
      int deltaX = (int) (toX - fromX - prevTranslationX);
      int deltaY = (int) (toY - fromY - prevTranslationY);
      // recover prev translation state after ending animation
      ViewCompat.setTranslationX(oldHolder.itemView, prevTranslationX);
      ViewCompat.setTranslationY(oldHolder.itemView, prevTranslationY);
      ViewCompat.setAlpha(oldHolder.itemView, prevAlpha);
      if (newHolder != null && newHolder.itemView != null) {
         // carry over translation values
         if (!mAddAnimations.contains(newHolder)) {
            endAnimation(newHolder);
         }
         ViewCompat.setTranslationX(newHolder.itemView, -deltaX);
         ViewCompat.setTranslationY(newHolder.itemView, -deltaY);
         ViewCompat.setAlpha(newHolder.itemView, 0);
      }
      mPendingChanges.add(new ChangeInfo(oldHolder, newHolder, fromX, fromY, toX, toY));
      return false;
   }

   private void animateChangeImpl(final ChangeInfo changeInfo) {
      final ViewHolder holder = changeInfo.oldHolder;
      final View view = holder == null ? null : holder.itemView;
      final ViewHolder newHolder = changeInfo.newHolder;
      final View newView = newHolder != null ? newHolder.itemView : null;

      if (mAddAnimations.contains(holder)) {
         notYetAnimated.put(holder, changeInfo);
         return;
      }

      if (view != null) {
         mChangeAnimations.add(changeInfo.oldHolder);
         final ViewPropertyAnimatorCompat oldViewAnim = ViewCompat.animate(view).setDuration(getChangeDuration());
         oldViewAnim.translationX(changeInfo.toX - changeInfo.fromX);
         oldViewAnim.translationY(changeInfo.toY - changeInfo.fromY);

         oldViewAnim.alpha(0).setListener(new VpaListenerAdapter() {
            @Override
            public void onAnimationStart(View view) {
               dispatchChangeStarting(changeInfo.oldHolder, true);
            }

            @Override
            public void onAnimationEnd(View view) {
               oldViewAnim.setListener(null);
               ViewCompat.setAlpha(view, 1);
               ViewCompat.setTranslationX(view, 0);
               ViewCompat.setTranslationY(view, 0);
               dispatchChangeFinished(changeInfo.oldHolder, true);
               mChangeAnimations.remove(changeInfo.oldHolder);
               dispatchFinishedWhenDone();
            }
         }).start();
      }
      if (newView != null) {
         mChangeAnimations.add(changeInfo.newHolder);
         final ViewPropertyAnimatorCompat newViewAnimation = ViewCompat.animate(newView);

         ViewCompat.setAlpha(newView, 1f);
         ViewCompat.setTranslationX(newView, 0);
         ViewCompat.setTranslationY(newView, 0);

         newViewAnimation.scaleX(1.0f)
               .scaleY(1.0f)
               .setDuration(getChangeDuration())
               .setListener(new VpaListenerAdapter() {
                  @Override
                  public void onAnimationStart(View view) {
                     dispatchChangeStarting(changeInfo.newHolder, false);
                  }

                  @Override
                  public void onAnimationEnd(View view) {
                     newViewAnimation.setListener(null);
                     ViewCompat.setAlpha(newView, 1);
                     ViewCompat.setTranslationX(newView, 0);
                     ViewCompat.setTranslationY(newView, 0);
                     dispatchChangeFinished(changeInfo.newHolder, false);
                     mChangeAnimations.remove(changeInfo.newHolder);
                     dispatchFinishedWhenDone();
                  }
               })
               .start();
      }
   }

   private void endChangeAnimation(List<ChangeInfo> infoList, ViewHolder item) {
      for (int i = infoList.size() - 1; i >= 0; i--) {
         ChangeInfo changeInfo = infoList.get(i);
         if (endChangeAnimationIfNecessary(changeInfo, item)
               && changeInfo.oldHolder == null && changeInfo.newHolder == null) {
            infoList.remove(changeInfo);
         }
      }
   }

   private void endChangeAnimationIfNecessary(ChangeInfo changeInfo) {
      if (changeInfo.oldHolder != null) {
         endChangeAnimationIfNecessary(changeInfo, changeInfo.oldHolder);
      }
      if (changeInfo.newHolder != null) {
         endChangeAnimationIfNecessary(changeInfo, changeInfo.newHolder);
      }
   }

   private boolean endChangeAnimationIfNecessary(ChangeInfo changeInfo, ViewHolder item) {
      boolean oldItem = false;
      if (changeInfo.newHolder == item) {
         changeInfo.newHolder = null;
      } else if (changeInfo.oldHolder == item) {
         changeInfo.oldHolder = null;
         oldItem = true;
      } else {
         return false;
      }
      ViewCompat.setAlpha(item.itemView, 1);
      ViewCompat.setTranslationX(item.itemView, 0);
      ViewCompat.setTranslationY(item.itemView, 0);
      dispatchChangeFinished(item, oldItem);
      return true;
   }

   @Override
   public void endAnimation(ViewHolder item) {
      final View view = item.itemView;
      // this will trigger end callback which should set properties to their target values.
      ViewCompat.animate(view).cancel();
      // TODO if some other animations are chained to end, how do we cancel them as well?
      for (int i = mPendingMoves.size() - 1; i >= 0; i--) {
         MoveInfo moveInfo = mPendingMoves.get(i);
         if (moveInfo.holder == item) {
            ViewCompat.setTranslationY(view, 0);
            ViewCompat.setTranslationX(view, 0);
            dispatchMoveFinished(item);
            mPendingMoves.remove(item);
         }
      }
      endChangeAnimation(mPendingChanges, item);
      if (mPendingRemovals.remove(item)) {
         ViewCompat.setAlpha(view, 1);
         dispatchRemoveFinished(item);
      }
      if (mPendingAdditions.remove(item)) {
         ViewCompat.setScaleY(view, 1f);
         ViewCompat.setScaleX(view, 1f);
         dispatchAddFinished(item);
      }
      for (int i = mChangesList.size() - 1; i >= 0; i--) {
         ArrayList<ChangeInfo> changes = mChangesList.get(i);
         endChangeAnimation(changes, item);
         if (changes.isEmpty()) {
            mChangesList.remove(changes);
         }
      }
      for (int i = mMovesList.size() - 1; i >= 0; i--) {
         ArrayList<MoveInfo> moves = mMovesList.get(i);
         for (int j = moves.size() - 1; j >= 0; j--) {
            MoveInfo moveInfo = moves.get(j);
            if (moveInfo.holder == item) {
               ViewCompat.setTranslationY(view, 0);
               ViewCompat.setTranslationX(view, 0);
               dispatchMoveFinished(item);
               moves.remove(j);
               if (moves.isEmpty()) {
                  mMovesList.remove(moves);
               }
               break;
            }
         }
      }
      for (int i = mAdditionsList.size() - 1; i >= 0; i--) {
         ArrayList<ViewHolder> additions = mAdditionsList.get(i);
         if (additions.remove(item)) {
            ViewCompat.setScaleY(view, 1f);
            ViewCompat.setScaleX(view, 1f);
            dispatchAddFinished(item);
            if (additions.isEmpty()) {
               mAdditionsList.remove(additions);
            }
         }
      }
      dispatchFinishedWhenDone();
   }

   @Override
   public boolean isRunning() {
      return (!mPendingAdditions.isEmpty()
            || !mPendingChanges.isEmpty()
            || !mPendingMoves.isEmpty()
            || !mPendingRemovals.isEmpty()
            || !mMoveAnimations.isEmpty()
            || !mRemoveAnimations.isEmpty()
            || !mAddAnimations.isEmpty()
            || !mChangeAnimations.isEmpty()
            || !mMovesList.isEmpty()
            || !mAdditionsList.isEmpty()
            || !mChangesList.isEmpty());
   }

   private void dispatchFinishedWhenDone() {
      if (!isRunning()) {
         dispatchAnimationsFinished();
      }
   }

   @Override
   public void endAnimations() {
      int count = mPendingMoves.size();
      for (int i = count - 1; i >= 0; i--) {
         MoveInfo item = mPendingMoves.get(i);
         View view = item.holder.itemView;
         ViewCompat.setTranslationY(view, 0);
         ViewCompat.setTranslationX(view, 0);
         dispatchMoveFinished(item.holder);
         mPendingMoves.remove(i);
      }
      count = mPendingRemovals.size();
      for (int i = count - 1; i >= 0; i--) {
         ViewHolder item = mPendingRemovals.get(i);
         dispatchRemoveFinished(item);
         mPendingRemovals.remove(i);
      }
      count = mPendingAdditions.size();
      for (int i = count - 1; i >= 0; i--) {
         ViewHolder item = mPendingAdditions.get(i);
         View view = item.itemView;
         ViewCompat.setScaleY(view, 1f);
         ViewCompat.setScaleX(view, 1f);
         dispatchAddFinished(item);
         mPendingAdditions.remove(i);
      }
      count = mPendingChanges.size();
      for (int i = count - 1; i >= 0; i--) {
         endChangeAnimationIfNecessary(mPendingChanges.get(i));
      }
      mPendingChanges.clear();
      if (!isRunning()) {
         return;
      }
      int listCount = mMovesList.size();
      for (int i = listCount - 1; i >= 0; i--) {
         ArrayList<MoveInfo> moves = mMovesList.get(i);
         count = moves.size();
         for (int j = count - 1; j >= 0; j--) {
            MoveInfo moveInfo = moves.get(j);
            ViewHolder item = moveInfo.holder;
            View view = item.itemView;
            ViewCompat.setTranslationY(view, 0);
            ViewCompat.setTranslationX(view, 0);
            dispatchMoveFinished(moveInfo.holder);
            moves.remove(j);
            if (moves.isEmpty()) {
               mMovesList.remove(moves);
            }
         }
      }
      listCount = mAdditionsList.size();
      for (int i = listCount - 1; i >= 0; i--) {
         ArrayList<ViewHolder> additions = mAdditionsList.get(i);
         count = additions.size();
         for (int j = count - 1; j >= 0; j--) {
            ViewHolder item = additions.get(j);
            View view = item.itemView;
            ViewCompat.setScaleY(view, 1f);
            ViewCompat.setScaleX(view, 1f);
            dispatchAddFinished(item);
            additions.remove(j);
            if (additions.isEmpty()) {
               mAdditionsList.remove(additions);
            }
         }
      }
      listCount = mChangesList.size();
      for (int i = listCount - 1; i >= 0; i--) {
         ArrayList<ChangeInfo> changes = mChangesList.get(i);
         count = changes.size();
         for (int j = count - 1; j >= 0; j--) {
            endChangeAnimationIfNecessary(changes.get(j));
            if (changes.isEmpty()) {
               mChangesList.remove(changes);
            }
         }
      }
      cancelAll(mRemoveAnimations);
      cancelAll(mMoveAnimations);
      cancelAll(mAddAnimations);
      cancelAll(mChangeAnimations);
      dispatchAnimationsFinished();
   }

   void cancelAll(List<ViewHolder> viewHolders) {
      for (int i = viewHolders.size() - 1; i >= 0; i--) {
         ViewCompat.animate(viewHolders.get(i).itemView).cancel();
      }
   }

   private static class VpaListenerAdapter implements ViewPropertyAnimatorListener {
      @Override
      public void onAnimationStart(View view) {
         //do nothing
      }

      @Override
      public void onAnimationEnd(View view) {
         //do nothing
      }

      @Override
      public void onAnimationCancel(View view) {
         //do nothing
      }
   }


   private static final int ANIM_DURATION = 500;

   @Override
   public long getAddDuration() {
      return ANIM_DURATION;
   }

   @Override
   public long getRemoveDuration() {
      return ANIM_DURATION;
   }

   @Override
   public long getChangeDuration() {
      return 0;
   }

   @Override
   public long getMoveDuration() {
      return ANIM_DURATION;
   }

}
