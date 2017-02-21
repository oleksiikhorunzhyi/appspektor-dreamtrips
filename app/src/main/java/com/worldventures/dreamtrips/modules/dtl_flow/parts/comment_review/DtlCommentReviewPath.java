package com.worldventures.dreamtrips.modules.dtl_flow.parts.comment_review;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlMasterPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar.MasterToolbarPath;

import java.util.Collections;
import java.util.List;

import flow.path.Path;

/**
 * Created by andres.rubiano on 20/02/2017.
 */

@Layout(R.layout.activity_merchant_review)
public class DtlCommentReviewPath extends DtlMasterPath {

   private final Merchant merchant;

   public DtlCommentReviewPath(@NonNull Merchant merchant) {
      super();
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
