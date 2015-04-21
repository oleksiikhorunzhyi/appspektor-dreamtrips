package com.worldventures.dreamtrips.modules.infopages.view.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ScrollView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.video.cell.Video360Cell;
import com.worldventures.dreamtrips.modules.video.cell.Video360SmallCell;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.modules.video.model.Video360;
import com.worldventures.dreamtrips.modules.video.presenter.Video360Presenter;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Optional;

@Layout(R.layout.fragment_360_videos)
public class Video360Fragment extends BaseFragment<Video360Presenter> implements Video360Presenter.View {

    @Optional
    @InjectView(R.id.recyclerViewFeatured)
    protected RecyclerView recyclerViewFeatured;

    @Optional
    @InjectView(R.id.recyclerViewRecent)
    protected RecyclerView recyclerViewRecent;

    @Optional
    @InjectView(R.id.recyclerViewAll)
    protected RecyclerView recyclerViewAll;

    @InjectView(R.id.containerLandscape)
    protected ScrollView scrollView;

    private BaseArrayListAdapter<Video360> adapterFeatured;
    private BaseArrayListAdapter<Video360> adapterRecent;
    private BaseArrayListAdapter<Video360> adapterAll;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        setUp();
    }

    private void setUp() {
        if (ViewUtils.isLandscapeOrientation(getActivity())) {
            scrollView.setVisibility(View.VISIBLE);
            LinearLayoutManager linearLayoutManagerFeatured = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            LinearLayoutManager linearLayoutManagerRecent = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerViewFeatured.setLayoutManager(linearLayoutManagerFeatured);
            recyclerViewRecent.setLayoutManager(linearLayoutManagerRecent);

            adapterFeatured = new BaseArrayListAdapter<>(getActivity(), (Injector) getActivity());
            adapterRecent = new BaseArrayListAdapter<>(getActivity(), (Injector) getActivity());

            adapterFeatured.registerCell(Video360.class, Video360Cell.class);
            adapterRecent.registerCell(Video360.class, Video360SmallCell.class);

            recyclerViewFeatured.setAdapter(adapterFeatured);
            recyclerViewRecent.setAdapter(adapterRecent);

            getPresenter().fillFeatured();

            if (adapterAll != null) {
                adapterAll.clear();
                adapterAll.notifyDataSetChanged();
            }
        } else {
            scrollView.setVisibility(View.GONE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerViewAll.setLayoutManager(linearLayoutManager);

            adapterAll = new BaseArrayListAdapter<>(getActivity(), (Injector) getActivity());
            adapterAll.registerCell(Video360.class, Video360Cell.class);

            recyclerViewAll.setAdapter(adapterAll);

            getPresenter().fillAll();
            
            if (adapterFeatured != null) {
                adapterFeatured.clear();
                adapterFeatured.notifyDataSetChanged();
                adapterRecent.clear();
                adapterRecent.notifyDataSetChanged();
            }

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setUp();
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
    public BaseArrayListAdapter getAllAdapter() {
        return adapterAll;
    }


    @Override
    protected Video360Presenter createPresenter(Bundle savedInstanceState) {
        return new Video360Presenter(this);
    }

    @Override
    public void notifyItemChanged(CachedEntity videoEntity) {
        if (getFeaturedAdapter() != null) {
            getFeaturedAdapter().notifyDataSetChanged();
        }
        if (getRecentAdapter() != null) {
            getRecentAdapter().notifyDataSetChanged();
        }
        if (getAllAdapter() != null) {
            getAllAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void showDeleteDialog(CachedEntity cachedVideo) {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.delete_cached_video_title)
                .content(R.string.delete_cached_video_text)
                .positiveText(R.string.delete_photo_positiove)
                .negativeText(R.string.delete_photo_negative)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        getPresenter().onDeleteAction(cachedVideo);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
