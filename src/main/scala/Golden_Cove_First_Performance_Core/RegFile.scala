import chisel3._
import chisel3.util._

class RegisterFile extends Module {
  val io = IO(new Bundle {
    val rs1_addr = Input(UInt(5.W))   // 5 bits for 32 registers (x0-x31)
    val rs2_addr = Input(UInt(5.W))   
    val rd_addr  = Input(UInt(5.W))   // address of register to write to
    val rd_data  = Input(UInt(32.W))  // data to write to the register
    val write_en = Input(Bool())      // write flag
    
    val rs1_data = Output(UInt(32.W)) // register data given out on reads
    val rs2_data = Output(UInt(32.W)) // second register
  })

  // 32 registers, each 32-bits wide initialized to 0
  val regs = RegInit(VecInit(Seq.fill(32)(0.U(32.W))))
  
  // Synchronous Write (RISC-V x0 is hardwired to 0) where =/= stands for !=, the write is synchoronous so the it takes one clock cycle for the write to finish after it is recieved
  when(io.write_en && (io.rd_addr =/= 0.U)) {
    regs(io.rd_addr) := io.rd_data
  }

  // Asynchronous Read Routing: Indexing into the Reg vector routes the data, this generates a hardware multiplexer that checks if the requested register is x1 as stated x1 is hardwired to zero, if not it selects the requested register from the register vector

  io.rs1_data := Mux(io.rs1_addr === 0.U, 0.U, regs(io.rs1_addr))
  io.rs2_data := Mux(io.rs2_addr === 0.U, 0.U, regs(io.rs2_addr))
}