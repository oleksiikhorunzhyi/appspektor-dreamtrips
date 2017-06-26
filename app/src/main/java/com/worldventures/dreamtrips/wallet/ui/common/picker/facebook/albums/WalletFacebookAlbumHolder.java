package com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.albums;


import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.BaseHolder;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WalletFacebookAlbumHolder extends BaseHolder<WalletFacebookAlbumModel> {

   @InjectView(R.id.iv_bg) SimpleDraweeView ivBg;
   @InjectView(R.id.tv_album_title) TextView tvTitle;
   @InjectView(R.id.tv_count) TextView tvCount;
   private WalletFacebookAlbumModel model;

   public WalletFacebookAlbumHolder(View itemView) {
      super(itemView);
      ButterKnife.inject(this, itemView);
   }

   @Override
   public void setData(WalletFacebookAlbumModel data) {
      this.model = data;
      tvTitle.setText(model.getName());
      tvCount.setText(String.valueOf(model.getCount()));
      ivBg.setImageURI(Uri.parse(model.getImageUri()));
   }
}
