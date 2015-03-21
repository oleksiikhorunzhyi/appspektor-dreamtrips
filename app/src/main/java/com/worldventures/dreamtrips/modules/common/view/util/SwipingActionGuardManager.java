package com.worldventures.dreamtrips.modules.common.view.util;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class SwipingActionGuardManager {
    private static final String TAG = "ARVTouchActionGuardMgr";

    private static final boolean LOCAL_LOGV = false;
    private static final boolean LOCAL_LOGD = false;

    private RecyclerView.OnItemTouchListener mInternalUseOnItemTouchListener;
    private RecyclerView mRecyclerView;
    private boolean mGuarding;
    private int mInitialTouchX;
    private int mLastTouchX;
    private int mTouchSlop;
    private boolean mEnabled;

    public SwipingActionGuardManager() {
        mInternalUseOnItemTouchListener = new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return SwipingActionGuardManager.this.onInterceptTouchEvent(rv, e);
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                SwipingActionGuardManager.this.onTouchEvent(rv, e);
            }
        };
    }

    public boolean isReleased() {
        return (mInternalUseOnItemTouchListener == null);
    }

    public void attachRecyclerView(RecyclerView rv) {
        if (rv == null) {
            throw new IllegalArgumentException("RecyclerView cannot be null");
        }

        if (isReleased()) {
            throw new IllegalStateException("Accessing released object");
        }

        if (mRecyclerView != null) {
            throw new IllegalStateException("RecyclerView instance has already been set");
        }

        mRecyclerView = rv;
        mRecyclerView.addOnItemTouchListener(mInternalUseOnItemTouchListener);

        mTouchSlop = ViewConfiguration.get(rv.getContext()).getScaledTouchSlop();
    }

    public void release() {
        if (mRecyclerView != null && mInternalUseOnItemTouchListener != null) {
            mRecyclerView.removeOnItemTouchListener(mInternalUseOnItemTouchListener);
        }
        mInternalUseOnItemTouchListener = null;
        mRecyclerView = null;
    }

    /*package*/ boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        if (!mEnabled) {
            return false;
        }

        final int action = MotionEventCompat.getActionMasked(e);

        if (LOCAL_LOGV) {
            Log.v(TAG, "onInterceptTouchEvent() action = " + action);
        }

        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handleActionUpOrCancel();
                break;

            case MotionEvent.ACTION_DOWN:
                handleActionDown(e);
                break;

            case MotionEvent.ACTION_MOVE:
                if (handleActionMove(rv, e)) {
                    return true;
                }
                break;
        }

        return false;
    }

    /*package*/ void onTouchEvent(RecyclerView rv, MotionEvent e) {
        if (!mEnabled) {
            return;
        }

        final int action = MotionEventCompat.getActionMasked(e);

        if (LOCAL_LOGV) {
            Log.v(TAG, "onTouchEvent() action = " + action);
        }

        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handleActionUpOrCancel();
                break;
        }
    }

    private boolean handleActionMove(RecyclerView rv, MotionEvent e) {
        if (!mGuarding) {
            mLastTouchX = (int) (e.getX() + 0.5f);

            final int distance = mLastTouchX - mInitialTouchX;

            if ((Math.abs(distance) > mTouchSlop)) {
                // intercept vertical move touch events while animation is running
                mGuarding = true;
            }
        }

        return mGuarding;
    }

    private void handleActionUpOrCancel() {
        mGuarding = false;
        mInitialTouchX = 0;
        mLastTouchX = 0;
    }

    private void handleActionDown(MotionEvent e) {
        mInitialTouchX = mLastTouchX = (int) (e.getX() + 0.5f);
        mGuarding = false;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        if (mEnabled == enabled) {
            return;
        }
        mEnabled = enabled;

        if (!mEnabled) {
            handleActionUpOrCancel();
        }
    }

}