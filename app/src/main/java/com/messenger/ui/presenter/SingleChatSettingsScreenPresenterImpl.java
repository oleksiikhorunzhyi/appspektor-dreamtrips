package com.messenger.ui.presenter;


import android.content.Context;
import android.text.TextUtils;

import com.messenger.delegate.ProfileCrosser;
import com.messenger.ui.view.settings.ChatSettingsScreen;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;

import static com.worldventures.dreamtrips.core.module.RouteCreatorModule.PROFILE;

public class SingleChatSettingsScreenPresenterImpl extends ChatSettingsScreenPresenterImpl<ChatSettingsScreen> {

    @Inject
    @Named(PROFILE)
    RouteCreator<Integer> routeCreator;

    protected final ProfileCrosser profileCrosser;

    public SingleChatSettingsScreenPresenterImpl(Context context, String conversationId) {
        super(context, conversationId);

        profileCrosser = new ProfileCrosser(context, routeCreator);
    }

    @Override
    public void onConversationAvatarClick() {
        participantsObservable
                .flatMap(users -> Observable.from(users))
                .filter(participant -> !TextUtils.equals(user.getId(), participant.getId()))
                .first()
                .subscribe(user ->  profileCrosser.crossToProfile(user));
    }
}
