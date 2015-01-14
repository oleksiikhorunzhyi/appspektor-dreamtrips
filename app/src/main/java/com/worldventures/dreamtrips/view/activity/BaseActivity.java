package com.worldventures.dreamtrips.view.activity;

import android.os.Bundle;
import android.view.View;

import com.nispok.snackbar.Snackbar;
import com.techery.spares.module.Injector;
import com.techery.spares.ui.activity.InjectingActivity;
import com.worldventures.dreamtrips.core.module.DomainModule;
import com.worldventures.dreamtrips.view.presentation.BaseActivityPresentation;
import com.worldventures.dreamtrips.view.presentation.IInformView;

import org.robobinding.ViewBinder;
import org.robobinding.binder.BinderFactory;
import org.robobinding.binder.BinderFactoryBuilder;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public abstract class BaseActivity extends InjectingActivity implements Injector, IInformView {

    private BinderFactory binderFactory;
    private BaseActivityPresentation baseActivityPresentation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivityPresentation = new BaseActivityPresentation(this,this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        List<Object> result = new ArrayList<Object>();
        result.add(new DomainModule(this));
        return result;
    }

    public void informUser(String st) {
        Timber.d("InformUser", st);
        Snackbar.with(getApplicationContext()).text(st).show(this);
    }

    public void handleError(Exception e) {
        baseActivityPresentation.handleError(e);
    }
}

