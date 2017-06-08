package com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.photos;


import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.BaseHolder;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WalletFacebookPhotoHolder extends BaseHolder<WalletFacebookPhotoModel> {

   @InjectView(R.id.imageViewPhoto) SimpleDraweeView ivBg;
   @InjectView(R.id.pick) ImageView pick;
   private WalletFacebookPhotoModel model;

   public WalletFacebookPhotoHolder(View itemView) {
      super(itemView);
      ButterKnife.inject(this, itemView);
   }

   @Override
   public void setData(WalletFacebookPhotoModel data) {
      this.model = data;
      ivBg.setImageURI(Uri.parse(model.getImageUri()));
      updatePickState();
   }

   private void updatePickState() {
      if (model.isChecked()) {
         pick.setImageResource(R.drawable.add_photo_icon_selected);
      } else {
         pick.setImageResource(R.drawable.add_photo_icon);
      }
   }
}
