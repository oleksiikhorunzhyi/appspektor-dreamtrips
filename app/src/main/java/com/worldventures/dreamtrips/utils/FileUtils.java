package com.worldventures.dreamtrips.utils;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;

import timber.log.Timber;

/**
 * Created by Edward on 21.01.15.
 * utils for reading writing from file
 */
public class FileUtils {

    public static final String TRIPS = "trips.json";
    public static final String REGIONS = "regions.json";
    public static final String ACTIVITIES = "activities.json";


    public static void saveJsonToCache(Context context, Object object, String name) {
        Gson gson = new Gson();
        String jsonFile = gson.toJson(object);
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(name,
                    Context.MODE_PRIVATE));
            outputStreamWriter.write(jsonFile);
            outputStreamWriter.close();
        } catch (IOException e) {
            Timber.e(e, "");
        }
    }

    public static <T> T parseJsonFromCache(Context context, Type type, String fileName) {
        T result = null;
        Gson gson = new Gson();

        try {
            InputStream inputStream = context.openFileInput(fileName);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receivedString;
                StringBuilder stringBuilder = new StringBuilder();

                while ((receivedString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receivedString);
                }

                inputStream.close();
                String resultString = stringBuilder.toString();
                result = gson.fromJson(resultString, type);
            }
        } catch (IOException e) {
            Timber.e(e, "");
        }

        return result;
    }

}
