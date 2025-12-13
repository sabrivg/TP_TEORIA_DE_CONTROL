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

            double error = setPoint - peActual;

            sumError += error * 0.01;
            double controlOutput = kp * error + ki * sumError;

            // dinámica del proceso
            peActual += (1.2 * controlOutput - peActual) * 0.01;

            // actualizar gráficos
            graficos.seriePEEP.add(tiempo, peActual);
            graficos.serieSetPoint.add(tiempo, setPoint);
            graficos.serieControl.add(tiempo, controlOutput);
            graficos.serieError.add(tiempo, error);

            tiempo += 0.1;

            try { Thread.sleep(50); } catch (Exception ignored) {}
        }
    }
}
