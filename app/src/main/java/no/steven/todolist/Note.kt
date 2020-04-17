package no.steven.todolist

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Note(var noteText: String, var title: String, var selected: Boolean): Parcelable

@Parcelize
data class NoteNew(var noteItemsList: MutableList<NoteListItem>, var title: String, var selected: Boolean): Parcelable

@Parcelize
data class NoteListItem(var noteText: String, var image: Boolean): Parcelable