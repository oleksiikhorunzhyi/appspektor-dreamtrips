package com.worldventures.dreamtrips.modules.friends.model;

import java.io.Serializable;

public class Circle implements Serializable, Comparable<Circle> {

   public static Circle all(String title) {
      Circle all = new Circle();
      all.name = title;
      return all;
   }

   String id;
   String name;
   boolean predefined;

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public boolean isPredefined() {
      return predefined;
   }

   @Override
   public String toString() {
      return name;
   }

   @Override
   public int compareTo(Circle another) {
      return name.compareTo(another.getName());
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Circle circle = (Circle) o;

      return id.equals(circle.id);
   }

   @Override
   public int hashCode() {
      return id.hashCode();
   }
}
