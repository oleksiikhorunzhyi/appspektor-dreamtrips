package com.worldventures.dreamtrips.modules.common.view.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;
import timber.log.Timber;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.widget.media.AndroidMediaController;
import tv.danmaku.ijk.media.widget.media.IjkVideoView;


@Layout(R.layout.player_activity_simple)
public class PlayerActivity extends BaseActivity {

    @InjectView(R.id.myVideo)
    protected IjkVideoView videoView;
    protected AndroidMediaController mediaController;

    private boolean mBackPressed;

    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);
        Uri uri = getIntent().getData();

        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);

        mediaController = new AndroidMediaController(this, false);

        videoView.setMediaController(mediaController);

        if (uri != null) {
            videoView.setVideoURI(uri);
            videoView.start();
        } else {
            Timber.e("Null Data Source\n");
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        mBackPressed = true;
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBackPressed || !videoView.isBackgroundPlayEnabled()) {
            videoView.stopPlayback();
            videoView.release(true);
            videoView.stopBackgroundPlay();
        } else {
            videoView.enterBackground();
        }
    }

}
