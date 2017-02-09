package com.worldventures.dreamtrips.wallet.service.nxt;

import android.content.Context;

import com.techery.spares.module.Injector;

import io.techery.janet.ActionHolder;
import io.techery.janet.ActionServiceWrapper;
import io.techery.janet.HttpActionService;
import io.techery.janet.JanetException;
import io.techery.janet.converter.Converter;
import io.techery.janet.http.HttpClient;
import timber.log.Timber;

public class NxtHttpService extends ActionServiceWrapper {

   public NxtHttpService(Context appContext, String baseUrl, HttpClient client, Converter converter) {
      super(new HttpActionService(baseUrl, client, converter));
      ((Injector) appContext).inject(this);
   }

   @Override
   protected <A> boolean onInterceptSend(ActionHolder<A> holder) {
      return false;
   }

   @Override
   protected <A> void onInterceptCancel(ActionHolder<A> holder) {
   }

   @Override
   protected <A> void onInterceptStart(ActionHolder<A> holder) {
   }

   @Override
   protected <A> void onInterceptProgress(ActionHolder<A> holder, int progress) {
   }

   @Override
   protected <A> void onInterceptSuccess(ActionHolder<A> holder) {
   }

   @Override
   protected <A> boolean onInterceptFail(ActionHolder<A> holder, JanetException e) {
      Timber.e(e, "WTF: NxtHttpService#onInterceptFail: holder = " + holder);
      // TODO: 2/7/17 Implement NXT error handling and nxt token refresh logic
      return false;
   }

}