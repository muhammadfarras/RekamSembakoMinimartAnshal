package com.example.rekamsembakominimartanshal.mail

import java.util.*

class CustomEmail (var messageBody : String) {

    fun sendEmail (){

        val properties : Properties = System.getProperties()
        properties.put("mail.smtps.host","smtp.gmail.com")
    }
}