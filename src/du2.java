
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

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
            System.out.println("Zadejte hodnotu exponentu 'p': ");
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
            System.out.println("Zadejte hodnotu vyhlazovacího koeficientu 's'");
            s = readInt();
            if (s <= 0) {
            System.err.println("Hodnota vyhlazovacího koeficientu 's' může být pouze kladná!");
            System.exit(1);
            }
        } else if (vstup_s != 'N') {
            System.err.println("Byl zadán špatný vstup!");
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
