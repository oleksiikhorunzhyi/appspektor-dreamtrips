package com.worldventures.wallet.ui.records.add;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface AddCardDetailsPresenter extends WalletPresenter<AddCardDetailsScreen> {

   void fetchRecordViewModel();

   void goBack();

   void onCardInfoConfirmed(String cvv, String nickname, boolean setAsDefaultCard);

   void onCardToDefaultClick(boolean confirmed);
}
