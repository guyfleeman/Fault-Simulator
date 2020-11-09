package com.willspants.synth

enum class NetFaultType {
    NONE,
    S_A_0,
    S_A_1
}

fun invertNetFaultType(type: NetFaultType): NetFaultType {
    if (type == NetFaultType.S_A_0) {
        return NetFaultType.S_A_1
    }

    if (type == NetFaultType.S_A_1) {
        return NetFaultType.S_A_0
    }

    return NetFaultType.NONE
}

fun netFaultMatchesControlling(nFault: NetFaultType, cVal: Boolean): Boolean {
    if (nFault == NetFaultType.S_A_0 && !cVal) {
        return true
    }

    if (nFault == NetFaultType.S_A_1 && cVal) {
        return true
    }

    return false
}

fun boolToNetFaultType(bv: Boolean): NetFaultType {
    return if (bv) {
        NetFaultType.S_A_1
    } else {
        NetFaultType.S_A_0
    }
}