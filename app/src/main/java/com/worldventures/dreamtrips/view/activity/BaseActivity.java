package com.worldventures.dreamtrips.view.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import com.nispok.snackbar.Snackbar;
import com.worldventures.dreamtrips.DTApplication;
import com.worldventures.dreamtrips.core.module.DomainModule;
import com.worldventures.dreamtrips.core.module.DTModule;
import com.worldventures.dreamtrips.utils.Logs;

import org.robobinding.ViewBinder;
import org.robobinding.binder.BinderFactory;
import org.robobinding.binder.BinderFactoryBuilder;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

public abstract class BaseActivity extends ActionBarActivity implements Injector {


    private ObjectGraph activityGraph;
    private BinderFactory binderFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityGraph = ObjectGraph.create(getModules().toArray());
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
        try {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.getBackStackEntryCount() > 1) {
                Log.i(MainActivity.class.getSimpleName(), "popping backstack");
                super.onBackPressed();
            } else {
                Log.i(MainActivity.class.getSimpleName(), "calling super");
                finish();
            }
        } catch (IllegalStateException e) {
            Logs.e(BaseActivity.class.getSimpleName(), e); //for avoid application crash when called at runtime
        }
    }

    protected List<Object> getModules() {
        return Arrays.<Object>asList(new DomainModule(this), new DTModule(DTApplication.get(this)));
    }

    public void informUser(String st) {
        Logs.d("InformUser", st);
        Snackbar.with(getApplicationContext()).text(st).show(this);
    }
}

