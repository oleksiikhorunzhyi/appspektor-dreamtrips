package com.worldventures.dreamtrips.modules.dtl.model.merchant.offer;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class DtlOfferTest {

    private DtlOffer firstOffer;
    private DtlOffer secondOffer;
    private DtlOffer firstEndlessOffer;
    private DtlOffer secondEndlessOffer;

    @Test
    public void testGreaterThan() {
        int result = DtlOffer.END_DATE_COMPARATOR.compare(firstOffer, secondOffer);

        assertThat(result).isEqualTo(-1);
    }

    @Test
    public void testLessThan() {
        int result = DtlOffer.END_DATE_COMPARATOR.compare(secondOffer, firstOffer);

        assertThat(result).isEqualTo(1);
    }

    @Test
    public void testEqual() {
        int result = DtlOffer.END_DATE_COMPARATOR.compare(secondOffer, secondOffer);

        assertThat(result).isEqualTo(0);
    }

    @Test
    public void testEqualNull() {
        int result = DtlOffer.END_DATE_COMPARATOR.compare(secondEndlessOffer, firstEndlessOffer);

        assertThat(result).isEqualTo(0);
    }

    @Test
    public void testGreaterThanNull() {
        int result = DtlOffer.END_DATE_COMPARATOR.compare(secondOffer, firstEndlessOffer);

        assertThat(result).isEqualTo(-1);
    }

    @Test
    public void testLessThanNull() {
        int result = DtlOffer.END_DATE_COMPARATOR.compare(secondEndlessOffer, firstOffer);

        assertThat(result).isEqualTo(1);
    }

    @Before
    public void setupOffers() {
        firstOffer = new DtlOffer("first");
        secondOffer = new DtlOffer("second");
        firstEndlessOffer = new DtlOffer("firstEndless");
        secondEndlessOffer = new DtlOffer("secondEndless");
        //
        DtlOfferData firstOfferData = new DtlOfferData() {
            @Override
            public String getType() {
                return null;
            }
        };
        firstOfferData.setEndDate(new Date(1463423767L));
        //
        DtlOfferData secondOfferData = new DtlOfferData() {
            @Override
            public String getType() {
                return null;
            }
        };
        secondOfferData.setEndDate(new Date(1463443200L)); // + 1 day to first
        //
        DtlOfferData firstEndlessOfferData = new DtlOfferData() {
            @Override
            public String getType() {
                return null;
            }
        };
        firstEndlessOfferData.setEndDate(null);
        //
        DtlOfferData secondEndlessOfferData = new DtlOfferData() {
            @Override
            public String getType() {
                return null;
            }
        };
        secondEndlessOfferData.setEndDate(null);
        //
        firstOffer.setOffer(firstOfferData);
        secondOffer.setOffer(secondOfferData);
        firstEndlessOffer.setOffer(firstEndlessOfferData);
        secondEndlessOffer.setOffer(secondEndlessOfferData);
    }
}
