package com.eron.tikbeep.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.eron.tikbeep.service.SmsMonitorService
import com.eron.tikbeep.ui.AlertActivity

class SmsReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "SmsReceiver"
        private const val KEYWORD = "交警"
    }

    override fun onReceive(context: Context, intent: Intent) {
        // 检查服务是否在运行
        var cnt = 0
        while (true) {
            if (cnt > 3) return
            if (!SmsMonitorService.isRunning) {
                Log.d(TAG, "服务未运行，不处理短信")
                Thread {
                    try {
                        // 线程休眠3秒
                        Thread.sleep(3000)

                        // 这里可以执行需要等待后执行的操作
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }.start()
                cnt++
            } else {
                cnt = 0
                break
            }
        }

        Log.d(TAG, "服务运行中，检测短信ing")
        // 检查是否是短信接收广播
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            
            for (message in messages) {
                val smsBody = message.messageBody
                val sender = message.originatingAddress ?: "未知号码"
                
                Log.d(TAG, "收到短信: $smsBody, 来自: $sender")
                
                // 检查短信内容是否包含关键词
                if (smsBody.contains(KEYWORD)) {
                    Log.d(TAG, "检测到交警短信，启动警报界面")
                    
                    // 启动警报界面
                    val alertIntent = Intent(context, AlertActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra("sms_body", smsBody)
                        putExtra("sms_sender", sender)
                    }
                    context.startActivity(alertIntent)
                    break
                }
            }
        }
    }
}