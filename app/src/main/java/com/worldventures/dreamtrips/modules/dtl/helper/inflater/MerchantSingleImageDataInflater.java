package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.ImageryDraweeView;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.MerchantMedia;

import java.util.List;

import butterknife.InjectView;

public class MerchantSingleImageDataInflater extends MerchantCommonDataInflater {

   @InjectView(R.id.merchant_details_cover) ImageryDraweeView cover;

   @Override
   protected void onMerchantAttributesApply() {
      super.onMerchantAttributesApply();
      setImage(merchantAttributes.images());
   }

   private void setImage(List<MerchantMedia> mediaList) {
      MerchantMedia media = Queryable.from(mediaList).firstOrDefault();
      if (media == null) {
         return;
      }
      //
      cover.setImageUrl(media.getImagePath());
   }
}
