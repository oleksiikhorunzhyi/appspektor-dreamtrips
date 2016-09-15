package com.worldventures.dreamtrips.dtl.model.merchant.offer;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOffer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferPerk;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlOfferPoints;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class DtlOfferTest {

   private DtlOffer firstOffer;
   private DtlOffer secondOffer;
   private DtlOffer firstEndlessOffer;
   private DtlOffer secondEndlessOffer;
   private DtlOffer pointOffer;

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

   @Test
   public void testPointLessThanPerk() {
      int result = DtlOffer.END_DATE_COMPARATOR.compare(pointOffer, firstOffer);

      assertThat(result).isEqualTo(-1);
   }

   @Before
   public void setupOffers() {
      firstOffer = new DtlOfferPerk();
      secondOffer = new DtlOfferPerk();
      firstEndlessOffer = new DtlOfferPerk();
      secondEndlessOffer = new DtlOfferPerk();
      pointOffer = new DtlOfferPoints();
      //
      firstOffer.setEndDate(new Date(1463423767L));
      secondOffer.setEndDate(new Date(1463443200L)); // + 1 day to first
      firstEndlessOffer.setEndDate(null);
      secondEndlessOffer.setEndDate(null);
   }
}