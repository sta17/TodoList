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
import androidx.core.view.get
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
class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: MyAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var noteList = mutableListOf<String>()
    private var noteListSelected = mutableListOf<Boolean>()
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
        viewAdapter = MyAdapter(noteList,noteListSelected,this)
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
            builder.setTitle("Write Note")
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)
            builder.setPositiveButton("Add") { _, _ -> noteList.add(input.text.toString());noteListSelected.add(false); viewAdapter.notifyDataSetChanged()  }
            builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            builder.show()
            true
        }
        R.id.action_delete -> {
            noteListSelected = viewAdapter.getSelected()
            var tempNoteList = noteList
            var tempNoteListSelected = noteListSelected
            for(i in 1 until noteListSelected.size){
                if(!noteListSelected[i]){
                    tempNoteList.removeAt(i)
                    tempNoteListSelected.removeAt(i)
                }
            }
            noteList = tempNoteList
            noteListSelected = tempNoteListSelected
            viewAdapter.notifyDataSetChanged()
            true
        }
        R.id.action_credit -> {
            val dDialog = AlertDialog.Builder(this, R.style.AppTheme_DialogTheme)
                .setTitle("Credits")
                .setMessage(
                    System.getProperty("line.separator") + System.getProperty("line.separator") + "Add button designed by Kiranshastry from www.Flaticon.com"
                            + System.getProperty("line.separator") + System.getProperty("line.separator") + "Delete button designed by Kiranshastry from www.Flaticon.com"
                            + System.getProperty("line.separator") + System.getProperty("line.separator") + "pin icon designed by Smashicons from www.Flaticon.com"
                            + System.getProperty("line.separator") + System.getProperty("line.separator") + "App by Steven Aanetsen."
                )
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(true)
                .setNegativeButton("Back") { dialog, _ -> dialog.cancel() }
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
        Toast.makeText(applicationContext, "Preferences Saved", Toast.LENGTH_SHORT).show()
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
                    "Comic list not found. No Comics in list.",
                    Toast.LENGTH_LONG
                ).show()
                Log.d("error", "ComicList file was not found.")
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
