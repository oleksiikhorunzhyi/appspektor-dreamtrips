package com.messenger.ui.presenter;


import android.app.Activity;
import android.content.Intent;

import com.messenger.delegate.ProfileCrosser;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;

import javax.inject.Inject;
import javax.inject.Named;

import static com.worldventures.dreamtrips.core.module.RouteCreatorModule.PROFILE;

public class SingleChatSettingsScreenPresenterImpl extends ChatSettingsScreenPresenterImpl {

    @Inject
    @Named(PROFILE)
    RouteCreator<Integer> routeCreator;

    protected final ProfileCrosser profileCrosser;

    public SingleChatSettingsScreenPresenterImpl(Activity activity, Intent startIntent) {
        super(activity, startIntent);

        profileCrosser = new ProfileCrosser(activity, routeCreator);
    }

    @Override
    public void onConversationAvatarClick() {
        profileCrosser.crossToProfile(participants.get(0));
    }
}
