package com.worldventures.dreamtrips.wallet.ui.wizard.card_details;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableAddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.FetchDefaultCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetDefaultAddressCommand;
import com.worldventures.dreamtrips.wallet.service.command.SaveCardDetailsDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.CardListPath;
import com.worldventures.dreamtrips.wallet.util.BankCardHelper;
import com.worldventures.dreamtrips.wallet.util.CardUtils;
import com.worldventures.dreamtrips.wallet.util.FormatException;
import com.worldventures.dreamtrips.wallet.util.SmartCardInteractorHelper;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import io.techery.janet.helper.ActionStateToActionTransformer;
import rx.Observable;
import timber.log.Timber;

public class AddCardDetailsPresenter extends WalletPresenter<AddCardDetailsPresenter.Screen, Parcelable> {

   @Inject LocaleHelper localeHelper;
   @Inject BankCardHelper bankCardHelper;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject SmartCardInteractorHelper smartCardInteractorHelper;

   private final BankCard bankCard;

   public AddCardDetailsPresenter(Context context, Injector injector, BankCard bankCard) {
      super(context, injector);

      this.bankCard = bankCard;
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);

      getView().cardBankInfo(bankCard);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      connectToDefaultCardPipe();
      connectToDefaultAddressPipe();
      connectToSaveCardDetailsPipe();
      loadDataFromDevice();
   }

   private void connectToDefaultCardPipe() {
      smartCardInteractor.fetchDefaultCardCommandPipe().clearReplays();
      smartCardInteractorHelper.sendSingleDefaultCardTask(bankCard -> {
         getView().defaultPaymentCard(!CardUtils.isRealCard(bankCard));
         getView().setAsDefaultPaymentCardCondition().compose(bindView()).subscribe(this::onSetAsDefaultCard);
      }, bindViewIoToMainComposer());
   }

   private void connectToDefaultAddressPipe() {
      smartCardInteractor.getDefaultAddressCommandPipe()
            .observeWithReplay()
            .compose(new ActionStateToActionTransformer<>())
            .map(command -> {
               AddressInfo addressInfo = command.getResult();
               if (addressInfo == null) return null;

               return ImmutableAddressInfoWithLocale.builder()
                     .addressInfo(addressInfo)
                     .locale(localeHelper.getDefaultLocale())
                     .build();
            })
            .compose(bindViewIoToMainComposer())
            .subscribe(this::setDefaultAddress, throwable -> {
               Timber.e(throwable, "Fail to use GetDefaultAddressCommand");
               // TODO: 8/24/16 add error handling
            });
   }

   private void setDefaultAddress(@Nullable AddressInfoWithLocale defaultAddress) {
      if (defaultAddress == null) return;
      getView().defaultAddress(defaultAddress);
   }

   private void connectToSaveCardDetailsPipe() {
      smartCardInteractor.saveCardDetailsDataPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(smartCardInteractor.saveCardDetailsDataPipe()))
            .subscribe(OperationSubscriberWrapper.<SaveCardDetailsDataCommand>forView(getView().provideOperationDelegate())
                  .onStart("")
                  .onSuccess(saveCardDetailsDataCommand -> Flow.get(getContext())
                        .setHistory(History.single(new CardListPath()), Flow.Direction.REPLACE))
                  .onFail(throwable -> {
                     Context context = getContext();
                     String msg = throwable.getCause() instanceof FormatException ? context.getString(R.string.wallet_add_card_details_error_message) : context
                           .getString(R.string.error_something_went_wrong);

                     return new OperationSubscriberWrapper.MessageActionHolder<>(msg, null);
                  })
                  .wrap());
   }

   private void loadDataFromDevice() {
      smartCardInteractor.fetchDefaultCardCommandPipe().send(new FetchDefaultCardCommand());
      smartCardInteractor.getDefaultAddressCommandPipe().send(new GetDefaultAddressCommand());
   }

   public void onCardInfoConfirmed(AddressInfo addressInfo, String cvv, String nickname, boolean useDefaultAddress, boolean setAsDefaultAddress, boolean setAsDefaultCard) {
      smartCardInteractor.saveCardDetailsDataPipe()
            .send(new SaveCardDetailsDataCommand(bankCard, addressInfo, nickname, cvv, useDefaultAddress, setAsDefaultAddress, setAsDefaultCard));
   }

   private void onSetAsDefaultCard(boolean setDefaultCard) {
      if (!setDefaultCard) return;

      smartCardInteractorHelper.sendSingleDefaultCardTask(defaultCard -> {
         if (!CardUtils.isRealCard(defaultCard)) return;
         // TODO: 9/7/16 (Sprint 4) we should use bank name instead of fin service
         getView().showChangeCardDialog(bankCardHelper.financialServiceWithCardNumber(defaultCard));
      }, bindViewIoToMainComposer());
   }

   public void defaultCardDialogConfirmed(boolean confirmed) {
      if (!confirmed) getView().defaultPaymentCard(false);
   }

   public void goBack() {
      Flow.get(getContext()).goBack();
   }

   public interface Screen extends WalletScreen {
      void cardBankInfo(BankCard bankCard);

      void defaultAddress(AddressInfoWithLocale defaultAddress);

      void defaultPaymentCard(boolean defaultPaymentCard);

      void showChangeCardDialog(@NonNull String bankCardName);

      Observable<Boolean> setAsDefaultPaymentCardCondition();
   }

}
