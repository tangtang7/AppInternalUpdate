package com.example.appinternalupdate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button1 = findViewById<Button>(R.id.button1)
        button1.setOnClickListener {
            val appStoreName = AppUpdateManager.isAppStoreAvailable(applicationContext)
            Toast.makeText(applicationContext, appStoreName, Toast.LENGTH_LONG).show()
            Log.e("whh", "isAppStoreAvailable = " +  AppUpdateManager.isAppStoreAvailable(applicationContext))
        }

        val button2 = findViewById<Button>(R.id.button2)
        button2.setOnClickListener {
            Toast.makeText(applicationContext, "即将跳转应用商场", Toast.LENGTH_SHORT).show()
            AppUpdateManager.jumpToMarketAppDetail(applicationContext, "com.tencent.mm", "")
        }

        val button3 = findViewById<Button>(R.id.button3)
        button3.setOnClickListener {
            val url = "https://一个链接"
            Toast.makeText(applicationContext, "即将跳转浏览器下载链接", Toast.LENGTH_SHORT).show()
            AppUpdateManager.jumpToBrowserAppDetail(applicationContext, url)
        }
    }
}