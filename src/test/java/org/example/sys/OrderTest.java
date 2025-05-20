package org.example.sys;

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
        Product produkt = new Product("Laptop", "Elektronika", 2500.00);
        Employee pracownik = new Employee();
        pracownik.setLogin("test_user");
        Date data = new Date();

        Order order = new Order(produkt, pracownik, 10, new BigDecimal("25000.00"), data);

        assertEquals(produkt, order.getProdukt());
        assertEquals(pracownik, order.getPracownik());
        assertEquals(10, order.getIlosc());
        assertEquals(new BigDecimal("25000.00"), order.getCena());
        assertEquals(data, order.getData());
    }

    @Test
    void testSettersAndToString() {
        Order order = new Order();

        Product produkt = new Product("Tablet", "Elektronika", 1000.00);
        Employee pracownik = new Employee();
        pracownik.setLogin("admin");
        Date data = new Date();

        order.setProdukt(produkt);
        order.setPracownik(pracownik);
        order.setIlosc(5);
        order.setCena(new BigDecimal("5000.00"));
        order.setData(data);

        assertEquals(produkt, order.getProdukt());
        assertEquals(pracownik, order.getPracownik());
        assertEquals(5, order.getIlosc());
        assertEquals(new BigDecimal("5000.00"), order.getCena());
        assertEquals(data, order.getData());

        assertTrue(order.toString().contains("Tablet"));
        assertTrue(order.toString().contains("5000.00"));
        assertTrue(order.toString().contains("admin"));
    }
}
