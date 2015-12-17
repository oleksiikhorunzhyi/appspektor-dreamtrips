package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlSuggestMerchantPresenter;

@Layout(R.layout.fragment_suggest_merchant)
@MenuResource(R.menu.menu_suggest_merchant)
public class DtlSuggestMerchantFragment extends SuggestPlaceBaseFragment<DtlSuggestMerchantPresenter>
        implements DtlSuggestMerchantPresenter.View {

    @Override
    protected DtlSuggestMerchantPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlSuggestMerchantPresenter(getArgs());
    }

    @Override
    public void syncUiWithPlace(DtlMerchant place) {
        restaurantName.setText(place.getDisplayName());
        restaurantName.setFocusable(false);
        restaurantName.setFocusableInTouchMode(false);
        restaurantName.setClickable(false);
        city.setVisibility(View.GONE);
    }

    @Override
    protected void dialogCanceled(DialogInterface dialog) {
        super.dialogCanceled(dialog);
        getActivity().onBackPressed();
    }
}
