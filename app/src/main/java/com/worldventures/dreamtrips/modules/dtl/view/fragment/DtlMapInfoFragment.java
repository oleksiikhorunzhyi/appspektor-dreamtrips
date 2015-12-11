package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.dtl.bundle.DtlMerchantDetailsBundle;
import com.worldventures.dreamtrips.modules.dtl.event.PlaceClickedEvent;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceCommonDataInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceHelper;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceInfoInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceSingleImageDataInflater;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlMapInfoPresenter;

import butterknife.OnClick;

@Layout(R.layout.fragment_dtl_map_info)
public class DtlMapInfoFragment
        extends BaseFragmentWithArgs<DtlMapInfoPresenter, DtlMerchantDetailsBundle>
        implements DtlMapInfoPresenter.View {

    DtlPlaceCommonDataInflater commonDataInflater;
    DtlPlaceInfoInflater categoryDataInflater;

    @Override
    protected DtlMapInfoPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlMapInfoPresenter(getArgs().getId());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        DtlPlaceHelper helper = new DtlPlaceHelper(rootView.getContext());
        commonDataInflater = new DtlPlaceSingleImageDataInflater(helper);
        categoryDataInflater = new DtlPlaceInfoInflater(helper);
        commonDataInflater.setView(rootView);
        categoryDataInflater.setView(rootView);
        observeSize(rootView);
    }

    private void observeSize(final View rootView) {
        ViewTreeObserver viewTreeObserver = rootView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int ownHeight = rootView.getHeight();
                getPresenter().onSizeReady(ownHeight);
                //
                ViewTreeObserver observer = rootView.getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    observer.removeOnGlobalLayoutListener(this);
                } else {
                    observer.removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    @Override
    public void setPlace(DtlMerchant place) {
        commonDataInflater.apply(place);
        categoryDataInflater.apply(place);
    }

    @OnClick(R.id.place_details_root)
    void placeClicked() {
        getPresenter().onPlaceClick();
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
            eventBus.post(new PlaceClickedEvent(id));
        } else {
            router.moveTo(Route.DTL_MERCHANT_DETAILS, NavigationConfigBuilder.forActivity()
                    .data(new DtlMerchantDetailsBundle(id, false))
                    .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                    .build());
        }
    }
}
