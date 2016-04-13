package com.worldventures.dreamtrips.core.api;

import com.google.gson.JsonObject;
import com.messenger.api.ShortProfilesBody;
import com.messenger.api.TranslateTextBody;
import com.messenger.api.TranslatedText;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketBasePostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketOrderModel;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketStatusItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.Suggestion;
import com.worldventures.dreamtrips.modules.common.model.AvailableLocale;
import com.worldventures.dreamtrips.modules.common.model.DELETE_WITH_BODY;
import com.worldventures.dreamtrips.modules.common.model.Session;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.notification.PushSubscription;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackType;
import com.worldventures.dreamtrips.modules.membership.api.InviteBody;
import com.worldventures.dreamtrips.modules.membership.model.History;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;
import com.worldventures.dreamtrips.modules.reptools.model.VideoLocale;
import com.worldventures.dreamtrips.modules.settings.model.SettingsHolder;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;
import com.worldventures.dreamtrips.modules.trips.model.RegionModel;
import com.worldventures.dreamtrips.modules.trips.model.TripDetails;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.tripsimages.model.AddPhotoTag;
import com.worldventures.dreamtrips.modules.tripsimages.model.DeletePhotoTag;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;
import com.worldventures.dreamtrips.modules.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;
import com.worldventures.dreamtrips.modules.tripsimages.model.YSBHPhoto;
import com.worldventures.dreamtrips.modules.video.model.Category;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

import static com.worldventures.dreamtrips.modules.friends.api.ResponseBatchRequestCommand.RequestBody;
import static com.worldventures.dreamtrips.modules.infopages.api.SendFeedbackCommand.FeedbackBody;

public interface DreamTripsApi {

    String TYPE_MEMBER = "DTAPP";
    String TYPE_MEMBER_360 = "DTAPP360";
    String TYPE_REP = "dtapprep";

    @FormUrlEncoded
    @POST("/api/sessions")
    Session login(@Field("username") String username, @Field("password") String password);

    @POST("/api/profile/avatar")
    @Multipart
    User uploadAvatar(@Part("avatar") TypedFile image);

    @POST("/api/profile/background_photo")
    @Multipart
    User uploadBackgroundPhoto(@Part("background_photo") TypedFile image);

    @GET("/api/profile")
    User getProfile();

    @GET("/api/profiles/{id}")
    User getPublicProfile(@Path("id") int id);

    @POST("/api/users/profiles/short")
    ArrayList<User> getShortProfiles(@Body ShortProfilesBody body);

    @GET("/api/trips")
    List<TripModel> getTrips();

    @GET("/api/regions")
    List<RegionModel> getRegions();

    @GET("/api/activities")
    List<ActivityModel> getActivities();

    /* *** PHOTOS *****************************/

    /**
     * Photo of all members
     */
    @GET("/api/photos")
    ArrayList<Photo> getMembersPhotos(@Query("per_page") int perPage, @Query("page") int page);

    @GET("/api/users/{user_id}/photos")
    ArrayList<Photo> getUserPhotos(@Path("user_id") int userId, @Query("per_page") int query, @Query("page") int page);

    @GET("/api/inspirations?random_seed=1")
    ArrayList<Inspiration> getInspirationsPhotos(@Query("per_page") int perPage, @Query("page") int page, @Query("random_seed") double randomSeed);

    @GET("/api/ysbh_photos")
    ArrayList<YSBHPhoto> getYouShouldBeHerePhotos(@Query("per_page") int perPage, @Query("page") int page);


    @FormUrlEncoded
    @POST("/api/photos/{id}/flags")
    JsonObject flagPhoto(@Path("id") String photoId, @Field("reason") String nameOfReason);

    @DELETE("/api/photos/{id}")
    JsonObject deletePhoto(@Path("id") String photoId);

    @POST("/api/photos")
    Photo uploadTripPhoto(@Body UploadTask uploadTask);

    @PUT("/api/photos/{uid}")
    Photo editTripPhoto(@Path("uid") String uid, @Body UploadTask uploadTask);
    /* *** END PHOTOS *****************************/

    @GET("/api/success_stories")
    ArrayList<SuccessStory> getSuccessStores();

    @POST("/api/success_stories/{id}/like")
    JsonObject likeSS(@Path("id") int photoId);

    @DELETE("/api/success_stories/{id}/like")
    JsonObject unlikeSS(@Path("id") int photoId);

    @POST("/api/bucket_list_items/{uid}/photos")
    BucketPhoto uploadBucketPhoto(@Path("uid") String uid, @Body BucketPhoto bucketPhoto);

    @GET("/api/trips/{id}")
    TripDetails getDetails(@Path("id") String tripId);

    @POST("/api/bucket_list_items")
    BucketItem createItem(@Body BucketBasePostItem bucketItem);

    @PATCH("/api/bucket_list_items/{uid}")
    BucketItem completeItem(@Path("uid") String uid, @Body BucketStatusItem bucketPostItem);

