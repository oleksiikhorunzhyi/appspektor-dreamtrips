package com.worldventures.dreamtrips.core.flow.container;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.R;

import flow.Flow;
import flow.path.Path;
import flow.path.PathContainerView;

/**
 * This view is shown only in landscape orientation on tablets
 */
public class TabletMasterDetailRoot extends LinearLayout
        implements PathContainerView {
    private FramePathContainerView masterContainer;
    private FramePathContainerView detailContainer;

    private boolean disabled;

    public TabletMasterDetailRoot(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return !disabled && super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        masterContainer = (FramePathContainerView) findViewById(R.id.master_container);
        detailContainer = (FramePathContainerView) findViewById(R.id.details_container);
    }

    @Override
    public ViewGroup getCurrentChild() {
        MasterDetailPath showing = Path.get(getContext());
        return showing.isMaster() ? masterContainer.getCurrentChild()
                : detailContainer.getCurrentChild();
    }

    @Override
    public ViewGroup getContainerView() {
        return this;
    }

    @Override
    public void dispatch(final Flow.Traversal traversal, Flow.TraversalCallback callback) {

        class CountdownCallback implements Flow.TraversalCallback {
            final Flow.TraversalCallback wrapped;
            int countDown = 2;

            CountdownCallback(Flow.TraversalCallback wrapped) {
                this.wrapped = wrapped;
            }

            @Override
            public void onTraversalCompleted() {
                countDown--;
                if (countDown == 0) {
                    disabled = false;
                    wrapped.onTraversalCompleted();
                }
            }
        }

        disabled = true;
        callback = new CountdownCallback(callback);
        detailContainer.dispatch(traversal, callback);
        masterContainer.dispatch(traversal, callback);
    }

}