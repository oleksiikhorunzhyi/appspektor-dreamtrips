package com.worldventures.dreamtrips.view.cell;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.Video;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;

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
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getModelObject().getMp4Url()));
        intent.setDataAndType(Uri.parse(getModelObject().getMp4Url()), "video/*");
        context.startActivity(intent);
    }

    @Override
    public void prepareForReuse() {

    }
}
