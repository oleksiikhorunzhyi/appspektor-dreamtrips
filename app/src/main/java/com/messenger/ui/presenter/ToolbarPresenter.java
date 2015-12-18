package com.messenger.ui.presenter;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.worldventures.dreamtrips.R;

public class ToolbarPresenter {

    private ActionBar actionBar;
    private Toolbar toolbar;

    public ToolbarPresenter(Toolbar toolbar, AppCompatActivity activity) {
        this.toolbar = toolbar;
        activity.setSupportActionBar(toolbar);
        actionBar = activity.getSupportActionBar();
        toolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.theme_main));
        toolbar.setNavigationOnClickListener(view -> {
            activity.onBackPressed();
        });
        toolbar.setTitleTextAppearance(activity, R.style.ActionBarTitle);
        toolbar.setSubtitleTextAppearance(activity, R.style.ActionBarSubtitle);
    }

    public ActionBar getActionBar() {
        return actionBar;
    }

    public void setTitle(@StringRes int name) {
        actionBar.setTitle(name);
    }

    public void setTitle(String name) {
        actionBar.setTitle(name);
    }

    public void disableTitle() {
        actionBar.setDisplayShowTitleEnabled(false);

    }

    public void setSubtitle(@StringRes int name) {
        getActionBar().setSubtitle(name);
    }

    public void setSubtitle(String name) {
        getActionBar().setSubtitle(name);
    }

    public void enableUpNavigationButton() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
    }

    public void enableUpNavigationButton(@DrawableRes int navigationIcon) {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(navigationIcon);
    }
}
