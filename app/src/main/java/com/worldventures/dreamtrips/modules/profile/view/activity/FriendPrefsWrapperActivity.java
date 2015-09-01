package com.worldventures.dreamtrips.modules.profile.view.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.activity.ActivityWithPresenter;
import com.worldventures.dreamtrips.modules.profile.presenter.FriendPrefsWrapperPresenter;

import butterknife.InjectView;

@Layout(R.layout.activity_friend_pref)
public class FriendPrefsWrapperActivity extends ActivityWithPresenter<FriendPrefsWrapperPresenter> implements Presenter.View {

    public static final String BUNDLE_FRIEND = "BUNDLE_FRIEND";

    @InjectView(R.id.toolbar_actionbar)
    protected Toolbar toolbar;

    @Override
    protected void onResume() {
        super.onResume();
        if (toolbar != null) toolbar.getBackground().setAlpha(255);
    }

    @Override
    protected FriendPrefsWrapperPresenter createPresentationModel(Bundle savedInstanceState) {
        return new FriendPrefsWrapperPresenter((getIntent()
                .getBundleExtra(ActivityRouter.EXTRA_BUNDLE).getParcelable(BUNDLE_FRIEND)));
    }

    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.friend_pref_lists_header);
    }

}
