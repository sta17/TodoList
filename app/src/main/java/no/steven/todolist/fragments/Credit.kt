package no.steven.todolist.fragments

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_credits.view.*
import no.steven.todolist.R

class Credit : Fragment() {
    private lateinit var listValue: Array<String>
    private lateinit var tempView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tempView = inflater.inflate(R.layout.fragment_credits, container, false)
        tempView.creditBackButton.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }

        // Function for assign string.xml file array to local array.
        listValue = resources.getStringArray(R.array.credits_list)
        val viewAdapter = CreditAdapter(listValue)

        tempView.creditRecyclerView.apply {
            this!!.setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = viewAdapter
        }

        // perform setOnClickListener on second Button
        tempView.creditBackButton.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }
        return tempView
    }

    class CreditAdapter(private val creditList: Array<String>) :
        RecyclerView.Adapter<CreditAdapter.MyViewHolder>() {

        class MyViewHolder(v: View) : RecyclerView.ViewHolder(v){
            val textView: TextView = v.findViewById(R.id.creditText)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.credit_list_item, parent, false))
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.textView.text = creditList[position]
            holder.textView.gravity = Gravity.CENTER
            holder.textView.textAlignment =  TEXT_ALIGNMENT_CENTER
        }

        override fun getItemCount() = creditList.size
    }

}
