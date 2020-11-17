package com.example.weighttracker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TableLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.example.weighttracker.models.Progress
import kotlinx.android.synthetic.main.table_row.view.*
import java.text.DateFormat

class RecyclerViewAdapter: Adapter<RecyclerViewAdapter.AdapterViewHolder>(){
    private var progressList= emptyList<Progress>()

    class AdapterViewHolder(tableLayout: TableLayout): RecyclerView.ViewHolder(tableLayout){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val tableLayout = LayoutInflater.from(parent.context).inflate(R.layout.table_row, parent, false) as TableLayout
        return AdapterViewHolder(tableLayout)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        val currentItem = progressList[position]
        val formattedDate = DateFormat.getDateInstance().format(currentItem.date)
        holder.itemView.cell_date.text = formattedDate
        holder.itemView.cell_weight.text = currentItem.weight.toString()
    }

    override fun getItemCount(): Int {
        return progressList.count()
    }

    fun setData(progresses: List<Progress>){
        this.progressList = progresses
        notifyDataSetChanged()
    }
}