package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.feed.model.FeedAvatarEventModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedHeaderCell;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_feed_photo_event)
public class FeedAvatarEventCell extends FeedHeaderCell<FeedAvatarEventModel> {

    @InjectView(R.id.photo)
    SimpleDraweeView photo;
    @InjectView(R.id.title)
    TextView title;

    @Inject
    ActivityRouter router;

    public FeedAvatarEventCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();
        FeedAvatarEventModel obj = getModelObject();
        photo.setImageURI(Uri.parse(obj.getUsers()[0].getAvatar().getThumb()));
        title.setVisibility(View.GONE);
        itemView.setOnClickListener(view -> router.openUserProfile(getModelObject().getUsers()[0]));
    }


    @Override
    public void prepareForReuse() {

    }
}
