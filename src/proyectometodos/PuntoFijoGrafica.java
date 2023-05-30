/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectometodos;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.swing.JFrame;

public class PuntoFijoGrafica {
    public static void main(String[] args) {
        // Definición de la función f(x)
        Function<Double, Double> f = x -> Math.pow(x, 3) - 2 * x - 5;

        // Definición de la función g(x) obtenida a partir de f(x) despejando x
        Function<Double, Double> g = x -> Math.pow(x + 5, 1.0 / 3.0);

        double puntoInicial = 2.0; // Punto inicial
        double tolerancia = 0.0001; // Tolerancia para la convergencia
        int iteracionesMaximas = 100; // Número máximo de iteraciones

        // Intervalo de confianza
        double intervaloInferior = 1.0;
        double intervaloSuperior = 3.0;

        // Cálculo del valor medio del intervalo
        double valorMedio = (intervaloInferior + intervaloSuperior) / 2.0;

        // Obtención de los posibles despejes de x
        List<Function<Double, Double>> posiblesDespejes = obtenerPosiblesDespejes(f);

        // Cálculo de las derivadas y sustitución por el valor medio
        List<Function<Double, Double>> derivadasSustituidas = obtenerDerivadasSustituidas(posiblesDespejes, valorMedio);

        // Inicio de la iteración con el despeje más apto
        Function<Double, Double> despejeInicial = obtenerDespejeMasApto(derivadasSustituidas);
        double puntoFijo = metodoPuntoFijo(despejeInicial, puntoInicial, tolerancia, iteracionesMaximas);

        System.out.println("Punto fijo: " + puntoFijo);

        // Creación de la gráfica
        XYSeries series = crearSeries(f, intervaloInferior, intervaloSuperior);
        JFreeChart chart = ChartFactory.createXYLineChart("Gráfica de la función", "x", "f(x)", new XYSeriesCollection(series), PlotOrientation.VERTICAL, true, true, false);
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(500, 400));

        // Mostrar la gráfica
        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame("Gráfica de la función");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(panel, BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    public static double metodoPuntoFijo(Function<Double, Double> g, double puntoInicial, double tolerancia, int iteracionesMaximas) {
        double puntoActual = puntoInicial;
        double puntoAnterior;

        for (int i = 0; i < iteracionesMaximas; i++) {
            puntoAnterior = puntoActual;
            puntoActual = g.apply(puntoAnterior);

            if (Math.abs(puntoActual - puntoAnterior) < tolerancia) {
                return puntoActual; // Retorna el punto fijo encontrado
            }
        }

        System.out.println("El método no converge después de " + iteracionesMaximas + " iteraciones.");
        return Double.NaN; // Retorna NaN si el método no converge
    }

    public static List<Function<Double, Double>> obtenerPosiblesDespejes(Function<Double, Double> f) {
        // Aquí puedes agregar los posibles despejes de x de acuerdo a la función f(x)
        List<Function<Double, Double>> posiblesDespejes = new ArrayList<>();
        posiblesDespejes.add(x -> Math.pow(f.apply(x) + 5, 1.0 / 3.0)); // Ejemplo: f(x) = x^3 - 2x - 5

        return posiblesDespejes;
    }

    public static List<Function<Double, Double>> obtenerDerivadasSustituidas(List<Function<Double, Double>> posiblesDespejes, double punto) {
        List<Function<Double, Double>> derivadasSustituidas = new ArrayList<>();

        for (Function<Double, Double> despeje : posiblesDespejes) {
            Function<Double, Double> derivada = obtenerDerivada(despeje);
            double derivadaSustituida = derivada.apply(punto);
            derivadasSustituidas.add(x -> derivadaSustituida);
        }

        return derivadasSustituidas;
    }

    public static Function<Double, Double> obtenerDerivada(Function<Double, Double> f) {
        double h = 0.0001; // Tamaño del incremento
        return x -> (f.apply(x + h) - f.apply(x)) / h;
    }

    public static Function<Double, Double> obtenerDespejeMasApto(List<Function<Double, Double>> derivadasSustituidas) {
        double maximo = Double.NEGATIVE_INFINITY;
        Function<Double, Double> despejeMasApto = null;

        for (Function<Double, Double> despeje : derivadasSustituidas) {
            double valor = despeje.apply(0.0); // Evaluación en el punto 0.0
            if (valor > maximo) {
                maximo = valor;
                despejeMasApto = despeje;
            }
        }

        return despejeMasApto;
    }

    public static XYSeries crearSeries(Function<Double, Double> f, double intervaloInferior, double intervaloSuperior) {
        XYSeries series = new XYSeries("f(x)");

        double paso = 0.1;
        for (double x = intervaloInferior; x <= intervaloSuperior; x += paso) {
            double y = f.apply(x);
            series.add(x, y);
        }

        return series;
    }
}
