package com.worldventures.dreamtrips.core.utils;

import android.content.Intent;

import com.innahema.collections.query.queriables.Queryable;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ActivityResultDelegate {

   int requestCode;
   int resultCode;
   Intent data;

   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      this.requestCode = requestCode;
      this.resultCode = resultCode;
      this.data = data;
      //
      Queryable.from(listeners).forEachR(listener -> {
         if (listener.onActivityResult(requestCode, resultCode, data)) {
            clear();
            return;
         }
      });
   }

   public int getResultCode() {
      return resultCode;
   }

   public Intent getData() {
      return data;
   }

   public int getRequestCode() {
      return requestCode;
   }

   /**
    * Clear data stored in delegate from previous activation - to ensure it won't <br />
    * fire again with outdated request/result code or data
    */
   public void clear() {
      onActivityResult(Integer.MIN_VALUE, Integer.MIN_VALUE, null);
   }

   private List<ActivityResultListener> listeners = new ArrayList<>();

   public void addListener(ActivityResultListener listener) {
      if (listener == null) {
         Timber.e("Listener cannot be null!");
         return;
      }
      //
      this.listeners.add(listener);
   }

   public void removeListener(ActivityResultListener listener) {
      this.listeners.remove(listener);
   }

   public interface ActivityResultListener {

      /**
       * Callback mehod for activity result delegate.
       *
       * @param requestCode code of currently dispatched activity result
       * @param resultCode  code of result status
       * @param data        data provided with result
       * @return true if result consumed (subsequent dispatching will be suspended) <br />
       * or false to notify other possible delegates
       */
      boolean onActivityResult(int requestCode, int resultCode, Intent data);
   }
}
