package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceCategoryDataInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceCommonDataInflater;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceHelper;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlPlaceSingleImageDataInflater;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlMapInfoPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPlaceCommonDetailsPresenter;

import butterknife.OnClick;

@Layout(R.layout.fragment_dtl_map_info)
public class DtlMapInfoFragment
        extends BaseFragmentWithArgs<DtlMapInfoPresenter, DtlPlace>
        implements DtlPlaceCommonDetailsPresenter.View {

    DtlPlaceCommonDataInflater commonDataInflater;
    DtlPlaceCategoryDataInflater categoryDataInflater;

    @Override
    protected DtlMapInfoPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlMapInfoPresenter(getArgs());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        DtlPlaceHelper helper = new DtlPlaceHelper(rootView.getContext());
        commonDataInflater = new DtlPlaceSingleImageDataInflater(helper);
        categoryDataInflater = new DtlPlaceCategoryDataInflater(helper);
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
    public void setPlace(DtlPlace place) {
        commonDataInflater.apply(place);
        categoryDataInflater.apply(place);
    }

    @OnClick(R.id.place_details_root)
    void placeClicked() {
        getPresenter().onPlaceClick();
    }

}
