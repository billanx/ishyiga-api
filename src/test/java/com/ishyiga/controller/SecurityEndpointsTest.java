package com.ishyiga.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ishyiga.entities.User;
import com.ishyiga.enums.Role;
import com.ishyiga.repo.UserRepository;
import com.ishyiga.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityEndpointsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String ishyigaToken;
    private String bankToken;

    @BeforeEach
    void setUp() {
        // Clear existing users
        userRepository.deleteAll();

        // Create test users
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        User ishyiga = new User();
        ishyiga.setUsername("ishyiga");
        ishyiga.setPassword(passwordEncoder.encode("ishyiga123"));
        ishyiga.setRole(Role.ISHYIGA);
        userRepository.save(ishyiga);

        User bank = new User();
        bank.setUsername("bank");
        bank.setPassword(passwordEncoder.encode("bank123"));
        bank.setRole(Role.BANK);
        userRepository.save(bank);

        // Generate tokens without Bearer prefix
        adminToken = jwtUtil.generateToken("admin", Role.ADMIN);
        ishyigaToken = jwtUtil.generateToken("ishyiga", Role.ISHYIGA);
        bankToken = jwtUtil.generateToken("bank", Role.BANK);
    }

    private String createInvoiceRequest() throws Exception {
        Map<String, Object> invoice = new HashMap<>();
        invoice.put("idInvoice", 1);
        invoice.put("numClient", "TEST" + System.currentTimeMillis());
        invoice.put("date", "2024-03-25");
        invoice.put("total", 1000.0);
        invoice.put("employe", "Test Employee");
        invoice.put("heure", new Timestamp(System.currentTimeMillis()));
        invoice.put("tva", 18.0);
        invoice.put("document", 0);
        invoice.put("scanned", "1");
        invoice.put("mode", "CASH");
        invoice.put("status", "PENDING");
        invoice.put("served", new Timestamp(System.currentTimeMillis()));
        invoice.put("retour", "OUI");
        invoice.put("numFact", "FG001");
        invoice.put("reference", "REF001");
        invoice.put("listItems", new ArrayList<>());
        return objectMapper.writeValueAsString(invoice);
    }

    private String createSaleRequest() throws Exception {
        Map<String, Object> sale = new HashMap<>();
        sale.put("client_id", "TEST" + System.currentTimeMillis());
        sale.put("month", 3);
        sale.put("year", 2024);
        sale.put("invoiceCount", 5);
        sale.put("salesValue", 5000.0);
        sale.put("totalVat", 900.0);
        sale.put("cash", 3000.0);
        sale.put("credit", 2000.0);
        return objectMapper.writeValueAsString(sale);
    }

    private String createPurchaseRequest() throws Exception {
        Map<String, Object> purchase = new HashMap<>();
        purchase.put("client_id", "TEST" + System.currentTimeMillis());
        purchase.put("month", 3);
        purchase.put("year", 2024);
        purchase.put("invoiceCount", 5);
        purchase.put("poValue", 5000.0);
        return objectMapper.writeValueAsString(purchase);
    }

    private String createStockRequest() throws Exception {
        Map<String, Object> stock = new HashMap<>();
        stock.put("clientId", "TEST" + System.currentTimeMillis());
        stock.put("totalValue", 10000L);
        stock.put("today", new Date());
        return objectMapper.writeValueAsString(stock);
    }

    private String createListItemRequest() throws Exception {
        String uniqueId = String.valueOf(System.currentTimeMillis());
        Map<String, Object> invoice = new HashMap<>();
        invoice.put("date", "2025-03-25");
        invoice.put("heure", "10:00:00");
        invoice.put("document", "DOC" + uniqueId);
        invoice.put("employe", "EMP" + uniqueId);
        invoice.put("mode", "CASH");
        invoice.put("numClient", "CLT" + uniqueId);
        invoice.put("numFact", "FG" + uniqueId);
        invoice.put("reference", "REF" + uniqueId);

        // Create the Invoice first and wait for it to complete
        String invoiceJson = objectMapper.writeValueAsString(invoice);
        ResultActions invoiceResult = mockMvc.perform(post("/api/v1/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invoiceJson)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated());

        // Get the created Invoice's ID
        String responseBody = invoiceResult.andReturn().getResponse().getContentAsString();
        Map<String, Object> createdInvoice = objectMapper.readValue(responseBody, Map.class);
        Integer invoiceId = (Integer) createdInvoice.get("id");

        // Now create the ListItem with the Invoice's ID
        Map<String, Object> listItem = new HashMap<>();
        listItem.put("listIdProduct", 1);
        Map<String, Object> invoiceMap = new HashMap<>();
        invoiceMap.put("id", invoiceId);
        listItem.put("invoice", invoiceMap);  // Wrap the invoice ID in an object
        listItem.put("codeUni", "CODE" + uniqueId);
        listItem.put("numLot", "LOT" + uniqueId);
        listItem.put("quantite", 10);
        listItem.put("price", 100.0);
        listItem.put("prixRevient", 80.0);
        listItem.put("scanned", "1");
        listItem.put("dateExp", "2025-12-31");
        listItem.put("bonLivraison", "BL" + uniqueId);
        listItem.put("qteRetourne", 0);
        listItem.put("tva", 18.0);
        listItem.put("originalPrice", 100.0);
        listItem.put("keyInvoice", "KEY" + uniqueId);
        listItem.put("warehouse", "PRINCIPALE");
        listItem.put("igicumaStatus", "NO");
        listItem.put("taxCode", "TAX" + uniqueId);
        listItem.put("state", "ACTIVE");

        return objectMapper.writeValueAsString(listItem);
    }

    private String createOrderRequest() throws Exception {
        String uniqueClientId = "TEST" + System.currentTimeMillis();
        Map<String, Object> order = new HashMap<>();
        order.put("client_id", uniqueClientId);
        order.put("month", 3);
        order.put("day", 25);
        order.put("year", 2024);
        order.put("itemCount", 5);
        order.put("poValue", 5000.0);
        return objectMapper.writeValueAsString(order);
    }

    private String createItemRequest() throws Exception {
        Map<String, Object> item = new HashMap<>();
        item.put("idProduct", "PROD" + System.currentTimeMillis());
        item.put("nameProduct", "Test Product");
        item.put("code", "CODE001");
        item.put("prix", new BigDecimal("100.0"));
        item.put("prixSociete", new BigDecimal("90.0"));
        item.put("prixRevient", new BigDecimal("80.0"));
        item.put("tva", new BigDecimal("18.0"));
        return objectMapper.writeValueAsString(item);
    }

    private ResultActions performGet(String endpoint, String token) throws Exception {
        return mockMvc.perform(get(endpoint)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performPost(String endpoint, String token, String requestBody) throws Exception {
        return mockMvc.perform(post(endpoint)
                .header("Authorization", "Bearer " + token)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performPut(String endpoint, String token, String requestBody) throws Exception {
        return mockMvc.perform(put(endpoint)
                .header("Authorization", "Bearer " + token)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performDelete(String endpoint, String token) throws Exception {
        return mockMvc.perform(delete(endpoint)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testInvoicesEndpoints() throws Exception {
        // Test GET /api/v1/invoices - All roles should have access
        performGet("/api/v1/invoices", adminToken)
                .andExpect(status().isOk());

        performGet("/api/v1/invoices", ishyigaToken)
                .andExpect(status().isOk());

        performGet("/api/v1/invoices", bankToken)
                .andExpect(status().isOk());

        // Test POST /api/v1/invoices - Only ADMIN and ISHYIGA should have access
        performPost("/api/v1/invoices", adminToken, createInvoiceRequest())
                .andExpect(status().isCreated());

        performPost("/api/v1/invoices", ishyigaToken, createInvoiceRequest())
                .andExpect(status().isCreated());

        performPost("/api/v1/invoices", bankToken, createInvoiceRequest())
                .andExpect(status().isForbidden());
    }

    @Test
    void testSalesEndpoints() throws Exception {
        // Test GET /api/v1/sales - All roles should have access
        performGet("/api/v1/sales", adminToken)
                .andExpect(status().isOk());

        performGet("/api/v1/sales", ishyigaToken)
                .andExpect(status().isOk());

        performGet("/api/v1/sales", bankToken)
                .andExpect(status().isOk());

        // Test POST /api/v1/sales - Only ADMIN should have access
        performPost("/api/v1/sales", adminToken, createSaleRequest())
                .andExpect(status().isCreated());

        performPost("/api/v1/sales", ishyigaToken, createSaleRequest())
                .andExpect(status().isForbidden());

        performPost("/api/v1/sales", bankToken, createSaleRequest())
                .andExpect(status().isForbidden());
    }

    @Test
    void testPurchasesEndpoints() throws Exception {
        // Test GET /api/v1/purchases - All roles should have access
        performGet("/api/v1/purchases", adminToken)
                .andExpect(status().isOk());

        performGet("/api/v1/purchases", ishyigaToken)
                .andExpect(status().isOk());

        performGet("/api/v1/purchases", bankToken)
                .andExpect(status().isOk());

        // Test POST /api/v1/purchases - Only ADMIN and ISHYIGA should have access
        String uniqueClientId = "TEST" + System.currentTimeMillis();
        String purchaseJson = String.format("""
                {
                    "client_id": "%s",
                    "month": 3,
                    "year": 2024,
                    "invoiceCount": 5,
                    "poValue": 5000.0
                }""", uniqueClientId);

        performPost("/api/v1/purchases", adminToken, purchaseJson)
                .andExpect(status().isCreated());

        uniqueClientId = "TEST" + System.currentTimeMillis();
        purchaseJson = String.format("""
                {
                    "client_id": "%s",
                    "month": 3,
                    "year": 2024,
                    "invoiceCount": 5,
                    "poValue": 5000.0
                }""", uniqueClientId);

        performPost("/api/v1/purchases", ishyigaToken, purchaseJson)
                .andExpect(status().isCreated());

        performPost("/api/v1/purchases", bankToken, purchaseJson)
                .andExpect(status().isForbidden());
    }

    @Test
    void testStockEndpoints() throws Exception {
        // Test GET /api/v1/stocks - Admin Role
        mockMvc.perform(get("/api/v1/stocks")
                .param("sortBy", "clientId")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // Test GET /api/v1/stocks - Ishyiga Role
        mockMvc.perform(get("/api/v1/stocks")
                .param("sortBy", "clientId")
                .header("Authorization", "Bearer " + ishyigaToken))
                .andExpect(status().isOk());

        // Test GET /api/v1/stocks - Bank Role
        mockMvc.perform(get("/api/v1/stocks")
                .param("sortBy", "clientId")
                .header("Authorization", "Bearer " + bankToken))
                .andExpect(status().isOk());

        // Test POST /api/v1/stocks - Admin Role
        mockMvc.perform(post("/api/v1/stocks")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createStockRequest()))
                .andExpect(status().isCreated());

        // Test POST /api/v1/stocks - Ishyiga Role
        mockMvc.perform(post("/api/v1/stocks")
                .header("Authorization", "Bearer " + ishyigaToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createStockRequest()))
                .andExpect(status().isCreated());

        // Test POST /api/v1/stocks - Bank Role (should be forbidden)
        mockMvc.perform(post("/api/v1/stocks")
                .header("Authorization", "Bearer " + bankToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createStockRequest()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testListItemEndpoints() throws Exception {
        // Test GET /api/v1/list-items - Admin Role
        mockMvc.perform(get("/api/v1/list-items")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // Test GET /api/v1/list-items - Ishyiga Role
        mockMvc.perform(get("/api/v1/list-items")
                .header("Authorization", "Bearer " + ishyigaToken))
                .andExpect(status().isOk());

        // Test GET /api/v1/list-items - Bank Role
        mockMvc.perform(get("/api/v1/list-items")
                .header("Authorization", "Bearer " + bankToken))
                .andExpect(status().isOk());

        // Test POST /api/v1/list-items/create - Admin Role
        mockMvc.perform(post("/api/v1/list-items/create")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createListItemRequest()))
                .andExpect(status().isCreated());

        // Test POST /api/v1/list-items/create - Ishyiga Role
        mockMvc.perform(post("/api/v1/list-items/create")
                .header("Authorization", "Bearer " + ishyigaToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createListItemRequest()))
                .andExpect(status().isCreated());

        // Test POST /api/v1/list-items/create - Bank Role (should be forbidden)
        mockMvc.perform(post("/api/v1/list-items/create")
                .header("Authorization", "Bearer " + bankToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createListItemRequest()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testOrderEndpoints() throws Exception {
        // Test GET /api/v1/orders - All roles should have access
        performGet("/api/v1/orders", adminToken)
                .andExpect(status().isOk());

        performGet("/api/v1/orders", ishyigaToken)
                .andExpect(status().isOk());

        performGet("/api/v1/orders", bankToken)
                .andExpect(status().isOk());

        // Test POST /api/v1/orders - Only ADMIN and ISHYIGA should have access
        performPost("/api/v1/orders", adminToken, createOrderRequest())
                .andExpect(status().isCreated());

        performPost("/api/v1/orders", ishyigaToken, createOrderRequest())
                .andExpect(status().isCreated());

        performPost("/api/v1/orders", bankToken, createOrderRequest())
                .andExpect(status().isForbidden());
    }

    @Test
    void testItemEndpoints() throws Exception {
        // Test GET /api/v1/items - All roles should have access
        performGet("/api/v1/items", adminToken)
                .andExpect(status().isOk());

        performGet("/api/v1/items", ishyigaToken)
                .andExpect(status().isOk());

        performGet("/api/v1/items", bankToken)
                .andExpect(status().isOk());

        // Test POST /api/v1/items - Only ADMIN and ISHYIGA should have access
        performPost("/api/v1/items", adminToken, createItemRequest())
                .andExpect(status().isCreated());

        performPost("/api/v1/items", ishyigaToken, createItemRequest())
                .andExpect(status().isCreated());

        performPost("/api/v1/items", bankToken, createItemRequest())
                .andExpect(status().isForbidden());
    }
} 