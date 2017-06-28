package com.worldventures.dreamtrips.modules.common.presenter;

import android.net.Uri;
import android.text.TextUtils;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.command.DownloadFileCommand;
import com.worldventures.dreamtrips.modules.common.delegate.DownloadFileInteractor;
import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.facebook.FacebookHelper;
import com.worldventures.dreamtrips.modules.video.utils.CachedModelHelper;

import java.io.File;
import java.util.Collection;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class SharePresenter extends Presenter<SharePresenter.View> {

   @Inject DownloadFileInteractor downloadFileInteractor;

   @Inject FacebookHelper facebookHelper;
   @Inject CachedModelHelper cachedModelHelper;

   private CallbackManager facebookCallbackManager;

   @Override
   public void takeView(View view) {
      super.takeView(view);
      facebookCallbackManager = CallbackManager.Factory.create();
      view.setFacebookCallbackManager(facebookCallbackManager);
   }

   public void create(String imageUrl, String shareLink, String text, String type) {
      text = text == null ? "" : text;
      if (type.equals(ShareType.FACEBOOK)) {
         shareFacebookDialog(imageUrl, shareLink, text);
      } else if (type.equals(ShareType.TWITTER)) {
         if (TextUtils.isEmpty(imageUrl)) {
            view.shareTwitterDialog(null, shareLink, text);
         } else {
            File file = new File(cachedModelHelper.getExternalFilePath(imageUrl));
            if (file.exists()) {
               Uri parse = Uri.fromFile(file);
               view.shareTwitterDialog(parse, shareLink, text);
            } else {
               downloadFile(imageUrl, shareLink, text);
            }
         }
      }
   }

   private void shareFacebookDialog(String url, String link, String text) {
      FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
         @Override
         public void onSuccess(Sharer.Result loginResult) {
            view.informUser(R.string.fab_posted);
            view.back();
         }

         @Override
         public void onCancel() {
            onFacebookSharingFailed();
         }

         @Override
         public void onError(FacebookException error) {
            onFacebookSharingFailed();
         }
      };
      if (facebookHelper.isLoggedIn()) {
         view.shareFacebookDialog(url, link, text, shareCallback);
         return;
      }
      LoginManager.getInstance().registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
         @Override
         public void onSuccess(final LoginResult loginResult) {
            view.shareFacebookDialog(url, link, text, shareCallback);
         }

         @Override
         public void onCancel() {
            onFacebookLoginFailed();
         }

         @Override
         public void onError(final FacebookException exception) {
            onFacebookLoginFailed();
         }
      });
      view.loginToFacebook(FacebookHelper.LOGIN_PERMISSIONS);
   }

   private void onFacebookLoginFailed() {
      view.informUser(R.string.facebook_login_error);
      view.back();
   }

   private void onFacebookSharingFailed() {
      view.informUser(R.string.facebook_posting_error);
      view.back();
   }

   private void downloadFile(String url, final String shareLink, final String text) {
      File cacheFile = new File(cachedModelHelper.getExternalFilePath(url));
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

      void setFacebookCallbackManager(CallbackManager callbackManager);

      void loginToFacebook(Collection<String> permissions);

      void shareFacebookDialog(String url, String link, String text, FacebookCallback<Sharer.Result> callback);

      void back();
   }
}
