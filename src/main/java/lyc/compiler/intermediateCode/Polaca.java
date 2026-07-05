package lyc.compiler.intermediateCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Polaca {
    private static Polaca instance;
    private List<String> polaca;
    private int temporalCounter; // Para generar variables temporales @t1, @t2, @t3, etc.
    private Stack<Integer> saltos; // Pila para ET / BI de bucles
    private Stack<Integer> saltosFalso; // Direcciones cuando condición es FALSA // Saltos si la condición es falsa (salir del if/while)
    private Stack<Integer> saltosVerdadero;  // Direcciones cuando condición es VERDADERA // Saltos OR: si condición izquierda es verdadera (entrar al cuerpo)
    private Map<Integer, List<Integer>> saltosFalsoVinculados; // Indices "_" que deben resolverse con el mismo destino que otro (AND con varios terminos)

    private Polaca() {
        this.polaca = new ArrayList<>();
        this.saltos = new Stack<>();
        this.saltosFalso = new Stack<>();
        this.saltosVerdadero = new Stack<>();
        this.saltosFalsoVinculados = new HashMap<>();
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
     * Genera una variable temporal única (@t1, @t2, @t3, etc)
     */
    public String generateTemporal() {
        temporalCounter++;
        return "@t" + temporalCounter;
    }


    public List<String> getPolaca() {
        return polaca;
    }

    public void clear() {
        polaca.clear();
        saltos.clear();
        saltosFalso.clear();
        saltosVerdadero.clear();
        saltosFalsoVinculados.clear();
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
        addElement(value);
        addElement(identifier);
        addElement(":=");
    }

    /**
     * Retorna la posición actual (índice donde se insertará el próximo elemento)
     */
    public int getPosicionActual() {
        return polaca.size();
    }

    /**
     * Apila una dirección en la pila de saltos
     */
    public void apilarSaltoPila(int direccion) {
        saltos.push(direccion);
        System.out.println("Apilando dirección de salto: " + direccion);
    }

    /**
     * Desapila una dirección de la pila de saltos
     */
    public int desapilarSaltoPila() {
        if (!saltos.isEmpty()) {
            return saltos.pop();
        }
        throw new RuntimeException("Error: Pila de saltos vacía al intentar desapilar.");
    }

    /**
     * Rellena una celda de la polaca con un valor específico (usado para completar saltos)
     */
    public void setElementAt(int index, String value) {
        System.out.println("Índice: " + index + ", Valor: " + value);
        if (index >= 0 && index < polaca.size()) {
            polaca.set(index, value);
        } else {
            throw new RuntimeException("Error: Índice fuera de rango al intentar rellenar celda de polaca.");
        }
    }

    //Funcionalidades de pilas condicionales de direcciones de salto
    public void apilarSaltoFalso(int direccion) {
        saltosFalso.push(direccion);
    }

    public int desapilarSaltoFalso() {
        if (saltosFalso.isEmpty()) {
            throw new RuntimeException("Error: Pila de saltos falsos vacía.");
        }
        return saltosFalso.pop();
    }

    public void apilarSaltoVerdadero(int direccion) {
        saltosVerdadero.push(direccion);
    }

    public void rellenarSaltosFalso(int destino) {
        if (!saltosFalso.isEmpty()) {
            int posicionARellenar = saltosFalso.pop(); // Solo saca EL ÚLTIMO
            setElementAt(posicionARellenar, String.valueOf(destino));

            // Si esta posicion tiene otras vinculadas (terminos de un AND que deben
            // compartir el mismo destino final), tambien se resuelven aca.
            List<Integer> vinculados = saltosFalsoVinculados.remove(posicionARellenar);
            if (vinculados != null) {
                for (int idx : vinculados) {
                    setElementAt(idx, String.valueOf(destino));
                }
            }
        }
    }

    /**
     * Vincula la posicion "vinculado" a "principal": cuando "principal" se resuelva
     * via rellenarSaltosFalso, "vinculado" recibe el mismo destino. Usado por AND
     * para que ambos lados de la condicion salten al mismo lugar si alguno es falso,
     * sin romper el comportamiento de "una sola posicion por llamada" que necesita el if anidado.
     */
    public void vincularSaltoFalso(int principal, int vinculado) {
        saltosFalsoVinculados.computeIfAbsent(principal, k -> new ArrayList<>()).add(vinculado);
    }

    public void rellenarSaltosVerdadero(int destino) {
        while (!saltosVerdadero.isEmpty()) {
            setElementAt(saltosVerdadero.pop(), String.valueOf(destino));
        }
    }

    //usado para la condicion NOT
    public void invertirSaltoCondicional(int idxCeldaReservada) {
        if (idxCeldaReservada <= 0 || idxCeldaReservada >= polaca.size()) {
            throw new RuntimeException("Error: Celda de salto inválida para NOT.");
        }
        String op = polaca.get(idxCeldaReservada - 1);
        polaca.set(idxCeldaReservada - 1, invertirOperadorSalto(op));
    }

    private String invertirOperadorSalto(String op) {
        return switch (op) {
            case "BLE" -> "BGT";
            case "BGT" -> "BLE";
            case "BLT" -> "BGE";
            case "BGE" -> "BLT";
            case "BNE" -> "BEQ";
            case "BEQ" -> "BNE";
            default -> op;
        };
    }


    // Extrae un rango de la polaca como lista (para capturar el cuerpo)
    public List<String> extractRange(int desde, int hasta) {
        List<String> body = new ArrayList<>(polaca.subList(desde, hasta));
        polaca.subList(desde, hasta).clear();
        return body;
    }

    // Inserta una lista de tokens en una posición
    public void insertAt(int pos, List<String> tokens) {
        polaca.addAll(pos, tokens);
    }


    public void injectBodyAfterEachAssignment(String varId, List<String> body, int desde) {
        // Recorre desde 'desde' buscando patrones: [algo] [varId] [:=]
        // y después de cada := inserta una copia del body
        int i = desde;
        while (i < polaca.size()) {
            // Detectar patrón: posición i+1 es varId y i+2 es :=
            if (i + 2 < polaca.size()
                    && polaca.get(i + 1).equals(varId)
                    && polaca.get(i + 2).equals(":=")) {
                // Insertar copia del body después del :=
                polaca.addAll(i + 3, new ArrayList<>(body));
                // Saltar sobre lo que acabamos de insertar
                i = i + 3 + body.size();
            } else {
                i++;
            }
        }
    }

    public void removeAt(int index) {
        if (index >= 0 && index < polaca.size()) {
            polaca.remove(index);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String element : polaca) {
            sb.append("\""+element+"\"" ).append(" ");
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
