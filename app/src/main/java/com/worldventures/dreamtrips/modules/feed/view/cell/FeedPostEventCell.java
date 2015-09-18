package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.app.Dialog;
import android.support.v7.widget.PopupMenu;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.feed.bundle.PostBundle;
import com.worldventures.dreamtrips.modules.feed.event.DeletePostEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedPostEventModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedHeaderCell;

import javax.inject.Inject;

import butterknife.InjectView;
import cn.pedant.SweetAlert.SweetAlertDialog;

@Layout(R.layout.adapter_post_event)
public class FeedPostEventCell extends FeedHeaderCell<FeedPostEventModel> {

    @InjectView(R.id.post)
    TextView post;

    @Inject
    FragmentCompass fragmentCompass;

    public FeedPostEventCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();
        FeedPostEventModel obj = getModelObject();
        post.setText(obj.getItem().getDescription());
        itemView.setOnClickListener(v -> itemClicked());
    }

    protected void itemClicked() {
        openComments(getModelObject());
    }

    @Override
    protected void onDelete() {
        getEventBus().post(new DeletePostEvent(getModelObject()));
    }

    @Override
    protected void onEdit() {
        fragmentCompass.removePost();
        fragmentCompass.setContainerId(R.id.container_details_floating);
        fragmentCompass.disableBackStack();
        fragmentCompass.showContainer();
        //
        NavigationBuilder.create()
                .with(fragmentCompass)
                .data(new PostBundle(getModelObject().getItem()))
                .attach(Route.POST_CREATE);
    }

    @Override
    public void prepareForReuse() {

    }

    @Override
    protected void onMore() {
        showMoreDialog(R.menu.menu_post_edit, R.string.post_delete, R.string.post_delete_caption);
    }
}
