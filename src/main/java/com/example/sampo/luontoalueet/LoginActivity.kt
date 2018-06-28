package com.example.sampo.luontoalueet

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*



class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = "FirebaseEmailPassword"

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_email_sign_in.setOnClickListener(this)
        btn_email_create_account.setOnClickListener(this)
        btn_sign_out.setOnClickListener(this)
        btn_verify_email.setOnClickListener(this)

        mAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()

        val currentUser = mAuth!!.currentUser
        updateUI(currentUser)
    }

    override fun onClick(view: View?) {
        val i = view!!.id

        if (i == R.id.btn_email_create_account) {
            createAccount(edtEmail.text.toString(), edtPassword.text.toString())
        } else if (i == R.id.btn_email_sign_in) {
            signIn(edtEmail.text.toString(), edtPassword.text.toString())
        } else if (i == R.id.btn_sign_out) {
            signOut()
        } else if (i == R.id.btn_verify_email) {
            sendEmailVerification()
        }
    }

    private fun createAccount(email: String, password: String) {
        Log.e(TAG, "createAccount:" + email)
        if (!validateForm(email, password)) {
            return
        }

        mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.e(TAG, "createAccount: Success!")

                        // update UI with the signed-in user's information
                        val user = mAuth!!.currentUser
                        updateUI(user)
                    } else {
                        Log.e(TAG, "createAccount: Fail!", task.exception)
                        Toast.makeText(applicationContext, "Authentication failed!", Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
    }

    private fun signIn(email: String, password: String) {
        Log.e(TAG, "signIn:" + email)
        if (!validateForm(email, password)) {
            return
        }

        mAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.e(TAG, "signIn: Success!")

                        // update UI with the signed-in user's information
                        val user = mAuth!!.getCurrentUser()
                        updateUI(user)

                        startActivity(Intent(
                                this,
                                MainMapsActivity::class.java)
                        )

                    } else {
                        Log.e(TAG, "signIn: Fail!", task.exception)
                        Toast.makeText(applicationContext, "Authentication failed!", Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }

                    if (!task.isSuccessful) {
                        tvStatus.text = "Authentication failed!"
                    }
                }
    }

    private fun signOut() {
        mAuth!!.signOut()
        updateUI(null)
    }

    private fun sendEmailVerification() {
        // Disable Verify Email button
        findViewById<View>(R.id.btn_verify_email).isEnabled = false

        val user = mAuth!!.currentUser
        user!!.sendEmailVerification()
                .addOnCompleteListener(this) { task ->
                    // Re-enable Verify Email button
                    findViewById<View>(R.id.btn_verify_email).isEnabled = true

                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "Verification email sent to " + user.email!!, Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e(TAG, "sendEmailVerification failed!", task.exception)
                        Toast.makeText(applicationContext, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun validateForm(email: String, password: String): Boolean {

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(applicationContext, "Enter email address!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, "Enter password!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(applicationContext, "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun updateUI(user: FirebaseUser?) {

        if (user != null) {
            tvStatus.text = "User Email: " + user.email + "(verified: " + user.isEmailVerified + ")"
            tvDetail.text = "Firebase User ID: " + user.uid

            email_password_buttons.visibility = View.GONE
            email_password_fields.visibility = View.GONE
            layout_signed_in_buttons.visibility = View.VISIBLE

            btn_verify_email.isEnabled = !user.isEmailVerified
        } else {
            tvStatus.text = "Signed Out"
            tvDetail.text = null

            email_password_buttons.visibility = View.VISIBLE
            email_password_fields.visibility = View.VISIBLE
            layout_signed_in_buttons.visibility = View.GONE


        }
    }

    private fun handleFirebaseAccessToken(token: LoginActivity) {
        Log.d(TAG, "handleFirebaseAccessToken:" + token)

        fun signIn(email: String, password: String) {
            Log.e(TAG, "signIn:" + email)
            if (!validateForm(email, password)) {
                return
            }
            mAuth!!.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success")
                            val user = mAuth!!.currentUser
                            startActivity(Intent(this@LoginActivity, MainMapsActivity::class.java))
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException())
                            Toast.makeText(this@LoginActivity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
        }
    }
}