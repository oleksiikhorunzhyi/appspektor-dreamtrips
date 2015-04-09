package com.worldventures.dreamtrips.modules.common.presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.apptentive.android.sdk.Log;
import com.techery.spares.module.Annotations.Global;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.session.UserSession;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class Presenter<VT extends Presenter.View> {

    protected final VT view;

    @Inject
    protected FragmentCompass fragmentCompass;

    @Inject
    protected ActivityRouter activityRouter;

    @Inject
    protected SessionHolder<UserSession> appSessionHolder;

    @Inject
    protected DreamSpiceManager dreamSpiceManager;

    @Inject
    @Global
    protected EventBus eventBus;

    @Inject
    protected Context context;

    protected int priorityEventBus = 0;

    public Presenter(VT view) {
        this.view = view;
    }

    public void init() {
        try {
            eventBus.registerSticky(this, priorityEventBus);
        } catch (Exception ignored) {
            //Ignored
            Log.e(this.getClass().getSimpleName(), "", ignored);

        }
    }

    public void destroyView() {
        try {
            eventBus.unregister(this);
        } catch (Exception ignored) {
            //Ignored
            Log.e(this.getClass().getSimpleName(), "", ignored);
        }
    }

    public void resume() {
        //nothing to do here
    }

    public DreamSpiceManager getDreamSpiceManager() {
        return dreamSpiceManager;
    }

    public String getUserId() {
        return appSessionHolder.get().get().getUser().getEmail();
    }

    public void onStop() {
        stopSpiceManager();
    }

    private void stopSpiceManager() {
        if (dreamSpiceManager.isStarted()) {
            dreamSpiceManager.shouldStop();
        }
    }

    public void onStart() {
        startSpiceManager();
    }

    private void startSpiceManager() {
        if (!dreamSpiceManager.isStarted()) {
            dreamSpiceManager.start(context);
        }
    }

    public boolean isConnected() {
        ConnectivityManager conMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        return i != null && i.isConnected() && i.isAvailable();
    }

    public interface View {
        void informUser(int stringId);

        void informUser(String string);

        void alert(String s);

        boolean isTabletLandscape();
    }
}
