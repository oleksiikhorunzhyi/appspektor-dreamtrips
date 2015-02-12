package com.worldventures.dreamtrips.view.cell;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Optional;

@Layout(R.layout.adapter_item_photo)
public class PhotoCell extends AbstractCell<IFullScreenAvailableObject> {

    @InjectView(R.id.iv_bg)
    public ImageView imageView;

    @Optional
    @InjectView(R.id.user_photo)
    ImageView imageViewUser;
    @Optional
    @InjectView(R.id.user_location)
    TextView user_location;
    @Optional
    @InjectView(R.id.user_name)
    TextView user_name;
    @Optional
    @InjectView(R.id.title)
    TextView title;
    @Optional
    @InjectView(R.id.shot_location)
    TextView shot_location;

    @Inject
    UniversalImageLoader universalImageLoader;

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
        this.universalImageLoader.loadImage(getModelObject().getFSImage().getMedium().getUrl(), this.imageView, null, new SimpleImageLoadingListener());
    }

    @Override
    public void prepareForReuse() {
        imageView.setImageBitmap(null);
    }
}
