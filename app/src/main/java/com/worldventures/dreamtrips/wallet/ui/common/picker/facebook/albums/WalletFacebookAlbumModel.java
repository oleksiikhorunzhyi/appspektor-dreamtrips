package com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.albums;

import android.net.Uri;

import com.facebook.AccessToken;
import com.worldventures.dreamtrips.modules.facebook.model.FacebookCoverPhoto;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.WalletPickerHolderFactory;
import com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.WalletFacebookPickerModel;


public class WalletFacebookAlbumModel extends WalletFacebookPickerModel {
   private static final String GRAPH_URL = "https://graph.facebook.com/";
   private static final String COVER_URL = "/picture?type=album&access_token=";
   private static final String PARAMS = "&type=normal";
   private String id;
   private String name;
   private int count;
   private FacebookCoverPhoto coverPhoto;

   public WalletFacebookAlbumModel(String id, String name, int count, FacebookCoverPhoto coverPhoto) {
      this.id = id;
      this.name = name;
      this.count = count;
      this.coverPhoto = coverPhoto;
   }

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public int getCount() {
      return count;
   }

   @Override
   public int type(WalletPickerHolderFactory typeFactory) {
      return typeFactory.type(this);
   }

   @Override
   public Type getType() {
      throw new UnsupportedOperationException("WalletFacebookAlbumModel doesn't support type");
   }

   @Override
   public boolean isChecked() {
      throw new UnsupportedOperationException("WalletFacebookAlbumModel doesn't support check");
   }

   @Override
   public void setChecked(boolean checked) {
      throw new UnsupportedOperationException("WalletFacebookAlbumModel doesn't support check");
   }

   @Override
   public Uri getUri() {
      return Uri.parse(GRAPH_URL +
            (coverPhoto == null ? "" : coverPhoto.getId()) +
            COVER_URL +
            AccessToken.getCurrentAccessToken().getToken() +
            PARAMS);
   }

   @Override
   public String getAbsolutePath() {
      throw new UnsupportedOperationException("WalletFacebookAlbumModel doesn't support path");
   }

   @Override
   public long getPickedTime() {
      throw new UnsupportedOperationException("WalletFacebookAlbumModel doesn't support pick");
   }

   @Override
   public long getDateTaken() {
      throw new UnsupportedOperationException("WalletFacebookAlbumModel doesn't support taken date");
   }

   @Override
   public void setPickedTime(long pickedTime) {
      throw new UnsupportedOperationException("WalletFacebookAlbumModel doesn't support pick");
   }
}
