package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig.Builder;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceCategoryDataInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceCommonDataInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceHelper;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceSingleImageDataInflater;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;

import javax.inject.Inject;

import butterknife.OnClick;

@Layout(R.layout.adapter_item_dtl_place)
public class DtlPlaceCell extends AbstractCell<DtlPlace> {

    @Inject
    ActivityRouter activityRouter;
    //
    DtlPlaceCommonDataInflater commonDataInflater;
    DtlPlaceCategoryDataInflater categoryDataInflater;

    public DtlPlaceCell(View view) {
        super(view);
        DtlPlaceHelper helper = new DtlPlaceHelper(view.getContext());
        commonDataInflater = new DtlPlaceSingleImageDataInflater(helper);
        categoryDataInflater = new DtlPlaceCategoryDataInflater(helper);
        commonDataInflater.setView(view);
        categoryDataInflater.setView(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        commonDataInflater.apply(getModelObject());
        categoryDataInflater.apply(getModelObject());
    }

    @OnClick(R.id.dtl_place_root)
    void placeClicked() {
        NavigationBuilder.create()
                .with(activityRouter)
                .data(getModelObject())
                .toolbarConfig(Builder.create().visible(false).build())
                .move(Route.DTL_PLACE_DETAILS);
    }

    @Override
    public void prepareForReuse() {
        //
    }
}
