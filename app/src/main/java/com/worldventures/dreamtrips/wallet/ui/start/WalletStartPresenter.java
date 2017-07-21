package com.worldventures.dreamtrips.wallet.ui.start;


import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface WalletStartPresenter extends WalletPresenterI<WalletStartScreen>{

   HttpErrorHandlingUtil httpErrorHandlingUtil();

   void retryFetchingCard();

   void cancelFetchingCard();
}
