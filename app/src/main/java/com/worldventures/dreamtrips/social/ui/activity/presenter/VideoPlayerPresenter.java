package com.worldventures.dreamtrips.social.ui.activity.presenter;

import android.content.Intent;
import android.content.IntentFilter;

import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.video.utils.HeadphonesPlugReceiver;

public class VideoPlayerPresenter<T extends VideoPlayerPresenter.View> extends ActivityPresenter<T> {

   private HeadphonesPlugReceiver headphonesPlugReceiver;

   @Override
   public void takeView(T view) {
      super.takeView(view);
      registerHeadphonesPlugReceiver();
   }

   @Override
   public void dropView() {
      super.dropView();
      unregisterHeadphonesPlugReceiver();
   }

   private void registerHeadphonesPlugReceiver() {
      headphonesPlugReceiver = new HeadphonesPlugReceiver() {
         @Override
         protected void onHeadphonesUnPlugged() {
            view.onHeadphonesUnPlugged();
         }
      };
      IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
      context.registerReceiver(headphonesPlugReceiver, filter);
   }

   private void unregisterHeadphonesPlugReceiver() {
      if (headphonesPlugReceiver != null) {
         context.unregisterReceiver(headphonesPlugReceiver);
      }
   }

   public interface View extends ActivityPresenter.View {
      void onHeadphonesUnPlugged();
   }
}