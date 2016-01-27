package com.messenger.ui.view.layout;

import android.content.Context;
import android.util.AttributeSet;

import com.messenger.flow.path.PathView;
import com.messenger.flow.path.StyledPath;
import com.messenger.ui.presenter.MessengerPresenter;

import flow.path.Path;

public abstract class MessengerPathLayout<V extends MessengerScreen, P extends MessengerPresenter<V, ?>, T extends StyledPath>
        extends MessengerLinearLayout<V, P> implements PathView<T> {

    private Path path;

    public MessengerPathLayout(Context context) {
        super(context);
    }

    public MessengerPathLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setPath(Path path) {
        this.path = path;
        onPrepared();
    }

    protected void onPrepared() {
        // safe method to init UI with path provided
    }

    @Override
    public T getPath() {
        return (T) path;
    }
}
