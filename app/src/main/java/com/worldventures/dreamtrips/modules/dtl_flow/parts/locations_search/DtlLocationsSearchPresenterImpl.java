package com.worldventures.dreamtrips.modules.dtl_flow.parts.locations_search;

import android.content.Context;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.LocationSearchEvent;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.SearchLocationAction;
import com.worldventures.dreamtrips.modules.dtl.view.util.ProxyApiErrorView;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.DtlMerchantsPath;

import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import io.techery.janet.helper.ActionStateSubscriber;

public class DtlLocationsSearchPresenterImpl extends DtlPresenterImpl<DtlLocationsSearchScreen, DtlLocationsSearchViewState> implements DtlLocationsSearchPresenter {

   @Inject DtlLocationInteractor locationInteractor;

   public DtlLocationsSearchPresenterImpl(Context context, Injector injector) {
      super(context);
      injector.inject(this);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      getView().toggleDefaultCaptionVisibility(true);

      connectLocationsSearch();
      apiErrorViewAdapter.setView(new ProxyApiErrorView(getView(), () -> getView().hideProgress()));
   }

   private void connectLocationsSearch() {
      locationInteractor.searchLocationPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<SearchLocationAction>()
                  .onStart(command -> getView().showProgress())
                  .onFail(apiErrorViewAdapter::handleError)
                  .onSuccess(this::onSearchFinished));
   }

   private void onSearchFinished(SearchLocationAction action) {
      List<DtlLocation> locations = action.getResult();
      getView().hideProgress();
      getView().setItems(locations);
      if (TextUtils.isEmpty(action.getQuery()) && !locations.isEmpty()) {
         getView().toggleDefaultCaptionVisibility(false);
      }
   }

   @Override
   public void searchClosed() {
      sendSearchAction("");
      Flow.get(getContext()).goBack();
   }

   @Override
   public void search(String query) {
      getView().toggleDefaultCaptionVisibility(query.isEmpty());
      sendSearchAction(query);
   }

   private void sendSearchAction(String query) {
      locationInteractor.search(query.trim());
   }

   @Override
   public void onLocationSelected(DtlLocation location) {
      analyticsInteractor.analyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(LocationSearchEvent.create(location)));
      locationInteractor.changeSourceLocation(location);

      navigateToMerchants();
   }

   private void navigateToMerchants() {
      History history = History.single(DtlMerchantsPath.getDefault());
      Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
   }

   @Override
   public int getToolbarMenuRes() {
      return R.menu.menu_locations_search;
   }

   @Override
   public void onToolbarMenuPrepared(Menu menu) {
   }

   @Override
   public boolean onToolbarMenuItemClick(MenuItem item) {
      return false;
   }
}
