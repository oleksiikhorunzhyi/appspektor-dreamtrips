package com.worldventures.dreamtrips.wallet.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.RecordListCommand;
import com.worldventures.dreamtrips.wallet.service.command.wizard.AddDummyRecordCommand;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningModeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPresenter;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.WalletSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.WalletSettingsScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.WalletGeneralSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.WalletGeneralSettingsScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.security.WalletSecuritySettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.WalletSecuritySettingsScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.assign.WizardAssignUserPath;

import butterknife.ButterKnife;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

public class WalletFeatureHelperRelease implements WalletFeatureHelper {

   private final Janet janet;
   private final RecordInteractor recordInteractor;
   private final WizardInteractor wizardInteractor;

   public WalletFeatureHelperRelease(Janet walletJanet, RecordInteractor recordInteractor, WizardInteractor wizardInteractor) {
      this.janet = walletJanet;
      this.recordInteractor = recordInteractor;
      this.wizardInteractor = wizardInteractor;
   }

   @Override
   public void prepareSettingsScreen(WalletSettingsPresenter.Screen view) {
      WalletSettingsScreen screen = (WalletSettingsScreen) view;
      ButterKnife.apply(screen.toggleableItems, (item, i) -> item.setVisibility(View.GONE));
      invalidateDivider((LinearLayout) screen.toggleableItems.get(0).getParent());
   }

   @Override
   public void prepareSettingsGeneralScreen(WalletGeneralSettingsPresenter.Screen view) {
      WalletGeneralSettingsScreen screen = (WalletGeneralSettingsScreen) view;
      ButterKnife.apply(screen.toggleableItems, (item, i) -> item.setVisibility(View.GONE));
      invalidateDivider((LinearLayout) screen.toggleableItems.get(0).getParent());
   }

   @Override
   public void openEditProfile(Context context, Action0 action) {
      Toast.makeText(context, R.string.coming_soon, Toast.LENGTH_SHORT).show();
   }

   @Override
   public void prepareSettingsSecurityScreen(WalletSecuritySettingsPresenter.Screen view) {
      WalletSecuritySettingsScreen screen = (WalletSecuritySettingsScreen) view;
      ButterKnife.apply(screen.toggleableItems, (item, i) -> item.setVisibility(View.GONE));
      invalidateDivider((LinearLayout) screen.toggleableItems.get(0).getParent());
   }

   @Override
   public void openFindCard(Context context, Action0 action) {
      Toast.makeText(context, R.string.coming_soon, Toast.LENGTH_SHORT).show();
   }

   @Override
   public void prepareDashboardScreen(CardListPresenter.Screen view) {
      CardListScreen screen = (CardListScreen) view;
      screen.fabButton.setBackgroundTintList(
            ColorStateList.valueOf(ContextCompat.getColor(screen.getContext(), R.color.wallet_add_cards_inactive_color))
      );
      screen.emptyCardListView.setText(R.string.wallet_wizard_card_list_add_card_coming_soon_text);
      screen.fabButton.setOnClickListener(v -> Toast.makeText(screen.getContext(), R.string.coming_soon, Toast.LENGTH_SHORT).show());
   }

   @Override
   public boolean addingCardIsNotSupported() {
      return true;
   }

   @Override
   public boolean offlineModeState(boolean isOfflineMode) {
      return true;
   }

   @Override
   public Observable<Void> onUserAssigned(SmartCardUser user) {
      return janet.createPipe(AddDummyRecordCommand.class)
            .createObservableResult(new AddDummyRecordCommand(user, false))
            .map(c -> (Void) null)
            .onErrorReturn(throwable -> null);
   }

   @Override
   public void onUserFetchedFromServer(SmartCardUser user) {
      recordInteractor.cardsListPipe()
            .createObservableResult(RecordListCommand.fetch())
            .map(Command::getResult)
            .subscribe(list -> {
               if (list == null || list.isEmpty()) {
                  janet.createPipe(AddDummyRecordCommand.class)
                        .send(new AddDummyRecordCommand(user, true));
               }
            });
   }

   @Override
   public void navigateFromSetupUserScreen(Navigator navigator, SmartCardUser user, boolean withoutLast) {
      wizardInteractor.provisioningStatePipe()
            .createObservable(ProvisioningModeCommand.fetchState())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<ProvisioningModeCommand>()
                  .onSuccess(command ->
                        navigator.withoutLast(new WizardAssignUserPath(command.getResult()))));
   }

   @Override
   public boolean isSampleCardMode() {
      return true;
   }

   private void invalidateDivider(LinearLayout container) {
      container.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE|LinearLayout.SHOW_DIVIDER_END);
      container.requestLayout();
   }
}
