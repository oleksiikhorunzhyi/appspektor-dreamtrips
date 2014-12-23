package com.techery.spares.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.techery.spares.loader.ContentLoader;
import com.techery.spares.ui.view.cell.BaseCell;

import java.util.List;

import de.greenrobot.event.EventBus;

public class BaseArrayListAdapter<T> extends ArrayAdapter<T> implements DataListAdapter<List<T>>,ContentLoader.ContentLoadingObserving<List<T>> {
    private AdapterController adapterController;
    private ContentLoader<List<T>> contentLoader;

    public BaseArrayListAdapter(Context context) {
        this(context, null);
    }

    public BaseArrayListAdapter(Context context, BaseCell.CellBuilder<T> cellBuilder) {
        this(context, cellBuilder, null);
    }

    public BaseArrayListAdapter(Context context, BaseCell.CellBuilder<T> cellBuilder, EventBus bus) {
        super(context, -1);
        this.adapterController = new AdapterController(context, cellBuilder, bus);
    }

    @Override
    public android.view.View getView(int position, android.view.View convertView, ViewGroup parent) {
        android.view.View cell = convertView;

        if (cell == null) {
            cell = this.adapterController.newView(this, position, parent);
        }

        return this.adapterController.bindView(this, position, cell);
    }

    public AdapterController getController() {
        return adapterController;
    }

    public void setContentLoader(ContentLoader<List<T>> contentLoader) {
        this.contentLoader = contentLoader;
        if (this.contentLoader != null) {
            this.contentLoader.getContentLoaderObserver().registerObserver(this);
            if (this.contentLoader.getResult() != null) {
                onFinishLoading(this.contentLoader.getResult());
            }
        }
    }

    public ContentLoader<List<T>> getContentLoader() {
        return contentLoader;
    }

    @Override
    public void onStartLoading() {

    }

    @Override
    public void onFinishLoading(List<T> result) {
        for (T res : result) {
            this.add(res);
        }

        this.notifyDataSetChanged();
    }

    @Override
    public void onError(Throwable throwable) {

    }
}