package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
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

    public SubPhotoAttachmentCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        setImage(Uri.parse(getModelObject().getImages().getUrl()), photo);

        itemView.setOnClickListener(v -> {
            if (cellDelegate != null) {
                cellDelegate.onCellClicked(getModelObject());
            }
        });

        itemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = itemView.getWidth();
                photo.getLayoutParams().width = width;
                photo.getLayoutParams().height = (int) (width / (float) getModelObject().getWidth() * getModelObject().getHeight());
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    itemView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        tag.setVisibility(getModelObject().getPhotoTagsCount() > 0 || !getModelObject().getPhotoTags().isEmpty() ? View.VISIBLE : View.GONE);
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
