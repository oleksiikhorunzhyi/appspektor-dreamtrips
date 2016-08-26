package com.worldventures.dreamtrips.wallet.ui.wizard.manual;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.wizard.card_alias.WizardCardNamePath;

import javax.inject.Inject;

import flow.Flow;
import timber.log.Timber;

public class WizardManualInputPresenter extends WalletPresenter<WizardManualInputPresenter.Screen, Parcelable> {
   @Inject WizardInteractor wizardInteractor;

   public WizardManualInputPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      wizardInteractor.createAndConnectActionPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.createAndConnectActionPipe()))
            .subscribe(OperationSubscriberWrapper.<CreateAndConnectToCardCommand>forView(view.provideOperationDelegate())
                  .onStart(getContext().getString(R.string.waller_wizard_scan_barcode_progress_label))
                  .onSuccess(getContext().getString(R.string.wallet_got_it_label),
                        command -> Flow.get(getContext()).set(new WizardCardNamePath(command.getCode()))
                  )
                  .onFail(throwable -> new OperationSubscriberWrapper.MessageActionHolder<>(getContext().getString(R.string.wallet_wizard_scid_validation_error),
                        command -> Timber.e("Could not connect to device")))
                  .wrap());
   }

   public void checkBarcode(String barcode) {
      wizardInteractor.createAndConnectActionPipe().send(new CreateAndConnectToCardCommand(barcode));
   }

   public void goBack() {
      Flow.get(getContext()).goBack();
   }

   public interface Screen extends WalletScreen {}
}
