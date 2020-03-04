package no.steven.todolist.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_add_note.view.*
import no.steven.todolist.MainActivity
import no.steven.todolist.Note
import no.steven.todolist.R

class MakeNote : Fragment() {
    private lateinit var tempView: View
    private var mCallback: AddClicked? = null

    interface AddClicked {
        fun sendNote(note: Note?)
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

        // perform setOnClickListener on second Button
        tempView.addAdd.setOnClickListener {
            //get note text
            val title = tempView.noteTitle.editableText.toString()
            val note = tempView.addNote.editableText.toString()
            // set up intent
            val intent = Intent(activity!!.baseContext, MainActivity::class.java)
            intent.putExtra("noteTitle",title)
            intent.putExtra("noteNote",note)
            mCallback!!.sendNote(Note(note, title, false))
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