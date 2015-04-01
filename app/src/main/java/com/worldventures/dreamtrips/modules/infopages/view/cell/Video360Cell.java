package com.worldventures.dreamtrips.modules.infopages.view.cell;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.modules.infopages.model.Video360;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_video_360)
public class Video360Cell extends AbstractCell<Video360> {

    @InjectView(R.id.textViewDuration)
    protected TextView textViewDuration;

    @InjectView(R.id.tv_title)
    protected TextView textViewTitle;

    @InjectView(R.id.iv_bg)
    protected ImageView imageViewPreview;

    @Inject
    protected UniversalImageLoader universalImageLoader;

    @Inject
    protected ActivityRouter activityRouter;

    public Video360Cell(View view) {
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
