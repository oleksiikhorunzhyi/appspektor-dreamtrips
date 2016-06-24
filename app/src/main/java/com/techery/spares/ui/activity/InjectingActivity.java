package com.techery.spares.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.module.InjectingActivityModule;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.Global;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.ObjectGraph;
import de.greenrobot.event.EventBus;

public abstract class InjectingActivity extends AppCompatActivity implements Injector {

    private ObjectGraph objectGraph;

    @Inject
    @Global
    protected EventBus eventBus;

    @Override
    public ObjectGraph getObjectGraph() {
        if (objectGraph == null) {
            setupObjectGraph();
        }

        return objectGraph;
    }

    @Override
    public void inject(Object target) {
        getObjectGraph().inject(target);
    }

    protected void afterInject() {
    }

    protected void setupObjectGraph() {
        objectGraph = getApplicationInjector().getObjectGraph().plus(getModules().toArray());
    }

    private Injector getApplicationInjector() {
        return (Injector) getApplication();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupObjectGraph();
        inject(this);
        afterInject();

        beforeCreateView(savedInstanceState);

        setupLayout();
        ButterKnife.inject(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        afterCreateView(savedInstanceState);
    }

    public void setupLayout() {
        Layout layout = this.getClass().getAnnotation(Layout.class);
        if (layout != null) {
            setContentView(layout.value());
        }
    }


    protected void beforeCreateView(Bundle savedInstanceState) {
        //nothing to here
    }

    protected void afterCreateView(Bundle savedInstanceState) {
        //nothing to here
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            this.eventBus.registerSticky(this);
        } catch (Exception e) {
            //Ignored
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (eventBus.isRegistered(this)) {
            this.eventBus.unregister(this);
        }
    }

    @Override
    protected void onDestroy() {
        this.objectGraph = null;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuResource menuResource = this.getClass().getAnnotation(MenuResource.class);
        if (menuResource != null) {
            getMenuInflater().inflate(menuResource.value(), menu);
            return true;
        } else {
            return super.onCreateOptionsMenu(menu);
        }
    }

    protected List<Object> getModules() {
        List<Object> result = new ArrayList<Object>();
        result.add(new InjectingActivityModule(this, this));
        return result;
    }
}
