package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlMerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.DayOfWeek;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationHours;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_dtl_details_working_hours_cell)
public class DtlWorkingHoursCell extends AbstractCell<OperationDay> {

   @InjectView(R.id.workingDay) TextView workingDay;
   @InjectView(R.id.workingHours) TextView workingHours;

   @Inject LocaleHelper localeHelper;

   private int timezone;

   public DtlWorkingHoursCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      final DayOfWeek dayOfWeek = getModelObject().getDayOfWeek();
      final List<OperationHours> operationDays = getModelObject().getOperationHours();
      //
      workingDay.setText(DateTimeUtils.getDisplayWeekDay(dayOfWeek.getDay(), Calendar.LONG, localeHelper.getDefaultLocale()));
      workingHours.setText(DtlMerchantHelper.formatOperationDayHours(itemView.getContext(), operationDays));
      //
      if (DateTimeUtils.isSameDayOfWeek(dayOfWeek, timezone)) {
         workingHours.setTypeface(null, Typeface.BOLD);
         workingDay.setTypeface(null, Typeface.BOLD);
      }
   }

   @Override
   public boolean shouldInject() {
      return true;
   }

   @Override
   public void prepareForReuse() {
      workingHours.setTypeface(null, Typeface.NORMAL);
      workingDay.setTypeface(null, Typeface.NORMAL);
   }

   public void setTimezone(int timezone) {
      this.timezone = timezone;
   }
}
