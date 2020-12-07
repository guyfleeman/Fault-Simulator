package com.willspants.podem

data class PodemGate(val type: String, val inputs: List<Int>, val output: Int) {
    fun getControllingValue(): Boolean {
        return when (type) {
            "AND", "NAND" -> {
                true
            }
            else -> {
                false
            }
        }
    }

    fun getControllingPodemValue(): PodemValue {
        return when (type) {
            "AND", "NAND" -> {
                PodemValue.FALSE
            }
            else -> {
                PodemValue.TRUE
            }
        }
    }

    fun getInversionPodemValue(): PodemValue {
        return when (type) {
            "NAND", "NOR" -> {
                PodemValue.TRUE
            }
            else -> {
                PodemValue.FALSE
            }
        }
    }
}