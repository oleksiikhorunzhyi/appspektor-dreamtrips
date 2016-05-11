package com.messenger.ui.presenter;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerPresenter;

import javax.inject.Inject;

import flow.Flow;

public class ToolbarPresenter {

    private Toolbar toolbar;
    private Context context;

    @Inject
    NavigationDrawerPresenter navigationDrawerPresenter;

    public ToolbarPresenter(Toolbar toolbar, Context context) {
        this.context = context;
        this.toolbar = toolbar;
    }

    private void initToolbar() {
        toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.theme_main));
        toolbar.setTitleTextAppearance(context, R.style.ActionBarTitle);
        toolbar.setSubtitleTextAppearance(context, R.style.ActionBarSubtitle);
    }

    public void inject(Injector injector) {
        injector.inject(this);
    }

    public void setTitle(@StringRes int name) {
        toolbar.setTitle(name);
    }

    public void setTitle(String name) {
        toolbar.setTitle(name);
    }

    public void disableTitle() {
        toolbar.setTitle("");
    }

    public void setSubtitle(@StringRes int name) {
        toolbar.setSubtitle(name);
    }

    public void setSubtitle(String name) {
        toolbar.setSubtitle(name);
    }

    public void attachPathAttrs(PathAttrs pathAttrs) {
        initToolbar();
        //
        if (pathAttrs.isDrawerEnabled()) enableDrawerNavigationButton();
        else enableUpNavigationButton();
    }

    public void hideBackButtonInLandscape() {
        if (ViewUtils.isLandscapeOrientation(context) && ViewUtils.isTablet(context)) {
            toolbar.setNavigationIcon(null);
            toolbar.setNavigationOnClickListener(null);
        }
    }

    private void enableUpNavigationButton() {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.homeAsUpIndicator,
                typedValue, true);
        toolbar.setNavigationIcon(typedValue.resourceId);
        toolbar.setNavigationOnClickListener(view -> Flow.get(context).goBack());
    }

    private void enableDrawerNavigationButton() {
        if (context.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
            toolbar.setNavigationIcon(R.drawable.ic_menu_hamburger);

         toolbar.setNavigationOnClickListener(view -> openDrawer());
    }

    private void openDrawer() {
        if (navigationDrawerPresenter != null) navigationDrawerPresenter.openDrawer();
    }

}
