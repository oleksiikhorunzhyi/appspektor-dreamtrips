package com.worldventures.dreamtrips.core.module;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.adapter.LoaderRecycleAdapter;

import dagger.Module;

@Module(
        injects = {
                BaseArrayListAdapter.class,
                BaseDelegateAdapter.class,
                LoaderRecycleAdapter.class,
        },
        library = true, complete = false
)
public class UiBindingModule {

}
