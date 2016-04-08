/*
 * SimpleStreamPlayer
 * Android example of Panframe library
 * The example plays back an panoramic movie from a resource.
 * 
 * (c) 2012-2013 Mindlight. All rights reserved.
 * Visit www.panframe.com for more information. 
 * 
 */

package com.worldventures.dreamtrips.modules.common.view.activity;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.panframe.android.lib.PFAsset;
import com.panframe.android.lib.PFAssetObserver;
import com.panframe.android.lib.PFAssetStatus;
import com.panframe.android.lib.PFNavigationMode;
import com.panframe.android.lib.PFObjectFactory;
import com.panframe.android.lib.PFView;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.utils.AnimationUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.PFViewMediaControls;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.VideoPlayerPresenter;

import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

@Layout(R.layout.activity_360)
public class Player360Activity extends ActivityWithPresenter<VideoPlayerPresenter> implements PFAssetObserver, VideoPlayerPresenter.View {

    public static final String EXTRA_URL = "EXTRA_URL";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";

    private PFView pfView;
    private PFAsset pfAsset;
    private PFNavigationMode currentNavigationMode = PFNavigationMode.MOTION;

    private boolean isBarsShown = true;

    @InjectView(R.id.framecontainer)
    protected ViewGroup frameContainer;
    @InjectView(R.id.media_controls)
    protected PFViewMediaControls mediaControls;
    @InjectView(R.id.topBar)
    protected View topBarHolder;

    @Override
    protected VideoPlayerPresenter createPresentationModel(Bundle savedInstanceState) {
        return new VideoPlayerPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        frameContainer.setBackgroundColor(0xFF000000);

        String url = getIntent().getBundleExtra(ActivityRouter.EXTRA_BUNDLE).getString(EXTRA_URL);
        if (TextUtils.isEmpty(url)) {
            finish();
        } else {
            loadVideo(url);
        }

        String title = getIntent().getBundleExtra(ActivityRouter.EXTRA_BUNDLE).getString(EXTRA_TITLE);
        ((TextView)findViewById(R.id.topBar_tv_title)).setText(title);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (pfAsset != null) {
            pfAsset.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (pfAsset != null) {
            pfAsset.stop();
        }
        finish();
    }

    @Override
    public void onDestroy() {
        try {
            if (pfAsset != null) {
                pfAsset.release();
            }
            if (pfView != null) {
                pfView.release();
            }
        } catch (Exception e) {
            Timber.e(e, "Problem on destroy");
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PFAssetStatus.PAUSED.equals(pfAsset.getStatus())) {
            pfAsset.play();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (pfView != null) {
            pfView.handleOrientationChange();
        }
    }

    @Override
    public void onHeadphonesUnPlugged() {
        if (pfAsset != null && PFAssetStatus.PLAYING.equals(pfAsset.getStatus())) {
            pfAsset.pause();
        }
    }

    @OnClick({R.id.clickable_view })
    public void onRootClick(View view){
        toggleBarsVisibility();
    }

    @OnClick({R.id.topbar_btn_exit})
    public void onBtnExitClick(View view){
        finish();
    }

    private void toggleBarsVisibility(){
        if (isBarsShown) {
            hideBars();
        } else {
            showBars();
        }
        isBarsShown = !isBarsShown;
    }

    private void showBars(){
        AnimationUtils.appearFromTopEdge(topBarHolder);
        AnimationUtils.appearFromBottomEdge(mediaControls);
    }

    private void hideBars(){
        AnimationUtils.hideInTopEdge(topBarHolder);
        AnimationUtils.hideInBottomEdge(mediaControls);
    }

    /**
     * Start the onMemberShipVideos with a local file path
     *
     * @param filename The file path on device storage
     */
    public void loadVideo(String filename) {
        pfView = PFObjectFactory.view(this);
        pfAsset = PFObjectFactory.assetFromUri(this, Uri.parse(filename), this);

        pfView.displayAsset(pfAsset);
        pfView.setNavigationMode(currentNavigationMode);

        frameContainer.addView(pfView.getView(), 0);

        initializeMediaControls();
    }

    private void initializeMediaControls() {
        mediaControls.setPFView(pfView, pfAsset);
        mediaControls.setControlsListener(null);
    }

    /**
     * Status callback from the PFAsset instance.
     * Based on the status this function selects the appropriate action.
     *
     * @param asset  The asset who is calling the function
     * @param status The current status of the asset.
     */
    @Override
    public void onStatusMessage(final PFAsset asset, PFAssetStatus status) {
        mediaControls.onStatusMessage(asset, status);
        switch (status) {
            case LOADED:
                Timber.d("Loaded");
                pfAsset.play();
                break;
            case DOWNLOADING:
                Timber.d("Downloading 360� movie: \" + pfAsset.getDownloadProgress() + \" percent complete");
                break;
            case DOWNLOADED:
                Timber.d("Downloaded to " + asset.getUrl());
                break;
            case DOWNLOADCANCELLED:
                Timber.d("Download cancelled");
                break;
            case PLAYING:
                Timber.d("Playing");
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                break;
            case PAUSED:
                Timber.d("Paused");
                break;
            case STOPPED:
                Timber.d("Stopped");
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                break;
            case COMPLETE:
                Timber.d("Complete");
                break;
            case ERROR:
                Timber.d("Error");
                break;
        }
    }
}