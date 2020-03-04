package no.steven.todolist

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import no.steven.todolist.fragments.Credit
import no.steven.todolist.fragments.MakeNote
import no.steven.todolist.fragments.NoteList
import java.io.File

// ========================================= Tutorials ========================================== //
// https://www.makeuseof.com/tag/beginner-programming-projects/
//
// =========================================== Icons ============================================ //
// https://www.flaticon.com/free-icon/delete_1214428?term=garbage&page=1&position=4 - delete icon
// https://www.flaticon.com/free-icon/pen_1159725 - add icon
// https://www.flaticon.com/free-icon/pin_2491655?term=pin&page=1&position=20 - pin Icon
//
// https://www.flaticon.com/free-icon/folder_149334 - Folder Icon
// https://www.flaticon.com/free-icon/camera_883787?term=camera&page=1&position=12 - Camera Icon
// https://www.flaticon.com/free-icon/down-arrow_271210 - down/expand icon
// https://www.flaticon.com/packs/pointers-3 - up/minimise icon
//
// ====================================== Generating lists ====================================== //
// https://abhiandroid.com/ui/expandablelistview
// https://github.com/davideas/FlexibleAdapter
//
// ================================ Expandable recyclerListView ================================= //
// https://blog.usejournal.com/multi-level-expandable-recycler-view-e75cf1f4ac4b
// https://acadgild.com/blog/expandable-recyclerview-in-android-with-examples

class MainActivity : AppCompatActivity(), MakeNote.AddClicked {

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
        recyclerView = findViewById<RecyclerView>(R.id.noteRecyclerView).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        val bundle = Bundle()
        bundle.putParcelableArrayList("note",ArrayList(noteList.toList()))
        val fragment = NoteList()
        fragment.arguments = bundle
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.frameLayout, fragment,"list")
        fragmentTransaction.commit()

        supportFragmentManager.addOnBackStackChangedListener {
            val myFragment: NoteList = supportFragmentManager.findFragmentByTag("list") as NoteList
            myFragment.newList(noteList)
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
            loadFragment(MakeNote(), Bundle(),"MakeNote")
            true
        }
        R.id.action_delete -> {
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
            loadFragment(Credit(),Bundle(),"Credit")
            true
        }
        else -> {
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

    private fun loadFragment(fragment: Fragment, bundle: Bundle, Name: String) {
        fragment.arguments = bundle
        var manager = supportFragmentManager.beginTransaction()
        manager.replace(R.id.frameLayout, fragment)
        manager.addToBackStack(Name)
        manager.commit()
    }

    override fun sendNote(note: Note?) {
        Log.d("noteList", "before$noteList")
        noteList.add(note!!)
        Log.d("noteList", "after$noteList")
    }

}
