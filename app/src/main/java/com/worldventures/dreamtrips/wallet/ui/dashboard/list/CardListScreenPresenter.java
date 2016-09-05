package com.worldventures.dreamtrips.wallet.ui.dashboard.list;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerPresenter;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard.CardType;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.dashboard.detail.CardDetailsPath;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.CardStackHeaderHolder;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.CardStackViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.ImmutableCardStackHeaderHolder;
import com.worldventures.dreamtrips.wallet.ui.settings.WalletCardSettingsPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.magstripe.WizardMagstripePath;
import com.worldventures.dreamtrips.wallet.util.CardListStackConverter;
import com.worldventures.dreamtrips.wallet.util.CardUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;

import static com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand.CardStackModel;

public class CardListScreenPresenter extends WalletPresenter<CardListScreenPresenter.Screen, Parcelable> {

   private static final int MAX_CARD_LIMIT = 10;

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject NavigationDrawerPresenter navigationDrawerPresenter;

   private final CardListStackConverter cardListStackConverter;

   private CardStackHeaderHolder cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder().build();
   private SmartCard smartCard;

   public CardListScreenPresenter(Context context, Injector injector) {
      super(context, injector);
      cardListStackConverter = new CardListStackConverter(context);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observeChanges();

      Observable.concat(smartCardInteractor.cardStacksPipe()
            .createObservable(CardStacksCommand.get(false)), smartCardInteractor.cardStacksPipe()
            .createObservable(CardStacksCommand.get(true))).debounce(100, TimeUnit.MILLISECONDS).subscribe();

      smartCardInteractor.smartCardModifierPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(it -> setSmartCard(it.getResult()));

      smartCardInteractor.lockPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationSubscriberWrapper.<SetLockStateCommand>forView(getView().provideOperationDelegate()).onFail(getContext()
                  .getString(R.string.wallet_dashboard_unlock_error), a -> getView().notifySmartCardChanged(cardStackHeaderHolder))
                  .onSuccess(action -> { })
                  .wrap());
   }

   public void onLockChanged(boolean isLocked) {
      if (smartCard.lock() == isLocked) return;
      smartCardInteractor.lockPipe().send(new SetLockStateCommand(isLocked));
   }

   private void setSmartCard(SmartCard smartCard) {
      this.smartCard = smartCard;
      this.cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder()
            .from(cardStackHeaderHolder)
            .smartCard(smartCard)
            .build();
      getView().notifySmartCardChanged(cardStackHeaderHolder);
   }

   public void showBankCardDetails(BankCard bankCard) {
      Flow.get(getContext()).set(new CardDetailsPath(bankCard));
   }

   public void navigationClick() {
      navigationDrawerPresenter.openDrawer();
   }

   public void onSettingsChosen() {
      if (cardStackHeaderHolder.smartCard() == null) return;
      Flow.get(getContext()).set(new WalletCardSettingsPath(cardStackHeaderHolder.smartCard()));
   }

   public void addCardRequired(CardType cardType) {
      Flow.get(getContext()).set(new WizardMagstripePath(cardType));
   }

   private void observeChanges() {
      smartCardInteractor.cardStacksPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<CardStacksCommand>() //TODO check for progress, f.e. swipe refresh
                  .onSuccess(command -> cardsLoaded(command.getResult()))
                  .onFail((command, throwable) -> {
                  }));
   }

   private void cardsLoaded(List<CardStackModel> loadedModels) {
      List<CardStackViewModel> cards = cardListStackConverter.convertToModelViews(loadedModels);
      int cardsCount = CardUtils.stacksToItemsCount(cards);
      cardStackHeaderHolder = ImmutableCardStackHeaderHolder.builder()
            .from(cardStackHeaderHolder)
            .cardCount(cardsCount)
            .build();

      getView().notifySmartCardChanged(cardStackHeaderHolder);
      getView().showRecordsInfo(cards);
      getView().setEnableAddingCardButtons(cardsCount != MAX_CARD_LIMIT);
   }

   public interface Screen extends WalletScreen {
      void showRecordsInfo(List<CardStackViewModel> result);

      void notifySmartCardChanged(CardStackHeaderHolder smartCard);

      void setEnableAddingCardButtons(boolean enable);
   }
}
