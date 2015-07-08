package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;
import com.worldventures.dreamtrips.modules.feed.model.FeedBucketEventModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedHeaderCell;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_feed_bucket_event)
public class FeedBucketEventCell extends FeedHeaderCell<FeedBucketEventModel> {

    @InjectView(R.id.imageViewCover)
    SimpleDraweeView imageViewCover;
    @InjectView(R.id.imageViewPhoto)
    ImageView imageViewPhoto;
    @InjectView(R.id.textViewName)
    TextView textViewName;
    @InjectView(R.id.textViewCategory)
    TextView textViewCategory;
    @InjectView(R.id.textViewDate)
    TextView textViewDate;
    @InjectView(R.id.textViewPlace)
    TextView textViewPlace;
    @InjectView(R.id.textViewFriends)
    TextView textViewFriends;
    @InjectView(R.id.textViewTags)
    TextView textViewTags;

    @Inject
    ActivityRouter router;

    public FeedBucketEventCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();
        //TODO set
        itemView.setOnClickListener(view -> {
            BucketItem bucketItem = getModelObject().getEntities()[0];
            router.openBucketItemDetails(getType(bucketItem.getType()), bucketItem.getId());
        });
    }

    @Override
    public void prepareForReuse() {

    }

    private BucketTabsPresenter.BucketType getType(String name) {
        return BucketTabsPresenter.BucketType.valueOf(name.toUpperCase());
    }
}
