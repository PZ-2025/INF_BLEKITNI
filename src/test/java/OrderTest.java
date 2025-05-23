import org.example.sys.Order;
import org.example.sys.Employee;
import org.example.sys.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void testConstructorAndGetters() {
        Product product = new Product("Laptop", "Elektronika", 2500.00);
        Employee employee = new Employee();
        employee.setLogin("test_user");
        Date date = new Date();

        Order order = new Order(product, employee, 10, new BigDecimal("25000.00"), date);

        assertEquals(product, order.getProduct());
        assertEquals(employee, order.getEmployee());
        assertEquals(10, order.getQuantity());
        assertEquals(new BigDecimal("25000.00"), order.getPrice());
        assertEquals(date, order.getDate());
    }

    @Test
    void testSettersAndToString() {
        Order order = new Order();

        Product product = new Product("Tablet", "Elektronika", 1000.00);
        Employee employee = new Employee();
        employee.setLogin("admin");
        Date date = new Date();

        order.setProduct(product);
        order.setEmployee(employee);
        order.setQuantity(5);
        order.setPrice(new BigDecimal("5000.00"));
        order.setDate(date);

        assertEquals(product, order.getProduct());
        assertEquals(employee, order.getEmployee());
        assertEquals(5, order.getQuantity());
        assertEquals(new BigDecimal("5000.00"), order.getPrice());
        assertEquals(date, order.getDate());

        assertTrue(order.toString().contains("Tablet"));
        assertTrue(order.toString().contains("5000.00"));
        assertTrue(order.toString().contains("admin"));
    }
}
