package com.messenger.ui.presenter;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.worldventures.dreamtrips.R;

public class ToolbarPresenter {

    private ActionBar actionBar;

    public ToolbarPresenter(Toolbar toolbar, AppCompatActivity appCompatActivity) {
        appCompatActivity.setSupportActionBar(toolbar);
        actionBar = appCompatActivity.getSupportActionBar();
        toolbar.setBackgroundColor(appCompatActivity.getResources().getColor(R.color.action_bar_background));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                // TODO handle nicely this
                ((Activity)toolbar.getContext()).finish();
            }
        });
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
}
