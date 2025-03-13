package com.eron.tikbeep.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.eron.tikbeep.R
import com.eron.tikbeep.databinding.ActivityMainBinding
import com.eron.tikbeep.service.SmsMonitorService

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val SMS_PERMISSION_CODE = 100
    private val NOTIFICATION_PERMISSION_CODE = 101
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupListeners()
    }
    
    private fun setupListeners() {
        // 启动服务按钮
        binding.btnStartService.setOnClickListener {
            if (!checkNotificationPermission()) {
                requestNotificationPermission()
            }
            if (checkSmsPermission()) {
                startSmsMonitorService()
                updateServiceStatus(true)
                // 启动服务后自动退出应用
//                finish()
            } else {
                requestSmsPermission()
            }

//            // 启动警报界面 -- test
//            val alertIntent = Intent(this, AlertActivity::class.java).apply {
//                flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                putExtra("sms_body", "heheeda text")
//                putExtra("sms_sender", "10096")
//            }
//            this.startActivity(alertIntent)
        }
        
        // 停止服务按钮
        binding.btnStopService.setOnClickListener {
            stopSmsMonitorService()
            updateServiceStatus(false)
        }
        
        // 关于按钮
        binding.btnAbout.setOnClickListener {
            Toast.makeText(this, R.string.app_description, Toast.LENGTH_LONG).show()
        }
    }
    
    private fun checkSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECEIVE_SMS
        ) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun requestSmsPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS),
            SMS_PERMISSION_CODE
        )
    }

    private fun checkNotificationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun requestNotificationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            NOTIFICATION_PERMISSION_CODE
        )
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startSmsMonitorService()
                updateServiceStatus(true)
                // 启动服务后自动退出应用
                finish()
            } else {
                Toast.makeText(this, "需要短信权限才能监听交警短信", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun startSmsMonitorService() {
        val serviceIntent = Intent(this, SmsMonitorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }
    
    private fun stopSmsMonitorService() {
        val serviceIntent = Intent(this, SmsMonitorService::class.java)
        stopService(serviceIntent)
    }
    
    private fun updateServiceStatus(isRunning: Boolean) {
        if (isRunning) {
            binding.tvServiceStatus.setText(R.string.service_running)
            binding.tvServiceStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
        } else {
            binding.tvServiceStatus.setText(R.string.service_stopped)
            binding.tvServiceStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        }
    }
}