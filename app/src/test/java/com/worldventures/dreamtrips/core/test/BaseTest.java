package com.worldventures.dreamtrips.core.test;

import android.location.Location;
import android.text.TextUtils;

import com.worldventures.dreamtrips.core.janet.cache.CacheResultWrapper;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.techery.janet.ActionService;
import rx.Scheduler;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TextUtils.class, Location.class})
public abstract class BaseTest {

    static {
        RxJavaPlugins.getInstance().registerSchedulersHook(new RxJavaSchedulersHook() {
            @Override
            public Scheduler getIOScheduler() {
                return Schedulers.immediate();
            }
        });
    }

    @Before
    public void setupBase() {
        mockStatic(TextUtils.class);//See http://g.co/androidstudio/not-mocked
        PowerMockito.when(TextUtils.isEmpty(anyString()))
                .thenAnswer(invocation -> {
                    String arg = (String) invocation.getArguments()[0];
                    if (arg == null || arg.length() == 0)
                        return true;
                    else
                        return false;
                });
    }

    protected static ActionService cachedService(ActionService service) {
        return new CacheResultWrapper(service);
    }

}
