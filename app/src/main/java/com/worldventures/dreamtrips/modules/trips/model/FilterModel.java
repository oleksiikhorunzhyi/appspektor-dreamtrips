package com.worldventures.dreamtrips.modules.trips.model;


import java.io.Serializable;

public class FilterModel implements Serializable {

   public static final int START_INDEX = 0;
   public static final int LAST_PRICE_INDEX = 4;
   public static final int LAST_DURATION_INDEX = 9;

   private int indexLeftPrice = START_INDEX;
   private int indexRightPrice = LAST_PRICE_INDEX;
   private int indexLeftDuration = START_INDEX;
   private int indexRightDuration = LAST_DURATION_INDEX;

   public int getIndexLeftPrice() {
      return indexLeftPrice;
   }

   public void setIndexLeftPrice(int indexLeftPrice) {
      this.indexLeftPrice = indexLeftPrice;
   }

   public int getIndexRightPrice() {
      return indexRightPrice;
   }

   public void setIndexRightPrice(int indexRightPrice) {
      this.indexRightPrice = indexRightPrice;
   }

   public int getIndexLeftDuration() {
      return indexLeftDuration;
   }

   public void setIndexLeftDuration(int indexLeftDuration) {
      this.indexLeftDuration = indexLeftDuration;
   }

   public int getIndexRightDuration() {
      return indexRightDuration;
   }

   public void setIndexRightDuration(int indexRightDuration) {
      this.indexRightDuration = indexRightDuration;
   }

   public void reset() {
      indexLeftPrice = START_INDEX;
      indexRightPrice = LAST_PRICE_INDEX;
      indexLeftDuration = START_INDEX;
      indexRightDuration = LAST_DURATION_INDEX;
   }
}
