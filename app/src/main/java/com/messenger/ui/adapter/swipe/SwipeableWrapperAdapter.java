package com.messenger.ui.adapter.swipe;

import android.support.v7.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl;
import com.daimajia.swipe.interfaces.SwipeAdapterInterface;
import com.daimajia.swipe.interfaces.SwipeItemMangerInterface;
import com.daimajia.swipe.util.Attributes;
import com.h6ah4i.android.widget.advrecyclerview.utils.BaseWrapperAdapter;

import java.util.List;

/*
 * Wrapper for providing swipe functionality.
 */
public class SwipeableWrapperAdapter<A extends RecyclerView.Adapter & SwipeLayoutContainer, VH extends RecyclerView.ViewHolder> extends BaseWrapperAdapter<VH> implements SwipeItemMangerInterface, SwipeAdapterInterface {

   private SwipeItemRecyclerMangerImpl swipeButtonsManger = new SwipeItemRecyclerMangerImpl(this);
   private SwipeLayoutContainer swipeLayoutContainer;

   public SwipeableWrapperAdapter(A adapter) {
      super(adapter);
      swipeLayoutContainer = adapter;
   }

   @Override
   public void onBindViewHolder(VH holder, int position) {
      super.onBindViewHolder(holder, position);
      if (getSwipeLayoutResourceId(position) > 0) {
         swipeButtonsManger.bindView(holder.itemView, position);
      }
   }

   @Override
   public int getSwipeLayoutResourceId(int position) {
      return swipeLayoutContainer.getSwipeLayoutResourceId(position);
   }

   @Override
   public void openItem(int position) {
      swipeButtonsManger.openItem(position);
   }

   @Override
   public void closeItem(int position) {
      swipeButtonsManger.closeItem(position);
   }

   @Override
   public void closeAllExcept(SwipeLayout layout) {
      swipeButtonsManger.closeAllExcept(layout);
   }

   @Override
   public void closeAllItems() {
      swipeButtonsManger.closeAllItems();
   }

   @Override
   public List<Integer> getOpenItems() {
      return swipeButtonsManger.getOpenItems();
   }

   @Override
   public List<SwipeLayout> getOpenLayouts() {
      return swipeButtonsManger.getOpenLayouts();
   }

   @Override
   public void removeShownLayouts(SwipeLayout layout) {
      swipeButtonsManger.removeShownLayouts(layout);
   }

   @Override
   public boolean isOpen(int position) {
      return swipeButtonsManger.isOpen(position);
   }

   @Override
   public Attributes.Mode getMode() {
      return swipeButtonsManger.getMode();
   }

   @Override
   public void setMode(Attributes.Mode mode) {
      swipeButtonsManger.setMode(mode);
   }
}
