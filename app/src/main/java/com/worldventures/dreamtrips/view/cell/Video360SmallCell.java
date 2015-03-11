package com.worldventures.dreamtrips.view.cell;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.config.Video360;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by 1 on 10.03.15.
 */
@Layout(R.layout.adapter_item_video_360_small)
public class Video360SmallCell extends AbstractCell<Video360> {

    @InjectView(R.id.textViewDuration)
    TextView textViewDuration;

    @InjectView(R.id.tv_title)
    TextView textViewTitle;

    @InjectView(R.id.iv_bg)
    ImageView imageViewPreview;

    @Inject
    UniversalImageLoader universalImageLoader;

    @Inject
    ActivityRouter activityRouter;

    public Video360SmallCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        this.universalImageLoader.loadImage(getModelObject().getThumbnail(), this.imageViewPreview, null);
        this.textViewTitle.setText(getModelObject().getTitle());
        this.textViewDuration.setText(getModelObject().getDuration());
    }

    @OnClick(R.id.iv_bg)
    void onItemClick() {
        activityRouter.open360Activity(getModelObject().getURL());
    }

    @Override
    public void prepareForReuse() {

    }
}
