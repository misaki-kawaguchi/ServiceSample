package com.misakikawaguchi.servicesample

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class SoundManageService : Service() {

    // メディアプレーヤーフィールド
    private var _player: MediaPlayer? = null

    override fun onCreate() {
        // フィールドのメディアプレーヤーオブジェクトを生成
        _player = MediaPlayer()
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}