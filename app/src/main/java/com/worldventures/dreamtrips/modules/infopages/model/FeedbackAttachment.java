package com.worldventures.dreamtrips.modules.infopages.model;

public interface FeedbackAttachment {

   enum Type {
      IMAGE
   }

   Type getType();

   String getUrl();
}
