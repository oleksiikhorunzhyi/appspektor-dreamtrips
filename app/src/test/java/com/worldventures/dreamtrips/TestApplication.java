package com.worldventures.dreamtrips;

public class TestApplication extends App {

   @Override
   protected void runInitializers() {
      // we should avoid initializing Crashlytics, etc, with each instrumented test
   }

   @Override
   public void inject(Object target) {
      // since we are not going to inject anything here anyway and to avoid listing this app
      // in app module (which would require moving it from "test" folder to "main" folder)
   }
}
