package com.techery.spares.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseAbstractLoader<T> extends AsyncTaskLoader<T> {
   private Throwable lastError;
   private T cachedResult;
   private Bundle params;

   public BaseAbstractLoader(Context context) {
      super(context);
      onContentChanged();
   }

   @Override
   protected void onReset() {
      super.onReset();
      this.cachedResult = null;
   }

   @Override
   protected void onStartLoading() {
      if (this.cachedResult != null) {
         deliverResult(this.cachedResult);
      } else {
         forceLoad();
      }
   }

   @Override
   public T loadInBackground() {
      try {
         this.cachedResult = perform();
         return this.cachedResult;
      } catch (Exception e) {
         lastError = e;
         logException(e);
         return null;
      }
   }

   private void logException(Exception e) {
      if (getLogger() != null) {
         getLogger().log(Level.WARNING, e.getLocalizedMessage());
      }
   }

   protected abstract T perform();

   public Throwable getLastError() {
      return lastError;
   }

   public Logger getLogger() {
      return null;
   }

   public void setLogger(Logger logger) {

   }

   public Bundle getParams() {
      return params;
   }

   public void setParams(Bundle params) {
      this.params = params;
   }
}
