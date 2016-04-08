package com.messenger.util;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;

import java.util.Arrays;

import rx.Observable;

public class RxContentResolver {

    private final ContentResolver contentResolver;
    private CursorFetcher<Query> cursorFetcher;
    private final HandlerThread thread;

    public RxContentResolver(ContentResolver contentResolver, CursorFetcher<Query> cursorFetcher) {
        this.contentResolver = contentResolver;
        this.cursorFetcher = cursorFetcher;
        thread = new HandlerThread("ObserverThread", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
    }

    public Observable<Cursor> query(Query query, Uri... uriToObserve) {
        final ContentObserver[] contentObserver = {null};
        return Observable.<Void>create(subscriber -> {
                    contentObserver[0] = new ContentObserver(new Handler(thread.getLooper())) {
                        @Override
                        public void onChange(boolean selfChange) {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(null);
                            } else {
                                unsubscribeFromContentUpdates(this);
                            }
                        }
                    };
                    if (query.uri != null) {
                        subscribeToContentUpdates(query.uri, contentObserver[0]);
                    }
                    if (uriToObserve != null) {
                        for (Uri uri : uriToObserve) {
                            subscribeToContentUpdates(uri, contentObserver[0]);
                        }
                    }
                    subscriber.onNext(null);
                }
        )
                .doOnUnsubscribe(() -> {
                    if (contentObserver[0] != null)
                        unsubscribeFromContentUpdates(contentObserver[0]);
                })
                .onBackpressureLatest()
                .flatMap(aVoid -> fetchCursor(query));
    }

    private Observable<Cursor> fetchCursor(Query query) {
        return Observable.create(subscriber -> {
            Cursor cursor = cursorFetcher.fetchCursor(query);
            try {
                // Ensure the cursor window is filled.
                cursor.getCount();
                subscriber.onNext(cursor);
            } catch (RuntimeException ex) {
                cursor.close();
                subscriber.onError(new RxFetchCursorException(query.toString(), ex));
            }
        });
    }

    private void subscribeToContentUpdates(Uri uri, ContentObserver contentObserver) {
        contentResolver.registerContentObserver(uri, true, contentObserver);
    }

    private void unsubscribeFromContentUpdates(ContentObserver contentObserver) {
        contentResolver.unregisterContentObserver(contentObserver);
    }

    // TODO insert

    // TODO update

    // TODO delete

    public interface CursorFetcher<Q> {
        Cursor fetchCursor(Q query);
    }

    public static class Query {
        public final Uri uri;
        public final String[] projection;
        public final String selection;
        public final String[] selectionArgs;
        public final String sortOrder;

        private Query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                      String sortOrder) {
            this.uri = uri;
            this.projection = projection;
            this.selection = selection;
            this.selectionArgs = selectionArgs;
            this.sortOrder = sortOrder;
        }

        public Builder buildUpon() {
            return new Builder(uri).withProjection(projection)
                    .withSelection(selection)
                    .withSelectionArgs(selectionArgs)
                    .withSortOrder(sortOrder);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Query query = (Query) o;

            if (!Arrays.equals(projection, query.projection)) return false;
            if (selection != null ? !selection.equals(query.selection) : query.selection != null) {
                return false;
            }
            if (!Arrays.equals(selectionArgs, query.selectionArgs)) return false;
            if (sortOrder != null ? !sortOrder.equals(query.sortOrder) : query.sortOrder != null) {
                return false;
            }
            if (uri != null ? !uri.equals(query.uri) : query.uri != null) return false;

            return true;
        }

        @Override
        public String toString() {
            return "Query{" +
                    "uri=" + uri +
                    ", projection=" + Arrays.toString(projection) +
                    ", selection='" + selection + '\'' +
                    ", selectionArgs=" + Arrays.toString(selectionArgs) +
                    ", sortOrder='" + sortOrder + '\'' +
                    '}';
        }

        @Override
        public int hashCode() {
            int result = uri != null ? uri.hashCode() : 0;
            result = 31 * result + (projection != null ? Arrays.hashCode(projection) : 0);
            result = 31 * result + (selection != null ? selection.hashCode() : 0);
            result = 31 * result + (selectionArgs != null ? Arrays.hashCode(selectionArgs) : 0);
            result = 31 * result + (sortOrder != null ? sortOrder.hashCode() : 0);
            return result;
        }

        public static class Builder {
            private final Uri uri;

            private String[] projection;
            private String selection;
            private String[] selectionArgs;
            private String sortOrder;

            public Builder(Uri uri) {
                this.uri = uri;
            }

            public Builder withProjection(String[] projection) {
                this.projection = projection;
                return this;
            }

            public Builder withSelection(String selection) {
                this.selection = selection;
                return this;
            }

            public Builder withSelectionArgs(String[] selectionArgs) {
                this.selectionArgs = selectionArgs;
                return this;
            }

            public Builder withSortOrder(String sortOrder) {
                this.sortOrder = sortOrder;
                return this;
            }

            public Query build() {
                return new Query(uri, projection, selection, selectionArgs, sortOrder);
            }
        }
    }

    public static class RxFetchCursorException extends Exception {

        private RxFetchCursorException(String message, Exception ex) {
            super(message, ex);
        }
    }
}
