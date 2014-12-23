package com.techery.spares.ui.fragment.loadable.collection;

import com.techery.spares.adapter.BaseArrayListAdapter;

import java.util.List;

public abstract class ArrayAdapterFragment<T, LV> extends InjectingListFragment<List<T>, T, LV> {
    @Override
    public void afterCreateView(android.view.View rootView) {
        super.afterCreateView(rootView);
        setDataAdapter(new BaseArrayListAdapter<T>(getActivity()));
    }


}
