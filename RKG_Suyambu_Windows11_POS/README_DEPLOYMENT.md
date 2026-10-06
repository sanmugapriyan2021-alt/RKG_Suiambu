# RKG Suyambu POS Billing Station - Production Deployment Guide

## 1. System Overview
**RKG Suyambu POS Billing Station** is an enterprise-grade Windows 11 point-of-sale terminal built for oil mills, cattle feed retail, and agro-product distribution with hybrid offline-first cloud synchronization and 80mm ESC/POS hardware thermal printing.

---

## 2. Quick Launch & Credentials

### How to Launch in Production:
- **Option 1 (Recommended)**: Double-click the **`RKG Suyambu POS Billing`** shortcut on the Windows Desktop.
- **Option 2**: Run [`New_Bill_Windows11.exe`](file:///c:/Users/sanmu/OneDrive/Documents/suiambu/RKG_Suyambu_Windows11_POS/New_Bill_Windows11.exe) or [`Launch_POS.bat`](file:///c:/Users/sanmu/OneDrive/Documents/suiambu/RKG_Suyambu_Windows11_POS/Launch_POS.bat).

### Terminal Access:
- **Password**: **`230826`**
- **Operator Role**: **`Billing`** (Billing Operator)
- **Central Management**: CEO manages catalog, pricing, transactions, and reports directly from Firebase.

---

## 3. Hardware & Network Compatibility

| Component | Standard Specification | Status Validation |
|---|---|---|
| **Receipt Printer** | 80mm (3-inch) ESC/POS Thermal (TVS RP 3200 Series) | Auto-detected via USB/WMI. Status indicator lights up green when plugged in, gray when unplugged. Non-thermal virtual printers are automatically rejected. |
| **Cloud Engine** | Google Firebase Firestore REST (`rkg-suyambu-erp`) | Hybrid Engine. Real-time background sync when connected; instant local FIFO queue (`pos_bills_queue.jsonl`) when offline. |
| **Operating System** | Windows 11 (x64) / Windows 10 | .NET Framework 4.8 runtime compatible. |

---

## 4. Firebase Cloud Data Architecture

### Collections Synchronized:
1. **`users/billing`**: Registers operator status, password hash, and live timestamp.
2. **`bill_numbers`**: Connects Bill Number (e.g., `RKG/260902/1645`) with Invoices.
3. **`daily_transactions`**: Stores transaction subtotal, discount, net amount, customer details, and itemized product breakdown with timestamp.
4. **`promo_codes`**: Live promo rules fetched from Firestore and cached locally (`pos_promos_cache.json`).

---

## 5. Active Promo Discount Rules

| Promo Code | Discount Rate | Minimum Order | Maximum Cap |
|---|---|---|---|
| **`DISC5`** | **5% OFF** | ₹0 | ₹500 |
| **`RKG10`** | **10% OFF** | ₹500 | ₹1,000 |
| **`FARMER15`** | **15% OFF** | ₹1,000 | ₹1,500 |
| **`SUYAMBU20`** | **20% OFF** | ₹1,500 | ₹2,000 |
| **`AGRO25`** | **25% OFF** | ₹2,500 | ₹2,500 |
| **`PONGAL30`** | **30% OFF** | ₹3,000 | ₹3,000 |
| **`RKG-PRM-2026-A01`** | **10% OFF** | ₹1,500 | ₹1,000 |

---

## 6. Automated Quality Assurance & Verification
Run [`Run_Tests.bat`](file:///c:/Users/sanmu/OneDrive/Documents/suiambu/RKG_Suyambu_Windows11_POS/Run_Tests.bat) to run the 39-point test suite:
- **Authentication**: Password verification and security lockout.
- **Cart Calculations**: Subtotal, discount capping, net amount recalculation.
- **Cart Editing**: Item quantity increment/decrement, selective deletion.
- **Firebase Payloads**: Structured JSON serialization for bills and daily transactions.
- **Hardware Validation**: Thermal printer detection and acceptance.
