package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.storage;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class ReviewStorage {

    private static final String PREFS_NAME = "DTL_PREFS";

    public static void saveReviewsPosted(Context context, String idUser, String idMerchant) {

        if (!exists(context, idUser, idMerchant)) {
            Timber.i("------> Saved the post shared preferences");
            List<String> merchantsArray;
            String[] merchants = getReviewsPosted(context, idUser);
            if (merchants == null) {
                merchantsArray = new ArrayList<>();
            } else {
                merchantsArray =  new ArrayList<>(Arrays.asList(merchants));
            }
            merchantsArray.add(idMerchant);
            saveReviewsList(context, idUser, merchantsArray);
        }
    }

    private static String[] getReviewsPosted(Context context, String idUser) {
        Gson gson = new Gson();
        String json = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(idUser, null);
        return gson.fromJson(json, String[].class);
    }

    public static boolean exists(Context context, String idUser, String idMerchant) {
        String[] merchants = getReviewsPosted(context, idUser);
        if (merchants != null && merchants.length > 0) {
            return (Arrays.asList(merchants).contains(idMerchant));
        } else {
            return false;
        }
    }

    public static void updateReviewsPosted(Context context, String idUser, String idMerchant, boolean hasPending) {
        if (exists(context, idUser, idMerchant) && !hasPending) {
            String[] merchants = getReviewsPosted(context, idUser);
            List<String> merchantsArray = new ArrayList<>(Arrays.asList(merchants));
            for (String s : merchants) {
                if (s.equals(idMerchant)){
                    merchantsArray.remove(s);
                }
            }
            saveReviewsList(context, idUser, merchantsArray);
        }
    }

    private static void saveReviewsList(Context context, String idUser, List<String> merchantsArray) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(merchantsArray);
        editor.putString(idUser, jsonFavorites);
        editor.apply();
    }
}