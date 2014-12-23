package com.techery.spares.module;

import dagger.ObjectGraph;

public interface Injector {
    public ObjectGraph getObjectGraph();
    public void inject(Object target);
}
