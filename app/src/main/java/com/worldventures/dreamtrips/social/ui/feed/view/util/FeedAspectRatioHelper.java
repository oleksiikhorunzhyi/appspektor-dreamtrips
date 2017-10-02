package com.worldventures.dreamtrips.social.ui.feed.view.util;

import com.worldventures.dreamtrips.social.ui.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;
import com.worldventures.dreamtrips.social.ui.feed.model.VideoFeedItem;

import java.util.List;

public class FeedAspectRatioHelper {

   public List correctAspectRatio(List items, double minAspectRatio) {
      for (Object item : items) {
         if (item instanceof PostFeedItem) {
            TextualPost post = ((PostFeedItem) item).getItem();
            if (hasVideoAttachments(post)) {
               VideoFeedItem video = (VideoFeedItem) post.getAttachments().get(0);
               if (video.getItem().getAspectRatio() < minAspectRatio) {
                  video.getItem().setAspectRatio(minAspectRatio);
               }
            }
         }
      }
      return items;
   }

   private boolean hasVideoAttachments(TextualPost post) {
      return post.getAttachments() != null && post.getAttachments().size() > 0
            && post.getAttachments().get(0) instanceof VideoFeedItem;
   }
}
