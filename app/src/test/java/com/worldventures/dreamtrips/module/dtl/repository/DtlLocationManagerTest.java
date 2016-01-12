package com.worldventures.dreamtrips.module.dtl.repository;

import android.content.Context;
import android.location.Location;

import com.worldventures.dreamtrips.core.api.DtlApi;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
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

    private DtlLocationManager dtlLocationManager;
    @Mock
    private Context context;
    @Mock
    private SnappyRepository db;
    @Mock
    private DtlApi dtlApi;
  
    @Before
    public void beforeEachTest() {
        dtlLocationManager = new DtlLocationManager(context, db, dtlApi);
    }

    @Test
    public void persistLocation_LocationPersisted() {
        DtlLocation location = mock(DtlLocation.class);

        dtlLocationManager.persistLocation(location);

        assertThat(dtlLocationManager.getSelectedLocation()).isEqualTo(location);
    }

    @Test
    public void persistLocation_LocationUpdated() {
        DtlLocation locationOld = new DtlLocation();
        locationOld.setId("oldId");
        dtlLocationManager.persistLocation(locationOld);

        DtlLocation locationNew = new DtlLocation();
        locationNew.setId("newId");
        dtlLocationManager.persistLocation(locationNew);

        assertThat(dtlLocationManager.getSelectedLocation()).isEqualTo(locationNew);
    }
}
