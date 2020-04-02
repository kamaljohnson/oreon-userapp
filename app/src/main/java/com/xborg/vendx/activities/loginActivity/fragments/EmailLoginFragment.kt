package com.xborg.vendx.activities.loginActivity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

import com.xborg.vendx.R
import com.xborg.vendx.activities.loginActivity.SharedViewModel
import kotlinx.android.synthetic.main.fragment_email_login.*

class EmailLoginFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var name: String
    private lateinit var firstName: String
    private lateinit var lastName: String

    private lateinit var email: String

    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_email_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sharedViewModel = ViewModelProvider(this.activity!!).get(SharedViewModel::class.java)

        submit_email_button.setOnClickListener {
            if(email_input.text.toString() != "" && name_input.text.toString() != "") {

                name = name_input.text.toString().trim()

                firstName = name.split(" ")[0]
                lastName = name.split(" ")[name.split(" ").size - 1]

                Toast.makeText(context, "full name: $firstName $lastName", Toast.LENGTH_SHORT).show()

                email = email_input.text.toString().trim()

                sharedViewModel.sendNameAndEmail(name, email)

                email_input_field.visibility = View.INVISIBLE
                otp_input_field.visibility = View.VISIBLE

//                TODO: this should be made visible only after user enters a valid OTP
                submit_otp_button.visibility = View.VISIBLE
            } else {
                Toast.makeText(context, "please fill in the email and name fields", Toast.LENGTH_SHORT).show()
            }
        }

        submit_otp_button.setOnClickListener {
            if(otp_input.text.toString().length == 6) {

                token = otp_input.text.toString()

                sharedViewModel.sendEmailToken(email, token)

            } else {
                Toast.makeText(context, "improper OTP format", Toast.LENGTH_SHORT).show()
            }
        }
    }
}