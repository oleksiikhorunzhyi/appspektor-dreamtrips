package com.worldventures.dreamtrips.modules.common.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.techery.spares.annotations.Layout;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.SharePresenter;
import com.worldventures.dreamtrips.modules.common.view.bundle.ShareBundle;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;

import java.util.Collection;

@Layout(R.layout.share_fragment)
public class ShareFragment extends BaseFragmentWithArgs<SharePresenter, ShareBundle> implements SharePresenter.View {

   private CallbackManager facebookCallbackManager;

   @Override
   public void onViewCreated(View view, Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      ShareBundle bundleExtra = getArgs();
      String imageUrl = bundleExtra.getImageUrl();
      String shareUrl = bundleExtra.getShareUrl();
      String text = bundleExtra.getText();
      String type = bundleExtra.getShareType();
      getPresenter().create(imageUrl, shareUrl, text, type);
   }

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
   }

   @Override
   protected SharePresenter createPresenter(Bundle savedInstanceState) {
      return new SharePresenter();
   }

   @Override
   public void setFacebookCallbackManager(CallbackManager facebookCallbackManager) {
      this.facebookCallbackManager = facebookCallbackManager;
   }

   @Override
   public void loginToFacebook(Collection<String> permissions) {
      LoginManager.getInstance().logInWithReadPermissions(this, permissions);
   }

   @Override
   public void shareFacebookDialog(String pictureUrl, String linkToShare, String text, FacebookCallback<Sharer.Result> callback) {
      if (!ShareDialog.canShow(ShareLinkContent.class)) {
         return;
      }

      ShareLinkContent.Builder linkContent = new ShareLinkContent.Builder();
      // set link from arg or pictureUrl if arg is empty
      if (!TextUtils.isEmpty(linkToShare)) {
         linkContent.setContentUrl(Uri.parse(linkToShare));
      } else if (!TextUtils.isEmpty(pictureUrl)) {
         linkContent.setContentUrl(Uri.parse(pictureUrl));
      }
      if (!TextUtils.isEmpty(pictureUrl)) {
         linkContent.setImageUrl(Uri.parse(pictureUrl));
      }
      linkContent.setContentTitle(getString(R.string.app_name))
            .setContentDescription(text)
            .build();

      ShareDialog shareDialog = new ShareDialog(this);
      shareDialog.registerCallback(facebookCallbackManager, callback);
      shareDialog.show(linkContent.build());
   }

   @Override
   public void shareTwitterDialog(Uri imageUrl, String shareUrl, String text) {
      String url = shareUrl == null ? "" : shareUrl;

      if (!url.isEmpty()) {
         url += "\n";
      }

      TweetComposer.Builder builder = new TweetComposer.Builder(getActivity());
      builder.text(url + text);

      if (imageUrl != null) {
         builder.image(imageUrl);
      }

      builder.show();

      back();
   }

   @Override
   public void back() {
      getActivity().onBackPressed();
   }
}
