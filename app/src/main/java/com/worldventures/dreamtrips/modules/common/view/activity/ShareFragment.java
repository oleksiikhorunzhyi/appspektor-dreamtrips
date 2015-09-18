package com.worldventures.dreamtrips.modules.common.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.facebook.widget.WebDialog;
import com.techery.spares.annotations.Layout;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.SharePresenter;
import com.worldventures.dreamtrips.modules.common.view.bundle.ShareBundle;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.facebook.presenter.FacebookPickPhotoPresenter;

import butterknife.InjectView;

@Layout(R.layout.share_fragment)
public class ShareFragment extends BaseFragmentWithArgs<SharePresenter, ShareBundle>
        implements FacebookPickPhotoPresenter.View, SharePresenter.View {

    public static final String FB = "fb";
    public static final String TW = "tw";

    @InjectView(R.id.login_button)
    protected LoginButton loginButton;
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = (session, state, exception) -> {
        //nothing to do here
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        ShareBundle bundleExtra = getArgs();
        if (bundleExtra == null) {
            getActivity().finish();
        } else {
            String imageUrl = bundleExtra.getImageUrl();
            String shareUrl = bundleExtra.getShareUrl();
            String text = bundleExtra.getText();
            String type = bundleExtra.getShareType();
            getPresenter().create(imageUrl, shareUrl, text, type);
            clearArgs();
        }

        AppEventsLogger.activateApp(getActivity()); //facebook SDK event logger. Really needed?
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        AppEventsLogger.deactivateApp(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    protected SharePresenter createPresenter(Bundle savedInstanceState) {
        return new SharePresenter();
    }

    @Override
    public void shareFBDialog(String url, String link, String text) {
        if (FacebookDialog.canPresentShareDialog(getActivity().getApplicationContext(),
                FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
            FacebookDialog.ShareDialogBuilder shareDialog =
                    new FacebookDialog.ShareDialogBuilder(getActivity());
            if (!TextUtils.isEmpty(url)) {
                shareDialog.setPicture(url);
            }
            if (TextUtils.isEmpty(link)
                    && !TextUtils.isEmpty(url)) {
                shareDialog.setLink(url);
            }
            if (!TextUtils.isEmpty(link)) {
                shareDialog.setLink(link);
            }
            if (!TextUtils.isEmpty(text)) {
                shareDialog.setDescription(text);
            }

            uiHelper.trackPendingDialogCall(shareDialog.build().present());
        } else {
            publishFeedDialog(url, link, text, "DreamTrips");
        }
    }

    private void publishFeedDialog(String picture, String link, String text, String appName) {
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            Bundle params = new Bundle();
            params.putString("name", appName);
            params.putString("caption", text);
            params.putString("link", link);
            params.putString("picture", picture);
            WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(getActivity(), Session.getActiveSession(), params)).build();
            feedDialog.setOnCompleteListener((bundle, e) -> {
                if (feedDialog != null) {
                    if (e == null) {
                        informUser(getString(R.string.fab_posted));
                        getActivity().finish();
                    }
                    feedDialog.dismiss();
                }
            });
            feedDialog.setOnDismissListener(dialog -> getActivity().finish());
            feedDialog.show();
        } else {
            loginButton.setReadPermissions("user_photos");
            loginButton.setSessionStatusCallback((s, state, exception) -> {
                Log.w("Session callback: ", "" + s + "; " + state + "; " + exception);
                if (session != null && session.isOpened()) {
                    getActivity().runOnUiThread(() -> new Handler().postDelayed(() ->
                            getPresenter().openShareActivity(picture, link, text), 150));
                }
            });
            loginButton.performClick();
        }
    }

    @Override
    public void shareTwitterDialog(Uri imageUrl, String shareUrl, String text) {
        String url = shareUrl == null ? "" : shareUrl;

        if (!url.isEmpty()) {
            url += "\n";
        }

        TweetComposer.Builder builder = new TweetComposer.Builder(getActivity());
        builder.text(url + text);

        if (imageUrl != null) {
            builder.image(imageUrl);
        }

        builder.show();
    }

}
