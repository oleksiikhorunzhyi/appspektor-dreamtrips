package com.techery.spares.module;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Module(library = true)
public class InjectingServiceModule {
   private final android.app.Service service;
   private final Injector injector;

   public InjectingServiceModule(android.app.Service service, Injector injector) {
      this.service = service;
      this.injector = injector;
   }

   @Qualifier
   @Target({FIELD, PARAMETER, METHOD})
   @Documented
   @Retention(RUNTIME)
   public @interface Service {}

   @Provides
   @Singleton
   @ForApplication
   public Context provideApplicationContext() {
      return this.service.getApplicationContext();
   }

   @Provides
   @Singleton
   public android.app.Service provideService() {
      return this.service;
   }

   @Provides
   @Singleton
   @Service
   public Injector provideServiceInjector() {
      return this.injector;
   }

}
