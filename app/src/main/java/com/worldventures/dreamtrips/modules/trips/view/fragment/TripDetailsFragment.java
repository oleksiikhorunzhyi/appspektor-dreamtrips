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
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.core.utils.TextViewLinkHandler;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.events.ImageClickedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.SweetDialogHelper;
import com.worldventures.dreamtrips.modules.common.view.adapter.ContentAdapter;
import com.worldventures.dreamtrips.modules.membership.bundle.UrlBundle;
import com.worldventures.dreamtrips.modules.trips.model.ContentItem;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.presenter.TripDetailsPresenter;
import com.worldventures.dreamtrips.modules.trips.view.bundle.TripDetailsBundle;
import com.worldventures.dreamtrips.modules.trips.view.util.TripDetailsViewInjector;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

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
      }
      return super.onOptionsItemSelected(item);
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      sweetDialogHelper = new SweetDialogHelper();
      signUp.setMovementMethod(new TextViewLinkHandler(url -> router.moveTo(Route.ENROLL_UPGRADE)));

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
   }

   public void onEvent(ImageClickedEvent event) {
      getPresenter().onItemClick(tripDetailsViewInjector.getCurrentActivePhotoPosition());
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
   public void hideBookIt() {
      textViewBookIt.setEnabled(false);
      textViewBookIt.setBackgroundColor(getResources().getColor(R.color.tripButtonDisabled));
   }

   @Override
   public void openFullscreen(FullScreenImagesBundle data) {
      router.moveTo(Route.FULLSCREEN_PHOTO_LIST, NavigationConfigBuilder.forActivity()
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .data(data)
            .build());
   }

   @Override
   public void openBookIt(String url) {
      router.moveTo(Route.BOOK_IT, NavigationConfigBuilder.forActivity().data(new UrlBundle(url)).build());
   }

   @Override
   public void setup(TripModel tripModel) {
      tripDetailsViewInjector.initTripData(tripModel, getPresenter().getAccount());
      if (toolbarLandscape != null)
         ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(tripModel.getName());
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
