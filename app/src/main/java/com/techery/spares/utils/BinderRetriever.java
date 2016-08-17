package com.techery.spares.utils;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class BinderRetriever {
   public interface OnBinderRetrieved<T extends IBinder> {
      public void onBinderRetrieved(T binder);
   }

   private final Context context;

   public BinderRetriever(Context context) {
      this.context = context;
   }

   public <T extends IBinder> void retrieveBinder(Class<? extends Service> serviceClass, OnBinderRetrieved<T> onBinderRetrieved) {
      Intent intent = new Intent(this.context, serviceClass);
      this.context.bindService(intent, new ServiceLoadingConnection(context, onBinderRetrieved), Context.BIND_AUTO_CREATE);
   }

   private static class ServiceLoadingConnection implements ServiceConnection {
      private final OnBinderRetrieved listener;
      private final Context context;

      private ServiceLoadingConnection(Context context, OnBinderRetrieved listener) {
         this.context = context;
         this.listener = listener;
      }

      @Override
      public void onServiceConnected(ComponentName name, IBinder service) {
         listener.onBinderRetrieved(service);
         context.unbindService(this);
      }

      @Override
      public void onServiceDisconnected(ComponentName name) {
         //nothing to do here
      }
   }
}
