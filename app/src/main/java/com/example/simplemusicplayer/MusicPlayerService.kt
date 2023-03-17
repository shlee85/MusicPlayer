package com.example.simplemusicplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast

class MusicPlayerService : Service() {
    var mMediaPlayer: MediaPlayer? = null

    /* binder를 반환해서 서비스 함수를 쓸 수 있게 합니다. */
    var mBinder: MusicPlayerBinder = MusicPlayerBinder()
    inner class MusicPlayerBinder : Binder() {
        fun getService(): MusicPlayerService {
            return this@MusicPlayerService
        }
    }

    override fun onCreate() {               //서비스가 생성될 때 딱 한번 실행
        Log.d(TAG, "onCreate()")
        startForegroundService()            //포그라운드 서비스 시작.
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind()")

        return mBinder
    }

    //시작된 상태 & 백그라운드. (startService()를 호출하면 실행되는 콜백 함수)
    //return 값은 시스템이 서비스를 종료할 때 서비스를 어떻게 유지할지를 설명.
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy!!")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true)
        }
    }

    fun startForegroundService() {
        Log.d(TAG, "startForegroundService!!!")
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val mChannel = NotificationChannel(   //알림채널 생성
            "CHANNEL_ID","CHANNEL_NAME",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(mChannel)

        val notification = Notification.Builder(this, "CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_play)   //알림 아이콘
            .setContentTitle("뮤직 플레이어 앱")   //알림의 제목 설정
            .setContentText("앱이 실행 중입니다.")  //알림의 내용 설정
            .build()

        startForeground(1, notification) //인수로 알림 ID와 알림 지정
    }

    //재생 중인지 확인.
    fun isPlaying() : Boolean{
        Log.d(TAG, "isPlaying()!")
        return (mMediaPlayer != null && mMediaPlayer?.isPlaying ?: false)
    }

    //재생
    fun play() {
        Log.d(TAG, "play!!")
        if(mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(this, R.raw.chocolate)

            mMediaPlayer?.setVolume(1.0f, 1.0f) //볼륨 설정
            mMediaPlayer?.isLooping = true  //반복재생 여부
            mMediaPlayer?.start()   //음악을 재생.
        } else { //음악재생중일 경우
            if(mMediaPlayer!!.isPlaying) {
                Toast.makeText(this, "이미 음악을 재생중입니다.",Toast.LENGTH_SHORT).show()
            } else {
                mMediaPlayer?.start()   //음악을 재생.
            }
        }
    }

    //일시 정지
    fun pause() {
        Log.d(TAG, "pause!!")
        mMediaPlayer?.let {
            if(it.isPlaying) {
                it.pause()
            }
        }
    }

    //재생 정지
    fun stop() {
        Log.d(TAG, "stop!!")
        mMediaPlayer?.let {
            if(it.isPlaying) {
                it.stop()       //음악을 멈춤
                it.release()    //자원 해제
                mMediaPlayer = null
            }
        }
    }

    companion object {
        val TAG: String = MusicPlayerService::class.java.simpleName
    }
}