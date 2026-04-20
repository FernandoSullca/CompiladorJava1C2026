package lyc.compiler.simboleTable;

import lyc.compiler.simboleTable.Symbol_lyc;
import java.util.HashMap;

// Esta clase SymbolTable está separada de la clase Symbol_lyc porque cumplen roles diferentes:
// Symbol_lyc representa una única entrada o símbolo de la tabla (su nombre, tipo, valor y longitud),
// mientras que SymbolTable es la estructura que administra el conjunto de esos símbolos, es decir, la propia tabla de símbolos.
public class SymbolTable {

    private static SymbolTable symt;
    private HashMap<String, Symbol_lyc> table;

    private SymbolTable() {
        table = new HashMap<>();
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
        //literalmente una tabla
        String inicio = "┌"+"─".repeat(50)+"┬"+"─".repeat(10)+"┬"+"─".repeat(50)+"┬"+"─".repeat(10)+"┐"+"\n";
        String format = "│%-50s│%-10s│%-50s│%-10s│";
        String out = inicio + String.format(format,"NAME","TYPE","VALUE","LENGTH") +"\n";
        String separador = "├"+"─".repeat(50)+"┼"+"─".repeat(10)+"┼"+"─".repeat(50)+"┼"+"─".repeat(10)+"┤"+"\n";
        String fin = "└"+"─".repeat(50)+"┴"+"─".repeat(10)+"┴"+"─".repeat(50)+"┴"+"─".repeat(10)+"┘"+"\n";
        out += separador;
        format = "│%-50s│%s│";
        for(String k : table.keySet() ){
            out += String.format(format,k,table.get(k)) + "\n" + separador;
        }
        return out.substring(0,out.length()-separador.length())+fin;
    }

}
