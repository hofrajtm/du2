/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.Math.sqrt;
import java.util.Locale;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jdk.nashorn.internal.runtime.regexp.joni.Syntax.Java;

/**
 * třída na správu argumentů z příkazové řádky
 * @author Martin
 */
class Arguments {
    // argumenty programu
    public int p; // exponent ve váhové funkci
    public int s;
    public int sirka;
    public int vyska;
    public String in;
    public String out;

    public Arguments(String[] args) {
        int arguments_length = args.length;

        if (arguments_length < 2) {
            throw new RuntimeException("The length of arguments should be at least 2, in and out file.");
        }

        this.p = 2;
        this.s = 0;
        this.sirka = 100;
        this.vyska = 100;
        this.in = args[arguments_length - 2];
        this.out = args[arguments_length - 1];

        for (int i = 0; i < (arguments_length - 2) / 2; i++) {
            if (args[i * 2].equals("-p")) {
                this.p = Integer.parseInt(args[i * 2 + 1]); // funkce na přeměnu stringu na integer
            } else if (args[i * 2].equals("-s")) {
                this.s = Integer.parseInt(args[i * 2 + 1]);
            } else if (args[i * 2].equals("-g")) {
                String[] sirkaVyska = args[i * 2 + 1].split("x");
                this.sirka = Integer.parseInt(sirkaVyska[0]);
                this.vyska = Integer.parseInt(sirkaVyska[1]);
            } else {
                // doplnit chybovou hlasku
            }
        }
    }
}

/**
 *
 * @author Martin
 */
public class du2 {

