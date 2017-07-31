package com.worldventures.dreamtrips.wallet.ui.dashboard;

import android.support.annotation.DrawableRes;
import android.view.View;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.CommonCardViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.TransitionModel;
import com.worldventures.dreamtrips.wallet.ui.records.model.RecordViewModel;

public interface CardListPresenter extends WalletPresenter<CardListScreen> {

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

   void cardClicked(CommonCardViewModel record, TransitionModel transitionModel);

   void addCardRequired(int cardLoadedCount);

   void syncPayments();

   void goToFactoryReset();

}
