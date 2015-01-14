package com.worldventures.dreamtrips.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CommonUtils {

    public static String convertStreamToString(java.io.InputStream in) {
        InputStreamReader is = new InputStreamReader(in);
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(is);
            String read = br.readLine();

            while (read != null) {
                sb.append(read);
                read = br.readLine();
            }
        } catch (Exception e) {
            Logs.e(e);
        }

        return sb.toString();
    }

}
