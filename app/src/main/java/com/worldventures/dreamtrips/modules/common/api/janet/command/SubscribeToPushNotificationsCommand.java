package com.worldventures.dreamtrips.modules.common.api.janet.command;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.push_notifications.SubscribeToPushNotificationsHttpAction;
import com.worldventures.dreamtrips.api.push_notifications.model.ImmutablePushSubscriptionParams;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.AppVersionNameBuilder;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

@CommandAction
public class SubscribeToPushNotificationsCommand extends Command<Void> implements InjectableAction {

   @Inject Janet janet;
   @Inject AppVersionNameBuilder appVersionNameBuilder;
   @Inject SnappyRepository snappyRepository;
   @Inject @ForApplication Context context;

   private final boolean isTokenChangedFromCallback;

   public SubscribeToPushNotificationsCommand(boolean isTokenChanged) {
      this.isTokenChangedFromCallback = isTokenChanged;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      String token = getGcmToken();
      if (!TextUtils.isEmpty(token) && (isTokenChanged(token) || isAppVersionChanged())) {
         janet.createPipe(SubscribeToPushNotificationsHttpAction.class, Schedulers.io())
               .createObservableResult(new SubscribeToPushNotificationsHttpAction(ImmutablePushSubscriptionParams.builder()
                     .token(token)
                     .appVersion(appVersionNameBuilder.getReleaseSemanticVersionName())
                     .osVersion(String.valueOf(Build.VERSION.SDK_INT))
                     .build()))
               .doOnNext(action -> updateSubscribeData(token))
               .subscribe(subscribeToPushNotificationsHttpAction -> {
                  callback.onSuccess(null);
               }, throwable -> {
                  resetToken();
               });
      }
   }

   private String getGcmToken() throws IOException {
      return InstanceID.getInstance(context)
            .getToken(context.getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
   }

   private boolean isTokenChanged(String token) {
      return !token.equals(snappyRepository.getGcmRegToken()) || isTokenChangedFromCallback;
   }

   private boolean isAppVersionChanged() {
      return TextUtils.isEmpty(snappyRepository.getLastSyncAppVersion()) || !snappyRepository.getLastSyncAppVersion()
            .equals(appVersionNameBuilder.getReleaseSemanticVersionName());
   }

   private void updateSubscribeData(String token) {
      snappyRepository.setGcmRegToken(token);
      snappyRepository.setLastSyncAppVersion(appVersionNameBuilder.getReleaseSemanticVersionName());
   }

   private void resetToken() {
      snappyRepository.setGcmRegToken(null);
   }
}
