package com.worldventures.dreamtrips.modules.dtl.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.dtl.DtlPlaceHelper;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceType;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPlaceDetailsPresenter;

import butterknife.InjectView;
import me.relex.circleindicator.CircleIndicator;

@Layout(R.layout.fragment_dtl_place_details)
public class DtlPlaceDetailsFragment
        extends BaseFragmentWithArgs<DtlPlaceDetailsPresenter, DtlPlace>
        implements DtlPlaceDetailsPresenter.View {


    @InjectView(R.id.place_details_cover_pager)
    ViewPager coverPager;
    @InjectView(R.id.place_details_cover_pager_indicator)
    CircleIndicator coverPagerIndicator;
    @InjectView(R.id.place_details_title)
    TextView title;
    @InjectView(R.id.place_details_rating)
    RatingBar rating;
    @InjectView(R.id.place_details_points_badge)
    ImageView earnPointsBadge;
    @InjectView(R.id.place_details_category)
    TextView category;
    @InjectView(R.id.place_details_pricing)
    RatingBar pricing;
    @InjectView(R.id.place_details_earn_wrapper)
    ViewGroup earnWrapper;
    @InjectView(R.id.place_details_earn)
    Button earn;
    @InjectView(R.id.place_details_estimate_points)
    Button estimatePoints;
    @InjectView(R.id.place_details_merchant_wrapper)
    ViewGroup merchantWrapper;
    @InjectView(R.id.place_details_suggest_merchant)
    Button merchant;
    @InjectView(R.id.place_details_description)
    TextView description;
    @InjectView(R.id.place_details_additional)
    ViewGroup additionalContainer;
    SupportMapFragment destinationMap;

    DtlPlaceHelper helper;

    @Override
    protected DtlPlaceDetailsPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlPlaceDetailsPresenter(getArgs());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        helper = new DtlPlaceHelper(activity);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        destinationMap = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.place_details_map);
    }

    @Override
    public void setPlace(DtlPlace place) {
        title.setText(place.getName());
        description.setText(place.getDescription());
        category.setText(helper.getFirstCategoryName(place));
        pricing.setRating(place.getAvgPrice());
        setType(place.getType());
        setAdditional(place);
        setMap(place);
    }

    private void setType(DtlPlaceType type) {
        int offerVisibility = (type == DtlPlaceType.OFFER) ? View.VISIBLE : View.GONE;
        int merchantVisibility = (type == DtlPlaceType.DINING) ? View.VISIBLE : View.GONE;
        earnPointsBadge.setVisibility(offerVisibility);
        earnWrapper.setVisibility(offerVisibility);
        merchantWrapper.setVisibility(merchantVisibility);
    }

    private void setAdditional(DtlPlace place) {
        Queryable.from(helper.getContactsData(place)).forEachR(contact -> {
            TextView contactView = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.list_item_dtl_place_contact, additionalContainer, false);
            contactView.setText(contact.text);
            contactView.setCompoundDrawablesWithIntrinsicBounds(contact.icon, null, null, null);
            additionalContainer.addView(contactView);
        });

    }

    private void setMap(DtlPlace place) {
        destinationMap.getMapAsync(googleMap -> {
            LatLng pos = new LatLng(place.getLocation().getLat(), place.getLocation().getLng());
            googleMap.addMarker(new MarkerOptions()
                            .position(pos)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_pin))
            );
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(pos));
        });
    }


}
