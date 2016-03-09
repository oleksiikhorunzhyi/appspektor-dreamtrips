package com.messenger.util;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.messenger.entities.DataParticipant$Table;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.model.Participant;
import com.messenger.ui.util.recyclerview.Header;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.worldventures.dreamtrips.R;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import timber.log.Timber;

public class UsersDataTransformer
        implements Observable.Transformer<Cursor, UsersDataTransformer.TransformResult> {

    private Context context;
    private String filter;
    private Cursor currentCursor;

    private boolean searchAdmin;
    private boolean searchHosts;

    public UsersDataTransformer(Context context) {
        this.context = context;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setSearchAdmin(boolean searchAdmin) {
        this.searchAdmin = searchAdmin;
    }

    public void setSearchHosts(boolean searchHosts) {
        this.searchHosts = searchHosts;
    }

    @Override
    public Observable<UsersDataTransformer.TransformResult> call(Observable<Cursor> cursorObservable) {
        return cursorObservable
                .flatMap(cursor -> getTransformResultObservable(cursor))
                .doOnUnsubscribe(() -> {
                    closeCursor();
                });
    }

    private Observable<TransformResult> getTransformResultObservable(Cursor cursor) {
        return Observable.<TransformResult>create(subscriber -> {
                    try {
                        subscriber.onNext(getResult(cursor));
                        subscriber.onCompleted();
                    } catch (Throwable e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    } finally {
                        if (currentCursor != null && currentCursor != cursor) {
                            closeCursor();
                        }
                        currentCursor = cursor;
                    }
                });
    }

    private UsersDataTransformer.TransformResult getResult(Cursor cursor) {
        int participantAffiliationIndex = -1;
        if (searchAdmin) {
            participantAffiliationIndex = cursor.getColumnIndex(DataParticipant$Table.AFFILIATION);
        }

        DataUser admin = null;
        List<DataUser> hosts = new ArrayList<>();
        List<DataUser> users = new ArrayList<>();
        int usersCount = cursor.getCount();

        if (cursor.moveToFirst()) {
            do {
                DataUser user = SqlUtils.convertToModel(true, DataUser.class, cursor);
                if (searchAdmin && TextUtils.equals(Participant.Affiliation.OWNER,
                        cursor.getString(participantAffiliationIndex))) {
                    admin = user;
                } else if (searchHosts && false) { // TODO ignore this for now, how do we check if user is host?
                    hosts.add(user);
                } else if (search(user)) {
                    users.add(user);
                }
            } while (cursor.moveToNext());
        }

        return createUsersWithSections(admin, hosts, users, usersCount);
    }

    @NonNull
    private TransformResult createUsersWithSections(DataUser admin, List<DataUser> hosts,
                                                    List<DataUser> users, int usersCount) {
        List usersWithHeaders = new ArrayList<>();
        if (admin != null) {
            usersWithHeaders.add(new Header(context.getString(R.string.edit_chat_members_admin_section)));
            usersWithHeaders.add(admin);
        }

        if (!hosts.isEmpty()) {
            usersWithHeaders.add(new Header(context.getString(R.string.edit_chat_members_host_section)));
            usersWithHeaders.addAll(hosts);
        }

        if (!users.isEmpty()) {
            Header previousHeader = null;
            for (DataUser user : users) {
                String displayName = user.getDisplayedName();
                String newHeaderName = TextUtils.isEmpty(displayName) ? "" : displayName.substring(0, 1);
                if (previousHeader == null ||
                        !previousHeader.getName().equals(newHeaderName)) {
                    usersWithHeaders.add(previousHeader = new Header(newHeaderName));
                }
                usersWithHeaders.add(user);
            }
        }

        return new TransformResult(admin, usersWithHeaders, usersCount);
    }

    private boolean search(DataUser user) {
        if (TextUtils.isEmpty(filter)) return true;
        if (TextUtils.isEmpty(user.getDisplayedName())) return false;
        return user.getDisplayedName().toLowerCase().contains(filter.toLowerCase());
    }

    private void closeCursor() {
        if (currentCursor != null && !currentCursor.isClosed()) {
            currentCursor.close();
        }
    }

    public static class TransformResult {
        private DataUser admin;
        private List usersWithHeaders;
        private int usersCount;

        public TransformResult(DataUser admin, List usersWithHeaders, int usersCount) {
            this.admin = admin;
            this.usersWithHeaders = usersWithHeaders;
            this.usersCount = usersCount;
        }

        public DataUser getAdmin() {
            return admin;
        }

        public List getUsersWithHeaders() {
            return usersWithHeaders;
        }

        public int getUsersCount() {
            return usersCount;
        }
    }
}
