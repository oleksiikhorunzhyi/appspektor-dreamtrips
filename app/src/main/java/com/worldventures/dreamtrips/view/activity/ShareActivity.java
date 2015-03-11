package com.worldventures.dreamtrips.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

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
import com.worldventures.dreamtrips.presentation.FacebookPickPhotoActivityPM;
import com.worldventures.dreamtrips.presentation.ShareActivityPM;

import butterknife.InjectView;

@Layout(R.layout.activity_share)
public class ShareActivity extends PresentationModelDrivenActivity<ShareActivityPM> implements FacebookPickPhotoActivityPM.View, ShareActivityPM.View {

    public static final String FB = "fb";
    public static final String TW = "tw";

    public static final String BUNDLE_URL = "BUNDLE_URL";
    public static final String BUNDLE_TEXT = "BUNDLE_TEXT";
    public static final String BUNDLE_SHARE_TYPE = "BUNDLE_SHARE_TYPE";
    @InjectView(R.id.login_button)
    LoginButton loginButton;
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = (session, state, exception) -> {
        // onSessionStateChange(session, state, exception);
    };
    private String url;
    private String text;
    private String type;

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
            url = bundleExtra.getString(BUNDLE_URL);
            text = bundleExtra.getString(BUNDLE_TEXT);
            type = bundleExtra.getString(BUNDLE_SHARE_TYPE);
            getPresentationModel().create(url, text, type);
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
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    protected ShareActivityPM createPresentationModel(Bundle savedInstanceState) {
        return new ShareActivityPM(this);
    }


    public void shareFBDialog(String url, String text) {
        if (FacebookDialog.canPresentShareDialog(getApplicationContext(), FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
            FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
                    .setLink(url)
                    .setCaption(text)
                    .setApplicationName("DreamTrips")
                    .build();
            uiHelper.trackPendingDialogCall(shareDialog.present());
        } else {
            publishFeedDialog(url, text, "DreamTrips");
        }
    }


    private void publishFeedDialog(String picture, String text, String appName) {
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            Bundle params = new Bundle();
            params.putString("name", appName);
            params.putString("caption", text);
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
                if (session != null && session.isOpened()) {
                    publishFeedDialog(picture, text, appName);
                }
            });
            loginButton.performClick();
        }

    }

    public void shareTwitterDialog(Uri url, String text) {
        TweetComposer.Builder builder = new TweetComposer.Builder(this)
                .text(text);
        if (url != null) builder.image(url);
        builder.show();
    }
}
