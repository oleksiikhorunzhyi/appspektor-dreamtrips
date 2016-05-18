package com.messenger.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.messenger.entities.PhotoAttachment;
import com.messenger.ui.module.flagging.FlaggingView;
import com.messenger.ui.module.flagging.FullScreenFlaggingViewImpl;
import com.messenger.ui.presenter.MessageImageFullscreenPresenter;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.FlagView;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen.FullScreenPhotoFragment;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_fullscreen_message_photo)
public class MessageImageFullscreenFragment extends FullScreenPhotoFragment<MessageImageFullscreenPresenter, PhotoAttachment>
                                            implements MessageImageFullscreenPresenter.View {

    @InjectView(R.id.tv_date)
    TextView attachmentDate;
    @InjectView(R.id.flag)
    FlagView flagView;

    private FlaggingView flaggingView;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        flaggingView = new FullScreenFlaggingViewImpl(rootView);
    }

    @Override
    protected MessageImageFullscreenPresenter createPresenter(Bundle savedInstanceState) {
        return new MessageImageFullscreenPresenter((PhotoAttachment) getArgs().getPhoto(), getArgs().getType());
    }

    @OnClick(R.id.flag)
    public void onFlagPressed() {
        getPresenter().onFlagPressed();
    }

    @Override
    public void setDateLabel(String dateLabel) {
        attachmentDate.setText(dateLabel);
    }

    @Override
    public FlaggingView getFlaggingView() {
        return flaggingView;
    }

    @Override
    public void setShowFlag(boolean showFlag) {
        flagView.setVisibility(showFlag ? View.VISIBLE : View.GONE);
    }
}
