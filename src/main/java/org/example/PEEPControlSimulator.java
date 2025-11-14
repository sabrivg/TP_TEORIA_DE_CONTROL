package org.example;
import java.util.Scanner;

public class PEEPControlSimulator {

    // --- Parámetros del Sistema de Control (Variables de diseño) ---
    private static final double KP = 1.5;   // Ganancia Proporcional (K_p)
    private static final double KI = 0.5;   // Ganancia Integral (K_i)
    private static final double TS = 0.01;  // Tiempo de muestreo (segundos) - T_s
    private static final double PEEP_SETPOINT = 5.0; // PEEP deseada (Set Point) en cm H2O

    // --- Parámetros de la Planta (Modelo simple del Sistema Respiratorio) ---
    // G(s) = K / (tau*s + 1)
    private static final double PLANTA_GANANCIA = 1.0;
    private static final double PLANTA_TAU = 0.5; // Constante de tiempo (resistencia/compliance)

    // --- Variables de Estado y Acumuladores ---
    private double integralError = 0.0;
    private double peActual = 0.0;      // PEEP actual (Salida del proceso)
    private double procesoAnterior = 0.0;
    private double perturbacionActual = 0.0; // Valor actual de la perturbación (D)
    private double tiempoSimulacion = 0.0;

    /**
     * Simula un único paso de muestreo (TS) del sistema de control de lazo cerrado.
     * Implementa la lógica digital de ADC, Controlador, DAC (implícito) y la dinámica de la Planta.
     * @param setpoint La PEEP de referencia deseada.
     * @return El valor de PEEP actual después de la acción de control.
     */
    public double simularPaso(double setpoint) {

        // 1. ADC / ELEMENTO DE MEDICIÓN (Asumimos que el sensor mide la PEEP actual)
        double peMedida = peActual;

        // 2. PUNTO SUMA: Cálculo del Error (Digital)
        double error = setpoint - peMedida;

        // 3. CONTROLADOR DIGITAL PI (LEY DE CONTROL)
        // Acción Integral: Suma discreta del error
        integralError += error * TS;

        // Acción Proporcional
        double up = KP * error;

        // Señal de Control (U = Up + Ui). Esta señal pasaría por un DAC (implícito)
        double u = up + (KI * integralError);

        // 4. PUNTO SUMA DE PERTURBACIÓN y ACTUADOR (Válvula)
        // La Válvula opera con la orden de control (U) más la fuerza externa (D)
        double entradaPlanta = u + perturbacionActual;

        // 5. PROCESO (PLANTA / SISTEMA RESPIRATORIO)
        // Simulación discreta del proceso de primer orden
        double a = PLANTA_TAU / (PLANTA_TAU + TS);
        double b = PLANTA_GANANCIA * TS / (PLANTA_TAU + TS);

        double peNuevo = a * procesoAnterior + b * entradaPlanta;

        // Actualizar variables de estado
        procesoAnterior = peNuevo;
        peActual = peNuevo;
        tiempoSimulacion += TS; // Incrementar el tiempo de la simulación

        return peActual;
    }

    // Métodos auxiliares para mostrar valores en la salida
    public double u_current() {
        double error = PEEP_SETPOINT - peActual;
        double up = KP * error;
        return up + (KI * integralError);
    }

    public double error_current() {
        return PEEP_SETPOINT - peActual;
    }

    // =========================================================================
    // PUNTO DE ENTRADA PRINCIPAL
    // =========================================================================
    public static void main(String[] args) {
        PEEPControlSimulator simulator = new PEEPControlSimulator();
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- SIMULACIÓN CONTINUA E INTERACTIVA DE CONTROL DE PEEP ---");
        System.out.println("PEEP Deseada (Set Point): " + PEEP_SETPOINT + " cm H2O");
        System.out.println("Instrucciones: Escribe 'P' y ENTER para aplicar una nueva Perturbación (D).");
        System.out.println("Presiona CTRL+C para detener la simulación (Tiempo Infinito).");
        System.out.println("------------------------------------------------------------------");
        System.out.println("T (s) | PEEP (cm H2O) | Control U | Perturbación D | Error E");
        System.out.println("------------------------------------------------------------------");

        long ultimoCheck = System.currentTimeMillis();

        // Bucle infinito: La simulación se ejecuta hasta que el usuario la detenga (CTRL+C)
        while (true) {

            // 1. Simulación del paso de control (ejecución digital)
            simulator.simularPaso(PEEP_SETPOINT);

            // 2. MOSTRAR RESULTADOS EN CADA PASO DE MUESTREO (0.01s)
            // Se utiliza System.out.printf para un formato limpio de la salida de datos
            System.out.printf("%.3f | %.4f | %.4f | %.2f | %.4f\n",
                    simulator.tiempoSimulacion,
                    simulator.peActual,
                    simulator.u_current(),
                    simulator.perturbacionActual,
                    simulator.error_current());

            // 3. Lógica Interactiva (Chequeo de entrada)
            // Chequea si hay entrada del usuario cada 500 ms para no bloquear la simulación
            if (System.currentTimeMillis() - ultimoCheck > 500) {
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("P")) {
                        try {
                            System.out.print("Introduce el VALOR de la Perturbación (ej: -2.0 para fuga, 0.0 para quitarla): ");
                            double nuevaPerturbacion = Double.parseDouble(scanner.nextLine());
                            simulator.perturbacionActual = nuevaPerturbacion;
                            System.out.println("--- ¡PERTURBACIÓN APLICADA! Nuevo valor D: " + nuevaPerturbacion + " ---");
                        } catch (NumberFormatException e) {
                            System.err.println("--- ENTRADA INVÁLIDA. Debe ser un número decimal (ej: -1.5). ---");
                        }
                    }
                }
                ultimoCheck = System.currentTimeMillis();
            }

            // 4. Pausa de Tiempo Real
            // Simula que el bucle de control se ejecuta cada TS (10ms)
            try {
                Thread.sleep((long)(TS * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
