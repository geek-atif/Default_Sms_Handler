package com.atifqamar.smsdefaulthandler

import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var intentLauncher: ActivityResultLauncher<Intent>

    companion object {
        // The requested role.
        const val role = RoleManager.ROLE_SMS
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
        prepareIntentLauncher()
    }

    private fun prepareIntentLauncher() {
        intentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    showToast("Success requesting ROLE_SMS!")
                } else {
                    showToast("Failed requesting ROLE_SMS")
                }
            }
    }

    private fun initUI() {
        val btSubmit = findViewById<Button>(R.id.btSubmit)
        btSubmit.setOnClickListener {
            askDefaultSmsHandlerPermission()
        }
    }

    private fun askDefaultSmsHandlerPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager: RoleManager = getSystemService(RoleManager::class.java)
            // check if the app is having permission to be as default SMS app
            val isRoleAvailable = roleManager.isRoleAvailable(role)
            if (isRoleAvailable) {
                // check whether your app is already holding the default SMS app role.
                val isRoleHeld = roleManager.isRoleHeld(role)
                if (!isRoleHeld) {
                    intentLauncher.launch(roleManager.createRequestRoleIntent(role))
                } else {
                    // Request permission for SMS
                }
            }
        } else {
            val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
            startActivityForResult(intent, 1001)
        }

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


}