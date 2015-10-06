package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPlacesListPresenter;

@Layout(R.layout.fragment_dtl_places_list)
public class DtlPlacesListFragment extends BaseFragment<DtlPlacesListPresenter> implements DtlPlacesListPresenter.View {

    @Override
    protected DtlPlacesListPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlPlacesListPresenter();
    }

    @Override
    public void stub() {
        //
    }
}
