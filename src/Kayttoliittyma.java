
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
        while (true) {
            System.out.println("Valitse vaihtoehdoista: \n"
                    + "Luettele komennot (L)\n"
                    + "Poistu ohjelmasta (X)\n"
                    + "Anna komento (1-9)\n");
            System.out.print("Komento: ");
            String komento = lukija.nextLine().toUpperCase();
            if (komento.equals("X")) {
                break;
            } else if (komento.equals("L")) {
                System.out.println(luottele());
                
            } else {
                kutsuTietokantaa(komento);
                System.out.println("");
                
            }
        }
    }
    public String luottele() {
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
                String paikka = lukija.nextLine();
                tietokanta.lisaaPaikka(paikka);
                break;
            case "3":
                System.out.print("Anna asiakkaan nimi: ");
                String asiakas = lukija.nextLine();
                tietokanta.lisaaAsiakas(asiakas);
                break;
            case "4":
                System.out.print("Anna paketin seurantakoodi: ");
                //int koodi = Integer.valueOf(lukija.nextLine());
                String koodi = lukija.nextLine();
                System.out.print("Anna asiakkaan nimi: ");
                String asiakasId = lukija.nextLine();
                tietokanta.lisaaPaketti(koodi, asiakasId);
                break;
            case "5":
                System.out.print("Anna seurantakoodi: ");
                String tapahtumaKoodi = lukija.nextLine();
                System.out.print("Anna paikka: ");
                String tapahtumaPaikka = lukija.nextLine();
                System.out.print("Anna kuvaus: ");
                String tapahtumaKuvaus = lukija.nextLine();
                tietokanta.lisaaTapahtuma(tapahtumaKoodi, tapahtumaPaikka, tapahtumaKuvaus);
                break;
            case "6":
                System.out.print("Anna paketin seurantakoodi: ");
                String seurantaKoodi = lukija.nextLine();
                tietokanta.haePaketinTapahtumat(seurantaKoodi);
                break;
            case "7":
                System.out.print("Anna asiakkaan nimi: ");
                String asiakasNimi = lukija.nextLine();
                tietokanta.haeAsiakkaanPaketitJaTapahtumat(asiakasNimi);
                break;
            case "8":
                System.out.print("Anna paikan nimi: ");
                String paikanNimi = lukija.nextLine();
                System.out.print("Anna päivämäärä: ");
                String paivamaara = lukija.nextLine();
                tietokanta.haePaketinTapahtumatPaivana(paikanNimi, paivamaara);
                break;
            case "9":
                System.out.println("Tehokkuustesti");
                tietokanta.tehokkuusTesti();
                break;
            default:
                break;
        }
    }
    
}
