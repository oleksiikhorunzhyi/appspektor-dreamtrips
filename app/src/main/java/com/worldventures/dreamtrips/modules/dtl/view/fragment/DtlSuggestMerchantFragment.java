package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlSuggestMerchantPresenter;

@MenuResource(R.menu.menu_suggest_merchant)
public class DtlSuggestMerchantFragment extends SuggestRestaurantBaseFragment<DtlSuggestMerchantPresenter>
        implements DtlSuggestMerchantPresenter.View {

    @Override
    protected DtlSuggestMerchantPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlSuggestMerchantPresenter(getArgs());
    }

    @Override
    public void syncUiWithMerchant(DtlMerchant merchant) {
        restaurantName.setText(merchant.getDisplayName());
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
