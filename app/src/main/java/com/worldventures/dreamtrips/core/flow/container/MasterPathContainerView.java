package com.worldventures.dreamtrips.core.flow.container;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;

import flow.Flow;
import flow.path.Path;
import flow.path.PathContextFactory;

public class MasterPathContainerView extends FramePathContainerView {

   public MasterPathContainerView(Context context, AttributeSet attrs) {
      super(context, attrs, new MasterPathContainer(context, R.id.screen_switcher_tag, Path.contextFactory()));
   }

   @Override
   public void dispatch(Flow.Traversal traversal, final Flow.TraversalCallback callback) {

      MasterDetailPath currentMaster = ((MasterDetailPath) traversal.origin.top()).getMaster();
      MasterDetailPath newMaster = ((MasterDetailPath) traversal.destination.top()).getMaster();

      // Short circuit if the new screen has the same master.
      if (getCurrentChild() != null && newMaster.equals(currentMaster)) {
         callback.onTraversalCompleted();
      } else {
         super.dispatch(traversal, () -> callback.onTraversalCompleted());
      }
   }

   static class MasterPathContainer extends SimplePathContainer {

      MasterPathContainer(Context context, int tagKey, PathContextFactory contextFactory) {
         super(context, tagKey, contextFactory);
      }

      @Override
      protected int getLayout(Path path) {
         MasterDetailPath mdPath = (MasterDetailPath) path;
         return super.getLayout(mdPath.getMaster());
      }

      @Override
      protected void performTraversal(ViewGroup containerView, TraversalState traversalState, Flow.Direction direction, Flow.TraversalCallback callback) {
         super.performTraversal(containerView, traversalState, direction, callback);
      }
   }
}
