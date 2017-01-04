package com.worldventures.dreamtrips.modules.common.presenter;

import android.net.Uri;
import android.text.TextUtils;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.command.DownloadFileCommand;
import com.worldventures.dreamtrips.modules.common.delegate.DownloadFileInteractor;
import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class SharePresenter extends Presenter<SharePresenter.View> {

   @Inject DownloadFileInteractor downloadFileInteractor;

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
      downloadFileInteractor.getDownloadFileCommandPipe()
            .createObservable(new DownloadFileCommand(cacheFile, url))
            .subscribe(new ActionStateSubscriber<DownloadFileCommand>()
                  .onSuccess(downloadFileCommand -> {
                     Uri parse = Uri.fromFile(downloadFileCommand.getFile());
                     view.shareTwitterDialog(parse, shareLink, text);
                  })
                  .onFail((downloadFileCommand, throwable) -> view.informUser(R.string.share_error)));
   }

   public interface View extends Presenter.View {
      void shareTwitterDialog(Uri url, String link, String text);

      void shareFBDialog(String url, String link, String text);
   }
}
