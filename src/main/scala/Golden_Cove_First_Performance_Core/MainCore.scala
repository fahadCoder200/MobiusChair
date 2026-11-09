import chisel3._
import chisel3.util._
import _root_.circt.stage.ChiselStage


class FetchUnit extends Module {
  val io = IO(new Bundle {
    val address = Input(UInt(32.W)) // From where to start fetching the instructions
  })


}


class CoreTop extends Module {
  val io = IO(new Bundle {
    val instruction = Input(UInt(32.W))
    val outResult   = Output(UInt(32.W))
  })

  // Instantiate units
  val regFile = Module(new RegisterFile())

  regFile.io.rd_data  := 0.U  // Every input must be driven by an electrical signal 
  regFile.io.write_en := false.B

  val alu = Module(new Alu())

  // Getting codes to get the exact registers
  val rd     = io.instruction(11, 7)
  val rs1    = io.instruction(19, 15)

  // --- 2. ROUTE TO REGFILE READ PORTS ---
  regFile.io.rs1_addr := rs1
  regFile.io.rd_addr  := rd

  // --- 3. GENERATE CONTROL SIGNALS FOR ALU ---
  val decoder = Module(new Decoder())

  // Checking if the instruction for the immediate
  when(decoder.io.useImm === false.B){
    val rs2 = io.instruction(24, 20)
    regFile.io.rs2_addr := rs2
    alu.io.in_b := regFile.io.rs2_data


  } .otherwise {
    val rs2 = io.instruction(31, 20)
    regFile.io.rs2_addr := "b00000".U
    alu.io.in_b := io.instruction(31, 20)
  }

  decoder.io.instructionDecoder := io.instruction

  alu.io.in_a := regFile.io.rs1_data // Wire RegFile output directly to ALU input
  alu.io.ctrl := decoder.io.outputForCore           // Drive control wire bundle into ALU

  // Drive Core Output
  io.outResult := alu.io.result
}

object Main {
  def main(args: Array[String]): Unit = {
    ChiselStage.emitSystemVerilogFile(
      new CoreTop(),
      Array("--target-dir", "generated")
    )
  }
}