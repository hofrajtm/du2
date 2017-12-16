# du2

## Funkce programu
Program slouží k interpolaci naměřených dat metodou inverzní vážené vzdálenosti (IDW). Interpolace je postup, který slouží k výpočtu
odhadované měřené veličiny v okolí bodů se známou hodnotou.

### Vstup
Vstupní soubor obsahuje na prvním řádku celé číslo, které odpovídá počtu řádků s naměřenými hodnotami. Na dalších řádcích jsou
desetiná čísla (s desetinnou tečkou) oddělená čárkami, které v tomto pořadí odpovádají x-ové souřadnici, y-ové souřadnici a
naměřené veličine ve známém bodech. Je podmíkou, že vstupní soubor musí být pojmenovaný 'in' a ve formátu CSV (*in.csv*).

### Výstup
Výstupní soubor obsahuje sto řádků se sto desetinnými čísly (s desetinnou tečkou) oddělených čárkami, které v základním nastavení
odpovádají interpolovaným hodnotám v mřížových bodech sítě obsahující 100 x 100 bodů. Výstupní soubor je pojmenovaný 'out' a
je ve formátu CSV (*out.csv*).

## Metoda IDW
Metoda inverzní vážené vzdálenosti je jednou z nejjednodušších a zároveň nejpoužívanějších metod prostorové interpolace. Jeho podstatou
je vážený průměr, jehož váhy jsou nepřímo úměrné mocnině vzdálenosti mezi interpolovaným bodem a body se známou naměřenou veličinou.
Charakteristický je tak vyšší vliv hodnot blízkých interpolovanému bodu, než vzdálenějších a tedy jejich vyšší vliv na výsledek.

Vzorec: ![alt text](https://github.com/hofrajtm/du2/blob/master/vzorec_vypoctu.gif), kde
- *z<sub>0</sub><sup>*</sup>* - hodnota interpolovaného bodu
- *r<sub>i</sub>* - vzdálenost bodu, kde interpolujeme od i-tého bodu měření
- *r<sub>j</sub>* - suma vzdáleností mezi bodu, kde interpolujeme od všech bodů měření
- *α* - exponent ve váhové funkce
- *s* - vyhlazovací koeficient
              
## Zadávané parametry
Program je neinteraktivní, avšak je možné upravovat v argumenty parametry výpočtu. Volenými parametry jsou: exponent ve váhové funkci, vyhlazovací
koeficient a velikost mřížky (šířka x výška).
Přednastavené hodnoty jsou: exponent ve váhové funkci *p* = 2, vyhlazovací koeficient *s* = 0 a velikost mřížky 100 x 100.

### Exponent ve váhové funkci
Jako parametr *p*, tedy exponent ve váhové funkci, je možné zadat pouze kladné číslo. V jiném případě program skončí. Čím vyšší je
hodnota parametru *p*, tím více narůstá vliv nejbližšího měřeného bodu na hodnotu bodu interpolovaného.

### Vyhlazovací koeficient
Dalším voleným parametrem je vyhlazovací koeficient *s*, který může nabývat pouze kladných hodnot a nuly. V jiném případě program skončí.
Čím vyšší je hodnota parametru *s*, tím více narůstá míra shlazení interpolační plochy. Nejedná se však již o přesnou interpolaci.

### Velikost mřížky
Posledním voleným parametrem jsou rozměry mřížky, tedy šířka a výška. Tyto parametry je možné vybírat v intervalu <0,Int.MAX_VALUE>, v jiném případě
program skončí. Čím větší je velikost mřížky, tím větší je i počet bodů, ve kterých probíhá interpolace.

## Ukázka interpolace IDW
![alt text](https://github.com/hofrajtm/du2/blob/master/interpolace_mapa.png)
Na obrázku jsou zobrazeny body se známýmy hodnotami, v jejich okolí pak interpolované hodnoty odpovídají odstínů, hnědé barvy. Čím světlejší, tím menší hodnota, čím tmavší, tím vyšší hodnota.
