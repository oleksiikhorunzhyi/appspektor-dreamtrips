package com.worldventures.dreamtrips.modules.facebook.view.cell;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.event.PhotoPickedEvent;
import com.worldventures.dreamtrips.modules.facebook.model.FacebookPhoto;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_photo_facebook)
public class FacebookPhotoCell extends AbstractCell<FacebookPhoto> {

   @InjectView(R.id.imageViewPhoto) SimpleDraweeView ivBg;
   @InjectView(R.id.pick) ImageView pick;

   public FacebookPhotoCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      ivBg.setImageURI(Uri.parse(getModelObject().getImageUri()));

      itemView.setOnClickListener(v -> {
         getModelObject().setChecked(!getModelObject().isChecked());
         getModelObject().setPickedTime(getModelObject().isChecked() ? System.currentTimeMillis() : -1);

         getEventBus().post(new PhotoPickedEvent(getModelObject()));
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
}
