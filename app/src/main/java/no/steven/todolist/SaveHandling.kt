package no.steven.todolist

import android.util.JsonReader
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.*


//list saving.
@Throws(IOException::class)
internal fun saveList(noteList: MutableList<NoteNew>, filename: String, downloadLocation: File) {

    var jObject4 = JSONArray()
    for(item in  noteList){
        val jObject = JSONObject()
        jObject.put("title",item.title.trim())

        var jObject3 = JSONArray()
        for (item2 in item.noteItemsList){
            val jObject2 = JSONObject()
            jObject2.put("noteText",item2.noteText.trim())
            jObject2.put("image",item2.image)
            jObject3.put(jObject2)
        }
        jObject.put("noteList",jObject3)

        jObject4.put(jObject)
    }
    var text = jObject4.toString().trim()
    Log.d("saving", text)

    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(
            File(
                downloadLocation,
                filename
            )
        )

        fos.write(text.toByteArray())
        fos.close()
    } finally {
        if (fos != null) {
            try {
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}

//loads a list.
@Throws(IOException::class)
internal fun loadList(filename: String, downloadLocation: File): MutableList<NoteNew> {
    var fis: FileInputStream? = null

    try {
        fis = FileInputStream(
            File(
                downloadLocation,
                filename
            )
        )
        return readJsonStream(fis)
    } finally {
        if (fis != null) {
            try {
                fis.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}

@Throws(IOException::class)
private fun readJsonStream(`in`: InputStream?): MutableList<NoteNew> {
    val reader = JsonReader(InputStreamReader(`in`, "UTF-8"))
    return try {
        readNoteArray(reader)
    } finally {
        reader.close()
    }!!
}

@Throws(IOException::class)
private fun readNoteArray(reader: JsonReader): MutableList<NoteNew>? {
    val notes: MutableList<NoteNew> = ArrayList<NoteNew>()
    reader.beginArray()
    while (reader.hasNext()) {
        notes.add(readNotes(reader)!!)
    }
    reader.endArray()
    return notes
}

@Throws(IOException::class)
private fun readNotes(reader: JsonReader): NoteNew? {
    var title: String? = null
    var noteList: MutableList<NoteListItem>? = null
    reader.beginObject()
    while (reader.hasNext()) {
        val name = reader.nextName()
        if (name == "title") {
            title = reader.nextString()
        } else if (name == "noteList") {
            noteList = readNoteList(reader)
        } else {
            reader.skipValue()
        }
    }
    reader.endObject()
    return NoteNew(noteList!!, title.toString(),false)
}

@Throws(IOException::class)
private fun readNoteList(reader: JsonReader): MutableList<NoteListItem>? {
    val noteItems: MutableList<NoteListItem> = ArrayList<NoteListItem>()
    reader.beginArray()
    while (reader.hasNext()) {
        noteItems.add(readItems(reader)!!)
    }
    reader.endArray()
    return noteItems
}

@Throws(IOException::class)
private fun readItems(reader: JsonReader): NoteListItem? {
    var noteText: String? = null
    var image = false
    reader.beginObject()
    while (reader.hasNext()) {
        val name = reader.nextName()
        if (name == "noteText") {
            noteText = reader.nextString()
        } else if (name == "image") {
            image = reader.nextBoolean()
        } else {
            reader.skipValue()
        }
    }
    reader.endObject()
    return NoteListItem(noteText.toString(), image)
}