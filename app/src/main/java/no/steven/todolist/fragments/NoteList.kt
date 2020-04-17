package no.steven.todolist.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_note_list.view.*
import kotlinx.android.synthetic.main.note_list_item.view.*
import no.steven.todolist.NoteNew
import no.steven.todolist.R

class NoteList : Fragment() {
    private var noteList = mutableListOf<NoteNew>()
    private lateinit var viewAdapter: NoteAdapter
    private var mCallback: NoteListCommunication? = null

    interface NoteListCommunication {
        fun startEditor(note: NoteNew?,position: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(activity!!.applicationContext)
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        mCallback = try {
            activity as NoteListCommunication
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString()
                        + " must implement launchAddEdit"
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { // Inflate the layout for this fragment
        val tempView = inflater.inflate(R.layout.fragment_note_list, container, false)

        if((arguments != null) && (arguments!!.containsKey("note")) ){
            noteList = arguments!!.getParcelableArrayList<NoteNew>("note")!!.toMutableList()
            Log.d("noteList", "Display:$noteList")
        }

        viewAdapter =
            NoteAdapter(noteList, this.context as Context, mCallback)
        tempView.noteRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = viewAdapter
        }
        newList(noteList)
        return tempView
    }

    fun newList(List: MutableList<NoteNew>){
        viewAdapter.noteList = List
        Log.d("noteList", "Changes:$noteList")
        viewAdapter.notifyItemRangeChanged(0, noteList.size)
    }

    fun getList(): MutableList<NoteNew> {
        return viewAdapter.noteList
    }

    fun deleteItem(List: MutableList<NoteNew>,position: Int){
        viewAdapter.noteList = List
        Log.d("noteList", "Changes:$noteList")
        viewAdapter.notifyItemRemoved(position)
        viewAdapter.notifyItemRangeChanged(0, noteList.size)
        //viewAdapter.notifyDataSetChanged()
    }

    override fun onDetach() {
        mCallback = null // => avoid leaking, thanks @Deepscorn
        super.onDetach()
    }

    class NoteAdapter(
        var noteList: MutableList<NoteNew>,
        private var context: Context,
        private var mCallback: NoteListCommunication?
    ) : RecyclerView.Adapter<NoteAdapter.MyViewHolder>() {

        class MyViewHolder(
            v: View,
            context: Context,
            noteList: MutableList<NoteNew>,
            private var mCallback: NoteListCommunication?
        ) : RecyclerView.ViewHolder(v) {
            val titleDisplay: Button
            private var pinState: Boolean = false
            var positionHolder = 0

            init {
                // Define click listener for the ViewHolder's View.
                v.setOnClickListener { Log.d("test", "Element $adapterPosition clicked.") }
                titleDisplay = v.findViewById(R.id.noteTitle)
                v.noteTitle.setOnClickListener {
                    mCallback!!.startEditor(noteList[positionHolder],positionHolder)
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
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.note_list_item, parent, false)
            return MyViewHolder(
                v,
                context,
                noteList,
                mCallback
            )
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.titleDisplay.text = noteList[position].title
            holder.positionHolder = position
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = noteList.size
    }
}