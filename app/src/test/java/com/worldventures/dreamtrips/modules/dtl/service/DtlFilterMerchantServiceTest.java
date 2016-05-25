package com.worldventures.dreamtrips.modules.dtl.service;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlLocationHelper;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterParameters;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.ImmutableDtlFilterParameters;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlFilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlFilterMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.settings.model.Setting;

import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.Collections;

import io.techery.janet.ActionState;
import rx.Observable;
import rx.functions.Func1;
import rx.observers.TestSubscriber;

import static com.worldventures.dreamtrips.core.test.AssertUtil.assertActionSuccess;
import static com.worldventures.dreamtrips.modules.settings.util.SettingsFactory.DISTANCE_UNITS;
import static com.worldventures.dreamtrips.modules.settings.util.SettingsFactory.KILOMETERS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest(DtlLocationHelper.class)
public class DtlFilterMerchantServiceTest extends DtlBaseMerchantServiceTest {

    private final static Setting testDistanceSetting = new Setting(DISTANCE_UNITS, Setting.Type.SELECT, KILOMETERS);

    private DtlFilterMerchantService filterMerchantService;
    private Location testLocation;

    @Before
    public void setup() {
        init();
        LocationDelegate locationDelegate = mock(LocationDelegate.class);

        testLocation = mock(Location.class);
        when(testLocation.getLatitude()).thenReturn(Double.valueOf(1));
        when(testLocation.getLongitude()).thenReturn(Double.valueOf(1));
        when(locationDelegate.getLastKnownLocationOrEmpty()).thenReturn(Observable.just(testLocation));
        when(locationDelegate.requestLocationUpdate()).thenReturn(Observable.just(testLocation));

        mockStatic(DtlLocationHelper.class);
        PowerMockito.when(DtlLocationHelper.selectAcceptableLocation(any(Location.class), any(DtlLocation.class)))
                .thenReturn(new LatLng(1, 1));
        PowerMockito.when(DtlLocationHelper.calculateDistance(any(LatLng.class), any(LatLng.class)))
                .thenReturn(Double.valueOf(1));

        filterMerchantService = new DtlFilterMerchantService(merchantService, locationService, locationDelegate, janet);
        when(db.getSettings()).thenReturn(Collections.singletonList(testDistanceSetting));
        when(db.getLastSelectedOffersOnlyToggle()).thenReturn(false);
        when(db.getAmenities()).thenReturn(Collections.emptyList());
    }

    @Test
    public void testFilterInit() {
        checkFilterAction(DtlFilterDataAction.init(),
                action -> action.getResult().isDefault()
                        && action.getResult().getDistanceType() == DistanceType.provideFromSetting(testDistanceSetting));
    }

    @Test
    public void testFilterReset() {
        checkFilterAction(DtlFilterDataAction.reset(), action -> action.getResult() != null);
    }

    @Test
    public void testFilterAmenitiesUpdate() {
        DtlMerchantAttribute amenity = new DtlMerchantAttribute("test");
        checkFilterAction(DtlFilterDataAction.amenitiesUpdate(Collections.singletonList(amenity)),
                action -> action.getResult().hasAmenities() && action.getResult().getAmenities().contains(amenity));
    }

    @Test
    public void testFilterApplyParams() {
        double maxDistance = 500;
        DtlFilterParameters parameters = ImmutableDtlFilterParameters.builder()
                .minPrice(2)
                .maxPrice(3)
                .maxDistance(maxDistance)
                .build();
        checkFilterAction(DtlFilterDataAction.applyParams(parameters),
                action -> !action.getResult().isDefault() && action.getResult().getMaxDistance() == maxDistance);
    }

    @Test
    public void testFilterApplySearch() {
        String query = "test";
        checkFilterAction(DtlFilterDataAction.applySearch(query),
                action -> action.getResult().getSearchQuery().equals(query));
    }

    @Test
    public void testFilterApplyOffersOnly() {
        checkFilterAction(DtlFilterDataAction.applyOffersOnly(true),
                action -> action.getResult().isOffersOnly());
        verify(db, times(1)).saveLastSelectedOffersOnlyToogle(eq(true));
    }

    @Test
    public void testDtlFilterMerchantsAction() {
        TestSubscriber<ActionState<DtlFilterMerchantsAction>> subscriber = new TestSubscriber<>();
        filterMerchantService.filterMerchantsActionPipe().observe().subscribe(subscriber);
        locationService.locationPipe().send(
                DtlLocationCommand.change(
                        ImmutableDtlManualLocation.builder()
                                .locationSourceType(LocationSourceType.FROM_MAP)
                                .analyticsName("test")
                                .coordinates(new com.worldventures.dreamtrips.modules.trips.model.Location(testLocation))
                                .longName("test")
                                .build()));
        checkDtlMerchantsAction();
        subscriber.unsubscribe();
        assertActionSuccess(subscriber, action -> !action.getResult().isEmpty());
    }

    private void checkFilterAction(DtlFilterDataAction filterDataAction, Func1<DtlFilterDataAction, Boolean> assertPredicate) {
        TestSubscriber<ActionState<DtlFilterDataAction>> subscriber = new TestSubscriber<>();
        filterMerchantService.filterDataPipe()
                .createObservable(filterDataAction)
                .subscribe(subscriber);
        assertActionSuccess(subscriber, assertPredicate);
    }


}
