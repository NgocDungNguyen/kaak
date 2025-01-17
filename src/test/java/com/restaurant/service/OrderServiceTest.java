package com.restaurant.service;

import com.restaurant.dao.OrderDAO;
import com.restaurant.model.Order;
import com.restaurant.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderDAO orderDAO;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderServiceImpl(orderDAO);
    }

    @Test
    void testGetAllOrders() throws SQLException {
        List<Order> expectedOrders = Arrays.asList(TestUtils.createSampleOrder(), TestUtils.createSampleOrder());
        when(orderDAO.findAll()).thenReturn(expectedOrders);

        List<Order> result = orderService.getAllOrders();

        assertEquals(2, result.size());
        verify(orderDAO).findAll();
    }

    @Test
    void testGetOrderById() throws SQLException {
        Order expectedOrder = TestUtils.createSampleOrder();
        when(orderDAO.findById("ORD001")).thenReturn(expectedOrder);

        Order result = orderService.getOrderById("ORD001");

        assertNotNull(result);
        assertEquals("ORD001", result.getId());
        verify(orderDAO).findById("ORD001");
    }

    @Test
    void testAddOrder() throws SQLException {
        Order orderToAdd = TestUtils.createSampleOrder();

        doNothing().when(orderDAO).create(any(Order.class));

        orderService.addOrder(orderToAdd);

        verify(orderDAO).create(orderToAdd);
    }

    @Test
    void testUpdateOrder() throws SQLException {
        Order orderToUpdate = TestUtils.createSampleOrder();
        orderToUpdate.setStatus("DELIVERED");

        doNothing().when(orderDAO).update(any(Order.class));

        orderService.updateOrder(orderToUpdate);

        verify(orderDAO).update(orderToUpdate);
    }

    @Test
    void testDeleteOrder() throws SQLException {
        orderService.deleteOrder("ORD001");

        verify(orderDAO).delete("ORD001");
    }

    @Test
    void testSearchOrders() throws SQLException {
        List<Order> allOrders = Arrays.asList(
                TestUtils.createSampleOrder(),
                TestUtils.createSampleOrder()
        );
        allOrders.get(1).setId("ORD002");
        when(orderDAO.findAll()).thenReturn(allOrders);

        List<Order> result = orderService.searchOrders("ORD001", "id", "id", true);

        assertEquals(1, result.size());
        assertEquals("ORD001", result.get(0).getId());
    }

    @Test
    void testFindOrdersByDateRange() throws SQLException {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        List<Order> expectedOrders = Arrays.asList(TestUtils.createSampleOrder(), TestUtils.createSampleOrder());
        when(orderDAO.findAll()).thenReturn(expectedOrders);

        List<Order> result = orderService.findOrdersByDateRange(startDate, endDate);

        assertEquals(2, result.size());
        verify(orderDAO).findAll();
    }
}