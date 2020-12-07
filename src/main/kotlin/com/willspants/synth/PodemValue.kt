package com.willspants.synth

enum class PodemValue {
    L_TRUE,
    L_FALSE,
    F_D,
    F_D_BAR,
    UNSET,
    UNKNOWN
}

fun podemValueIsFault(pVal: PodemValue): Boolean {
    return pVal == PodemValue.F_D || pVal == PodemValue.F_D_BAR
}

fun invertPodemValueWeak(pVal: PodemValue): PodemValue {
    if (pVal == PodemValue.L_TRUE) {
        return PodemValue.L_FALSE
    }

    if (pVal == PodemValue.L_FALSE) {
        return PodemValue.L_TRUE
    }

    if (pVal == PodemValue.F_D) {
        return PodemValue.F_D_BAR
    }

    if (pVal == PodemValue.F_D_BAR) {
        return PodemValue.F_D
    }

    return pVal
}

fun xorPodemValue(left: PodemValue, right: PodemValue): PodemValue {
    if (left == PodemValue.L_TRUE && right == PodemValue.L_FALSE) {
        return PodemValue.L_TRUE
    }

    if (left == PodemValue.L_FALSE && right == PodemValue.L_TRUE) {
        return PodemValue.L_TRUE
    }

    return PodemValue.L_FALSE
}

fun podemValueToBoolean(pVal: PodemValue): Boolean {
    if (pVal == PodemValue.L_TRUE) {
        return true
    }

    return false
}