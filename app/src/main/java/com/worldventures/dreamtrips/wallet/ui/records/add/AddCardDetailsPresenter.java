package com.worldventures.dreamtrips.wallet.ui.records.add;

import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface AddCardDetailsPresenter extends WalletPresenterI<AddCardDetailsScreen> {

   HttpErrorHandlingUtil httpErrorHandlingUtil();

   void goBack();

   void onCardInfoConfirmed(String cvv, String nickname, boolean setAsDefaultCard);

   void onCardToDefaultClick(boolean confirmed);
}
