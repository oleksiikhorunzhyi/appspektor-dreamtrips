package com.worldventures.dreamtrips.modules.common.presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.techery.spares.module.Annotations.Global;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.modules.auth.session.UserSession;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class BasePresenter<VT extends BasePresenter.View> {

    protected final VT view;

    @Inject
    protected FragmentCompass fragmentCompass;

    @Inject
    protected Context context;

    @Inject
    protected ActivityRouter activityRouter;

    @Inject
    protected SessionHolder<UserSession> appSessionHolder;

    @Inject
    protected DreamSpiceManager dreamSpiceManager;

    @Inject
    @Global
    protected
    EventBus eventBus;

    public BasePresenter(VT view) {
        this.view = view;
    }

    public void init() {

    }

    public void destroyView() {
    }

    public void resume() {

    }

    public void handleError(Exception ex) {
        Log.e(this.getClass().getSimpleName(), "", ex);
    }

    public DreamSpiceManager getDreamSpiceManager() {
        return dreamSpiceManager;
    }

    public String getUserId() {
        return appSessionHolder.get().get().getUser().getEmail();
    }

    public boolean isConnected() {
        ConnectivityManager conMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        return i != null && i.isConnected() && i.isAvailable();
    }

    public interface View {
        void informUser(String stringId);

        void alert(String s);
    }
}
