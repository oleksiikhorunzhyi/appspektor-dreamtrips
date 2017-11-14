package com.worldventures.dreamtrips.social.ui.feed.model.util;

public class FeedListWidth {
   private int widthInPortrait;
   private int widthInLandscape;

   public int getWidthInPortrait() {
      return widthInPortrait;
   }

   public void setWidthInPortrait(int widthInPortrait) {
      this.widthInPortrait = widthInPortrait;
   }

   public int getWidthInLandscape() {
      return widthInLandscape;
   }

   public void setWidthInLandscape(int widthInLandscape) {
      this.widthInLandscape = widthInLandscape;
   }

   public static FeedListWidth forPortrait(int widthInPortrait) {
      FeedListWidth feedListWidth = new FeedListWidth();
      feedListWidth.setWidthInPortrait(widthInPortrait);
      return feedListWidth;
   }

   public static FeedListWidth forLandscape(int widthInLandscape) {
      FeedListWidth feedListWidth = new FeedListWidth();
      feedListWidth.setWidthInLandscape(widthInLandscape);
      return feedListWidth;
   }
}
