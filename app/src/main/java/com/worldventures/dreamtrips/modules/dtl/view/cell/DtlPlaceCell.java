package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.graphics.PointF;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_dtl_place)
public class DtlPlaceCell extends AbstractCell<DtlPlace> {

    protected final static PointF FOCUS_POINT = new PointF(0.5f, 0.0f);

    @InjectView(R.id.imageViewDtlPlaceImage)
    protected SimpleDraweeView placeImage;
    @InjectView(R.id.placeName)
    protected TextView placeName;
    @InjectView(R.id.categoryName)
    protected TextView categoryName;

    @Inject
    ActivityRouter activityRouter;

    public DtlPlaceCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        placeImage.getHierarchy().setActualImageFocusPoint(FOCUS_POINT);
        if (!getModelObject().getMediaList().isEmpty()) {
            placeImage.setImageURI(Uri.parse(getModelObject().getMediaList().get(0).getMediaFileName()));
        }
        placeName.setText(getModelObject().getName());
        if (!getModelObject().getCategories().isEmpty()) {
            categoryName.setText(getModelObject().getCategories().get(0).getName());
        } else {
            categoryName.setText("");
        }
    }

    @OnClick(R.id.itemLayout) void placeClicked() {
//        NavigationBuilder
//                .create()
//                .with(activityRouter)
//                .data()
//                .move(Route.DTL_PLACE_DETAILS);
        Toast.makeText(itemView.getContext(),
                "Hold your horses, cowboy!\nNot implemented yet!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void prepareForReuse() {
        //
    }
}
