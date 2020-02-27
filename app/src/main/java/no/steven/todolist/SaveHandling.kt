package no.steven.todolist

import java.io.*

//list saving.
@Throws(IOException::class)
internal fun saveList(noteList: MutableList<Note>, filename: String, downloadLocation: File) {

    var temp: MutableList<String> = mutableListOf("temp")
    temp.remove("temp")

    for(item in  noteList){
        temp.add(item.content)
    }

    val text = temp.map { '"' + it + '"' }.toMutableList().toString()

    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(
            File(
                downloadLocation,
                filename
            )
        )

        //fos = context.openFileOutput(filename, MODE_PRIVATE)
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
internal fun loadList(filename: String, downloadLocation: File): MutableList<Note> {
    var fis: FileInputStream? = null

    try {
        fis = FileInputStream(
            File(
                downloadLocation,
                filename
            )
        )
        val input = BufferedReader(InputStreamReader(fis)).readText()
        val initialInput = input.removeSurrounding("[", "]")
        if (initialInput.isEmpty()) {
            return mutableListOf()
        }

        var initialList = initialInput.split(",").map { it.trim() }.map{ it.dropLast(1)}.map{ it.drop(1)}.map { it.trim() }.toMutableList()

        var temp: MutableList<Note> = mutableListOf(Note("temp",false))
        temp.removeAt(0)

        for(item in  initialList){
            temp.add(Note(item,false))
        }

        return temp
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