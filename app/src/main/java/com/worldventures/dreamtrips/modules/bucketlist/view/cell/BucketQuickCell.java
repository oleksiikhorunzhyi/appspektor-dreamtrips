package com.worldventures.dreamtrips.modules.bucketlist.view.cell;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPostItem;
import com.worldventures.dreamtrips.core.utils.events.BucketItemReloadEvent;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_bucket_item_quick)
public class BucketQuickCell extends AbstractCell<BucketPostItem> {

    @InjectView(R.id.tv_name)
    TextView tvName;

    @InjectView(R.id.imageViewRefresh)
    ImageView imageViewRestart;

    @InjectView(R.id.progressBar)
    ProgressBar progressBar;

    public BucketQuickCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        tvName.setText(getModelObject().getName());
        if (getModelObject().isLoaded()) {
            progressBar.setVisibility(View.GONE);
            imageViewRestart.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            imageViewRestart.setVisibility(View.GONE);
        }

        if (getModelObject().isError()) {
            progressBar.setVisibility(View.GONE);
            imageViewRestart.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.imageViewRefresh)
    void onRefresh() {
        getEventBus().post(new BucketItemReloadEvent(getModelObject()));
    }

    @Override
    public void prepareForReuse() {

    }
}
