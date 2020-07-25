package com.waznapp.Utilities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.waznapp.R
import com.waznapp.database.Transaction
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


/**
 *  Handles coming SMS messages depending on type of message
 *   @param context Object of application/activity context
 *   @param message Content of the SMS message
 * */
fun handleMessage (context : Context, message : String){
    /*
    *   Check if message is from Bank
    * */
    if (isBank(message)) {
        /*
        *   If message is a Bank message,
        *       extract transaction details
        * */
        val amount = extractPrice(message)
        val type = detectTransactionType(message)
        val date = extractDate(message)
        val currency = extractCurrency(message)

        var merchant : String = ""
        when (type) {
            PURCHASE -> {
                merchant = extractMerchantName (message)
            }
        }
        /*
        *   Since we can only obtain information from SMS message right now,
        *       we don't have all the information;
        *       we need user intervention to provide additional information,
        *       and since at this point Transaction will not be recorded;
        *       we'll leave some fields empty for now.
        * */
        val transaction = Transaction (
            id = 0, type = type, amount = amount, date = date, merchant = merchant, currency = currency,
            priority = "", purpose = "", userExtraInfo = "")

        /*
        *   Display a notification to user,
        *       so they can choose to record this transaction or not
        *       and have the choice to provide additional information.
        * */
        sendNotification (context, transaction)
    }
}


/**
 *  Displays notification to user with transaction details
 *  @param context Object of application/activity context
 *  @param transaction Object of Transaction containing transaction details
 */
fun sendNotification (context : Context, transaction: Transaction){
    var notificationMessage = ""
    when (transaction.type) {

        /*
        *   If transaction is of type Payment, get proper message
        * */
        PURCHASE -> {
            if (transaction.amount != AMOUNT_NOT_FOUND) {
                /*
                *   If amount is available, embed in notification message
                * */
                notificationMessage = context.getString(
                    R.string.purchase_detected_notification,
                    transaction.amount
                )
            } else {
                /*
                *   Otherwise show general message without price
                * */
                notificationMessage = context.getString(
                    R.string.purchase_detected_notification_no_amount
                )
            }
        }

        /*
        *   If transaction is of type Transfer, get proper message
        * */
        TRANSFER -> {
            if (transaction.amount != AMOUNT_NOT_FOUND) {
                /*
                *   If amount is available, embed in notification message
                * */
                notificationMessage = context.getString(
                    R.string.transfer_detected_notification,
                    transaction.amount
                )
            } else {
                /*
                *   Otherwise show general message without price
                * */
                notificationMessage = context.getString(
                    R.string.transfer_detected_notification_no_amount
                )
            }
        }


        /*
        *   If transaction is of type Salary, get proper message
        * */
        SALARY -> notificationMessage = context.getString(R.string.salary_detected_notification)
    }

    if (!notificationMessage.isEmpty()) {
        showNotification(context, notificationMessage, transaction)
    }
}

/**
 * Checks weather the provided string has any indication the message is coming from a Bank.
 * @param msg Content of the SMS message
 * @return true if message is from a Bank, false otherwise.
 * */
fun isBank (message : String) : Boolean {
    /*
    *   List of possible words that could indicate
    *            this message is coming from a Bank
    * */
    val possibleWords = arrayOf("دفع", "شراء", "صادرة", "راتب")
    /*
    *   Split message string to an array of words to search for possibleWords
    * */
    val msgWords = message.toLowerCase(Locale.US).split("\\s+".toRegex())
    /*
    *   Iterate through the words, if they contain any of possibleWords, return True otherwise False.
    * */
    return msgWords.any { it in possibleWords }
}


/**
 * Extracts price from a given string.
 * @param msg Content of the SMS message
 * @return Price if found, Null otherwise
 * */
@Nullable
fun extractPrice (msg : String) : Double {
    /*
    *   Define a regex pattern for prices
    *   Pattern will detect all shapes such as:
    *   5.00
    *   1,000
    *   1,000,000.99
    *   5,99 (european price)
    *   5.999,99 (european price)
    *   0.11
    *   0.00
    * */
    val pattern = "\\d{1,3}(?:[.,]\\d{3})*(?:[.,]\\d{2})".toRegex()

    /*
    *   Search for pattern, and return value if available.
    * */
    val result = pattern.find(msg)
    if (result != null){
        return result.value.replace(",", "").toDouble()
    }

    return AMOUNT_NOT_FOUND
}


