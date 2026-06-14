package lyc.compiler.files;

import lyc.compiler.assembler.AssemblerGenerator;

import java.io.FileWriter;
import java.io.IOException;

public class AsmCodeGenerator implements FileGenerator {

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        fileWriter.write(AssemblerGenerator.getGenerator().toString());
    }
}
