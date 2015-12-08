package com.worldventures.dreamtrips.modules.dtl.helper;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import butterknife.InjectView;

public class DtlCategoryDataInflater extends DtlMerchantDataInflater {

    protected DtlMerchantHelper helper;
    @InjectView(R.id.category_title)
    TextView category;

    public DtlCategoryDataInflater(DtlMerchantHelper helper) {
        this.helper = helper;
    }

    @Override
    protected void onMerchantApply(DtlMerchant merchant) {
        String categories = helper.getCategories(merchant);
        if (!TextUtils.isEmpty(categories)) {
            category.setVisibility(View.VISIBLE);
            category.setText(categories);
        }
    }
}
