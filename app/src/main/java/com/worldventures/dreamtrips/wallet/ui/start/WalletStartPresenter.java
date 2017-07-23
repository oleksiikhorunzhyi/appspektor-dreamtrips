package com.worldventures.dreamtrips.wallet.ui.start;


import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface WalletStartPresenter extends WalletPresenter<WalletStartScreen> {

   HttpErrorHandlingUtil httpErrorHandlingUtil();

   void retryFetchingCard();

   void cancelFetchingCard();
}
