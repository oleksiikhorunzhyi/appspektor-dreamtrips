package com.worldventures.dreamtrips.modules.dtl_flow.parts.review;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum PostReviewError {

   PROFANITY("ERROR_FORM_PROFANITY"),
   UNKNOWN("ERROR_UNKNOWN"),
   REQUESTS_LIMIT("ERROR_REQUEST_LIMIT_REACHED"),
   DUPLICATED("ERROR_DUPLICATED_REVIEW"),
   UNRECOGNIZED("");

   private final String error;

   PostReviewError(String error) {
      this.error = error;
   }

   public String error() {
      return error;
   }

   public static PostReviewError of(@Nullable String error) {
      if (error == null) {
         return UNRECOGNIZED;
      }

      for (PostReviewError name : values()) {
         if (String.format(name.error(), Locale.ENGLISH).equalsIgnoreCase(error)) {
            return name;
         }
      }
      return UNRECOGNIZED;
   }
}
