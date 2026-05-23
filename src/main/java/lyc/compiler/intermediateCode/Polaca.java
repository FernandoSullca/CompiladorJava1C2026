package lyc.compiler.intermediateCode;

import java.util.ArrayList;
import java.util.List;

public class Polaca {
    private static Polaca instance;
    private List<String> polaca;
    private int temporalCounter; // Para generar variables temporales t1, t2, t3, etc.

    private Polaca() {
        this.polaca = new ArrayList<>();
        this.temporalCounter = 0;
    }

    public static Polaca getInstance() {
        if (instance == null) {
            instance = new Polaca();
        }
        return instance;
    }

    /**
     * Agrega un elemento a la polaca inversa
     */
    public void addElement(String element) {
        polaca.add(element);
    }
    /**
     * Genera una variable temporal única (t1, t2, t3, etc)
     */
    public String generateTemporal() {
        temporalCounter++;
        return "t" + temporalCounter;
    }


    public List<String> getPolaca() {
        return polaca;
    }

    public void clear() {
        polaca.clear();
        temporalCounter = 0;
    }


    /**
     * Agrega un operador binario a la polaca
     * Formato: operando1 operando2 operador
     */
    public void addBinaryOperation(String operand1, String operand2, String operator) {
        addElement(operand1);
        addElement(operand2);
        addElement(operator);
    }

    /**
     * Agrega una asignación a la polaca
     * Formato: valorOrigen id :=
     */
    public void addAssignment(String value, String identifier) {
        addElement(identifier);
        addElement(value);
        addElement(":=");
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String element : polaca) {
            sb.append(element).append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * Retorna la polaca en formato formateado para archivo
     */
    public String toFormattedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("POLACA INVERSA (CÓDIGO INTERMEDIO)\n");
        sb.append("═".repeat(60)).append("\n");
        sb.append(toString()).append("\n");
        sb.append("═".repeat(60)).append("\n");
        return sb.toString();
    }

}
