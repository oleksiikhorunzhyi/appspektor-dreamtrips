package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.net.Uri;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
import com.worldventures.dreamtrips.modules.feed.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.feed.presenter.PostPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_photo_pick)
public class PhotoGalleryCell extends AbstractCell<PhotoGalleryModel> {

    @InjectView(R.id.iv_photo)
    SimpleDraweeView photo;

    public PhotoGalleryCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        setImage(Uri.parse(getModelObject().getThumbnailPath()), photo);

        itemView.setOnClickListener(v -> {
            ChosenImage chosenImage = new ChosenImage();
            chosenImage.setFileThumbnail(getModelObject().getThumbnailPath());
            chosenImage.setFilePathOriginal(getModelObject().getOriginalPath());
            getEventBus().post(new ImagePickedEvent(PickImageDelegate.REQUEST_PICK_PICTURE,
                    PostPresenter.REQUESTER_ID, new ChosenImage[]{chosenImage}));
        });
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
