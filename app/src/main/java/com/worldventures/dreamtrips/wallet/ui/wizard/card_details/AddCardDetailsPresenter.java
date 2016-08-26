package com.worldventures.dreamtrips.wallet.ui.wizard.card_details;

import android.content.Context;
import android.os.Parcelable;

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
import com.worldventures.dreamtrips.wallet.util.CardUtils;
import com.worldventures.dreamtrips.wallet.util.FormatException;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.helper.ActionStateToActionTransformer;
import timber.log.Timber;
import rx.Observable;

public class AddCardDetailsPresenter extends WalletPresenter<AddCardDetailsPresenter.Screen, Parcelable> {

   @Inject LocaleHelper localeHelper;
   @Inject SmartCardInteractor smartCardInteractor;

   private final BankCard bankCard;

   public AddCardDetailsPresenter(Context context, Injector injector, BankCard bankCard) {
      super(context, injector);

      this.bankCard = bankCard;
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);

      getView().setCardBankInfo(bankCard);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      subscribePipes();
      loadDataFromDevice();
   }

   private void loadDataFromDevice() {
      smartCardInteractor.fetchDefaultCardCommandActionPipe().send(FetchDefaultCardCommand.fetch(true));
      smartCardInteractor.getDefaultAddressCommandPipe().send(new GetDefaultAddressCommand());
   }

   private void subscribePipes() {
      smartCardInteractor.fetchDefaultCardCommandActionPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<FetchDefaultCardCommand>()
                  .onSuccess(command -> {
                     getView().setAsDefaultPaymentCard(!CardUtils.isRealCardId(command.getResult()));
                     getView().setAsDefaultPaymentCardCondition().compose(bindView()).subscribe(this::onSetAsDefaultCard);
                  }));

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
            .subscribe(addressInfoWithLocale -> {
               if (addressInfoWithLocale == null) {
                  getView().hideDefaultAddressCheckbox();
               } else {
                  getView().useDefaultAddress(addressInfoWithLocale);
               }
            }, throwable -> {
               Timber.e(throwable, "Fail to use GetDefaultAddressCommand");
               // TODO: 8/24/16 add error handling
            });

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

   public void useDefaultAddressRequirement(boolean useDefaultAddress) {
      if (useDefaultAddress) getView().showDefaultAddress();
      else getView().showAddressFields();
   }

   public void onCardInfoConfirmed(AddressInfo addressInfo, String cvv, String nickname, boolean useDefaultAddress, boolean setAsDefaultAddress) {
      smartCardInteractor.saveCardDetailsDataPipe()
            .send(new SaveCardDetailsDataCommand(bankCard, addressInfo, nickname, cvv, useDefaultAddress, setAsDefaultAddress));
   }

   private void onSetAsDefaultCard(boolean setDefaultCard){
      if (!setDefaultCard) return;
      //todo replace it
      getView().showDefaultCardDialog("DEFAULT CARD NAME");
   }

   public void defaultCardDialogConfirmed(boolean confirmed) {
      if (!confirmed) getView().setAsDefaultPaymentCard(false);
   }

   public void goBack() {
      Flow.get(getContext()).goBack();
   }

   public interface Screen extends WalletScreen {
      void showDefaultAddress();

      void showAddressFields();

      void hideDefaultAddressCheckbox();

      void setCardBankInfo(BankCard bankCard);

      void useDefaultAddress(AddressInfoWithLocale defaultAddressInfo);

      void setAsDefaultPaymentCard(boolean defaultPaymentCard);

      void showDefaultCardDialog(String defaultCardName);

      Observable<Boolean> setAsDefaultPaymentCardCondition();
   }

}
