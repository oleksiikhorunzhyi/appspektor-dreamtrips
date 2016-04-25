package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferPointsData;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.DtlMerchantsScreenImpl;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_offer_points)
public class DtlPointsCell extends AbstractDelegateCell<DtlOfferPointsData, DtlMerchantsScreenImpl.PointsDelegate> {

    @InjectView(R.id.pointsImage) SimpleDraweeView image;
    @InjectView(R.id.pointsDescription) TextView description;

    public DtlPointsCell(View view) {
        super(view);
    }

    @OnClick(R.id.points_view)
    protected void onPerkClick() {
        cellDelegate.onCellClicked(getModelObject());
    }

    @Override
    protected void syncUIStateWithModel() {
        bindImage();
        bindDescription();
    }

    private void bindImage() {
        DtlOfferMedia media = Queryable.from(getModelObject().getImages()).firstOrDefault();
        if (media == null) return;
        //
        image.setImageURI(Uri.parse(media.getImagePath()));
    }

    private void bindDescription() {
        if (getModelObject().getDescription() != null) description.setText(getModelObject().getDescription());
    }


    @Override
    public void prepareForReuse() {

    }
}
