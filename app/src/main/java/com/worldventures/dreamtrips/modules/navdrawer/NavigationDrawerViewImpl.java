package com.worldventures.dreamtrips.modules.navdrawer;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.NavigationDrawerListener;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.social.di.SocialAppModule;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.NpaLinearLayoutManager;
import com.worldventures.dreamtrips.modules.common.view.fragment.navigationdrawer.NavigationDrawerAdapter;
import com.worldventures.dreamtrips.modules.common.view.fragment.navigationdrawer.NavigationHeader;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NavigationDrawerViewImpl extends LinearLayout implements NavigationDrawerView, NavigationDrawerListener {

   @InjectView(R.id.drawerList) protected RecyclerView recyclerView;
   @InjectView(R.id.version) protected TextView version;

   private NavigationDrawerPresenter navigationDrawerPresenter;

   private NavigationDrawerAdapter adapter;

   private ComponentDescription currentComponent;

   public NavigationDrawerViewImpl(Context context) {
      super(context);
      init(context);
   }

   public NavigationDrawerViewImpl(Context context, AttributeSet attrs) {
      super(context, attrs);
      init(context);
   }

   public NavigationDrawerViewImpl(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init(context);
   }

   @TargetApi(Build.VERSION_CODES.LOLLIPOP)
   public NavigationDrawerViewImpl(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);
      init(context);
   }

   private void init(Context context) {
      ButterKnife.inject(this, LayoutInflater.from(context).inflate(R.layout.fragment_navigation_drawer, this, true));
      recyclerView.setLayoutManager(new NpaLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
      setVersion();
      setupViews();
   }

   public void setupViews() {
      if (ViewUtils.isLandscapeOrientation(getContext())) {
         recyclerView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bg_tablet_menu));
         version.setTextColor(ContextCompat.getColor(getContext(), R.color.grey));
      } else {
         recyclerView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
         version.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
      }
   }

   @Override
   public void setNavigationDrawerPresenter(NavigationDrawerPresenter navigationDrawerPresenter) {
      this.navigationDrawerPresenter = navigationDrawerPresenter;
   }

   @Override
   public void setData(List<ComponentDescription> components) {
      adapter = new NavigationDrawerAdapter(components);
      adapter.setNavigationDrawerCallbacks(this);
      recyclerView.setAdapter(adapter);
   }

   @Override
   public void setUser(User user) {
      if (ViewUtils.isLandscapeOrientation(getContext())) return;
      //
      if (adapter.setHeader(createNavigationHeader(user))) {
         adapter.notifyItemInserted(0);
      } else {
         adapter.notifyItemChanged(0);
      }
   }

   private void setVersion() {
      try {
         version.setText(getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName
               + "-" + BuildConfig.versionBuild);
      } catch (PackageManager.NameNotFoundException e) {
         e.printStackTrace();
      }
   }

   private NavigationHeader createNavigationHeader(User user) {
      return new NavigationHeader(user);
   }

   @Override
   public void onNavigationDrawerItemSelected(ComponentDescription newComponent) {
      if (newComponent.getKey().equals(SocialAppModule.LOGOUT)) {
         navigationDrawerPresenter.onLogout();
         return;
      }

      boolean updateComponentSelection = currentComponent == null || !newComponent.getKey()
            .equalsIgnoreCase(currentComponent.getKey());

      if (updateComponentSelection) {
         navigationDrawerPresenter.onItemSelected(newComponent);
      } else {
         navigationDrawerPresenter.onItemReselected(newComponent);
      }
   }

   @Override
   public void onNavigationDrawerItemReselected(ComponentDescription newComponent) {
      //
   }

   @Override
   public void setCurrentComponent(ComponentDescription newComponent) {
      recyclerView.post(() -> {
         currentComponent = newComponent;
         if (adapter != null) adapter.selectComponent(newComponent);
      });
   }

   @Override
   public void setNotificationCount(int count) {
      if (adapter != null) adapter.setNotificationCount(count);
   }

   @Override
   public void setUnreadMessagesCount(int count) {
      if (adapter != null) adapter.setUnreadMessageCount(count);
   }
}
