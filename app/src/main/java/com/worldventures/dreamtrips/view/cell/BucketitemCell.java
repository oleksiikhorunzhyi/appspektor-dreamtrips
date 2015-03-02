package com.worldventures.dreamtrips.view.cell;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.techery.spares.annotations.Layout;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.BucketItem;
import com.worldventures.dreamtrips.core.repository.BucketListSelectionStorage;
import com.worldventures.dreamtrips.utils.busevents.DeleteBucketItemEvent;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_bucket_item)
public class BucketItemCell extends AbstractCell<BucketItem> {

    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.checkBoxDone)
    CheckBox checkBoxDone;
    @InjectView(R.id.swipe_container)
    SwipeLayout swipeLayout;

    public BucketItemCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        tvName.setText(getModelObject().getName());
        //set show mode.
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        //set drag edge.
        swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
    }

    @OnClick(R.id.delete)
    void delete() {
        getEventBus().post(new DeleteBucketItemEvent(getModelObject()));
    }

    @Override
    public void prepareForReuse() {

    }
}
