package com.worldventures.dreamtrips.view.cell;

import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_photo)
public class PhotoCell extends AbstractCell<IFullScreenAvailableObject> {

    @InjectView(R.id.iv_bg)
    public ImageView imageView;

    @Inject
    UniversalImageLoader universalImageLoader;

    public PhotoCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        this.universalImageLoader.loadImage(getModelObject().getFSImage().getMedium().getUrl(), this.imageView, null, new SimpleImageLoadingListener());
    }

    @Override
    public void prepareForReuse() {
        imageView.setImageBitmap(null);
    }
}
