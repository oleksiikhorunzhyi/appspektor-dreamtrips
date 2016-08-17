package com.worldventures.dreamtrips.modules.membership.view.cell;

import android.graphics.PointF;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.membership.model.TemplatePhoto;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_bucket_photo_cell)
public class TemplatePhotoCell extends AbstractCell<TemplatePhoto> {

   @InjectView(R.id.imageViewPhoto) protected SimpleDraweeView imageViewPhoto;

   public TemplatePhotoCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      imageViewPhoto.getHierarchy().setActualImageFocusPoint(new PointF(0.0f, 0.0f));
      imageViewPhoto.setImageURI(getModelObject().getPath());
   }
}
