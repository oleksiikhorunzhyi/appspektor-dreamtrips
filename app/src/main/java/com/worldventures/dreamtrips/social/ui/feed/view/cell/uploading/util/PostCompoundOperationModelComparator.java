package com.worldventures.dreamtrips.social.ui.feed.view.cell.uploading.util;

import com.worldventures.dreamtrips.social.ui.background_uploading.model.CompoundOperationState;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel;

import java.util.Comparator;

/*
 * Sorts CompoundOperationModelComparator in descending order depending on state and
 * the place in the queue
 */
public class PostCompoundOperationModelComparator implements Comparator<PostCompoundOperationModel> {

   @Override
   public int compare(PostCompoundOperationModel o1, PostCompoundOperationModel o2) {
      int displayPriorityDiff = getDisplayPriorityByState(o2.state()) -
            getDisplayPriorityByState(o1.state());
      if (displayPriorityDiff == 0 && o1.state().equals(o2.state())) {
         return o2.creationDate().compareTo(o1.creationDate());
      }
      return displayPriorityDiff;
   }

   private int getDisplayPriorityByState(CompoundOperationState state) {
      switch (state) {
         case FAILED:
            return 0;
         case PAUSED:
            return 1;
         case SCHEDULED:
            return 2;
         case STARTED:
         case FINISHED:
            return 3;
         default:
            return 0;
      }
   }
}
