
import java.util.Scanner;

public class Kayttoliittyma {

    private Scanner lukija;
    private Tietokanta tietokanta;

    public Kayttoliittyma(Scanner lukija, Tietokanta tietokanta) {
        this.lukija = lukija;
        this.tietokanta = tietokanta;
    }

    public void kaynnista() {
        System.out.println("===Harjoitustyö===\n");
        OUTER:
        while (true) {
            System.out.println("Valitse vaihtoehdoista: \n"
                    + "Luettele komennot (L)\n"
                    + "Anna komento (1-9)\n"
                    + "Poistu ohjelmasta (X)\n");
           
            System.out.print("Komento: ");
            String komento = lukija.nextLine().trim().toUpperCase();
            switch (komento) {
                case "X":
                    break OUTER;
                case "L":
                    System.out.println(luettele());
                    break;
                default:
                    kutsuTietokantaa(komento);
                    System.out.println("");
                    break;
            }
        }
    }

    public String luettele() {
        return "Komennot \n"
                + "1. Luo tietokanta ja taulut\n"
                + "2. Uusi paikka\n"
                + "3. Uusi asiakas\n"
                + "4. Uusi paketti\n"
                + "5. Uusi tapahtuma\n"
                + "6. Hae tapahtumat \n"
                + "7. Hae asiakkaan paketit ja tapahtumat\n"
                + "8. Hae paikan tapahtumamäärät\n"
                + "9. Tehokkuustesti\n";
    }

    public void kutsuTietokantaa(String komento) {
        switch (komento) {
            case "1":
                tietokanta.luoTaulutTietokantaan();

                break;
            case "2":
                System.out.print("Anna paikan nimi: ");
                String paikka = lukija.nextLine().trim();
                tietokanta.lisaaPaikka(paikka);
                break;
            case "3":
                System.out.print("Anna asiakkaan nimi: ");
                String asiakas = lukija.nextLine().trim();
                tietokanta.lisaaAsiakas(asiakas);
                break;
            case "4":
                System.out.print("Anna paketin seurantakoodi: ");               
                String koodi = lukija.nextLine();
                System.out.print("Anna asiakkaan nimi: ");
                String asiakasId = lukija.nextLine().trim();
                tietokanta.lisaaPaketti(koodi, asiakasId);
                break;
            case "5":
                System.out.print("Anna seurantakoodi: ");
                String tapahtumaKoodi = lukija.nextLine().trim();
                System.out.print("Anna paikka: ");
                String tapahtumaPaikka = lukija.nextLine().trim();
                System.out.print("Anna kuvaus: ");
                String tapahtumaKuvaus = lukija.nextLine();
                tietokanta.lisaaTapahtuma(tapahtumaKoodi, tapahtumaPaikka, tapahtumaKuvaus);
                break;
            case "6":
                System.out.print("Anna paketin seurantakoodi: ");
                String seurantaKoodi = lukija.nextLine().trim();
                tietokanta.haePaketinTapahtumat(seurantaKoodi);
                break;
            case "7":
                System.out.print("Anna asiakkaan nimi: ");
                String asiakasNimi = lukija.nextLine().trim();
                tietokanta.haeAsiakkaanPaketitJaTapahtumat(asiakasNimi);
                break;
            case "8":
                System.out.print("Anna paikan nimi: ");
                String paikanNimi = lukija.nextLine().trim();
                System.out.print("Anna päivämäärä(pp.kk.vvvv): ");
                String paivamaara = lukija.nextLine().trim();
                tietokanta.haePaketinTapahtumatPaivana(paikanNimi, paivamaara);
                break;
            case "9":
                System.out.println("Tehokkuustesti\n"
                        + "(1) Testi ilman indeksiä\n"
                        + "(2) Testi indeksin kanssa\n");
                System.out.print("Syötä numero: ");
                String testinumero = lukija.nextLine();
                if (testinumero.trim().equals("1")) {
                    String polku = "jdbc:sqlite:testi_ilman_indeksia.db";
                    Tietokanta testi1 = new Tietokanta(polku);
                    testi1.poistaTaulutKannasta();
                    testi1.luoTaulutTietokantaan();
                    testi1.tehokkuustestiIlmanIndeksia();
                } else if (testinumero.trim().equals("2")) {
                    String polku = "jdbc:sqlite:testi_indekseilla.db";
                    Tietokanta testi2 = new Tietokanta(polku);
                    testi2.poistaTaulutKannasta();
                    testi2.luoTaulutTietokantaan();
                    testi2.tehokkuustestiIndeksilla();
                }

                break;
            default:
                break;
        }
    }

}
