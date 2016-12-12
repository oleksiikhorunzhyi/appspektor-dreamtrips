package com.worldventures.dreamtrips.modules.membership.view.cell;

import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.membership.model.Member;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;

@Layout(R.layout.adapter_item_invite_member)
public class MemberCell extends AbstractDelegateCell<Member, CellDelegate<Member>> {

   @InjectView(R.id.cb_checked) CheckBox cbChecked;
   @InjectView(R.id.tv_name) TextView tvName;
   @InjectView(R.id.tv_subtitle) TextView tvSubtitle;
   @InjectView(R.id.iv_phone) ImageView ivPhone;
   @InjectView(R.id.tv_date) TextView tvDate;
   @InjectView(R.id.tv_resend) TextView tvResend;
   @InjectView(R.id.ll_resend) LinearLayout llResend;

   private String country;

   public MemberCell(View view) {
      super(view);
      country = LocaleHelper.getDefaultLocale().getCountry();
   }

   @Override
   protected void syncUIStateWithModel() {
      cbChecked.setChecked(getModelObject().isChecked());
      tvName.setText(getModelObject().getName());
      String subtitle;
      if (getModelObject().isEmailMain()) {
         subtitle = getModelObject().getSubtitle();
      } else {
         subtitle = PhoneNumberUtils.formatNumber(getModelObject().getSubtitle(), country);
      }
      tvSubtitle.setText(subtitle);
      ivPhone.setVisibility(View.GONE);
      if (getModelObject().getHistory() != null) {
         llResend.setVisibility(View.VISIBLE);
         tvDate.setText(DateTimeUtils.convertDateToString(getModelObject().getHistory()
               .getDate(), DateTimeUtils.MEMBER_FORMAT));
      } else {
         tvDate.setText("");
         llResend.setVisibility(View.GONE);
      }
   }

   @OnCheckedChanged(R.id.cb_checked)
   public void onChecked(boolean checked) {
      if (getModelObject().isChecked() != checked) {
         getModelObject().setIsChecked(checked);
         cellDelegate.onCellClicked(getModelObject());
      }
   }
}
