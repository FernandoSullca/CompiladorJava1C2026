package lyc.compiler.simboleTable;

import java.util.LinkedHashMap;

// Esta clase SymbolTable está separada de la clase Symbol_lyc porque cumplen roles diferentes:
// Symbol_lyc representa una única entrada o símbolo de la tabla (su nombre, tipo, valor y longitud),
// mientras que SymbolTable es la estructura que administra el conjunto de esos símbolos, es decir, la propia tabla de símbolos.
public class SymbolTable {

    private static SymbolTable symt;
    private LinkedHashMap<String, Symbol_lyc> table;
    // El texto literal de una constante (STRING, INT o FLOAT) puede tener espacios,
    // puntos o signos invalidos para un identificador de TASM (ej: "Peso inferido",
    // "9999.99", "-10"), asi que a cada literal se le asigna un nombre generado
    // (_CTE0, _CTE1, ...) en vez de usar el propio texto como label.
    private LinkedHashMap<String, String> constantNames;
    private int constantCounter;

    private SymbolTable() {
        // LinkedHashMap mantiene el orden de insercion, util para depurar la salida.
        table = new LinkedHashMap<>();
        constantNames = new LinkedHashMap<>();
        constantCounter = 0;
    }

    public static SymbolTable getSymbolTable(){
        if (symt == null){
            symt = new SymbolTable();
        }
        return symt;
    }

    public void insert(String name,String type, String value, boolean isID){
        if(!isID){
            // El texto literal (name) puede tener espacios, puntos o signos invalidos
            // para un label de TASM: se reemplaza por un nombre generado, reutilizando
            // el mismo si el literal ya fue declarado antes.
            String generated = constantNames.get(name);
            if (generated == null) {
                generated = "_CTE" + (constantCounter++);
                constantNames.put(name, generated);
            }
            name = generated;
            type = "CTE_" + type;
        }
        table.put(name,new Symbol_lyc(name,value,type));
    }

    // Dado el texto literal original de una constante, devuelve el nombre de label
    // generado para ella (o null si no fue declarada como tal).
    public String getConstantName(String literalValue) {
        return constantNames.get(literalValue);
    }

    public Symbol_lyc get(String name){
        return table.get(name);
    }

    public String getType(String name){
        return exists(name)?table.get(name).type:"Undefined";
    }

    public boolean exists(String name){
        return table.containsKey(name);
    }

    @Override
    public String toString() {
        String inicio = "┌" + "─".repeat(50) + "┬" + "─".repeat(10) + "┬" + "─".repeat(50) + "┬" + "─".repeat(10) + "┐\n";
        String separador = "├" + "─".repeat(50) + "┼" + "─".repeat(10) + "┼" + "─".repeat(50) + "┼" + "─".repeat(10) + "┤\n";
        String fin = "└" + "─".repeat(50) + "┴" + "─".repeat(10) + "┴" + "─".repeat(50) + "┴" + "─".repeat(10) + "┘\n";
        String format = "│%-50s│%-10s│%-50s│%-10s│";

        StringBuilder out = new StringBuilder();
        out.append(inicio);
        out.append(String.format(format, "NAME", "TYPE", "VALUE", "LENGTH")).append("\n");
        out.append(separador);

        for (Symbol_lyc symbol : table.values()) {//Fix de armado de tabla cambiado por agregador de atributos "manual"
            String value = symbol.getValue() == null ? "" : symbol.getValue();
            String type = symbol.getType() == null ? "" : symbol.getType();
            String length = (type.equalsIgnoreCase("STRING") || type.equalsIgnoreCase("CTE_STRING"))
                    ? String.valueOf(symbol.getLength())
                    : "";

            out.append(String.format(format, symbol.getName(), type, value, length)).append("\n");
            out.append(separador);
        }

        if (!table.isEmpty()) {
            out.setLength(out.length() - separador.length());
        }
        out.append(fin);

        return out.toString();
    }

    public LinkedHashMap<String, Symbol_lyc> getTable() {
        return table;
    }
}
