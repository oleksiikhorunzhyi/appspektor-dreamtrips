package com.worldventures.dreamtrips.modules.membership.view.cell;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.membership.event.MemberCellSelectedEvent;
import com.worldventures.dreamtrips.modules.membership.model.Member;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;

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

    public MemberCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        cbChecked.setChecked(getModelObject().isChecked());
        tvName.setText(getModelObject().getName());
        tvSubtitle.setText(getModelObject().getSubtitle());
        if (!TextUtils.isEmpty(getModelObject().getPhone())) {
            ivPhone.setVisibility(View.VISIBLE);
        } else {
            ivPhone.setVisibility(View.GONE);
        }
        llResend.setVisibility(View.GONE);
    }

    @OnCheckedChanged(R.id.cb_checked)
    public void onChecked(boolean checked) {
        getModelObject().setIsChecked(checked);
        getEventBus().post(new MemberCellSelectedEvent(checked));
    }

    @Override
    public void prepareForReuse() {

    }
}
