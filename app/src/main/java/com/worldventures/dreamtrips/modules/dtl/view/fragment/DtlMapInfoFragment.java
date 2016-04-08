package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.jakewharton.rxbinding.view.RxView;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.dtl.bundle.DtlMerchantDetailsBundle;
import com.worldventures.dreamtrips.modules.dtl.event.MerchantClickedEvent;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlMerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.DtlMerchantCommonDataInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.DtlMerchantInfoInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.DtlMerchantSingleImageDataInflater;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlMapInfoPresenter;

import butterknife.OnClick;

@Layout(R.layout.fragment_dtl_map_info)
public class DtlMapInfoFragment
        extends RxBaseFragmentWithArgs<DtlMapInfoPresenter, DtlMerchantDetailsBundle>
        implements DtlMapInfoPresenter.View {

    DtlMerchantCommonDataInflater commonDataInflater;
    DtlMerchantInfoInflater categoryDataInflater;

    @Override
    protected DtlMapInfoPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlMapInfoPresenter(getArgs().getId());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        DtlMerchantHelper helper = new DtlMerchantHelper(rootView.getContext());
        commonDataInflater = new DtlMerchantSingleImageDataInflater(helper);
        categoryDataInflater = new DtlMerchantInfoInflater(helper);
        commonDataInflater.setView(rootView);
        categoryDataInflater.setView(rootView);
        observeSize(rootView);
    }

    private void observeSize(final View rootView) {
        bind(RxView.globalLayouts(rootView))
                .take(1)
                .subscribe(aVoid -> {
                    int ownHeight = rootView.getHeight();
                    getPresenter().onSizeReady(ownHeight);
                });
    }

    @Override
    public void setMerchant(DtlMerchant merchant) {
        commonDataInflater.apply(merchant);
        categoryDataInflater.apply(merchant);
    }

    @OnClick(R.id.merchant_details_root)
    void merchantClicked() {
        getPresenter().onMerchantClick();
    }

    @Override
    public void hideLayout() {
        getView().setVisibility(View.INVISIBLE);
    }

    @Override
    public void showLayout() {
        getView().setVisibility(View.VISIBLE);
    }

    @Override
    public void showDetails(String id) {
        if (tabletAnalytic.isTabletLandscape() && getArgs().isSlave()) {
            eventBus.post(new MerchantClickedEvent(id));
        } else {
            router.moveTo(Route.DTL_MERCHANT_DETAILS, NavigationConfigBuilder.forActivity()
                    .data(new DtlMerchantDetailsBundle(id, false))
                    .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                    .build());
        }
    }
}
