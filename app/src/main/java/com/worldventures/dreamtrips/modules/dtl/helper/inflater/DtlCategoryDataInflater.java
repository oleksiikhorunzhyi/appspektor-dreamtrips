package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceHelper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import butterknife.InjectView;

public class DtlCategoryDataInflater extends DtlPlaceDataInflater {

    protected DtlPlaceHelper helper;
    @InjectView(R.id.category_title)
    TextView category;

    public DtlCategoryDataInflater(DtlPlaceHelper helper) {
        this.helper = helper;
    }

    @Override
    protected void onPlaceApply(DtlMerchant place) {
        String categories = helper.getCategories(place);
        if (!TextUtils.isEmpty(categories)) {
            category.setVisibility(View.VISIBLE);
            category.setText(categories);
        }
    }
}
