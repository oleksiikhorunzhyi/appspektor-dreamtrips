package com.worldventures.dreamtrips.modules.trips.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RewardsRuleModel implements Serializable {
   private static final long serialVersionUID = 999L;

   @SerializedName("DTG") private String dtg;

   @SerializedName("DTM") private String dtm;

   @SerializedName("DTP") private String dtp;

   public String getDtg() {
      return dtg;
   }

   public String getDtm() {
      return dtm;
   }

   public String getDtp() {
      return dtp;
   }

   public boolean hasDtp() {
      return !TextUtils.isEmpty(dtp);
   }

   public boolean hasDtg() {
      return !TextUtils.isEmpty(dtg);
   }

   public boolean hasDtm() {
      return !TextUtils.isEmpty(dtm);
   }
}
