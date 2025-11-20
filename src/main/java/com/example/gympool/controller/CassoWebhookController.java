package com.example.gympool.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;

@RestController
@RequestMapping("/api/casso")
public class CassoWebhookController {

    // Lưu toàn bộ transaction theo description
    private static final ConcurrentHashMap<String, Map<String, Object>> txnMap = new ConcurrentHashMap<>();

    // Webhook Casso gọi vào
    @PostMapping("/webhook")
    public ResponseEntity<Map<String, Object>> handleWebhook(@RequestBody Map<String, Object> payload) {

        System.out.println("=== Webhook received from Casso ===");
        System.out.println(payload);

        List<Map<String, Object>> data = (List<Map<String, Object>>) payload.get("data");
        if (data == null || data.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "No transaction"));
        }

        Map<String, Object> txn = data.get(0);

        // Lấy thông tin với fallback nếu null
        double amount = txn.get("amount") != null ? Double.parseDouble(txn.get("amount").toString()) : 0;
        String description = txn.get("description") != null ? txn.get("description").toString() : "";
        String tranId = txn.get("tid") != null ? txn.get("tid").toString() : "";
        String bank = txn.get("bank") != null ? txn.get("bank").toString() : "";
        Object cusumBalance = txn.get("cusumBalance"); // có thể null

        // Lưu nguyên transaction vào RAM
        Map<String, Object> storedTxn = new HashMap<>();
        storedTxn.put("paid", true);
        storedTxn.put("amount", amount);
        storedTxn.put("description", description);
        storedTxn.put("tranId", tranId);
        storedTxn.put("bank", bank);
        storedTxn.put("cusumBalance", cusumBalance);

        txnMap.put(description, storedTxn);

        // Log ra console
        System.out.println("Payment received!");
        System.out.println("Amount: " + amount);
        System.out.println("Description: " + description);
        System.out.println("Transaction ID: " + tranId);

        return ResponseEntity.ok(storedTxn);
    }

    // FE poll status
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkPayment(
            @RequestParam String addInfo,
            @RequestParam double amount
    ) {
        // Duyệt tất cả transactions trong RAM
        for (Map<String, Object> txn : txnMap.values()) {
            String desc = (String) txn.get("description");
            double txnAmount = (double) txn.get("amount");

            // Check nếu amount đúng và description chứa addInfo (partial match)
            if (txnAmount == amount && desc != null && desc.contains(addInfo)) {
                return ResponseEntity.ok(txn);
            }
        }

        // Không tìm thấy transaction nào phù hợp
        Map<String, Object> result = new HashMap<>();
        result.put("paid", false);
        return ResponseEntity.ok(result);
    }

}
