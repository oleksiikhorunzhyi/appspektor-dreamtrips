package com.worldventures.dreamtrips.modules.infopages.view.cell;

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
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.core.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.view.activity.PlayerActivity;
import com.worldventures.dreamtrips.modules.infopages.model.Video;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_video)
public class VideoCell extends AbstractCell<Video> {

    @InjectView(R.id.iv_bg)
    protected ImageView ivBg;
    @InjectView(R.id.iv_play)
    protected ImageView ivPlay;
    @InjectView(R.id.tv_title)
    protected TextView tvTitle;

    @Inject
    protected Context context;
    @Inject
    protected UniversalImageLoader universalImageLoader;

    @Inject
    protected SessionHolder<UserSession> appSessionHolder;

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
        TrackingHelper.playVideo(getModelObject().getVideoName(), appSessionHolder.get().get().getUser().getEmail());
        context.startActivity(intent);
    }

    @Override
    public void prepareForReuse() {
        ivBg.setImageResource(0);
    }
}
