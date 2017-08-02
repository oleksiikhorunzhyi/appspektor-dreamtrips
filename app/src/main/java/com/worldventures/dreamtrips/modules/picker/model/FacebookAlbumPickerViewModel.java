package com.worldventures.dreamtrips.modules.picker.model;

import android.net.Uri;

import com.facebook.AccessToken;
import com.worldventures.dreamtrips.modules.facebook.model.FacebookCoverPhoto;
import com.worldventures.dreamtrips.modules.picker.util.adapter.MediaPickerHolderFactory;


public class FacebookAlbumPickerViewModel extends FacebookMediaPickerViewModel<FacebookCoverPhoto> {
   private static final String GRAPH_URL = "https://graph.facebook.com/";
   private static final String COVER_URL = "/picture?type=album&access_token=";
   private static final String PARAMS = "&type=normal";
   private String id;
   private String name;
   private int count;

   public FacebookAlbumPickerViewModel(String id, String name, int count, FacebookCoverPhoto coverPhoto) {
      super(coverPhoto);
      this.id = id;
      this.name = name;
      this.count = count;
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
   public int type(MediaPickerHolderFactory typeFactory) {
      return typeFactory.type(this);
   }

   @Override
   public Type getType() {
      throw new UnsupportedOperationException("FacebookAlbumPickerViewModel doesn't support type");
   }

   @Override
   public boolean isChecked() {
      throw new UnsupportedOperationException("FacebookAlbumPickerViewModel doesn't support check");
   }

   @Override
   public void setChecked(boolean checked) {
      throw new UnsupportedOperationException("FacebookAlbumPickerViewModel doesn't support check");
   }

   @Override
   public String getAbsolutePath() {
      throw new UnsupportedOperationException("FacebookAlbumPickerViewModel doesn't support path");
   }

   @Override
   public long getPickedTime() {
      throw new UnsupportedOperationException("FacebookAlbumPickerViewModel doesn't support pick");
   }

   @Override
   public long getDateTaken() {
      throw new UnsupportedOperationException("FacebookAlbumPickerViewModel doesn't support taken date");
   }

   @Override
   public void setPickedTime(long pickedTime) {
      throw new UnsupportedOperationException("FacebookAlbumPickerViewModel doesn't support pick");
   }

   @Override
   public Uri getUriFromSource(FacebookCoverPhoto source) {
      return Uri.parse(GRAPH_URL +
            (source == null ? "" : source.getId()) +
            COVER_URL +
            AccessToken.getCurrentAccessToken().getToken() +
            PARAMS);
   }
}
