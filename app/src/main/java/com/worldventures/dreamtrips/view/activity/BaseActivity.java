package com.worldventures.dreamtrips.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.nispok.snackbar.Snackbar;
import com.worldventures.dreamtrips.DTApplication;
import com.worldventures.dreamtrips.core.module.DTModule;
import com.worldventures.dreamtrips.core.module.DomainModule;
import com.worldventures.dreamtrips.utils.Logs;
import com.worldventures.dreamtrips.view.presentation.BaseActivityPresentation;
import com.worldventures.dreamtrips.view.presentation.IInformView;

import org.robobinding.ViewBinder;
import org.robobinding.binder.BinderFactory;
import org.robobinding.binder.BinderFactoryBuilder;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

public abstract class BaseActivity extends ActionBarActivity implements Injector, IInformView {


    private ObjectGraph activityGraph;
    private BinderFactory binderFactory;
    private BaseActivityPresentation baseActivityPresentation;
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = (session, state, exception) -> {
       // onSessionStateChange(session, state, exception);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityGraph = ObjectGraph.create(getModules().toArray());
        baseActivityPresentation = new BaseActivityPresentation(this, this);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
        AppEventsLogger.activateApp(this);//facebook SDK event logger. Really needed?
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

    public void inject(Object ob) {
        activityGraph.inject(ob);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityGraph = null;
    }

    public void initializeContentView(int layoutId, Object presentationModel) {
        ViewBinder viewBinder = createViewBinder();
        View rootView = viewBinder.inflateAndBind(layoutId, presentationModel);
        setContentView(rootView);
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
        return Arrays.<Object>asList(new DomainModule(this), new DTModule(DTApplication.get(this)));
    }

    public void informUser(String st) {
        Logs.d("InformUser", st);
        Snackbar.with(getApplicationContext()).text(st).show(this);
    }

    public void handleError(Exception e) {
        baseActivityPresentation.handleError(e);
    }
}

