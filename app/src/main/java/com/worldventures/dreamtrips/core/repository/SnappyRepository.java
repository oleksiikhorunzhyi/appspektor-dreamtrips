package com.worldventures.dreamtrips.core.repository;

import com.worldventures.dreamtrips.modules.config.model.Configuration;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.trips.model.Pin;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.model.filter.CachedTripFilters;

import java.util.Collection;
import java.util.List;

public interface SnappyRepository {

   String TRIP_FILTERS = "trip_filters";

   String BADGE_NOTIFICATIONS_COUNT = "badge_notifications_count";
   String EXCLUSIVE_NOTIFICATIONS_COUNT = "Unread-Notifications-Count";
   String FRIEND_REQUEST_COUNT = "Friend-Requests-Count";
   String GCM_REG_TOKEN = "GCM_REG_TOKEN ";
   String LAST_SYNC_APP_VERSION = "LAST_SYNC_APP_VERSION";

   String TRIPS = "TRIPS";
   String PINS = "PINS";
   String TRIPS_DETAILS = "TRIPS_DETAILS";

   String DTL_TRANSACTION_PREFIX = "DTL_TRANSACTION_";
   String DTL_LAST_MAP_POSITION = "DTL_LAST_MAP_POSITION";

   String CONFIGURATION = "CONFIGURATION";
   String UPDATE_APP_OPTIONAL_DIALOG_CONFIRMED_TIMESTAMP = "UPDATE_APP_OPTIONAL_DIALOG_CONFIRMED_TIMESTAMP";

   void clearAll();

   Boolean isEmpty(String key);

   <T> void putList(String key, Collection<T> list);

   <T> List<T> readList(String key, Class<T> clazz);

   void clearAllForKey(String key);

   void clearAllForKeys(String... keys);

   void saveAppUpdateRequirement(Configuration updateRequirement);

   Configuration getAppUpdateRequirement();

   void saveAppUpdateOptionalDialogConfirmedTimestamp(long appUpdateDialogShownTimestamp);

   long getAppUpdateOptionalDialogConfirmedTimestamp();

   void saveBadgeNotificationsCount(int notificationsCount);

   int getBadgeNotificationsCount();

   void saveNotificationsCount(int count);

   void saveFriendRequestsCount(int count);

   int getExclusiveNotificationsCount();

   int getFriendsRequestsCount();

   String getGcmRegToken();

   void setGcmRegToken(String token);

   void cleanLastMapCameraPosition();

   DtlTransaction getDtlTransaction(String id);

   void saveDtlTransaction(String id, DtlTransaction dtlTransaction);

   void deleteDtlTransaction(String id);

   void setLastSyncAppVersion(String appVersion);

   String getLastSyncAppVersion();

   void saveTrips(List<TripModel> trips);

   List<TripModel> getTrips();

   void saveTripFilters(CachedTripFilters tripFilters);

   CachedTripFilters getTripFilters();

   void savePins(List<Pin> pins);

   List<Pin> getPins();

   void saveTripDetails(TripModel tripModel);

   void saveTripsDetails(List<TripModel> trips);

   boolean hasTripsDetailsForUids(List<String> uids);

   List<TripModel> getTripsDetailsForUids(List<String> uids);

   TripModel getTripDetail(String uid);
}
