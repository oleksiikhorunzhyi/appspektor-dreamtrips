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
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Inspiration;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_photo)
public class InspirationPhotoCell extends BaseAbstractDelegateCell<Inspiration, CellDelegate<Inspiration>> {

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
