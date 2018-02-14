package com.worldventures.wallet.ui.settings.general.about;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface AboutPresenter extends WalletPresenter<AboutScreen> {

   void fetchAboutInfo();

   void goBack();
}
