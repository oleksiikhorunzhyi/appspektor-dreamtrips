package com.worldventures.wallet.ui.settings.general.newcard.detection;

import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface ExistingCardDetectPresenter extends WalletPresenter<ExistingCardDetectScreen> {

   void goBack();

   void navigateToPowerOn();

   void prepareUnassignCardOnBackend();

   void prepareUnassignCard();

   void unassignCard();

   void unassignCardOnBackend();

   HttpErrorHandlingUtil httpErrorHandlingUtil();

}
