package com.willspants.synth

import java.lang.RuntimeException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class Net {
    private val _metaFileLineReferences: ArrayList<Int> = ArrayList()

    var curBooleanVal: Boolean = false

    private val index: Int
    private var name: String
    private lateinit var driver: Pin
    private val sinks: HashSet<Pin> = HashSet()

    constructor(index: Int, name: String = "NET_$index", driver: Pin) {
        this.index = index
        this.name = name

        this.driver = driver

        this.sinks.addAll(sinks)
    }

    constructor(index: Int, name: String = "NET_$index") {
        this.index = index
        this.name = name

        this.sinks.addAll(sinks)
    }

    fun getName(): String {
        return name
    }

    fun _metaAddLineReference(lineNo: Int) {
        _metaFileLineReferences.add(lineNo)
    }

    fun getLineReferencesDebugString(): String {
        return Arrays.toString(_metaFileLineReferences.toArray())
    }

    fun hasDriver(): Boolean {
        return this::driver.isInitialized
    }

    fun getDriver(): Pin {
        if (!hasDriver()) {
            throw RuntimeException("illegal access of invalid driver")
        }

        return driver
    }

    fun setDriver(driver: Pin) {
        this.driver = driver
    }

    fun hasSink(): Boolean {
        return sinks.isNotEmpty()
    }

    fun getSinks(): HashSet<Pin> {
        return sinks
    }

    fun addSink(sink: Pin) {
        this.sinks.add(sink)
    }

    fun hasValidConfiguration(): Boolean {
        return hasDriver() && hasSink()
    }
}