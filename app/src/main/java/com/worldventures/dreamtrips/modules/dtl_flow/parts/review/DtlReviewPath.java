package com.worldventures.dreamtrips.modules.dtl_flow.parts.review;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlMasterPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar.MasterToolbarPath;

import flow.path.Path;

@Layout(R.layout.screen_dtl_review)
public class DtlReviewPath extends DtlMasterPath {

   private final Merchant merchant;
   private final boolean isVerified;

   public DtlReviewPath(@NonNull Merchant merchant) {
      this.merchant = merchant;
      this.isVerified = false;
   }

   public DtlReviewPath(Merchant merchant, boolean isVerified) {
      this.merchant = merchant;
      this.isVerified = isVerified;
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

   public boolean isVerified() {
      return isVerified;
   }
}
