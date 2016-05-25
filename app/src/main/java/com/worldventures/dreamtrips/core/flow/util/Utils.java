/*
 * Copyright 2013 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.worldventures.dreamtrips.core.flow.util;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewTreeObserver;

public final class Utils {

    public interface OnMeasuredCallback {
        void onMeasured(View view, int width, int height);
    }

    public interface OnMeasureFailCallback {
        void onMeasureFailed(View view);
    }

    public static void waitForMeasure(final View view, final OnMeasuredCallback callback) {
        waitForMeasure(view, callback, null);
    }

    public static void waitForMeasure(final View view, final OnMeasuredCallback callback,
                                      @Nullable final OnMeasureFailCallback failCallback) {
        int width = view.getWidth();
        int height = view.getHeight();
        if (width > 0 || height > 0) {
            callback.onMeasured(view, width, height);
            return;
        }
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                int width = view.getWidth();
                int height = view.getHeight();
                if (width > 0 || height > 0) {
                    callback.onMeasured(view, width, height);
                } else if (failCallback != null) {
                    failCallback.onMeasureFailed(view);
                }
                if (viewTreeObserver.isAlive()) viewTreeObserver.removeOnPreDrawListener(this);
                return true;
            }
        });
    }

    private Utils() {
    }
}
