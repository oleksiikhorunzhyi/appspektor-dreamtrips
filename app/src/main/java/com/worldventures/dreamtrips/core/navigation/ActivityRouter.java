package com.worldventures.dreamtrips.core.navigation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.techery.spares.ui.routing.ActivityBoundRouter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.ComponentActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.LaunchActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.Palyer360Activity;
import com.worldventures.dreamtrips.modules.common.view.activity.ShareFragment;
import com.worldventures.dreamtrips.modules.common.view.bundle.ShareBundle;
import com.worldventures.dreamtrips.modules.tripsimages.view.activity.CreatePhotoActivity;

public class ActivityRouter extends ActivityBoundRouter {

    public ActivityRouter(Activity activity) {
        super(activity);
    }

    public void openMain() {
        startActivity(MainActivity.class);
    }

    public void openLaunch() {
        startActivity(LaunchActivity.class);
    }

    public void openCreatePhoto(Fragment fm, Uri fileUri, String type) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(CreatePhotoActivity.EXTRA_FILE_URI, fileUri);
        bundle.putString(CreatePhotoActivity.EXTRA_TYPE, type);
        startForResult(fm, CreatePhotoActivity.class, CreatePhotoActivity.REQUEST_CODE_CREATE_PHOTO, bundle);
    }

    public void open360Activity(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(Palyer360Activity.EXTRA_URL, url);
        startActivity(Palyer360Activity.class, bundle);
    }

    public void openShareFacebook(String imageUrl, String shareLink, String text) {
        ShareBundle data = new ShareBundle();
        data.setImageUrl(imageUrl);
        data.setShareUrl(shareLink);
        data.setText(text);
        data.setShareType(ShareFragment.FB);
        NavigationBuilder.create().data(data).with(this).move(Route.SHARE);
    }

    public void openShareTwitter(String imageUrl, String shareLink, String text) {
        ShareBundle data = new ShareBundle();
        data.setImageUrl(imageUrl);
        data.setShareUrl(shareLink);
        data.setText(text == null ? "" : text);
        data.setShareType(ShareFragment.TW);
        NavigationBuilder.create().data(data).with(this).move(Route.SHARE);
    }

    public void openDefaultShareIntent(Intent intent) {
        startActivityIntent(Intent.createChooser(intent, getActivity().getString(R.string.action_share)));
    }

    public void openComponentActivity(@NonNull Route route, @NonNull Bundle args) {
        args.putSerializable(ComponentPresenter.ROUTE, route);
        startActivityWithArgs(ComponentActivity.class, args);
    }

    public void openComponentActivity(@NonNull Route route, @NonNull Bundle args, int flags) {
        args.putSerializable(ComponentPresenter.ROUTE, route);
        startActivityWithArgs(ComponentActivity.class, args, flags);
    }

    public void startService(Class clazz) {
        super.startService(clazz);
    }
}
