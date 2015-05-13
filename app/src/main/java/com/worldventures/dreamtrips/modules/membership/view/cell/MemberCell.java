package com.worldventures.dreamtrips.modules.membership.view.cell;

import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.membership.event.MemberCellResendEvent;
import com.worldventures.dreamtrips.modules.membership.event.MemberCellSelectedEvent;
import com.worldventures.dreamtrips.modules.membership.model.Member;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_invite_member)
public class MemberCell extends AbstractCell<Member> {
    @InjectView(R.id.cb_checked)
    CheckBox cbChecked;
    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.tv_subtitle)
    TextView tvSubtitle;
    @InjectView(R.id.iv_phone)
    ImageView ivPhone;
    @InjectView(R.id.tv_date)
    TextView tvDate;
    @InjectView(R.id.tv_resend)
    TextView tvResend;
    @InjectView(R.id.ll_resend)
    LinearLayout llResend;

    String country;

    public MemberCell(View view) {
        super(view);
        country = Locale.getDefault().getCountry();
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
            tvDate.setText(DateTimeUtils.convertDateToJodaString(
                            getModelObject().getHistory().getDate()
                            , DateTimeUtils.MEMBER_FORMAT)
            );
        } else {
            tvDate.setText("");
            llResend.setVisibility(View.GONE);
        }
    }

    @OnCheckedChanged(R.id.cb_checked)
    public void onChecked(boolean checked) {
        if (getModelObject().isChecked() != checked) {
            getModelObject().setIsChecked(checked);

            if (checked) {
                getModelObject().setOriginalPosition(getAdapterPosition());
            }

            getEventBus().post(new MemberCellSelectedEvent(checked, getAdapterPosition(), checked
                    ? 0 : getModelObject().getOriginalPosition()));
        }
    }

    @OnClick(R.id.ll_resend)
    public void onResendClick() {
        getEventBus().post(new MemberCellResendEvent(getModelObject().getHistory(), getModelObject().getName()));
    }

    @Override
    public void prepareForReuse() {

    }
}
