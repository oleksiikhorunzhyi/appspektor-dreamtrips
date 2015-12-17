package com.messenger.loader;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.AsyncTaskLoader;

import com.messenger.messengerservers.entities.Message;
import com.raizlabs.android.dbflow.config.FlowManager;

public abstract class BaseCursorRowLoader extends AsyncTaskLoader<Cursor> {
    private Cursor cursor;
    private SQLiteDatabase db;

    public BaseCursorRowLoader(Context context) {
        super(context);
    }

    protected Cursor rawQuery(String query, String[] whereArgs) {
        db = FlowManager.getDatabaseForTable(Message.class).getWritableDatabase();
        return db.rawQuery(query, whereArgs);
    }

    /* Runs on the UI thread */
    @Override
    public void deliverResult(Cursor cursor) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            if (cursor != null) {
                cursor.close();
            }
            return;
        }
        Cursor oldCursor = this.cursor;
        this.cursor = cursor;

        if (isStarted()) {
            super.deliverResult(cursor);
        }

        if (oldCursor != null && oldCursor != cursor && !oldCursor.isClosed()) {
            oldCursor.close();
        }
    }


    /**
     * Starts an asynchronous load of the contacts list data. When the result is ready the callbacks
     * will be called on the UI thread. If a previous load has been completed and is still valid
     * the result may be passed to the callbacks immediately.
     * <p>
     * Must be called from the UI thread
     */
    @Override
    protected void onStartLoading() {
        if (cursor != null) {
            deliverResult(cursor);
        }
        if (takeContentChanged() || cursor == null) {
            forceLoad();
        }
    }

    /**
     * Must be called from the UI thread
     */
    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    protected void onAbandon() {
        super.onAbandon();
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
        db = null;
        cursor = null;
    }
}