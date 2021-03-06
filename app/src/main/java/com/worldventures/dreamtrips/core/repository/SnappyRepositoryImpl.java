package com.worldventures.dreamtrips.core.repository;

import android.content.Context;

import com.innahema.collections.query.queriables.Queryable;
import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.worldventures.core.repository.BaseSnappyRepository;
import com.worldventures.core.repository.DefaultSnappyOpenHelper;
import com.worldventures.dreamtrips.modules.config.model.Configuration;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.ImmutableDtlTransaction;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.model.filter.CachedTripFilters;
import com.worldventures.dreamtrips.modules.trips.model.map.Pin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class SnappyRepositoryImpl extends BaseSnappyRepository implements SnappyRepository {

   private final DefaultSnappyOpenHelper defaultSnappyOpenHelper;

   SnappyRepositoryImpl(Context context, DefaultSnappyOpenHelper defaultSnappyOpenHelper) {
      super(context, defaultSnappyOpenHelper.provideExecutorService());
      this.defaultSnappyOpenHelper = defaultSnappyOpenHelper;
   }

   @Override
   protected DB openDbInstance(Context context) throws SnappydbException {
      return defaultSnappyOpenHelper.openDbInstance(context);
   }

   @Override
   public Boolean isEmpty(String key) {
      return actWithResult((db) -> {
         String[] keys = db.findKeys(key);
         return keys == null || keys.length == 0;
      }).or(false);
   }

   @Override
   public <T> void putList(String key, Collection<T> list) {
      super.putList(key, list);
   }

   @Override
   public <T> List<T> readList(String key, Class<T> clazz) {
      return super.readList(key, clazz);
   }

   @Override
   public void clearAll() {
      act(DB::destroy);
   }

   /**
    * Method is intended to delete all records for given key.
    *
    * @param key key to be deleted.
    */
   @Override
   public void clearAllForKey(String key) {
      clearAllForKeys(key);
   }

   /**
    * Method is intended to delete all records for given keys.
    *
    * @param keys keys array to be deleted.
    */
   @Override
   public void clearAllForKeys(String... keys) {
      Queryable.from(keys).forEachR(key -> act(db -> {
         String[] placesKeys = db.findKeys(key);
         for (String placeKey : placesKeys) {
            db.del(placeKey);
         }
      }));
   }

   @Override
   public String getLastSyncAppVersion() {
      return actWithResult(db -> db.get(LAST_SYNC_APP_VERSION)).orNull();
   }

   @Override
   public void setLastSyncAppVersion(String appVersion) {
      act(db -> db.put(LAST_SYNC_APP_VERSION, appVersion));
   }

   ///////////////////////////////////////////////////////////////////////////
   // App version check
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void saveAppUpdateRequirement(Configuration updateRequirement) {
      act(db -> db.put(CONFIGURATION, updateRequirement));
   }

   @Override
   public Configuration getAppUpdateRequirement() {
      return actWithResult(db -> db.getObject(CONFIGURATION, Configuration.class)).orNull();
   }

   @Override
   public void saveAppUpdateOptionalDialogConfirmedTimestamp(long appUpdateDialogShownTimestamp) {
      act(db -> db.putLong(UPDATE_APP_OPTIONAL_DIALOG_CONFIRMED_TIMESTAMP, appUpdateDialogShownTimestamp));
   }

   @Override
   public long getAppUpdateOptionalDialogConfirmedTimestamp() {
      return actWithResult(db -> db.getLong(UPDATE_APP_OPTIONAL_DIALOG_CONFIRMED_TIMESTAMP)).or(0L);
   }

   ///////////////////////////////////////////////////////////////////////////
   //
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public String getGcmRegToken() {
      return actWithResult(db -> db.get(GCM_REG_TOKEN)).orNull();
   }

   @Override
   public void setGcmRegToken(String token) {
      act(db -> db.put(GCM_REG_TOKEN, token));
   }

   ///////////////////////////////////////////////////////////////////////////
   // DTL
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void cleanLastMapCameraPosition() {
      clearAllForKey(DTL_LAST_MAP_POSITION);
   }

   @Override
   public DtlTransaction getDtlTransaction(String id) {
      return actWithResult(db -> db.getObject(DTL_TRANSACTION_PREFIX + id, ImmutableDtlTransaction.class)).orNull();
   }

   @Override
   public void saveDtlTransaction(String id, DtlTransaction dtlTransaction) {
      act(db -> db.put(DTL_TRANSACTION_PREFIX + id, dtlTransaction));
   }

   @Override
   public void deleteDtlTransaction(String id) {
      act(db -> db.del(DTL_TRANSACTION_PREFIX + id));
   }

   @Override
   public void saveTrips(List<TripModel> tripModels) {
      if (tripModels == null) {
         tripModels = new ArrayList<>();
      }
      putList(TRIPS, tripModels);
   }

   @Override
   public List<TripModel> getTrips() {
      return readList(TRIPS, TripModel.class);
   }

   @Override
   public void saveTripFilters(CachedTripFilters tripFilters) {
      act(db -> db.put(TRIP_FILTERS, tripFilters));
   }

   @Override
   public CachedTripFilters getTripFilters() {
      return actWithResult(db -> db.get(TRIP_FILTERS, CachedTripFilters.class)).orNull();
   }

   @Override
   public void savePins(List<Pin> pins) {
      if (pins == null) {
         pins = new ArrayList<>();
      }
      putList(PINS, pins);
   }

   @Override
   public List<Pin> getPins() {
      return readList(PINS, Pin.class);
   }

   @Override
   public void saveTripDetails(TripModel tripModel) {
      act(db -> db.put(TRIPS_DETAILS + tripModel.getUid(), tripModel));
   }

   @Override
   public void saveTripsDetails(List<TripModel> trips) {
      act(db -> {
         for (TripModel tripModel : trips) {
            db.put(TRIPS_DETAILS + tripModel.getUid(), tripModel);
         }
      });
   }

   @Override
   public TripModel getTripDetail(String uid) {
      return actWithResult(db -> db.get(TRIPS_DETAILS + uid, TripModel.class)).orNull();
   }

   @Override
   public boolean hasTripsDetailsForUids(List<String> uids) {
      return actWithResult(db ->
            Queryable.from(db.findKeys(TRIPS_DETAILS))
                  .toList()
                  .containsAll(Queryable.from(uids).map(uid -> TRIPS_DETAILS + uid).toList())
      ).or(false);
   }

   @Override
   public List<TripModel> getTripsDetailsForUids(List<String> uids) {
      return actWithResult(db -> {
         List<TripModel> tripModels = new ArrayList<>();
         for (String uid : uids) {
            TripModel tripModel = db.get(TRIPS_DETAILS + uid, TripModel.class);
            if (tripModel != null) {
               tripModels.add(tripModel);
            }
         }
         return tripModels;
      }).or(new ArrayList<>());
   }

   /**
    * All notifications
    */
   @Override
   public void saveBadgeNotificationsCount(int notificationsCount) {
      act(db -> db.putInt(BADGE_NOTIFICATIONS_COUNT, notificationsCount));
   }

   /**
    * All notifications
    */
   @Override
   public int getBadgeNotificationsCount() {
      return actWithResult(db -> db.getInt(BADGE_NOTIFICATIONS_COUNT)).or(0);
   }

   @Override
   public void saveNotificationsCount(int count) {
      act(db -> db.putInt(EXCLUSIVE_NOTIFICATIONS_COUNT, count));
   }

   @Override
   public void saveFriendRequestsCount(int count) {
      act(db -> db.putInt(FRIEND_REQUEST_COUNT, count));
   }

   @Override
   public int getExclusiveNotificationsCount() {
      return actWithResult(db -> db.getInt(EXCLUSIVE_NOTIFICATIONS_COUNT)).or(0);
   }

   @Override
   public int getFriendsRequestsCount() {
      return actWithResult(db -> db.getInt(FRIEND_REQUEST_COUNT)).or(0);
   }
}
