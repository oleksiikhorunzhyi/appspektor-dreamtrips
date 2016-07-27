package com.worldventures.dreamtrips.wallet.ui.common.base;

import android.content.Context;
import android.support.annotation.MenuRes;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.messenger.ui.presenter.ViewStateMvpPresenter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;

import butterknife.InjectView;

public abstract class BaseToolbarScreenLayout<V extends WalletScreen, P extends ViewStateMvpPresenter<V, ?>, T extends StyledPath> extends WalletFrameLayout<V, P, T> {
    @InjectView(R.id.toolbar) Toolbar toolbar;

    public BaseToolbarScreenLayout(Context context) {
        super(context);
    }

    public BaseToolbarScreenLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @MenuRes
    protected int getMenuRes() {
        return 0;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initToolbar(toolbar);
    }

    protected void initToolbar(Toolbar toolbar) {
        int menu = getMenuRes();
        if (menu != 0) toolbar.inflateMenu(menu);
        onPrepareOptionMenu(toolbar.getMenu());
        toolbar.setOnMenuItemClickListener(this::onMenuItemSelected);
        toolbar.setNavigationOnClickListener(this::onNavigateButtonClick);
    }

    protected boolean onMenuItemSelected(MenuItem item) {
        return false;
    }

    protected void onNavigateButtonClick(View view) {
    }

    protected void onPrepareOptionMenu(Menu menu) {
    }

    protected Toolbar getToolbar() {
        return toolbar;
    }
}
