package com.worldventures.dreamtrips.module.dtl.model;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DtlDistanceComparatorTest {

    @Test
    public void testEqual() {
        DtlMerchant dtlMerchantFirst = new DtlMerchant();
        dtlMerchantFirst.setDistanceInMiles(10.0d);
        DtlMerchant dtlMerchantSecond = new DtlMerchant();
        dtlMerchantSecond.setDistanceInMiles(10.0d);

        int result = DtlMerchant.DISTANCE_COMPARATOR.compare(dtlMerchantFirst, dtlMerchantSecond);

        assertThat(result).isEqualTo(0);
    }

    @Test
    public void testGreaterThan() {
        DtlMerchant dtlMerchantFirst = new DtlMerchant();
        dtlMerchantFirst.setDistanceInMiles(11.0d);
        DtlMerchant dtlMerchantSecond = new DtlMerchant();
        dtlMerchantSecond.setDistanceInMiles(10.0d);

        int result = DtlMerchant.DISTANCE_COMPARATOR.compare(dtlMerchantFirst, dtlMerchantSecond);

        assertThat(result).isGreaterThan(-1);
    }

    @Test
    public void testLessThan() {
        DtlMerchant dtlMerchantFirst = new DtlMerchant();
        dtlMerchantFirst.setDistanceInMiles(9.0d);
        DtlMerchant dtlMerchantSecond = new DtlMerchant();
        dtlMerchantSecond.setDistanceInMiles(10.0d);

        int result = DtlMerchant.DISTANCE_COMPARATOR.compare(dtlMerchantFirst, dtlMerchantSecond);

        assertThat(result).isLessThanOrEqualTo(-1);
    }
}
