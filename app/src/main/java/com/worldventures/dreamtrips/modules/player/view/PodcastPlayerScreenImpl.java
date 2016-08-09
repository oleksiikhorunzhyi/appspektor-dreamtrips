package com.worldventures.dreamtrips.modules.player.view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.MediaController;

import com.messenger.ui.view.layout.BaseViewStateLinearLayout;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.modules.player.presenter.PodcastPresenter;
import com.worldventures.dreamtrips.modules.player.presenter.PodcastPresenterImpl;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class PodcastPlayerScreenImpl extends BaseViewStateLinearLayout<PodcastPlayerScreen, PodcastPresenter>
        implements PodcastPlayerScreen {

    @InjectView(R.id.player_view)
    FrameLayout playerView;
    private Injector injector;
    private MediaController androidMediaController;

    public PodcastPlayerScreenImpl(Context context) {
        super(context);
        init();
    }

    public PodcastPlayerScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        injector = ((Injector) getContext());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override
    public PodcastPresenter createPresenter() {
        return new PodcastPresenterImpl(getContext(), injector);
    }

    @Override
    public void attachMediaPlayerControl(MediaController.MediaPlayerControl mediaPlayerControl) {
        androidMediaController = new MediaController(getContext());
        androidMediaController.setAnchorView(playerView);
        androidMediaController.setMediaPlayer(mediaPlayerControl);
        androidMediaController.show();
        androidMediaController.setEnabled(true);
    }

    @Override
    public boolean onApiError(ErrorResponse errorResponse) {
        return false;
    }

    @Override
    public void onApiCallFailed() {
        //
    }

    @Override
    public void informUser(@StringRes int stringId) {
        //
    }

    @Override
    public void informUser(String message) {
        //
    }

    @Override
    public boolean isTabletLandscape() {
        return false;
    }
}
