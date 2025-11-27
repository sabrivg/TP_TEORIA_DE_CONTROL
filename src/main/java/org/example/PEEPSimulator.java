package org.example;
public class PEEPSimulator {

    // --- Parámetros del Controlador PI ---
    private static final double KP = 0.5; // Ganancia Proporcional
    private static final double KI = 0.1; // Ganancia Integral
    private static final double TS = 0.1; // Tiempo de Muestreo (en segundos)
    private static final double OUTPUT_MAX = 10.0; // Límite superior de la señal de control (válvula)
    private static final double OUTPUT_MIN = 0.0;  // Límite inferior de la señal de control (válvula)
    private double integralTerm = 0.0;
    private double previousError = 0.0;

    // --- Parámetros del Proceso (Modelo Simple de Primer Orden: Circuito RC) ---
    // El proceso (válvula, circuito respiratorio, pulmón) se modela con una TdC Tau [cite: 102, 139]
    private static final double TAU = 0.5; // Constante de Tiempo del proceso (RC)
    private static final double GAIN_PROCESS = 1.0; // Ganancia del Proceso
    private double currentPEEP = 0.0;

    /**
     * Implementa la lógica del controlador Proporcional-Integral (PI) digital.
     *
     * @param setpoint     La PEEP deseada (Valor de Referencia).
     * @param measuredPEEP La PEEP medida por el sensor (Variable Controlada).
     * @return La señal de control (salida al actuador/válvula).
     */
    public double calculateControlSignal(double setpoint, double measuredPEEP) {
        // 1. Cálculo de la Señal de Error: e(k) = Setpoint - PEEP_medida
        double error = setpoint - measuredPEEP;

        // 2. Componente Proporcional: P(k) = Kp * e(k)
        double proportionalTerm = KP * error;

        // 3. Componente Integral: I(k) = I(k-1) + Ki * Ts * e(k)
        // Se utiliza la regla rectangular (integración de Euler hacia adelante)
        integralTerm += KI * TS * error;

        // **Anti-windup por Umbral (Clamping):** Limitar el término integral
        if (integralTerm > OUTPUT_MAX) {
            integralTerm = OUTPUT_MAX;
        } else if (integralTerm < OUTPUT_MIN) {
            integralTerm = OUTPUT_MIN;
        }

        // 4. Salida del Controlador: u(k) = P(k) + I(k)
        double controlSignal = proportionalTerm + integralTerm;

        // 5. Saturación de la Señal de Control (Umbrales de la Válvula)
        if (controlSignal > OUTPUT_MAX) {
            controlSignal = OUTPUT_MAX;
        } else if (controlSignal < OUTPUT_MIN) {
            controlSignal = OUTPUT_MIN;
        }

        // 6. Actualizar el error anterior para posible uso futuro (por ejemplo, en un PID)
        previousError = error;

        return controlSignal;
    }

    /**
     * Simula la respuesta del proceso (Pulmón/Circuito).
     * Usa un modelo de primer orden: dy/dt = (1/Tau) * (-y + K * u)
     * Implementación discreta: y(k+1) = y(k) + Ts * (1/Tau) * (-y(k) + K * u(k))
     *
     * @param controlSignal La entrada al proceso (señal de la válvula).
     * @return La nueva PEEP real.
     */
    public double simulateProcess(double controlSignal) {
        // Cálculo del cambio en PEEP basado en el control signal y la PEEP actual
        double derivative = (1.0 / TAU) * (-currentPEEP + GAIN_PROCESS * controlSignal);

        // Integración de Euler
        currentPEEP += TS * derivative;

        // Asegurar que la PEEP no sea negativa (restricción física)
        if (currentPEEP < 0.0) {
            currentPEEP = 0.0;
        }

        return currentPEEP;
    }

    public static void main(String[] args) {
        PEEPSimulator simulator = new PEEPSimulator();
        double setpointPEEP = 5.0; // Consigna: PEEP de 5 cm H2O

        // Eliminamos 'int totalSteps = 100;'

        // Inicializamos el contador de pasos (sustituye a 'i' del for)
        int step = 0;

        System.out.println("--- Simulación del Control de PEEP (PI Discreto) ---\n");
        System.out.println("SetPoint PEEP: " + setpointPEEP + " cm H2O\n");
        System.out.println("Tiempo\tPEEP_Medida\tError\tSeñal_Control");

        // Bucle infinito: la simulación corre hasta que la detengas (Ctrl+C)
        while (true) {
            double time = step * TS;

            // 1. Medición (Asumimos la PEEP simulada es la PEEP medida por el sensor)
            double measuredPEEP = simulator.currentPEEP;

            // 2. Controlador calcula la señal de control
            double controlSignal = simulator.calculateControlSignal(setpointPEEP, measuredPEEP);

            // 3. El proceso (pulmón) responde a la señal de control (válvula)
            double newPEEP = simulator.simulateProcess(controlSignal);

            // 4. Mostrar resultados
            double error = setpointPEEP - measuredPEEP;
            System.out.printf("%.1f\t\t%.4f\t\t%.4f\t\t%.4f%n", time, measuredPEEP, error, controlSignal);

            // 5. Incrementamos el paso para la próxima iteración
            step++;

            // 6. Perturbación (Ejemplo: en un tiempo específico)
            if (step == 50) {
                System.out.println("\n*** PERTURBACIÓN APLICADA: Disminución momentánea de PEEP. ***\n");
                simulator.currentPEEP -= 1.5;
                if (simulator.currentPEEP < 0.0) simulator.currentPEEP = 0.0;
            }


            try {
                // Pausa la ejecución por el número de milisegundos especificado.
                // Aquí se usan 100 ms.
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        // La línea 'System.out.println("\nSimulación finalizada.");'
        // se hace inalcanzable con un 'while(true)'.


    }
}