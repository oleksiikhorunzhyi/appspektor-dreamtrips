package com.techery.spares.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.techery.spares.loader.ContentLoader;
import com.techery.spares.ui.view.cell.BaseCell;

import de.greenrobot.event.EventBus;

public class BaseCursorListAdapter extends CursorAdapter implements DataListAdapter<Cursor>,ContentLoader.ContentLoadingObserving<Cursor> {
    private ContentLoader<Cursor> contentLoader;
    private AdapterController adapterController;

    public BaseCursorListAdapter(Context context) {
        this(context, null, null);
    }

    public BaseCursorListAdapter(Context context, BaseCell.CellBuilder cellBuilder) {
        this(context, cellBuilder, null);
    }

    public BaseCursorListAdapter(Context context, BaseCell.CellBuilder cellBuilder, EventBus bus) {
        super(context, null, false);
        this.adapterController = new AdapterController(context, cellBuilder, bus);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return this.adapterController.newView(this, cursor.getPosition(), parent);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        this.adapterController.bindView(this, cursor.getPosition(), view);
    }

    @Override
    public void setContentLoader(ContentLoader<Cursor> contentLoader) {
        this.contentLoader = contentLoader;
        if (this.contentLoader != null) {
            this.contentLoader.getContentLoaderObserver().registerObserver(this);
            if (this.contentLoader.getResult() != null) {
                onFinishLoading(this.contentLoader.getResult());
            }
        }
    }

    @Override
    public ContentLoader<Cursor> getContentLoader() {
        return this.contentLoader;
    }

    @Override
    public AdapterController getController() {
        return this.adapterController;
    }


    @Override
    public void onStartLoading() {

    }

    @Override
    public void onFinishLoading(Cursor result) {
        result.moveToPosition(-1);
        result.moveToNext();
        swapCursor(result);
        notifyDataSetChanged();
    }

    @Override
    public void onError(Throwable throwable) {

    }
}
