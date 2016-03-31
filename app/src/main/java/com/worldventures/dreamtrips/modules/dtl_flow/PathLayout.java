package com.worldventures.dreamtrips.modules.dtl_flow;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.core.flow.path.PathView;

import butterknife.ButterKnife;
import flow.path.Path;

public abstract class PathLayout<V extends FlowScreen, P extends FlowPresenter<V, ?>, T extends StyledPath>
        extends InjectingLayout<V, P> implements PathView<T> {

    private Path path;

    public PathLayout(Context context) {
        super(context);
    }

    public PathLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @CallSuper
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
    }

    protected void onPrepared() {
        // safe method to init UI with path provided
    }

    @Override
    public void setPath(Path path) {
        this.path = path;
        onPrepared();
    }

    @Override
    public T getPath() {
        return (T) this.path;
    }
}
