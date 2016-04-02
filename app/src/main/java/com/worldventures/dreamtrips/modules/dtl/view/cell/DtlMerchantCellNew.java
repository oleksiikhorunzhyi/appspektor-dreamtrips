package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.selectable.SelectableCell;
import com.worldventures.dreamtrips.core.selectable.SelectableDelegate;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlMerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_dtl_merchant_new)
public class DtlMerchantCellNew extends AbstractDelegateCell<DtlMerchant, CellDelegate<DtlMerchant>>
        implements SelectableCell {

    @InjectView(R.id.merchantCoverImage)
    SimpleDraweeView merchantCoverImage;
    @InjectView(R.id.merchantName)
    TextView merchantName;
    @InjectView(R.id.merchantCategories)
    TextView merchantCategories;
    @InjectView(R.id.merchantOpenClosedStatus)
    TextView merchantOperationalStatus;
    @InjectView(R.id.merchantDistance)
    TextView merchantDistance;
    @InjectView(R.id.merchantParticipantSection)
    View merchantParticipantSection;
    //
    private DtlMerchantHelper dtlMerchantHelper;
    private SelectableDelegate selectableDelegate;

    public DtlMerchantCellNew(View view) {
        super(view);
        dtlMerchantHelper = new DtlMerchantHelper(view.getContext());
    }

    @Override
    protected void syncUIStateWithModel() {
        setImage(getModelObject().getImages());
        merchantName.setText(getModelObject().getDisplayName());
        setDistance();
        setCategories();
        setOperationalTime();
        setParticipantSection();
        //
        itemView.setSelected(selectableDelegate.isSelected(getAdapterPosition()));
        // TODO :: 4/2/16 last element has bottom margin = 0 and is "stuck" to bottom of screen
    }

    private void setImage(List<DtlMerchantMedia> mediaList) {
        DtlMerchantMedia media = Queryable.from(mediaList).firstOrDefault();
        if (media == null) {
            return;
        }
        //
        merchantCoverImage.setImageURI(Uri.parse(media.getImagePath()));
    }

    private void setDistance() {
        Resources res = merchantDistance.getResources();
        if (getModelObject().getDistance() != 0.0d) {
            merchantDistance.setVisibility(View.VISIBLE);
            merchantDistance.setText(res.getString(
                    R.string.distance_caption_format,
                    getModelObject().getDistance(),
                    res.getString(getModelObject().getDistanceType() == DistanceType.MILES ?
                            R.string.mi : R.string.km)));
        } else merchantDistance.setVisibility(View.GONE);
    }

    private void setCategories() {
        String categoriesString = dtlMerchantHelper.getCategories(getModelObject());
        if (!TextUtils.isEmpty(categoriesString)) {
            merchantCategories.setVisibility(View.VISIBLE);
            merchantCategories.setText(categoriesString);
        } else merchantCategories.setVisibility(View.GONE);
    }

    private void setOperationalTime() {
        if (getModelObject().hasOffer(DtlOffer.TYPE_POINTS) &&
                getModelObject().getOperationDays() != null && !getModelObject().getOperationDays().isEmpty()) {
            merchantOperationalStatus.setVisibility(View.VISIBLE);
            merchantOperationalStatus.setText(dtlMerchantHelper.getOperationalTime(getModelObject()));
        } else merchantOperationalStatus.setVisibility(View.GONE);
    }

    private void setParticipantSection() {
        merchantParticipantSection.setVisibility(getModelObject().hasNoOffers() ? View.GONE : View.VISIBLE);
    }

    @OnClick(R.id.merchant_details_root)
    void merchantClicked() {
        if (!selectableDelegate.isSelected(getAdapterPosition()))
            selectableDelegate.toggleSelection(getAdapterPosition());
        //
        cellDelegate.onCellClicked(getModelObject());
    }

    @Override
    public void prepareForReuse() {
    }

    @Override
    public void setSelectableDelegate(SelectableDelegate selectableDelegate) {
        this.selectableDelegate = selectableDelegate;
    }
}
