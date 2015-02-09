package com.techery.spares.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.techery.spares.loader.ContentLoader;
import com.techery.spares.module.Annotations.Global;
import com.techery.spares.module.Injector;
import com.techery.spares.ui.view.cell.AbstractCell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class BaseArrayListAdapter<BaseItemClass> extends RecyclerView.Adapter<AbstractCell> implements DataListAdapter<List<BaseItemClass>>, ContentLoader.ContentLoadingObserving<List<BaseItemClass>> {

    private final Map<Class, Class<? extends AbstractCell>> itemCellMapping = new HashMap<>();

    private final AdapterHelper adapterHelper;
    private final Injector injector;
    private ContentLoader<List<BaseItemClass>> contentLoader;
    protected List<BaseItemClass> items = new ArrayList<>();

    @Inject
    @Global
    EventBus eventBus;

    private List<Class> viewTypes = new ArrayList<>();

    public BaseArrayListAdapter(Context context, Injector injector) {
        this.injector = injector;

        this.injector.inject(this);

        this.adapterHelper = new AdapterHelper((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
    }

    public void registerCell(Class itemClass, Class<? extends AbstractCell> cellClass) {
        this.itemCellMapping.put(itemClass, cellClass);

        int type = this.viewTypes.indexOf(itemClass);

        if (type == -1) {
            this.viewTypes.add(itemClass);
        }
    }

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

    @Override
    public AbstractCell onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e("", viewType + "");

        Class itemClass = this.viewTypes.get(viewType);
        Class<? extends AbstractCell> cellClass = this.itemCellMapping.get(itemClass);
        AbstractCell cell = this.adapterHelper.buildCell(cellClass, parent);
        cell.setEventBus(eventBus);
        return cell;
    }

    @Override
    public int getItemViewType(int position) {
        Class itemClass = this.items.get(position).getClass();
        int index = viewTypes.indexOf(itemClass);
        if (index < 0) {
            throw new IllegalArgumentException(itemClass.getSimpleName() + " is not registered");
        }
        return index;
    }


    @Override
    public void onBindViewHolder(AbstractCell cell, int position) {
        BaseItemClass item = this.getItem(position);

        cell.prepareForReuse();
        this.injector.inject(cell);
        cell.afterInject();
        cell.fillWithItem(item);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public BaseItemClass getItem(int position) {
        return this.items.get(position);
    }

    public void addItems(List<BaseItemClass> result) {
        if (result != null) {
            this.items.addAll(result);
            this.notifyDataSetChanged();
        }
    }

    public void addItem(int location, BaseItemClass obj) {
        this.items.add(location, obj);
    }

    public void addItem(BaseItemClass obj) {
        this.items.add(obj);
    }

    public void replaceItem(int location, BaseItemClass obj) {
        this.items.set(location, obj);
    }
    public void remove(int location) {
        this.items.remove(location);
    }

    public void clear() {
        this.items.clear();
        this.notifyDataSetChanged();
    }

    public List<BaseItemClass> getItems() {
        return items;
    }

}