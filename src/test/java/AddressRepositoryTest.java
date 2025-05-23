/*
 * Classname: TestAddressRepository
 * Version information: 1.1
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */

import org.example.database.AddressRepository;
import org.example.sys.Address;

import java.util.List;

/**
 * Klasa testująca działanie AddressRepository.
 */
public class AddressRepositoryTest {

    public static void main(String[] args) {
        AddressRepository addressRepo = new AddressRepository();

        try {
            // === 1. Dodanie nowego adresu ===
            Address address1 = new Address();
            address1.setTown("Testowo");
            address1.setCity("Miastko");
            address1.setZipCode("99-999");
            address1.setHouseNumber("10B");
            address1.setApartmentNumber("3");

            addressRepo.addAddress(address1);
            System.out.println(">>> Dodano adres!");

            // === 2. Wyświetlenie wszystkich adresów ===
            System.out.println("\n>>> Lista adresów:");
            writeAddresses(addressRepo.getAllAddresses());

            // === 3. Odczyt po ID ===
            Address znaleziony = addressRepo.findAddressById(address1.getId());
            System.out.println("\n>>> Adres po ID: " + znaleziony);

            // === 4. Usunięcie adresu ===
            addressRepo.removeAddress(address1.getId());
            System.out.println(">>> Usunięto adres.");

            // === 5. Wyświetlenie po usunięciu ===
            System.out.println("\n>>> Lista adresów po usunięciu:");
            writeAddresses(addressRepo.getAllAddresses());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            addressRepo.close();
        }
    }

    /**
     * Pomocnicza metoda wypisująca listę adresów.
     *
     * @param addresses lista adresów
     */
    private static void writeAddresses(List<Address> addresses) {
        if (addresses.isEmpty()) {
            System.out.println("(Brak adresów)");
        } else {
            for (Address a : addresses) {
                System.out.printf("ID: %-3d Miasto: %-15s Miejscowość: %-15s Kod: %-10s%n",
                        a.getId(),
                        a.getCity(),
                        a.getTown(),
                        a.getZipCode()
                );
            }
        }
        System.out.println("-----------------------------");
    }
}
