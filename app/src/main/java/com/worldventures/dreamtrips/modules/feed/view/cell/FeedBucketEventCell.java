package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.graphics.PointF;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.util.BucketItemInfoUtil;
import com.worldventures.dreamtrips.modules.feed.model.FeedBucketEventModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedHeaderCell;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_feed_bucket_event)
public class FeedBucketEventCell extends FeedHeaderCell<FeedBucketEventModel> {

    @InjectView(R.id.imageViewCover)
    SimpleDraweeView imageViewCover;

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
        imageViewCover.getHierarchy().setActualImageFocusPoint(new PointF(0.5f, 0.0f));

    }

    private void loadImage(String lowUrl, String url) {
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setLowResImageRequest(ImageRequest.fromUri(lowUrl))
                .setImageRequest(ImageRequest.fromUri(url))
                .build();
        imageViewCover.setController(draweeController);
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();
        BucketItem bucketItem = getModelObject().getEntities()[0];
        String small = BucketItemInfoUtil.getMediumResUrl(itemView.getContext(), bucketItem);
        String big = BucketItemInfoUtil.getHighResUrl(itemView.getContext(), bucketItem);
        loadImage(small, big);
        textViewName.setText(bucketItem.getName());
        textViewCategory.setText(getCategory(bucketItem));
        textViewDate.setText(BucketItemInfoUtil.getTime(itemView.getContext(), bucketItem));
        textViewPlace.setText(BucketItemInfoUtil.getPlace(bucketItem));
        textViewFriends.setText(bucketItem.getFriends());
        textViewTags.setText(bucketItem.getBucketTags());

        itemView.setOnClickListener(view -> router.openBucketItemDetails(getType(bucketItem.getType()), bucketItem.getId()));
    }


    private String getCategory(BucketItem bucketItem) {
        return Character.toUpperCase(bucketItem.getType().charAt(0)) + bucketItem.getType().substring(1);
    }

    @Override
    public void prepareForReuse() {

    }

    private BucketTabsPresenter.BucketType getType(String name) {
        return BucketTabsPresenter.BucketType.valueOf(name.toUpperCase());
    }
}
