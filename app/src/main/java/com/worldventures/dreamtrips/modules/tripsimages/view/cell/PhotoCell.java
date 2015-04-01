package com.worldventures.dreamtrips.modules.tripsimages.view.cell;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenAvailableObject;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Optional;

@Layout(R.layout.adapter_item_photo)
public class PhotoCell extends AbstractCell<IFullScreenAvailableObject> {

    @InjectView(R.id.iv_bg)
    protected  ImageView imageView;

    @Optional
    @InjectView(R.id.user_photo)
    protected ImageView imageViewUser;
    @Optional
    @InjectView(R.id.user_location)
    protected TextView user_location;
    @Optional
    @InjectView(R.id.user_name)
    protected TextView user_name;
    @Optional
    @InjectView(R.id.title)
    protected TextView title;
    @Optional
    @InjectView(R.id.shot_location)
    protected TextView shot_location;

    @Inject
    protected UniversalImageLoader universalImageLoader;

    public PhotoCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        if (imageViewUser != null) {
            this.user_location.setText(getModelObject().getUserLocation());
            this.shot_location.setText(getModelObject().getPhotoLocation());
            this.title.setText(getModelObject().getFSTitle());
            this.user_name.setText(getModelObject().getUserName());
            this.universalImageLoader.loadImage(getModelObject().getUserAvatar(), this.imageViewUser, null, new SimpleImageLoadingListener());
        }
        this.universalImageLoader.loadImage(getModelObject().getFSImage().getThumb().getUrl(), this.imageView, UniversalImageLoader.OP_TRIP_PHOTO, new SimpleImageLoadingListener());
    }

    @Override
    public void prepareForReuse() {
        imageView.setImageBitmap(null);
    }
}
