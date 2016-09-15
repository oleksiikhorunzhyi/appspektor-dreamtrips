package com.techery.spares.adapter.expandable;

import android.support.annotation.CallSuper;
import android.view.View;

import com.techery.spares.ui.view.cell.AbstractCell;

import java.util.List;

import butterknife.ButterKnife;

public abstract class GroupCell<G, C> extends AbstractCell<G> implements View.OnAttachStateChangeListener, View.OnClickListener {

   private boolean expanded;
   private boolean enabled;
   private GroupExpandListener groupExpandListener;

   public GroupCell(View view) {
      super(view);
      setEnabled(true);
      view.addOnAttachStateChangeListener(this);
   }

   public void setExpanded(boolean expanded) {
      this.expanded = expanded;
   }

   public boolean isExpanded() {
      return expanded;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public boolean isEnabled() {
      return enabled;
   }

   public void setGroupExpandListener(GroupExpandListener groupExpandListener) {
      this.groupExpandListener = groupExpandListener;
   }

   protected void attachActionsToView(View.OnClickListener listener) {
      if (getClickableItemViewId() > 0)
         setupAction(ButterKnife.findById(itemView, getClickableItemViewId()), ACTION.CLICK, listener);
      if (getExpandItemViewId() > 0)
         setupAction(ButterKnife.findById(itemView, getExpandItemViewId()), ACTION.TOGGLE, listener);
      else setupAction(itemView, ACTION.TOGGLE, listener);
   }


   protected int getClickableItemViewId() {
      return 0;
   }

   protected int getExpandItemViewId() {
      return 0;
   }

   @Override
   public void onViewAttachedToWindow(View v) {
      attachActionsToView(this);
   }

   @Override
   public void onViewDetachedFromWindow(View v) {
      attachActionsToView(null);
   }

   protected boolean shouldReactWhenClick(View v) {
      return true;
   }

   public abstract List<C> getChildListCell();

   @Override
   public void onClick(View v) {
      if (!shouldReactWhenClick(v) || !isEnabled()) return;

      final ACTION action = (ACTION) v.getTag();
      if (action == ACTION.TOGGLE) toggleView();
      this.onAction(action);
   }

   private void setupAction(View view, ACTION action, View.OnClickListener listener) {
      final ACTION tag = listener != null ? action : null;
      view.setTag(tag);
      view.setOnClickListener(listener);
   }

   private void toggleView() {
      if (isExpanded()) collapseView();
      else expandView();
   }

   protected void onAction(ACTION action) {
   }

   /**
    * Triggers expansion of the parent.
    */
   @CallSuper
   protected void expandView() {
      setExpanded(true);

      if (groupExpandListener != null) {
         groupExpandListener.onGroupExpanded(getAdapterPosition());
      }
   }

   /**
    * Triggers collapse of the parent.
    */
   @CallSuper
   protected void collapseView() {
      setExpanded(false);

      if (groupExpandListener != null) {
         groupExpandListener.onGroupCollapsed(getAdapterPosition());
      }
   }


   public interface GroupExpandListener {

      /**
       * Called when a list item is expanded.
       *
       * @param position The index of the item in the list being expanded
       */
      void onGroupExpanded(int position);

      /**
       * Called when a list item is collapsed.
       *
       * @param position The index of the item in the list being collapsed
       */
      void onGroupCollapsed(int position);
   }

   public enum ACTION {
      CLICK, TOGGLE
   }
}
