package com.techery.spares.adapter;

import android.content.Context;

import com.techery.spares.loader.ContentLoader;
import com.techery.spares.module.Injector;

import java.util.List;

public class LoaderRecycleAdapter<BaseItemClass> extends BaseArrayListAdapter<BaseItemClass> implements DataListAdapter<List<BaseItemClass>>, ContentLoader.ContentLoadingObserving<List<BaseItemClass>> {
    public LoaderRecycleAdapter(Context context, Injector injector) {
        super(context, injector);
    }

    private ContentLoader<List<BaseItemClass>> contentLoader;


    @Override
    public void setContentLoader(ContentLoader<List<BaseItemClass>> contentLoader) {
        this.contentLoader = contentLoader;
        if (this.contentLoader != null) {
            this.contentLoader.getContentLoaderObserver().registerObserver(this);
            if (this.contentLoader.getResult() != null) {
                onFinishLoading(this.contentLoader.getResult());
            }
        }
    }

    public ContentLoader<List<BaseItemClass>> getContentLoader() {
        return contentLoader;
    }

    @Override
    public void onStartLoading() {

    }


    @Override
    public void onFinishLoading(List<BaseItemClass> result) {
        this.items.clear();
        this.items.addAll(result);
        this.notifyDataSetChanged();
    }

    @Override
    public void onError(Throwable throwable) {
    }
}