    @PATCH("/api/bucket_list_items/{uid}")
    BucketItem updateItem(@Path("uid") String uid, @Body BucketBasePostItem bucketPostItem);

    @DELETE("/api/bucket_list_items/{uid}")
    JsonObject deleteItem(@Path("uid") String uid);

    @GET("/api/bucket_list_items")
    ArrayList<BucketItem> getBucketListFull();

    @GET("/api/users/{user_id}/bucket_list_items")
    ArrayList<BucketItem> getBucketListFull(@Path("user_id") int userId);

    @GET("/api/social/users/{user_id}/bucket_list_items")
    ArrayList<BucketItem> getBucketListFull(@Path("user_id") String userId);

    @DELETE("/api/bucket_list_items/{uid}/photos/{photo_id}")
    JsonObject deleteBucketPhoto(@Path("uid") String uid, @Path("photo_id") String photoId);

    @GET("/api/bucket_list/locations")
    ArrayList<PopularBucketItem> getPopularLocations();

    @GET("/api/bucket_list/activities")
    ArrayList<PopularBucketItem> getPopularActivities();

    @GET("/api/bucket_list/dinings")
    ArrayList<PopularBucketItem> getPopularDining();

    @PUT("/api/bucket_list_items/{uid}/position")
    JsonObject changeOrder(@Path("uid") String uid, @Body BucketOrderModel item);

    @GET("/api/categories")
    ArrayList<CategoryItem> getCategories();

    @GET("/api/location_suggestions")
    ArrayList<Suggestion> getLocationSuggestions(@Query("name") String name);

    @GET("/api/activity_suggestions")
    ArrayList<Suggestion> getActivitySuggestions(@Query("name") String name);

    @GET("/api/dining_suggestions")
    ArrayList<Suggestion> getDiningSuggestions(@Query("name") String name);

    @GET("/api/location_suggestions/popular")
    ArrayList<PopularBucketItem> getLocationPopularSuggestions(@Query("name") String name);

    @GET("/api/activity_suggestions/popular")
    ArrayList<PopularBucketItem> getActivityPopularSuggestions(@Query("name") String name);

    @GET("/api/dining_suggestions/popular")
    ArrayList<PopularBucketItem> getDiningPopularSuggestions(@Query("name") String name);

    @GET("/api/invitations/templates")
    ArrayList<InviteTemplate> getInviteTemplates();

    @GET("/api/invitations")
    ArrayList<History> getInvitations();

    @POST("/api/invitations")
    JSONObject sendInvitations(@Body InviteBody body);

    @FormUrlEncoded
    @POST("/api/invitations/templates/{id}")
    InviteTemplate getFilledInviteTemplate(@Path("id") int id, @Field("message") String message);

    @FormUrlEncoded
    @POST("/api/invitations/filled_templates")
    InviteTemplate createInviteTemplate(@Field("template_id") int id,
                                        @Field("message") String message,
                                        @Field("cover_photo_url") String photoUrl);

    @GET("/api/invitations/filled_templates/{id} ")
    InviteTemplate getFilledInviteTemplate(@Path("id") int id);

    @GET("/api/locales")
    ArrayList<AvailableLocale> getLocales();

    @GET("/api/member_videos/")
    ArrayList<Category> getVideos(@Query("type") String type);

    @GET("/api/member_videos/")
    ArrayList<Category> getVideos(@Query("type") String type, @Query("locale") String locale);


    @GET("/api/member_videos/locales")
    ArrayList<VideoLocale> getTrainingVideosLocales();

    @GET("/api/flag_reasons")
    ArrayList<Flag> getFlags();

    @GET("/api/social/circles")
    ArrayList<Circle> getCircles();

    @GET("/api/social/friends")
    ArrayList<User> getFriends(@Query("circle_id") String circle_id,
                               @Query("query") String query,
                               @Query("page") int page,
                               @Query("per_page") int perPage);

    @GET("/api/social/friends")
    ArrayList<User> getAllFriends(@Query("query") String query,
                                  @Query("page") int page,
                                  @Query("per_page") int perPage);

    @GET("/api/social/users")
    ArrayList<User> searchUsers(@Query("query") String query, @Query("page") int page, @Query("per_page") int perPage);

    @GET("/api/social/friends/requests")
    ArrayList<User> getRequests();

    @POST("/api/social/friends/requests")
    JSONObject addFriend(@Query("user_id") int userId,
                         @Query("circle_id") String circleId);

    @FormUrlEncoded
    @PUT("/api/social/friends/request_responses")
    JSONObject actOnRequest(@Query("user_id") int userId,
                            @Field("action") String action,
                            @Field("circle_id") String circleId);

    @PATCH("/api/social/friends/request_responses")
    ArrayList<JSONObject> actOnBatchRequests(@Body RequestBody userIds);

