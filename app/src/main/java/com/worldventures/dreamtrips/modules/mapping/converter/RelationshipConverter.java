package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.dreamtrips.api.session.model.Relationship;
import com.worldventures.dreamtrips.modules.common.model.User;

import io.techery.mappery.MapperyContext;

public class RelationshipConverter implements Converter<Relationship, User.Relationship> {
   @Override
   public Class<Relationship> sourceClass() {
      return Relationship.class;
   }

   @Override
   public Class<User.Relationship> targetClass() {
      return User.Relationship.class;
   }

   @Override
   public User.Relationship convert(MapperyContext mapperyContext, Relationship relationship) {
      if (relationship == null) return null;
      switch (relationship) {
         case FRIEND:
            return User.Relationship.FRIEND;
         case INCOMING_REQUEST:
            return User.Relationship.INCOMING_REQUEST;
         case OUTGOING_REQUEST:
            return User.Relationship.OUTGOING_REQUEST;
         case REJECTED:
            return User.Relationship.REJECTED;
         case NONE:
            return User.Relationship.NONE;
         default:
            throw new IllegalArgumentException("No match in relationship");
      }
   }
}