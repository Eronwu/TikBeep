package com.eron.tikbeep.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import com.eron.tikbeep.R
import com.eron.tikbeep.databinding.ActivityAlertBinding
import com.eron.tikbeep.receiver.SmsReceiver

class AlertActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "TIKALERT"
    }
    private lateinit var binding: ActivityAlertBinding
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var waveAnimation: Animation
    private lateinit var gestureDetector: GestureDetectorCompat
    private var isSoundMuted = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "on create done set contentView")
        // 获取短信内容并显示
        val smsBody = intent.getStringExtra("sms_body") ?: ""
        val smsSender = intent.getStringExtra("sms_sender") ?: ""
        binding.tvSmsContent.text = "$smsSender:\n$smsBody"
        
        // 初始化波浪动画
        waveAnimation = AnimationUtils.loadAnimation(this, R.anim.wave_animation)
        binding.waveView.startAnimation(waveAnimation)
        
        // 初始化音频播放器
        setupMediaPlayer()
        
        // 设置手势检测器
        setupGestureDetector()
        
        // 设置按钮点击事件
        binding.btnMute.setOnClickListener {
            toggleSound()
        }
        
        // 设置按钮触摸事件，用于检测长按
        binding.btnMute.setOnTouchListener { view, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        Log.d(TAG, "done init")
    }
    
    private fun setupMediaPlayer() {
        Log.d(TAG, "media start!")
        mediaPlayer = MediaPlayer.create(this, R.raw.police_siren)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }
    
    private fun setupGestureDetector() {
        gestureDetector = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                // 长按关闭活动
                Log.d(TAG, "long click and finish!")
                finish()
            }
        })
    }
    
    private fun toggleSound() {
        Log.d(TAG, "Sound click!")
        isSoundMuted = !isSoundMuted
        if (isSoundMuted) {
            mediaPlayer.pause()
            binding.btnMuteText.text = getString(R.string.long_press_to_close)
        } else {
            mediaPlayer.start()
            binding.btnMuteText.text = getString(R.string.tap_to_mute)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "activity destroy!")
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }
}