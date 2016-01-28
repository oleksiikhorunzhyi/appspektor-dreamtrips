package com.worldventures.dreamtrips.modules.trips.view.fragment;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckedTextView;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedDetailsBundle;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.presenter.TripMapInfoPresenter;
import com.worldventures.dreamtrips.modules.trips.view.bundle.TripMapInfoBundle;
import com.worldventures.dreamtrips.modules.trips.view.util.TripDetailsViewInjector;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_trip_pin)
public class TripMapInfoFragment extends BaseFragmentWithArgs<TripMapInfoPresenter, TripMapInfoBundle> implements TripMapInfoPresenter.View {

    TripDetailsViewInjector tripDetailsViewInjector;

    @InjectView(R.id.imageViewTripImage)
    protected SimpleDraweeView imageViewTripImage;
    @InjectView(R.id.imageViewAddToBucket)
    protected CheckedTextView addToBucketView;
    @InjectView(R.id.imageViewLike)
    protected CheckedTextView likeView;
    @InjectView(R.id.itemLayout)
    protected RelativeLayout itemLayout;

    @Override
    protected TripMapInfoPresenter createPresenter(Bundle savedInstanceState) {
        return new TripMapInfoPresenter(getArgs().tripModel);
    }

    @Override
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void afterCreateView(final View rootView) {
        super.afterCreateView(rootView);
        tripDetailsViewInjector = new TripDetailsViewInjector(rootView);

        ViewTreeObserver viewTreeObserver = rootView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int fragmentHeight = itemLayout.getHeight();
                int offset;
                if (ViewUtils.isLandscapeOrientation(getActivity())) {
                    offset = fragmentHeight / 2;
                    offset += getResources().getDimensionPixelSize(R.dimen.spacing_huge);
                } else {
                    int centerY = rootView.getHeight() / 2;
                    int resultY = fragmentHeight + getResources().getDimensionPixelSize(R.dimen.spacing_huge);
                    offset = resultY - centerY;

                }
                getPresenter().sendOffset(offset);
                ViewTreeObserver obs = rootView.getViewTreeObserver();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    @Override
    public void setImage(String image) {
        imageViewTripImage.setImageURI(Uri.parse(image));
    }

    @Override
    public void openDetails(FeedDetailsBundle bundle) {
        router.moveTo(Route.FEED_ENTITY_DETAILS, NavigationConfigBuilder.forActivity()
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .data(bundle)
                .build());
    }

    @Override
    public void showLayout() {
        itemLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.itemLayout)
    void onClick() {
        getPresenter().onClick();
    }

    @OnClick(R.id.imageViewAddToBucket)
    void onAddToBucket() {
        getPresenter().addTripToBucket();
    }

    @OnClick(R.id.imageViewLike)
    void onLike() {
        getPresenter().likeTrip();
    }

    @Override
    public void setup(TripModel tripModel) {
        tripDetailsViewInjector.initTripData(tripModel, getPresenter().getAccount());
        addToBucketView.setChecked(tripModel.isInBucketList());
        addToBucketView.setEnabled(!tripModel.isInBucketList());
        likeView.setChecked(tripModel.isLiked());
    }

}
