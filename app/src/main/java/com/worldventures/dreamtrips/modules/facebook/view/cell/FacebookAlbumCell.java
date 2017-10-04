package com.worldventures.dreamtrips.modules.facebook.view.cell;

import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.modules.facebook.model.FacebookAlbum;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractCell;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_facebook_album)
public class FacebookAlbumCell extends BaseAbstractCell<FacebookAlbum> {

   @InjectView(R.id.iv_bg) SimpleDraweeView ivBg;
   @InjectView(R.id.tv_album_title) TextView tvTitle;
   @InjectView(R.id.tv_count) TextView tvCount;

   public FacebookAlbumCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      tvTitle.setText(getModelObject().getName());
      tvCount.setText(getModelObject().getCount() + "");
      String accessToken = AccessToken.getCurrentAccessToken().getToken();
      ivBg.setImageURI(Uri.parse(getModelObject().getCoverUrl(accessToken)));
   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}
