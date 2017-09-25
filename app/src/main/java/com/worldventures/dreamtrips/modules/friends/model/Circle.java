package com.worldventures.dreamtrips.modules.friends.model;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Circle implements Serializable, Comparable<Circle> {

   public static Circle withTitle(String title) {
      Circle all = new Circle();
      all.name = title;
      return all;
   }

   private String id;
   private String name;

   public Circle() {
   }

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public void setId(String id) {
      this.id = id;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public String toString() {
      return name;
   }

   @Override
   public int compareTo(@NonNull Circle another) {
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
