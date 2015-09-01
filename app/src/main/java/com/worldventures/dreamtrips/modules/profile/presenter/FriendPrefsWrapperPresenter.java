package com.worldventures.dreamtrips.modules.profile.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.profile.view.fragment.FriendPreferenceFragment;

public class FriendPrefsWrapperPresenter extends Presenter<Presenter.View> {

    private final User friend;

    public FriendPrefsWrapperPresenter(User friend) {
        this.friend = friend;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(FriendPreferenceFragment.BUNDLE_FRIEND, friend);
            fragmentCompass.switchBranch(Route.FRIEND_PREFERENCES, bundle);
        }
    }
}

