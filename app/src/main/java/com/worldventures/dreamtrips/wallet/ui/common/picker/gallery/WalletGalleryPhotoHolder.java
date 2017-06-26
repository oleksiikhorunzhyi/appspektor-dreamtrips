package com.worldventures.dreamtrips.wallet.ui.common.picker.gallery;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.BaseHolder;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class WalletGalleryPhotoHolder extends BaseHolder<WalletGalleryPhotoModel> {
   @InjectView(R.id.iv_photo) SimpleDraweeView photo;
   @InjectView(R.id.pick) ImageView pick;
   private WalletGalleryPhotoModel model;

   public WalletGalleryPhotoHolder(View itemView) {
      super(itemView);
      ButterKnife.inject(this, itemView);
   }

   @Override
   public void setData(WalletGalleryPhotoModel data) {
      this.model = data;
      setImage(Uri.parse(model.getImageUri()), photo);
      updatePickState();
   }

   private void updatePickState() {
      if (model.isChecked()) {
         pick.setImageResource(R.drawable.add_photo_icon_selected);
      } else {
         pick.setImageResource(R.drawable.add_photo_icon);
      }
   }

   private void setImage(Uri uri, SimpleDraweeView draweeView) {
      if (draweeView.getTag() != null) {
         if (uri.equals(draweeView.getTag())) {
            return;
         }
      }

      PipelineDraweeController controller = GraphicUtils.provideFrescoResizingController(uri, draweeView.getController(), 100, 100);
      draweeView.setController(controller);
      draweeView.setTag(uri);
   }
}
