package com.worldventures.dreamtrips.wallet.ui.records.add;

import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface AddCardDetailsPresenter extends WalletPresenter<AddCardDetailsScreen> {

   HttpErrorHandlingUtil httpErrorHandlingUtil();

   void goBack();

   void onCardInfoConfirmed(String cvv, String nickname, boolean setAsDefaultCard);

   void onCardToDefaultClick(boolean confirmed);
}
