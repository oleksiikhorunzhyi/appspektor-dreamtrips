package com.worldventures.dreamtrips.social.ui.settings.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.worldventures.core.modules.settings.model.SettingsGroup;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.social.ui.settings.bundle.SettingsBundle;
import com.worldventures.dreamtrips.social.ui.settings.view.cell.SettingsGroupCell;
import com.worldventures.dreamtrips.social.ui.settings.view.presenter.SettingsGroupPresenter;

import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.fragment_group_settings)
public class SettingsGroupFragment extends BaseFragment<SettingsGroupPresenter> implements SettingsGroupPresenter.View, CellDelegate<SettingsGroup> {

   @InjectView(R.id.settings_list) RecyclerView recyclerView;

   private BaseDelegateAdapter adapter;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
      recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
      adapter = new BaseDelegateAdapter(getContext(), this);
      adapter.registerCell(SettingsGroup.class, SettingsGroupCell.class);
      adapter.registerDelegate(SettingsGroup.class, this);
      recyclerView.setAdapter(adapter);
   }

   @Override
   protected SettingsGroupPresenter createPresenter(Bundle savedInstanceState) {
      return new SettingsGroupPresenter();
   }

   @Override
   public void setSettings(List<SettingsGroup> settings) {
      adapter.setItems(settings);
      adapter.notifyDataSetChanged();
   }

   @Override
   public void openSettings(Route route, SettingsGroup model) {
      router.moveTo(route, NavigationConfigBuilder.forActivity().toolbarConfig(ToolbarConfig.Builder.create()
            .visible(false)
            .build()).data(new SettingsBundle(model)).build());
   }

   @Override
   public void onCellClicked(SettingsGroup model) {
      getPresenter().handleCellClick(model);
   }
}
