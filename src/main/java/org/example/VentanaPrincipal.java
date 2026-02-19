package org.example;

import javax.swing.*;

public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal() {
        setTitle("Simulación Control PEEP - UTN FRBA");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new java.awt.BorderLayout());

        PanelControl panelControl = new PanelControl();
        PanelGraficos panelGraficos = new PanelGraficos();

        add(panelControl, java.awt.BorderLayout.WEST);
        add(panelGraficos, java.awt.BorderLayout.CENTER);

        // Crear simulación y engancharla a los gráficos y sliders
        SimulacionPEEP simulacion = new SimulacionPEEP(panelGraficos, panelControl);
        simulacion.start();

        setVisible(true);
    }

    public static void main(String[] args) {
        new VentanaPrincipal();
    }
}

    public static void main(String[] args) {
        new VentanaPrincipal();
    }
}
