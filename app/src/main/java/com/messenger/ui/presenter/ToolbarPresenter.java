package com.messenger.ui.presenter;

import android.app.Activity;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.worldventures.dreamtrips.R;

public class ToolbarPresenter {

    private ActionBar actionBar;
    private Toolbar toolbar;

    public ToolbarPresenter(Toolbar toolbar, AppCompatActivity appCompatActivity) {
        this.toolbar = toolbar;
        appCompatActivity.setSupportActionBar(toolbar);
        actionBar = appCompatActivity.getSupportActionBar();
        toolbar.setBackgroundColor(appCompatActivity.getResources().getColor(R.color.theme_main));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                // TODO handle nicely this
                if (toolbar.getContext() instanceof Activity) {
                    ((Activity) toolbar.getContext()).finish();
                }
            }
        });
        toolbar.setTitleTextAppearance(appCompatActivity, R.style.ActionBarTitle);
        toolbar.setSubtitleTextAppearance(appCompatActivity, R.style.ActionBarSubtitle);
    }

    public ActionBar getActionBar() {
        return actionBar;
    }

    public void setTitle(@StringRes int name) {
        getActionBar().setTitle(name);
    }

    public void setTitle(String name) {
        getActionBar().setTitle(name);
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
