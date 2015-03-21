package com.worldventures.dreamtrips.modules.bucketlist.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.bucket.BucketHeader;

/**
 * Created by 1 on 05.03.15.
 */
@Layout(R.layout.adapter_header_bucket)
public class BucketHeaderCell extends AbstractCell<BucketHeader> {

    public BucketHeaderCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        //textViewHeader.setText(getModelObject().getHeaderResource());
    }

    @Override
    public void prepareForReuse() {

    }
}
