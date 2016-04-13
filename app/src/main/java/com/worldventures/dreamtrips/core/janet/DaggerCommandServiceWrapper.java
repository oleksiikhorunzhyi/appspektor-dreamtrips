package com.worldventures.dreamtrips.core.janet;

import android.content.Context;

import com.techery.spares.module.Injector;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import dagger.ObjectGraph;
import io.techery.janet.ActionHolder;
import io.techery.janet.ActionServiceWrapper;
import io.techery.janet.CommandActionBase;
import io.techery.janet.CommandActionService;
import io.techery.janet.JanetException;
import timber.log.Timber;

public class DaggerCommandServiceWrapper extends ActionServiceWrapper {

    private final CommandInjector injector;

    public DaggerCommandServiceWrapper(CommandActionService service, Context appContext) {
        super(service);
        this.injector = new CommandInjector(((Injector) appContext).getObjectGraph());
    }

    @Override protected <A> boolean onInterceptSend(ActionHolder<A> holder) {
        try {
            injector.inject((CommandActionBase) holder.action());
        } catch (Throwable throwable) {
            Timber.e(throwable, "Can't inject command %s", holder.action());
        }
        return false;
    }

    @Override protected <A> void onInterceptCancel(ActionHolder<A> holder) {}

    @Override protected <A> void onInterceptStart(ActionHolder<A> holder) {}

    @Override protected <A> void onInterceptProgress(ActionHolder<A> holder, int progress) {}

    @Override protected <A> void onInterceptSuccess(ActionHolder<A> holder) {}

    @Override protected <A> void onInterceptFail(ActionHolder<A> holder, JanetException e) {}

    private static class CommandInjector {

        private static final Map<Class<? extends CommandActionBase>, Method> cache = new HashMap<>();
        private ObjectGraph objectGraph;

        private CommandInjector(ObjectGraph objectGraph) {
            this.objectGraph = objectGraph;
        }

        public void inject(CommandActionBase command) {
            Class<? extends CommandActionBase> commandClass = command.getClass();
            try {
                objectGraph.inject(command);
            } catch (Throwable e) {
                String detailMessage = "No graph method found to inject " + commandClass.getSimpleName() + ". Check your component";
                Timber.e(e, detailMessage);
                throw e;
            }
        }
    }
}
