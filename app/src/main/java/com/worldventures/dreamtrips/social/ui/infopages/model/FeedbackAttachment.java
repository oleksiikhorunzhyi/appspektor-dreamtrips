package com.worldventures.dreamtrips.social.ui.infopages.model;

public interface FeedbackAttachment {

   enum Type {
      IMAGE
   }

   Type getType();

   String getUrl();
}
