package com.worldventures.dreamtrips.view.activity;

import android.content.Intent;
import android.os.Bundle;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.nispok.snackbar.Snackbar;
import com.techery.spares.ui.activity.InjectingActivity;
import com.worldventures.dreamtrips.core.SessionManager;
import com.worldventures.dreamtrips.core.module.ActivityModule;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.view.presentation.BaseActivityPresentation;
import com.worldventures.dreamtrips.view.presentation.IInformView;

import org.robobinding.ViewBinder;
import org.robobinding.binder.BinderFactory;
import org.robobinding.binder.BinderFactoryBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public abstract class BaseActivity extends InjectingActivity implements IInformView {

    private BinderFactory binderFactory;
    private BaseActivityPresentation baseActivityPresentation;
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = (session, state, exception) -> {
        // onSessionStateChange(session, state, exception);
    };

    @Inject
    ActivityRouter router;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivityPresentation = new BaseActivityPresentation(this,this);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
    }


    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
        AppEventsLogger.activateApp(this); //facebook SDK event logger. Really needed?
    }

    @Override
    protected void onPause() {
        super.onPause();
        uiHelper.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    public ViewBinder createViewBinder() {
        BinderFactory binderFactory = getReusableBinderFactory();
        return binderFactory.createViewBinder(this);
    }

    private BinderFactory getReusableBinderFactory() {
        if (binderFactory == null) {
            binderFactory = new BinderFactoryBuilder().build();
        }
        return binderFactory;
    }

    @Override
    public void onBackPressed() {
        baseActivityPresentation.pop();
    }

    protected List<Object> getModules() {
        List<Object> result = new ArrayList<Object>();
        result.add(new ActivityModule(this));
        return result;
    }

    public void informUser(String st) {
        Snackbar.with(getApplicationContext()).text(st).show(this);
    }

    public void handleError(Exception e) {
        baseActivityPresentation.handleError(e);
    }

    public void onEvent(SessionManager.LogoutEvent logoutEvent) {
        this.router.finish();
        this.router.openLogin();
    }
}

