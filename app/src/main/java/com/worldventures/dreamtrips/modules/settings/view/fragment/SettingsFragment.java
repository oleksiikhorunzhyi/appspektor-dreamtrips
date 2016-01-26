package com.worldventures.dreamtrips.modules.settings.view.fragment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.settings.bundle.SettingsBundle;
import com.worldventures.dreamtrips.modules.settings.model.FlagSettings;
import com.worldventures.dreamtrips.modules.settings.model.SelectSettings;
import com.worldventures.dreamtrips.modules.settings.model.Settings;
import com.worldventures.dreamtrips.modules.settings.view.cell.SettingsFlagCell;
import com.worldventures.dreamtrips.modules.settings.view.cell.SettingsSelectCell;
import com.worldventures.dreamtrips.modules.settings.view.presenter.SettingsPresenter;

import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.fragment_settings)
public class SettingsFragment extends BaseFragmentWithArgs<SettingsPresenter, SettingsBundle>
        implements SettingsPresenter.View, CellDelegate<Settings> {

    @InjectView(R.id.settings_list)
    RecyclerView recyclerView;
    @InjectView(R.id.settings_toolbar)
    Toolbar toolbar;

    protected BaseDelegateAdapter adapter;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        setupToolbar();
        //
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BaseDelegateAdapter(getActivity(), this);
        registerCells();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getPresenter().applyChanges();
    }

    protected void registerCells() {
        adapter.registerCell(FlagSettings.class, SettingsFlagCell.class);
        adapter.registerCell(SelectSettings.class, SettingsSelectCell.class);
        adapter.registerDelegate(FlagSettings.class, this);
        adapter.registerDelegate(SelectSettings.class, this);
    }

    private void setupToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(getArgs().settingsGroup.getTitle());
    }

    @Override
    protected SettingsPresenter createPresenter(Bundle savedInstanceState) {
        return new SettingsPresenter(getArgs().settingsGroup, getArgs().settingsList);
    }

    @Override
    public void onCellClicked(Settings model) {

    }

    @Override
    public void setSettings(List<Settings> settingsList) {
        adapter.setItems(settingsList);
        adapter.notifyDataSetChanged();
    }
}
