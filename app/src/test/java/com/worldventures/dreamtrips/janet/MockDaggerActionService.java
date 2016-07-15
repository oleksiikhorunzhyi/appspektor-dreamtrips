package com.worldventures.dreamtrips.janet;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.ActionService;
import io.techery.janet.ActionServiceWrapper;
import io.techery.janet.JanetException;
import timber.log.Timber;

public class MockDaggerActionService extends ActionServiceWrapper {

    private final Map<Class, ObjectProvider> injectionMap = new HashMap<>();

    public MockDaggerActionService(ActionService actionService) {
        super(actionService);
    }

    public <T> MockDaggerActionService registerProvider(Class<T> objClass, ObjectProvider<T> provider) {
        if (injectionMap.containsKey(objClass)) {
            throw new IllegalArgumentException("Class " + objClass + " is already registered");
        }
        injectionMap.put(objClass, provider);
        return this;
    }

    @Override
    protected <A> boolean onInterceptSend(ActionHolder<A> holder) throws JanetException {
        A action = holder.action();
        if (!(action instanceof InjectableAction)) return false;
        try {
            InjectableAction injectableAction = (InjectableAction) action;
            inject(injectableAction, injectableAction.getClass());
        } catch (Throwable throwable) {
            Timber.e(throwable, "Can't inject action %s", action);
        }
        return false;
    }

    private void inject(InjectableAction action, Class<?> actionClass) throws IllegalAccessException {
        Field[] fields = actionClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(Inject.class) != null) {
                ObjectProvider provider = injectionMap.get(field.getType());
                if (provider != null) {
                    field.setAccessible(true);
                    field.set(action, provider.provide());
                }
            }
        }

        if (actionClass.getSuperclass() != null) {
            inject(action, actionClass.getSuperclass());
        }
    }

    public interface ObjectProvider<T> {
        T provide();
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
        return false;
    }
}
