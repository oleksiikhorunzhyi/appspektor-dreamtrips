package com.worldventures.dreamtrips.modules.facebook;

import com.facebook.Response;
import com.facebook.model.GraphMultiResult;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class FacebookUtils {
   private static final String EMPTY = "";

   public static <T extends GraphObject> List<T> typedListFromResponse(Response response, Class<T> clazz) {
      GraphMultiResult multiResult = response.getGraphObjectAs(GraphMultiResult.class);
      if (multiResult == null) {
         return Collections.emptyList();
      }
      GraphObjectList<GraphObject> data = multiResult.getData();
      if (data == null) {
         return Collections.emptyList();
      }
      return data.castToListOf(clazz);
   }


   public static String getPropertyInsideProperty(GraphObject graphObject, String parent, String child) {
      if (graphObject == null) {
         return null;
      }

      JSONObject jsonObject = (JSONObject) graphObject.getProperty(parent);
      if (jsonObject != null) {
         return String.valueOf(jsonObject.opt(child));
      }
      return null;
   }

   public static String getPropertyString(GraphObject graphObject, String property) {
      if (graphObject == null) {
         return null;
      }
      return String.valueOf(graphObject.getProperty(property));
   }

   public static Long getPropertyLong(GraphObject graphObject, String property) {
      if (graphObject == null) {
         return null;
      }
      Object value = graphObject.getProperty(property);
      if (value == null || value.equals(EMPTY)) {
         return null;
      }

      try {
         return Long.valueOf(String.valueOf(value));
      } catch (NumberFormatException e) {
         return null;
      }
   }

   public static Boolean getPropertyBoolean(GraphObject graphObject, String property) {
      if (graphObject == null) {
         return false;
      }
      Object value = graphObject.getProperty(property);
      if (value == null || value.equals(EMPTY)) {
         return false;
      }
      return Boolean.valueOf(String.valueOf(value));
   }

   public static Integer getPropertyInteger(GraphObject graphObject, String property) {
      if (graphObject == null) {
         return null;
      }
      Object value = graphObject.getProperty(property);
      if (value == null || value.equals(EMPTY)) {
         return null;
      }

      try {
         return Integer.valueOf(String.valueOf(value));
      } catch (NumberFormatException e) {
         return null;
      }

   }

   public static Double getPropertyDouble(GraphObject graphObject, String property) {
      if (graphObject == null) {
         return null;
      }
      Object value = graphObject.getProperty(property);
      if (value == null || value.equals(EMPTY)) {
         return null;
      }
      return Double.valueOf(String.valueOf(value));
   }

   public static JSONArray getPropertyJsonArray(GraphObject graphObject, String property) {
      if (graphObject == null) {
         return null;
      }
      Object value = graphObject.getProperty(property);
      if (value instanceof JSONArray) {
         return (JSONArray) value;
      }

      return null;
   }

   public static GraphObject getPropertyGraphObject(GraphObject graphObject, String property) {
      if (graphObject == null) {
         return null;
      }
      return graphObject.getPropertyAs(property, GraphObject.class);
   }

   public static <T> List<T> createList(GraphObject graphObject, String property, Converter<T> converter) {
      List<T> result = new ArrayList<T>();
      if (graphObject == null) {
         return result;
      }

      GraphObjectList<GraphObject> graphObjects = graphObject.getPropertyAsList(property, GraphObject.class);
      if (graphObjects == null || graphObjects.isEmpty()) {
         return result;
      }

      ListIterator<GraphObject> iterator = graphObjects.listIterator();
      while (iterator.hasNext()) {
         GraphObject graphObjectItr = iterator.next();
         T t = converter.convert(graphObjectItr);
         result.add(t);
      }
      return result;
   }

   public interface GeneralConverter<T, E> {
      T convert(E e);
   }

   public interface Converter<T> extends GeneralConverter<T, GraphObject> {}

}
