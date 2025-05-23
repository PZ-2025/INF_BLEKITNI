/*
 * Classname: TestProductRepository
 * Version information: 1.2
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */

import org.example.database.ProductRepository;
import org.example.sys.Product;

import java.math.BigDecimal;
import java.util.List;

/**
 * Klasa testująca działanie ProductRepository.
 */
public class ProductRepositoryTest {

    public static void main(String[] args) {
        ProductRepository productRepo = new ProductRepository();

        try {
            // === 1. Dodanie produktu ===
            Product product1 = new Product("Masło", "Nabiał", 6.99);
            productRepo.addProduct(product1);
            System.out.println(">>> Dodano produkt: Masło");

            Product product2 = new Product("Szampon", "Kosmetyki", 12.49);
            productRepo.addProduct(product2);
            System.out.println(">>> Dodano produkt: Szampon");

            // === 2. Pobranie wszystkich produktów ===
            System.out.println("\n>>> Wszystkie produkty:");
            writeProducts(productRepo.getAllProducts());

            // === 3. Pobranie produktu po ID ===
            Product found = productRepo.findProductById(product1.getId());
            System.out.println(">>> Znaleziony po ID: " + found);

            // === 4. Pobranie po kategorii ===
            System.out.println("\n>>> Produkty w kategorii 'Nabiał':");
            writeProducts(productRepo.getProductsByCategory("Nabiał"));

            // === 5. Aktualizacja obiektu produkt ===
            found.setCategory("Produkty spożywcze");
            productRepo.updateProduct(found);
            System.out.println(">>> Zmieniono kategorię produktu.");

            // === 6. Aktualizacja ceny ===
            productRepo.updateProductPrice(product2.getId(), BigDecimal.valueOf(10.99));
            System.out.println(">>> Zmieniono cenę produktu Szampon.");

            // === 7. Produkty w zakresie cenowym ===
            System.out.println("\n>>> Produkty w zakresie cenowym 5.00 - 11.00:");
            List<Product> list = productRepo.getPriceRangeProducts(
                    BigDecimal.valueOf(5.00),
                    BigDecimal.valueOf(11.00)
            );
            writeProducts(list);

            // === 8. Usunięcie produktów po kategorii ===
            int removed = productRepo.removeProductsFromCategory("Produkty spożywcze");
            System.out.println(">>> Usunięto produktów z kategorii 'Produkty spożywcze': " + removed);

            // === 9. Usunięcie konkretnego produktu ===
            productRepo.removeProduct(product2.getId());
            System.out.println(">>> Usunięto produkt: Szampon");

            // === 10. Lista końcowa ===
            System.out.println("\n>>> Lista produktów po usunięciach:");
            writeProducts(productRepo.getAllProducts());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            productRepo.close();
        }
    }

    /**
     * Pomocnicza metoda wypisująca products.
     *
     * @param products lista produktów
     */
    private static void writeProducts(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("(Brak produktów)");
        } else {
            for (Product p : products) {
                System.out.printf("ID: %-3d | Nazwa: %-20s | Kategoria: %-15s | Cena: %.2f zł%n",
                        p.getId(),
                        p.getName(),
                        p.getCategory(),
                        p.getPrice()
                );
            }
        }
        System.out.println("-----------------------------");
    }
}
