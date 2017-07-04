package com.worldventures.dreamtrips.modules.feed.view.util;

import android.support.v7.widget.RecyclerView;
import android.view.ViewTreeObserver;

import com.worldventures.dreamtrips.modules.feed.service.FeedListWidthInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.FeedListWidthCommand;

/**
 * Right after orientation change getWidth() on both recycle view and item views returns not correct value.
 * Use this workaround class to eventually supply correct list width to cells.
 */
public class FeedWidthOrientationHelper {

   private FeedListWidthInteractor feedListWidthInteractor;
   private RecyclerView recyclerView;

   public FeedWidthOrientationHelper(FeedListWidthInteractor feedListWidthInteractor, RecyclerView recyclerView) {
      this.feedListWidthInteractor = feedListWidthInteractor;
      this.recyclerView = recyclerView;
   }

   private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
      private int previousWidth;

      @Override
      public void onGlobalLayout() {
         int width = recyclerView.getMeasuredWidth();
         if (previousWidth == 0 || previousWidth != width) {
            previousWidth = width;
            feedListWidthInteractor.feedListWidthPipe().send(new FeedListWidthCommand(width));
         }
      }
   };

   public void startReportingListWidth() {
      recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
   }
}
