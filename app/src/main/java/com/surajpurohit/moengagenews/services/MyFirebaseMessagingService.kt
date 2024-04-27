import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.surajpurohit.moengagenews.R

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Check if message contains data payload
        if (remoteMessage.data.isNotEmpty()) {
            handleDataPayload(remoteMessage.data)
        }
    }

    private fun handleDataPayload(data: Map<String, String>) {
        // Extract and handle data from the payload
        val title = data["title"] ?: ""
        val message = data["message"] ?: ""

        // Handle the notification or data based on your app logic
        // (e.g., show a notification, update UI, perform actions)
        showNotification(title, message)
    }

    private fun showNotification(title: String, message: String) {
        // Implement notification logic here using NotificationManager or libraries
        // This is an example using NotificationCompat (AndroidX)
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    // Implement onNewToken to observe token changes
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")

        // You can send this token to your server to associate it with the device
        // for targeted messaging, etc.
    }

    companion object {
        private const val CHANNEL_ID = "fcm_channel_1"
        private const val NOTIFICATION_ID = 1
    }
}
