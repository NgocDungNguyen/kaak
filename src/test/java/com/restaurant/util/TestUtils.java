package com.restaurant.util;

import com.restaurant.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class TestUtils {

    public static Customer createSampleCustomer() {
        Customer customer = new Customer();
        customer.setId("CST001");
        customer.setName("John Doe");
        customer.setPhone("1234567890");
        customer.setEmail("john@example.com");
        customer.setAddress("123 Main St");
        customer.setJoinDate(LocalDate.now());
        customer.setTotalOrders(0);
        return customer;
    }

    public static Deliveryman createSampleDeliveryman() {
        Deliveryman deliveryman = new Deliveryman();
        deliveryman.setId("DLV001");
        deliveryman.setName("Jane Smith");
        deliveryman.setPhone("9876543210");
        deliveryman.setEmail("jane@example.com");
        deliveryman.setStatus("ACTIVE");
        deliveryman.setVehicleType("Car");
        deliveryman.setLicenseNumber("LIC123456");
        deliveryman.setJoinDate(LocalDate.now());
        deliveryman.setTotalDeliveries(0);
        deliveryman.setRating(5.0);
        deliveryman.setAvailable(true);
        return deliveryman;
    }

    public static Item createSampleItem() {
        Item item = new Item();
        item.setId("ITM001");
        item.setName("Pizza");
        item.setCategory("Main Course");
        item.setPrice(12.99);
        item.setDescription("Delicious pizza");
        item.setPreparationTime(20);
        item.setAvailable(true);
        item.setImageUrl("pizza.jpg");
        item.setCalories(800);
        item.setSpicyLevel(2);
        return item;
    }

    public static Order createSampleOrder() {
        Order order = new Order();
        order.setId("ORD001");
        order.setCustomer(createSampleCustomer());
        order.setDeliveryman(createSampleDeliveryman());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setTotalAmount(12.99);
        order.setDeliveryAddress("123 Main St");
        order.setSpecialInstructions("Ring doorbell");
        order.setPaymentMethod("CREDIT_CARD");

        Map<Item, Integer> items = new HashMap<>();
        items.put(createSampleItem(), 1);
        order.setItems(items);

        return order;
    }
}