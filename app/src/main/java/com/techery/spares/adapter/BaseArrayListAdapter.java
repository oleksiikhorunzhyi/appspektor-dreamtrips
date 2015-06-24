package com.techery.spares.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.Global;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import de.greenrobot.event.EventBus;

public class BaseArrayListAdapter<BaseItemClass> extends RecyclerView.Adapter<AbstractCell> implements IRoboSpiceAdapter<BaseItemClass> {

    private final Map<Class, Class<? extends AbstractCell>> itemCellMapping = new HashMap<>();

    private final AdapterHelper adapterHelper;
    private final Provider<Injector> injector;
    protected List<BaseItemClass> items = new ArrayList<>();

    @Inject
    @Global
    protected EventBus eventBus;

    private List<Class> viewTypes = new ArrayList<>();

    public BaseArrayListAdapter(Context context, Provider<Injector> injector) {
        this.injector = injector;
        this.injector.get().inject(this);
        this.adapterHelper = new AdapterHelper((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
    }

    public void registerCell(Class itemClass, Class<? extends AbstractCell> cellClass) {
        this.itemCellMapping.put(itemClass, cellClass);
        int type = this.viewTypes.indexOf(itemClass);
        if (type == -1) {
            this.viewTypes.add(itemClass);
        }
    }

    @Override
    public AbstractCell onCreateViewHolder(ViewGroup parent, int viewType) {
        Class itemClass = this.viewTypes.get(viewType);
        Class<? extends AbstractCell> cellClass = this.itemCellMapping.get(itemClass);
        AbstractCell cell = this.adapterHelper.buildCell(cellClass, parent);
        cell.setEventBus(eventBus);
        this.injector.get().inject(cell);
        cell.afterInject();
        return cell;
    }

    @Override
    public long getItemId(int position) {
        if (getItem(position) instanceof BaseEntity) {
            return ((BaseEntity) getItem(position)).getId();
        }
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        BaseItemClass baseItemClass = this.items.get(position);
        Class itemClass = baseItemClass.getClass();
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
        cell.fillWithItem(item);
    }


    @Override
    public void onViewDetachedFromWindow(AbstractCell holder) {
        holder.clearResources();
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

    public void addItems(int index, List<BaseItemClass> result) {
        if (result != null) {
            this.items.addAll(index, result);
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

    public void remove(BaseItemClass location) {
        this.items.remove(location);
    }

    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        final BaseItemClass item = items.remove(fromPosition);

        items.add(toPosition, item);
    }

    public void clear() {
        this.items.clear();
        notifyDataSetChanged();
    }

    @Override
    public void addItems(ArrayList<BaseItemClass> baseItemClasses) {
        if (baseItemClasses != null) {
            this.items.addAll(baseItemClasses);
            this.notifyDataSetChanged();
        }
    }

    public void setItems(List<BaseItemClass> baseItemClasses) {
        this.items = baseItemClasses;
        this.notifyDataSetChanged();
    }

    public List<BaseItemClass> getItems() {
        return items;
    }

    @Override
    public int getCount() {
        return getItemCount();
    }
}