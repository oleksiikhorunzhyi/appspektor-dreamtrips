package com.worldventures.dreamtrips.module.dtl.repository;

import com.worldventures.dreamtrips.core.api.DtlApi;
import com.worldventures.dreamtrips.core.api.factory.RxApiFactory;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.store.DtlLocationManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class DtlLocationManagerTest {

    @Mock
    DtlApi dtlApi;
    @Mock
    SnappyRepository db;
    @Mock
    RxApiFactory rxApiFactory;
    //
    private DtlLocationManager dtlLocationManager;

    @Before
    public void beforeEachTest() {
        dtlLocationManager = new DtlLocationManager(dtlApi, db, rxApiFactory);
    }

    @Test
    public void persistLocation_LocationPersisted() {
        DtlExternalLocation location = mock(DtlExternalLocation.class);
        //
        dtlLocationManager.persistLocation(location);
        //
        assertThat(dtlLocationManager.getSelectedLocation()).isEqualTo(location);
    }

    @Test
    public void persistLocation_LocationUpdated() {
        DtlExternalLocation locationOld = new DtlExternalLocation();
        locationOld.setId("oldId");
        dtlLocationManager.persistLocation(locationOld);
        //
        DtlExternalLocation locationNew = new DtlExternalLocation();
        locationNew.setId("newId");
        dtlLocationManager.persistLocation(locationNew);
        //
        assertThat(dtlLocationManager.getSelectedLocation()).isEqualTo(locationNew);
    }
}
