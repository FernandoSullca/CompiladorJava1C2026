package lyc.compiler.simboleTable;

import lyc.compiler.simboleTable.Symbol_lyc;
import java.util.LinkedHashMap;

// Esta clase SymbolTable está separada de la clase Symbol_lyc porque cumplen roles diferentes:
// Symbol_lyc representa una única entrada o símbolo de la tabla (su nombre, tipo, valor y longitud),
// mientras que SymbolTable es la estructura que administra el conjunto de esos símbolos, es decir, la propia tabla de símbolos.
public class SymbolTable {

    private static SymbolTable symt;
    private LinkedHashMap<String, Symbol_lyc> table;

    private SymbolTable() {
        // LinkedHashMap mantiene el orden de insercion, util para depurar la salida.
        table = new LinkedHashMap<>();
    }

    public static SymbolTable getSymbolTable(){
        if (symt == null){
            symt = new SymbolTable();
        }
        return symt;
    }

    public void insert(String name,String type, String value, boolean isID){
        if(!isID){
            //constantes empiezan con _ para diferenciar de las IDs
            name = "_" + name;
            type = "CTE_" + type;
        }
        table.put(name,new Symbol_lyc(name,value,type));
    }

    public Symbol_lyc get(String name){
        return table.get(name);
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

}
