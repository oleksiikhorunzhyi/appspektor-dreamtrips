package com.messenger.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.FlagView;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;
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
            loadImage(photo.getFSImage());
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

    private void loadImage(Image image) {
        ivImage.requestLayout();

        Runnable task = () -> {
            // this method is called for previous fragment, which has difference between position of
            // this fragment and displayed one is greater than count of visible items divided by 2 + 1
            // And the width of one is 0.
            if (ivImage != null && ivImage.getWidth() > 0 && ivImage.getHeight() > 0) {
                int previewWidth = getResources().getDimensionPixelSize(R.dimen.chat_image_width);
                int previewHeight = getResources().getDimensionPixelSize(R.dimen.chat_image_height);

                DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                        .setLowResImageRequest(GraphicUtils.createResizeImageRequest(Uri.parse(image.getUrl()),
                                previewWidth, previewHeight))
                        .setImageRequest(GraphicUtils.createResizeImageRequest(Uri.parse(image.getUrl()),
                                ivImage.getWidth(), ivImage.getHeight()))
                        .build();

                ivImage.setController(draweeController);
            }
        };
        ViewUtils.runTaskAfterMeasure(ivImage, task);
    }

}
