package com.worldventures.dreamtrips.module.dtl.store;

import android.location.Location;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.RequestingPresenter;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationStore;

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
public class DtlLocationStoreTest {

    private DtlLocationStore dtlLocationStore;
    @Mock
    private RequestingPresenter requestingPresenter;
    @Mock
    private SnappyRepository db;
    @Mock
    private Location defaultLocation;

    @Before
    public void beforeEachTest() {
        dtlLocationStore = new DtlLocationStore(db);
        dtlLocationStore.setRequestingPresenter(requestingPresenter);
    }

    @Test
    public void loadLocations_LocationsLoaded() {
        List<DtlLocation> locations = new ArrayList<>(asList(mock(DtlLocation.class),
                mock(DtlLocation.class)));

        doAnswer(invocation -> {
            ((DreamSpiceManager.SuccessListener<List<DtlLocation>>)invocation.getArguments()[1])
                    .onRequestSuccess(locations);
            return null;
        }).when(requestingPresenter).doRequest(any(SpiceRequest.class),
                any(DreamSpiceManager.SuccessListener.class));

        List<DtlLocation> locationsObtained = new ArrayList<>();
        dtlLocationStore.attachListener(locations1 -> locationsObtained.addAll(locations1));

        dtlLocationStore.loadNearbyLocations(defaultLocation);

        assertThat(locationsObtained).isEqualTo(locations);
    }

    @Test
    public void persistLocation_LocationPersisted() {
        DtlLocation location = mock(DtlLocation.class);

        dtlLocationStore.persistLocation(location);

        assertThat(dtlLocationStore.getSelectedLocation()).isEqualTo(location);
    }

    @Test
    public void persistLocation_LocationUpdated() {
        DtlLocation locationOld = new DtlLocation();
        locationOld.setId("oldId");
        dtlLocationStore.persistLocation(locationOld);

        DtlLocation locationNew = new DtlLocation();
        locationNew.setId("newId");
        dtlLocationStore.persistLocation(locationNew);

        assertThat(dtlLocationStore.getSelectedLocation()).isEqualTo(locationNew);
    }
}
