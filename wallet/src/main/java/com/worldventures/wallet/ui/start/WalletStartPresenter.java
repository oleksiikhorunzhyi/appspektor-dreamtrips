package com.worldventures.wallet.ui.start;


import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface WalletStartPresenter extends WalletPresenter<WalletStartScreen> {

   HttpErrorHandlingUtil httpErrorHandlingUtil();

   void retryFetchingCard();

   void cancelFetchingCard();
}
