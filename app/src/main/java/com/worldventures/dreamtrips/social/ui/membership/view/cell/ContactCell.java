package com.worldventures.dreamtrips.social.ui.membership.view.cell;

import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.core.utils.DateTimeUtils;
import com.worldventures.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ProjectPhoneNumberUtils;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.social.domain.entity.Contact;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;

@Layout(R.layout.adapter_item_invite_member)
public class ContactCell extends BaseAbstractDelegateCell<Contact, CellDelegate<Contact>> {

   @InjectView(R.id.selectedCheckBox) CheckBox selectedCheckBox;
   @InjectView(R.id.name) TextView name;
   @InjectView(R.id.contact) TextView contact;
   @InjectView(R.id.lastSentDate) TextView lastSentDate;
   @InjectView(R.id.resendContainer) LinearLayout resendContainer;

   public ContactCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      selectedCheckBox.setChecked(getModelObject().getSelected());
      name.setText(getModelObject().getName());
      if (getModelObject().getEmailIsMain()) {
         contact.setText(getModelObject().getEmail());
      } else {
         contact.setText(ProjectPhoneNumberUtils.formatNumber(getModelObject().getPhone(),
               LocaleHelper.getDefaultLocale().getCountry()));
      }
      if (getModelObject().getSentInvite() != null) {
         lastSentDate.setText(DateTimeUtils.convertDateToString(getModelObject().getSentInvite()
               .getDate(), DateTimeUtils.MEMBER_FORMAT));
         resendContainer.setVisibility(View.VISIBLE);
      } else {
         lastSentDate.setText("");
         resendContainer.setVisibility(View.GONE);
      }
   }

   @OnCheckedChanged(R.id.selectedCheckBox)
   public void onChecked(boolean checked) {
      if (getModelObject().getSelected() != checked) {
         getModelObject().setSelected(checked);
         cellDelegate.onCellClicked(getModelObject());
      }
   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}
