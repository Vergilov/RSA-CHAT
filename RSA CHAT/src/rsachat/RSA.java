package rsachat;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RSA {

    private BigInteger n, d, e;

    private int bitlen = 1024; //Dlugosc Bita

    public RSA() {
        n = new BigInteger("20068127683770671479355377625673839509839611259427453058798508634184584783345213870620162646419408615592319119994384481549264620354315758658417901205955292686524771079998377045826288741050711969451033510149922115073629948242352551671397832685964449337335415260952575134453776698024417553229931323869314099187536120352836273051230109925064360121145424282282183438177123171374333129911743254013721207197438459822379361795457442464252872647753385896982624402231401130929178068462589342972740513237628881974088446379890358325309144084414543839663822014516441588094225128622979846599035850220778357802637078541754394496317");
        e = new BigInteger("7");
        d = new BigInteger("11467501533583240845345930071813622577051206433958544605027719219534048447625836497497235797953947780338468068568219703742436925916751862090524514974831595820871297759999072597615022137743263982543447720085669780042074256138487172383655904391979685335620237291972900076830729541728238601845675042211036628106999989740692112617844223778534013081949596561587966735562821323331364998205566027639180627898731751525837318128325796140159938803128456210221558310982709610226579612342503510579244372180693368977928384927036214594142211747351644010833644882918043338382470428305644734399551449919473376826708423668628710318663");
    }

    /**
     * Tworzy instacje ktore moze zaszyfrowac i odszyfrowac
     */
    public RSA(int bits) {
        bitlen = bits; //zmieniamy na bity
        SecureRandom r = new SecureRandom();
        //Liczby pierwsze P i Q
        BigInteger p = new BigInteger(bitlen / 2, 100, r);
        BigInteger q = new BigInteger(bitlen / 2, 100, r);
        n = p.multiply(q);
        BigInteger m = (p.subtract(BigInteger.ONE)).multiply(q
                .subtract(BigInteger.ONE));
        e = new BigInteger("3");
        while (m.gcd(e).intValue() > 1) {
            e = e.add(new BigInteger("2"));
        }
        d = e.modInverse(m); //d ≡ e−1 (mod φ(n))
    }

    /**
     * Koduje daną mu wiadomość.
     */
    public synchronized BigInteger kodowanie(BigInteger message) {
        return message.modPow(e, n);
    }

    /**
     * Odkoduje mu daną wiadomość
     */
    public synchronized BigInteger dekodowanie(BigInteger message) {
        return message.modPow(d, n);
    }

    /**
     * Zwraca modulus.
     */
    public synchronized BigInteger getN() {
        return n;
    }

    /**
     * Zwraca Publiczny Klucz.
     */
    public synchronized BigInteger getE() {
        return e;
    }

    public synchronized BigInteger getD() {
        return d;
    }

    /**
     * Banalny test programu
     */
    public static void main(String[] args) {
        RSA rsa = new RSA(2048);

        String text1 = "R S A";
        System.out.println("Zwykły tekst: " + text1);
        BigInteger normal = new BigInteger(text1.getBytes());

        BigInteger zakod = rsa.kodowanie(normal);
        System.out.println("Zaszyfrowany tekst: " + zakod);
        normal = rsa.dekodowanie(zakod);

        String text2 = new String(normal.toByteArray());
        System.out.println("Zwykły tekst: " + text2);

        System.out.println("N " + rsa.getN());

        System.out.println("E " + rsa.getE());

        System.out.println("D " + rsa.getD());
    }
}
