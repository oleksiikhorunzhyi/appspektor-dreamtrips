package com.worldventures.dreamtrips.module.dtl.repository;

import android.location.Location;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.RequestingPresenter;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class DtlLocationRepositoryTest {

    private DtlLocationRepository dtlLocationRepository;
    @Mock
    private RequestingPresenter requestingPresenter;
    @Mock
    private SnappyRepository db;
    @Mock
    private Location defaultLocation;

    @Before
    public void beforeEachTest() {
        dtlLocationRepository = new DtlLocationRepository(db);
        dtlLocationRepository.setRequestingPresenter(requestingPresenter);
    }

    @Test
    public void loadLocations_LocationsLoaded() {
        List<DtlLocation> locations = new ArrayList<>(asList(mock(DtlLocation.class),
                mock(DtlLocation.class)));

        doAnswer(invocation -> {
            ((DreamSpiceManager.SuccessListener<List<DtlLocation>>) invocation.getArguments()[1])
                    .onRequestSuccess(locations);
            return null;
        }).when(requestingPresenter).doRequest(any(SpiceRequest.class),
                any(DreamSpiceManager.SuccessListener.class));

        List<DtlLocation> locationsObtained = new ArrayList<>();
        dtlLocationRepository.attachListener(new DtlLocationRepository.LocationsLoadedListener() {
            @Override
            public void onLocationsLoaded(List<DtlLocation> locations) {
                locationsObtained.addAll(locations);
            }

            @Override
            public void onLocationsFailed(SpiceException exception) {

            }
        });

        dtlLocationRepository.loadNearbyLocations(defaultLocation);

        assertThat(locationsObtained).isEqualTo(locations);
    }

    @Test
    public void persistLocation_LocationPersisted() {
        DtlLocation location = mock(DtlLocation.class);

        dtlLocationRepository.persistLocation(location);

        assertThat(dtlLocationRepository.getSelectedLocation()).isEqualTo(location);
    }

    @Test
    public void persistLocation_LocationUpdated() {
        DtlLocation locationOld = new DtlLocation();
        locationOld.setId("oldId");
        dtlLocationRepository.persistLocation(locationOld);

        DtlLocation locationNew = new DtlLocation();
        locationNew.setId("newId");
        dtlLocationRepository.persistLocation(locationNew);

        assertThat(dtlLocationRepository.getSelectedLocation()).isEqualTo(locationNew);
    }
}
