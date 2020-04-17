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
import no.steven.todolist.fragments.Credit
import no.steven.todolist.fragments.MakeNote
import no.steven.todolist.fragments.NoteList
import java.io.File

// ========================================= Tutorials ========================================== //
// https://www.makeuseof.com/tag/beginner-programming-projects/ - tutorial is number 4, match features
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
// https://github.com/luizgrp/SectionedRecyclerViewAdapter
// https://stackoverflow.com/questions/28389052/how-can-i-implement-a-material-design-expand-collapse-list-on-android

//TODO: merge the two layout versions for the note and image into one.

class MainActivity : AppCompatActivity(), MakeNote.MakeNoteCommunication,NoteList.NoteListCommunication {

    private var noteList = mutableListOf<NoteNew>()
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

            val myFragment: NoteList = supportFragmentManager.findFragmentByTag("list") as NoteList
            var tempList = myFragment.getList()
            Log.d("delete","size:" + tempList.size + " list:" + tempList.toString())

            var deleteNumber = 0
            for (listPosition in 1..tempList.size) {
                if (tempList[listPosition-1].selected){
                    deleteNumber++
                }
            }

            while(deleteNumber != 0){
                tempList = deleteItem(tempList)
                Log.d("delete", deleteNumber.toString())
                Log.d("delete", tempList.toString())
                deleteNumber--
            }
            noteList = tempList
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

    private fun deleteItem(list: MutableList<NoteNew>): MutableList<NoteNew> {
        for (listPosition in 0..list.size) {
            if (list[listPosition].selected){
                list.removeAt(listPosition)
                val myFragment: NoteList = supportFragmentManager.findFragmentByTag("list") as NoteList
                myFragment.deleteItem(list,listPosition)
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
                    resources.getString(R.string.notes_not_found),
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

    override fun sendNote(note: NoteNew?,edited: Boolean,number: Int) {
        Log.d("noteList", "before$noteList")
        if(edited){
            noteList[number] = note!!
            val myFragment: NoteList = supportFragmentManager.findFragmentByTag("list") as NoteList
            myFragment.newList(noteList)
        } else {
            noteList.add(note!!)
        }
        Log.d("noteList", "after$noteList")
    }

    override fun startEditor(note: NoteNew?, position: Int) {
        val bundle = Bundle()
        bundle.putBoolean("edit",true)
        bundle.putString("noteTitle",note!!.title)
        bundle.putString("noteText", note.noteItemsList.toString())
        bundle.putInt("editNumber",position)

        val fragment = NoteList()
        fragment.arguments = bundle
        loadFragment(MakeNote(), bundle,"MakeNote")
    }

}
