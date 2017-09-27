package com.worldventures.dreamtrips.wallet.ui.records.detail;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface CardDetailsPresenter extends WalletPresenter<CardDetailsScreen> {

   void goBack();

   void updateNickName();

   void onDeleteCardClick();

   void payThisCard();

   void onChangeDefaultCardConfirmed();

   void onChangeDefaultCardCanceled();

   void onDeleteCardConfirmed();

   void onCardIsReadyDialogShown();

   void validateRecordName(String name);

   void changeDefaultCard(boolean isDefault);
}
