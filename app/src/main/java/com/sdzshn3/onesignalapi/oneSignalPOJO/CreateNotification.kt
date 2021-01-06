package com.sdzshn3.onesignalapi.oneSignalPOJO

import com.google.gson.annotations.SerializedName

@Suppress("ArrayInDataClass")
data class CreateNotification(
    /**
     * App
     */
    @SerializedName("app_id") var appId: String? = null,

    /**
     * Send to Users Based on Filters
     */
    @SerializedName("filters") var filters: List<Filter>? = null,

    /**
     * Idempotency
     */
    @SerializedName("external_id") var externalId: String? = null,

    /**
     * Notification Content
     */
    @SerializedName("contents") var content: Content? = null,
    @SerializedName("headings") var heading: Heading? = null,
    @SerializedName("subtitle") var subtitle: Subtitle? = null,
    @SerializedName("template_id") var templateId: String? = null,
    @SerializedName("content_available") var contentAvailable: Boolean? = null,
    @SerializedName("mutable_content") var mutableContent: Boolean? = null,

    /**
     * Send to Segments
     */
    @SerializedName("included_segments") var includedSegments: Array<String?>? = null,
    @SerializedName("excluded_segments") var excludedSegments: Array<String?>? = null,

    /**
     * Send to Specific Devices
     */
    @SerializedName("include_player_ids") var includePlayerIds: Array<String>? = null,
    @SerializedName("include_external_user_ids") var includeExternalUserIds: Array<String>? = null,
    @SerializedName("include_email_tokens") var includeEmailTokens: Array<String>? = null,
    @SerializedName("include_ios_tokens") var includeIosTokens: Array<String>? = null,
    @SerializedName("include_wp_wns_uris") var includeWpWnsUris: Array<String>? = null,
    @SerializedName("include_amazon_reg_ids") var includeAmazonRegIds: Array<String>? = null,
    @SerializedName("include_chrome_reg_ids") var includeChromeRegIds: Array<String>? = null,
    @SerializedName("include_chrome_web_reg_ids") var includeChromeWebRegIds: Array<String>? = null,
    @SerializedName("include_android_reg_ids") var includeAndroidRegIds: Array<String>? = null,

    /**
     * Email Content
     */
    @SerializedName("email_subject") var emailSubject: String? = null,
    @SerializedName("email_body") var emailBody: String? = null,
    @SerializedName("email_from_name") var emailFromName: String? = null,
    @SerializedName("email_from_address") var emailFromAddress: String? = null,

    /**
     * Attachments
     */
    // data will be a custom map which will have dynamic key names.
    // So, it needs to be added manually
    @SerializedName("url") var url: String? = null,
    @SerializedName("web_url") var webUrl: String? = null,
    @SerializedName("app_url") var appUrl: String? = null,
    // ios_attachments will be a custom map which will have dynamic key names
    // So, it needs to be added manually
    @SerializedName("big_picture") var bigPicture: String? = null,
    @SerializedName("adm_big_picture") var admBigPicture: String? = null,
    @SerializedName("chrome_big_picture") var chromeBigPicture: String? = null,

    /**
     * Action Buttons
     */
    @SerializedName("buttons") var buttons: List<Button>? = null,
    @SerializedName("web_buttons") var webButtons: List<WebButton>? = null,
    @SerializedName("ios_category") var iosCategory: String? = null,

    /**
     * Appearance
     */
    @SerializedName("android_channel_id") var androidChannelId: String? = null,
    @SerializedName("existing_android_channel_id") var existingAndroidChannelId: String? = null,
    @SerializedName("android_background_layout") var androidBackgroundLayout: AndroidBackgroundLayout? = null,
    @SerializedName("small_icon") var smallIcon: String? = null,
    @SerializedName("large_icon") var largeIcon: String? = null,
    @SerializedName("adm_small_icon") var admSmallIcon: String? = null,
    @SerializedName("adm_large_icon") var admLargeIcon: String? = null,
    @SerializedName("chrome_web_icon") var chromeWebIcon: String? = null,
    @SerializedName("chrome_web_image") var chromeWebImage: String? = null,
    @SerializedName("chrome_web_badge") var chromeWebBadge: String? = null,
    @SerializedName("firefox_icon") var firefoxIcon: String? = null,
    @SerializedName("chrome_icon") var chromeIcon: String? = null,
    @SerializedName("ios_sound") var iosSound: String? = null,
    @SerializedName("android_sound") var androidSound: String? = null,
    @SerializedName("adm_sound") var admSound: String? = null,
    @SerializedName("wp_wns_sound") var wpWnsSound: String? = null,
    @SerializedName("android_led_color") var androidLedColor: String? = null,
    @SerializedName("android_accent_color") var androidAccentColor: String? = null,
    @SerializedName("android_visibility") var androidLockScreenVisibility: Int? = null,
    @SerializedName("ios_badgeType") var iosBadgeType: String? = null,
    @SerializedName("ios_badgeCount") var iosBadgeCount: Int? = null,
    @SerializedName("collapse_id") var collapseId: String? = null,
    @SerializedName("apns_alert") var apnsAlert: ApnsAlert? = null,

    /**
     * Delivery
     */
    @SerializedName("send_after") var sendAfter: String? = null,
    @SerializedName("delayed_option") var delayedOptions: String? = null,
    @SerializedName("delivery_time_of_day") var deliveryTimeOfDay: String? = null,
    @SerializedName("ttl") var timeToLive: Int? = null,
    @SerializedName("priority") var priority: Int? = null,
    @SerializedName("apns_push_type_override") var apnsPushTypeOverride: String? = null,

    /**
     * Grouping & Collapsing
     */
    @SerializedName("android_group") var androidGroup: String? = null,
    @SerializedName("android_group_message") var androidGroupMessage: AndroidGroupMessage? = null,
    @SerializedName("adm_group") var admGroup: String? = null,
    @SerializedName("adm_group_message") var admGroupMessage: AdmGroupMessage? = null,
    @SerializedName("thread_id") var threadId: String? = null,
    @SerializedName("summary_arg") var summaryArg: String? = null,
    @SerializedName("summary_arg_count") var summaryArgCount: Int? = null,

    /**
     * Platform to Deliver To
     */
    @SerializedName("isIos") var isIos: Boolean? = null,
    @SerializedName("isAndroid") var isAndroid: Boolean? = null,
    @SerializedName("isAnyWeb") var isAnyWeb: Boolean? = null,
    @SerializedName("isEmail") var isEmail: Boolean? = null,
    @SerializedName("isChromeWeb") var isChromeWeb: Boolean? = null,
    @SerializedName("isFirefox") var isFirefox: Boolean? = null,
    @SerializedName("isSafari") var isSafari: Boolean? = null,
    @SerializedName("isWP_WNS") var isWpWns: Boolean? = null,
    @SerializedName("isAdm") var isAdm: Boolean? = null,
    @SerializedName("isChrome") var isChrome: Boolean? = null,
    @SerializedName("channel_for_external_user_ids") var channelForExternalUserIds: String? = null
)