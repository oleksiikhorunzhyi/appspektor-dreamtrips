package com.worldventures.dreamtrips.view.cell;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.Video;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.utils.AdobeTrackingHelper;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.view.activity.PlayerActivity;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_video)
public class VideoCell extends AbstractCell<Video> {

    @InjectView(R.id.iv_bg)
    ImageView ivBg;
    @InjectView(R.id.iv_play)
    ImageView ivPlay;
    @InjectView(R.id.tv_title)
    TextView tvTitle;

    @Inject
    Context context;
    @Inject
    UniversalImageLoader universalImageLoader;

    @Inject
    SessionHolder<UserSession> appSessionHolder;


    public VideoCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        this.universalImageLoader.loadImage(getModelObject().getImageUrl(), this.ivBg, null);
        this.tvTitle.setText(getModelObject().getVideoName());
    }

    @OnClick(R.id.iv_play)
    public void onPlayClick() {
        Intent intent = new Intent(context, PlayerActivity.class)
                .setData(Uri.parse(getModelObject().getMp4Url()));
        AdobeTrackingHelper.playVideo(getModelObject().getVideoName(), appSessionHolder.get().get().getUser().getEmail());
        context.startActivity(intent);
    }

    @Override
    public void prepareForReuse() {

    }
}
