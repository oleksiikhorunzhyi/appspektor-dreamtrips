package com.worldventures.dreamtrips.modules.player.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;

import rx.functions.Action0;
import rx.functions.Action1;

public class DtMediaController extends FrameLayout {

    private SeekBar progress;
    private TextView entTime, currentTime;
    private ImageButton pause;

    private boolean dragging;

    private Action1<Integer> onSeekTo;
    private Action0 onPlayPause;

    private int duration;

    private MediaStringUtils mediaStringUtils = new MediaStringUtils();
    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
            dragging = true;
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (fromuser) onSeekTo.call((duration * progress) / 1000);
        }

        public void onStopTrackingTouch(SeekBar bar) {
            dragging = false;
        }
    };

    public DtMediaController(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.media_controller, this, true);
    }

    public DtMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.media_controller, this, true);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        pause = (ImageButton) findViewById(R.id.pause_play);

        pause.requestFocus();
        pause.setOnClickListener(view -> onPausePlayPressed());

        progress = (SeekBar) findViewById(R.id.mediacontroller_progress);
        progress.setOnSeekBarChangeListener(mSeekListener);
        progress.setMax(1000);

        entTime = (TextView) findViewById(R.id.time);
        currentTime = (TextView) findViewById(R.id.time_current);
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setProgress(int currentPosition, int bufferPercentage) {
        if (dragging) return;

        if (duration > 0) {
            long pos = 1000L * currentPosition / duration;
            progress.setProgress((int) pos);
        }

        progress.setSecondaryProgress(bufferPercentage * 10);

        entTime.setText(mediaStringUtils.stringForTime((int) duration));
        currentTime.setText(mediaStringUtils.stringForTime(currentPosition));
    }

    public void setPausePlay(boolean isPlaying) {
        if (isPlaying) {
            pause.setImageResource(R.drawable.ic_player360_pause);
        } else {
            pause.setImageResource(R.drawable.ic_player360_play);
        }
    }

    private void onPausePlayPressed() {
        onPlayPause.call();
    }

    public void setOnPlayPause(Action0 onPlayPause) {
        this.onPlayPause = onPlayPause;
    }

    public void setOnSeekTo(Action1<Integer> onSeekTo) {
        this.onSeekTo = onSeekTo;
    }

    @Override
    public void setEnabled(boolean enabled) {
        pause.setEnabled(enabled);
        progress.setEnabled(enabled);
        super.setEnabled(enabled);
    }

}
