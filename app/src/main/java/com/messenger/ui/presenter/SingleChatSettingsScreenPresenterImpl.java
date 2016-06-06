package com.messenger.ui.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.messenger.delegate.ProfileCrosser;
import com.messenger.ui.view.settings.ChatSettingsScreen;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;

import static com.worldventures.dreamtrips.core.module.RouteCreatorModule.PROFILE;

public class SingleChatSettingsScreenPresenterImpl extends ChatSettingsScreenPresenterImpl<ChatSettingsScreen> {

    @Inject
    @Named(PROFILE)
    RouteCreator<Integer> routeCreator;

    @Inject
    ProfileCrosser profileCrosser;

    public SingleChatSettingsScreenPresenterImpl(Context context, Injector injector, String conversationId) {
        super(context, injector, conversationId);
    }

    @Override
    public void onConversationAvatarClick() {
        participantsObservable
                .flatMap(Observable::from)
                .filter(participant -> !TextUtils.equals(currentUser.getId(), participant.getId()))
                .take(1)
                .subscribe(profileCrosser::crossToProfile);
    }
}
