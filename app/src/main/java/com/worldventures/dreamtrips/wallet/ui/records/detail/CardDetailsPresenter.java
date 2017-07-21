package com.worldventures.dreamtrips.wallet.ui.records.detail;

import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface CardDetailsPresenter extends WalletPresenterI<CardDetailsScreen> {

   void goBack();

   void updateNickName();

   void onDeleteCardClick();

   void payThisCard();

   void onChangeDefaultCardConfirmed();

   void onChangeDefaultCardCanceled();

   void onDeleteCardConfirmed();

   void onCardIsReadyDialogShown();

   HttpErrorHandlingUtil httpErrorHandlingUtil();

}
