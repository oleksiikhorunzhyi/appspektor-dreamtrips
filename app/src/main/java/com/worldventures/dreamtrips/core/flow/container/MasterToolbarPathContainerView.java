package com.worldventures.dreamtrips.core.flow.container;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl_flow.view.SuperMasterPath;

import flow.Flow;
import flow.path.Path;
import flow.path.PathContextFactory;

public class MasterToolbarPathContainerView extends FramePathContainerView {

    public MasterToolbarPathContainerView(Context context, AttributeSet attrs) {
        super(context, attrs, new SuperMasterPathContainer(context, R.id.screen_switcher_tag,
                Path.contextFactory()));
    }

    @Override
    public void dispatch(Flow.Traversal traversal, Flow.TraversalCallback callback) {
        // Short circuit if the new screen's supermaster is the same as current.
        Path currentSuperMaster = ((SuperMasterPath) traversal.origin.top()).getSuperMasterPath();
        Path newSuperMaster = ((SuperMasterPath) traversal.destination.top()).getSuperMasterPath();

        if (getCurrentChild() != null && newSuperMaster.equals(currentSuperMaster)) {
            callback.onTraversalCompleted();
        } else {
            super.dispatch(traversal, () -> callback.onTraversalCompleted());
        }
    }

    static class SuperMasterPathContainer extends SimplePathContainer {

        SuperMasterPathContainer(Context context, int tagKey, PathContextFactory contextFactory) {
            super(context, tagKey, contextFactory);
        }

        @Override
        protected int getLayout(Path path) {
            SuperMasterPath mdPath = (SuperMasterPath) path;
            return super.getLayout(mdPath.getSuperMasterPath());
        }

        @Override
        protected void performTraversal(ViewGroup containerView, TraversalState traversalState,
                                        Flow.Direction direction, Flow.TraversalCallback callback) {
            super.performTraversal(containerView, traversalState, direction, callback);
        }
    }
}
