package com.worldventures.dreamtrips.modules.feed.view.util;

import com.worldventures.dreamtrips.modules.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.model.video.Video;

import java.util.List;

public class FeedAspectRatioHelper {

   public List correctAspectRatio(List items, double minAspectRatio) {
      for (Object item : items) {
         if (item instanceof PostFeedItem) {
            TextualPost post = ((PostFeedItem) item).getItem();
            if (hasVideoAttachments(post)) {
               Video video = (Video) post.getAttachments().get(0);
               if (video.getAspectRatio() < minAspectRatio) {
                  video.setAspectRatio(minAspectRatio);
               }
            }
         }
      }
      return items;
   }

   private boolean hasVideoAttachments(TextualPost post) {
      return post.getAttachments() != null && post.getAttachments().size() > 0
            && post.getAttachments().get(0) instanceof Video;
   }
}
