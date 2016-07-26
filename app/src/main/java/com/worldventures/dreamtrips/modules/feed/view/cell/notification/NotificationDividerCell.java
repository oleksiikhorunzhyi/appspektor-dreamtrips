package com.worldventures.dreamtrips.modules.feed.view.cell.notification;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_notification_divider)
public class NotificationDividerCell extends AbstractCell<String> {

    @InjectView(R.id.divider_title) TextView dividerTitle;

    public NotificationDividerCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        dividerTitle.setText(getModelObject());
    }
}
