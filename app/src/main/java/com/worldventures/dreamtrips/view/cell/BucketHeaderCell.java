package com.worldventures.dreamtrips.view.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.bucket.BucketHeader;

import butterknife.InjectView;

/**
 * Created by 1 on 05.03.15.
 */
@Layout(R.layout.adapter_header_bucket)
public class BucketHeaderCell extends AbstractCell<BucketHeader> {

    @InjectView(R.id.textViewHeader)
    TextView textViewHeader;

    public BucketHeaderCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        textViewHeader.setText(getModelObject().getHeaderResource());
    }

    @Override
    public void prepareForReuse() {

    }
}
