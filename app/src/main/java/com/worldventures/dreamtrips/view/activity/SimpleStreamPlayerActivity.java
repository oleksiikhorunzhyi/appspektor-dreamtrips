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

import java.util.*;

import com.panframe.android.lib.*;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.database.Cursor;
import android.support.v4.app.*;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;
import android.widget.SeekBar.*;

import butterknife.InjectView;

@Layout(R.layout.activity_360)
public class SimpleStreamPlayerActivity extends BaseActivity implements PFAssetObserver, OnSeekBarChangeListener {

    public static final String EXTRA_URL = "EXTRA_URL";

    PFView _pfview;
    PFAsset _pfasset;
    PFNavigationMode _currentNavigationMode = PFNavigationMode.MOTION;

    @InjectView(R.id.progressBar)
    ProgressBar progressBar;

    boolean _updateThumb = true;
    Timer _scrubberMonitorTimer;

    ViewGroup _frameContainer;
    Button _stopButton;
    Button _playButton;
    Button _touchButton;
    SeekBar _scrubber;

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

        _frameContainer = (ViewGroup) findViewById(R.id.framecontainer);
        _frameContainer.setBackgroundColor(0xFF000000);

        _playButton = (Button) findViewById(R.id.playbutton);
        _stopButton = (Button) findViewById(R.id.stopbutton);
        _touchButton = (Button) findViewById(R.id.touchbutton);
        _scrubber = (SeekBar) findViewById(R.id.scrubber);

        _playButton.setOnClickListener(playListener);
        _stopButton.setOnClickListener(stopListener);
        _touchButton.setOnClickListener(touchListener);
        _scrubber.setOnSeekBarChangeListener(this);

        _scrubber.setEnabled(false);

        // Apple's reference example
        loadVideo(getIntent().getBundleExtra(ActivityRouter.EXTRA_BUNDLE).getString(EXTRA_URL));

        showControls(true);

    }

    /**
     * Show/Hide the playback controls
     *
     * @param bShow Show or hide the controls. Pass either true or false.
     */
    public void showControls(boolean bShow) {
        int visibility = View.GONE;

        if (bShow)
            visibility = View.VISIBLE;

        _playButton.setVisibility(visibility);
        _stopButton.setVisibility(visibility);
        _touchButton.setVisibility(visibility);
        _scrubber.setVisibility(visibility);

        if (_pfview != null) {
            if (!_pfview.supportsNavigationMode(PFNavigationMode.MOTION))
                _touchButton.setVisibility(View.GONE);
        }
    }


    /**
     * Start the video with a local file path
     *
     * @param filename The file path on device storage
     */
    public void loadVideo(String filename) {

        _pfview = PFObjectFactory.view(this);
        _pfasset = PFObjectFactory.assetFromUri(this, Uri.parse(filename), this);

        _pfview.displayAsset(_pfasset);
        _pfview.setNavigationMode(_currentNavigationMode);

        _frameContainer.addView(_pfview.getView(), 0);

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
                break;
            case DOWNLOADING:
                Log.d("SimplePlayer", "Downloading 360� movie: " + _pfasset.getDownloadProgress() + " percent complete");
                break;
            case DOWNLOADED:
                progressBar.setVisibility(View.GONE);
                Log.d("SimplePlayer", "Downloaded to " + asset.getUrl());
                break;
            case DOWNLOADCANCELLED:
                Log.d("SimplePlayer", "Download cancelled");
                break;
            case PLAYING:
                Log.d("SimplePlayer", "Playing");
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                _scrubber.setEnabled(true);
                _playButton.setText("pause");
                _scrubberMonitorTimer = new Timer();
                final TimerTask task = new TimerTask() {
                    public void run() {
                        if (_updateThumb) {
                            _scrubber.setMax((int) asset.getDuration());
                            _scrubber.setProgress((int) asset.getPlaybackTime());
                        }
                    }
                };
                _scrubberMonitorTimer.schedule(task, 0, 33);
                break;
            case PAUSED:
                Log.d("SimplePlayer", "Paused");
                _playButton.setText("play");
                break;
            case STOPPED:
                Log.d("SimplePlayer", "Stopped");
                _playButton.setText("play");
                _scrubberMonitorTimer.cancel();
                _scrubberMonitorTimer = null;
                _scrubber.setProgress(0);
                _scrubber.setEnabled(false);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                break;
            case COMPLETE:
                Log.d("SimplePlayer", "Complete");
                _playButton.setText("play");
                _scrubberMonitorTimer.cancel();
                _scrubberMonitorTimer = null;
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                break;
            case ERROR:
                Log.d("SimplePlayer", "Error");
                break;
        }
    }

    /**
     * Click listener for the play/pause button
     */
    private OnClickListener playListener = new OnClickListener() {
        public void onClick(View v) {
            if (_pfasset.getStatus() == PFAssetStatus.PLAYING) {
                _pfasset.pause();
            } else
                _pfasset.play();
        }
    };

    /**
     * Click listener for the stop/back button
     */
    private OnClickListener stopListener = new OnClickListener() {
        public void onClick(View v) {
            _pfasset.stop();
        }
    };

    /**
     * Click listener for the navigation mode (touch/motion (if available))
     */
    private OnClickListener touchListener = new OnClickListener() {
        public void onClick(View v) {
            if (_pfview != null) {
                Button touchButton = (Button) findViewById(R.id.touchbutton);
                if (_currentNavigationMode == PFNavigationMode.TOUCH) {
                    _currentNavigationMode = PFNavigationMode.MOTION;
                    touchButton.setText("motion");
                } else {
                    _currentNavigationMode = PFNavigationMode.TOUCH;
                    touchButton.setText("touch");
                }
                _pfview.setNavigationMode(_currentNavigationMode);
            }
        }
    };

    /**
     * Called when pausing the app.
     * This function pauses the playback of the asset when it is playing.
     */
    public void onPause() {
        super.onPause();
        if (_pfasset != null) {
            if (_pfasset.getStatus() == PFAssetStatus.PLAYING)
                _pfasset.pause();
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus making its data unavailable.
     *
     * @param seekbar  The SeekBar whose progress has changed
     * @param progress The current progress level.
     * @param fromUser True if the progress change was initiated by the user.
     */
    public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
    }

    /**
     * Notification that the user has started a touch gesture.
     * In this function we signal the timer not to update the playback thumb while we are adjusting it.
     *
     * @param seekbar The SeekBar in which the touch gesture began
     */
    public void onStartTrackingTouch(SeekBar seekbar) {
        _updateThumb = false;
    }

    /**
     * Notification that the user has finished a touch gesture.
     * In this function we request the asset to seek until a specific time and signal the timer to resume the update of the playback thumb based on playback.
     *
     * @param seekbar The SeekBar in which the touch gesture began
     */
    public void onStopTrackingTouch(SeekBar seekbar) {
        _updateThumb = true;
    }

}
