package com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter;


import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

   private OnItemClickListener mListener;
   private RecyclerView mRecyclerView;

   public interface OnItemClickListener {
      void onItemClick(View view, int position);

      void onItemLongClick(View childView, int position, Point point);
   }

   GestureDetector mGestureDetector;

   public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
      mListener = listener;
      mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
         @Override
         public boolean onSingleTapUp(MotionEvent e) {

            return true;
         }

         @Override
         public void onLongPress(MotionEvent e) {
            View childView = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            mListener.onItemLongClick(childView, mRecyclerView.getChildAdapterPosition(childView), new Point((int) e.getX(), (int) e
                  .getY()));

         }
      });
   }

   @Override
   public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
      this.mRecyclerView = view;
      View childView = view.findChildViewUnder(e.getX(), e.getY());
      if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
         mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
         return true;
      }
      return false;
   }

   @Override
   public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
   }

   @Override
   public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

   }
}
