package org.example;import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeUnit;

public class SimulacionPEEP extends Thread {

    private PanelGraficos graficos;
    private PanelControl control;

    private double tiempo = 0;
    private double peActual = 0;
    private double sumError = 0;

    public SimulacionPEEP(PanelGraficos g, PanelControl c) {
        this.graficos = g;
        this.control = c;
    }

    @Override
    public void run() {
        while (true) {
            double setPoint = control.sliderSetPoint.getValue();
            double kp = control.sliderKp.getValue() / 100.0;
            double ki = control.sliderKi.getValue() / 100.0;

            if (control.isPausado()) {
                try { Thread.sleep(100); continue; } catch (Exception e) {}
            }

            // --- CÁLCULO DEL ERROR ---
            // El error matemático DEBE mantener el signo para que el PI funcione
            double errorMatematico = setPoint - peActual;

            // El error visual es el que "siempre sube" (valor absoluto)
            double errorVisual = Math.abs(errorMatematico);

            sumError += errorMatematico * 0.01;
            double controlOutput = kp * errorMatematico + ki * sumError;

            // --- GESTIÓN DE PERTURBACIONES ---
            double perturbacion = 0;
            if (tiempo >= 17.0 && tiempo <= 20.0) perturbacion = -3.0; // Fuga
            if (tiempo >= 35.0 && tiempo <= 37.0) perturbacion = -3.0; // Fuga
            if (tiempo >= 45.0 && tiempo <= 48.0) perturbacion = 4.0;  // TOS (+)

            // Dinámica del proceso
            peActual += (1.2 * (controlOutput + perturbacion) - peActual) * 0.01;

            // --- ACTUALIZACIÓN DE GRÁFICOS ---
            graficos.seriePEEP.add(tiempo, peActual);
            graficos.serieSetPoint.add(tiempo, setPoint);
            graficos.serieControl.add(tiempo, controlOutput);

            // Usamos el errorVisual para que la curva siempre suba ante cualquier desvío
            graficos.serieError.add(tiempo, errorVisual);

            tiempo += 0.1;
            try { Thread.sleep(50); } catch (Exception ignored) {}
        }
    }
}
