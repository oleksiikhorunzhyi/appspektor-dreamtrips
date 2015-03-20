/*
 * SimpleStreamPlayer
 * Android example of Panframe library
 * The example plays back an panoramic movie from a resource.
 * 
 * (c) 2012-2013 Mindlight. All rights reserved.
 * Visit www.panframe.com for more information. 
 * 
 */

package com.worldventures.dreamtrips.view.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.panframe.android.lib.PFAsset;
import com.panframe.android.lib.PFAssetObserver;
import com.panframe.android.lib.PFAssetStatus;
import com.panframe.android.lib.PFNavigationMode;
import com.panframe.android.lib.PFObjectFactory;
import com.panframe.android.lib.PFView;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;

import butterknife.InjectView;

@Layout(R.layout.activity_360)
public class SimpleStreamPlayerActivity extends BaseActivity implements PFAssetObserver {

    public static final String EXTRA_URL = "EXTRA_URL";

    PFView pfView;
    PFAsset pfAsset;
    PFNavigationMode _currentNavigationMode = PFNavigationMode.MOTION;

    @InjectView(R.id.framecontainer)
    ViewGroup frameContainer;

    /**
     * Creation and initalization of the Activitiy.
     * Initializes variables, listeners, and starts request of a movie list.
     *
     * @param savedInstanceState a saved instance of the Bundle
     */
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        frameContainer.setBackgroundColor(0xFF000000);

        // Apple's reference example
        loadVideo(getIntent().getBundleExtra(ActivityRouter.EXTRA_BUNDLE).getString(EXTRA_URL));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (pfAsset != null) {
            pfAsset.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pfAsset.release();
        pfView.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pfAsset.getStatus().equals(PFAssetStatus.PAUSED)) {
            pfAsset.play();
        }
    }

    /**
     * Start the video with a local file path
     *
     * @param filename The file path on device storage
     */
    public void loadVideo(String filename) {

        pfView = PFObjectFactory.view(this);
        pfAsset = PFObjectFactory.assetFromUri(this, Uri.parse(filename), this);

        pfView.displayAsset(pfAsset);
        pfView.setNavigationMode(_currentNavigationMode);

        frameContainer.addView(pfView.getView(), 0);

    }

    /**
     * Status callback from the PFAsset instance.
     * Based on the status this function selects the appropriate action.
     *
     * @param asset  The asset who is calling the function
     * @param status The current status of the asset.
     */
    public void onStatusMessage(final PFAsset asset, PFAssetStatus status) {
        switch (status) {
            case LOADED:
                Log.d("SimplePlayer", "Loaded");
                pfAsset.play();
                break;
            case DOWNLOADING:
                Log.d("SimplePlayer", "Downloading 360� movie: " + pfAsset.getDownloadProgress() + " percent complete");
                break;
            case DOWNLOADED:
                Log.d("SimplePlayer", "Downloaded to " + asset.getUrl());
                break;
            case DOWNLOADCANCELLED:
                Log.d("SimplePlayer", "Download cancelled");
                break;
            case PLAYING:
                Log.d("SimplePlayer", "Playing");
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                break;
            case PAUSED:
                Log.d("SimplePlayer", "Paused");
                break;
            case STOPPED:
                Log.d("SimplePlayer", "Stopped");
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                break;
            case COMPLETE:
                Log.d("SimplePlayer", "Complete");
                break;
            case ERROR:
                Log.d("SimplePlayer", "Error");
                break;
        }
    }

}
