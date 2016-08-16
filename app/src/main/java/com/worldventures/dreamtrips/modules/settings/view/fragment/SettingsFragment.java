package com.worldventures.dreamtrips.modules.settings.view.fragment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.adapter.BaseDelegateAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.settings.bundle.SettingsBundle;
import com.worldventures.dreamtrips.modules.settings.model.FlagSetting;
import com.worldventures.dreamtrips.modules.settings.model.SelectSetting;
import com.worldventures.dreamtrips.modules.settings.model.Setting;
import com.worldventures.dreamtrips.modules.settings.view.cell.SettingsFlagCell;
import com.worldventures.dreamtrips.modules.settings.view.cell.SettingsSelectCell;
import com.worldventures.dreamtrips.modules.settings.view.presenter.SettingsPresenter;

import java.util.List;

import butterknife.InjectView;

public abstract class SettingsFragment extends RxBaseFragmentWithArgs<SettingsPresenter, SettingsBundle>
        implements SettingsPresenter.View {

    @InjectView(R.id.settings_list) RecyclerView recyclerView;
    @InjectView(R.id.settings_toolbar) Toolbar toolbar;
    @InjectView(R.id.loading_view) ViewGroup loadingView;

    protected BaseDelegateAdapter adapter;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BaseDelegateAdapter(getActivity(), this);
        registerCells();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        setupToolbar();
    }

    protected void registerCells() {
        adapter.registerCell(FlagSetting.class, SettingsFlagCell.class);
        adapter.registerCell(SelectSetting.class, SettingsSelectCell.class);
    }

    protected void setupToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(getArgs().settingsGroup.getTitle());
    }

    protected void close() {
        getActivity().onBackPressed();
    }

    @Override
    protected SettingsPresenter createPresenter(Bundle savedInstanceState) {
        return new SettingsPresenter(getArgs().settingsGroup);
    }

    @Override
    public void setSettings(List<Setting> settingsList) {
        adapter.setItems(settingsList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        loadingView.setVisibility(View.GONE);
    }
}
