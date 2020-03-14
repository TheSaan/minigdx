package com.github.dwursteisen.minigdx.logger

import java.util.logging.Level
import java.util.logging.LogManager

typealias LambdaMessage = () -> String

class JavaLoggingLogger : Logger {

    private val log = LogManager.getLogManager().getLogger("minigdx")

    private fun LambdaMessage.withTag(tag: String): LambdaMessage {
        return { -> "[$tag] ${this.invoke()}" }
    }

    override fun debug(tag: String, message: () -> String) {
        if (rootLevel.ordinal <= Logger.LogLevel.DEBUG.ordinal) {
            log.log(Level.FINEST, message.withTag(tag))
        }
    }

    override fun debug(tag: String, exception: Throwable, message: () -> String) {
        if (rootLevel.ordinal <= Logger.LogLevel.DEBUG.ordinal) {
            log.log(Level.FINEST, exception, message.withTag(tag))
        }
    }

    override fun info(tag: String, message: () -> String) {
        if (rootLevel.ordinal <= Logger.LogLevel.INFO.ordinal) {
            log.log(Level.INFO, message.withTag(tag))
        }
    }

    override fun info(tag: String, exception: Throwable, message: () -> String) {
        if (rootLevel.ordinal <= Logger.LogLevel.INFO.ordinal) {
            log.log(Level.INFO, exception, message.withTag(tag))
        }
    }

    override fun warn(tag: String, message: () -> String) {
        if (rootLevel.ordinal <= Logger.LogLevel.WARN.ordinal) {
            log.log(Level.WARNING, message.withTag(tag))
        }
    }

    override fun warn(tag: String, exception: Throwable, message: () -> String) {
        if (rootLevel.ordinal <= Logger.LogLevel.WARN.ordinal) {
            log.log(Level.WARNING, exception, message.withTag(tag))
        }
    }

    override fun error(tag: String, message: () -> String) {
        if (rootLevel.ordinal <= Logger.LogLevel.ERROR.ordinal) {
            log.log(Level.SEVERE, message.withTag(tag))
        }
    }

    override fun error(tag: String, exception: Throwable, message: () -> String) {
        if (rootLevel.ordinal <= Logger.LogLevel.ERROR.ordinal) {
            log.log(Level.SEVERE, exception, message.withTag(tag))
        }
    }

    override var rootLevel: Logger.LogLevel = Logger.LogLevel.INFO
        set(value) {
            field = value
            val javaUtilsLog = when (value) {
                Logger.LogLevel.DEBUG -> Level.FINEST
                Logger.LogLevel.INFO -> Level.INFO
                Logger.LogLevel.WARN -> Level.WARNING
                Logger.LogLevel.ERROR -> Level.SEVERE
            }
            log.level = javaUtilsLog
        }
}
