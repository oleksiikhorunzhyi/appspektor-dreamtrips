package com.worldventures.core.ui.view.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.worldventures.core.janet.Injector;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.annotations.MenuResource;

import java.util.ArrayList;
import java.util.List;

import dagger.ObjectGraph;

public abstract class InjectingActivity extends AppCompatActivity implements Injector {

   private ObjectGraph objectGraph;

   @Override
   public ObjectGraph getObjectGraph() {
      if (objectGraph == null) {
         setupObjectGraph();
      }

      return objectGraph;
   }

   @Override
   public void inject(Object target) {
      getObjectGraph().inject(target);
   }

   protected void afterInject() {
      //do nothing
   }

   protected void setupObjectGraph() {
      objectGraph = getApplicationInjector().getObjectGraph().plus(getModules().toArray());
   }

   private Injector getApplicationInjector() {
      return (Injector) getApplication();
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      setupObjectGraph();
      inject(this);
      afterInject();

      beforeCreateView(savedInstanceState);

      setupLayout();
   }

   @Override
   protected void onPostCreate(Bundle savedInstanceState) {
      super.onPostCreate(savedInstanceState);
      afterCreateView(savedInstanceState);
   }

   public void setupLayout() {
      Layout layout = this.getClass().getAnnotation(Layout.class);
      if (layout != null) {
         setContentView(layout.value());
      }
   }


   protected void beforeCreateView(Bundle savedInstanceState) {
      //nothing to here
   }

   protected void afterCreateView(Bundle savedInstanceState) {
      //nothing to here
   }

   @Override
   protected void onDestroy() {
      this.objectGraph = null;
      super.onDestroy();
   }

   @Override
   public boolean onCreateOptionsMenu(android.view.Menu menu) {
      MenuResource menuResource = this.getClass().getAnnotation(MenuResource.class);
      if (menuResource != null) {
         getMenuInflater().inflate(menuResource.value(), menu);
         return true;
      } else {
         return super.onCreateOptionsMenu(menu);
      }
   }

   protected List<Object> getModules() {
      return new ArrayList<>();
   }

   @Override
   public Object getSystemService(@NonNull String name) {
      if (OBJECT_GRAPH_SERVICE_NAME.equals(name)) {
         return getObjectGraph();
      }
      return super.getSystemService(name);
   }
}
