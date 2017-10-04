package com.worldventures.dreamtrips.wallet.ui.common;

import android.view.View;

import rx.functions.Action0;

public interface WalletNavigationDelegate {

   void init(View view);

   void setOnLogoutAction(Action0 action);

   void openDrawer();
}
