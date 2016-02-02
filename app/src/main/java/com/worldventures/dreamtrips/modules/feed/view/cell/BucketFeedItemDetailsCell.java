package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.graphics.PointF;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.bucketlist.manager.BucketItemManager;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.util.BucketItemInfoUtil;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.event.DeleteBucketEvent;
import com.worldventures.dreamtrips.modules.feed.event.EditBucketEvent;
import com.worldventures.dreamtrips.modules.feed.model.BucketFeedItem;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedItemDetailsCell;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_feed_bucket_event)
public class BucketFeedItemDetailsCell extends FeedItemDetailsCell<BucketFeedItem> {

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

    @Inject
    BucketItemManager bucketItemManager;

    public BucketFeedItemDetailsCell(View view) {
        super(view);
    }

    private void loadImage(String lowUrl, String url) {
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setLowResImageRequest(ImageRequest.fromUri(lowUrl))
                .setImageRequest(ImageRequest.fromUri(url))
                .build();
        imageViewCover.getHierarchy().setActualImageFocusPoint(new PointF(0.5f, 0.0f));
        imageViewCover.setController(draweeController);
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();

        BucketItem bucketItem = getModelObject().getItem();
        String small = BucketItemInfoUtil.getMediumResUrl(itemView.getContext(), bucketItem);
        String big = BucketItemInfoUtil.getHighResUrl(itemView.getContext(), bucketItem);
        loadImage(small, big);
        textViewName.setText(bucketItem.getName());
        if (TextUtils.isEmpty(bucketItem.getCategoryName())) {
            textViewCategory.setVisibility(View.GONE);
        } else {
            textViewCategory.setVisibility(View.VISIBLE);
            textViewCategory.setText(getCategory(bucketItem));
        }
        if (TextUtils.isEmpty(BucketItemInfoUtil.getPlace(bucketItem))) {
            textViewPlace.setVisibility(View.GONE);
        } else {
            textViewPlace.setVisibility(View.VISIBLE);
            textViewPlace.setText(BucketItemInfoUtil.getPlace(bucketItem));
        }
        textViewDate.setText(BucketItemInfoUtil.getTime(itemView.getContext(), bucketItem));
    }


    private String getCategory(BucketItem bucketItem) {
        return bucketItem.getCategoryName();
    }

    @Override
    public void prepareForReuse() {

    }

    @Override
    protected void onMore() {
        showMoreDialog(R.menu.menu_feed_entity_edit, R.string.bucket_delete, R.string.bucket_delete_caption);
    }

    @Override
    protected void onDelete() {
        super.onDelete();
        getEventBus().post(new DeleteBucketEvent(getModelObject().getItem()));
    }

    @Override
    protected void onEdit() {
        super.onEdit();
        BucketItem.BucketType bucketType = getType(getModelObject().getItem().getType());
        bucketItemManager.saveSingleBucketItem(getModelObject().getItem());
        //
        getEventBus().post(new EditBucketEvent(getModelObject().getItem().getUid(), bucketType));
    }

    @OnClick(R.id.bucket_main)
    void openBucketEntityDetails() {
        router.moveTo(Route.FEED_ENTITY_DETAILS, NavigationConfigBuilder.forActivity()
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .data(new FeedDetailsBundle(getModelObject()))
                .build());
    }

    private BucketItem.BucketType getType(String name) {
        return BucketItem.BucketType.valueOf(name.toUpperCase());
    }
}
