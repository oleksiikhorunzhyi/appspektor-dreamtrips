package com.worldventures.core.model.session;

import com.worldventures.core.model.User;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

import javax.annotation.Nullable;

@Value.Immutable
@Gson.TypeAdapters
public interface UserSession {

   @Nullable User user();

   @Nullable String locale();

   @Nullable String apiToken();

   @Nullable String legacyApiToken();

   @Nullable String userPassword();

   @Nullable String username();

   @Nullable List<Feature> permissions();

   @Nullable Long lastUpdate();
}
