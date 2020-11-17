package com.example.weighttracker.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.weighttracker.FirestoreService
import com.example.weighttracker.IResult
import com.example.weighttracker.R
import com.example.weighttracker.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        view.findViewById<Button>(R.id.btn_launch_register).setOnClickListener() {
            run {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }
        view.findViewById<Button>(R.id.btn_login).setOnClickListener {
            login()
        }
        return view
    }

    private fun login() {
        val email = txt_email.text.toString()
        val password = txt_password.text.toString()

        if (validateCredentials(email, password)) {
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    run {
                        FirestoreService().getUserById(it.user!!.uid, object : IResult<User> {
                            override fun onSuccess(items: User?) {
                                val bundle = Bundle()
                                bundle.putParcelable("user", items)
                                if (items!!.goalWeight!!.compareTo(0) == 0 && items!!.startWeight!!.compareTo(0) == 0) {
                                    findNavController().navigate(
                                        R.id.action_loginFragment_to_approachFragment,
                                        bundle
                                    )
                                } else {
                                    findNavController().navigate(R.id.action_loginFragment_to_progressFragment)
                                }
                            }

                            override fun onError(exception: Exception) {
                                FirebaseAuth.getInstance().signOut()
                                findNavController().navigate(R.id.loginFragment)
                            }
                        })
                    }
                }
                .addOnFailureListener {
                    showAlert()
                }
        }
    }

    private fun validateCredentials(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                txt_email.error = getString(R.string.text_field_error)
                false
            }
            TextUtils.isEmpty(password) -> {
                txt_password.error = getString(R.string.text_field_error)
                false
            }
            else -> true
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error al intentar iniciar sesión.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}