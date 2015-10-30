package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.event.PhotoPickedEvent;
import com.worldventures.dreamtrips.modules.feed.model.PhotoGalleryModel;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_photo_pick)
public class PhotoGalleryCell extends AbstractCell<PhotoGalleryModel> {

    @InjectView(R.id.iv_photo)
    SimpleDraweeView photo;
    @InjectView(R.id.pick)
    ImageView pick;

    public PhotoGalleryCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        setImage(Uri.parse(getModelObject().getThumbnailPath()), photo);

        itemView.setOnClickListener(v -> {
            getModelObject().setChecked(!getModelObject().isChecked());

            getEventBus().post(new PhotoPickedEvent(getModelObject()));
        });

        updatePickState();
    }

    private void updatePickState() {
        if (getModelObject().isChecked()) {
            pick.setImageResource(R.drawable.add_photo_icon_selected);
        } else {
            pick.setImageResource(R.drawable.add_photo_icon);
        }
    }

    @Override
    public void prepareForReuse() {

    }

    private void setImage(Uri uri, SimpleDraweeView draweeView) {
        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder();

        if (draweeView.getTag() != null) {
            if (uri.equals(draweeView.getTag())) {
                return;
            }
            builder.setLowResImageRequest(ImageRequest.fromUri((Uri) draweeView.getTag()));
        }

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(200, 200))
                .build();
        builder.setOldController(draweeView.getController());
        builder.setImageRequest(request);

        DraweeController dc = builder.build();
        draweeView.setController(dc);
        draweeView.setTag(uri);
    }
}
