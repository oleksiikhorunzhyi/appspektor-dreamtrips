package com.worldventures.dreamtrips.wallet.ui.dashboard.util.model;


import android.databinding.BindingAdapter;
import android.support.annotation.DrawableRes;
import android.view.View;

import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.BaseViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.HolderTypeFactory;

public class CommonCardViewModel extends BaseViewModel {

   private String recordId;
   private CharSequence cardName;
   private StackType cardType;
   private String cardTypeName;
   private boolean defaultCard;
   private CharSequence cardLastDigitsShort;
   private String cardHolderName;
   private CharSequence cardLastDigitsLong;
   private CharSequence goodThrough;
   private @DrawableRes int cardBackGround;

   public CommonCardViewModel(String recordId, CharSequence cardName, StackType cardType, String cardTypeName,
         boolean defaultCard, CharSequence cardLastDigitsShort, String cardHolderName,
         CharSequence cardLastDigitsLong, CharSequence goodThrough, @DrawableRes int cardBackGround) {
      this.recordId = recordId;
      this.cardName = cardName;
      this.cardType = cardType;
      this.cardTypeName = cardTypeName;
      this.defaultCard = defaultCard;
      this.cardLastDigitsShort = cardLastDigitsShort;
      this.cardHolderName = cardHolderName;
      this.cardLastDigitsLong = cardLastDigitsLong;
      this.goodThrough = goodThrough;
      this.cardBackGround = cardBackGround;
   }

   public String getRecordId() {
      return recordId;
   }

   public CharSequence getCardName() {
      return cardName;
   }

   public StackType getCardType() {
      return cardType;
   }

   public String getCardTypeName() {
      return cardTypeName;
   }

   public boolean isDefaultCard() {
      return defaultCard;
   }

   public CharSequence getCardLastDigitsShort() {
      return cardLastDigitsShort;
   }

   public String getCardHolderName() {
      return cardHolderName;
   }

   public CharSequence getCardLastDigitsLong() {
      return cardLastDigitsLong;
   }

   public CharSequence getGoodThrough() {
      return goodThrough;
   }

   public int getCardBackGround() {
      return cardBackGround;
   }

   @BindingAdapter({"bind:cardBackground"})
   public static void getCardBackground(View view, @DrawableRes int backGround) {
      view.setBackgroundResource(backGround);
   }

   @Override
   public int type(HolderTypeFactory typeFactory) {
      return typeFactory.type(this);
   }

   public enum StackType {
      PAYMENT, LOYALTY
   }
}
