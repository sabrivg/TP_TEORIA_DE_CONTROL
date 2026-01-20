package org.example;

import javax.swing.*;

public class PanelControl extends JPanel {

    public JSlider sliderSetPoint;
    public JSlider sliderKp;
    public JSlider sliderKi;

    public PanelControl() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        sliderSetPoint = crearSlider("SetPoint (cmH2O)", 0, 20, 8);
        sliderKp       = crearSlider("Kp", 0, 200, 100);
        sliderKi       = crearSlider("Ki", 0, 200, 80);
    }

    private JSlider crearSlider(String titulo, int min, int max, int value) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JSlider slider = new JSlider(min, max, value);
        slider.setMajorTickSpacing((max - min) / 4);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        
        p.setBorder(BorderFactory.createTitledBorder(titulo));
        p.add(slider);

        add(p); 
        return slider;
    }
}

