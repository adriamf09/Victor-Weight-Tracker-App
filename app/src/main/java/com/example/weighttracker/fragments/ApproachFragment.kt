package com.example.weighttracker.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.weighttracker.FirestoreService
import com.example.weighttracker.IResult
import com.example.weighttracker.R
import com.example.weighttracker.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_approach.*
import java.lang.Exception

class ApproachFragment : Fragment() {
    lateinit var user: User

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_approach, container, false)

        user = arguments?.getParcelable<User>("user")!!
        val text = "Hola, ${user.name}"
        view.findViewById<TextView>(R.id.txt_user).text = text

        view.findViewById<Button>(R.id.btn_continue).setOnClickListener {
            updateUser()
        }
        return view
    }

    private fun updateUser(){
        val startWeight = txt_startWeight.text.toString().toDouble()
        val goalWeight = txt_goalWeight.text.toString().toDouble()
        if(validateFields(startWeight, goalWeight)){
            user.startWeight = startWeight
            user.goalWeight = goalWeight
            FirestoreService().updateUser(FirebaseAuth.getInstance().currentUser!!.uid, user, object: IResult<User>{
                override fun onSuccess(items: User?) {
                    if(items != null)
                        findNavController().navigate(R.id.action_approachFragment_to_progressFragment)
                }

                override fun onError(exception: Exception) {
                    showAlert()
                }
            })
        }
    }

    private fun validateFields(startWeight: Double, goalWeight: Double): Boolean{
        return when{
            TextUtils.isEmpty(startWeight.toString()) -> {
                txt_startWeight.error = getString(R.string.text_field_error)
                false
            }
            TextUtils.isEmpty(goalWeight.toString()) -> {
                txt_goalWeight.error = getString(R.string.text_field_error)
                false
            }
            else ->true
        }
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error al guardar los datos.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}