
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Tietokanta {

    private final String tietokannanPolku;

    public Tietokanta(String polku) {
        this.tietokannanPolku = polku;
    }

    public Connection luoYhteysJaTietokanta() {
        Connection yhteys = null;
        try {
            yhteys = DriverManager.getConnection(tietokannanPolku);

        } catch (SQLException e) {
            System.out.println("Virhe yhteydessä: " + e.getMessage());
        }

        return yhteys;
    }

    public void luoTaulutTietokantaan() {
        /*1. Luo sovelluksen tarvitsemat taulut tyhjään tietokantaan 
    (tätä toimintoa voidaan käyttää, kun tietokantaa ei ole vielä olemassa).*/
        Connection yhteys = null;
        try {
            yhteys = this.luoYhteysJaTietokanta();
            if (yhteys != null) {
                Statement s = yhteys.createStatement();
                s.execute("PRAGMA foreign_keys = ON;");
                s.execute("CREATE TABLE IF NOT EXISTS Paikat (id INTEGER PRIMARY KEY, "
                        + "paikka TEXT UNIQUE NOT NULL CHECK (LENGTH(paikka) > 0));");
                s.execute("CREATE TABLE IF NOT EXISTS Asiakas (id INTEGER PRIMARY KEY,"
                        + "nimi TEXT UNIQUE NOT NULL CHECK (LENGTH(nimi) > 0));");
                s.execute("CREATE TABLE IF NOT EXISTS Paketti (id INTEGER PRIMARY KEY,"
                        + "seurantakoodi INTEGER UNIQUE NOT NULL CHECK (LENGTH(seurantakoodi) > 0),"
                        + "asiakas_id INTEGER NOT NULL REFERENCES Asiakas);");
                s.execute("CREATE TABLE IF NOT EXISTS Tapahtuma (id INTEGER PRIMARY KEY,"
                        + "seurantakoodi_id INTEGER NOT NULL REFERENCES Paketti,"
                        + "paikka_id INTEGER NOT NULL REFERENCES Paikat,"
                        + "kuvaus TEXT, paivamaara DATE);");

            }
            System.out.println("Tietokanta luotu");

        } catch (SQLException e) {
            System.out.println("Virhe taulujen luomisessa: " + e.getMessage());
        } finally {
            if (yhteys != null) {
                try {
                    yhteys.close();
                } catch (SQLException ex) {
                    System.out.println("Virhe yhteyden sulkemisessa: " + ex.getMessage());
                }
            }
        }
    }

    public void lisaaPaikka(String paikka) {
        /*2. Lisää uusi paikka tietokantaan, kun annetaan paikan nimi.*/
        if (paikka.isEmpty()) {
            System.out.println("Tyhjää paikkaa ei saa syöttää");
            return;
        }
        int paikkaKannassa = this.haePaikkaId(paikka);
        if (paikkaKannassa != -1) {
            System.out.println("Paikka on jo olemassa");
            return;
        }
        PreparedStatement stm = null;
        String lisaa = "INSERT INTO Paikat (paikka) VALUES(?)";
        Connection yhteys = null;
        yhteys = this.luoYhteysJaTietokanta();
        try {
            stm = yhteys.prepareStatement(lisaa);
            stm.setString(1, paikka);
            stm.execute();
            System.out.println("Paikka lisätty");
        } catch (SQLException e) {
            System.out.println("Virhe paikan lisäämisessä: " + e.getMessage());

        } finally {
            if (yhteys != null) {
                try {
                    yhteys.close();
                } catch (SQLException ex) {
                    System.out.println("Virhe yhteyden sulkemisessa: " + ex.getMessage());
                }
            }
        }
    }

    public void lisaaAsiakas(String asiakas) {
        /*3. Lisää uusi asiakas tietokantaan, kun annetaan asiakkaan nimi.*/
        if (asiakas.isEmpty()) {
            System.out.println("Tyhjää riviä ei saa syöttää.");
            return;
        }
        if (this.haeAsiakasId(asiakas) != -1) {
            System.out.println("Asiakas on jo olemassa.");
            return;
        }
        String lisaa = "INSERT INTO Asiakas (nimi) VALUES(?)";
        Connection yhteys = null;
        try {
            yhteys = this.luoYhteysJaTietokanta();
            PreparedStatement stm = yhteys.prepareStatement(lisaa);
            stm.setString(1, asiakas);
            stm.execute();
            System.out.println("Asiakas lisätty");

        } catch (SQLException e) {
            System.out.println("Virhe asiakkaan lisäämisessä: " + e.getMessage());

        } finally {
            if (yhteys != null) {
                try {
                    yhteys.close();
                } catch (SQLException ex) {
                    System.out.println("Virhe yhteyden sulkemisessa: " + ex.getMessage());
                }
            }
        }
    }

    public void lisaaPaketti(String seurantakoodi, String asiakas) {
        /*4.Lisää uusi paketti tietokantaan, kun annetaan paketin seurantakoodi 
        ja asiakkaan nimi. */
        int muunnosLuvuksi = 0;
        try {
            muunnosLuvuksi = Integer.valueOf(seurantakoodi.trim());
        } catch (NumberFormatException e) {
            System.out.println("Syöte tulee olla numeromuodossa");
            return;
        }

        int koodiKannassa = this.haeSeurantakoodi(seurantakoodi.trim());
        if (koodiKannassa != -1) {
            System.out.println("Seurantakoodi on jo tietokannassa");
            return;
        }

        int haettavaId = 0;
        if (!asiakas.isEmpty()) {
            String nimiKannassa = haeAsiakasNimi(asiakas);
            if (asiakas.equals(nimiKannassa)) {
                haettavaId = this.haeAsiakasId(asiakas);
            } else {
                System.out.println("Asiakasta ei löydy");
                return;
            }
        } else {
            System.out.println("Asiakkaan nimi ei saa olla tyhjä");
            return;
        }

        String lisaa = "INSERT INTO Paketti (seurantakoodi, asiakas_id) VALUES (?,?)";
        Connection yhteys = null;
        yhteys = this.luoYhteysJaTietokanta();
        try {
            yhteys = this.luoYhteysJaTietokanta();
            PreparedStatement stm = yhteys.prepareStatement(lisaa);
            stm.setInt(1, muunnosLuvuksi);
            stm.setInt(2, haettavaId);
            stm.execute();
            System.out.println("Paketti lisätty");
        } catch (SQLException e) {
            System.out.println("Seurantakoodi virheellinen:" + e.getMessage());
        } finally {
            if (yhteys != null) {
                try {
                    yhteys.close();
                } catch (SQLException ex) {
                    System.out.println("Virhe yhteyden sulkemisessa: " + ex.getMessage());
                }
            }
        }

    }

    public void lisaaTapahtuma(String seurantakoodi, String paikka, String kuvaus) {
        /*5. Lisää uusi tapahtuma tietokantaan, kun annetaan paketin seurantakoodi, 
    tapahtuman paikka sekä kuvaus. 
    Paketin ja paikan tulee olla valmiiksi tietokannassa.*/
        if (seurantakoodi.isEmpty()) {
            System.out.println("Seurantakoodi ei saa olla tyhjä");
            return;
        }
        if (paikka.isEmpty()) {
            System.out.println("Paikka ei saa olla tyhjä");
            return;
        }

        int haettavaKoodi = 0;
        try {
            haettavaKoodi = Integer.valueOf(seurantakoodi.trim());

        } catch (NumberFormatException e) {
            System.out.println("Seurantakoodi täytyy olla numeromuodossa.");
            return;
        }
        int koodiKannassa = this.haeSeurantakoodi(seurantakoodi.trim());
        if (koodiKannassa == -1) {
            System.out.println("Tarkista seurantakoodi");
            return;
        }

        int paikkaKannassa = this.haePaikkaId(paikka);
        if (paikkaKannassa == -1) {
            System.out.println("Tarkista paikka");
            return;
        }

        String lisaa = "INSERT INTO Tapahtuma (seurantakoodi_id, paikka_id, kuvaus, paivamaara) VALUES (?,?,?,?)";
        Connection yhteys = null;
        try {
            yhteys = this.luoYhteysJaTietokanta();
            PreparedStatement kysely = yhteys.prepareStatement(lisaa);
            kysely.setInt(1, koodiKannassa);
            kysely.setInt(2, paikkaKannassa);
            kysely.setString(3, kuvaus);
            kysely.setString(4, haeAika());
            kysely.execute();

            System.out.println("Tapahtuman lisäys onnistui.");
        } catch (SQLException e) {
            System.out.println("Virhe tapahtuman lisäämisessä: " + e.getMessage());

        } finally {
            if (yhteys != null) {
                try {
                    yhteys.close();
                } catch (SQLException ex) {
                    System.out.println("Virhe yhteyden sulkemisessa: " + ex.getMessage());

                }
            }
        }
    }

    public void haePaketinTapahtumat(String koodi) {
        /*6. Hae kaikki paketin tapahtumat seurantakoodin perusteella.*/

        if (koodi.isEmpty()) {
            System.out.println("Seurantakoodi ei saa olla tyhjä");
            return;
        }
        int haettavaKoodi = 0;
        try {
            haettavaKoodi = Integer.valueOf(koodi.trim());
        } catch (NumberFormatException e) {
            System.out.println("Seurantakoodi täytyy olla numeromuodossa");
            return;
        }
        int koodiKannassa = this.haeSeurantakoodi(koodi.trim());
        if (koodiKannassa == -1) {
            System.out.println("Tarkista seurantakoodi");
            return;
        }
        String lisaa
                = "SELECT paivamaara, paikka, kuvaus \n"
                + "FROM Tapahtuma LEFT JOIN Paikat, Paketti \n"
                + "ON Paketti.id = seurantakoodi_id AND paikka_id = Paikat.id\n"
                + "AND Paketti.seurantakoodi =?\n"
                + "GROUP BY paivamaara;";
        Connection yhteys = null;
        try {
            yhteys = this.luoYhteysJaTietokanta();
            PreparedStatement kysely = yhteys.prepareStatement(lisaa);
            kysely.setInt(1, haettavaKoodi);
            ResultSet tulos = kysely.executeQuery();
            if (tulos.getString("paivamaara").equals(null)) {
                
            } 

            while (tulos.next()) {
                System.out.println(tulos.getString("paivamaara") + ", "
                        + tulos.getString("paikka") + ", "
                        + tulos.getString("kuvaus"));

            }

        } catch (SQLException ex) {
            System.out.println("Ei tapahtumia");
            System.out.println("Virhe tapahtumien hakemisessa: " + ex.getMessage());

        } finally {
            if (yhteys != null) {
                try {
                    yhteys.close();
                } catch (SQLException ex) {
                    System.out.println("Virhe yhteyden sulkemisessa");
                }
            }
        }

    }

    public void haeAsiakkaanPaketitJaTapahtumat(String asiakas) {
        /*7. Hae kaikki asiakkaan paketit ja niihin liittyvien tapahtumien määrä.*/

        if (asiakas.isEmpty()) {
            System.out.println("Asiakkaan nimi ei saa olla tyhjä");
            return;
        }
        int nimiKannassa = this.haeAsiakasId(asiakas);
        if (nimiKannassa == -1) {
            System.out.println("Tarkista asiakkaan nimi");
            return;
        }
        String lisaa
                = "SELECT Pa.seurantakoodi, COUNT(T.seurantakoodi_id)\n"
                + "FROM Paketti Pa LEFT JOIN Tapahtuma T\n"
                + "ON Pa.id = T.seurantakoodi_id\n"
                + "LEFT JOIN Asiakas A ON A.id = Pa.asiakas_id WHERE A.nimi =?\n"
                + "GROUP BY Pa.seurantakoodi\n"
                + "ORDER BY COUNT(T.seurantakoodi_id) DESC;";
        Connection yhteys = null;
        try {
            yhteys = this.luoYhteysJaTietokanta();
            PreparedStatement kysely = yhteys.prepareStatement(lisaa);
            kysely.setString(1, asiakas);
            ResultSet tulos = kysely.executeQuery();
            if (!tulos.next()) {
                System.out.println("Ei tapahtumia");
            }
            while (tulos.next()) {
                System.out.println(tulos.getInt("seurantakoodi")
                        + ", " + tulos.getInt("COUNT(T.seurantakoodi_id)") + " tapahtumaa");

            }

        } catch (SQLException e) {
            System.out.println("Virhe: " + e.getMessage());
        } finally {
            if (yhteys != null) {
                try {
                    yhteys.close();
                } catch (SQLException ex) {
                    System.out.println("Virhe yhteyden sulkemisessa");
                }
            }
        }

    }

    public void haePaketinTapahtumatPaivana(String paikka, String paivamaara) {
        /*8. Hae annetusta paikasta tapahtumien määrä tiettynä päivänä.*/

        if (paikka.isEmpty()) {
            System.out.println("Paikka ei saa olla tyhjä");
        }
        if (paivamaara.isEmpty()) {
            System.out.println("Päivämäärä ei saa olla tyhjä");
            return;
        }
        int paikkaKannassa = this.haePaikkaId(paikka);
        if (paikkaKannassa == -1) {
            System.out.println("Paikkaa ei löytynyt");
            return;
        }
        int paivamaaraKannassa = this.haePaivamaaraId(paivamaara);
        if (paivamaaraKannassa == -1) {
            System.out.println("Päivämäärää ei löytynyt");
            return;
        }
        Connection yhteys = null;
        //paivamaara = paivamaara + "%";
        String lisaa = "SELECT COUNT(T.id)\n"
                + "FROM Tapahtuma T LEFT JOIN Paikat P\n"
                + "ON T.paikka_id = P.id WHERE P.paikka =?\n"
                + "AND T.paivamaara LIKE ?";
        try {
            yhteys = this.luoYhteysJaTietokanta();
            PreparedStatement kysely = yhteys.prepareStatement(lisaa);
            kysely.setString(1, paikka);
            kysely.setString(2, paivamaara + "%");
            ResultSet tulos = kysely.executeQuery();
            while (tulos.next()) {
                System.out.println("Tapahtumien määrä: " + tulos.getInt("COUNT(T.id)"));
            }

        } catch (SQLException e) {
            System.out.println("Virhe jossain: " + e.getMessage());
        }
    }

    public void tehokkuustestiIlmanIndeksia() {
        /*9. Suorita tietokannan tehokkuustesti ilmman indeksejä.*/
        PreparedStatement stm = null;
        String lisaaPaikka = "INSERT INTO Paikat (paikka) VALUES(?)";
        String lisaaAsiakas = "INSERT INTO Asiakas (nimi) VALUES(?)";
        String lisaaPaketti = "INSERT INTO Paketti (seurantakoodi, asiakas_id) VALUES (?,?)";
        String lisaaTapahtuma = "INSERT INTO Tapahtuma (seurantakoodi_id, paikka_id, kuvaus, paivamaara) VALUES (?,?,?,?)";
        String haePakettienMaara = "SELECT COUNT(seurantakoodi) FROM Paketti WHERE asiakas_id = (?)";
        String haeTapahtumienMaara = "SELECT COUNT(seurantakoodi_id) FROM Tapahtuma WHERE seurantakoodi_id = (?)";

        Connection yhteys = null;
        yhteys = this.luoYhteysJaTietokanta();

        try {
            //lisätään paikat
            long tehokkuustestiAlku = System.nanoTime();
            Statement s = yhteys.createStatement();
            s.execute("BEGIN TRANSACTION");
            stm = yhteys.prepareStatement(lisaaPaikka);
            for (int i = 1; i <= 1000; i++) {
                stm.setString(1, "P" + i);
                stm.execute();

            }
            System.out.println("Paikat lisätty");
            long tehokkuustestiYksiLoppu = System.nanoTime();
            //lisätaan asiakkaat
            long tehokkuustestiKaksiAlku = System.nanoTime();
            stm = yhteys.prepareStatement(lisaaAsiakas);
            for (int i = 1; i < 1000; i++) {
                stm.setString(1, "A" + i);
                stm.execute();
            }
            System.out.println("Asiakkaat lisätty");
            long tehokkuustestiKaksiLoppu = System.nanoTime();
            //lisätään paketit
            long tehokkuustestiKolmeAlku = System.nanoTime();
            stm = yhteys.prepareStatement(lisaaPaketti);
            for (int i = 1; i <= 1000; i++) {
                stm.setInt(1, i);
                stm.setInt(2, 1);
                stm.execute();
            }
            System.out.println("Paketit lisätty");
            long tehokkuustestiKolmeLoppu = System.nanoTime();
            //lisätäään miljoona tapahtumaa
            long tehokkuustestiNeljaAlku = System.nanoTime();
            stm = yhteys.prepareStatement(lisaaTapahtuma);
            for (int i = 1; i <= 1000000; i++) {
                stm.setInt(1, 1);
                stm.setInt(2, 1);
                stm.setString(3, "Testi" + i);
                stm.setString(4, this.haeAika());
                stm.execute();
            }

            System.out.println("Tapahtumat lisätty");
            long tehokkuustestiNeljaLoppu = System.nanoTime();

            s.execute("COMMIT");
            long tehokkuustestiViisiAlku = System.nanoTime();
            //Suoritetaan tuhat kyselyä, joista jokaisessa haetaan jonkin asiakkaan pakettien määrä.
            stm = yhteys.prepareStatement(haePakettienMaara);
            for (int i = 1; i <= 1000; i++) {
                stm.setInt(1, i);
                ResultSet tulos = stm.executeQuery();
                while (tulos.next()) {
                    System.out.println("Pakettien määrä: " + tulos.getInt("COUNT(seurantakoodi)"));
                }
            }
            long tehokkuustestiViisiLoppu = System.nanoTime();

            long tehokkuustestiKuusiAlku = System.nanoTime();
            //Suoritetaan tuhat kyselyä, joista jokaisessa haetaan jonkin paketin tapahtumien määrä.
            stm = yhteys.prepareStatement(haeTapahtumienMaara);
            for (int i = 1; i <= 1000; i++) {
                stm.setInt(1, i);
                ResultSet tulos = stm.executeQuery();
                while (tulos.next()) {
                    System.out.println("Tapahtumien määrä: " + tulos.getInt("COUNT(seurantakoodi_id)"));
                }

            }
            long tehokkuustestiKuusiLoppu = System.nanoTime();

            long tehokkuustestiLoppu = System.nanoTime();

            System.out.println("Testiin yksi kului aikaa: " + (tehokkuustestiYksiLoppu - tehokkuustestiAlku) / 1e9 + " sekuntia");
            System.out.println("Testiin kaksi kului aikaa: " + (tehokkuustestiKaksiLoppu - tehokkuustestiKaksiAlku) / 1e9 + " sekuntia");
            System.out.println("Testiin kolme kului aikaa: " + (tehokkuustestiKolmeLoppu - tehokkuustestiKolmeAlku) / 1e9 + " sekuntia");
            System.out.println("Testiin neljä kului aikaa: " + (tehokkuustestiNeljaLoppu - tehokkuustestiNeljaAlku) / 1e9 + " sekuntia");
            System.out.println("Testiin viisi kului aikaa: " + (tehokkuustestiViisiLoppu - tehokkuustestiViisiAlku) / 1e9 + " sekuntia");
            System.out.println("Testiin kuusi kului aikaa: " + (tehokkuustestiKuusiLoppu - tehokkuustestiKuusiAlku) / 1e9 + "sekuntia");
            System.out.println("Aikaa kului yhteensä: " + (tehokkuustestiLoppu - tehokkuustestiAlku) / 1e9 + " sekuntia");
        } catch (SQLException e) {
            System.out.println("Virhe: " + e.getMessage());

        } finally {
            if (yhteys != null) {
                try {
                    yhteys.close();
                } catch (SQLException ex) {
                    System.out.println("Virhe yhteyden sulkemisessa: " + ex.getMessage());
                }
            }
        }

    }

    public void tehokkuustestiIndeksilla() {
        //suorita tehokkustesti indekseillä
        PreparedStatement stm = null;
        String lisaaPaikka = "INSERT INTO Paikat (paikka) VALUES(?)";
        String lisaaAsiakas = "INSERT INTO Asiakas (nimi) VALUES(?)";
        String lisaaPaketti = "INSERT INTO Paketti (seurantakoodi, asiakas_id) VALUES (?,?)";
        String lisaaTapahtuma = "INSERT INTO Tapahtuma (seurantakoodi_id, paikka_id, kuvaus, paivamaara) VALUES (?,?,?,?)";
        String haePakettienMaara = "SELECT COUNT(seurantakoodi) FROM Paketti WHERE asiakas_id = (?)";
        String haeTapahtumienMaara = "SELECT COUNT(seurantakoodi_id) FROM Tapahtuma WHERE seurantakoodi_id = (?)";
        String pakettiIndeksi = "CREATE INDEX idx_seurantakoodi ON Paketti (asiakas_id)";
        String tapahtumaIndeksi = "CREATE INDEX idx_seurantakoodi_id ON Tapahtuma (seurantakoodi_id)";

        Connection yhteys = null;
        yhteys = this.luoYhteysJaTietokanta();

        try {
            //lisätään indeksitä
            stm = yhteys.prepareStatement(pakettiIndeksi);
            stm.execute();
            stm = yhteys.prepareStatement(tapahtumaIndeksi);
            stm.execute();
            //lisätään paikat
            long tehokkuustestiAlku = System.nanoTime();
            Statement s = yhteys.createStatement();
            s.execute("BEGIN TRANSACTION");
            stm = yhteys.prepareStatement(lisaaPaikka);
            for (int i = 1; i <= 1000; i++) {
                stm.setString(1, "P" + i);
                stm.execute();

            }
            System.out.println("Paikat lisätty");
            long tehokkuustestiYksiLoppu = System.nanoTime();
            //lisätaan asiakkaat
            long tehokkuustestiKaksiAlku = System.nanoTime();
            stm = yhteys.prepareStatement(lisaaAsiakas);
            for (int i = 1; i < 1000; i++) {
                stm.setString(1, "A" + i);
                stm.execute();
            }
            System.out.println("Asiakkaat lisätty");
            long tehokkuustestiKaksiLoppu = System.nanoTime();
            //lisätään paketit
            long tehokkuustestiKolmeAlku = System.nanoTime();
            stm = yhteys.prepareStatement(lisaaPaketti);
            for (int i = 1; i <= 1000; i++) {
                stm.setInt(1, i);
                stm.setInt(2, 1);
                stm.execute();
            }
            System.out.println("Paketit lisätty");
            long tehokkuustestiKolmeLoppu = System.nanoTime();
            //lisätäään miljoona tapahtumaa
            long tehokkuustestiNeljaAlku = System.nanoTime();
            stm = yhteys.prepareStatement(lisaaTapahtuma);
            for (int i = 1; i <= 1000000; i++) {
                stm.setInt(1, 1);
                stm.setInt(2, 1);
                stm.setString(3, "Testi" + i);
                stm.setString(4, this.haeAika());
                stm.execute();
            }

            System.out.println("Tapahtumat lisätty");
            long tehokkuustestiNeljaLoppu = System.nanoTime();

            s.execute("COMMIT");
            long tehokkuustestiViisiAlku = System.nanoTime();
            //Suoritetaan tuhat kyselyä, joista jokaisessa haetaan jonkin asiakkaan pakettien määrä.

            stm = yhteys.prepareStatement(haePakettienMaara);
            for (int i = 1; i <= 1000; i++) {
                stm.setInt(1, i);
                ResultSet tulos = stm.executeQuery();
                while (tulos.next()) {
                    System.out.println("Pakettien määrä: " + tulos.getInt("COUNT(seurantakoodi)"));
                }
            }
            long tehokkuustestiViisiLoppu = System.nanoTime();

            long tehokkuustestiKuusiAlku = System.nanoTime();
            //Suoritetaan tuhat kyselyä, joista jokaisessa haetaan jonkin paketin tapahtumien määrä.
            stm = yhteys.prepareStatement(haeTapahtumienMaara);
            for (int i = 1; i <= 1000; i++) {
                stm.setInt(1, i);
                ResultSet tulos = stm.executeQuery();
                while (tulos.next()) {
                    System.out.println("Tapahtumien määrä: " + tulos.getInt("COUNT(seurantakoodi_id)"));
                }

            }
            long tehokkuustestiKuusiLoppu = System.nanoTime();

            long tehokkuustestiLoppu = System.nanoTime();

            System.out.println("Testiin yksi kului aikaa: " + (tehokkuustestiYksiLoppu - tehokkuustestiAlku) / 1e9 + " sekuntia");
            System.out.println("Testiin kaksi kului aikaa: " + (tehokkuustestiKaksiLoppu - tehokkuustestiKaksiAlku) / 1e9 + " sekuntia");
            System.out.println("Testiin kolme kului aikaa: " + (tehokkuustestiKolmeLoppu - tehokkuustestiKolmeAlku) / 1e9 + " sekuntia");
            System.out.println("Testiin neljä kului aikaa: " + (tehokkuustestiNeljaLoppu - tehokkuustestiNeljaAlku) / 1e9 + " sekuntia");
            System.out.println("Testiin viisi kului aikaa: " + (tehokkuustestiViisiLoppu - tehokkuustestiViisiAlku) / 1e9 + " sekuntia");
            System.out.println("Testiin kuusi kului aikaa: " + (tehokkuustestiKuusiLoppu - tehokkuustestiKuusiAlku) / 1e9 + "sekuntia");
            System.out.println("Aikaa kului yhteensä: " + (tehokkuustestiLoppu - tehokkuustestiAlku) / 1e9 + " sekuntia");
        } catch (SQLException e) {
            System.out.println("Virhe: " + e.getMessage());

        } finally {
            if (yhteys != null) {
                try {
                    yhteys.close();
                } catch (SQLException ex) {
                    System.out.println("Virhe yhteyden sulkemisessa: " + ex.getMessage());
                }
            }
        }

    }

    public String haeAsiakasNimi(String nimi) {
        String haettuNimi = "";
        String lisaa = "SELECT nimi FROM Asiakas WHERE nimi=?";
        Connection yhteys = null;
        try {
            yhteys = this.luoYhteysJaTietokanta();
            PreparedStatement kysely = yhteys.prepareStatement(lisaa);
            kysely.setString(1, nimi);
            ResultSet tulos = kysely.executeQuery();
            haettuNimi = tulos.getString("nimi");

        } catch (SQLException e) {

            System.out.println("Hae nimellä virhe: " + e.getMessage());
        } finally {
            try {
                if (yhteys != null) {
                    yhteys.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return haettuNimi;
    }

    public int haeAsiakasId(String nimi) {
        int asiakasId = -1;
        String lisaa = "SELECT id FROM Asiakas WHERE nimi=?";
        Connection yhteys = null;
        try {
            yhteys = this.luoYhteysJaTietokanta();
            PreparedStatement kysely = yhteys.prepareStatement(lisaa);
            kysely.setString(1, nimi);
            ResultSet tulos = kysely.executeQuery();
            asiakasId = tulos.getInt("id");

        } catch (SQLException e) {
            System.out.println("Asiakasta ei löytynyt tietokannasta");

        } finally {
            try {
                if (yhteys != null) {
                    yhteys.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }

        return asiakasId;
    }

    public int haePaikkaId(String paikka) {

        int palautaId = -1;
        String lisaa = "SELECT id FROM Paikat WHERE paikka=?";

        Connection yhteys = null;
        try {
            yhteys = this.luoYhteysJaTietokanta();
            PreparedStatement kysely = yhteys.prepareStatement(lisaa);

            kysely.setString(1, paikka);
            ResultSet tulos = kysely.executeQuery();

            palautaId = tulos.getInt("id");

        } catch (SQLException e) {

            System.out.println("Paikkaa ei löytynyt tietokannasta.");
        } finally {
            if (yhteys != null) {
                try {
                    yhteys.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        return palautaId;
    }

    public int haeSeurantakoodi(String koodi) {

        int koodiId = -1;
        int haettavaKoodi = 0;
        try {
            haettavaKoodi = Integer.valueOf(koodi);
        } catch (NumberFormatException e) {
            System.out.println("Virhe: " + e.getMessage());
        }

        String lisaa = "SELECT id FROM Paketti WHERE seurantakoodi=?";
        Connection yhteys = null;
        try {
            yhteys = this.luoYhteysJaTietokanta();
            PreparedStatement kysely = yhteys.prepareStatement(lisaa);
            kysely.setInt(1, haettavaKoodi);
            ResultSet tulos = kysely.executeQuery();

            koodiId = tulos.getInt("id");

        } catch (SQLException e) {
            System.out.println("Seurantakoodia ei löytynyt");
            //System.out.println("Virhe seurantaKoodi: " + e.getMessage());
        } finally {
            if (yhteys != null) {
                try {
                    yhteys.close();
                } catch (SQLException ex) {
                    System.out.println("Virhe yhteyden sulkemisessa: " + ex.getMessage());

                }
            }
        }

        return koodiId;
    }

    public int haePaivamaaraId(String paivamaara) {
        int paikkaId = -1;
        String lisaa = "SELECT id FROM Tapahtuma WHERE paivamaara LIKE ?";

        Connection yhteys = null;
        try {
            yhteys = this.luoYhteysJaTietokanta();
            PreparedStatement kysely = yhteys.prepareStatement(lisaa);

            kysely.setString(1, paivamaara + "%");
            ResultSet tulos = kysely.executeQuery();

            paikkaId = tulos.getInt("id");

        } catch (SQLException e) {
            System.out.println("Päivämäärää ei löytynyt.");
        } finally {
            if (yhteys != null) {
                try {
                    yhteys.close();
                } catch (SQLException ex) {
                    System.out.println("Virhe yhteyden sulkemisessa: " + ex.getMessage());
                }
            }
        }

        return paikkaId;
    }

    public String haeAika() {
        LocalDateTime aika = LocalDateTime.now();
        DateTimeFormatter muuta = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        String aikanyt = aika.format(muuta);
        return aikanyt;
    }

    public void poistaTaulutKannasta() {

        Connection yhteys = null;

        String poistaAsiakas = "DROP TABLE IF EXISTS Asiakas;";
        String poistaPaikat = "DROP TABLE IF EXISTS Paikat;";
        String poistaPaketti = "DROP TABLE IF EXISTS Paketti;";
        String poistaTapahtuma = "DROP TABLE IF EXISTS Tapahtuma;";
        try {
            yhteys = this.luoYhteysJaTietokanta();
            Statement stm = yhteys.createStatement();
            stm.execute(poistaAsiakas);
            stm.execute(poistaPaikat);
            stm.execute(poistaPaketti);
            stm.execute(poistaTapahtuma);

        } catch (SQLException e) {
            System.out.println("Virhe taulua poistaessa: " + e.getMessage());
        } finally {
            if (yhteys != null) {
                try {
                    yhteys.close();
                } catch (SQLException ex) {
                    System.out.println("Virhe yhteyden sulkemmisessa: " + ex.getMessage());
                }
            }
        }
    }

}
