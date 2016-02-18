package com.messenger.ui.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.messenger.entities.PhotoAttachment;
import com.messenger.ui.presenter.MessageImageFullscreenPresenter;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen.FullScreenPhotoFragment;

import butterknife.InjectView;

@Layout(R.layout.fragment_fullscreen_message_photo)
public class MessageImageFullscreenFragment extends FullScreenPhotoFragment<MessageImageFullscreenPresenter, PhotoAttachment>
                                            implements MessageImageFullscreenPresenter.View {

    @InjectView(R.id.tv_date)
    TextView attachmentDate;

    @Override
    protected MessageImageFullscreenPresenter createPresenter(Bundle savedInstanceState) {
        return new MessageImageFullscreenPresenter((PhotoAttachment) getArgs().getPhoto(), getArgs().getType());
    }

    @Override
    public void setDateLabel(String dateLabel) {
        attachmentDate.setText(dateLabel);
    }

}
