package com.worldventures.dreamtrips.modules.common.view.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.panframe.android.lib.PFAsset;
import com.panframe.android.lib.PFAssetStatus;
import com.panframe.android.lib.PFView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PFViewMediaControls extends RelativeLayout {

    public static final int MODE_SPHERICAL = 0;
    public static final int MODE_FLAT_DISPLAY = 1;
    public static final int MODE_STEREO_SIDE_BY_SIDE = 2;

    private static final int STEP_VIDEO_POSITION_ON_BACKWARD_FORWARD_CLICK = 10;   //in seconds
    private static final int STEP_VIDEO_POSITION_ON_BACKWARD_FORWARD_HOLD = 10;   //in seconds

    @IntDef({MODE_SPHERICAL,
            MODE_FLAT_DISPLAY,
            MODE_STEREO_SIDE_BY_SIDE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CardBoardMode {
    }

    @InjectView(R.id.pf_control_seek)
    SeekBar seekBar;
    @InjectView(R.id.pf_control_time)
    TextView timeText;
    @InjectView(R.id.pf_control_play_pause)
    ToggleButton toggleBtnPlayPause;
    @InjectView(R.id.pf_control_card_board)
    ToggleButton toggleBtnCardBoardMode;
    @InjectView(R.id.pf_holdbtn_backward)
    HoldableButton holdableBtnBackward;
    @InjectView(R.id.pf_holdbtn_forward)
    HoldableButton holdableBtnForward;

    private PFView pfView;
    private PFAsset pfAsset;

    private ControlsListener controlsListener;
    private Timer timeAndBarTimer;
    private boolean isNeedTimeAndBarUpdate = true;

    private Runnable updateTimeViewRunnable = new Runnable() {
        @Override
        public void run() {
            String minutes = DateTimeUtils.convertTimeToString(pfAsset.getPlaybackTime());
            String seconds = DateTimeUtils.convertTimeToString(pfAsset.getDuration());
            String time = minutes + " / " + seconds;
            timeText.setText(time);
        }
    };

    //region View listeners
    private CompoundButton.OnCheckedChangeListener onPlayPauseChanged = (buttonView, isChecked) -> {
        if (isChecked) {
            onPlayClick();
        } else {
            onPauseClick();
        }
    };

    private CompoundButton.OnCheckedChangeListener onCardBoardModeChanged = (buttonView, isChecked) -> {
        onCardBoardModeChange(isChecked);
    };

    private SeekBar.OnSeekBarChangeListener onSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (controlsListener != null) {
                controlsListener.onSeek(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isNeedTimeAndBarUpdate = false;

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            setPfPlaybackPosition(seekBar.getProgress());
            isNeedTimeAndBarUpdate = true;
            if (controlsListener != null) {
                controlsListener.onFinishSeek(seekBar);
            }
        }
    };

    private HoldableButton.OnClickHoldListener onClickHoldListener = new HoldableButton.OnClickHoldListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.pf_holdbtn_backward:
                    onBackwardClicked();
                    break;
                case R.id.pf_holdbtn_forward:
                    onForwardClicked();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onHoldStart(View view) {
            PFViewMediaControls.this.onHoldStart();
        }

        @Override
        public void onHold(View view) {
            switch (view.getId()) {
                case R.id.pf_holdbtn_backward:
                    onBackwardHold();
                    break;
                case R.id.pf_holdbtn_forward:
                    onForwardHold();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onHoldEnd(View view, int holdEventCount) {
            PFViewMediaControls.this.onHoldEnd(holdEventCount);
        }
    };
    //endregion

    //region Setters
    public void setPFView(PFView pfView, PFAsset pfAsset) {
        this.pfView = pfView;
        this.pfAsset = pfAsset;
    }

    public void setControlsListener(ControlsListener controlsListener) {
        this.controlsListener = controlsListener;
    }
    //endregion

    //region Constructors
    public PFViewMediaControls(Context context) {
        super(context);
        initialize();
    }

    public PFViewMediaControls(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public PFViewMediaControls(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PFViewMediaControls(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }
    //endregion

    //region Private methods
    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_player360_controls, this, true);
        ButterKnife.inject(this);
        //highlighting on press. Not same listener because of case of multi view press at same moment of time
        toggleBtnPlayPause.setOnTouchListener(new ViewOnTouchHighlighter.Builder().setDefault().build());
        toggleBtnCardBoardMode.setOnTouchListener(new ViewOnTouchHighlighter.Builder().setDefault().build());
        holdableBtnBackward.setOnTouchListener(new ViewOnTouchHighlighter.Builder().setDefault().build());
        holdableBtnForward.setOnTouchListener(new ViewOnTouchHighlighter.Builder().setDefault().build());
        //
        seekBar.setPadding(0, 0, 0, 0);
        seekBar.setOnSeekBarChangeListener(onSeekBarListener);
        toggleBtnPlayPause.setOnCheckedChangeListener(onPlayPauseChanged);
        toggleBtnCardBoardMode.setOnCheckedChangeListener(onCardBoardModeChanged);
        holdableBtnBackward.setOnClickHoldListener(onClickHoldListener);
        holdableBtnForward.setOnClickHoldListener(onClickHoldListener);
        setClickable(true);
    }

    private void setPfPlaybackPosition(float playbackPosition) {
        pfAsset.setPLaybackTime(playbackPosition);
    }

    private void onPlayClick() {
        if (pfView != null) {
            pfView.injectImage(null);
        }
        pfAsset.play();
        if (controlsListener != null) {
            controlsListener.onPlayClicked();
        }
    }

    private void onPauseClick() {
        if (pfAsset.getStatus() == PFAssetStatus.PLAYING) {
            pfAsset.pause();
        }
        if (controlsListener != null) {
            controlsListener.onPauseClicked();
        }
    }

    private void onBackwardClicked() {
        setPfPlaybackPosition(pfAsset.getPlaybackTime() - STEP_VIDEO_POSITION_ON_BACKWARD_FORWARD_CLICK);
    }

    private void onForwardClicked() {
        setPfPlaybackPosition(pfAsset.getPlaybackTime() + STEP_VIDEO_POSITION_ON_BACKWARD_FORWARD_CLICK);
    }

    private void onBackwardHold() {
        setPfPlaybackPosition(pfAsset.getPlaybackTime() - STEP_VIDEO_POSITION_ON_BACKWARD_FORWARD_HOLD);
    }

    private void onForwardHold() {
        setPfPlaybackPosition(pfAsset.getPlaybackTime() + STEP_VIDEO_POSITION_ON_BACKWARD_FORWARD_HOLD);
    }

    private void onHoldStart() {
    }

    private void onHoldEnd(int count) {
    }

    private void onCardBoardModeChange(boolean useCardBoard) {
        /**
         * From panframe-android-sdk PFView setMode(int mode, float aspect)
         * Set display mode and optional aspect ratio of content
         * Parameters:
         *  mode - Select 0 for spherical, 1 for flat display, 2 for stereo side-by-side
         *  aspect - The aspect ratio of the content in flat view mode
         */
        pfView.setMode(useCardBoard ? MODE_STEREO_SIDE_BY_SIDE : MODE_SPHERICAL, 1);
        if (controlsListener != null) {
            controlsListener.onCardBoardModeChange(useCardBoard);
        }
    }

    private void updateTime() {
        timeText.post(updateTimeViewRunnable);
    }

    private void updateProgress() {
        seekBar.setProgress((int) pfAsset.getPlaybackTime());
    }

    private void purgeUpdateTimer() {
        if (timeAndBarTimer != null) {
            timeAndBarTimer.cancel();
            timeAndBarTimer.purge();
            timeAndBarTimer = null;
        }
    }

    private void purgeUpdateTimeViewRunnable() {
        timeText.removeCallbacks(updateTimeViewRunnable);
        updateTimeViewRunnable = null;
    }
    //endregion

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        purgeUpdateTimer();
        purgeUpdateTimeViewRunnable();
    }

    public void onStatusMessage(final PFAsset asset, PFAssetStatus status) {
        switch (status) {
            case PLAYING:
                seekBar.setMax((int) pfAsset.getDuration());
                if (timeAndBarTimer == null) {
                    timeAndBarTimer = new Timer();
                    final TimerTask updateThumbPosition = new TimerTask() {
                        public void run() {
                            if (isNeedTimeAndBarUpdate) {
                                updateProgress();
                                updateTime();
                            }
                        }
                    };
                    timeAndBarTimer.schedule(updateThumbPosition, 0, 33);
                }
                break;
            case STOPPED:
            case PAUSED:
            case COMPLETE:
                toggleBtnPlayPause.setOnCheckedChangeListener(null);
                toggleBtnPlayPause.setChecked(false);
                toggleBtnPlayPause.setOnCheckedChangeListener(onPlayPauseChanged);
                purgeUpdateTimer();
                break;
            default:
                break;
        }
    }

    public static class ControlsListener {
        public void onSeek(int progress) {
        }

        public void onFinishSeek(SeekBar seekBar) {
        }

        public void onPlayClicked() {
        }

        public void onPauseClicked() {
        }

        public void onBackwardClicked() {
        }

        public void onBackwardHold() {
        }

        public void onForwardClicked() {
        }

        public void onForwardHold() {
        }

        public void onCardBoardModeChange(boolean useCardBoard) {
        }
    }
}