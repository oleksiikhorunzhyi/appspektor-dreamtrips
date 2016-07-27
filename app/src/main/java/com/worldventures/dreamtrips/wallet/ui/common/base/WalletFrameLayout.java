package com.worldventures.dreamtrips.wallet.ui.common.base;

import android.content.Context;
import android.util.AttributeSet;

import com.messenger.ui.presenter.ViewStateMvpPresenter;
import com.messenger.ui.view.layout.BaseViewStateFrameLayout;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.flow.layout.InjectorHolder;
import com.worldventures.dreamtrips.core.flow.path.PathView;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;

import butterknife.ButterKnife;
import flow.path.Path;

public abstract class WalletFrameLayout<V extends WalletScreen, P extends ViewStateMvpPresenter<V, ?>, T extends StyledPath>
        extends BaseViewStateFrameLayout<V, P> implements InjectorHolder, PathView<T> {
    private Injector injector;

    public WalletFrameLayout(Context context) {
        super(context);
    }

    public WalletFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Deprecated
    @Override
    public void setPath(T path) {
    }

    @Override
    public T getPath() {
        return Path.get(getContext());
    }

    @Override
    public void setInjector(Injector injector) {
        this.injector = injector;
    }

    public Injector getInjector() {
        return injector;
    }
}
