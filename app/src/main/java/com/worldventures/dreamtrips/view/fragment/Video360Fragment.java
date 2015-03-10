package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.config.Video360;
import com.worldventures.dreamtrips.presentation.Video360FragmentPM;
import com.worldventures.dreamtrips.view.cell.Video360Cell;

import butterknife.InjectView;

/**
 * Created by 1 on 10.03.15.
 */
@Layout(R.layout.fragment_360_videos)
public class Video360Fragment extends BaseFragment<Video360FragmentPM> implements Video360FragmentPM.View {

    @InjectView(R.id.recyclerViewFeatured)
    RecyclerView recyclerViewFeatured;

    @InjectView(R.id.recyclerViewRecent)
    RecyclerView recyclerViewRecent;

    private BaseArrayListAdapter<Video360> adapterFeatured;
    private BaseArrayListAdapter<Video360> adapterRecent;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        LinearLayoutManager linearLayoutManagerFeatured = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager linearLayoutManagerRecent = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewFeatured.setLayoutManager(linearLayoutManagerFeatured);
        recyclerViewRecent.setLayoutManager(linearLayoutManagerRecent);

        adapterFeatured = new BaseArrayListAdapter<>(getActivity(), (Injector) getActivity());
        adapterRecent = new BaseArrayListAdapter<>(getActivity(), (Injector) getActivity());

        adapterFeatured.registerCell(Video360.class, Video360Cell.class);
        adapterRecent.registerCell(Video360.class, Video360Cell.class);

        recyclerViewFeatured.setAdapter(adapterFeatured);
        recyclerViewRecent.setAdapter(adapterRecent);
    }

    @Override
    public BaseArrayListAdapter getFeaturedAdapter() {
        return adapterFeatured;
    }

    @Override
    public BaseArrayListAdapter getRecentAdapter() {
        return adapterRecent;
    }

    @Override
    protected Video360FragmentPM createPresentationModel(Bundle savedInstanceState) {
        return new Video360FragmentPM(this);
    }
}
