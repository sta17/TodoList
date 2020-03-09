package no.steven.todolist.fragments

import android.content.Context
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_add_note.view.*
import no.steven.todolist.Note
import no.steven.todolist.R

class MakeNote : Fragment() {
    private lateinit var tempView: View
    private var mCallback: AddClicked? = null
    private var number = 0

    interface AddClicked {
        fun sendNote(note: Note?,edited: Boolean,number:Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(activity!!.applicationContext)
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        mCallback = try {
            activity as AddClicked
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString()
                        + " must implement TextClicked"
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { // Inflate the layout for this fragment
        tempView = inflater.inflate(R.layout.fragment_add_note, container, false)

        if((arguments != null) && (arguments!!.containsKey("edit")) ){
            tempView.noteTitle.text = SpannableStringBuilder(arguments!!.getString("noteTitle"))
            tempView.addNote.text = SpannableStringBuilder(arguments!!.getString("noteText"))
            tempView.addAdd.text = resources.getString(R.string.change)
            number = arguments!!.getInt("editNumber")
        }

        // perform setOnClickListener on second Button
        tempView.addAdd.setOnClickListener {
            //get note text
            val title = tempView.noteTitle.editableText.toString()
            val note = tempView.addNote.editableText.toString()
            if((arguments != null) && (arguments!!.containsKey("edit")) ){
                mCallback!!.sendNote(Note(note, title, false),true,number)
            }else {
                mCallback!!.sendNote(Note(note, title, false),false,number)
            }
            activity!!.supportFragmentManager.popBackStack()
        }
        tempView.addCancel.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }
        return tempView
    }

    override fun onDetach() {
        mCallback = null // => avoid leaking, thanks @Deepscorn
        super.onDetach()
    }
}