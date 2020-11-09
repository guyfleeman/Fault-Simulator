package com.willspants.synth

class Fault(val netIndex: Int, val faultType: NetFaultType) {
    companion object {
        fun createFromGate(gate: Gate, hasControllingInputs: Boolean = false): Fault {
            val dsNet = gate.getDrivePin().net
            val controllingValue: Boolean = when (gate.gateFunction) {
                GateFunction.INV -> {
                    return Fault(dsNet.getIndex(), boolToNetFaultType(!dsNet.getNetValue()))
                }
                GateFunction.BUF -> {
                    return Fault(dsNet.getIndex(), boolToNetFaultType(!dsNet.getNetValue()))
                }
                GateFunction.NAND, GateFunction.AND, GateFunction.NOR, GateFunction.OR -> {
                    gate.getControlling() == GateControllingType.ONE
                }
                GateFunction.XNOR, GateFunction.XOR -> {
                    false
                }
            }

            val inversionValue = gate.getInversion()
            return Fault(dsNet.getIndex(), boolToNetFaultType(!dsNet.getNetValue()))
            /*
            return if (hasControllingInputs) {
                Fault(dsNet.getIndex(), boolToNetFaultType(!controllingValue xor inversionValue))
            } else {
                Fault(dsNet.getIndex(), boolToNetFaultType(controllingValue xor inversionValue))
            }
            */

        }
    }

    fun sensitized(net: Net): Boolean {
        if (faultType == NetFaultType.S_A_0 && net.getNetValue() == false) {
            return false
        }

        if (faultType == NetFaultType.S_A_1 && net.getNetValue() == true) {
            return false
        }

        return true
    }

    fun sensitized(bVal: Boolean): Boolean {
        if (faultType == NetFaultType.S_A_0 && bVal == false) {
            return false
        }

        if (faultType == NetFaultType.S_A_1 && bVal == true) {
            return false
        }

        return true
    }

    override fun toString(): String {
        return "NET: $netIndex stuck at value: $faultType"
    }
}