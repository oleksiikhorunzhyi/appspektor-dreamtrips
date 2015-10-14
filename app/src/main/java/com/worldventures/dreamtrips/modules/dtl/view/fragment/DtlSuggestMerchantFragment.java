package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.DTEditText;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlSuggestMerchantPresenter;

import butterknife.InjectView;
import io.techery.properratingbar.ProperRatingBar;

@Layout(R.layout.fragment_suggest_merchant)
public class DtlSuggestMerchantFragment
        extends BaseFragment<DtlSuggestMerchantPresenter>
        implements DtlSuggestMerchantPresenter.View {

    @InjectView(R.id.restaurantName)
    DTEditText restaurantName;
    @InjectView(R.id.contactName)
    DTEditText contactName;
    @InjectView(R.id.phoneNumber)
    DTEditText phoneNumber;
    @InjectView(R.id.fromDate)
    TextView fromDate;
    @InjectView(R.id.fromTime)
    TextView fromTime;
    @InjectView(R.id.toDate)
    TextView toDate;
    @InjectView(R.id.toTime)
    TextView toTime;
    @InjectView(R.id.foodRatingBar)
    ProperRatingBar foodRatingBar;
    @InjectView(R.id.serviceRatingBar)
    ProperRatingBar serviceRatingBar;
    @InjectView(R.id.cleanlinessRatingBar)
    ProperRatingBar cleanlinessRatingBar;
    @InjectView(R.id.uniquenessRatingBar)
    ProperRatingBar uniquenessRatingBar;
    @InjectView(R.id.additionalInfo)
    DTEditText additionalInfo;

    @Override
    protected DtlSuggestMerchantPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlSuggestMerchantPresenter();
    }

    @Override
    public void stub() {
        //
    }
}
