package com.worldventures.dreamtrips.core.janet;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.AttributeMap;
import com.worldventures.dreamtrips.core.utils.tracksystem.LifecycleEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Tracker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.techery.janet.ActionHolder;
import io.techery.janet.ActionService;
import io.techery.janet.JanetException;
import timber.log.Timber;

public class AnalyticsService extends ActionService {

   private final Map<String, Tracker> trackers = new HashMap<>();

   public AnalyticsService(Collection<Tracker> trackers) {
      Queryable.from(trackers).forEachR(tracker -> this.trackers.put(tracker.getKey(), tracker));
   }

   @Override
   protected Class getSupportedAnnotationType() {
      return AnalyticsEvent.class;
   }

   @Override
   protected <A> void sendInternal(ActionHolder<A> holder) throws JanetException {
      try {
         final String category = getCategory(holder);
         final String[] types = getTypes(holder);
         for (String type : types) {
            checkType(type);
            if (category != null && category.equals(LifecycleEvent.LIFECYCLE_CATEGORY)) {
               handleLifecycleEvent(type, (LifecycleEvent) holder.action());
            } else {
               String action = getAction(holder);
               Map<String, Object> data = getData(holder);
               tryLogEvent(type, category, action, data);
               trackers.get(type).trackEvent(TextUtils.isEmpty(category) ? null : category, action, data);
            }
         }
      } catch (Throwable e) {
         throw new AnalyticsServiceException(e);
      }
   }

   private void checkType(String type) {
      if (!trackers.containsKey(type)) {
         throw new IllegalArgumentException("Unsupported tracker type: " + type);
      }
   }

   private void handleLifecycleEvent(String type, LifecycleEvent action) throws AnalyticsServiceException {
      switch (action.getAction()) {
         case LifecycleEvent.ACTION_ONCREATE:
            trackers.get(type).onCreate(action.getActivity());
            break;
         case LifecycleEvent.ACTION_ONSTART:
            trackers.get(type).onStart(action.getActivity());
            break;
         case LifecycleEvent.ACTION_ONRESUME:
            trackers.get(type).onResume(action.getActivity());
            break;
         case LifecycleEvent.ACTION_ONPAUSE:
            trackers.get(type).onPause(action.getActivity());
            break;
         case LifecycleEvent.ACTION_ONSTOP:
            trackers.get(type).onStop(action.getActivity());
            break;
         case LifecycleEvent.ACTION_ONSAVESTATE:
            trackers.get(type).onSaveInstanceState(action.getState());
            break;
         case LifecycleEvent.ACTION_ONRESTORESTATE:
            trackers.get(type).onRestoreInstanceState(action.getState());
            break;
         default:
            throw new IllegalArgumentException("Unsupported lifecycle event");
      }
   }

   @Nullable
   private String getCategory(ActionHolder holder) {
      return holder.action().getClass().getAnnotation(AnalyticsEvent.class).category();
   }

   private static String getAction(ActionHolder holder) {
      return holder.action().getClass().getAnnotation(AnalyticsEvent.class).action();
   }

   private static String[] getTypes(ActionHolder holder) {
      return holder.action().getClass().getAnnotation(AnalyticsEvent.class).trackers();
   }

   private static Map<String, Object> getData(ActionHolder holder) throws IllegalAccessException {
      Map<String, Object> data = new HashMap<>();
      List<FieldAttribute> fieldAttributes = getAttributeFields(holder.action().getClass(), holder);
      Queryable.from(fieldAttributes).forEachR(fieldAttribute -> data.put(fieldAttribute.name, fieldAttribute.value));
      return data;
   }

   private static List<FieldAttribute> getAttributeFields(Class actionClass, ActionHolder actionHolder) throws IllegalAccessException {
      // TODO :: 17.06.16 as per think Alex Malevaniy review - this annotation processing
      // TODO :: 17.06.16 needs to be reworked for better performance
      List<FieldAttribute> result = new ArrayList<>();
      Field[] declaredFields = actionClass.getDeclaredFields();
      for (Field field : declaredFields) {
         FieldAttribute fieldAttribute = getFieldAttribute(field, actionHolder.action());
         if (fieldAttribute != null) result.add(fieldAttribute);
         List<FieldAttribute> fieldAttributeList = getFieldAttributeList(field, actionHolder.action());
         if (fieldAttributeList != null) result.addAll(fieldAttributeList);
      }
      if (actionClass.getSuperclass() != null) {
         result.addAll(getAttributeFields(actionClass.getSuperclass(), actionHolder));
      }
      return result;
   }

   @Nullable
   private static FieldAttribute getFieldAttribute(Field field, Object action) throws IllegalAccessException {
      Attribute annotation = field.getAnnotation(Attribute.class);
      if (annotation != null) {
         field.setAccessible(true);
         Object value = field.get(action);
         if (value != null) {
            return new FieldAttribute(annotation.value(), String.valueOf(value));
         }
      }
      return null;
   }

   @Nullable
   private static List<FieldAttribute> getFieldAttributeList(Field field, Object action) throws IllegalAccessException {
      AttributeMap annotation = field.getAnnotation(AttributeMap.class);
      List<FieldAttribute> fieldAttributeList = new ArrayList<>();
      if (annotation != null) {
         field.setAccessible(true);
         Map<String, String> map = (Map<String, String>) field.get(action);
         if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet())
               fieldAttributeList.add(new FieldAttribute(entry.getKey(), entry.getValue()));
         }
      }
      return fieldAttributeList;
   }

   private void tryLogEvent(@NonNull String type, String category, String action, Map<String, Object> data) {
      if (!BuildConfig.ANALYTICS_LOG_ENABLED) return;
      //
      StringBuilder stringBuilder = new StringBuilder("Analytic event sending attempted:\n");
      //
      stringBuilder.append("\t\tType: ").append(type).append("\n");
      if (!TextUtils.isEmpty(category)) {
         stringBuilder.append("\t\tCategory: ").append(category).append("\n");
      }
      stringBuilder.append("\t\tAction: ").append(action).append("\n");
      //
      if (!data.isEmpty()) {
         stringBuilder.append("Data:\n");
         for (Map.Entry<String, Object> dataEntry : data.entrySet()) {
            if (dataEntry.getValue() != null) {
               stringBuilder.append("\t\t")
                     .append(dataEntry.getKey())
                     .append(": ")
                     .append(dataEntry.getValue())
                     .append("\n");
            }
         }
      }
      Timber.v(stringBuilder.toString());
   }

   @Override
   protected <A> void cancel(ActionHolder<A> holder) {
   }

   private static class FieldAttribute {
      private String name;
      private String value;

      public FieldAttribute(String name, String value) {
         this.name = name;
         this.value = value;
      }
   }
}
