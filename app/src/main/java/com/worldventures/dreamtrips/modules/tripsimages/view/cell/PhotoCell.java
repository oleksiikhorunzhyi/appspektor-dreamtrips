package com.worldventures.dreamtrips.modules.tripsimages.view.cell;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;

import butterknife.InjectView;
import butterknife.Optional;

@Layout(R.layout.adapter_item_photo)
public class PhotoCell extends AbstractCell<IFullScreenAvailableObject> {

    @InjectView(R.id.imageViewPhoto)
    protected SimpleDraweeView draweeViewPhoto;
    @Optional
    @InjectView(R.id.user_photo)
    protected ImageView imageViewUser;
    @Optional
    @InjectView(R.id.user_location)
    protected TextView userLocation;
    @Optional
    @InjectView(R.id.user_name)
    protected TextView userName;
    @Optional
    @InjectView(R.id.title)
    protected TextView title;
    @Optional
    @InjectView(R.id.shot_location)
    protected TextView shotLocation;

    public PhotoCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        if (imageViewUser != null) {
            this.userLocation.setText(getModelObject().getUserLocation());
            this.shotLocation.setText(getModelObject().getPhotoLocation());
            this.title.setText(getModelObject().getFSTitle());
            this.userName.setText(getModelObject().getUserName());
        }

        Image fsImage = getModelObject().getFSImage();

        if (fsImage.isFromFile()) {
            draweeViewPhoto.setImageURI(Uri.parse(fsImage.getUrl()));
        } else {
            draweeViewPhoto.setImageURI(Uri.parse(fsImage.getThumbUrl(itemView.getResources())));
        }
    }

    @Override
    public void prepareForReuse() {
        //nothing to do her
    }
}
