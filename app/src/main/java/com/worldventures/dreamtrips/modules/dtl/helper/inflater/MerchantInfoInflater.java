package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.dtl.helper.MerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;

import butterknife.InjectView;
import io.techery.properratingbar.ProperRatingBar;
import rx.Observable;

public class MerchantInfoInflater extends MerchantDataInflater {

   @InjectView(R.id.operational_time) TextView operationalTime;
   @InjectView(R.id.merchant_details_pricing) ProperRatingBar pricing;
   @InjectView(R.id.categories) TextView categories;
   @InjectView(R.id.distance) TextView distance;

   protected Resources resources;

   @Override
   public void setView(View rootView) {
      super.setView(rootView);
      this.resources = rootView.getResources();
   }

   @Override
   protected void onMerchantApply() {
      setInfo();
   }

   private void setInfo() {
      pricing.setRating(merchant.budget());
      //
      boolean hasDistance = merchant.distance() != null;
      boolean hasOperationDays = MerchantHelper.merchantHasOperationDays(merchant);
      boolean hasCategories = !TextUtils.isEmpty(MerchantHelper.getCategories(merchant));

      // TODO Think about distance !!
//      CharSequence distanceText = hasDistance ? resources.getString(R.string.distance_caption_format, merchant.getDistance(), resources
//            .getString(merchant.getDistanceType() == DistanceType.MILES ? R.string.mi : R.string.km)) : "";
      CharSequence distanceText = hasDistance ? resources.getString(R.string.distance_caption_format, merchant.distance(), "") : "";
      CharSequence categoriesText = hasCategories ? MerchantHelper.getCategories(merchant) : "";

      ViewUtils.setTextOrHideView(distance, distanceText);
      ViewUtils.setTextOrHideView(categories, categoriesText);

      if (hasOperationDays) {
         Observable.fromCallable(() -> MerchantHelper.getOperationalTime(rootView.getContext(), merchant))
               .compose(RxLifecycle.bindView(rootView))
               .subscribe(operationalTime::setText, ex -> operationalTime.setVisibility(View.GONE));
      } else ViewUtils.setViewVisibility(operationalTime, View.GONE);

   }

}
