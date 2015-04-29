package com.worldventures.dreamtrips.modules.facebook.view.cell;

import android.net.Uri;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.facebook.model.FacebookPhoto;

import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_photo)
public class FacebookPhotoCell extends AbstractCell<FacebookPhoto> {

    @InjectView(R.id.imageViewPhoto)
    protected SimpleDraweeView ivBg;

    public FacebookPhotoCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        List<FacebookPhoto.ImageSource> is = getModelObject().getImageSources();
        String picture;
        if (is.size() > 2) {
            picture = is.get(is.size() / 2 + 1).getSource();
        } else {
            picture = getModelObject().getPicture();
        }
        ivBg.setImageURI(Uri.parse(picture));
    }

    @Override
    public void prepareForReuse() {

    }
}