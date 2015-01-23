package com.worldventures.dreamtrips.view.cell;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.BucketItem;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_bucket_item)
public class BucketItemCell extends AbstractCell<BucketItem> {

    public static final String PREFIX = "bucket_";
    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.iv_like)
    ImageView ivLike;

    @Inject
    SimpleKeyValueStorage storage;

    public BucketItemCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        tvName.setText(getModelObject().getName());
        ivLike.setSelected(storage.get(PREFIX + getModelObject().getId()) != null);
    }

    @Override
    public void prepareForReuse() {
    }

    @OnClick(R.id.iv_like)
    public void onLikeClick() {
        if (!ivLike.isSelected()) {
            storage.put(PREFIX + getModelObject().getId(), "");
            ivLike.setSelected(true);
        } else {
            storage.remove(PREFIX + getModelObject().getId());
            ivLike.setSelected(false);
        }
    }
}
