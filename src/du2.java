
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Martin
 */
public class du2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Locale.setDefault(new Locale("en", "US"));
        // Vypíše funkci programu a defaultní nastavení parametrů 'p' a 's'.
        System.out.println("Program pro interpolaci naměřených dat metodou inverzní vážené vzdálenosti (IDW).");
        System.out.println("Defaultní nastavení: exponent ve váhové funkci 'p' = 2");
        System.out.printf("%21svyhlazovací koeficient 's' = 0\n", ""); // System.out.printf("% - uvozuje parametr, číslo - počet vypisovaných míst, s - datový typ string", "parametr")
        System.out.printf("%21svelikost mřížky = 100x100\n", "");
                
        /* Uživatel si vybere, zda změní defaultní nastavení (Y) či nikoliv (N).
        Podle zadaného čísla určí parametr 'p' (exponent ve váhové funkci).
        Uzná pouze kladné hodnoty, v jiném případě je program ukončen.*/
        System.out.println("Přejete si změnit hodnotu exponentu 'p' ve váhové funkci? (Y/N)");
        int p = 2;
        char vstup_p = readChar();
        if (vstup_p == 'Y') {
            System.out.println("Zadejte hodnotu exponentu 'p':");
            p = readInt();
            if (p <= 0) {
                System.err.println("Hodnota exponentu 'p' může být pouze kladná!");
                System.exit(1);
            }
        } else if (vstup_p != 'N') {
            System.err.println("Byl zadán špatný vstup!");
            System.exit(1);
        }
                
        /* Uživatel si vybere, zda změní defaultní nastavení (Y) či nikoliv (N).
        Podle zadaného čísla určí parametr 's' (vyhlazovací koeficient). 
        Uzná pouze kladné hodnoty a nulu, v jiném případě je program ukončen.*/
        System.out.println("Přejete si změnit hodnotu vyhlazovacího koeficientu 's'? (Y/N)");
        int s = 0;
        char vstup_s = readChar();
        if (vstup_s == 'Y') {
            System.out.println("Zadejte hodnotu vyhlazovacího koeficientu 's':");
            s = readInt();
            if (s <= 0) {
                System.err.println("Hodnota vyhlazovacího koeficientu 's' může být pouze kladná!");
                System.exit(1);
            }
        } else if (vstup_s != 'N') {
            System.err.println("Byl zadán špatný vstup!");
            System.exit(1);
        }    
        
        /* Uživatel si vybere, zda změní defaultní nastavení (Y) či nikoliv (N).
        Podle zadaných čísel určí velikost mřížky (šířka x výška).
        Uzná pouze kladné hodnoty, v jiném případě je program ukončen.*/
        System.out.println("Přejete si změnit hodnoty výšky a šířky mřížky? (Y/N)");
        int sirka = 100;
        int vyska = 100;
        char vstup_sirka = readChar();
        if (vstup_sirka == 'Y') {
            System.out.println("Zadejte hodnotu šířky mřížky:");
            sirka = readInt();
            if (sirka <= 0) {
                System.err.println("Hodnota šířky mřížky může být pouze kladná!");
                System.exit(1);
            }
            System.out.println("Zadejte hodnotu výšky mřížky:");
            vyska = readInt();
            if (vyska <= 0) {
                System.err.println("Hodnota výšky mřížky může být pouze kladná!");
                System.exit(1);
            }
        } else if (vstup_sirka != 'N') {
            System.err.println("Byl zadán špatný vstup!");
            System.exit(1);
        }
        
        double [][] data;
        
        double min_x = Double.POSITIVE_INFINITY;
        double max_x = Double.NEGATIVE_INFINITY;
        double min_y = Double.POSITIVE_INFINITY;
        double max_y = Double.NEGATIVE_INFINITY;
        
        /*
        
        */
        try {
            BufferedReader br = new BufferedReader(new FileReader("in.csv"));
            String radek;
            
            int pocet_radku = Integer.parseInt(br.readLine());
            data = new double[pocet_radku][]; // pole polí obashující řádky a sloupce
            for (int i = 0; i < pocet_radku; i++) {
                radek = br.readLine();
                String [] string_hodnoty = radek.split(",");
                double [] hodnoty = new double[]{
                    Double.parseDouble(string_hodnoty[0]),
                    Double.parseDouble(string_hodnoty[1]),
                    Double.parseDouble(string_hodnoty[2]),
                };
                if (hodnoty[0]<min_x)
                    min_x = hodnoty[0];
                if (hodnoty[0]>max_x)
                    max_x = hodnoty[0];
                if (hodnoty[1]<min_y)
                    min_y = hodnoty[1];
                if (hodnoty[1]>max_y)
                    max_y = hodnoty[1];
                
                data[i] = hodnoty;
            }
            
            PrintWriter writer;
            try {
                writer = new PrintWriter("out.csv");
                writer.println(sirka*vyska);
                for (double x = min_x; x <= max_x; x += (max_x-min_x)/sirka) {
                    for (double y = min_y; y <= max_y; y += (max_y-min_y)/vyska) {
                        double l_spodni = 0;
                        double z0 = 0;

                        for (int i = 0; i < pocet_radku; i++) {
                            double v=sqrt((x-data[i][0])*(x-data[i][0])+(y-data[i][1])*(y-data[i][1]));
                            l_spodni += 1/Math.pow(v, p);
                        }

                        for (int i = 0; i < pocet_radku; i++) {
                            double v=sqrt((x-data[i][0])*(x-data[i][0])+(y-data[i][1])*(y-data[i][1]));
                            double l = (1/Math.pow(v + s, p))/(l_spodni);

                            z0 += l * data[i][2];
                        } 
                        writer.printf("%f,%f,%f\n", x, y, z0);
                        
                    }
                }
                writer.close();
            } catch (FileNotFoundException ex) {
                System.err.format("Soubor %s nebyl nalezen!","out.csv");
                System.exit(1);
            } catch (IOException ex) {
                System.err.print("Vyskytla se chyba při ukládání hodnot do řádku!");
                System.exit(1);
            }
            
            
        } catch (FileNotFoundException ex) {
            System.err.format("Soubor %s nebyl nalezen!","in.csv");
            System.exit(1);
        } catch (IOException ex) {
            System.err.print("Vyskytla se chyba při načítání hodnot z řádku!");
            System.exit(1);
        }
       
    }
      
    public static int readInt() throws IOException {
        BufferedReader reader;
        reader = new BufferedReader(new InputStreamReader(System.in));
        return Integer.parseInt(reader.readLine());
    }
    
    public static char readChar() throws IOException {
        Scanner s = new Scanner(System.in);
        return s.next().charAt(0);
    }
}