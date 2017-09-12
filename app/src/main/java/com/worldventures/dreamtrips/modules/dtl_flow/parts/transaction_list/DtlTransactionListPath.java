package com.worldventures.dreamtrips.modules.dtl_flow.parts.transaction_list;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlDetailPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar.MasterToolbarPath;

import flow.path.Path;

@Layout(R.layout.screen_dtl_transaction_list)
public class DtlTransactionListPath extends DtlDetailPath {

   private final Merchant merchant;

   public DtlTransactionListPath(MasterDetailPath path, @NonNull Merchant merchant) {
      super(path);
      this.merchant = merchant;
   }

   @Override
   public PathAttrs getAttrs() {
      return PathAttrs.WITHOUT_DRAWER;
   }

   @Override
   public Path getMasterToolbarPath() {
      return MasterToolbarPath.INSTANCE;
   }

   public Merchant getMerchant() {
      return merchant;
   }
}
