
import java.util.Scanner;

/*
 * 
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
