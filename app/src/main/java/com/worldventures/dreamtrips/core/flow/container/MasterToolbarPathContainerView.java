package com.worldventures.dreamtrips.core.flow.container;

import android.content.Context;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl_flow.view.MasterToolbarPath;

import flow.Flow;
import flow.path.Path;
import flow.path.PathContextFactory;

public class MasterToolbarPathContainerView extends FramePathContainerView {

   public MasterToolbarPathContainerView(Context context, AttributeSet attrs) {
      super(context, attrs, new MasterToolbarPathContainer(context, R.id.screen_switcher_tag, Path.contextFactory()));
   }

   @Override
   public void dispatch(Flow.Traversal traversal, Flow.TraversalCallback callback) {
      // Short circuit if the new screen's supermaster is the same as current.
      Path currentMasterToolbar = ((MasterToolbarPath) traversal.origin.top()).getMasterToolbarPath();
      Path newMasterToolbar = ((MasterToolbarPath) traversal.destination.top()).getMasterToolbarPath();

      if (getCurrentChild() != null && newMasterToolbar.equals(currentMasterToolbar)) {
         callback.onTraversalCompleted();
      } else {
         super.dispatch(traversal, () -> callback.onTraversalCompleted());
      }
   }

   static class MasterToolbarPathContainer extends SimplePathContainer {

      MasterToolbarPathContainer(Context context, int tagKey, PathContextFactory contextFactory) {
         super(context, tagKey, contextFactory);
      }

      @Override
      protected int getLayout(Path path) {
         MasterToolbarPath mdPath = (MasterToolbarPath) path;
         return super.getLayout(mdPath.getMasterToolbarPath());
      }
   }
}
