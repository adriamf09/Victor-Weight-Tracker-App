package com.example.weighttracker.fragments

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weighttracker.FirestoreService
import com.example.weighttracker.IResult
import com.example.weighttracker.R
import com.example.weighttracker.RecyclerViewAdapter
import com.example.weighttracker.models.Progress
import com.example.weighttracker.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_progress.*
import java.lang.Exception
import java.util.*

class ProgressFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        super.onViewCreated(view, savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_progress, container, false)

        val firestoreService = FirestoreService()

        firestoreService.getUserById(
            FirebaseAuth.getInstance().currentUser!!.uid,
            object : IResult<User> {
                override fun onSuccess(items: User?) {
                    txt_startWeight.text = items?.startWeight.toString()
                    txt_goalWeight.text = items?.goalWeight.toString()

                    val remaining = String.format("%.2f", items?.startWeight?.minus(items.goalWeight!!))
                    txt_remainingWeight.text = remaining
                }

                override fun onError(exception: Exception) {
                    txt_startWeight.text = "0"
                    txt_goalWeight.text = "0"
                    txt_remainingWeight.text = "0"
                }
            })

        val adapter = RecyclerViewAdapter()
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        FirestoreService().getProgresses(
            FirebaseAuth.getInstance().currentUser!!.uid,
            object : IResult<List<Progress>> {
                override fun onSuccess(items: List<Progress>?) {
                    if (!items.isNullOrEmpty()) {
                        adapter.setData(items)

                        val lastProgress = items.firstOrNull()!!
                        if (!txt_startWeight.text.isNullOrEmpty() && txt_startWeight.text.toString()
                                .toDouble() > 0
                        ) {
                            calculateTotalProgress(
                                txt_startWeight.text.toString().toDouble(),
                                lastProgress
                            )
                        }

                        //calculate monthly progress
                        val firstProgressOfMonth =
                            items.lastOrNull { progress -> progress.date!!.month == lastProgress.date!!.month }
                        if (firstProgressOfMonth != null) {
                            calculateMonthlyProgress(firstProgressOfMonth, lastProgress)
                        }

                        //calculate weekly progress
                        val calendar = Calendar.getInstance()
                        calendar.time = lastProgress.date!!
                        val weekNumber = calendar.get(Calendar.WEEK_OF_YEAR)

                        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC-4"))
                        cal.set(Calendar.WEEK_OF_YEAR, weekNumber)
                        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                        cal.set(Calendar.HOUR_OF_DAY, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);

                        val firstDateOfWeek = cal.time

                        val firstProgressOfWeek =
                            items.lastOrNull() { progress -> progress.date!! >= firstDateOfWeek }

                        if (firstProgressOfWeek != null) {
                            calculateWeeklyProgress(firstProgressOfWeek, lastProgress)
                        }
                    }
                }

                override fun onError(exception: Exception) {
                    txt_weekly.text = "0"
                    txt_monthly.text = "0"
                    txt_total.text = "0"
                }
            })

        view.findViewById<Button>(R.id.btn_launch_addProgress).setOnClickListener {
            run{
                findNavController().navigate(R.id.action_progressFragment_to_addProgressFragment)
            }
        }
        return view
    }

    private fun calculateTotalProgress(startWeight: Double, lastProgress: Progress){
        val total = startWeight.minus(lastProgress.weight!!)
        if (total > 0)
            txt_total.text = String.format("%.2f", total)
    }

    private fun calculateMonthlyProgress(firstProgressOfMonth: Progress, lastProgress: Progress){
        val monthlyProgress = firstProgressOfMonth.weight!!.minus(lastProgress.weight!!)
        if (monthlyProgress > 0)
            txt_monthly.text = String.format("%.2f", monthlyProgress)
    }

    private fun calculateWeeklyProgress(firstProgressOfWeek: Progress, lastProgress: Progress){
        val weeklyProgress = firstProgressOfWeek.weight!!.minus(lastProgress.weight!!)
        if (weeklyProgress > 0)
            txt_weekly.text = String.format("%.2f", weeklyProgress)
    }
}