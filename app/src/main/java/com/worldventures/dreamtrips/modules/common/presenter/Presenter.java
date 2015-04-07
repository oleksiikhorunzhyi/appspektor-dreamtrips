package com.worldventures.dreamtrips.modules.common.presenter;

import android.content.Context;

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

    public Presenter(VT view) {
        this.view = view;
    }

    public void init() {
        try {
            eventBus.registerSticky(this);
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

    public interface View {
        void informUser(int stringId);

        void informUser(String string);

        void alert(String s);

        boolean isTabletLandscape();
    }
}
