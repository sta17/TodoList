package no.steven.todolist

import android.content.Context
import android.util.Log
import android.widget.Toast
import java.io.*

//list saving.
@Throws(IOException::class)
internal fun saveList(noteList: List<String>, filename: String, downloadLocation: File) {

    val text = noteList.map { '"' + it + '"' }.toMutableList().toString()

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
internal fun loadList(filename: String, downloadLocation: File): MutableList<String> {
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

        return initialInput.split(",").map { it.trim() }.map{ it.dropLast(1)}.map{ it.drop(1)}.map { it.trim() }.toMutableList()
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