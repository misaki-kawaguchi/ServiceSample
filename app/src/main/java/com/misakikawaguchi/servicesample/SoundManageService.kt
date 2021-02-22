package com.misakikawaguchi.servicesample

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
import java.io.IOException

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

    // バックグラウンドで行う処理
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // 音声ファイルのURI文字列を作成
        val mediaFileUriStr = "android.resource://${packageName}/${R.raw.mountain_stream}"
        // 音声ファイルのURI文字列を元にURIオブジェクトを生成
        val mediaFileUri = Uri.parse(mediaFileUriStr)

        try {
            // メディアプレーヤーに音声ファイルを指定
            _player?.setDataSource(applicationContext, mediaFileUri)

            // 非同期でメディア再生準備が完了した際のリスナを設定
            _player?.setOnPreparedListener(PlayerPreparedListener())
            // メディア再生が終了した際のリスナを設定
            _player?.setOnCompletionListener(PlayerCompletionListener())

            // 非同期でメディア再生を準備
            _player?.prepareAsync()
        }

        // 例外発生時の処理
        catch (ex: IllegalAccessException) {
            Log.e("ServiceSample", "メディアプレーヤー準備時の例外発生", ex)
        }
        catch (ex: IOException) {
            Log.e("ServiceSample", "メディアプレーヤー準備時の例外発生", ex)
        }

        // 定数を返す
        return Service.START_NOT_STICKY
    }

    // アクティビティ終了時の処理
    override fun onDestroy() {
        // フィールドのプレーヤーがnullじゃなかったら
        _player?.let {
            // プレーヤーが再生中の場合
            if(it.isPlaying) {
                // プレーヤーの停止
                it.stop()
            }
            // プレーヤーの解放
            it.release()
            // プレーヤー用フィールドをnullに
            _player = null
        }
    }

    // メディア再生準備が完了時のリスナクラス
    private inner class PlayerPreparedListener : MediaPlayer.OnPreparedListener {
        override fun onPrepared(mp: MediaPlayer) {
            // メディアを再生
            mp.start()
        }
    }

    // メディア再生が終了した時のリスナクラス
    private inner class PlayerCompletionListener : MediaPlayer.OnCompletionListener {
        override fun onCompletion(mp: MediaPlayer) {
            // 自分自身を終了
            stopSelf()
        }
    }
}