package com.example.itschool.model

import com.google.firebase.Timestamp

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

data class GrouproomModel(
    var grouproomId: String? = null,
    var userIds: ArrayList<String>? = arrayListOf(),
    var lastMessageTimestamp: Timestamp? = null,
    var lastMessageSenderId: String? = null,
    var lastMessage: String? = null,
    var classId: String? = null,
    var nomGroup: String? = null,
    var createdBy: String? = null,
    var description: String? = null,
    var profCreated: Boolean? = false,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.createStringArrayList(), // Read list of user IDs
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as Boolean?
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(grouproomId)
        parcel.writeStringList(userIds) // Write list of user IDs
        parcel.writeParcelable(lastMessageTimestamp, flags)
        parcel.writeString(lastMessageSenderId)
        parcel.writeString(lastMessage)
        parcel.writeString(classId)
        parcel.writeString(nomGroup)
        parcel.writeString(createdBy)
        parcel.writeString(description)
        parcel.writeValue(profCreated)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<GrouproomModel> {
        override fun createFromParcel(parcel: Parcel): GrouproomModel {
            return GrouproomModel(parcel)
        }

        override fun newArray(size: Int): Array<GrouproomModel?> {
            return arrayOfNulls(size)
        }
    }
}

