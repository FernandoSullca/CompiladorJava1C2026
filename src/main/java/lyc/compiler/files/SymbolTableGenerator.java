package lyc.compiler.files;

import lyc.compiler.simboleTable.SymbolTable;

import java.io.FileWriter;
import java.io.IOException;

public class SymbolTableGenerator implements FileGenerator{

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        fileWriter.write(SymbolTable.getSymbolTable().toString());;
    }
}
