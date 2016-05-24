package com.messenger.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.messenger.entities.PhotoAttachment;
import com.messenger.ui.module.flagging.FlaggingView;
import com.messenger.ui.module.flagging.FullScreenFlaggingViewImpl;
import com.messenger.ui.presenter.MessageImageFullscreenPresenter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.FlagView;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
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
        flaggingView = new FullScreenFlaggingViewImpl(rootView, (Injector) getActivity());
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

    @Override
    public void setContent(IFullScreenObject photo) {
        if (!TextUtils.isEmpty(photo.getFSImage().getUrl())) {
            DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                    .setUri(photo.getFSImage().getUrl())
                    .build();

            ivImage.setController(draweeController);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        flaggingView.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        flaggingView.onRestoreInstanceState(savedInstanceState);
    }
}
