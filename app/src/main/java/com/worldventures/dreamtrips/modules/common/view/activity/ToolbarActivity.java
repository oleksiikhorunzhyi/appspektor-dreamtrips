package com.worldventures.dreamtrips.modules.common.view.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;

import butterknife.InjectView;

public abstract class ToolbarActivity<T extends ActivityPresenter> extends ActivityWithPresenter<T> {
   @InjectView(R.id.toolbar_actionbar) protected Toolbar toolbar;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setSupportActionBar(toolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowHomeEnabled(true);
      getSupportActionBar().setTitle(getToolbarTitle());
      toolbar.setBackgroundColor(getResources().getColor(R.color.theme_main));
   }

   protected abstract int getToolbarTitle();

}