    static double MIN_X = Double.POSITIVE_INFINITY;
    static double MAX_X = Double.NEGATIVE_INFINITY;
    static double MIN_Y = Double.POSITIVE_INFINITY;
    static double MAX_Y = Double.NEGATIVE_INFINITY;

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        // nastaví na anglickou lokaci, např. desetinná čárka se změní na tečku
        Locale.setDefault(new Locale("en", "US"));
        Arguments arguments = new Arguments(args); //načte parametry z příkazové řádky 
        double[][] data = nactiVstupniSoubor();
        vypisVystup(arguments, data);
    }

    /**
     *
     * @param arguments Nactena trida argumentu
     * @param data Vsechna data ze vstupniho souboru.
     */
    public static void vypisVystup(Arguments arguments, double[][] data) {
        // uložení hodnot do určeného souboru
        PrintWriter writer;
        try {
            writer = new PrintWriter("out.csv");
            writer.println(arguments.sirka * arguments.vyska);
            // projde celý řádek od min s krokem dle definované hodnoty mřížky 'sirka' po max
            for (double x = MIN_X; x <= MAX_X; x += (MAX_X - MIN_X) / arguments.sirka) {
                // projde všechny sloupce od min s krokem dle definované hodnoty mřížky 'vyska' po max
                for (double y = MIN_Y; y <= MAX_Y; y += (MAX_Y - MIN_Y) / arguments.vyska) {

                    double z0 = spoctiIDW(data, x, y, arguments);
                    writer.printf("%.2f,", z0);
                }
                writer.printf("\n");
            }
            writer.close();
        } catch (FileNotFoundException ex) {
            System.err.format("Soubor %s nebyl nalezen!", "out.csv");
            System.exit(1);
        } catch (IOException ex) {
            System.err.print("Vyskytla se chyba při ukládání hodnot do řádku!");
            System.exit(1);
        }
    }

    /**
     *
     * @param data
     * @param x
     * @param y
     * @param arguments
     * @return
     */
    public static double spoctiIDW(double[][] data, double x, double y, Arguments arguments) {
        double lambda_jmenovatel = spoctiJmenovatel(data, x, y, arguments);
        //výpočet čitatele lambdy
        double z0 = spoctiVysledek(data, x, y, arguments, lambda_jmenovatel); //writer.printf("%.2f, %.2f, %.2f\n", x, y, z0);
        return z0;
    }

    /**
     *
     * @param data
     * @param x
     * @param y
     * @param arguments
     * @return
     */
    public static double spoctiJmenovatel(double[][] data, double x, double y, Arguments arguments) {
        double lambda_jmenovatel = 0;
        // výpočet jmenovatele lambdy
        for (int i = 0; i < data.length; i++) {
            double v = sqrt((x - data[i][0]) * (x - data[i][0]) + (y - data[i][1]) * (y - data[i][1]));
            lambda_jmenovatel += 1 / Math.pow(v, arguments.p);
        }
        return lambda_jmenovatel;
    }

    /**
     *
     * @return
     * @throws NumberFormatException
     */
    public static double[][] nactiVstupniSoubor() throws NumberFormatException {
        double[][] data = null;
        try {
            // načtení hodnot z určeného souboru
            BufferedReader br = new BufferedReader(new FileReader("in.csv"));
            // definuje proměnné min a max 'x' a 'y'
            // načte počet řádku dle čísla v prvním řádku
            int pocet_radku = Integer.parseInt(br.readLine());
            // pole polí obasahující pozici [sloupce] a [řádku]
            data = ctiData(pocet_radku, br);
        } catch (FileNotFoundException ex) {
            System.err.format("Soubor %s nebyl nalezen!", "in.csv");
            System.exit(1);
        } catch (IOException ex) {
            System.err.print("Vyskytla se chyba při načítání hodnot z řádku!");
            System.exit(1);
        }
        return data;
    }

    /**
     *
     * @param pocet_radku
     * @param br
     * @return
     * @throws IOException
     */
    public static double[][] ctiData(int pocet_radku, BufferedReader br) throws IOException {
        double[][] data = new double[pocet_radku][];

        for (int i = 0; i < pocet_radku; i++) {
            String radek = br.readLine(); // dfinuje řádek jako řezězec a načte hodnoty ze všech řádků do proměnné 'radek'
            String[] string_hodnoty = radek.split(","); // rozdělí řezězec na tři řetězce podle čárek
            double[] hodnoty = new double[]{ // pro každý řádek přemění řetězce na pole o třech hodnotách double
                // ve složené závorce si ručně nastavím jenotlivé položky pole a přemění string na double
                Double.parseDouble(string_hodnoty[0]),
                Double.parseDouble(string_hodnoty[1]),
                Double.parseDouble(string_hodnoty[2])
            };

            // pro urychlení operace hledá min a max již při načítání hodnot do proměnné 'hodnoty'
            if (hodnoty[0] < MIN_X) {
                MIN_X = hodnoty[0];
            }
            if (hodnoty[0] > MAX_X) {
                MAX_X = hodnoty[0];
            }
            if (hodnoty[1] < MIN_Y) {
                MIN_Y = hodnoty[1];
            }
            if (hodnoty[1] > MAX_Y) {
                MAX_Y = hodnoty[1];
            }

            // přiřadní do pole na jednom řádku separované hodnoty
            data[i] = hodnoty;
        }
        return data;
    }

    /**
     *
     * @param data
     * @param x
     * @param y
     * @param arguments
     * @param lambda_jmenovatel
     * @return
     */
    public static double spoctiVysledek(double[][] data, double x, double y, Arguments arguments, double lambda_jmenovatel) {
        double z0 = 0;
        for (int i = 0; i < data.length; i++) {
            z0 = updateVysledku(x, data, i, y, arguments, lambda_jmenovatel, z0);
        }
        return z0;
    }

    /**
     *
     * @param x
     * @param data
     * @param i
     * @param y
     * @param arguments
     * @param lambda_spodni
     * @param z0
     * @return
     */
    public static double updateVysledku(double x, double[][] data, int i, double y, Arguments arguments, double lambda_spodni, double z0) {
        double lambda_horni;
        double lambda;
        double v = sqrt((x - data[i][0]) * (x - data[i][0]) + (y - data[i][1]) * (y - data[i][1]));
        lambda_horni = (1 / Math.pow(v + arguments.s, arguments.p));
        //výpočet lambdy
        lambda = lambda_horni / lambda_spodni;
        //výpočet interpolovaných hodnot
        z0 += lambda * data[i][2];
        return z0;
    }

    /**
     *
     * @return
     * @throws IOException
     */
    public static int readInt() throws IOException {
        BufferedReader reader;
        reader = new BufferedReader(new InputStreamReader(System.in));
        return Integer.parseInt(reader.readLine());
    }

    /**
     *
     * @return
     * @throws IOException
     */
    public static char readChar() throws IOException {
        Scanner s = new Scanner(System.in);
        return s.next().charAt(0);
    }
}