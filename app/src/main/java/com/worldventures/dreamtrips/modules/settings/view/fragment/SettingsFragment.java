package com.worldventures.dreamtrips.modules.settings.view.fragment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
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
@MenuResource(R.menu.menu_settings)
public class SettingsFragment extends BaseFragmentWithArgs<SettingsPresenter, SettingsBundle>
        implements SettingsPresenter.View, CellDelegate<Settings> {

    @InjectView(R.id.settings_list)
    RecyclerView recyclerView;
    @InjectView(R.id.settings_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.loading_view)
    ViewGroup loadingView;

    private MenuItem buttonDone;

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

    @Override
    protected void onMenuInflated(Menu menu) {
        super.onMenuInflated(menu);
        buttonDone = menu.findItem(R.id.done);
        buttonDone.setEnabled(getPresenter().isSettingsChanged());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done:
                getPresenter().applyChanges();
                break;
        }
        return super.onOptionsItemSelected(item);
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
        toolbar.setNavigationOnClickListener(v -> {
            if (getPresenter().isSettingsChanged()) {
                new MaterialDialog.Builder(getContext())
                        .title("Tralala")
                        .positiveText("Yes")
                        .negativeText("No")
                        .onPositive((materialDialog, dialogAction) -> {
                            getPresenter().applyChanges();
                        })
                        .show();
            } else {
                close();
            }
        });
    }

    @Override
    protected SettingsPresenter createPresenter(Bundle savedInstanceState) {
        return new SettingsPresenter(getArgs().settingsGroup, getArgs().settingsList);
    }

    @Override
    public void onCellClicked(Settings model) {
        validateDoneButton();
    }

    private void validateDoneButton() {
        buttonDone.setEnabled(getPresenter().isSettingsChanged());
    }

    @Override
    public void setSettings(List<Settings> settingsList) {
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

    @Override
    public void close() {
        getActivity().onBackPressed();
    }
}
