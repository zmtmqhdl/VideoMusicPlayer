package com.example.videoMusicPlayer

import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowInsets
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.VideoMusicPlayer.R

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {

    private lateinit var surfaceView: SurfaceView
    private val viewModel: ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI()

        surfaceView = SurfaceView(this)
        setContent {
            Box(
                modifier = Modifier.fillMaxSize().background(color = Color.Black)
            ) {
                Box(
                    modifier = Modifier
                        .height(450.dp)
                        .fillMaxWidth()
                        .align(Alignment.Center)
                ) {
                    AndroidView(
                        factory = { surfaceView },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Button(
                    onClick = {
                        viewModel.youtube(this@MainActivity)
                    },
                    modifier = Modifier
                        .size(45.dp)
                        .align(Alignment.TopStart),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.play),
                        contentDescription = "Play Button",
                        modifier = Modifier.fillMaxSize().background(color = Color.White)
                    )
                }
            }
        }

        initializeSurfaceView()
    }

    private fun hideSystemUI() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            window.setFlags(
                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    android.view.View.SYSTEM_UI_FLAG_FULLSCREEN or
                    android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }

    private fun initializeSurfaceView() {
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                viewModel.initializeMediaPlayer(holder)
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                viewModel.releaseMediaPlayer()
            }
        })
    }

    override fun dispatchTouchEvent(event: android.view.MotionEvent?): Boolean {
        event?.let {
            viewModel.onTouchEvent()
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releaseMediaPlayer()
    }
}