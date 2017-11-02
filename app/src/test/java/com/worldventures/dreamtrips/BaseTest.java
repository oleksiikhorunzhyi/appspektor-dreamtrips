package com.worldventures.dreamtrips;

import com.worldventures.core.janet.cache.CacheResultWrapper;
import com.worldventures.core.test.common.RxJavaSchedulerInitializer;

import org.junit.Before;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import io.techery.janet.ActionService;

@RunWith(JUnitPlatform.class)
public abstract class BaseTest {

   static {
      RxJavaSchedulerInitializer.init();
   }

   @Before
   public void setupBase() {
      MockitoAnnotations.initMocks(this);
   }

   protected static CacheResultWrapper cachedService(ActionService service) {
      return new CacheResultWrapper(service);
   }
}
