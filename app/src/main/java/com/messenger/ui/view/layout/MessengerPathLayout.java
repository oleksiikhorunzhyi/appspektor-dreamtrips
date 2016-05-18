package com.messenger.ui.view.layout;

import android.content.Context;
import android.util.AttributeSet;

import com.messenger.flow.path.PathView;
import com.messenger.flow.path.StyledPath;
import com.messenger.ui.presenter.MessengerPresenter;
import com.worldventures.dreamtrips.core.utils.tracksystem.MonitoringHelper;

import flow.path.Path;

public abstract class MessengerPathLayout<V extends MessengerScreen, P extends MessengerPresenter<V, ?>, T extends StyledPath>
        extends MessengerInjectingLayout<V, P> implements PathView<T> {

    private Path path;

    public MessengerPathLayout(Context context) {
        super(context);
    }

    public MessengerPathLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        MonitoringHelper.startInteractionName(this);
    }

    @Override
    public void setPath(Path path) {
        this.path = path;
        onPrepared();
    }

    /** Safe method to init UI with path provided */
    protected void onPrepared() {
    }

    @Override
    public T getPath() {
        return (T) path;
    }
}
