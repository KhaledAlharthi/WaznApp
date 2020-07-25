package com.waznapp.Utilities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.waznapp.MainActivity
import com.waznapp.R
import com.waznapp.database.Transaction


private fun createNotificationChannel(context : Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("MAIN", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    fun showNotification(context : Context, message : String, transaction : Transaction) {

        /*
        *   Make sure to create a channel for this notification
        * */
        createNotificationChannel(context)

        // Create an explicit intent for an Activity in your app
        val intent = Intent(context, MainActivity::class.java).apply {
            val bundle = Bundle()
            bundle.putString(context.getString(R.string.notification_type_intent_key), TRANSACTION_NOTIFICATION_TYPE)
            bundle.putDouble(context.getString(R.string.amount_intent_key), transaction.amount)
            bundle.putString(context.getString(R.string.merchant_intent_key), transaction.merchant)
            bundle.putString(context.getString(R.string.currency_intent_key), transaction.currency)
            bundle.putLong(context.getString(R.string.date_intent_key), transaction.date)
            bundle.putString(context.getString(R.string.transaction_type_intent_key), transaction.type)
            putExtra(context.getString(R.string.notification_data_intent_key), bundle)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationTitle = context.getString(R.string.transaction_notification_title)

        val primaryColor = context.getColor(R.color.colorPrimary)
        val textColor = Color.WHITE
        val text = transaction.amount.toString()
        val largeIcon = createNotificationIcon(text, textColor, bgColor = primaryColor)


        val builder = NotificationCompat.Builder(context, "MAIN")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(largeIcon)
            .setContentTitle(notificationTitle)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.arrow_up_float, context.getString(R.string.yes_record_transaction), pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(0, builder.build())
        }

    }




    /**
     *  Draws a circle shaped icon with text inside
     *  @param bgColor Shape background color
     *  @param textColor Color of text displayed inside shape
     *  @param text Text to display inside shape
     *  @return Bitmap icon with rounded shape and text written inside
     * */
    fun createNotificationIcon (text : String, textColor : Int, bgColor : Int) : Bitmap {
        /*
        * Define width, and height for this shape
        * */
        val width = 150
        val height = 150
        // Create an empty bitmap with specified size
        val bitmap : Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        // Define canvas to draw shape + text
        val canvas = Canvas(bitmap)
        // define shape properties
        val shapePaint = Paint()
        // Specify the background color of the shape
        shapePaint.color = bgColor
        // Make shape anti-alias to avoid noise on edges
        shapePaint.isAntiAlias = true
        // Draw a circle shape in canvas
        canvas.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), width.toFloat(), height.toFloat(), shapePaint)

        // Define text properties
        val textPaint = Paint()
        // Set text color
        textPaint.color = textColor
        // Make shape anti-alias to avoid noise on edges
        textPaint.isAntiAlias = true

        /*
        *   Now we're calculating the appropriate size of text to make it fit inside shape
        *   First, set a large text size (50px), and calculate the space it absorbed.
        *   Second, divide the width of shape by the space occupied by temp large text
        *    multiplied by temp large size (50px)
        *   Finally, we'll have the appropriate text size to fit inside shape
        * */
        // set temp large text size
        textPaint.textSize = 50f
        // define new rect, to calculate the space absorbed by text
        val bounds = Rect()
        // calculate space text occupied
        textPaint.getTextBounds(text, 0, text.length, bounds)
        // Calculate the appropriate text size
        val desiredTextSize: Float = 50f * width / bounds.width()
        // Set the correct text size - %15 margin
        textPaint.textSize = desiredTextSize * .85f

        //   Here we're trying to make the text centered.
        val finalBounds = Rect()
        // First, calculate the space the text occupies
        textPaint.getTextBounds(text, 0, text.length, finalBounds)
        // Second, define coordinates
        val x : Float = width/2f - finalBounds.width()/2f
        val y : Float = height/2f + finalBounds.height()/2f
        // Finally draw text inside the shape
        canvas.drawText (text, x, y, textPaint)
        // return result icon
        return bitmap
    }