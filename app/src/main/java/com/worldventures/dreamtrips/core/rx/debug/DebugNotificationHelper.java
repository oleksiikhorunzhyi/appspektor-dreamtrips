package com.worldventures.dreamtrips.core.rx.debug;


import rx.Observable;
import rx.Observer;
import rx.plugins.DebugNotification;

import static rx.plugins.DebugNotification.Kind;
import static rx.plugins.DebugNotification.quote;

public class DebugNotificationHelper {

   public static String toString(DebugNotification n) {
      StringBuilder s = new StringBuilder("{");
      s.append("\"observer\": ");
      //
      Observer observer = n.getObserver();
      Kind kind = n.getKind();
      Observable source = n.getSource();
      Observable.OnSubscribe sourceFunc = n.getSourceFunc();
      Observable.Operator from = n.getFrom();
      Observable.Operator to = n.getTo();
      //
      if (observer != null) s.append("\"")
            .append(observer.getClass().getName())
            .append("@")
            .append(Integer.toHexString(observer.hashCode()))
            .append("\"");
      else s.append("null");
      s.append(", \"type\": \"").append(kind).append("\"");
      if (kind == Kind.OnNext) s.append(", \"value\": ").append(quote(n.getValue()));
      if (kind == Kind.OnError) {
         Throwable throwable = n.getThrowable();
         s.append(", \"exception\": \"").append(throwable.getMessage() == null ? throwable : throwable.getMessage()
               .replace("\\", "\\\\")
               .replace("\"", "\\\"")).append("\"");
      }
      if (kind == Kind.Request) s.append(", \"n\": ").append(n.getN());
      if (source != null) s.append(", \"source\": \"")
            .append(source.getClass().getName())
            .append("@")
            .append(Integer.toHexString(source.hashCode()))
            .append("\"");
      if (sourceFunc != null) s.append(", \"sourceFunc\": \"")
            .append(sourceFunc.getClass().getName())
            .append("@")
            .append(Integer.toHexString(sourceFunc.hashCode()))
            .append("\"");
      if (from != null) s.append(", \"from\": \"")
            .append(from.getClass().getName())
            .append("@")
            .append(Integer.toHexString(from.hashCode()))
            .append("\"");
      if (to != null) s.append(", \"to\": \"").append(to.getClass().getName()).append("@").append(Integer.toHexString(to
            .hashCode())).append("\"");
      s.append("}");
      return s.toString();

   }
}
