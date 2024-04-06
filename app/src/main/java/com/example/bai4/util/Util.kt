package com.example.bai4.util

import java.util.Calendar

/**
 * This function is used to set time is 00:00:00:000
 */
fun Calendar.setDefaultTime() {
    this.set(Calendar.HOUR_OF_DAY, 0)
    this.set(Calendar.MINUTE, 0)
    this.set(Calendar.SECOND, 0)
    this.set(Calendar.MILLISECOND, 0)
}