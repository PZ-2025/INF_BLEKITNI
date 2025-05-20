package org.example.database;

import org.example.sys.Address;

import java.util.List;

public class TestAddressRepository {

    public static void main(String[] args) {
        AddressRepository addressRepo = new AddressRepository();

        try {
            // === 1. Dodanie nowego adresu ===
            Address address1 = new Address();
            address1.setMiejscowosc("Testowo");
            address1.setMiasto("Miastko");
            address1.setKodPocztowy("99-999");
            address1.setNumerDomu("10B");
            address1.setNumerMieszkania("3");

            addressRepo.dodajAdres(address1);
            System.out.println(">>> Dodano adres!");

            // === 2. Wyświetlenie wszystkich adresów ===
            System.out.println("\n>>> Lista adresów:");
            wypiszAdresy(addressRepo.pobierzWszystkieAdresy());

            // === 3. Odczyt po ID ===
            Address znaleziony = addressRepo.znajdzAdresPoId(address1.getId());
            System.out.println("\n>>> Adres po ID: " + znaleziony);

            // === 4. Wyszukiwanie po miejscowości ===
            List<Address> poMiejscowosci = addressRepo.znajdzPoMiejscowosci("Testowo");
            System.out.println("\n>>> Wyszukiwanie po miejscowości 'Testowo':");
            wypiszAdresy(poMiejscowosci);

            // === 5. Wyszukiwanie po numerze domu ===
            List<Address> poNumerzeDomu = addressRepo.znajdzPoNumerzeDomu("10B");
            System.out.println("\n>>> Wyszukiwanie po numerze domu '10B':");
            wypiszAdresy(poNumerzeDomu);

            // === 6. Wyszukiwanie po numerze mieszkania ===
            List<Address> poNumerzeMieszkania = addressRepo.znajdzPoNumerzeMieszkania("3");
            System.out.println("\n>>> Wyszukiwanie po numerze mieszkania '3':");
            wypiszAdresy(poNumerzeMieszkania);

            // === 7. Wyszukiwanie po kodzie pocztowym ===
            List<Address> poKodPocztowy = addressRepo.znajdzPoKodPocztowym("99-999");
            System.out.println("\n>>> Wyszukiwanie po kodzie pocztowym '99-999':");
            wypiszAdresy(poKodPocztowy);

            // === 8. Wyszukiwanie po mieście ===
            List<Address> poMiescie = addressRepo.znajdzPoMiescie("Miastko");
            System.out.println("\n>>> Wyszukiwanie po mieście 'Miastko':");
            wypiszAdresy(poMiescie);

            // === 9. Usunięcie adresu ===
            addressRepo.usunAdres(address1.getId());
            System.out.println("\n>>> Usunięto adres.");

            // === 10. Lista po usunięciu ===
            System.out.println("\n>>> Lista adresów po usunięciu:");
            wypiszAdresy(addressRepo.pobierzWszystkieAdresy());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            addressRepo.close();
        }
    }

    private static void wypiszAdresy(List<Address> adresy) {
        if (adresy.isEmpty()) {
            System.out.println("(Brak adresów)");
        } else {
            for (Address a : adresy) {
                System.out.printf("ID: %-3d Miasto: %-15s Miejscowość: %-15s Kod: %-10s Dom: %-6s Mieszkanie: %-6s%n",
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
