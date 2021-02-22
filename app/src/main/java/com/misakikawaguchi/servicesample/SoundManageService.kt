package com.misakikawaguchi.servicesample

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.io.IOException

class SoundManageService : Service() {

    // メディアプレーヤーフィールド
    private var _player: MediaPlayer? = null

    override fun onCreate() {
        // フィールドのメディアプレーヤーオブジェクトを生成
        _player = MediaPlayer()

        // 通知チャネルのID文字列を用意
        val id = "soundmanagerservice_notification_channel"
        // 通知チャネル名をstrings.xmlから取得
        val name = getString(R.string.notification_channel_name)
        // 通知チャネルの重要度を標準に設定
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        // 通知チャネルを生成
        val channel = NotificationChannel(id, name, importance)
        // NotificationManagerオブジェクトを取得
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // 通知チャネルを設定
        manager.createNotificationChannel(channel)
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
            // Notificationを作成するBuilderクラスを生成
            val builder = NotificationCompat.Builder(applicationContext, "soundmanagerservice_notification_channel")

            // 通知エリアに表示されるアイコンを設定
            builder.setSmallIcon(android.R.drawable.ic_dialog_info)
            // 通知ドロワーでの表示タイトルを設定
            builder.setContentTitle(getString(R.string.msg_notification_title_finish))
            // 通知ドロワーでの表示メッセージを設定
            builder.setContentText(getString(R.string.msg_notification_text_finish))

            // BuilderからNotificationオブジェクトを生成
            val notification = builder.build()
            // NotificationManagerオブジェクトを取得
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            // 通知
            manager.notify(0, notification)

            // 自分自身を終了
            stopSelf()
        }
    }
}