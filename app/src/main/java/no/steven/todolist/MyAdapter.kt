package no.steven.todolist

import android.app.AlertDialog
import android.content.Context
import android.database.DataSetObserver
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.noteview.view.*


class MyAdapter(var noteList: MutableList<Note>,private var context: Context) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(v: View, context: Context,noteList: MutableList<Note>) : RecyclerView.ViewHolder(v) {
        val textDisplay: Button
        private var image: ImageButton
        var pinState: Boolean = false
        var positionHolder = 0

        init {
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener { Log.d("test", "Element $adapterPosition clicked.") }
            textDisplay = v.findViewById(R.id.notebutton)
            image = v.findViewById(R.id.noteimage)
            v.notebutton.setOnClickListener {
                    var text = v.notebutton.text.toString()
                    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                    builder.setTitle("Edit Note")
                    val input = EditText(context)
                    input.inputType = InputType.TYPE_CLASS_TEXT
                    input.setText(text)
                builder.setView(input)
                    builder.setPositiveButton("Finish") { _, _ -> v.notebutton.text = input.text.toString(); noteList[positionHolder].content = input.text.toString() }
                    builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                    builder.show()
            }
            v.noteimage.setOnClickListener {
                if(pinState){
                    image.setImageDrawable(context.getDrawable(R.drawable.transparent))
                    image.setSelected(false);
                    pinState = false
                    noteList[positionHolder].selected = pinState
                } else {
                    image.setImageDrawable(context.getDrawable(R.drawable.pin))
                    image.setSelected(true);
                    pinState = true
                    noteList[positionHolder].selected = pinState
                }
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyAdapter.MyViewHolder {
        // create a new view
        val v = LayoutInflater.from(parent.context).inflate(R.layout.noteview, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(v,context,noteList)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textDisplay.text = noteList[position].content
        holder.positionHolder = position
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = noteList.size

    fun newList(List: MutableList<Note>) {
        noteList = List
        notifyItemRangeChanged(0, noteList.size);
    }
}
