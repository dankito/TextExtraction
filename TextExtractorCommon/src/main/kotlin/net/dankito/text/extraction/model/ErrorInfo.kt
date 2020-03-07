package net.dankito.text.extraction.model

import java.lang.Exception


open class ErrorInfo(val type: ErrorType, val exception: Exception? = null) {

    override fun toString(): String {
        if (exception != null) {
            return "$type: $exception"
        }

        return "$type"
    }

}