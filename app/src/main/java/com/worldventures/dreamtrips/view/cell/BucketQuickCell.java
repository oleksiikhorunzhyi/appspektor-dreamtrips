package com.worldventures.dreamtrips.view.cell;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.BucketItem;
import com.worldventures.dreamtrips.utils.busevents.DeleteBucketItemEvent;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_bucket_item_quick)
public class BucketQuickCell extends AbstractCell<BucketItem> {

    @InjectView(R.id.tv_name)
    TextView tvName;

    public BucketQuickCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        tvName.setText(getModelObject().getName());
    }

    @Override
    public void prepareForReuse() {

    }
}