/**
 *  Extracts merchant name from a Bank SMS message
 *  @param message Content of the SMS message
 *  @return Merchant name, or empty if not found
 */
fun extractMerchantName (message : String) : String {
    /*
    *   Define a regex patter for English sentences
    *       it'll also match '~' character found in SMS messages
    * */
    val pattern = "[a-zA-Z][a-zA-Z\\s\\~]+".toRegex()
    // Search for pattern in message
    val result = pattern.find(message)
    if (result != null){
        // Get value extracted by regex pattern
        val value = result.value.trim()
        // Iterate through all currencies to avoid extracting amount currency
        val isCurrency = getAllCurrenciesCodes().any { it == value }
        // If extracted value isn't a currency, return merchant name
        if (!isCurrency){
            return value
        }
    }
    // return null if merchant name couldn't be found in SMS message
    return ""
}






/**
 *  Extracts date from Bank SMS message
 * @param message Content of the SMS message
 * @return Date as timestamp extracted from message, or current date
 * */
fun extractDate (message : String) : Long {
    /*
    *   Define regex pattern for date (yyyy-MM-dd H:M (optional :ss))
    * */
    val pattern = "\\d+\\-\\d+\\-\\d+\\s\\d+\\:\\d+\\:?\\d\\d?".toRegex()
    // Search for pattern in message
    val result = pattern.find(message)
    if (result != null){
        // Get extracted value
        val value = result.value.trim()
        // Check if date contains seconds, to parse the result properly
        val colonsCount = value.count { it == ':'}
        val datePattern : String
        if (colonsCount == 2){
            // If colons (:) count == 2, then date contains seconds
            datePattern = "yyyy-MM-dd HH:mm:ss"
        } else {
            // If count is less, then it only contains hours and minutes
            datePattern = "yyyy-MM-dd HH:mm"
        }
        val date = SimpleDateFormat(datePattern, Locale.US).parse(value)
        return date?.time?: Date().time
    }

    return Date().time
}




/**
 * Extracts currency from a given string
 * @param message Content of the SMS message
 * @return Currency code, or null if not found
 * */
fun extractCurrency (message : String) : String {
    // Split message string into words array
    val wordsInMessage = message.split(" ")
    // Iterate through all words, and look for matches in currencies
    val currency = wordsInMessage.filter { it in getAllCurrenciesCodes() }
    // Return currency if found
    if (currency.size  == 1){
        return currency.first()
    }
    // Return default otherwise
    return DEFAULT_CURRENCY
}



/*
*   Returns all currencies available
* */
fun getAllCurrenciesCodes(): MutableList<String> {
    val toret: MutableList<String> = mutableListOf()
    val locs = Locale.getAvailableLocales()
    for (loc in locs) {
        try {
            val currency = Currency.getInstance(loc)
            if (currency != null) {
                toret.add(currency.currencyCode)
            }
        } catch (exc: Exception) {
        }
    }
    return toret
}


/**
 * Detects type of Bank transaction
 * @param message Content of the SMS message
 * @return Type of Bank transaction ("TRANSFER", "PURCHASE", "SALARY", "NONE")
  */
fun detectTransactionType (message : String) : String {

    if (message.contains("راتب") || message.contains("الراتب")){
        return SALARY
    }

    if (message.contains("حوالة")){
        return TRANSFER
    }

    if (message.contains("دفع") ||  message.contains("شراء")){
        return PURCHASE
    }

    return NONE
}


/**
 *  Checks the availability of SMS permission
 *  @param context Activity context reference
 *  @return True if permission is granted. False otherwise.
 * */
fun isSmsPermissionGranted (context: Activity) : Boolean {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
            &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
}

/**
 * Performs SMS permission request
 * @param context Activity context reference
 * @param request_code Request code to handle the result of the request in onRequestPermissionsResult
 * */
fun requestSmsPermissions (context : Activity, request_code: Int){
    ActivityCompat.requestPermissions(context,
        arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS),
        request_code)
}