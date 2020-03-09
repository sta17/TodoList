package no.steven.todolist.fragments

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_note_list.view.*
import kotlinx.android.synthetic.main.noteview.view.*
import no.steven.todolist.Note
import no.steven.todolist.R

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

        viewAdapter =
            NoteAdapter(noteList, this.context as Context)
        tempView.noteRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = viewAdapter
        }
        newList(noteList)
        return tempView
    }

    fun newList(List: MutableList<Note>){
        viewAdapter.noteList = List
        Log.d("noteList", "Changes:$noteList")
        viewAdapter.notifyItemRangeChanged(0, noteList.size)
        //viewAdapter.notifyDataSetChanged()
    }

    fun getList(): MutableList<Note> {
        return viewAdapter.noteList
    }

    fun deleteItem(List: MutableList<Note>,position: Int){
        viewAdapter.noteList = List
        Log.d("noteList", "Changes:$noteList")
        viewAdapter.notifyItemRemoved(position)
        viewAdapter.notifyItemRangeChanged(0, noteList.size)
        //viewAdapter.notifyDataSetChanged()
    }

    class NoteAdapter(var noteList: MutableList<Note>, private var context: Context) : RecyclerView.Adapter<NoteAdapter.MyViewHolder>() {

        class MyViewHolder(v: View, context: Context,noteList: MutableList<Note>) : RecyclerView.ViewHolder(v) {
            val textDisplay: Button
            val titleDisplay: Button
            private var pinState: Boolean = false
            var positionHolder = 0

            init {
                // Define click listener for the ViewHolder's View.
                v.setOnClickListener { Log.d("test", "Element $adapterPosition clicked.") }
                titleDisplay = v.findViewById(R.id.noteTitle)
                textDisplay = v.findViewById(R.id.noteContent)
                v.noteTitle.setOnClickListener {
                    changeDialog(v.noteTitle, positionHolder,context,noteList)
                }
                v.noteContent.setOnClickListener {
                    changeDialog(v.noteContent, positionHolder,context,noteList)
                }
                v.noteimage.setOnClickListener {
                    if(pinState){
                        v.noteimage.setImageDrawable(context.getDrawable(R.drawable.transparent))
                        v.noteimage.isSelected = false
                        pinState = false
                        noteList[positionHolder].selected = pinState
                    } else {
                        v.noteimage.setImageDrawable(context.getDrawable(R.drawable.pin))
                        v.noteimage.isSelected = true
                        pinState = true
                        noteList[positionHolder].selected = pinState
                    }
                }
            }

            private fun changeDialog(
                source: Button, positionHolder: Int, context: Context,
                noteList: MutableList<Note>) {
                var builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.setTitle("Edit Note")
                var input = EditText(context)
                input.inputType = InputType.TYPE_CLASS_TEXT
                input.setText(source.text.toString())
                var view = builder.setView(input)
                builder.setPositiveButton("Finish") { _, _ -> source.text = input.text.toString(); noteList[this.positionHolder].noteText = input.text.toString() }
                builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                builder.show()
            }
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.noteview, parent, false)
            return MyViewHolder(
                v,
                context,
                noteList
            )
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.titleDisplay.text = noteList[position].title
            holder.textDisplay.text = noteList[position].noteText
            holder.positionHolder = position
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = noteList.size
    }
}