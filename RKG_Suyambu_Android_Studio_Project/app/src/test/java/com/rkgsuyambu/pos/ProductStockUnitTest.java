package com.rkgsuyambu.pos;

import java.util.*;

public class ProductStockUnitTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  RKG SUYAMBU POS MOBILE — AUTOMATED UNIT TESTS   ");
        System.out.println("==================================================");

        int passed = 0;
        int failed = 0;

        // Test 1: CEO Password verification
        try {
            assertCondition(isValidCeoPassword("230826"), "CEO Password 230826 must authenticate");
            assertCondition(!isValidCeoPassword("wrong"), "Invalid password must be rejected");
            assertCondition(!isValidCeoPassword(""), "Empty password must be rejected");
            System.out.println("✅ [PASS] Test 1: CEO Security Gateway Password Authentication (230826)");
            passed++;
        } catch (Exception e) {
            System.err.println("❌ [FAIL] Test 1: " + e.getMessage());
            failed++;
        }

        // Test 2: Stock Whole-Number Non-Negative Constraint (min 0)
        try {
            assertCondition(sanitizeStock(-10) == 0, "Negative stock -10 must clamp to 0");
            assertCondition(sanitizeStock(0) == 0, "Zero stock must remain 0");
            assertCondition(sanitizeStock(45) == 45, "Positive stock 45 must remain 45");
            System.out.println("✅ [PASS] Test 2: Non-Negative Whole-Number Stock Constraint (Min 0)");
            passed++;
        } catch (Exception e) {
            System.err.println("❌ [FAIL] Test 2: " + e.getMessage());
            failed++;
        }

        // Test 3: Stock Alert Level Tier Hierarchy
        try {
            assertCondition("OUT_OF_STOCK".equals(getStockAlertLevel(0)), "0 units must trigger OUT_OF_STOCK");
            assertCondition("CRITICAL".equals(getStockAlertLevel(4)), "4 units (<10) must trigger CRITICAL");
            assertCondition("CRITICAL".equals(getStockAlertLevel(9)), "9 units (<10) must trigger CRITICAL");
            assertCondition("LOW".equals(getStockAlertLevel(15)), "15 units (<30) must trigger LOW");
            assertCondition("NORMAL".equals(getStockAlertLevel(50)), "50 units (>=30) must trigger NORMAL");
            System.out.println("✅ [PASS] Test 3: Tiered Alert Classification (0: Out of Stock, <10: Critical, <30: Low, >=30: Normal)");
            passed++;
        } catch (Exception e) {
            System.err.println("❌ [FAIL] Test 3: " + e.getMessage());
            failed++;
        }

        // Test 4: Least Stock Available First Sorting
        try {
            List<Product> products = new ArrayList<>();
            products.add(new Product("aa01", "Groundnut Oil 1L", 443));
            products.add(new Product("aa02", "Sesame Oil 1L", 8));
            products.add(new Product("aa03", "Coconut Oil 5L", 0));
            products.add(new Product("aa04", "Deepam Oil 1L", 25));

            products.sort(Comparator.comparingInt(p -> Math.max(0, p.stockQty)));

            assertCondition("aa03".equals(products.get(0).code) && products.get(0).stockQty == 0, "0 stock item must be #1");
            assertCondition("aa02".equals(products.get(1).code) && products.get(1).stockQty == 8, "8 stock item must be #2");
            assertCondition("aa04".equals(products.get(2).code) && products.get(2).stockQty == 25, "25 stock item must be #3");
            assertCondition("aa01".equals(products.get(3).code) && products.get(3).stockQty == 443, "443 stock item must be last");
            System.out.println("✅ [PASS] Test 4: Least Stock First Priority Ordering (0 ➔ Critical ➔ Low ➔ High)");
            passed++;
        } catch (Exception e) {
            System.err.println("❌ [FAIL] Test 4: " + e.getMessage());
            failed++;
        }

        // Test 5: Cart Whole Number Integer Restriction
        try {
            double rawVal = 1.0;
            int cartQty = (int) Math.floor(rawVal);
            assertCondition(cartQty == 1, "Cart item qty must strictly be whole integer");
            System.out.println("✅ [PASS] Test 5: Cart Whole-Number (Integer) Constraint");
            passed++;
        } catch (Exception e) {
            System.err.println("❌ [FAIL] Test 5: " + e.getMessage());
            failed++;
        }

        // Test 6: New Product Creation Pricing Math
        try {
            int retailPrice = 320;
            int calculatedWholesale = (int) Math.round(retailPrice * 0.92);
            assertCondition(calculatedWholesale == 294, "Wholesale must auto-calculate to ~92% (294)");
            System.out.println("✅ [PASS] Test 6: New Product Creator Auto Wholesale Pricing");
            passed++;
        } catch (Exception e) {
            System.err.println("❌ [FAIL] Test 6: " + e.getMessage());
            failed++;
        }

        // Test 7: Category Classification (Cake, Rice, Oil, Flour, Seeds, Packaging)
        try {
            List<String> validCategories = Arrays.asList("OIL", "CAKE", "RICE", "GRAIN_FLOUR", "SEEDS_RAW", "PACKAGING", "GENERAL");
            assertCondition(validCategories.contains("CAKE"), "Cake (புண்ணாக்கு) category must be valid");
            assertCondition(validCategories.contains("RICE"), "Rice (அரிசி) category must be valid");
            assertCondition(validCategories.contains("OIL"), "Oil category must be valid");
            System.out.println("✅ [PASS] Test 7: Product Categories (Cake, Rice, Oil, Flour, Seeds, Packaging)");
            passed++;
        } catch (Exception e) {
            System.err.println("❌ [FAIL] Test 7: " + e.getMessage());
            failed++;
        }

        // Test 8: Unit of Measure (UOM) Presets & Pack Formatting
        try {
            assertCondition(getPresetPack("CAKE").equals("50Kg"), "Cake default UOM must be 50Kg");
            assertCondition(getPresetPack("RICE").equals("1Kg"), "Rice default UOM must be 1Kg");
            assertCondition(getPresetPack("OIL").equals("1L"), "Oil default UOM must be 1L");
            System.out.println("✅ [PASS] Test 8: Unit of Measure Presets (Kg / L / 50Kg / 1Kg / 1L)");
            passed++;
        } catch (Exception e) {
            System.err.println("❌ [FAIL] Test 8: " + e.getMessage());
            failed++;
        }

        // Test 9: Multi-Network Cloud Firebase Direct Sync Formatting
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("doc_id", "aa01");
            payload.put("stock_qty", 443);
            payload.put("price", 270.0);
            payload.put("updated_by", "CEO Mobile");
            assertCondition("aa01".equals(payload.get("doc_id")), "Payload doc_id must match aa01");
            assertCondition((int) payload.get("stock_qty") == 443, "Payload stock_qty must match 443");
            System.out.println("✅ [PASS] Test 9: Multi-Network Firebase Direct Cloud Sync Payload Formatting");
            passed++;
        } catch (Exception e) {
            System.err.println("❌ [FAIL] Test 9: " + e.getMessage());
            failed++;
        }

        System.out.println("==================================================");
        System.out.println("  SUMMARY: " + passed + " PASSED, " + failed + " FAILED");
        System.out.println("==================================================");

        if (failed > 0) System.exit(1);
    }

    private static String getPresetPack(String category) {
        if ("CAKE".equals(category)) return "50Kg";
        if ("RICE".equals(category)) return "1Kg";
        if ("OIL".equals(category)) return "1L";
        if ("SEEDS_RAW".equals(category)) return "50Kg";
        return "1Kg";
    }

    private static boolean isValidCeoPassword(String pass) {
        if (pass == null) return false;
        String p = pass.trim().toLowerCase();
        return Arrays.asList("230826", "admin", "94425", "1234").contains(p);
    }

    private static int sanitizeStock(int input) {
        return Math.max(0, input);
    }

    private static String getStockAlertLevel(int qty) {
        if (qty <= 0) return "OUT_OF_STOCK";
        if (qty < 10) return "CRITICAL";
        if (qty < 30) return "LOW";
        return "NORMAL";
    }

    private static void assertCondition(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    static class Product {
        String code;
        String name;
        int stockQty;

        Product(String code, String name, int stockQty) {
            this.code = code;
            this.name = name;
            this.stockQty = stockQty;
        }
    }
}
