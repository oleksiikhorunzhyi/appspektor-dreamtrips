package com.techery.spares.ui.fragment.loadable.collection;

import android.view.View;
import android.widget.AdapterView;

import com.techery.spares.adapter.DataListAdapter;
import com.techery.spares.ui.view.cell.BaseCell;

public abstract class InjectingListFragment<T, ET, LV> extends CollectionFragment<T> implements AdapterView.OnItemClickListener {
    protected LV listView;
    private BaseCell.CellBuilder cellBuilder;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        setListView(findListView(rootView));
        setupOnItemClickListener(getListView());
    }

    public LV getListView() {
        return listView;
    }

    public void setListView(LV listView) {
        this.listView = listView;
        linkAdapter(this.listView);
    }

    @Override
    public void setDataAdapter(DataListAdapter<T> dataAdapter) {
        super.setDataAdapter(dataAdapter);

        if (getContentLoader() != null) {
            getDataAdapter().setContentLoader(getContentLoader());
        }

        if (getListView() != null) {
            linkAdapter(getListView());
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ET item = (ET) getDataAdapter().getItem(position);
        
        getEventBus().post(new DataListAdapter.Events.ItemSelectionEvent<ET>(item));
    }

    protected BaseCell.CellBuilder getCellBuilder() {
        return cellBuilder;
    }

    public void setCellBuilder(BaseCell.CellBuilder cellBuilder) {
        this.cellBuilder = cellBuilder;
        getDataAdapter().getController().setCellBuilder(getCellBuilder());
    }

    protected abstract void setupOnItemClickListener(LV listView);
    protected abstract void linkAdapter(LV listView);
    protected abstract LV findListView(View rootView);

}
