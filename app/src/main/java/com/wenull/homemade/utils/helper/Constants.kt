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

        // Values
        const val SUCCESSFUL = true
        const val FAILED = false
        const val FRAGMENT_PACK_ID = "packId"
        const val USER_DATA_EXISTS = true
        const val USER_DATA_DOES_NOT_EXIST = false

        // Firebase reference values
        const val COLLECTION_USERS = "users"
        const val IMAGES_USERS = "users"
        const val FOOD_PACK = "packs"
        const val FOODS = "foods"
        const val PACK_ = "pack_"
        const val FIELD_ID = "id"
        const val FIELD_NAME = "name"
        const val FIELD_DESCRIPTION = "description"
        const val FIELD_IMAGE_NAME = "imageName"
        const val FIELD_DAY = "day"
        const val FIELD_PRICE = "price"
        const val FIELD_PACK_ID = "packId"

    }

}
