package com.worldventures.dreamtrips.modules.dtl_flow.parts.review.util;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ReviewSettings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CommentParams {

   private final int maxSize;
   private final int minSize;

   public static CommentParams from(@NotNull Merchant merchant) {
      final int minSize = merchant.reviews() != null ? getMinSizeFrom(merchant.reviews().reviewSettings()) : 0;
      final int maxSize = merchant.reviews() != null ? getMaxSizeFrom(merchant.reviews().reviewSettings()) : 0;
      return new CommentParams(maxSize, minSize);
   }

   public CommentParams(int maxSize, int minSize) {
      this.maxSize = maxSize;
      this.minSize = minSize;
   }

   public int maxSize() {
      return maxSize;
   }

   public int minSize() {
      return minSize;
   }

   private static int getMinSizeFrom(@Nullable ReviewSettings reviewSettings) {
      return reviewSettings != null ? Integer.parseInt(reviewSettings.minimumCharactersAllowed()) : 0;
   }

   private static int getMaxSizeFrom(@Nullable ReviewSettings reviewSettings) {
      return reviewSettings != null ? Integer.parseInt(reviewSettings.maximumCharactersAllowed()) : 0;
   }
}
