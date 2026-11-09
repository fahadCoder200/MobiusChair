import chisel3._
import chisel3.util._

// The Control Signal Bundle passed to the ALU
class AluControl extends Bundle {
  val aluOp = UInt(4.W) // property that can be anything from the AluOp object
}

object AluOp {
  val ADD = 0.U(4.W)
  val SUB = 1.U(4.W)
  val SHIFT_LEFT_LOGICAL = 2.U(4.W)
  val SET_IF_LESS_THAN_SIGNED = 3.U(4.W)
  val SET_IF_LESS_THAN_UNSIGNED = 4.U(4.W)
  val XOR = 5.U(4.W)
  val SHIFT_RIGHT_LOGICAL = 6.U(4.W)
  val SHIFT_RIGHT_ARITHMETIC = 7.U(4.W)
  val OR = 8.U(4.W)
  val AND = 9.U(4.W)
}

class Alu extends Module {
  val io = IO(new Bundle {
    val in_a    = Input(UInt(32.W))
    val in_b    = Input(UInt(32.W))
    val ctrl    = Input(new AluControl()) // Control signals received
    val result  = Output(UInt(32.W))
  })

  // Set default output
  io.result := 0.U

  val signed_in_a = io.in_a.asSInt  // Everything inside the ALU is treated as signed
  val signed_in_b = io.in_b.asSInt

  // Decode control signal to select operation
  switch(io.ctrl.aluOp) {
      is(AluOp.ADD)                       { io.result := (signed_in_a + signed_in_b).asUInt } // We casted back into Unsigned Int because UInt in Chisel is just bitvectors representing physical wires, the register file and MainCore also except input as UInt, Signed Integer is just used for arithmetic to generate adders using two's complement

      is(AluOp.SUB)                       { io.result := (signed_in_a - signed_in_b).asUInt }
      is(AluOp.SHIFT_LEFT_LOGICAL)        { io.result := (signed_in_a << signed_in_b(4, 0)).asUInt }
      is(AluOp.SET_IF_LESS_THAN_SIGNED)   { io.result := Mux(signed_in_a < signed_in_b, 1.U, 0.U) }
      is(AluOp.SET_IF_LESS_THAN_UNSIGNED) { io.result := Mux(io.in_a < io.in_b, 1.U, 0.U) }
      is(AluOp.XOR)                       { io.result := (signed_in_a ^ signed_in_b).asUInt }
      is(AluOp.SHIFT_RIGHT_LOGICAL)       { io.result := (io.in_a >> io.in_b(4, 0)) } // requires unsigned integers, fills the other bits with zero
      is(AluOp.SHIFT_RIGHT_ARITHMETIC)    { io.result := (signed_in_a >> io.in_b(4, 0)).asUInt } // requires first input to be signed
      is(AluOp.OR)                        { io.result := (signed_in_a | signed_in_b).asUInt }
      is(AluOp.AND)                       { io.result := (signed_in_a & signed_in_b).asUInt }
  }
}