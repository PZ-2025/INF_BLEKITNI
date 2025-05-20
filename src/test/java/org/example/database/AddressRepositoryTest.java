package org.example.database;

import org.example.sys.Address;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AddressRepositoryTest {

    private AddressRepository addressRepo;

    @BeforeEach
    void setUp() {
        addressRepo = new AddressRepository();
    }

    @AfterEach
    void tearDown() {
        addressRepo.close();
    }

    @Test
    void testDodajAdres() {
        // === 1. Dodanie nowego adresu ===
        Address address1 = new Address();
        address1.setMiejscowosc("Testowo");
        address1.setMiasto("Miastko");
        address1.setKodPocztowy("99-999");
        address1.setNumerDomu("10B");
        address1.setNumerMieszkania("3");

        addressRepo.dodajAdres(address1);
        assertNotNull(address1.getId(), "Adres nie został dodany do bazy danych.");
        System.out.println(">>> Dodano adres!");
    }

    @Test
    void testPobierzWszystkieAdresy() {
        // === 2. Wyświetlenie wszystkich adresów ===
        List<Address> adresy = addressRepo.pobierzWszystkieAdresy();
        assertNotNull(adresy, "Lista adresów powinna być niepusta.");
        assertTrue(!adresy.isEmpty(), "Powinno być co najmniej jeden adres w bazie.");
        System.out.println("\n>>> Lista adresów:");
        wypiszAdresy(adresy);
    }

    @Test
    void testZnajdzAdresPoId() {
        // === 3. Odczyt po ID ===
        Address address1 = new Address();
        address1.setMiejscowosc("Testowo");
        address1.setMiasto("Miastko");
        address1.setKodPocztowy("99-999");
        address1.setNumerDomu("10B");
        address1.setNumerMieszkania("3");

        addressRepo.dodajAdres(address1);
        Address znaleziony = addressRepo.znajdzAdresPoId(address1.getId());
        assertNotNull(znaleziony, "Nie znaleziono adresu po ID.");
        assertEquals(address1.getMiejscowosc(), znaleziony.getMiejscowosc(), "Miejscowość nie zgadza się.");
        assertEquals(address1.getMiasto(), znaleziony.getMiasto(), "Miasto nie zgadza się.");
        assertEquals(address1.getKodPocztowy(), znaleziony.getKodPocztowy(), "Kod pocztowy nie zgadza się.");
        assertEquals(address1.getNumerDomu(), znaleziony.getNumerDomu(), "Numer domu nie zgadza się.");
        assertEquals(address1.getNumerMieszkania(), znaleziony.getNumerMieszkania(), "Numer mieszkania nie zgadza się.");
        System.out.println("\n>>> Adres po ID: " + znaleziony);
    }

    @Test
    void testZnajdzPoMiejscowosci() {
        // === 4. Wyszukiwanie po miejscowości ===
        Address address1 = new Address();
        address1.setMiejscowosc("Testowo");
        address1.setMiasto("Miastko");
        address1.setKodPocztowy("99-999");
        address1.setNumerDomu("10B");
        address1.setNumerMieszkania("3");

        addressRepo.dodajAdres(address1);

        List<Address> poMiejscowosci = addressRepo.znajdzPoMiejscowosci("Testowo");
        assertNotNull(poMiejscowosci, "Lista adresów po miejscowości powinna być niepusta.");
        assertTrue(!poMiejscowosci.isEmpty(), "Powinno być co najmniej jeden adres dla miejscowości 'Testowo'.");
        assertEquals(1, poMiejscowosci.size(), "Powinien zostać znaleziony dokładnie jeden adres.");
        assertEquals(address1.getMiejscowosc(), poMiejscowosci.get(0).getMiejscowosc(), "Miejscowość nie zgadza się.");
        System.out.println("\n>>> Wyszukiwanie po miejscowości 'Testowo':");
        wypiszAdresy(poMiejscowosci);
    }

    @Test
    void testZnajdzPoNumerzeDomu() {
        // === 5. Wyszukiwanie po numerze domu ===
        Address address1 = new Address();
        address1.setMiejscowosc("Testowo");
        address1.setMiasto("Miastko");
        address1.setKodPocztowy("99-999");
        address1.setNumerDomu("10B");
        address1.setNumerMieszkania("3");

        addressRepo.dodajAdres(address1);

        List<Address> poNumerzeDomu = addressRepo.znajdzPoNumerzeDomu("10B");
        assertNotNull(poNumerzeDomu, "Lista adresów po numerze domu powinna być niepusta.");
        assertTrue(!poNumerzeDomu.isEmpty(), "Powinno być co najmniej jeden adres dla numeru domu '10B'.");
        assertEquals(1, poNumerzeDomu.size(), "Powinien zostać znaleziony dokładnie jeden adres.");
        assertEquals(address1.getNumerDomu(), poNumerzeDomu.get(0).getNumerDomu(), "Numer domu nie zgadza się.");
        System.out.println("\n>>> Wyszukiwanie po numerze domu '10B':");
        wypiszAdresy(poNumerzeDomu);
    }

    @Test
    void testZnajdzPoNumerzeMieszkania() {
        // === 6. Wyszukiwanie po numerze mieszkania ===
        Address address1 = new Address();
        address1.setMiejscowosc("Testowo");
        address1.setMiasto("Miastko");
        address1.setKodPocztowy("99-999");
        address1.setNumerDomu("10B");
        address1.setNumerMieszkania("3");

        addressRepo.dodajAdres(address1);

        List<Address> poNumerzeMieszkania = addressRepo.znajdzPoNumerzeMieszkania("3");
        assertNotNull(poNumerzeMieszkania, "Lista adresów po numerze mieszkania powinna być niepusta.");
        assertTrue(!poNumerzeMieszkania.isEmpty(), "Powinno być co najmniej jeden adres dla numeru mieszkania '3'.");
        assertEquals(1, poNumerzeMieszkania.size(), "Powinien zostać znaleziony dokładnie jeden adres.");
        assertEquals(address1.getNumerMieszkania(), poNumerzeMieszkania.get(0).getNumerMieszkania(), "Numer mieszkania nie zgadza się.");
        System.out.println("\n>>> Wyszukiwanie po numerze mieszkania '3':");
        wypiszAdresy(poNumerzeMieszkania);
    }

    @Test
    void testZnajdzPoKodPocztowym() {
        // === 7. Wyszukiwanie po kodzie pocztowym ===
        Address address1 = new Address();
        address1.setMiejscowosc("Testowo");
        address1.setMiasto("Miastko");
        address1.setKodPocztowy("99-999");
        address1.setNumerDomu("10B");
        address1.setNumerMieszkania("3");

        addressRepo.dodajAdres(address1);

        List<Address> poKodPocztowy = addressRepo.znajdzPoKodPocztowym("99-999");
        assertNotNull(poKodPocztowy, "Lista adresów po kodzie pocztowym powinna być niepusta.");
        assertTrue(!poKodPocztowy.isEmpty(), "Powinno być co najmniej jeden adres dla kodu pocztowego '99-999'.");
        assertEquals(1, poKodPocztowy.size(), "Powinien zostać znaleziony dokładnie jeden adres.");
        assertEquals(address1.getKodPocztowy(), poKodPocztowy.get(0).getKodPocztowy(), "Kod pocztowy nie zgadza się.");
        System.out.println("\n>>> Wyszukiwanie po kodzie pocztowym '99-999':");
        wypiszAdresy(poKodPocztowy);
    }

    @Test
    void testZnajdzPoMiescie() {
        // === 8. Wyszukiwanie po mieście ===
        Address address1 = new Address();
        address1.setMiejscowosc("Testowo");
        address1.setMiasto("Miastko");
        address1.setKodPocztowy("99-999");
        address1.setNumerDomu("10B");
        address1.setNumerMieszkania("3");

        addressRepo.dodajAdres(address1);

        List<Address> poMiescie = addressRepo.znajdzPoMiescie("Miastko");
        assertNotNull(poMiescie, "Lista adresów po mieście powinna być niepusta.");
        assertTrue(!poMiescie.isEmpty(), "Powinno być co najmniej jeden adres dla miasta 'Miastko'.");
        assertEquals(1, poMiescie.size(), "Powinien zostać znaleziony dokładnie jeden adres.");
        assertEquals(address1.getMiasto(), poMiescie.get(0).getMiasto(), "Miasto nie zgadza się.");
        System.out.println("\n>>> Wyszukiwanie po mieście 'Miastko':");
        wypiszAdresy(poMiescie);
    }

    @Test
    void testUsunAdres() {
        // === 9. Usunięcie adresu ===
        Address address1 = new Address();
        address1.setMiejscowosc("Testowo");
        address1.setMiasto("Miastko");
        address1.setKodPocztowy("99-999");
        address1.setNumerDomu("10B");
        address1.setNumerMieszkania("3");

        addressRepo.dodajAdres(address1);
        addressRepo.usunAdres(address1.getId());

        // Sprawdzenie, czy adres został usunięty
        Address usunieto = addressRepo.znajdzAdresPoId(address1.getId());
        assertNull(usunieto, "Adres nie został usunięty z bazy danych.");
        System.out.println("\n>>> Usunięto adres.");

        // === 10. Lista po usunięciu ===
        List<Address> adresyPoUsunieciu = addressRepo.pobierzWszystkieAdresy();
        assertNotNull(adresyPoUsunieciu, "Lista adresów po usunięciu powinna być niepusta.");
        System.out.println("\n>>> Lista adresów po usunięciu:");
        wypiszAdresy(adresyPoUsunieciu);
    }

    private static void wypiszAdresy(List<Address> adresy) {
        if (adresy.isEmpty()) {
            System.out.println("(Brak adresów)");
        } else {
            for (Address a : adresy) {
                System.out.printf(
                        "ID: %-3d Miasto: %-15s Miejscowość: %-15s Kod: %-10s Dom: %-6s Mieszkanie: %-6s%n",
                        a.getId(),
                        a.getMiasto(),
                        a.getMiejscowosc(),
                        a.getKodPocztowy(),
                        a.getNumerDomu(),
                        a.getNumerMieszkania()
                );
            }
        }
        System.out.println("-----------------------------");
    }
}