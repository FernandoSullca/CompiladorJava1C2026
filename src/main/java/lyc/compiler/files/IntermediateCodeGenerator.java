package lyc.compiler.files;

import lyc.compiler.intermediateCode.Polaca;

import java.io.FileWriter;
import java.io.IOException;

public class IntermediateCodeGenerator implements FileGenerator {

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        Polaca polaca = Polaca.getInstance();

        // Si la polaca está vacía, lo indicamos
        if (polaca.getPolaca().isEmpty()) {
            fileWriter.write("No intermediate code generated.\n");
            return;
        }

        ;
        fileWriter.write(polaca.toFormattedString());;
    }
}
