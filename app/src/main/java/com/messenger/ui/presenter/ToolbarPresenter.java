package com.messenger.ui.presenter;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;

import com.worldventures.dreamtrips.R;

public class ToolbarPresenter {

    private Toolbar toolbar;
    private AppCompatActivity activity;

    public ToolbarPresenter(Toolbar toolbar, Context context) {
        this.toolbar = toolbar;
        if (! (context instanceof AppCompatActivity)) {
            throw new IllegalArgumentException("Context should be instance of AppCompatActivity");
        }
        this.activity = (AppCompatActivity) context;

        toolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.theme_main));
        toolbar.setNavigationOnClickListener(view -> activity.onBackPressed());
        toolbar.setTitleTextAppearance(activity, R.style.ActionBarTitle);
        toolbar.setSubtitleTextAppearance(activity, R.style.ActionBarSubtitle);
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

    public void enableUpNavigationButton() {
        TypedValue typedValue = new TypedValue();
        activity.getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.homeAsUpIndicator,
                typedValue, true);
        toolbar.setNavigationIcon(typedValue.resourceId);
    }
}
