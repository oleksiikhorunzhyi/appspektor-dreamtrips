package com.worldventures.dreamtrips.modules.dtl_flow.parts.map.info;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlMerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.DtlMerchantCommonDataInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.DtlMerchantInfoInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.DtlMerchantSingleImageDataInflater;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowLayout;

public class DtlMapInfoScreenImpl extends FlowLayout<DtlMapInfoScreen, DtlMapInfoPresenter, DtlMapInfoPath>
        implements DtlMapInfoScreen {

    DtlMerchantCommonDataInflater commonDataInflater;
    DtlMerchantInfoInflater categoryDataInflater;

    public DtlMapInfoScreenImpl(Context context) {
        super(context);
    }

    public DtlMapInfoScreenImpl(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void onPrepared() {
        super.onPrepared();
        injector.inject(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        DtlMerchantHelper helper = new DtlMerchantHelper(getContext());
        commonDataInflater = new DtlMerchantSingleImageDataInflater(helper);
        categoryDataInflater = new DtlMerchantInfoInflater(helper);
        commonDataInflater.setView(this);
        categoryDataInflater.setView(this);
        observeSize(this);
    }

    private void observeSize(final View view) {
        RxView.globalLayouts(view)
                .compose(RxLifecycle.bindView(view))
                .take(1)
                .subscribe(aVoid -> getPresenter().onSizeReady(view.getHeight()));
    }

    @Override
    public void visibleLayout(boolean show) {
        setVisibility(show ? VISIBLE : GONE);
    }

    @Override
    public void setMerchant(DtlMerchant merchant) {
        commonDataInflater.apply(merchant);
        categoryDataInflater.apply(merchant);
    }

    @Override
    public DtlMapInfoPresenter createPresenter() {
        return new DtlMapInfoPresenterImpl(getContext(), injector, getPath().getId());
    }
}
