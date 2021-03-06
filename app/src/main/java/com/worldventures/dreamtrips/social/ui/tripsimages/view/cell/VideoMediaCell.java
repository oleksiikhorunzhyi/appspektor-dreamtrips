package com.worldventures.dreamtrips.social.ui.tripsimages.view.cell;

import android.graphics.PointF;
import android.net.Uri;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.core.utils.ImageUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.VideoMediaEntity;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_media_video)
public class VideoMediaCell extends BaseAbstractDelegateCell<VideoMediaEntity, CellDelegate<VideoMediaEntity>> {

   @InjectView(R.id.videoThumbnail) SimpleDraweeView videoThumbnail;

   public VideoMediaCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      videoThumbnail.getHierarchy().setActualImageFocusPoint(new PointF(0.5F, 0F));
      videoThumbnail.setImageURI(getThumbUrl());
      itemView.setOnClickListener(v -> cellDelegate.onCellClicked(getModelObject()));
   }

   public String getThumbUrl() {
      int dimensionPixelSize = itemView.getResources().getDimensionPixelSize(R.dimen.photo_thumb_size);
      return ImageUtils.getParametrizedUrl(getModelObject().getItem()
            .getThumbnail(), dimensionPixelSize, dimensionPixelSize);
   }

   @Override
   public void prepareForReuse() {
      this.videoThumbnail.setImageURI(Uri.EMPTY);
   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}
