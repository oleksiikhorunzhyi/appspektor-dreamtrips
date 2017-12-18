package com.worldventures.dreamtrips.modules.facebook.view.cell;

import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.modules.facebook.model.FacebookPhoto;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.GraphicUtils;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_photo_facebook)
public class FacebookPhotoCell extends BaseAbstractDelegateCell<FacebookPhoto, CellDelegate<FacebookPhoto>> {

   @InjectView(R.id.imageViewPhoto) SimpleDraweeView photo;
   @InjectView(R.id.pick) ImageView pick;

   public FacebookPhotoCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      final int size = photo.getResources().getDimensionPixelSize(R.dimen.photo_picker_size);
      photo.setController(GraphicUtils.provideFrescoResizingController(getModelObject().getUri(),
            photo.getController(), size, size));
      itemView.setOnClickListener(v -> {
         getModelObject().setChecked(!getModelObject().isChecked());
         getModelObject().setPickedTime(getModelObject().isChecked() ? System.currentTimeMillis() : -1);
         cellDelegate.onCellClicked(getModelObject());
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
   public boolean shouldInject() {
      return false;
   }
}
