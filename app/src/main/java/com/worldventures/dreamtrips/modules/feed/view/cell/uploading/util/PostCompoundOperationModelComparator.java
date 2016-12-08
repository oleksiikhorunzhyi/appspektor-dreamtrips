package com.worldventures.dreamtrips.modules.feed.view.cell.uploading.util;

import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationState;

import java.util.Comparator;

/*
 * Sorts CompoundOperationModelComparator in descending order depending on state and
 * the place in the queue
 */
public class PostCompoundOperationModelComparator implements Comparator<CompoundOperationModel> {

   @Override
   public int compare(CompoundOperationModel o1, CompoundOperationModel o2) {
      int displayPriorityDiff = getDisplayPriorityByState(o2.state()) -
            getDisplayPriorityByState(o1.state());
      if (displayPriorityDiff == 0 && o1.state().equals(o2.state())) {
         return o2.position() - o1.position();
      }
      return displayPriorityDiff;
   }

   private int getDisplayPriorityByState(CompoundOperationState state) {
      switch (state) {
         case FAILED:
            return 0;
         case CANCELED:
            return 1;
         case PAUSED:
            return 2;
         case SCHEDULED:
            return 3;
         case STARTED:
         case FINISHED:
            return 4;
         default:
            return 0;
      }
   }
}
