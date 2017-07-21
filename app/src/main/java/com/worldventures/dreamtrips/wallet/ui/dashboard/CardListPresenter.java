package com.worldventures.dreamtrips.wallet.ui.dashboard;

import android.support.annotation.DrawableRes;
import android.view.View;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.TransitionModel;

public interface CardListPresenter extends WalletPresenterI<CardListScreen> {

   void navigationClick();

   void retryFWU();

   void retryFWUCanceled();

   void navigateBack();

   void confirmForceFirmwareUpdate();

   void navigateToFirmwareUpdate();

   boolean isCardDetailSupported();

   void onSettingsChosen();

   void onProfileChosen();

   TransitionModel getCardPosition(View view, int overlap, @DrawableRes int cardBackGroundResId, boolean defaultCard);

   void cardClicked(String recId, TransitionModel transitionModel);

   void addCardRequired(int cardLoadedCount);

   void syncPayments();

   void goToFactoryReset();

}
