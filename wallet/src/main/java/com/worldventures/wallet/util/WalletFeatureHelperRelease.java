package com.worldventures.wallet.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.worldventures.wallet.R;
import com.worldventures.wallet.service.SmartCardLocationInteractor;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.dashboard.CardListScreen;
import com.worldventures.wallet.ui.settings.WalletSettingsScreen;
import com.worldventures.wallet.ui.settings.general.WalletGeneralSettingsScreen;
import com.worldventures.wallet.ui.settings.security.WalletSecuritySettingsScreen;

import java.util.List;

import rx.Observable;
import rx.functions.Action0;

public class WalletFeatureHelperRelease implements WalletFeatureHelper {

   @Override
   public void prepareSettingsScreen(WalletSettingsScreen view) {
      hideDesiredViews(view.getToggleableItems());
      invalidateDivider((LinearLayout) view.getToggleableItems().get(0).getParent());
   }

   @Override
   public void prepareSettingsGeneralScreen(WalletGeneralSettingsScreen view) {
      hideDesiredViews(view.getToggleableItems());
      invalidateDivider((LinearLayout) view.getToggleableItems().get(0).getParent());
   }

   @Override
   public void openEditProfile(Context context, Action0 action) {
      Toast.makeText(context, R.string.wallet_coming_soon, Toast.LENGTH_SHORT).show();
   }

   @Override
   public void prepareSettingsSecurityScreen(WalletSecuritySettingsScreen view) {
      hideDesiredViews(view.getToggleableItems());
      invalidateDivider((LinearLayout) view.getToggleableItems().get(0).getParent());
   }

   @Override
   public void openFindCard(Context context, Action0 action) {
      Toast.makeText(context, R.string.wallet_coming_soon, Toast.LENGTH_SHORT).show();
   }

   @Override
   public void prepareDashboardScreen(CardListScreen view) {
      view.getCardListFab().setBackgroundTintList(
            ColorStateList.valueOf(ContextCompat.getColor(view.getViewContext(), R.color.wallet_add_cards_inactive_color))
      );
      view.getEmptyCardListView().setText(R.string.wallet_wizard_card_list_add_card_coming_soon_text);
      view.getCardListFab()
            .setOnClickListener(v -> Toast.makeText(view.getViewContext(), R.string.wallet_coming_soon, Toast.LENGTH_SHORT)
                  .show());
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
   public boolean isSampleCardMode() {
      return true;
   }

   @Override
   public void finishRegularProvisioning(Navigator navigator) {
      navigator.goCardList();
   }

   @Override
   public boolean pinFunctionalityAvailable() {
      return false;
   }

   @Override
   public Observable<Void> clearSettings(SmartCardLocationInteractor interactor) {
      return Observable.just(null);
   }

   private void invalidateDivider(LinearLayout container) {
      container.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE | LinearLayout.SHOW_DIVIDER_END);
      container.requestLayout();
   }

   private void hideDesiredViews(List<View> views) {
      for (View view : views) {
         view.setVisibility(View.GONE);
      }
   }
}
