package com.worldventures.dreamtrips.modules.feed.event;

public class EditCommentCloseRequest {

   String fragmentClazz;

   public EditCommentCloseRequest(String fragmentClazz) {
      this.fragmentClazz = fragmentClazz;
   }

   public String getFragmentClazz() {
      return fragmentClazz;
   }
}
