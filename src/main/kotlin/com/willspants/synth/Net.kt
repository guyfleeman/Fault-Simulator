package com.willspants.synth

import java.lang.RuntimeException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class Net {
    private val _metaFileLineReferences: ArrayList<Int> = ArrayList()

    private val index: Int
    private var name: String
    private lateinit var driver: Pin
    private val sinks: HashSet<Pin> = HashSet()

    private var isValid: Boolean = false
    private var value: Boolean = false
    var podemImpliedValueValid = false
    var podemImpliedValue: PodemValue = PodemValue.UNSET
    private var fault: NetFaultType = NetFaultType.NONE
    private val localFaults: MutableSet<Fault> = HashSet()
    private val allFaults: MutableSet<Fault> = HashSet()

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

    fun setFaultValue(fault: NetFaultType) {
        this.fault = fault
    }

    fun getFaultValue(): NetFaultType {
        return fault
    }

    fun propogate(value: Boolean) {
        when (fault) {
            NetFaultType.S_A_0 -> {
                this.value = false
            }
            NetFaultType.S_A_1 -> {
                this.value = true
            }
            else -> {
                this.value = value
            }
        }

        this.isValid = true;

        getSinks().forEach {
            it.booleanValue = this.value
            it.isValid = true
        }
    }

    fun podemPropagate() {
        getSinks().forEach {
            it.podemValue = podemImpliedValue
        }
    }

    fun getNetValue(): Boolean {
        if (fault == NetFaultType.S_A_0) {
            return false
        }

        if (fault == NetFaultType.S_A_1) {
            return true
        }

        return this.value
    }

    fun getIndex(): Int {
        return index
    }

    fun getAllFaults(): MutableSet<Fault> {
        return allFaults
    }

    fun addFault(fault: Fault) {
        if (fault.sensitized(this)) {
            allFaults.add(fault)
        }
    }

    fun isControlling(type: GateControllingType): Boolean {
        if (type == GateControllingType.ALL) {
            return true
        }

        if (getNetValue() && type == GateControllingType.ONE) {
            return true
        }

        if (!getNetValue() && type == GateControllingType.ZERO) {
            return true
        }

        return false
    }

    fun isFaultControlling(type: GateControllingType): Boolean {
        return !isControlling(type)
    }

    override fun toString(): String {
        return "$name: $value ($isValid) - $allFaults"
    }

    fun toSimpleFaultString(): String {
        return if (allFaults.isEmpty()) {
            "$name is fault free"
        } else {
            "$name suck at ${allFaults.map{ it.getSimpleString() }}".padStart(32, ' ')
        }
    }
}