package no.steven.todolist.fragments

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_note_list.view.*
import kotlinx.android.synthetic.main.note_list_item.view.*
import no.steven.todolist.CompoundView
import no.steven.todolist.NoteListItem
import no.steven.todolist.NoteNew
import no.steven.todolist.R
import java.io.File


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

        viewAdapter = NoteAdapter(noteList, this.context as Context, mCallback)
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
        Log.d("noteList", "Changes New List:$noteList")
        viewAdapter.notifyItemRangeChanged(0, noteList.size)
    }

    fun getList(): MutableList<NoteNew> {
        return viewAdapter.noteList
    }

    fun deleteItem(List: MutableList<NoteNew>,position: Int){
        viewAdapter.noteList = List
        Log.d("noteList", "Changes Delete Item:$noteList")
        viewAdapter.notifyItemRemoved(position)
        viewAdapter.notifyItemRangeChanged(0, noteList.size)
        //viewAdapter.notifyDataSetChanged()
    }

    override fun onDetach() {
        mCallback = null // => avoid leaking, thanks @Deepscorn
        super.onDetach()
    }

    internal class RecyclerViewAdapter(
        var noteList: MutableList<NoteListItem>,
        var context: Context) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerView.ViewHolder {
            val compoundView = CompoundView(context)
            for (i in 0 until noteList.size) {

                // construct TextView or ImageView or whatever
                    when (noteList[i].isImage) {
                        true -> {
                            val imageView = ImageView(context)
                            compoundView.addView(imageView)
                        }
                        false -> {
                            val textView = TextView(context)
                            compoundView.addView(textView)
                        }
                    }
            }
            return MyViewHolder(compoundView)
        }

        override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
            val compoundView: CompoundView = viewHolder.itemView as CompoundView
            val dataPiece: MutableList<NoteListItem> = noteList

            for (j in 0 until compoundView.childCount) {
                var tempView = compoundView.getChildAt(j)
                if(tempView is ImageView){
                    tempView = tempView as ImageView
                    val imgFile = File(dataPiece[j].noteText)

                    if (imgFile.exists()) {
                        val myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath())
                        tempView.setImageBitmap(myBitmap)
                    }
                } else{
                    tempView = tempView as TextView
                    tempView.text = dataPiece[j].noteText
                }
                compoundView.removeViewAt(j)
                compoundView.addView(tempView,j)
            }

        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

        override fun getItemCount(): Int {
            return noteList.size
        }

        inner class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!)

    }

    internal class NoteAdapter(
        var noteList: MutableList<NoteNew>,
        private var context: Context,
        private var mCallback: NoteListCommunication?
    ) : RecyclerView.Adapter<NoteAdapter.MyViewHolder>() {

        private val viewPool = RecyclerView.RecycledViewPool()

        inner class MyViewHolder(
            v: View,
            context: Context,
            noteList: MutableList<NoteNew>,
            private var mCallback: NoteListCommunication?
        ) : RecyclerView.ViewHolder(v) {
            val titleDisplay: Button
            private var pinState: Boolean = false
            var positionHolder = 0
            val recyclerView : RecyclerView = itemView.noteItemReyclerView

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
            holder.positionHolder = position
            holder.titleDisplay.text = noteList[position].title

            val childLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

            holder.recyclerView.apply {
                layoutManager = childLayoutManager
                //adapter = NoteItemAdapter(noteList[position].noteItemsList,context)
                adapter = RecyclerViewAdapter(noteList[position].noteItemsList,context)
                setRecycledViewPool(viewPool)
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = noteList.size
    }

    internal class NoteItemAdapter(
        private var noteItemList: MutableList<NoteListItem>,
        private val context: Context
    ) : RecyclerView.Adapter<NoteItemAdapter.MyViewHolder>() {

        inner class MyViewHolder(
            v: View,
            context: Context
        ) : RecyclerView.ViewHolder(v) {
            val itemFrame: FrameLayout
            lateinit var itemBase: LinearLayout
            var isImage: Boolean
            var positionHolder = 0

            init {
                // Define click listener for the ViewHolder's View.
                v.setOnClickListener { Log.d("test", "Element $adapterPosition clicked.") }
                itemFrame = v.findViewById(R.id.item_frame)
                itemBase = v.findViewById(R.id.item_base)
                isImage = false

            }
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.note_innerlist_item, parent, false)
            return MyViewHolder(
                v,
                context
            )
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.positionHolder = position
            holder.isImage = noteItemList[position].isImage
            if(holder.isImage){
                //val imageButton: ImageButton
                //holder.item_frame.addView(imageButton)
                val textView = TextView(context)
                textView.text = noteItemList[position].noteText // Temporary
                holder.itemBase.addView(textView)
            }
            else{
                val textView = TextView(context)
                textView.text = noteItemList[position].noteText
                holder.itemBase.addView(textView)
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = noteItemList.size
    }

}