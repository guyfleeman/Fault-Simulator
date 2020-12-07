package com.willspants.podem

enum class PodemValue() {
    TRUE,
    FALSE,
    D,
    D_BAR,
    X;

    companion object {
        fun fromSensitizedSav(sav: Boolean): PodemValue {
            if (sav) {
                return D
            }

            return D_BAR
        }

        fun fromBoolean(bVal: Boolean): PodemValue {
            if (bVal) {
                return TRUE
            }

            return FALSE
        }
    }

    fun unknown(): Boolean {
        return this == X
    }

    fun needsEvaluation(): Boolean {
        return this == D || this == D_BAR || this == X
    }

    fun isFault(): Boolean {
        return this == D || this == D_BAR
    }

    fun isBoolean(): Boolean {
        return this == TRUE || this == FALSE
    }

    fun getBoolean(): Boolean {
        return this == TRUE
    }

    infix fun and(pVal: PodemValue): PodemValue {
        // standard propagation
        if (this.isBoolean() && pVal.isBoolean()) {
            return fromBoolean(this.getBoolean() && pVal.getBoolean())
        }

        // we have a controlling value
        if (this == FALSE || pVal == FALSE) {
            return FALSE
        }

        // have one or more unknown with non-controlling
        if (this == X || pVal == X) {
            return X
        }

        // either or both values are fault, but they're not opposites
        if ((this.isFault() || pVal.isFault()) && this != !pVal) {
            return if (this.isFault()) {
                this
            } else {
                pVal
            }
        }

        // we have two faults, and theyre opposites
        return FALSE
    }

    infix fun nand(pVal: PodemValue): PodemValue {
        return !and(pVal)
    }

    infix fun or(pVal: PodemValue): PodemValue {
        // standard propagation
        if (this.isBoolean() && pVal.isBoolean()) {
            return fromBoolean(this.getBoolean() || pVal.getBoolean())
        }

        // we have a controlling value
        if (this == TRUE || pVal == TRUE) {
            return TRUE
        }

        // have one or more unknown with non-controlling
        if (this == X || pVal == X) {
            return X
        }

        // either or both values are fault, but they're not opposites
        if ((this.isFault() || pVal.isFault()) && this != !pVal) {
            return if (this.isFault()) {
                this
            } else {
                pVal
            }
        }

        return TRUE
    }

    infix fun nor(pVal: PodemValue): PodemValue {
        return !or(pVal)
    }

    infix fun xor(pVal: PodemValue): PodemValue {
        if ((this == FALSE && pVal == TRUE) || (this == TRUE && pVal == FALSE)) {
            return TRUE
        }

        return FALSE
    }
}

// kotlin only has not override sadly
operator fun PodemValue.not(): PodemValue {
    if (this == PodemValue.TRUE) {
        return PodemValue.FALSE
    }

    if (this == PodemValue.FALSE) {
        return PodemValue.TRUE
    }

    if (this == PodemValue.D) {
        return PodemValue.D_BAR
    }

    if (this == PodemValue.D_BAR) {
        return PodemValue.D
    }

    return PodemValue.X
}
