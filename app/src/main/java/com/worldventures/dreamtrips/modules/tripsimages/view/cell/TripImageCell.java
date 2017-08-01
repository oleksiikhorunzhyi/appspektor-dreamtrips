package com.worldventures.dreamtrips.modules.tripsimages.view.cell;

import android.graphics.PointF;
import android.net.Uri;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoMediaEntity;
import com.worldventures.dreamtrips.modules.tripsimages.view.ImageUtils;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_photo)
public class TripImageCell extends AbstractDelegateCell<PhotoMediaEntity, CellDelegate<PhotoMediaEntity>> {

   @InjectView(R.id.imageViewPhoto) SimpleDraweeView draweeView;

   public TripImageCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      draweeView.getHierarchy().setActualImageFocusPoint(new PointF(0.5F, 0F));
      draweeView.setImageURI(getThumbUrl());
      itemView.setOnClickListener(v -> cellDelegate.onCellClicked(getModelObject()));
   }

   public String getThumbUrl() {
      int dimensionPixelSize = itemView.getResources().getDimensionPixelSize(R.dimen.photo_thumb_size);
      return ImageUtils.getParametrizedUrl(getModelObject().getUrl(), dimensionPixelSize, dimensionPixelSize);
   }

   @Override
   public void prepareForReuse() {
      this.draweeView.setImageURI(Uri.EMPTY);
   }
}
