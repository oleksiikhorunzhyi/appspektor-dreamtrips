package com.worldventures.dreamtrips.modules.common.view.activity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;
import tv.danmaku.ijk.media.widget.MediaController;
import tv.danmaku.ijk.media.widget.VideoView;

/**
 * 1 on 04.02.15.
 */
@Layout(R.layout.player_activity_simple)
public class PlayerActivity extends BaseActivity {

    @InjectView(R.id.myVideo)
    protected VideoView videoView;

    private ProgressDialog pDialog;
    private boolean paused = false;

    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.buffering));
        pDialog.setCancelable(false);
        pDialog.show();

        Uri uri = getIntent().getData();
        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(
                    this);
            mediacontroller.setAnchorView(videoView);
            // Get the URL from String VideoURL
            videoView.setMediaController(mediacontroller);
            videoView.setVideoURI(uri);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        videoView.requestFocus();
        videoView.setOnPreparedListener((mp) -> {
            pDialog.dismiss();
            videoView.start();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (paused) {
            paused = false;
            videoView.resume();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
        videoView.pause();
    }

}
