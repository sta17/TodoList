package no.steven.todolist

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File


// https://www.makeuseof.com/tag/beginner-programming-projects/

// save list.
// create object, write in text.
// load list.
// display list.

// https://www.flaticon.com/free-icon/delete_1214428?term=garbage&page=1&position=4 - delete icon
// https://www.flaticon.com/free-icon/pen_1159725 - add icon
// https://www.flaticon.com/free-icon/pin_2491655?term=pin&page=1&position=20 - pin Icon

data class Note (
    var content: String,
    var selected: Boolean
)

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: MyAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var noteList = mutableListOf<Note>()
    private lateinit var downloadLocation: File
    private var sharedPrefs = "Steven's Notebook App"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        downloadLocation = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!

        loadPrefs(sharedPrefs) // get the preferences

        viewManager = LinearLayoutManager(this)
        viewAdapter = MyAdapter(noteList,this)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }
    }

    //setting menu in action bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // actions on click menu items
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_add -> {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle(resources.getString(R.string.writenote))
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)
            builder.setPositiveButton(resources.getString(R.string.add)) { _, _ -> noteList.add(Note(input.text.toString(),false)); viewAdapter.notifyDataSetChanged()  }
            builder.setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
            builder.show()
            true
        }
        R.id.action_delete -> {
            // https://stackoverflow.com/questions/31367599/how-to-update-recyclerview-adapter-data
            Log.d("delete", noteList.toString())
            var deleteNumber = 0
            for (listPosition in 1..noteList.size) {
                if (noteList[listPosition-1].selected){
                    deleteNumber++
                }
            }
            var temp = noteList.toMutableList()
            while(deleteNumber != 0){
                temp = deleteItem(temp)
                Log.d("delete", deleteNumber.toString())
                Log.d("delete", temp.toString())
                deleteNumber--
            }
            noteList = temp
            Log.d("delete", noteList.toString())
            true
        }
        R.id.action_credit -> {
            val dDialog = AlertDialog.Builder(this, R.style.AppTheme_DialogTheme)
                .setTitle(resources.getString(R.string.credit))
                .setMessage(
                    System.getProperty("line.separator") + System.getProperty("line.separator") + resources.getString(R.string.addbuttoncredit)
                            + System.getProperty("line.separator") + System.getProperty("line.separator") + resources.getString(R.string.deletebuttoncredit)
                            + System.getProperty("line.separator") + System.getProperty("line.separator") + resources.getString(R.string.pinbuttoncredit)
                            + System.getProperty("line.separator") + System.getProperty("line.separator") + resources.getString(R.string.byme)
                )
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(true)
                .setNegativeButton(resources.getString(R.string.back)) { dialog, _ -> dialog.cancel() }
                .create()
            dDialog.show()
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun deleteItem(list: MutableList<Note>): MutableList<Note> {
        for (listPosition in 0..list.size) {
            if (list[listPosition].selected){
                list.removeAt(listPosition)
                viewAdapter.newList(list)
                viewAdapter.notifyItemRemoved(listPosition)
                return list
            }
        }
        return list
    }

    override fun onDestroy() {
        super.onDestroy()
        saveState(sharedPrefs)
    }

    override fun onStop() {
        super.onStop()
        saveState(sharedPrefs)
    }

    /*
save preferences, comicList and favourites
 */
    private fun saveState(sharedPrefs: String) {
        val prefs = getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean("initialized", true)
        editor.apply()
        Toast.makeText(applicationContext, resources.getString(R.string.preferences_saved), Toast.LENGTH_SHORT).show()
        saveList(noteList, "noteList.json",downloadLocation)
    }

    /*
        load preferences, comicList and favourites
         */
    private fun loadPrefs(sharedPrefs: String) {
        val prefs = getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE)
        if ((prefs.contains("initialized")) && (prefs.getBoolean("initialized", false))) {

            if(File(downloadLocation, "noteList.json").exists()){
                noteList = loadList("noteList.json",downloadLocation)
            }else{
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.notesnotfound),
                    Toast.LENGTH_LONG
                ).show()
                Log.d("error", "NoteList file was not found.")
            }

            Toast.makeText(
                applicationContext,
                resources.getString(R.string.preferences_loaded),
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                applicationContext,
                resources.getString(R.string.new_app_set_up),
                Toast.LENGTH_LONG
            ).show()
        }
    }

}
