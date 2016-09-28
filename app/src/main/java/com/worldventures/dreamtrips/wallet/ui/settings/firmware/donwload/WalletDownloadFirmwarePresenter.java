package com.worldventures.dreamtrips.wallet.ui.settings.firmware.donwload;


import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.command.DownloadFileCommand;
import com.worldventures.dreamtrips.modules.common.delegate.DownloadFileInteractor;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareInfo;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import rx.Observable;

public class WalletDownloadFirmwarePresenter extends WalletPresenter<WalletDownloadFirmwarePresenter.Screen, Parcelable> {

   private final FirmwareInfo firmwareInfo;
   private final String filePath;

   @Inject DownloadFileInteractor fileInteractor;
   @Inject Navigator navigator;
   private DownloadFileCommand action;

   public WalletDownloadFirmwarePresenter(Context context, Injector injector, FirmwareInfo firmwareInfo, String filePath) {
      super(context, injector);
      this.firmwareInfo = firmwareInfo;
      this.filePath = filePath;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      action = new DownloadFileCommand(new File(filePath), firmwareInfo.downloadUrl());
      fileInteractor.getDownloadFileCommandPipe()
            .createObservable(action)
            .flatMap(it -> it.status == ActionState.Status.START ? Observable.just(it) :
                  Observable.just(it).delay(4, TimeUnit.SECONDS))//todo remove it
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationSubscriberWrapper.<DownloadFileCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(event -> getView().informSuccess())
                  .onFail(getContext().getString(R.string.smth_went_wrong), it -> navigator.goBack()).wrap());

   }

   void cancelDownload() {
      if (action != null) {
         fileInteractor.getDownloadFileCommandPipe().cancel(action);
      }
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      void informSuccess();
   }

}
