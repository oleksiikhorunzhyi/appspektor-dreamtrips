package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlMasterPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar.MasterToolbarPath;
import flow.path.Path;


@Layout(R.layout.screen_dtl_reviews)
public class DtlReviewsPath extends DtlMasterPath {

   private final Merchant merchant;

   private final String message;

   public DtlReviewsPath(@NonNull Merchant merchant, String message) {
      super();
      this.merchant = merchant;
      this.message = message;
   }

   public String getMessage() {
      return message;
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
