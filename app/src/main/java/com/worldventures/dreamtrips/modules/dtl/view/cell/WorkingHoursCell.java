package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.utils.DateTimeUtils;
import com.worldventures.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractCell;
import com.worldventures.dreamtrips.modules.dtl.helper.MerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.DayOfWeek;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationHours;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationalHoursUtils;

import java.util.Calendar;
import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_dtl_details_working_hours_cell)
public class WorkingHoursCell extends BaseAbstractCell<OperationDay> {

   @InjectView(R.id.workingDay) TextView workingDay;
   @InjectView(R.id.workingHours) TextView workingHours;

   private int timezone;

   public WorkingHoursCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      final DayOfWeek dayOfWeek = getModelObject().dayOfWeek();
      final List<OperationHours> operationDays = getModelObject().operationHours();
      //
      workingDay.setText(DateTimeUtils.getDisplayWeekDay(dayOfWeek.getDay(), Calendar.LONG, LocaleHelper.getDefaultLocale()));
      workingHours.setText(MerchantHelper.formatOperationDayHours(itemView.getContext(), operationDays));
      //
      if (OperationalHoursUtils.isSameDayOfWeek(dayOfWeek, timezone)) {
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
