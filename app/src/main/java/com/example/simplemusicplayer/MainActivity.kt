package com.example.simplemusicplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "서비스 연결 성공")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "서비스 연결 실패")
        }
    }

    private fun bindService() {
        val intent = Intent(this, AudioPlayerService::class.java)
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
    }

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }
}