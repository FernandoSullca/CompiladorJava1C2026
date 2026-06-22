package lyc.compiler.assembler;

import lyc.compiler.files.FileGenerator;
import lyc.compiler.simboleTable.SymbolTable;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import lyc.compiler.intermediateCode.Polaca;
import lyc.compiler.simboleTable.SymbolTable;
import lyc.compiler.simboleTable.Symbol_lyc;



public class AssemblerGenerator {

    private static AssemblerGenerator symt;

    List<String> auxVariables;

    private final Polaca polaca;
    private final SymbolTable symbolTable;
    private List<String> pilaSimbolos;
    public AssemblerGenerator() {
        this.polaca = Polaca.getInstance();
        this.symbolTable =  SymbolTable.getSymbolTable();
        this.pilaSimbolos = new ArrayList<>();
        this.auxVariables = new ArrayList<>();
    }



    @Override
    public String toString() {
        String code = generateCode(); // al recorrer la polaca, llena auxVariables
        StringBuilder sb = new StringBuilder();
        sb.append(generateHeader());
        sb.append(generateData());
        sb.append(code);
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
        return "include macros2.asm\n" +
                "include number.asm\n" +
                ".MODEL LARGE\n"
                + ".386\n"
                + ".STACK 200h\n\n";
    }

    // Seccion .DATA con variables de la tabla de simbolos
    private String generateData() {

        StringBuilder sb = new StringBuilder();
        sb.append("MAXTEXTSIZE equ 50\n\n");
        sb.append(".DATA\n");

        //Mimsma logica de la generacion de la tabla pero aplicando 2atributos de assembler para cada tipo de datos
        for (Symbol_lyc symbol : symbolTable.getTable().values()) {
            String value = symbol.getValue() == null ? "" : symbol.getValue();
            String type = symbol.getType() == null ? "" : symbol.getType();

            String name = symbol.getName();

            switch (type.toUpperCase()) {
                case "INT":
                    sb.append("    ").append(name).append("    dd    ?\n");
                    break;

                case "CTE_INT":
                    sb.append("    ").append(name).append("    dd    ").append(value).append("\n");
                    break;

                case "FLOAT":
                    sb.append("    ").append(name).append("    dd    ?\n");
                    break;

                case "CTE_FLOAT":
                    sb.append("    ").append(name).append("    dd    ").append(value).append("\n");
                    break;

                case "STRING":
                    sb.append("    ").append(name).append("    db    MAXTEXTSIZE dup (?), '$'\n");
                    break;

                case "CTE_STRING":
                    int len = symbol.getLength();
                    int padding = 50 - len;
                    sb.append("    ").append(name)
                            .append("    db    \"").append(value).append("\",'$'")
                            .append(padding > 0 ? ", " + padding + " dup (?)" : "")
                            .append("\n");
                    break;
                }
            
                    
        }
        for (String aux : auxVariables) {
            sb.append("    ").append(aux).append("    dd    ?\n");
        }
        sb.append("    ").append("R1").append("    dd    ?\n"); // R1 Tipo int para calculos
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

        ArrayDeque<String> stack = new ArrayDeque<>();

        for (String token : polaca.getPolaca()) {
            switch (token) {
                case "PRINT": {
                    String operand = stack.pop();
                    sb.append(generatePrint(operand));
                    break;
                }
                case "READ": {
                    String operand = stack.pop();
                    sb.append(generateRead(operand));
                    break;
                }

                case "+": case "-": case "*": case "/": {
                    String derecho = stack.pop();
                    String izquierdo = stack.pop();
                    String mnemonico = switch (token) {
                        case "+" -> "ADD";
                        case "-" -> "SUB";
                        case "*" -> "MUL";
                        case "/" -> "DIV";
                        default -> throw new IllegalStateException();

                    };
                    
                    String aux = polaca.generateTemporal();
                    sb.append("    MOV R1, ").append(izquierdo).append("\n");
                    sb.append("    ").append(mnemonico).append(" R1, ").append(derecho).append("\n");
                    sb.append("    MOV ").append(aux).append(", R1\n");
                    auxVariables.add(aux);
                    stack.push(aux);
                    break;
                }

                default:
                    // Por ahora, cualquier otro token (operandos, operadores, etc.)
                    // se apila para uso futuro. Todavia no se procesan +, -, *, /, :=
                    stack.push(token);
                    break;
            }
        }

        sb.append("\n");
        return sb.toString();
    }

    private String resolveName(String token) {
        if (symbolTable.exists(token)) {
            return token;
        }
        if (symbolTable.exists("_" + token)) {
            return "_" + token;
        }
        return token; // fallback, no deberia pasar
    }

    private String resolveType(String token) {
        String name = resolveName(token);
        return symbolTable.exists(name) ? symbolTable.getType(name) : "Undefine";
    }


    private String generatePrint(String operand) {
        String name = resolveName(operand);
        String type = resolveType(operand);
        if (type == null) type = "";

        switch (type.toUpperCase()) {
            case "STRING":
            case "CTE_STRING":
                return "    displayString " + name + "\n";
            case "INT":
            case "CTE_INT":
                return "    DisplayInteger " + name + "\n";
            case "FLOAT":
            case "CTE_FLOAT":
                return "    DisplayFloat " + name + ", 4\n";
            default:
                return "    ; PRINT desconocido para " + name + "\n";
        }
    }

    private String generateRead(String operand) {
        String name = resolveName(operand);
        String type = resolveType(operand);
        if (type == null) type = "";

        switch (type.toUpperCase()) {
            case "INT":
                return "    GetInteger " + name + "\n";
            case "FLOAT":
                return "    GetFloat " + name + "\n";
            default:
                return "    ; READ desconocido para " + name + "\n";
        }
    }


    // Pie fijo del .asm
    private String generateFooter() {
        return "    mov ax, 4C00h\n"
                + "    int 21h\n"
                + "END START\n";
    }

}
