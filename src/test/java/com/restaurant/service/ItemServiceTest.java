package com.restaurant.service;

import com.restaurant.dao.ItemDAO;
import com.restaurant.model.Item;
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

class ItemServiceTest {

    @Mock
    private ItemDAO itemDAO;

    private ItemService itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        itemService = new ItemServiceImpl(itemDAO);
    }

    @Test
    void testGetAllItems() throws SQLException {
        List<Item> expectedItems = Arrays.asList(TestUtils.createSampleItem(), TestUtils.createSampleItem());
        when(itemDAO.findAll()).thenReturn(expectedItems);

        List<Item> result = itemService.getAllItems();

        assertEquals(2, result.size());
        verify(itemDAO).findAll();
    }

    @Test
    void testGetItemById() throws SQLException {
        Item expectedItem = TestUtils.createSampleItem();
        when(itemDAO.findById("ITM001")).thenReturn(expectedItem);

        Item result = itemService.getItemById("ITM001");

        assertNotNull(result);
        assertEquals("ITM001", result.getId());
        verify(itemDAO).findById("ITM001");
    }

    @Test
    void testAddItem() throws SQLException {
        Item itemToAdd = TestUtils.createSampleItem();

        doNothing().when(itemDAO).create(any(Item.class));

        itemService.addItem(itemToAdd);

        verify(itemDAO).create(itemToAdd);
    }

    @Test
    void testUpdateItem() throws SQLException {
        Item itemToUpdate = TestUtils.createSampleItem();
        itemToUpdate.setPrice(15.99);

        doNothing().when(itemDAO).update(any(Item.class));

        itemService.updateItem("ITM001", itemToUpdate);

        verify(itemDAO).update(itemToUpdate);
    }

    @Test
    void testDeleteItem() throws SQLException {
        itemService.deleteItem("ITM001");

        verify(itemDAO).delete("ITM001");
    }

    @Test
    void testSearchItems() throws SQLException {
        List<Item> allItems = Arrays.asList(
                TestUtils.createSampleItem(),
                TestUtils.createSampleItem()
        );
        allItems.get(1).setName("Burger");
        when(itemDAO.findAll()).thenReturn(allItems);

        List<Item> result = itemService.searchItems("Pizza", "name", "name", true);

        assertEquals(1, result.size());
        assertEquals("Pizza", result.get(0).getName());
    }

    @Test
    void testFindByCategory() throws SQLException {
        List<Item> expectedItems = Arrays.asList(TestUtils.createSampleItem(), TestUtils.createSampleItem());
        when(itemDAO.findAll()).thenReturn(expectedItems);

        List<Item> result = itemService.findByCategory("Main Course");

        assertEquals(2, result.size());
        assertEquals("Main Course", result.get(0).getCategory());
    }

    @Test
    void testFindByPriceRange() throws SQLException {
        List<Item> allItems = Arrays.asList(
                TestUtils.createSampleItem(),
                TestUtils.createSampleItem()
        );
        allItems.get(1).setPrice(20.99);
        when(itemDAO.findAll()).thenReturn(allItems);

        List<Item> result = itemService.findByPriceRange(10.0, 15.0);

        assertEquals(1, result.size());
        assertEquals(12.99, result.get(0).getPrice());
    }
}