package no.steven.todolist

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Note(var noteText: String, var title: String, var selected: Boolean): Parcelable