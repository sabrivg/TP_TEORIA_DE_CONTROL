package org.example;

import javax.swing.*;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;

public class PanelControl extends JPanel {

    public JSlider sliderSetPoint;
    public JSlider sliderKp;
    public JSlider sliderKi;
    public JButton btnPausa; // El nuevo botón
    private boolean pausado = false; // Estado de la simulación

    public PanelControl() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        sliderSetPoint = crearSlider("SetPoint (cmH2O)", 0, 20, 8);
        sliderKp       = crearSlider("Kp", 0, 200, 100);
        sliderKi       = crearSlider("Ki", 0, 200, 80);
        // --- CONFIGURACIÓN DEL BOTÓN DE PAUSA ---
        btnPausa = new JButton("PAUSAR SIMULACIÓN");
        btnPausa.setAlignmentX(CENTER_ALIGNMENT);
        btnPausa.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnPausa.setBackground(new Color(230, 230, 230));

        btnPausa.addActionListener(e -> {
            pausado = !pausado;
            if (pausado) {
                btnPausa.setText("REANUDAR");
                btnPausa.setBackground(new Color(255, 204, 204)); // Rojo claro
            } else {
                btnPausa.setText("PAUSAR SIMULACIÓN");
                btnPausa.setBackground(new Color(204, 255, 204)); // Verde claro
            }
        });

        add(javax.swing.Box.createVerticalStrut(20)); // Espacio
        add(btnPausa);
    }
    public boolean isPausado() {
        return pausado;
    }
    private JSlider crearSlider(String titulo, int min, int max, int value) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JSlider slider = new JSlider(min, max, value);
        slider.setMajorTickSpacing((max - min) / 4);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);

        // Recuadro con título
        p.setBorder(BorderFactory.createTitledBorder(titulo));
        p.add(slider);

        add(p); // agregamos el panel completo
        return slider;
    }
}

