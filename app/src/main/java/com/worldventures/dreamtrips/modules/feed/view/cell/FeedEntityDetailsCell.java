package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.BaseFeedCell;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedEntityContentFragmentFactory;

import javax.inject.Inject;

@Layout(R.layout.adapter_item_entity_details)
public class FeedEntityDetailsCell extends BaseFeedCell<FeedItem> {

    @Inject
    FeedEntityContentFragmentFactory fragmentFactory;

    public FeedEntityDetailsCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();
        Pair<Route, Parcelable> entityData = fragmentFactory.create(getModelObject());
        //
        Fragment entityFragment = fragmentManager.findFragmentById(R.id.fragment_details);
        boolean notAdded = entityFragment == null
                || entityFragment.getView() == null || entityFragment.getView().getParent() == null
                || !entityFragment.getClass().getName().equals(entityData.first.getClazzName());
        if (notAdded) {
            NavigationConfig config = NavigationConfigBuilder.forFragment()
                    .backStackEnabled(false)
                    .fragmentManager(fragmentManager)
                    .data(entityData.second)
                    .containerId(R.id.fragment_details)
                    .build();
            router.moveTo(entityData.first, config);
        }
    }
}
