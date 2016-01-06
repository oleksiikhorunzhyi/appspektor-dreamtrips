package com.worldventures.dreamtrips.module.dtl.repository;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.RequestingPresenter;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantRepository;

import com.worldventures.dreamtrips.modules.trips.model.Location;

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
public class DtlMerchantRepositoryTest {

    private DtlMerchantRepository dtlMerchantRepository;
    @Mock
    private RequestingPresenter requestingPresenter;
    @Mock
    private SnappyRepository db;

    private DtlLocation defaultLocation = getDefaultLocation();

    @Before
    public void beforeEachTest() {
        dtlMerchantRepository = new DtlMerchantRepository(db);
        dtlMerchantRepository.setRequestingPresenter(requestingPresenter);
    }

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


        dtlMerchantRepository.loadMerchants(defaultLocation);

        assertThat(dtlMerchantRepository.getMerchants()).isEqualTo(items);
    }

    public static DtlLocation getDefaultLocation() {
        DtlLocation dtlLocation = new DtlLocation();
        Location location = new Location(0.0d, 0.0d);
        dtlLocation.setCoordinates(location);
        return dtlLocation;
    }

}
