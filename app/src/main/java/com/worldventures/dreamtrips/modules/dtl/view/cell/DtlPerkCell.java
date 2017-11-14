package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.modules.common.view.custom.ImageryDraweeView;
import com.worldventures.dreamtrips.modules.dtl.helper.MerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.MerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationalHoursUtils;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_offer_perk)
public class DtlPerkCell extends BaseAbstractDelegateCell<Offer, CellDelegate<Offer>> {

   @InjectView(R.id.perk_logo) ImageryDraweeView image;
   @InjectView(R.id.perks_description) TextView title;
   @InjectView(R.id.perks_operation_days) TextView operationDays;
   @InjectView(R.id.expirationBar) AppCompatTextView expirationBar;

   public DtlPerkCell(View view) {
      super(view);
   }

   @OnClick(R.id.perks_view)
   protected void onPerkClick() {
      cellDelegate.onCellClicked(getModelObject());
   }

   @Override
   protected void syncUIStateWithModel() {
      bindImage();
      bindExpirationBar();
      bindDescription();
      bindOperationDays();
   }

   @Override
   public void prepareForReuse() {
   }

   private void bindImage() {
      MerchantMedia media = Queryable.from(getModelObject().images()).firstOrDefault();
      if (media == null) {
         return;
      }
      //
      image.setImageUrl(media.getImagePath());
   }

   private void bindExpirationBar() {
      if (MerchantHelper.isOfferExpiringSoon(getModelObject())) {
         ViewUtils.setTextOrHideView(expirationBar, MerchantHelper.
               getOfferExpiringCaption(itemView.getContext(), getModelObject(), LocaleHelper.getDefaultLocale()));
      } else {
         ViewUtils.setViewVisibility(View.GONE, expirationBar);
      }
   }

   private void bindDescription() {
      if (getModelObject().description() != null) {
         title.setText(getModelObject().title());
      }
   }

   private void bindOperationDays() {
      List<OperationDay> operationDays = getModelObject().operationDays();
      if (operationDays == null) {
         return;
      }
      //
      String concatDays = OperationalHoursUtils.concatOperationDays(operationDays, LocaleHelper.getDefaultLocale(), itemView
            .getResources()
            .getString(R.string.everyday));
      this.operationDays.setText(concatDays);
   }
}
