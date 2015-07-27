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
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.common.presenter.SharePresenter;
import com.worldventures.dreamtrips.modules.facebook.presenter.FacebookPickPhotoPresenter;

import butterknife.InjectView;

@Layout(R.layout.activity_share)
public class ShareActivity extends ActivityWithPresenter<SharePresenter>
        implements FacebookPickPhotoPresenter.View, SharePresenter.View {

    public static final String FB = "fb";
    public static final String TW = "tw";

    public static final String BUNDLE_IMAGE_URL = "BUNDLE_IMAGE_URL";
    public static final String BUNDLE_SHARE_URL = "BUNDLE_SHARE_URL";
    public static final String BUNDLE_TEXT = "BUNDLE_TEXT";
    public static final String BUNDLE_SHARE_TYPE = "BUNDLE_SHARE_TYPE";
    @InjectView(R.id.login_button)
    protected LoginButton loginButton;
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = (session, state, exception) -> {
        //nothing to do here
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
        Bundle bundleExtra = getIntent().getBundleExtra(ActivityRouter.EXTRA_BUNDLE);
        if (bundleExtra == null) {
            finish();
        } else {
            String imageUrl = bundleExtra.getString(BUNDLE_IMAGE_URL, "");
            String shareUrl = bundleExtra.getString(BUNDLE_SHARE_URL, "");
            String text = bundleExtra.getString(BUNDLE_TEXT, "");
            String type = bundleExtra.getString(BUNDLE_SHARE_TYPE);
            getPresentationModel().create(imageUrl, shareUrl, text, type);
            getIntent().removeExtra(ActivityRouter.EXTRA_BUNDLE);
        }

        AppEventsLogger.activateApp(this); //facebook SDK event logger. Really needed?
    }

    @Override
    protected void onPause() {
        super.onPause();
        uiHelper.onPause();
        AppEventsLogger.deactivateApp(this);
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
    protected SharePresenter createPresentationModel(Bundle savedInstanceState) {
        return new SharePresenter();
    }


    @Override
    public void shareFBDialog(String url, String link, String text) {
        if (FacebookDialog.canPresentShareDialog(getApplicationContext(),
                FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
            FacebookDialog.ShareDialogBuilder shareDialog =
                    new FacebookDialog.ShareDialogBuilder(this);
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
            WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(this, Session.getActiveSession(), params)).build();
            feedDialog.setOnCompleteListener((bundle, e) -> {
                if (feedDialog != null) {
                    if (e == null) {
                        informUser(getString(R.string.fab_posted));
                        finish();
                    }
                    feedDialog.dismiss();
                }
            });
            feedDialog.setOnDismissListener(dialog -> finish());
            feedDialog.show();
        } else {
            loginButton.setReadPermissions("user_photos");
            loginButton.setSessionStatusCallback((s, state, exception) -> {
                Log.w("Session callback: ", "" + s + "; " + state + "; " + exception);
                if (session != null && session.isOpened()) {
                    runOnUiThread(() -> new Handler().postDelayed(() ->
                            getPresentationModel().openShareActivity(picture, link, text), 150));
                }
            });
            loginButton.performClick();
        }
    }

    @Override
    public void shareTwitterDialog(Uri imageUrl, String shareUrl, String text) {
        String url = shareUrl;

        if (!shareUrl.isEmpty()) {
            url += "\n";
        }

        TweetComposer.Builder builder = new TweetComposer.Builder(this);
        builder.text(url + text);

        if (imageUrl != null) {
            builder.image(imageUrl);
        }

        builder.show();
    }

}
