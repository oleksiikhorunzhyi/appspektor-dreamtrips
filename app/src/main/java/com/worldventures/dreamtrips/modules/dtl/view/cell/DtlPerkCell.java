package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.ImageryDraweeView;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlMerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.MerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_offer_perk)
public class DtlPerkCell extends AbstractDelegateCell<Offer, CellDelegate<Offer>> {

   @InjectView(R.id.perk_logo) ImageryDraweeView image;
   @InjectView(R.id.perks_description) TextView title;
   @InjectView(R.id.perks_operation_days) TextView operationDays;
   @InjectView(R.id.expirationBar) AppCompatTextView expirationBar;

   @Inject LocaleHelper localeHelper;

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
      if (media == null) return;
      //
      image.setImageUrl(media.getImagePath());
   }

   private void bindExpirationBar() {
      if (DtlMerchantHelper.isOfferExpiringSoon(getModelObject())) {
         ViewUtils.setTextOrHideView(expirationBar, DtlMerchantHelper.
               getOfferExpiringCaption(itemView.getContext(), getModelObject(), localeHelper.getDefaultLocale()));
      } else ViewUtils.setViewVisibility(View.GONE, expirationBar);
   }

   private void bindDescription() {
      if (getModelObject().description() != null) title.setText(getModelObject().title());
   }

   private void bindOperationDays() {
      List<OperationDay> operationDays = getModelObject().operationDays();
      if (operationDays == null) return;
      //
      String concatDays = DateTimeUtils.concatOperationDays(itemView.getResources(), operationDays);
      this.operationDays.setText(concatDays);
   }
}
