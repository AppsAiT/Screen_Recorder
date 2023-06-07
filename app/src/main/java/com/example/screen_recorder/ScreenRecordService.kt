package com.example.screen_recorder

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaMetadataRetriever
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class ScreenRecordService : Service() {
    private val PERMISSIONS_REQUEST_CODE = 123
    private var mWindowManager: WindowManager? = null
    private var params: WindowManager.LayoutParams? = null
    var LAYOUT_FLAG: Int = 0
    private var mFloatingView: View? = null
    private var mediaProjectionManager: MediaProjectionManager? = null
    private var mediaProjection: MediaProjection? = null
    private var mMediaProjection: MediaProjection? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private var mMediaRecorder: MediaRecorder? = null
    private var serviceResultCode = 0
    private var data: Intent? = null
    private var mScreenStateReceiver: BroadcastReceiver? = null
    private var isRecording = false
    private var filePathAndName: String = ""


    override fun onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //checks if the device's SDK version is equal to or higher than Android Oreo (API level 26)
            startMyOwnForeground()
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        } else {
            startForeground(1, Notification())
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE
            params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        }
//    ----------------------------------Floating Button----------------
        //Inflate the floating view layout we created
        // mFloatingView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null)

        mediaProjectionManager =
            getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager //can use it to interact with the screen capture functionality

//    ----------------------------------Floating Button----------------
//        //Specify the view position
//        params!!.gravity =
//            Gravity.TOP or Gravity.LEFT //Initially view will be added to top-left corner
//
//        params!!.x = 0
//        params!!.y = 100
//
//        //Add the view to the window
//        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
//        mWindowManager!!.addView(mFloatingView, params)


        // register receiver to check if the phone screen is on or off
//        mScreenStateReceiver = MyBroadcastReceiver()
//        val screenStateFilter = IntentFilter()
//        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON)
//        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF)
//        screenStateFilter.addAction(Intent.ACTION_CONFIGURATION_CHANGED)
//        registerReceiver(mScreenStateReceiver, screenStateFilter)
//        val thread = HandlerThread(
//            "ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND
//        )
//        thread.start()
//        val mServiceLooper = thread.looper
//        mServiceHandler = ServiceHandler(mServiceLooper)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent != null && intent.hasExtra("data")) {
            serviceResultCode = intent.getIntExtra(EXTRA_RESULT_CODE, 0)
            data = intent.getParcelableExtra(EXTRA_DATA)
            mediaProjection = mediaProjectionManager!!.getMediaProjection(
                Activity.RESULT_OK,
                intent.getParcelableExtra<Intent>("data")!!
            )
            //create media recorder to capture screen content
            mMediaRecorder = MediaRecorder()
            val metrics = DisplayMetrics()
            val wm = applicationContext.getSystemService(WINDOW_SERVICE) as WindowManager
            wm.defaultDisplay.getRealMetrics(metrics)

            val mScreenDensity = metrics.densityDpi
            val displayWidth = metrics.widthPixels
            val displayHeight = metrics.heightPixels

            mMediaRecorder!!.setVideoSource(MediaRecorder.VideoSource.SURFACE)
            mMediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mMediaRecorder!!.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            mMediaRecorder!!.setVideoEncodingBitRate(8 * 1000 * 1000)
            mMediaRecorder!!.setVideoFrameRate(15)
            mMediaRecorder!!.setVideoSize(displayWidth, displayHeight)

            val videoDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath
            val timestamp = System.currentTimeMillis()

            var orientation = "portrait"
            if (displayWidth > displayHeight) {
                orientation = "landscape"
            }
            filePathAndName =
                videoDir + "/time_" + timestamp.toString() + "_mode_" + orientation + ".mp4"

            mMediaRecorder!!.setOutputFile(filePathAndName)
            try {
                mMediaRecorder!!.prepare()
            } catch (e: java.lang.IllegalStateException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            mMediaProjection = mediaProjectionManager!!.getMediaProjection(serviceResultCode, data!!)
            val surface = mMediaRecorder!!.surface
            mVirtualDisplay = mMediaProjection!!.createVirtualDisplay(
                "MainActivity",
                displayWidth,
                displayHeight,
                mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION,
                surface,
                null,
                null
            )
            mMediaRecorder!!.start()
            Toast.makeText(this, "Started Recording", Toast.LENGTH_SHORT).show()
        }
        else if (intent.action == "com.example.screen_recorder.STOP_RECORDING") {
            stopRecording()
            stopForeground(true)
            stopSelf()
        }
