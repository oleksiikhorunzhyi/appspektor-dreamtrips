package com.worldventures.dreamtrips.wallet;

import android.content.Context;
import android.content.Intent;

import com.worldventures.dreamtrips.core.module.NavigationActivityModule;
import com.worldventures.wallet.ui.WalletActivity;

import java.util.List;

public class DTWalletActivity extends WalletActivity {

   @Override
   protected List<Object> getModules() {
      final List<Object> list = super.getModules();
      list.add(new NavigationActivityModule(this));
      list.add(new WalletExternalActivityModule());
      return list;
   }

   public static void startWallet(Context context) {
      context.startActivity(new Intent(context, DTWalletActivity.class));
   }
}
