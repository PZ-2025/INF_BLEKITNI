import org.example.database.OrderRepository;
import org.example.database.ProductRepository;
import org.example.database.UserRepository;
import org.example.sys.Order;
import org.example.sys.Employee;
import org.example.sys.Product;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Klasa testująca działanie OrderRepository.
 */
public class OrderRepositoryTest {

    public static void main(String[] args) {
        OrderRepository orderRepo = new OrderRepository();
        ProductRepository productRepo = new ProductRepository(); // zamieniono z WarehouseRepository
        UserRepository userRepo = new UserRepository();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            // === 1. Dodanie produktu i pracownika testowego ===
            Product product = new Product("Testowy produkt", "Testowa kategoria", 5.99);
            productRepo.addProduct(product);

            Employee employee = userRepo.getAllEmployess().get(0); // zakładamy, że już jest

            // === 2. Dodanie nowego zamówienia ===
            Order order = new Order();
            order.setProduct(product);
            order.setEmployee(employee);
            order.setQuantity(10);
            order.setPrice(new BigDecimal("59.90"));
            order.setDate(sdf.parse("2025-05-12"));

            orderRepo.addOrder(order);
            System.out.println(">>> Dodano zamówienie.");

            // === 3. Pobranie wszystkich zamówień ===
            System.out.println("\n>>> Lista wszystkich zamówień:");
            writeOrders(orderRepo.getAllOrders());

            // === 4. Aktualizacja ===
            order.setQuantity(20);
            order.setPrice(new BigDecimal("119.80"));
            orderRepo.updateOrder(order);
            System.out.println(">>> Zaktualizowano zamówienie.");

            // === 5. Pobranie po ID ===
            Order found = orderRepo.findOrderById(order.getId());
            System.out.println(">>> Zamówienie po ID: " + found);

            // === 6. Usunięcie ===
            orderRepo.removeOrders(order.getId());
            System.out.println(">>> Usunięto zamówienie.");

            // === 7. Sprawdzenie listy po usunięciu ===
            System.out.println("\n>>> Lista zamówień po usunięciu:");
            writeOrders(orderRepo.getAllOrders());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            orderRepo.close();
            productRepo.close();
            userRepo.close();
        }
    }

    private static void writeOrders(List<Order> orders) {
        if (orders.isEmpty()) {
            System.out.println("(Brak zamówień)");
        } else {
            for (Order z : orders) {
                System.out.printf("ID: %-3d | Produkt: %-20s | Ilość: %-3d | Cena: %-7.2f | Data: %s%n",
                        z.getId(),
                        z.getProduct().getName(), // poprawiono z getName()
                        z.getQuantity(),
                        z.getPrice(),
                        z.getDate().toString()
                );
            }
        }
        System.out.println("-----------------------------");
    }
}
