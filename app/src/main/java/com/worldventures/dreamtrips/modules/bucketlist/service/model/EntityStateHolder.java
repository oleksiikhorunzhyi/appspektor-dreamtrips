package com.worldventures.dreamtrips.modules.bucketlist.service.model;

public final class EntityStateHolder<T> {
   private T entity;

   private State state;

   public static <T> EntityStateHolder<T> create(T entity, State state) {
      return new EntityStateHolder<>(entity, state);
   }

   private EntityStateHolder(T entity, State state) {
      this.entity = entity;
      this.state = state;
   }

   public void setState(State state) {
      this.state = state;
   }

   public T entity() {
      return entity;
   }

   public void setEntity(T entity) {
      this.entity = entity;
   }

   public State state() {
      return state;
   }

   public enum State {
      PROGRESS, DONE, FAIL
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      EntityStateHolder<?> that = (EntityStateHolder<?>) o;

      if (entity != null ? !entity.equals(that.entity) : that.entity != null) return false;
      return state == that.state;

   }

   @Override
   public int hashCode() {
      int result = entity != null ? entity.hashCode() : 0;
      result = 31 * result + (state != null ? state.hashCode() : 0);
      return result;
   }
}