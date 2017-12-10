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
 *
 * @author Martin
 */
public class du2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // nastaví na anglickou lokaci, např. desetinná čárka se změní na tečku
        Locale.setDefault(new Locale("en", "US"));

        // Vypíše funkci programu a defaultní nastavení parametrů 'p' a 's'.
        System.out.println("Program pro interpolaci naměřených dat metodou inverzní vážené vzdálenosti (IDW).");
        System.out.println("Defaultní nastavení: exponent ve váhové funkci 'p' = 2");
        System.out.printf("%21svyhlazovací koeficient 's' = 0\n", ""); // formátovaný výstup ("% - uvozuje parametr, číslo - počet vypisovaných míst, s - datový typ string", "parametr")
        System.out.printf("%21svelikost mřížky = 100 x 100\n", "");
                
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
            if (s < 0) {
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
        char vstup_mrizka = readChar();
        if (vstup_mrizka == 'Y') {
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
        } else if (vstup_mrizka != 'N') {
            System.err.println("Byl zadán špatný vstup!");
            System.exit(1);
        }
                 
        try {
            // načtení hodnot z určeného souboru
            BufferedReader br = new BufferedReader(new FileReader("in.csv"));
            // definuje proměnné min a max 'x' a 'y'
            double min_x = Double.POSITIVE_INFINITY;
            double max_x = Double.NEGATIVE_INFINITY;
            double min_y = Double.POSITIVE_INFINITY;
            double max_y = Double.NEGATIVE_INFINITY;
            // načte počet řádku dle čísla v prvním řádku
            int pocet_radku = Integer.parseInt(br.readLine());
            // pole polí obasahující pozici [sloupce] a [řádku]
            double [][] data = new double[pocet_radku][];
            
            for (int i = 0; i < pocet_radku; i++) {
                String radek = br.readLine(); // dfinuje řádek jako řezězec a načte hodnoty ze všech řádků do proměnné 'radek'
                String [] string_hodnoty = radek.split(","); // rozdělí řezězec na tři řetězce podle čárek
                double [] hodnoty = new double[]{ // pro každý řádek přemění řetězce na pole o třech hodnotách double
                    // ve složené závorce si ručně nastavím jenotlivé položky pole a přemění string na double
                    Double.parseDouble(string_hodnoty[0]),
                    Double.parseDouble(string_hodnoty[1]),
                    Double.parseDouble(string_hodnoty[2]),
                };
                
                // pro urychlení operace hledá min a max již při načítání hodnot do proměnné 'hodnoty'
                if (hodnoty[0]<min_x)
                    min_x = hodnoty[0];
                if (hodnoty[0]>max_x)
                    max_x = hodnoty[0];
                if (hodnoty[1]<min_y)
                    min_y = hodnoty[1];
                if (hodnoty[1]>max_y)
                    max_y = hodnoty[1];
                
                // přiřadní do pole na jednom řádku separované hodnoty
                data[i] = hodnoty;
            }
            
            // uložení hodnot do určeného souboru
            PrintWriter writer;
            try {
                writer = new PrintWriter("out.csv");
                writer.println(sirka*vyska);
                // projde celý řádek od min s krokem dle definované hodnoty mřížky 'sirka' po max
                for (double x = min_x; x <= max_x; x += (max_x-min_x)/sirka) {
                    // projde všechny sloupce od min s krokem dle definované hodnoty mřížky 'vyska' po max
                    for (double y = min_y; y <= max_y; y += (max_y-min_y)/vyska) {
                        
                        double lambda_spodni = 0;
                        double lambda_horni = 0;
                        double lambda = 0;
                        double z0 = 0;
                        // výpočet jmenovatele lambdy
                        for (int i = 0; i < pocet_radku; i++) {
                            double v=sqrt((x-data[i][0])*(x-data[i][0])+(y-data[i][1])*(y-data[i][1]));
                            lambda_spodni += 1/Math.pow(v, p);
                        }
                        //výpočet čitatele lambdy
                        for (int i = 0; i < pocet_radku; i++) {
                            double v=sqrt((x-data[i][0])*(x-data[i][0])+(y-data[i][1])*(y-data[i][1]));
                            lambda_horni = (1/Math.pow(v + s, p));
                            //výpočet lambdy
                            lambda = lambda_horni/lambda_spodni;
                            //výpočet interpolovaných hodnot
                            z0 += lambda * data[i][2];
                        }
                        
                        writer.printf("%.2f,", z0);                        
                    }
                    writer.printf("\n");
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