package com.worldventures.dreamtrips.modules.dtl.helper;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.DTlMerchant;

import butterknife.InjectView;

public class DtlCategoryDataInflater extends DtlPlaceDataInflater {

    protected DtlPlaceHelper helper;
    @InjectView(R.id.category_title)
    TextView category;

    public DtlCategoryDataInflater(DtlPlaceHelper helper) {
        this.helper = helper;
    }

    @Override
    protected void onPlaceApply(DTlMerchant place) {
        String categories = helper.getCategories(place);
        if (!TextUtils.isEmpty(categories)) {
            category.setVisibility(View.VISIBLE);
            category.setText(categories);
        }
    }
}
