package com.worldventures.dreamtrips.modules.facebook.view.cell;

import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.facebook.model.FacebookAlbum;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_facebook_album)
public class FacebookAlbumCell extends AbstractCell<FacebookAlbum> {

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
      String accessToken = Session.getActiveSession().getAccessToken();
      ivBg.setImageURI(Uri.parse(getModelObject().getCoverUrl(accessToken)));
   }
}
