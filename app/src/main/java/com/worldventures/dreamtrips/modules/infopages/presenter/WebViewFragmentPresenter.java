package com.worldventures.dreamtrips.modules.infopages.presenter;

import com.worldventures.dreamtrips.core.janet.api_lib.NewDreamTripsHttpService;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.ConnectionState;

public class WebViewFragmentPresenter<T extends WebViewFragmentPresenter.View> extends Presenter<T> {

   protected String url;
   private boolean inErrorState;

   public WebViewFragmentPresenter(String url) {
      this.url = url;
   }

   @Override
   public void takeView(T view) {
      super.takeView(view);
      load();
   }

   @Override
   public void onResume() {
      super.onResume();
      if (inErrorState) load();
   }

   public void noInternetConnection() {
      connectionStatePublishSubject.onNext(ConnectionState.DISCONNECTED);
   }

   protected void load() {
      view.load(url);
   }

   protected void reload() {
      view.reload(url);
   }

   public void onReload() {
      reload();
   }

   public void pageLoaded(String url) {
      // TODO Check if view is still attached.
      // To improve this and remove check we need to refactor our StaticInfoFragment,
      // saving its state and detecting if page was already loaded
      if (view != null) view.hideLoadingProgress();
   }

   public void setInErrorState(boolean inErrorState) {
      this.inErrorState = inErrorState;
   }

   public String getAuthToken() {
      return NewDreamTripsHttpService.getAuthorizationHeader(appSessionHolder.get().get().getApiToken());
   }

   public void track(Route route) {
      switch (route) {
         case OTA:
            TrackingHelper.ota(getAccountUserId());
            break;
         case ENROLL_MEMBER:
            TrackingHelper.enrollMember(getAccountUserId());
            break;
         case ENROLL_MERCHANT:
            TrackingHelper.enrollMerchant(getAccountUserId());
            break;
      }
   }

   public interface View extends RxView {

      void load(String localizedUrl);

      void reload(String localizedUrl);

      void setRefreshing(boolean refreshing);

      void showError(int code);

      void hideLoadingProgress();
   }
}
