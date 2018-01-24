package com.worldventures.dreamtrips.modules.trips.view.fragment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.linearlistview.LinearListView;
import com.worldventures.dreamtrips.social.ui.membership.bundle.UrlBundle;
import com.worldventures.dreamtrips.social.util.event_delegate.ImagePresenterClickEventDelegate;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.annotations.MenuResource;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.core.utils.TextViewLinkHandler;
import com.worldventures.dreamtrips.modules.common.view.adapter.ContentAdapter;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.ConnectionState;
import com.worldventures.dreamtrips.modules.trips.model.ContentItem;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.presenter.TripDetailsPresenter;
import com.worldventures.dreamtrips.modules.trips.view.bundle.TripDetailsBundle;
import com.worldventures.dreamtrips.modules.trips.view.bundle.TripViewPagerBundle;
import com.worldventures.dreamtrips.modules.trips.view.util.TripDetailsViewInjector;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.util.SweetDialogHelper;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.StaticInfoFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import rx.Observable;

@Layout(R.layout.fragment_trip_details)
@MenuResource(R.menu.menu_detailed_trip)
public class TripDetailsFragment extends RxBaseFragmentWithArgs<TripDetailsPresenter, TripDetailsBundle> implements TripDetailsPresenter.View {

   @InjectView(R.id.textViewReload) TextView textViewReloadTripDetails;
   @InjectView(R.id.listViewContent) LinearListView linearListView;
   @InjectView(R.id.progressBarDetailLoading) ProgressBar progressBarDetailLoading;
   @InjectView(R.id.textViewBookIt) TextView textViewBookIt;
   @InjectView(R.id.signUp) TextView signUp;
   @Optional @InjectView(R.id.toolbar_actionbar) Toolbar toolbar;
   @Optional @InjectView(R.id.toolbar_actionbar_landscape) Toolbar toolbarLandscape;

   @Inject ImagePresenterClickEventDelegate imagePresenterClickEventDelegate;

   private TripDetailsViewInjector tripDetailsViewInjector;
   private SweetDialogHelper sweetDialogHelper;

   @Override
   protected TripDetailsPresenter createPresenter(Bundle savedInstanceState) {
      return new TripDetailsPresenter(getArgs().tripModel());
   }

   @OnClick(R.id.textViewBookIt)
   public void bookIt() {
      getPresenter().actionBookIt();
   }

   @Override
   public void onPrepareOptionsMenu(Menu menu) {
      tripDetailsViewInjector.initMenuItems(menu);
      super.onPrepareOptionsMenu(menu);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.action_like:
            getPresenter().likeTrip();
            return true;
         case R.id.action_add_to_bucket:
            getPresenter().addTripToBucket();
            return true;
         default:
            break;
      }
      return super.onOptionsItemSelected(item);
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      sweetDialogHelper = new SweetDialogHelper();
      signUp.setMovementMethod(new TextViewLinkHandler(url -> router.moveTo(StaticInfoFragment.EnrollUpgradeFragment.class,
            NavigationConfigBuilder.forActivity().build())));

      tripDetailsViewInjector = new TripDetailsViewInjector(rootView);
      if (toolbar != null) {
         ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
         ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
         toolbar.getBackground().setAlpha(0);
      } else if (toolbarLandscape != null) {
         ((AppCompatActivity) getActivity()).setSupportActionBar(toolbarLandscape);
         ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         toolbarLandscape.setBackgroundColor(getResources().getColor(R.color.theme_main));
         toolbarLandscape.getBackground().setAlpha(255);
      }

      tripDetailsViewInjector.initGalleryData(getChildFragmentManager(), getPresenter().getFilteredImages());

      subscribeToTripImagesClicks();
   }

   @Override
   public void initConnectionOverlay(Observable<ConnectionState> connectionStateObservable, Observable<Void> stopper) {
      if (ViewUtils.isLandscapeOrientation(getContext())) {
         super.initConnectionOverlay(connectionStateObservable, stopper);
      }
   }

   @Override
   protected int getContentLayoutId() {
      // Trip details in landscape mode has specific connection overlay parent view
      if (ViewUtils.isLandscapeOrientation(getContext())) {
         return R.id.trip_landscape_details_content_layout;
      }
      return super.getContentLayoutId();
   }

   private void subscribeToTripImagesClicks() {
      imagePresenterClickEventDelegate.getObservable().compose(bindUntilDropViewComposer())
            .subscribe(imagePathHolder -> getPresenter()
                  .onItemClick(tripDetailsViewInjector.getCurrentActivePhotoPosition()));
   }

   @Override
   public void setContent(List<ContentItem> contentItems) {
      if (isAdded()) {
         progressBarDetailLoading.setVisibility(View.GONE);
         if (contentItems != null) {
            linearListView.setAdapter(new ContentAdapter(contentItems, getActivity()));
         } else {
            textViewReloadTripDetails.setVisibility(View.VISIBLE);
         }
      }
   }

   @OnClick(R.id.textViewReload)
   void onReloadClicked() {
      textViewReloadTripDetails.setVisibility(View.GONE);
      progressBarDetailLoading.setVisibility(View.VISIBLE);
      getPresenter().loadTripDetails();
   }

   @Override
   public void showSignUp() {
      signUp.setVisibility(View.VISIBLE);
   }

   @Override
   public void disableBookIt() {
      textViewBookIt.setEnabled(false);
      textViewBookIt.setBackgroundColor(getResources().getColor(R.color.tripButtonDisabled));
   }

   @Override
   public void soldOutTrip() {
      textViewBookIt.setText(R.string.sold_out_trip);
      textViewBookIt.setEnabled(false);
      textViewBookIt.setBackgroundColor(getResources().getColor(R.color.tripButtonDisabled));
   }

   @Override
   public void openFullscreen(TripViewPagerBundle data) {
      router.moveTo(TripViewPagerFragment.class, NavigationConfigBuilder.forActivity()
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .data(data)
            .build());
   }

   @Override
   public void openBookIt(String url) {
      router.moveTo(StaticInfoFragment.BookItFragment.class, NavigationConfigBuilder.forActivity()
            .data(new UrlBundle(url))
            .build());
   }

   @Override
   public void setup(TripModel tripModel) {
      tripDetailsViewInjector.initTripData(tripModel);
      if (toolbarLandscape != null) {
         ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(tripModel.getName());
      }
   }

   @Override
   public void tripAddedToBucketItem(BucketItem bucketItem) {
      sweetDialogHelper.notifyItemAddedToBucket(getActivity(), bucketItem);
   }

   @Override
   public void tripLiked(TripModel tripModel) {
      sweetDialogHelper.notifyTripLiked(getActivity(), tripModel);
   }

   @Override
   public boolean isVisibleOnScreen() {
      return ViewUtils.isPartVisibleOnScreen(this);
   }
}