    @DELETE("/api/social/friends/request_responses")
    JSONObject deleteRequest(@Query("user_id") int userId);

    @DELETE("/api/social/friends/{user_id}")
    JSONObject unfriend(@Path("user_id") int userId);

    @GET("/api/social/users/{user_id}/timeline")
    ArrayList<ParentFeedItem> getUserTimeline(@Path("user_id") int userId, @Query("per_page") int perPage, @Query("before") String before);

    @GET("/api/social/timeline")
    ArrayList<ParentFeedItem> getAccountTimeline(@Query("per_page") int perPage, @Query("before") String before);

    @GET("/api/social/feed")
    ArrayList<ParentFeedItem> getAccountFeed(@Query("per_page") int perPage, @Query("before") String before, @Query("circle_id") String circleId);

    @GET("/api/{object_id}/comments")
    ArrayList<Comment> getComments(@Path("object_id") String objectId, @Query("per_page") int perPage, @Query("page") int page);

    @FormUrlEncoded
    @POST("/api/social/comments")
    Comment createComment(@Field("origin_id") String objectId, @Field("text") String text);

    @FormUrlEncoded
    @POST("/api/social/comments")
    Comment replyComment(@Field("reply_comment_id") String commentId, @Field("text") String text);

    @FormUrlEncoded
    @POST("/api/social/posts")
    TextualPost post(@Field("description") String description);

    @FormUrlEncoded
    @PUT("/api/social/posts/{uid}")
    TextualPost editPost(@Path("uid") String uid, @Field("description") String description);

    @DELETE("/api/social/posts/{uid}")
    Void deletePost(@Path("uid") String uid);

    @DELETE("/api/social/comments/{id}")
    JSONObject deleteComment(@Path("id") String commentId);

    @FormUrlEncoded
    @PUT("/api/social/comments/{id}")
    Comment editComment(@Path("id") String commentId, @Field("text") String text);

    @FormUrlEncoded
    @POST("/api/social/circles/{circle_id}/users")
    Void addToGroup(@Path("circle_id") String groupId, @Field("user_ids[]") List<String> userIds);

    @FormUrlEncoded
    @DELETE_WITH_BODY("/api/social/circles/{circle_id}/users")
    Void deleteFromGroup(@Path("circle_id") String groupId, @Field("user_ids[]") List<String> userIds);

    @POST("/api/{uid}/likes")
    Void likeEntity(@Path("uid") String uid);

    @DELETE("/api/{uid}/likes")
    Void dislikeEntity(@Path("uid") String uid);

    @GET("/api/{uid}/likes")
    ArrayList<User> getUsersWhoLikedEntity(@Path("uid") String uid, @Query("page") int page, @Query("per_page") int perPage);

    @GET("/api/social/notifications")
    ArrayList<ParentFeedItem> getNotifications(@Query("per_page") int perPage, @Query("before") String page);

    @GET("/api/{uid}")
    FeedEntityHolder getFeedEntity(@Path("uid") String uid);

    @PUT("/api/social/notifications")
    Void markAsRead(@Query("since") String since, @Query("before") String before);

    @PUT("/api/social/notifications/{id}")
    Void markAsRead(@Path("id") int id);

    @POST("/api/social/push_subscriptions")
    Void subscribeDevice(@Body PushSubscription pushSubscription);

    @DELETE("/api/social/push_subscriptions/{token}")
    Void unsubscribeDevice(@Path("token") String token);

    @FormUrlEncoded
    @POST("/api/{uid}/flags")
    Void flagItem(@Path("uid") String uid, @Field("flag_reason_id") int flagReasonId, @Field("reason") String nameOfReason);

    @FormUrlEncoded
    @POST("/api/terms_and_conditions/accept")
    Void acceptTermsConditions(@Field("text") String text);

    @DELETE("/api/sessions")
    Void logout();

    @GET("/api/social/friends/{userId}/mutual/")
    ArrayList<User> getMutualFriends(@Path("userId") int userId);

    @POST("/api/photos/{uid}/tags")
    ArrayList<PhotoTag> addPhotoTags(@Path("uid") String photoId, @Body AddPhotoTag addTag);

    @DELETE_WITH_BODY("/api/photos/{uid}/tags")
    Void deletePhotoTags(@Path("uid") String photoId, @Body DeletePhotoTag deleteTag);

    @GET("/api/photos/{uid}")
    Photo getPhotoInfo(@Path("uid") String uid);

    @GET("/api/user/settings")
    SettingsHolder getSettings();

    @PATCH("/api/user/settings")
    Void updateSettings(@Body SettingsHolder settingsHolder);

    @GET("/api/feedbacks/reasons")
    ArrayList<FeedbackType> getFeedbackReasons();

    @POST("/api/feedbacks")
    Void sendFeedback(@Body FeedbackBody feedbackBody);

    @POST("/api/translate")
    TranslatedText translateText(@Body TranslateTextBody body);

}
