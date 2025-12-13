package org.example;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.*;

import javax.swing.*;

public class PanelGraficos extends JPanel {

    public XYSeries seriePEEP = new XYSeries("PEEP Real");
    public XYSeries serieSetPoint = new XYSeries("SetPoint");
    public XYSeries serieControl = new XYSeries("Control Output");
    public XYSeries serieError = new XYSeries("Error");

    public PanelGraficos() {
        setLayout(new java.awt.GridLayout(2, 2));

        add(crearGrafico("PEEP Real vs Tiempo", seriePEEP, serieSetPoint));
        add(crearGrafico("Control Output", serieControl));
        add(crearGrafico("Error", serieError));
    }

    private ChartPanel crearGrafico(String titulo, XYSeries... series) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (XYSeries s : series) dataset.addSeries(s);

        JFreeChart chart = ChartFactory.createXYLineChart(
                titulo, "Tiempo (s)", "", dataset,
                PlotOrientation.VERTICAL, true, false, false
        );

        return new ChartPanel(chart);
    }
}
