package com.worldventures.dreamtrips.module.dtl.repository;

import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.modules.common.presenter.RequestingPresenter;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

//import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;

@RunWith(MockitoJUnitRunner.class)
public class DtlMerchantManagerTest {

    @Mock
    private RequestingPresenter requestingPresenter;
    @Mock
    private Injector injector;
    //
    private DtlExternalLocation defaultLocation = getDefaultLocation();
//    private DtlMerchantManager dtlMerchantManager;

//    @Before
//    public void beforeEachTest() {
//        dtlMerchantManager = new DtlMerchantManager(injector);
//    }

    @Test
    public void loadMerchants_MerchantsLoaded() {
        List<DtlMerchant> items = new ArrayList<>(asList(mock(DtlMerchant.class),
                mock(DtlMerchant.class)));
        doAnswer(invocation -> {
            ((DreamSpiceManager.SuccessListener<List<DtlMerchant>>) invocation.getArguments()[1])
                    .onRequestSuccess(items);
            return null;
        }).when(requestingPresenter).doRequest(any(SpiceRequest.class),
                any(DreamSpiceManager.SuccessListener.class));
        //
//        dtlMerchantManager.loadMerchants(defaultLocation);
        //
//        assertThat(dtlMerchantManager.getMerchants()).isEqualTo(items);
    }

    public static DtlExternalLocation getDefaultLocation() {
        DtlExternalLocation dtlExternalLocation = new DtlExternalLocation();
        Location location = new Location(0.0d, 0.0d);
        dtlExternalLocation.setCoordinates(location);
        return dtlExternalLocation;
    }
}
