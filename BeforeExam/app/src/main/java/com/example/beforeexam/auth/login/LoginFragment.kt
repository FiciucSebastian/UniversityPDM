package com.example.beforeexam.auth.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.beforeexam.R
import kotlinx.android.synthetic.main.fragment_login.*
import com.example.beforeexam.core.Result
import com.example.beforeexam.core.TAG

class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        setupLoginForm()
    }

    private fun setupLoginForm() {
        viewModel.loginFormState.observe(viewLifecycleOwner, { loginState ->
            loginBtn.isEnabled = loginState.isDataValid
            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
        })
        viewModel.loginResult.observe(viewLifecycleOwner, { loginResult ->
            loading.visibility = View.GONE
            if (loginResult is Result.Success<*>) {
                if (loginResult.data.toString().contains("professor")){
                    findNavController().navigate(R.id.action_fragment_login_to_profesor_fragment)
                }
                else{
                    findNavController().navigate(R.id.action_fragment_login_to_student_fragment)
                }
            } else if (loginResult is Result.Error) {
                Toast.makeText(this.context, "Login-ul a esuat", Toast.LENGTH_LONG).show()
                error_text.text = "Login error ${loginResult.exception.message}"
                error_text.visibility = View.VISIBLE
            }
        })
        username.afterTextChanged {
            viewModel.loginDataChanged(
                username.text.toString()
            )
        }
        loginBtn.setOnClickListener {
            loading.visibility = View.VISIBLE
            error_text.visibility = View.GONE
            viewModel.login(username.text.toString(), requireContext())
        }
    }
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}