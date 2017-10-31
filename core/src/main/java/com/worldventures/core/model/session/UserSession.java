package com.worldventures.core.model.session;

import com.worldventures.core.model.User;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@Gson.TypeAdapters
public interface UserSession {

   User user();

   String locale();

   String apiToken();

   String legacyApiToken();

   String username();

   String userPassword();

   List<Feature> permissions();

   Long lastUpdate();
}
