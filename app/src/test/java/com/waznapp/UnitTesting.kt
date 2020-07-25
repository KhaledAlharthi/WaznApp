package com.wazn

import com.waznapp.*
import com.waznapp.Utilities.*
import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class UnitTesting {


    var DELTA = 0.0

    @Test
    fun isBankTest () {
        assertEquals (true, isBank(CASE_1_MESSAGE))
        assertEquals (true, isBank(CASE_2_MESSAGE))
        assertEquals (true, isBank(CASE_3_MESSAGE))
        assertEquals (true, isBank(CASE_6_MESSAGE))
        assertEquals (false, isBank(CASE_4_MESSAGE))
        assertEquals (false, isBank(CASE_5_MESSAGE))
    }

    @Test
    fun extractPriceTest (){
        assertEquals(1.00, extractPrice(CASE_1_MESSAGE), DELTA)
        assertEquals(223.55, extractPrice(CASE_2_MESSAGE), DELTA)
        assertEquals(16.00, extractPrice(CASE_3_MESSAGE), DELTA)
        assertEquals(AMOUNT_NOT_FOUND, extractPrice(CASE_5_MESSAGE), DELTA)
        assertEquals(10175.00, extractPrice(CASE_6_MESSAGE), DELTA)
        assertEquals(10175400.00, extractPrice(CASE_7_MESSAGE), DELTA)
    }


    @Test
    fun detectTransactionTypeTest (){

        /**
         * Real SMS message of type PURCHASE
         */
        assertEquals(PURCHASE, detectTransactionType(CASE_1_MESSAGE))

        /**
         * Real SMS message of type TRANSFER
         */
        assertEquals(TRANSFER, detectTransactionType(CASE_2_MESSAGE))

        /**
         * Real SMS message of type PURCHASE
         */
        assertEquals(PURCHASE, detectTransactionType(CASE_3_MESSAGE))

        /**
         * Real SMS message of type NONE
         */
        assertEquals(NONE, detectTransactionType(CASE_5_MESSAGE))

        /*
        * SMS message of type Salary
        * */
        assertEquals(SALARY, detectTransactionType(CASE_6_MESSAGE))

        /*
        * SMS message of type Salary
        * */
        assertEquals(SALARY, detectTransactionType(CASE_8_MESSAGE))



    }


    @Test
    fun extractMerchantTest (){
        assertEquals("STC Pay", extractMerchantName(CASE_1_MESSAGE))


        assertEquals("", extractMerchantName(CASE_2_MESSAGE))

        assertEquals("Rabie Al Safwa Est~Al", extractMerchantName(CASE_3_MESSAGE))
    }


    @Test
    fun extractDateTest(){
        /**
         * no seconds
         */
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2020-07-15 21:56").time
        assertEquals(timestamp, extractDate(CASE_1_MESSAGE))

        /**
         * with seconds
         */
        val timestamp2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-06-24 10:19:58").time
        assertEquals(timestamp2, extractDate(CASE_3_MESSAGE))

        /**
         * no date at all
         */
        assertEquals(Date().time, extractDate(CASE_5_MESSAGE))


    }


    @Test
    fun extractCurrencyTest (){
        /**
         * CASE 1 SAR currency
         */
        assertEquals("SAR", extractCurrency(CASE_1_MESSAGE))

        /**
         * CASE 2 USD currency
         */
        assertEquals("USD", extractCurrency(CASE_9_MESSAGE))


        assertEquals(DEFAULT_CURRENCY, extractCurrency(CASE_3_MESSAGE))
    }
}