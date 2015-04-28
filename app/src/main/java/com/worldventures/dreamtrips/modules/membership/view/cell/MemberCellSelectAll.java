package com.worldventures.dreamtrips.modules.membership.view.cell;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.membership.event.SelectAllEvent;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_invite_member_select_all)
public class MemberCellSelectAll extends AbstractCell<Object> {
    @InjectView(R.id.cb_checked)
    CheckBox cbChecked;
    @InjectView(R.id.tv_name)
    TextView tvName;

    public MemberCellSelectAll(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {

    }

    @Override
    public void prepareForReuse() {

    }

    @OnClick(R.id.ll_cell)
    public void onCellClick() {
        cbChecked.setChecked(!cbChecked.isChecked());
    }

    @OnCheckedChanged(R.id.cb_checked)
    public void onCheckedChanged(boolean checked) {
        if (checked) {
            tvName.setText("Unselect All");
        } else {
            tvName.setText("Select All");
        }
        getEventBus().post(new SelectAllEvent(checked));
    }
}
