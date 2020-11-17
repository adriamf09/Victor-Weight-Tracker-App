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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.weighttracker.FirestoreService
import com.example.weighttracker.IResult
import com.example.weighttracker.R
import com.google.firebase.auth.FirebaseAuth
import com.example.weighttracker.models.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_register.*
import java.lang.Exception

class RegisterFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        super.onViewCreated(view, savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        view.findViewById<Button>(R.id.btn_launch_login).setOnClickListener{
            run{
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }

        view.findViewById<Button>(R.id.btn_register).setOnClickListener{
            registerUser()
        }

        return view
    }

    fun registerUser(){
        val name  = txt_name.text.toString()
        val lastName = txt_lastName.text.toString()
        val email = txt_email.text.toString()
        val password = txt_password.text.toString()

        if(validateUser(name, lastName, email, password)){
            val user = User(name, lastName, email, 0.0, 0.0)
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        run{
                            FirestoreService().createUser(it.user!!.uid, user)
                            Toast.makeText(activity, "Registrado correctamente!", Toast.LENGTH_LONG).show()
                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        }
                    }
                    .addOnFailureListener{
                        showAlert()
                    }
        }
    }

    private fun validateUser(name: String, lastName: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                txt_name.error = getString(R.string.text_field_error)
                false
            }
            TextUtils.isEmpty(lastName) -> {
                txt_lastName.error = getString(R.string.text_field_error)
                false
            }
            TextUtils.isEmpty(email) -> {
                txt_email.error = getString(R.string.text_field_error)
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->{
                txt_email.error = getString(R.string.invalid_email)
                false
            }
            TextUtils.isEmpty(password) ->{
                txt_password.error = getString(R.string.text_field_error)
                false
            }
            password.length < 6 ->{
                txt_password.error = getString(R.string.password_error)
                false
            }
            else -> true
        }
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error registrando el usuario.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}