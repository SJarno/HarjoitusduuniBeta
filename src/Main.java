
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jarno
 */
         
public class Main {

    public static void main(String[] args) {
        String polku = "jdbc:sqlite:C:/Users/Jarno/Documents/NetbeansProjects/HarjoitusduuniBeta/harjoitus.db";
        Scanner scanner = new Scanner(System.in);
        Tietokanta tietokanta = new Tietokanta(polku);
        Kayttoliittyma kayttis = new Kayttoliittyma(scanner, tietokanta);
        kayttis.kaynnista();
        //tietokanta.luoYhteysJaTietokanta();
        //tietokanta.luoTaulutTietokantaan();
        
                
        //tietokanta.haeAsiakasNimi("Jere");
        //System.out.println(tietokanta.haeAsiakasNimi("Jarno"));
        //System.out.println(tietokanta.haeAsiakasId("Jarno"));
        //System.out.println(tietokanta.haeAsiakasId("Jarno"));
        //tietokanta.lisaaPaketti(300, "J");
        //System.out.println(tietokanta.haePaikkaId("Koti"));
        //System.out.println(tietokanta.haeSeurantakoodi("100"));
        //System.out.println(tietokanta.haePaivamaaraId("16.02.2020"));
        //tietokanta.tehokkuusTesti();
        //tietokanta.lisaaTapahtuma(""+1, "P"+1, "Testi"+1);
        //tietokanta.haePaketinTapahtumatPaivana("Kalmankuja 13", "16.02.2020");
        //tietokanta.lisaaAsiakas("Testi");
        //tietokanta.haePaketinTapahtumat("999");
        //System.out.println("");
        
        
        
        
        
                
        
    }
    
}
