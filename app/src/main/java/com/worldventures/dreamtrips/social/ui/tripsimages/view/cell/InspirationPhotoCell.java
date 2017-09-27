package com.worldventures.dreamtrips.social.ui.tripsimages.view.cell;

import android.graphics.PointF;
import android.net.Uri;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.social.util.ImageUtils;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_photo)
public class InspirationPhotoCell extends AbstractDelegateCell<Inspiration, CellDelegate<Inspiration>> {

   @InjectView(R.id.imageViewPhoto) SimpleDraweeView draweeView;

   public InspirationPhotoCell(View view) {
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

   @Override
   public boolean shouldInject() {
      return false;
   }
}
