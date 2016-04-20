package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.modules.common.event.PhotoPickedEvent;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_photo_pick)
public class PhotoGalleryCell extends AbstractDelegateCell<PhotoGalleryModel, CellDelegate<PhotoGalleryModel>> {

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
            //
            if (cellDelegate != null) {
                cellDelegate.onCellClicked(getModelObject());
            } else {
                getEventBus().post(new PhotoPickedEvent(getModelObject()));
            }
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
        if (draweeView.getTag() != null) {
            if (uri.equals(draweeView.getTag())) {
                return;
            }
        }

        PipelineDraweeController controller = GraphicUtils.provideFrescoResizingController(uri, draweeView.getController(), 100, 100);
        draweeView.setController(controller);
        draweeView.setTag(uri);
    }
}
