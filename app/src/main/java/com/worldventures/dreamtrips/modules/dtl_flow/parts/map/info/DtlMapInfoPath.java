package com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlDetailPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlMerchantDetailsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar.MasterToolbarPath;

import flow.path.Path;

@Layout(R.layout.screen_dtl_map_info)
public class DtlMapInfoPath extends DtlDetailPath {

   private final DtlMerchant merchant;


   public DtlMapInfoPath(MasterDetailPath path, DtlMerchant merchant) {
      super(path);
      this.merchant = merchant;
   }

   public DtlMerchant getMerchant() {
      return merchant;
   }

   @Override
   public Path getMasterToolbarPath() {
      return MasterToolbarPath.INSTANCE;
   }

   @Override
   public PathAttrs getAttrs() {
      return PathAttrs.WITHOUT_DRAWER;
   }
}
