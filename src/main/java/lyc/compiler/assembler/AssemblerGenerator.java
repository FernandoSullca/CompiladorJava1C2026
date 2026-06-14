package lyc.compiler.assembler;

import lyc.compiler.files.FileGenerator;
import lyc.compiler.simboleTable.SymbolTable;

import java.io.FileWriter;
import java.io.IOException;
import lyc.compiler.intermediateCode.Polaca;
import lyc.compiler.simboleTable.SymbolTable;

public class AssemblerGenerator {

    private static AssemblerGenerator symt;

    private final Polaca polaca;
    private final SymbolTable symbolTable;

    public AssemblerGenerator() {
        this.polaca = Polaca.getInstance();
        this.symbolTable =  SymbolTable.getSymbolTable();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(generateHeader());
        sb.append(generateData());
        sb.append(generateCode());
        sb.append(generateFooter());
        return sb.toString();
    }

    public static AssemblerGenerator getGenerator(){
        if (symt == null){
            symt = new AssemblerGenerator();
        }
        return symt;
    }

    //Esqueleto de archivo assembler

    // Cabeceras fijas del .asm
    private String generateHeader() {
        return ".MODEL LARGE\n"
                + ".386\n"
                + ".STACK 200h\n\n";
    }

    // Seccion .DATA con variables de la tabla de simbolos
    private String generateData() {
        StringBuilder sb = new StringBuilder();
        sb.append(".DATA\n");

        // TODO: iterar tabla de simbolos y generar las declaraciones
        // Por ahora placeholder vacio
        sb.append("   TODO ; variables generadas desde tabla de simbolos\n");

        sb.append("\n");
        return sb.toString();
    }

    // Seccion .CODE con la traduccion de la polaca
    private String generateCode() {
        StringBuilder sb = new StringBuilder();
        sb.append(".CODE\n\n");
        sb.append("START:\n");
        sb.append("    mov AX, @DATA\n");
        sb.append("    mov DS, AX\n");
        sb.append("    mov es, ax\n\n");

        // TODO: traduccion de polaca a instrucciones assembler
        sb.append("TODO    ; codigo generado desde polaca inversa\n");

        sb.append("\n");
        return sb.toString();
    }

    // Pie fijo del .asm
    private String generateFooter() {
        return "    mov ax, 4C00h\n"
                + "    int 21h\n"
                + "END START\n";
    }

}
