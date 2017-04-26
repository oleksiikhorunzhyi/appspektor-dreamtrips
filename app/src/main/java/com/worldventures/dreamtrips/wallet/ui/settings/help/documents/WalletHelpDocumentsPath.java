package com.worldventures.dreamtrips.wallet.ui.settings.help.documents;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;


@Layout(R.layout.screen_wallet_documents)
public class WalletHelpDocumentsPath extends StyledPath {

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }

   @Override
   public boolean equals(Object o) {
      // for back navigation
      return o != null && getClass().equals(o.getClass());
   }
}
