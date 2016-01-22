package com.worldventures.dreamtrips.modules.settings.view.fragment;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.techery.spares.adapter.BaseDelegateAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.settings.bundle.SettingDetailsBundle;

import butterknife.InjectView;

public abstract class SettingsFragment<PRESENTER extends Presenter> extends BaseFragmentWithArgs<PRESENTER, SettingDetailsBundle> {

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
        adapter = new BaseDelegateAdapter(getActivity(), this);
        registerCells();
        recyclerView.setAdapter(adapter);
    }

    protected abstract void registerCells();

    private void setupToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(getArgs().settingsGroup.getTitle());
    }
}
