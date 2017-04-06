package com.worldventures.dreamtrips.api.profile.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.Identifiable;
import com.worldventures.dreamtrips.api.session.model.Avatar;
import com.worldventures.dreamtrips.api.session.model.Subscription;

import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

public interface UserProfile extends Identifiable<Integer> {

    ///////////////////////////////////////////////////////////////////////////
    // Personal
    ///////////////////////////////////////////////////////////////////////////

    @SerializedName("username")
    String username();

    @SerializedName("email")
    String email();

    @Nullable
    @SerializedName("company")
    String company();

    @SerializedName("avatar")
    Avatar avatar();

    @SerializedName("first_name")
    String firstName();

    @SerializedName("last_name")
    String lastName();

    @SerializedName("location")
    @Nullable
    String location();

    @SerializedName("locale")
    String locale();

    @SerializedName("birth_date")
    @Nullable
    Date birthDate();

    @SerializedName("background_photo_url")
    @Nullable
    String backgroundPhotoUrl();

    @SerializedName("country_code")
    String countryCode();

    ///////////////////////////////////////////////////////////////////////////
    // DT-related
    ///////////////////////////////////////////////////////////////////////////

    @SerializedName("enroll_date")
    Date enrollDate();

    @SerializedName("terms_accepted")
    boolean termsAccepted();

    @SerializedName("subscriptions")
    List<Subscription> subscriptions();

    @SerializedName("badges")
    List<String> badges();

    @Nullable
    @SerializedName("sponsor_username")
    String sponsorUsername();

    @Nullable
    @SerializedName("rank_id")
    Integer rankId();
}
