package com.example.videoMusicPlayer

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.SurfaceHolder
import androidx.lifecycle.AndroidViewModel

class ViewModel(application: Application) : AndroidViewModel(application) {

    private var mediaPlayer: MediaPlayer? = null
    private var currentVideoIndex = 0
    private val videoFiles = arrayOf(
        "video1",
        "video2",
        "video3",
        "video4"
    )

    private val inactivityTimeout: Long = 90000
    private var lastTouchTime: Long = 0
    private val handler = Handler(Looper.getMainLooper())

    fun initializeMediaPlayer(holder: SurfaceHolder) {
        val videoUri: Uri = Uri.parse("android.resource://${getApplication<Application>().packageName}/raw/${videoFiles[currentVideoIndex]}")

        mediaPlayer?.release()

        mediaPlayer = MediaPlayer().apply {
            setDataSource(getApplication(), videoUri)
            setDisplay(holder)
            isLooping = false
            setVolume(0f, 0f)

            setOnPreparedListener {
                start()
            }

            setOnCompletionListener {
                currentVideoIndex = (currentVideoIndex + 1) % videoFiles.size
                initializeMediaPlayer(holder)
            }

            prepareAsync()
        }
    }

    fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun youtube(activity: Activity) {
        val context = getApplication<Application>().applicationContext
        val youtubeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/"))
        youtubeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        youtubeIntent.`package` = "com.google.android.youtube"

        context.startActivity(youtubeIntent)
        startInactivityTimer(activity)
    }

    fun onTouchEvent() {
        lastTouchTime = System.currentTimeMillis()
    }

    private fun startInactivityTimer(activity: Activity) {
        lastTouchTime = System.currentTimeMillis()
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (System.currentTimeMillis() - lastTouchTime > inactivityTimeout) {
                    minimizeYoutubeAndReturnApp(activity)
                } else {
                    handler.postDelayed(this, 1000)
                }
            }
        }, 1000)
    }

    private fun minimizeYoutubeAndReturnApp(activity: Activity) {
        activity.moveTaskToBack(true)

        val launchIntent = activity.packageManager.getLaunchIntentForPackage(activity.packageName)
        launchIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(launchIntent)
    }
}
