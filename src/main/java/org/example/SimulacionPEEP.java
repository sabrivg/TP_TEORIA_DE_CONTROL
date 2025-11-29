package org.example;import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeUnit;

public class SimulacionPEEP {

    // --- Parámetros de Simulación ---
    private static final double PASO_TIEMPO = 0.01; // Intervalo de integración (dt)
    private static final double INTERVALO_MUESTREO = 0.10; // Frecuencia de impresión (0.10s)
    private static final long PAUSA_MS = 100L; // Tiempo de espera en milisegundos entre cada línea impresa.
    private static final double PEEP_SETPOINT = 8.0; // cmH2O (Valor deseado)
    private static final double PEEP_INICIAL = 0.0;

    // --- Parámetros del Controlador PI (Base) ---
    private static final double KP_NORMAL = 1.0;
    private static final double KI_NORMAL = 2.0;

    // --- Ganancias ÓPTIMAS para Convergencia Limpia ---
    private static final double KP_ADAPTADO = 0.5; // Mantiene la velocidad
    private static final double KI_ADAPTADO = 0.8; // Reducido para eliminar la oscilación final

    // --- Parámetros del Proceso y la Perturbación de Carga ---
    private static final double TIEMPO_PERTURBACION = 6.0;
    private static final double GANANCIA_NORMAL = 1.2;
    private static final double GANANCIA_PERTURBADA = 1.0;
    private static final double TAU_NORMAL = 0.5;
    private static final double TAU_PERTURBADO = 3.0;

    // --- Variables de Estado ---
    private double peActual = PEEP_INICIAL;
    private double sumatoriaError = 0.0;
    private double salidaActuador = 0.0;

    // --- Métodos de Dinámica del Proceso ---

    private double getGanancia(double tiempo) {
        if (tiempo >= TIEMPO_PERTURBACION) {
            return GANANCIA_PERTURBADA;
        }
        return GANANCIA_NORMAL;
    }

    private double getTau(double tiempo) {
        if (tiempo >= TIEMPO_PERTURBACION) {
            return TAU_PERTURBADO;
        }
        return TAU_NORMAL;
    }

    private double simularProceso(double controlSignal, double tiempo) {
        double tauActual = getTau(tiempo);
        double gananciaActual = getGanancia(tiempo);

        double dpdt = (gananciaActual * controlSignal - peActual) / tauActual;
        peActual = peActual + dpdt * PASO_TIEMPO;

        if (peActual < 0.0) peActual = 0.0;
        if (peActual > 60.0) peActual = 60.0;

        return peActual;
    }

    /**
     * Calcula la salida del Controlador PI usando ganancias adaptativas.
     */
    private double calcularControlPI(double error, double tiempo) {

        // --- LÓGICA DE SINTONIZACIÓN ADAPTATIVA OPTIMIZADA ---
        double Kp = (tiempo < TIEMPO_PERTURBACION) ? KP_NORMAL : KP_ADAPTADO; //la perturbacion cambia los valores de kp y ki
        double Ki = (tiempo < TIEMPO_PERTURBACION) ? KI_NORMAL : KI_ADAPTADO;
        // ----------------------------------------------------

        double pTerm = Kp * error;
        sumatoriaError += error * PASO_TIEMPO;

        if (sumatoriaError > 10.0) sumatoriaError = 10.0;
        if (sumatoriaError < -10.0) sumatoriaError = -10.0;

        double iTerm = Ki * sumatoriaError;
        salidaActuador = pTerm + iTerm;

        if (salidaActuador < 0.0) salidaActuador = 0.0;

        return salidaActuador;
    }

    // --- Ejecución de la Simulación Continua con Pausa ---

    public void ejecutarSimulacion() {
        System.out.println("--- Control PEEP (Simulación Continua con Convergencia Estable) ---");
        System.out.println("SetPoint PEEP: " + PEEP_SETPOINT + " cmH2O");
        System.out.printf("Sintonización Adaptativa Óptima: KP/KI cambian de %.1f/%.1f a %.1f/%.1f a t=%.1fs.%n",
                KP_NORMAL, KI_NORMAL, KP_ADAPTADO, KI_ADAPTADO, TIEMPO_PERTURBACION);
        System.out.printf("Frecuencia de muestreo: Cada %.2fs con una pausa de %d ms. (Detener con Ctrl+C)%n", INTERVALO_MUESTREO, PAUSA_MS);
        System.out.println("-------------------------------------------------------");
        System.out.println("Tiempo(s)\tPEEP_Real(cmH2O)\tControl_Output\tError");

        double tiempo = 0.0;
        int contadorMuestreo = 0;
        int pasosPorMuestreo = (int) (INTERVALO_MUESTREO / PASO_TIEMPO);

        // Bucle infinito
        while (true) {

            // 1. Cálculo del ciclo de control
            double error = PEEP_SETPOINT - peActual;
            double controlOutput = calcularControlPI(error, tiempo);
            peActual = simularProceso(controlOutput, tiempo);

            // 4. Lógica de Impresión y Pausa
            if (contadorMuestreo == pasosPorMuestreo) {
                String marcaPerturbacion = (Math.abs(tiempo - TIEMPO_PERTURBACION) < PASO_TIEMPO) ? " <<< PERTURBACIÓN SEVERA" : "";

                System.out.printf("%.2f\t\t%.4f\t\t\t\t%.4f\t\t%.4f%s%n",
                        tiempo, peActual, controlOutput, error, marcaPerturbacion);

                contadorMuestreo = 0;

                try {
                    TimeUnit.MILLISECONDS.sleep(PAUSA_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            tiempo += PASO_TIEMPO;
            contadorMuestreo++;
        }
    }

    public static void main(String[] args) {
        new SimulacionPEEP().ejecutarSimulacion();
    }
}