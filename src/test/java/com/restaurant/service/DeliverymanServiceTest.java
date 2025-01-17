package com.restaurant.service;

import com.restaurant.dao.DeliverymanDAO;
import com.restaurant.model.Deliveryman;
import com.restaurant.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeliverymanServiceTest {

    @Mock
    private DeliverymanDAO deliverymanDAO;

    private DeliverymanService deliverymanService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        deliverymanService = new DeliverymanServiceImpl(deliverymanDAO);
    }

    @Test
    void testGetAllDeliverymen() throws SQLException {
        List<Deliveryman> expectedDeliverymen = Arrays.asList(TestUtils.createSampleDeliveryman(), TestUtils.createSampleDeliveryman());
        when(deliverymanDAO.findAll()).thenReturn(expectedDeliverymen);

        List<Deliveryman> result = deliverymanService.getAllDeliverymen();

        assertEquals(2, result.size());
        verify(deliverymanDAO).findAll();
    }

    @Test
    void testGetDeliverymanById() throws SQLException {
        Deliveryman expectedDeliveryman = TestUtils.createSampleDeliveryman();
        when(deliverymanDAO.findById("DLV001")).thenReturn(expectedDeliveryman);

        Deliveryman result = deliverymanService.getDeliverymanById("DLV001");

        assertNotNull(result);
        assertEquals("DLV001", result.getId());
        verify(deliverymanDAO).findById("DLV001");
    }

    @Test
    void testAddDeliveryman() throws SQLException {
        Deliveryman deliverymanToAdd = TestUtils.createSampleDeliveryman();

        doNothing().when(deliverymanDAO).create(any(Deliveryman.class));

        deliverymanService.addDeliveryman(deliverymanToAdd);

        verify(deliverymanDAO).create(deliverymanToAdd);
    }

    @Test
    void testUpdateDeliveryman() throws SQLException {
        Deliveryman deliverymanToUpdate = TestUtils.createSampleDeliveryman();
        deliverymanToUpdate.setName("Updated Name");

        doNothing().when(deliverymanDAO).update(any(Deliveryman.class));

        deliverymanService.updateDeliveryman("DLV001", deliverymanToUpdate);

        verify(deliverymanDAO).update(deliverymanToUpdate);
    }

    @Test
    void testDeleteDeliveryman() throws SQLException {
        deliverymanService.deleteDeliveryman("DLV001");

        verify(deliverymanDAO).delete("DLV001");
    }

    @Test
    void testSearchDeliverymen() throws SQLException {
        List<Deliveryman> allDeliverymen = Arrays.asList(
                TestUtils.createSampleDeliveryman(),
                TestUtils.createSampleDeliveryman()
        );
        allDeliverymen.get(1).setName("John Smith");
        when(deliverymanDAO.findAll()).thenReturn(allDeliverymen);

        List<Deliveryman> result = deliverymanService.searchDeliverymen("Jane", "name", "name", true);

        assertEquals(1, result.size());
        assertEquals("Jane Smith", result.get(0).getName());
    }
}