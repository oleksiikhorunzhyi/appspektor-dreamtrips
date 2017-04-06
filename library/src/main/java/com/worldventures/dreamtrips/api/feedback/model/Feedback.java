package com.worldventures.dreamtrips.api.feedback.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface Feedback {

    /**
     * @return reasonId
     * @see FeedbackReason
     */
    @SerializedName("reason_id")
    int reasonId();
    @SerializedName("text")
    String text();
    @SerializedName("metadata")
    Metadata metadata();
    @Nullable
    @SerializedName("smartcard_metadata")
    SmartCardMetadata smartCardMetadata();
    @Nullable
    @SerializedName("attachments")
    List<FeedbackAttachment> attachments();

    @Gson.TypeAdapters
    @Value.Immutable
    interface Metadata {
        /**
         * Example: iPhone/iPad - 4.1, 4.5, etc.
         * device manufacturer:device model
         *
         * @return deviceModel
         */
        @SerializedName("device_model")
        String deviceModel();
        /**
         * Example: 9.1.2
         * android-17
         *
         * @return osVersion
         */
        @SerializedName("os_version")
        String osVersion();
        /**
         * Example: 1.11.0
         *
         * @return appVersion
         */
        @SerializedName("app_version")
        String appVersion();

        /**
         * Phone/Tablet
         *
         * @return deviceType
         */
        @SerializedName("device_type")
        DeviceType deviceType();
    }

    @Gson.TypeAdapters
    @Value.Immutable
    interface SmartCardMetadata {
        /**
         * SmartCard BLE address
         *
         * @return bleID
         */
        @SerializedName("ble_id")
        String bleId();

        /**
         * Smarcard serial number
         *
         * @return smarcardSerialNumber
         */
        @SerializedName("smartcard_serial_number")
        String smartCardSerialNumber();

        /**
         * Device Smarcard id
         *
         * @return smarcardId
         */
        @SerializedName("smartcard_id")
        int smartCardId();

        /**
         * Version of smartcard firmware
         *
         * @return firmwareVersion
         */
        @SerializedName("firmware_version")
        String firmwareVersion();

        /**
         * Version of smartcard SDK, stored in app config
         *
         * @return sdkVersion
         */
        @SerializedName("sdk_version")
        String sdkVersion();
    }

    enum DeviceType {
        @SerializedName("Phone")PHONE,
        @SerializedName("Tablet")TABLET
    }
}
