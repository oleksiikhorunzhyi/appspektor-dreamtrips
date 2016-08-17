package com.worldventures.dreamtrips.modules.feed.model.comment;

public class LoadMore {

   private boolean loading;
   private boolean visible;

   public boolean isLoading() {
      return loading;
   }

   public boolean isVisible() {
      return visible;
   }

   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   public void setLoading(boolean loading) {
      this.loading = loading;
   }
}
