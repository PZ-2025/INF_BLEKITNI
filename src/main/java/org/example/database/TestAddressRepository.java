/*
 * Classname: TestAddressRepository
 * Version information: 1.0
 * Date: 2025-05-12
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import org.example.sys.Address;

import java.util.List;

/**
 * Klasa testująca działanie AddressRepository.
 */
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

            // === 4. Usunięcie adresu ===
            addressRepo.usunAdres(address1.getId());
            System.out.println(">>> Usunięto adres.");

            // === 5. Wyświetlenie po usunięciu ===
            System.out.println("\n>>> Lista adresów po usunięciu:");
            wypiszAdresy(addressRepo.pobierzWszystkieAdresy());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            addressRepo.close();
        }
    }

    /**
     * Pomocnicza metoda wypisująca listę adresów.
     *
     * @param adresy lista adresów
     */
    private static void wypiszAdresy(List<Address> adresy) {
        if (adresy.isEmpty()) {
            System.out.println("(Brak adresów)");
        } else {
            for (Address a : adresy) {
                System.out.printf("ID: %-3d Miasto: %-15s Miejscowość: %-15s Kod: %-10s%n",
                        a.getId(),
                        a.getMiasto(),
                        a.getMiejscowosc(),
                        a.getKodPocztowy()
                );
            }
        }
        System.out.println("-----------------------------");
    }
}
