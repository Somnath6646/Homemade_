package com.wenull.homemade.utils.helper

import androidx.datastore.preferences.core.preferencesKey
import com.wenull.homemade.utils.model.User

class Constants {

    companion object {

        // Messages
        const val MESSAGE_SEND_OTP = "Send OTP"
        const val MESSAGE_VERIFY = "Verify"
        const val ENTER_PHONE_NUMBER = "Please enter a phone number"
        const val CODE_SENT = "OTP sent"
        const val SENDING_OTP = "Sending OTP..."
        const val ENTER_OTP = "Enter the OTP sent to your no."
        const val ENTER_ALL_CREDENTIALS = "Please fill all the details"
        const val TODAYS_MEAL = "Today's Meal"
        const val ALL_PACKS = "All Packs"
        const val AVAILABLE_FOODS = "Available Foods"
        const val AVAILABLE_PACKS = "Available Packs"

        // Values
        const val SUCCESSFUL = true
        const val FAILED = false
        const val FRAGMENT_PACK_ID = "packId"
        const val USER_DATA_EXISTS = true
        const val USER_DATA_DOES_NOT_EXIST = false
        const val ENROLLED = "Enrolled"
        const val ENROLL_NOW = "Enroll now"

        // Firebase reference values
        const val COLLECTION_USERS = "users"
        const val COLLECTION_FOOD_PACK = "packs"
        const val COLLECTION_FOODS = "foods"
        const val COLLECTION_SKIPPED = "skipped"
        const val IMAGES_USERS = "users"
        const val PACK_ = "pack_"
        const val FIELD_ID = "id"
        const val FIELD_NAME = "name"
        const val FIELD_DESCRIPTION = "description"
        const val FIELD_IMAGE_NAME = "imageName"
        const val FIELD_DAY = "day"
        const val FIELD_PRICE = "price"
        const val FIELD_PACK_ID = "packId"
        const val FIELD_UID = "uid"
        const val FIELD_SKIP_TME_LIMIT = "skipTimeLimit"
        const val FIELD_PHONE_NUMBER = "phoneNumber"
        const val FIELD_FIRST_NAME = "firstName"
        const val FIELD_LAST_NAME = "lastName"
        const val FIELD_ADDRESS = "address"
        const val FIELD_PACKS_ENROLLED = "packsEnrolled"
        const val FIELD_BUILDING_NAME_OR_NUMBER = "buildingNameOrNumber"
        const val FIELD_STREET_NAME = "streetName"
        const val FIELD_LOCALITY = "locality"
        const val FIELD_CITY = "city"
        const val FIELD_PINCODE = "pincode"
        const val FIELD_SKIPPED_MEALS = "skippedMeals"
        const val FIELD_DATE = "date"
        const val FIELD_FOOD_ID = "foodId"

        // Formats
        const val DAY_OF_WEEK = "EEEE"

        // Day value
        const val ONE_DAY_IN_MILLIS = 86400000L

        // Fragment constants
        const val BUNDLE_FOOD_PACK = "pack"
        const val BUNDLE_USER_SKIPPED_DATA = "userSkippedData"

        // Days of week
        const val SUNDAY = "Sunday"
        const val MONDAY = "Monday"
        const val TUESDAY = "Tuesday"
        const val WEDNESDAY = "Wednesday"
        const val THURSDAY = "Thursday"
        const val FRIDAY = "Friday"
        const val SATURDAY = "Saturday"

    }

}
