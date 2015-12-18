package com.messenger.ui.adapter;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * FilterCursorWrapper is for casses when running SQL query to get new cursor is not desirable.
 */
public class FilterCursorWrapper extends CursorWrapper {
    private int[] index;
    private int count;
    private int pos;

    public FilterCursorWrapper(Cursor cursor, String filter, int column) {
        super(cursor);
        filter = filter.toLowerCase();

        if (filter != "") {
            this.count = super.getCount();
            this.index = new int[this.count];
            for (int i = 0; i < this.count; i++) {
                super.moveToPosition(i);
                if (this.getString(column).toLowerCase().contains(filter)) {
                    this.index[this.pos++] = i;
                }
            }
            this.count = this.pos;
            this.pos = 0;
            super.moveToFirst();
        } else {
            this.count = super.getCount();
            this.index = new int[this.count];
            for (int i=0; i < this.count; i++) {
                this.index[i] = i;
            }
        }
    }

    @Override
    public boolean move(int offset) {
        return this.moveToPosition(this.pos + offset);
    }

    @Override
    public boolean moveToNext() {
        return this.moveToPosition(this.pos + 1);
    }

    @Override
    public boolean moveToPrevious() {
        return this.moveToPosition(this.pos - 1);
    }

    @Override
    public boolean moveToFirst() {
        return this.moveToPosition(0);
    }

    @Override
    public boolean moveToLast() {
        return this.moveToPosition(this.count - 1);
    }

    @Override
    public boolean moveToPosition(int position) {
        if (position >= this.count || position < 0) {
            return false;
        }
        return super.moveToPosition(this.index[position]);
    }

    @Override
    public int getCount() {
        return this.count;
    }

    @Override
    public int getPosition() {
        return this.pos;
    }
}