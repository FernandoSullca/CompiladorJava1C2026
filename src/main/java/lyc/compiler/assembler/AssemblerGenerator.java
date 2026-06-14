package lyc.compiler.assembler;

import lyc.compiler.files.FileGenerator;
import lyc.compiler.simboleTable.SymbolTable;

import java.io.FileWriter;
import java.io.IOException;

public class AssemblerGenerator {

    private static AssemblerGenerator symt;

    @Override
    public String toString() {
        return "AssemblerGenerator{}";
    }

    public AssemblerGenerator() {

    }
    public static AssemblerGenerator getGenerator(){
        if (symt == null){
            symt = new AssemblerGenerator();
        }
        return symt;
    }
}
