package com.techery.spares.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class AndroidServicesModule {

   @Provides
   @Singleton
   LocationManager provideLocationManager(Context context) {
      return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
   }

   @Provides
   @Singleton
   LayoutInflater provideLayoutInflater(Context context) {
      return (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
   }

   @Provides
   @Singleton
   Resources provideResources(Context context) {
      return context.getResources();
   }

   @Provides
   @Singleton
   ConnectivityManager provideConnectivityManager(Context context) {
      return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
   }

   @Provides
   @Singleton
   PackageManager providePackageManager(Context context) {
      return context.getPackageManager();
   }

   @Provides
   @Singleton
   SharedPreferences provideSharedPreferences(Context context) {
      return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
   }

   @Provides
   @Singleton
   WifiManager provideWifiManager(Context context) {
      return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
   }
}
