package com.worldventures.dreamtrips.social.ui.feed.view.cell;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.GraphicUtils;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;

import butterknife.InjectView;

@Layout(R.layout.adapter_feed_item_photo_atachment)
public class SubPhotoAttachmentCell extends BaseAbstractDelegateCell<Photo, CellDelegate<Photo>> {

   @InjectView(R.id.iv_photo) SimpleDraweeView photo;
   @InjectView(R.id.tag) ImageView tag;

   public SubPhotoAttachmentCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      photo.setAspectRatio(getModelObject().getWidth() / (float) getModelObject().getHeight());
      itemView.setOnClickListener(v -> {
         if (cellDelegate != null) {
            cellDelegate.onCellClicked(getModelObject());
         }
      });
      tag.setOnClickListener(v -> {
         if (cellDelegate != null) {
            cellDelegate.onCellClicked(getModelObject());
         }
      });

      setImage(Uri.parse(getModelObject().getImagePath()), photo);
      tag.setVisibility(getModelObject().getPhotoTagsCount() > 0 || !getModelObject().getPhotoTags()
            .isEmpty() ? View.VISIBLE : View.GONE);
   }

   private void setImage(Uri uri, SimpleDraweeView draweeView) {
      if (uri.equals(draweeView.getTag())) {
         return;
      }

      draweeView.setController(GraphicUtils.provideFrescoResizingController(uri, draweeView.getController()));
      draweeView.setTag(uri);
   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}
