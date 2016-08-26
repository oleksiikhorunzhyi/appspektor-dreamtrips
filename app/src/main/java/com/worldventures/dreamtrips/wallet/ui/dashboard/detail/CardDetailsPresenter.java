package com.worldventures.dreamtrips.wallet.ui.dashboard.detail;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.StringRes;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableAddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.smartcard.action.records.DeleteRecordAction;
import io.techery.janet.smartcard.model.Record;
import rx.Observable;

import static java.lang.Integer.valueOf;

public class CardDetailsPresenter extends WalletPresenter<CardDetailsPresenter.Screen, Parcelable> {

   @Inject LocaleHelper localeHelper;
   @Inject SmartCardInteractor smartCardInteractor;

   private final BankCard bankCard;

   public CardDetailsPresenter(Context context, Injector injector, BankCard bankCard) {
      super(context, injector);
      this.bankCard = bankCard;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();

      String toolBarTitle = String.format("%s •••• %d", getContext().getResources()
            .getString(obtainFinancialServiceType(bankCard.type())), bankCard.number() % 10000);
      Screen view = getView();

      view.setTitle(toolBarTitle);
      view.showCardBankInfo(bankCard);
      view.showDefaultAddress(obtainAddressWithCountry());
      view.setAsDefaultPaymentCardCondition().compose(bindView()).subscribe(this::onSetAsDefaultCard);

      smartCardInteractor.deleteCardPipe()
            .observeWithReplay()
            .filter(state -> valueOf(bankCard.id()).equals(state.action.recordId))
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(smartCardInteractor.deleteCardPipe()))
            .subscribe(OperationSubscriberWrapper.<DeleteRecordAction>forView(getView().provideOperationDelegate())
                  .onStart(getContext().getString(R.string.wallet_card_details_progress_delete, bankCard.title()))
                  .onSuccess(deleteRecordAction -> Flow.get(getContext()).goBack())
                  .onFail(getContext().getString(R.string.error_something_went_wrong))
                  .wrap());
   }

   private AddressInfoWithLocale obtainAddressWithCountry() {
      return ImmutableAddressInfoWithLocale.builder()
            .addressInfo(bankCard.addressInfo())
            .locale(localeHelper.getDefaultLocale())
            .build();
   }

   @StringRes
   private int obtainFinancialServiceType(Record.FinancialService financialService) {
      switch (financialService) {
         case VISA:
            return R.string.wallet_card_financial_service_visa;
         case MASTERCARD:
            return R.string.wallet_card_financial_service_master_card;
         case DISCOVER:
            return R.string.wallet_card_financial_service_discover;
         case AMEX:
            return R.string.wallet_card_financial_service_amex;
         default:
            throw new IllegalStateException("Incorrect Financial Service");
      }
   }

   public void onDeleteCardRequired() {
      smartCardInteractor.deleteCardPipe().send(new DeleteRecordAction(valueOf(bankCard.id())));
   }

   public void onSetAsDefaultCard(boolean setDefaultCard){
      if (!setDefaultCard) return;
      //todo replace it
      getView().showDefaultCardDialog("DEFAULT CARD NAME");
   }

   public void defaultCardDialogConfirmed(boolean confirmed) {
      if (!confirmed) getView().setDefaultCardCondition(false);
   }

   public void goBack() {
      Flow.get(getContext()).goBack();
   }

   public interface Screen extends WalletScreen {
      void setTitle(String title);

      void showCardBankInfo(BankCard bankCard);

      void showDefaultAddress(AddressInfoWithLocale addressInfoWithLocale);

      void showDefaultCardDialog(String defaultCardName);

      void setDefaultCardCondition(boolean defaultCard);

      Observable<Boolean> setAsDefaultPaymentCardCondition();
   }

}