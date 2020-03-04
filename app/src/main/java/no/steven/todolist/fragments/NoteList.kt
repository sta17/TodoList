package no.steven.todolist.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_note_list.view.*
import no.steven.todolist.Note
import no.steven.todolist.R

//TODO: Make the Fragment and list detect changes, and thus update.

class NoteList : Fragment() {
    private var noteList = mutableListOf<Note>()
    private lateinit var viewAdapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { // Inflate the layout for this fragment
        val tempView = inflater.inflate(R.layout.fragment_note_list, container, false)

        if((arguments != null) && (arguments!!.containsKey("note")) ){
            noteList = arguments!!.getParcelableArrayList<Note>("note")!!.toMutableList()
            Log.d("noteList", "Display:$noteList")
        }

        viewAdapter = NoteAdapter(noteList)
        tempView.noteRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = viewAdapter
        }
        viewAdapter.newList(noteList)
        return tempView
    }

    fun newList(List: MutableList<Note>){
        viewAdapter.newList(List)
    }

    class NoteAdapter(var noteList: MutableList<Note>) : RecyclerView.Adapter<NoteAdapter.MyViewHolder>() {
        class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val titleDisplay: TextView = v.findViewById(R.id.noteTitle)
            val noteDisplay: TextView = v.findViewById(R.id.noteContent)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.noteview, parent, false))
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.titleDisplay.text = noteList[position].title
            holder.noteDisplay.text = noteList[position].noteText
        }

        override fun getItemCount() = noteList.size

        fun newList(List: MutableList<Note>) {
            noteList = List
            Log.d("noteList", "Changes:$noteList")
            notifyItemRangeChanged(0, noteList.size)
            notifyDataSetChanged()
        }
    }
}