package org.example;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;


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

        // --- ESTILO GENERAL DEL GRÁFICO ---
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE); // Fondo blanco para mayor contraste
        plot.setDomainGridlinePaint(new Color(220, 220, 220)); // Rejilla gris muy suave
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.setOutlineVisible(false); // Quitar bordes innecesarios

        // Fuentes más grandes para que se vean desde lejos
        Font fontEjes = new Font("SansSerif", Font.BOLD, 14);
        plot.getDomainAxis().setLabelFont(fontEjes);
        plot.getRangeAxis().setLabelFont(fontEjes);
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 18));

        // --- RENDERER (DISEÑO DE LAS LÍNEAS) ---
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);

        // Grosor de las líneas (3.0f es ideal para presentaciones)
        BasicStroke lineaGruesa = new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        // Estilo para el SetPoint (Línea punteada negra)
        BasicStroke lineaPunteada = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1.0f, new float[] {10.0f, 10.0f}, 0.0f);

        if (titulo.contains("PEEP Real")) {
            // Serie 0: PEEP Real (Verde médico)
            renderer.setSeriesPaint(0, new Color(0, 153, 76));
            renderer.setSeriesStroke(0, lineaGruesa);

            // Serie 1: SetPoint (Negro Referencia)
            renderer.setSeriesPaint(1, Color.BLACK);
            renderer.setSeriesStroke(1, lineaPunteada);
        }
        else if (titulo.equals("Control Output")) {
            // Color Ámbar/Naranja (Acción de actuador)
            renderer.setSeriesPaint(0, new Color(255, 128, 0));
            renderer.setSeriesStroke(0, lineaGruesa);
        }
        else if (titulo.equals("Error")) {
            // Color Púrpura (Desviación)
            renderer.setSeriesPaint(0, new Color(102, 0, 204));
            renderer.setSeriesStroke(0, lineaGruesa);
        }

        plot.setRenderer(renderer);

        ChartPanel panel = new ChartPanel(chart);
        panel.setMouseWheelEnabled(true); // Permite hacer zoom con el mouse
        return panel;
    }
}

