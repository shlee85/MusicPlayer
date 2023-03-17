package com.example.simplemusicplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var btn_play: Button
    lateinit var btn_pause: Button
    lateinit var btn_stop: Button
    var mService: MusicPlayerService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_play = findViewById(R.id.btn_play)
        btn_pause = findViewById(R.id.btn_pause)
        btn_stop = findViewById(R.id.btn_stop)

        btn_play.setOnClickListener(this)
        btn_pause.setOnClickListener(this)
        btn_stop.setOnClickListener(this)

    }

    val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "서비스 연결 성공")

            //MusicPlayerBinder로 형변환
            mService = (service as MusicPlayerService.MusicPlayerBinder).getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "서비스 연결 실패")
            mService = null //서비스가 끊기면 null로 대입.
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_play -> {
                Log.d(TAG, "click play")
                play()
            }
            R.id.btn_pause -> {
                Log.d(TAG, "click pause")
                pause()
            }
            R.id.btn_stop -> {
                Log.d(TAG, "click stop")
                stop()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume!!")

        //서비스 실행
        if(mService == null) {
            //안드로이드 8 이상이면 startForegroundService를 사용 해야 함.

            val intent = Intent(this, MusicPlayerService::class.java)
            ContextCompat.startForegroundService(this, intent)

            //액티비티를 서비스와 바인드시킨다.
            val intent2 = Intent(this, MusicPlayerService::class.java)
            bindService(intent2, mServiceConnection, Context.BIND_AUTO_CREATE) //서비스가 실행되지 않은 상태라면 서비스를 생성.

            //사용자가 액티비티를 떠났을 때 처리.
            if(mService != null) {
                if(!mService!!.isPlaying()) {
                    mService!!.stopSelf()
                }
                unbindService(mServiceConnection) //서비스로부터 연결을 끊는다.
                mService = null
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause!!")
    }

    private fun play() {
        Log.d(TAG, "play.")
        mService?.play()
    }

    private fun pause() {
        Log.d(TAG, "pause.")
        mService?.pause()
    }

    private fun stop() {
        Log.d(TAG, "stop.")
        mService?.stop()
    }

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

}