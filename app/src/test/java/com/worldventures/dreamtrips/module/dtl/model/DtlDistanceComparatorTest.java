package com.worldventures.dreamtrips.module.dtl.model;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DtlDistanceComparatorTest {

    @Test
    public void testEqual() {
        DtlMerchant dtlMerchantFirst = new DtlMerchant();
        dtlMerchantFirst.setDistance(10.0d);
        DtlMerchant dtlMerchantSecond = new DtlMerchant();
        dtlMerchantSecond.setDistance(10.0d);

        int result = DtlMerchant.DISTANCE_COMPARATOR.compare(dtlMerchantFirst, dtlMerchantSecond);

        assertThat(result).isEqualTo(0);
    }

    @Test
    public void testGreaterThan() {
        DtlMerchant dtlMerchantFirst = new DtlMerchant();
        dtlMerchantFirst.setDistance(11.0d);
        DtlMerchant dtlMerchantSecond = new DtlMerchant();
        dtlMerchantSecond.setDistance(10.0d);

        int result = DtlMerchant.DISTANCE_COMPARATOR.compare(dtlMerchantFirst, dtlMerchantSecond);

        assertThat(result).isGreaterThanOrEqualTo(1);
    }

    @Test
    public void testLessThan() {
        DtlMerchant dtlMerchantFirst = new DtlMerchant();
        dtlMerchantFirst.setDistance(9.0d);
        DtlMerchant dtlMerchantSecond = new DtlMerchant();
        dtlMerchantSecond.setDistance(10.0d);

        int result = DtlMerchant.DISTANCE_COMPARATOR.compare(dtlMerchantFirst, dtlMerchantSecond);

        assertThat(result).isLessThanOrEqualTo(-1);
    }
}
