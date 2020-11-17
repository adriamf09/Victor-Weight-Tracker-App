package com.example.weighttracker.fragments

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.weighttracker.FirestoreService
import com.example.weighttracker.R
import com.example.weighttracker.models.Progress
import com.google.firebase.auth.FirebaseAuth
import com.google.type.Date
import kotlinx.android.synthetic.main.fragment_add_progress.*
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class AddProgressFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        super.onViewCreated(view, savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_progress, container, false)

        view.findViewById<Button>(R.id.btn_saveProgress).setOnClickListener {
            saveProgress()
        }
        return view
    }

    private fun saveProgress(){
        if(!TextUtils.isEmpty(txt_peso.text)){
            val datePicker = datePicker
            val progress = Progress(FirebaseAuth.getInstance().currentUser?.uid, java.util.Date(datePicker.year - 1900,
                datePicker.month, datePicker.dayOfMonth), txt_peso.text.toString().toDouble())
            FirestoreService().saveProgress(progress)
            findNavController().navigate(R.id.action_addProgressFragment_to_progressFragment)
        }
        else{
            txt_peso.error = getString(R.string.text_field_error)
        }
    }
}