//
//        val msg = mServiceHandler!!.obtainMessage()
//        msg.arg1 = startId
//        mServiceHandler!!.sendMessage(msg)
        return START_NOT_STICKY
    }


    private fun startMyOwnForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val NOTIFICATION_CHANNEL_ID = "com.example.screen_recorder"
            val channelName = "My Background Service"
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            manager.createNotificationChannel(notificationChannel)

            val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            val notification =
                notificationBuilder.setOngoing(true).setContentTitle("App is running in background")
                    .setContentText("Your screen is being recorded")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE).build()
            startForeground(2, notification)

        }
    }



    private fun stopRecording() {
        isRecording = false
        if (mMediaRecorder != null) {
            mMediaRecorder!!.stop()
            mMediaRecorder!!.release()
            mMediaRecorder = null
        }
        if (mVirtualDisplay != null) {
            mVirtualDisplay!!.release()
            mVirtualDisplay = null
        }
        if (mMediaProjection != null) {
            mMediaProjection!!.stop()
            mMediaProjection = null
        }
        Toast.makeText(this, "Recording Stopped", Toast.LENGTH_SHORT).show()
        saveVideoToGallery(filePathAndName)
    }


    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    override fun onDestroy() {
        stopRecording()
        //unregisterReceiver(mScreenStateReceiver)
        stopSelf()

    }

    companion object {
        private const val TAG = "RECORDERSERVICE"
        const val EXTRA_RESULT_CODE = "resultcode"
        const val EXTRA_DATA = "data"
        private const val ONGOING_NOTIFICATION_ID = 23
        const val ACTION_START_RECORDING = "com.example.screen_recorder.START_RECORDING"
        const val ACTION_STOP_RECORDING = "com.example.screen_recorder.STOP_RECORDING"

        fun newIntent(context: Context?, resultCode: Int, data: Intent?): Intent {
            val intent = Intent(context, ScreenRecordService::class.java)
            intent.putExtra(EXTRA_RESULT_CODE, resultCode)
            intent.putExtra(EXTRA_DATA, data)
            return intent
        }
    }

    private fun saveVideoToGallery(fileName: String) {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(applicationContext, Uri.fromFile(File(fileName)))
        val format = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
        retriever.release()
        Log.d("hello", "$fileName $format")
        val values = ContentValues().apply {
            put(MediaStore.Video.Media.TITLE, fileName)
            put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        }

        // Save the video to the gallery
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
            values.put(MediaStore.Video.Media.IS_PENDING, 1)

            val resolver = applicationContext.contentResolver
            val collectionUri =
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            val videoUri = resolver.insert(collectionUri, values)

            if (videoUri != null) {
                try {
                    val outputStream = resolver.openOutputStream(videoUri)
                    outputStream?.close()
                    values.put(MediaStore.Video.Media.IS_PENDING, 0)
                    resolver.update(videoUri, values, null, null)
                    Toast.makeText(
                        applicationContext, "Video saved successfully", Toast.LENGTH_SHORT
                    ).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(applicationContext, "Failed to save video", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            // Save the video to the deprecated external storage directory
            val directory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                    .toString()
            val videoFile = File(directory, fileName)

            try {
                val outputStream: FileOutputStream = FileOutputStream(videoFile)
                outputStream.flush()
                outputStream.close()
                // Notify the media scanner to add the video to the gallery
                MediaScannerConnection.scanFile(
                    applicationContext, arrayOf(videoFile.absolutePath), null, null
                )
                Toast.makeText(
                    applicationContext, "Video saved successfully", Toast.LENGTH_SHORT
                ).show()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }





}



