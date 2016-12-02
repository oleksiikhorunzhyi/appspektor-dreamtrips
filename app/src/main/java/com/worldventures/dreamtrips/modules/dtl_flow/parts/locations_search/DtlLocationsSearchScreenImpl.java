package com.worldventures.dreamtrips.modules.dtl_flow.parts.locations_search;

import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlLocation;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlLocationCell;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import rx.Observable;

public class DtlLocationsSearchScreenImpl extends DtlLayout<DtlLocationsSearchScreen, DtlLocationsSearchPresenter, DtlLocationsSearchPath> implements DtlLocationsSearchScreen, CellDelegate<DtlLocation> {

   @Inject @ForActivity Provider<Injector> injectorProvider;
   //
   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.recyclerView) RecyclerView recyclerView;
   @InjectView(R.id.defaultCaption) View defaultCaption;
   @InjectView(R.id.progressBar) View progressBar;
   //
   BaseDelegateAdapter adapter;
   MenuItem searchItem;

   @Override
   protected void onPostAttachToWindowView() {
      initToolbar();
      //
      recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
      recyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources().getDrawable(R.drawable.list_divider), true));
      //
      adapter = new BaseDelegateAdapter<DtlLocation>(getContext(), injectorProvider.get());
      adapter.registerCell(ImmutableDtlLocation.class, DtlLocationCell.class);
      adapter.registerDelegate(ImmutableDtlLocation.class, this);
      //
      recyclerView.setAdapter(adapter);
      //
      configureSearch();
   }

   private void initToolbar() {
      toolbar.setTitle(R.string.dtl_locations_title);
      toolbar.inflateMenu(R.menu.menu_locations_search);
      toolbar.setNavigationIcon(R.drawable.back_icon);
      toolbar.setNavigationOnClickListener(view -> getPresenter().searchClosed());
   }

   private void configureSearch() {
      searchItem = toolbar.getMenu().findItem(R.id.action_search);
      if (searchItem != null) {
         SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
         searchView.setInputType(EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES);
         searchView.setIconified(false);
         // line below is magic - prevents empty string to be sent as query during screen creation
         searchView.setQuery("", false);
         RxSearchView.queryTextChanges(searchView)
               .skip(1) // binding sends initial value on subscribe
               .compose(RxLifecycle.bindView(this))
               .flatMap(charSequence -> Observable.just(charSequence.toString()))
               .subscribe(getPresenter()::search);
         //
         MenuItemCompat.expandActionView(searchItem);
         MenuItemCompat.setOnActionExpandListener(searchItem, searchViewExpandListener);
      }
   }

   @Override
   public void onCellClicked(DtlLocation location) {
      hideSoftInput();
      getPresenter().onLocationSelected(location);
   }

   @Override
   public void setItems(List<DtlLocation> dtlExternalLocations) {
      hideProgress();
      adapter.clearAndUpdateItems(dtlExternalLocations);
   }

   @Override
   public void showProgress() {
      progressBar.setVisibility(VISIBLE);
   }

   @Override
   public void hideProgress() {
      progressBar.setVisibility(GONE);
   }

   @Override
   public void toggleDefaultCaptionVisibility(boolean visible) {
      defaultCaption.setVisibility(visible ? VISIBLE : GONE);
   }

   private MenuItemCompat.OnActionExpandListener searchViewExpandListener = new MenuItemCompat.OnActionExpandListener() {
      @Override
      public boolean onMenuItemActionExpand(MenuItem item) {
         return true; // do nothing - always expanded
      }

      @Override
      public boolean onMenuItemActionCollapse(MenuItem item) {
         getPresenter().searchClosed();
         return true;
      }
   };

   ///////////////////////////////////////////////////////////////////////////
   // Boilerplate stuff
   ///////////////////////////////////////////////////////////////////////////

   public DtlLocationsSearchScreenImpl(Context context) {
      super(context);
   }

   public DtlLocationsSearchScreenImpl(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public DtlLocationsSearchPresenter createPresenter() {
      return new DtlLocationsSearchPresenterImpl(getContext(), injector);
   }
}
