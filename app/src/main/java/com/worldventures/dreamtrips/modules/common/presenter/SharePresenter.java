package com.worldventures.dreamtrips.modules.common.presenter;

import android.net.Uri;
import android.text.TextUtils;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.simple.BigBinaryRequest;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

import java.io.File;
import java.io.InputStream;

public class SharePresenter extends Presenter<SharePresenter.View> {

   public void create(String imageUrl, String shareLink, String text, String type) {
      text = text == null ? "" : text;
      if (type.equals(ShareType.FACEBOOK)) {
         view.shareFBDialog(imageUrl, shareLink, text);
      } else if (type.equals(ShareType.TWITTER)) {
         if (TextUtils.isEmpty(imageUrl)) {
            view.shareTwitterDialog(null, shareLink, text);
         } else {
            File file = new File(CachedEntity.getExternalFilePath(context, imageUrl));
            if (file.exists()) {
               Uri parse = Uri.fromFile(file);
               view.shareTwitterDialog(parse, shareLink, text);
            } else {
               downloadFile(imageUrl, shareLink, text);
            }
         }
      }
   }

   private void downloadFile(String url, final String shareLink, final String text) {
      File cacheFile = new File(CachedEntity.getExternalFilePath(context, url));
      BigBinaryRequest bigBinaryRequest = new BigBinaryRequest(url, cacheFile);

      dreamSpiceManager.execute(bigBinaryRequest, url, DurationInMillis.ALWAYS_RETURNED, new RequestListener<InputStream>() {
         @Override
         public void onRequestFailure(SpiceException spiceException) {
            view.informUser(R.string.share_error);
         }

         @Override
         public void onRequestSuccess(InputStream inputStream) {
            Uri parse = Uri.fromFile(cacheFile);
            view.shareTwitterDialog(parse, shareLink, text);
         }
      });
   }

   public interface View extends Presenter.View {
      void shareTwitterDialog(Uri url, String link, String text);

      void shareFBDialog(String url, String link, String text);
   }
}
