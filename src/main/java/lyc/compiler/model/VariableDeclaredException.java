package lyc.compiler.model;

import java.io.Serial;

public class VariableDeclaredException  extends CompilerException{

    @Serial
    private static final long serialVersionUID = -8839023592847976072L;

    public VariableDeclaredException(String message) {
        super(message);
    }
}
