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
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;

public class DtlMapInfoScreenImpl extends DtlLayout<DtlMapInfoScreen, DtlMapInfoPresenter, DtlMapInfoPath>
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
    public DtlMapInfoPresenter createPresenter() {
        return new DtlMapInfoPresenterImpl(getContext(), injector, getPath().getId());
    }

    @Override
    protected void onPostAttachToWindowView() {
        commonDataInflater = new DtlMerchantSingleImageDataInflater();
        categoryDataInflater = new DtlMerchantInfoInflater();
        commonDataInflater.setView(this);
        categoryDataInflater.setView(this);
        observeSize(this);
        setOnClickListener(v -> getPresenter().onMarkerClick());
    }

    private void observeSize(final View view) {
        RxView.globalLayouts(view)
                .compose(RxLifecycle.bindView(view))
                .filter(aVoid -> view.getHeight() > 0)
                .take(1)
                .subscribe(aVoid -> getPresenter().onSizeReady(view.getHeight()));
    }

    @Override
    public void visibleLayout(boolean show) {
        setVisibility(show ? VISIBLE : GONE);
    }

    @Override
    public void setMerchant(DtlMerchant merchant) {
        commonDataInflater.apply(merchant, null);
        categoryDataInflater.apply(merchant, null);
    }
}
