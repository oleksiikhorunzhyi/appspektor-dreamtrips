package com.worldventures.dreamtrips.wallet.ui.dashboard.list;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.QuantityHelper;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerPresenter;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard.CardType;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.dashboard.detail.CardDetailsPath;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.CardStackViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.WalletCardSettingsPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.magstripe.WizardMagstripePath;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;

import static com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand.CardStackModel;

public class CardListScreenPresenter extends WalletPresenter<CardListScreenPresenter.Screen, Parcelable> {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject NavigationDrawerPresenter navigationDrawerPresenter;

   private SmartCard activeSmartCard;

   public CardListScreenPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observeChanges();

      Observable.concat(smartCardInteractor.cardStacksPipe()
            .createObservable(CardStacksCommand.get(false)), smartCardInteractor.cardStacksPipe()
            .createObservable(CardStacksCommand.get(true))).debounce(100, TimeUnit.MILLISECONDS).subscribe();
      smartCardInteractor.getActiveSmartCardPipe()
            .createObservableResult(new GetActiveSmartCardCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(it -> setSmartCard(it.getResult()));

      getView().lockStatus()
            .compose(bindView())
            .skip(1)
            .filter(checkedFlag -> activeSmartCard.lock() != checkedFlag)
            .subscribe(this::lockChanged);

      getView().unSupportedUnlockOperation().compose(bindView()).subscribe(it -> {
         getView().provideOperationDelegate()
               .showError(getContext().getString(R.string.wallet_dashboard_unlock_error), e -> {
               });
      });
   }

   private void lockChanged(boolean isLocked) {
      smartCardInteractor.lockPipe()
            .
                  createObservable(new SetLockStateCommand(isLocked))
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationSubscriberWrapper.<SetLockStateCommand>forView(getView().provideOperationDelegate()).onFail(getContext()
                  .getString(R.string.error_something_went_wrong)).onSuccess(action -> {
               if (isLocked) getView().disableLockBtn();
            }).wrap());
   }

   private void setSmartCard(SmartCard smartCard) {
      activeSmartCard = smartCard;
      getView().showSmartCardInfo(smartCard);
   }

   public void showBankCardDetails(BankCard bankCard) {
      Flow.get(getContext()).set(new CardDetailsPath(bankCard));
   }

   public void navigationClick() {
      navigationDrawerPresenter.openDrawer();
   }

   public void onSettingsChosen() {
      if (activeSmartCard == null) return;
      Flow.get(getContext()).set(new WalletCardSettingsPath(activeSmartCard));
   }

   public void addCreditCard() {
      Flow.get(getContext()).set(new WizardMagstripePath(CardType.CREDIT));
   }

   public void addDebitCard() {
      Flow.get(getContext()).set(new WizardMagstripePath(CardType.DEBIT));
   }

   public interface Screen extends WalletScreen {
      void showRecordsInfo(List<CardStackViewModel> result);

      void showSmartCardInfo(SmartCard smartCard);

      Observable<Boolean> lockStatus();

      Observable<Void> unSupportedUnlockOperation();

      void disableLockBtn();
   }

   private void observeChanges() {
      smartCardInteractor.cardStacksPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<CardStacksCommand>() //TODO check for progress, f.e. swipe refresh
                  .onSuccess(command -> getView().showRecordsInfo(adapt(command.getResult())))
                  .onFail((command, throwable) -> {
                  }));
      smartCardInteractor.smartCardModifierPipe()
            .observeSuccess()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> setSmartCard(command.smartCard()));
   }

   private List<CardStackViewModel> adapt(List<CardStackModel> stackList) {
      int sourceLength = stackList.size();
      List<CardStackViewModel> list = new ArrayList<>(sourceLength);

      for (int i = 0; i < sourceLength; i++) {
         CardStackModel vm = stackList.get(i);
         String title;
         switch (vm.type()) {
            case DEBIT:
               int debitCardListSize = vm.bankCards().size();
               int debitTitleId = QuantityHelper.chooseResource(debitCardListSize, R.string.wallet_debit_card_title, R.string.wallet_debit_cards_title);

               title = getContext().getString(debitTitleId, debitCardListSize);
               break;
            case CREDIT:
               int creditCardListSize = vm.bankCards().size();
               int creditTitleId = QuantityHelper.chooseResource(creditCardListSize, R.string.wallet_credit_card_title, R.string.wallet_credit_cards_title);

               title = getContext().getString(creditTitleId, creditCardListSize);
               break;
            default:
               title = getContext().getString(R.string.dashboard_default_card_stack_title);
         }

         list.add(new CardStackViewModel(vm.type(), vm.bankCards(), title));
      }

      return list;
   }
}