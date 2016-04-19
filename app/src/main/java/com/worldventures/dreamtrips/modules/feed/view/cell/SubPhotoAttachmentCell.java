package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import butterknife.InjectView;

@Layout(R.layout.adapter_feed_item_photo_atachment)
public class SubPhotoAttachmentCell extends AbstractDelegateCell<Photo, CellDelegate<Photo>> {

    @InjectView(R.id.iv_photo)
    SimpleDraweeView photo;
    @InjectView(R.id.tag)
    protected ImageView tag;
    int cellWidth;

    public SubPhotoAttachmentCell(View view) {
        super(view);
        itemView.post(() -> cellWidth = (itemView.getWidth()));
    }

    @Override
    protected void syncUIStateWithModel() {
        if (cellWidth > 0) {
            itemView.setVisibility(View.VISIBLE);
            photo.getLayoutParams().width = cellWidth;
            photo.getLayoutParams().height = calculateHeight();
            photo.requestLayout();

            itemView.setOnClickListener(v -> {
                if (cellDelegate != null) {
                    cellDelegate.onCellClicked(getModelObject());
                }
            });

            itemView.post(() -> {
                setImage(Uri.parse(getModelObject().getImages().getUrl()), photo);
                tag.setVisibility(getModelObject().getPhotoTagsCount() > 0 || !getModelObject().getPhotoTags().isEmpty() ? View.VISIBLE : View.GONE);
            });
        } else {
            itemView.setVisibility(View.INVISIBLE);
            itemView.post(this::syncUIStateWithModel);
        }
    }

    private int calculateHeight() {
        int calculated = (int) (cellWidth / (float) getModelObject().getWidth() * getModelObject().getHeight());
        int maxAvailable = cellWidth / 4 * 5;
        return Math.min(calculated, maxAvailable);
    }

    @Override
    public void prepareForReuse() {

    }

    private void setImage(Uri uri, SimpleDraweeView draweeView) {
        if (uri.equals(draweeView.getTag())) {
            return;
        }

        draweeView.setController(GraphicUtils.provideFrescoResizingController(uri, draweeView.getController(), 100, 100));
        draweeView.setTag(uri);
    }
}
