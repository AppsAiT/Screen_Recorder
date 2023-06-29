package com.example.screen_recorder

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import androidx.core.app.NotificationCompat
import com.example.screen_recorder.MainMenu.Companion.ACTION_STOP_FOREGROUND

class FloatingControlService :Service() {

    private var windowManager: WindowManager? = null
    private var floatingControlView: ViewGroup? = null
    var iconHeight = 0
    var iconWidth = 0
    private var screenHeight = 0
    private var screenWidth = 0

    private var popupView: ViewGroup? = null
    private var isDragging = false
    private var isPopupVisible = false
    private var touchSlop = 0

    private val channelId = "ForegroundChannelId"
    private val channelName = "Foreground Channel"
    private val notificationId = 1

    private var hideHandler: Handler? = null
    private var hideRunnable: Runnable? = null
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(windowManager == null) {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        }
        if(floatingControlView == null ){
            val li = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            floatingControlView = li.inflate(R.layout.layout_floating_control_view, null) as ViewGroup?
        }

        // Start the service as a foreground service
        startForeground(notificationId, createNotification())
        addFloatingMenu()
        return START_STICKY

        //Normal Service To test sample service comment the above    generateForegroundNotification() && return START_STICKY
        // Uncomment below return statement And run the app.
//        return START_NOT_STICKY
    }

    private fun removeFloatingContro() {
        if(floatingControlView?.parent !=null) {
            windowManager?.removeView(floatingControlView)
        }
    }

    private fun addFloatingMenu() {
        if (floatingControlView?.parent == null) {
            //Set layout params to display the controls over any screen.
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_PHONE else WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
            params.height = dpToPx(50)
            params.width = dpToPx(50)
            iconWidth = params.width
            iconHeight = params.height
            screenHeight = windowManager?.defaultDisplay?.height ?: 0
            screenWidth = windowManager?.defaultDisplay?.width ?: 0
            //Initial position of the floating controls
            params.gravity = Gravity.TOP or Gravity.START
            params.x = 0
            params.y = 100

            //Add the view to window manager
            windowManager?.addView(floatingControlView, params)
            try {
                addOnTouchListener(params)
            } catch (e: Exception) {
                // TODO: handle exception
            }

        }
    }

    private fun addOnTouchListener(params: WindowManager.LayoutParams) {
        // Add touch listener to floating controls view to move/close/expand the controls
        floatingControlView?.setOnTouchListener(object : View.OnTouchListener {
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            private var initialX = 0
            private var initialY = 0


            override fun onTouch(view: View?, motionevent: MotionEvent): Boolean {
                when (motionevent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = motionevent.rawX
                        initialTouchY = motionevent.rawY
                        isDragging = false
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        initialX = params.x
                        initialY = params.y
                        if ((motionevent.rawX < (initialX - iconWidth / 2).toFloat()) ||
                            (motionevent.rawY < (initialY - iconHeight / 2).toFloat()) ||
                            (motionevent.rawX > initialX + iconWidth * 1.2)
                        ) {
                            isDragging = true
                        }
                        params.x = (motionevent.rawX - (iconWidth / 2).toFloat()).toInt()
                        params.y = (motionevent.rawY - iconHeight.toFloat()).toInt()
                        try {
                            windowManager?.updateViewLayout(floatingControlView, params)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            // Exception handling code (code is missing in the provided snippet)
                        }
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        val touchMoved = Math.abs(motionevent.rawX - initialTouchX) > touchSlop ||
                                Math.abs(motionevent.rawY - initialTouchY) > touchSlop
                        if (!touchMoved && !isDragging) {
                            // Inflate and add the popup views
                            if (isPopupVisible) {
                                windowManager?.removeView(popupView)
                                isPopupVisible = false
                            } else {
                                // Inflate and add the popup views
                                val li = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                                popupView = li.inflate(R.layout.layout_popup_views, null) as ViewGroup

                                // Set the position of the popup views
                                val popupParams = WindowManager.LayoutParams(
                                    WindowManager.LayoutParams.WRAP_CONTENT,
                                    WindowManager.LayoutParams.WRAP_CONTENT,
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_PHONE else WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                    PixelFormat.TRANSLUCENT
                                )
                                popupParams.x = params.x + floatingControlView!!.width / 2
                                popupParams.y = params.y - (popupView?.height ?: 0)

                                // Add the popup views to the window manager
                                windowManager?.addView(popupView, popupParams)
                                isPopupVisible = true



                            }
                        } else if (!isDragging) {
                            val screenWidth = Resources.getSystem().displayMetrics.widthPixels
                            val viewCenterX = params.x + floatingControlView!!.width / 2

                            // Move the view towards the nearest side (left or right) based on its current position
                            if (viewCenterX <= screenWidth / 2) {
                                // Move towards the left side
                                params.x = 0
                            } else {
                                // Move towards the right side
                                params.x = screenWidth - floatingControlView!!.width
                            }

                            try {
                                windowManager?.updateViewLayout(floatingControlView, params)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                // Exception handling code (code is missing in the provided snippet)
                            }
                        }
                        return true
                    }
                    else -> {
                        // Handle other motion events if needed
                    }
                }
                return false
            }
        })



    }


    private fun createNotification(): Notification {
        createNotificationChannel()

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Floating View Service")
            .setContentText("Running...")
            .setSmallIcon(R.drawable.ic_notifications)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            channel.setShowBadge(false)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    //Method to convert dp to px
    private fun dpToPx(dp: Int): Int {
        val displayMetrics = this.resources.displayMetrics
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

}