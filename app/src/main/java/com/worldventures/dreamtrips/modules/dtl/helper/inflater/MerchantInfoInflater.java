package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.dtl.helper.FilterHelper;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.settings.model.Setting;
import com.worldventures.dreamtrips.modules.settings.util.SettingsFactory;

import javax.inject.Inject;

import butterknife.InjectView;
import io.techery.properratingbar.ProperRatingBar;
import rx.Observable;

public class MerchantInfoInflater extends MerchantDataInflater {

   @InjectView(R.id.operational_time) TextView operationalTime;
   @InjectView(R.id.merchant_details_pricing) ProperRatingBar pricing;
   @InjectView(R.id.categories) TextView categories;
   @InjectView(R.id.distance) TextView distance;

   @Inject SnappyRepository db;

   protected Resources resources;

   public MerchantInfoInflater(Injector injector) {
      injector.inject(this);
   }

   @Override
   public void setView(View rootView) {
      super.setView(rootView);
      this.resources = rootView.getResources();
   }

   @Override
   protected void onMerchantAttributesApply() {
      setInfo();
   }

   private void setInfo() {
      pricing.setRating(merchantAttributes.budget());
      //
      CharSequence distanceText = merchantAttributes.provideFormattedDistance(resources, FilterHelper.provideDistanceFromSettings(db));
      CharSequence categoriesText = merchantAttributes.provideFormattedCategories();

      ViewUtils.setTextOrHideView(distance, distanceText);
      ViewUtils.setTextOrHideView(categories, categoriesText);

      if (merchantAttributes.hasOperationDays()) {
         Observable.fromCallable(() -> merchantAttributes.provideFormattedOperationalTime(rootView.getContext(), true))
               .compose(RxLifecycle.bindView(rootView))
               .subscribe(operationalTime::setText, ex -> operationalTime.setVisibility(View.GONE));
      } else ViewUtils.setViewVisibility(operationalTime, View.GONE);

   }

}
