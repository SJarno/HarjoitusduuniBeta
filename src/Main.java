
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Jarno Saastamoinen
 */
public class Main {

    public static void main(String[] args) {        
        String polku = "jdbc:sqlite:harjoitus.db";
        Scanner scanner = new Scanner(System.in);
        Tietokanta tietokanta = new Tietokanta(polku);
        Kayttoliittyma kayttis = new Kayttoliittyma(scanner, tietokanta);
        kayttis.kaynnista();

    }

}
