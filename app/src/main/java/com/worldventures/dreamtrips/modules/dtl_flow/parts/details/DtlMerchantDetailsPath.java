package com.worldventures.dreamtrips.modules.dtl_flow.parts.details;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlDetailPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar.MasterToolbarPath;

import java.util.Collections;
import java.util.List;

import flow.path.Path;

@Layout(R.layout.screen_dtl_details)
public class DtlMerchantDetailsPath extends DtlDetailPath {

   private final Merchant merchant;
   private final List<String> preExpandOfferPositions;

   public DtlMerchantDetailsPath(MasterDetailPath path, @NonNull Merchant merchant, @Nullable List<String> preExpandOfferPositions) {
      super(path);
      this.merchant = merchant;
      this.preExpandOfferPositions = preExpandOfferPositions != null ? preExpandOfferPositions : Collections.emptyList();
   }

   public Merchant getMerchant() {
      return merchant;
   }

   public List<String> getPreExpandOffers() {
      return preExpandOfferPositions;
   }

   @Override
   public PathAttrs getAttrs() {
      return PathAttrs.WITHOUT_DRAWER;
   }

   @Override
   public Path getMasterToolbarPath() {
      return MasterToolbarPath.INSTANCE;
   }
}
