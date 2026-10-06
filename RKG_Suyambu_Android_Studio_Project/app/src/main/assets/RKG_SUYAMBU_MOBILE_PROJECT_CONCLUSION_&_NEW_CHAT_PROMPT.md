# 🌾 RKG SUYAMBU ENTERPRISE — MOBILE & ANDROID POS SYSTEM
## PROJECT CONCLUSION & MASTER NEW CHAT CONTINUITY HANDOVER

**File Path:** `C:\Users\sanmu\OneDrive\Documents\suiambu\RKG_SUYAMBU_MOBILE_PROJECT_CONCLUSION_&_NEW_CHAT_PROMPT.md`  
**Generated:** September 2026  
**Target Platform:** Android (Mobile PWA / WebAPK / APK) & Windows 11 Native Desktop POS  

---

## 1. 📌 PROJECT OVERVIEW & STATUS

The **RKG Suyambu Enterprise POS System** is a unified retail billing and inventory management suite built specifically for cold-pressed oils, agricultural grains, cattle feed, and premium rice.

### Core Ecosystem Components:
1. **Android & Mobile POS Suite (`RKG_Suyambu_Mobile_App`):**
   - Touch-optimized responsive interface with haptic feedback.
   - Bilingual support: **English** + **Tamil** (மரச்செக்கு தேங்காய் எண்ணெய், சுயம்பு மக்காச்சோளம், etc.).
   - Full offline functionality via Progressive Web App (PWA) and Service Worker.
   - Mandatory **10-Digit Customer Mobile Number** validation before checkout.
   - **1-Tap WhatsApp Receipt Generator** via `wa.me/91<phone>` with formatted billing summary and Tamil greeting (*நன்றி! மீண்டும் வருக!*).
   - **58mm / 80mm Bluetooth Thermal Printer** ESC/POS engine.
   - Payment modes: **UPI (GPay/PhonePe/Paytm)**, **CASH**, **CREDIT** (Bank Transfer removed per requirement).
   - Multi-tier PIN Security: **CEO Mode (`1234`)** and **Cashier Mode (`5678`)**.

2. **Windows 11 Desktop POS Suite (`RKG_Suyambu_Windows11_POS`):**
   - Native C# WinForms standalone executable (`New_Bill_Windows11.exe`).
   - Fullscreen auto-maximization on Windows 10 & 11.
   - TVS RP 3200 Lite direct USB thermal roll printing with auto-cutter commands.
   - Zero GST supply billing mode with 1-click desktop launcher.

---

## 2. 📂 COMPLETE FILE DIRECTORY MAP

```
C:\Users\sanmu\OneDrive\Documents\suiambu\
├── RKG_SUYAMBU_MOBILE_PROJECT_CONCLUSION_&_NEW_CHAT_PROMPT.md  <-- Master Handover File
│
├── RKG_Suyambu_Mobile_App\                      <-- Android / Mobile POS Application
│   ├── index.html                               <-- Mobile UI & Cart Drawer
│   ├── app.js                                   <-- Core Billing & WhatsApp & Print Logic
│   ├── style.css                                <-- Mobile Touch Theme & Thermal CSS
│   ├── catalog.json                             <-- 37-Product Master Database
│   ├── manifest.json                            <-- Android WebAPK / PWA Manifest
│   ├── sw.js                                    <-- 100% Offline Service Worker
│   ├── Launch_Mobile_App.bat                    <-- Local Launcher Script
│   └── README_MOBILE.md                         <-- Mobile Setup & Install Guide
│
└── RKG_Suyambu_Windows11_POS\                   <-- Windows 11 Desktop POS Application
    ├── New_Bill_Windows11.exe                   <-- Compiled Standalone POS
    ├── RKG_Suyambu_Billing_Universal.cs         <-- Full C# WinForms Source Code
    ├── Launch_POS.bat                           <-- 1-Click Launch Script
    └── INSTRUCTIONS.txt                         <-- Windows Release Notes
```

---

## 3. 📋 MASTER 37-PRODUCT CATALOG SPECIFICATION

| S.No | Category (பிரிவு) | SKU Code | Product Name | Tamil Name (தமிழ் பெயர்) | Unit / Weight | HSN | Price (₹) |
|:---:|:---|:---|:---|:---|:---:|:---:|:---:|
| 1 | Cold-Pressed Oils | `OIL-COC-5L` | Coconut Oil | மரச்செக்கு தேங்காய் எண்ணெய் | 5 Liters | 1513 | ₹ 1,500 |
| 2 | Cold-Pressed Oils | `OIL-COC-1L` | Coconut Oil | மரச்செக்கு தேங்காய் எண்ணெய் | 1 Liter | 1513 | ₹ 300 |
| 3 | Cold-Pressed Oils | `OIL-COC-500M` | Coconut Oil | மரச்செக்கு தேங்காய் எண்ணெய் | 500 ml | 1513 | ₹ 150 |
| 4 | Cold-Pressed Oils | `OIL-GND-5L` | Peanut Oil | மரச்செக்கு கடலை எண்ணெய் | 5 Liters | 1508 | ₹ 1,300 |
| 5 | Cold-Pressed Oils | `OIL-GND-1L` | Peanut Oil | மரச்செக்கு கடலை எண்ணெய் | 1 Liter | 1508 | ₹ 260 |
| 6 | Cold-Pressed Oils | `OIL-GND-500M` | Peanut Oil | மரச்செக்கு கடலை எண்ணெய் | 500 ml | 1508 | ₹ 130 |
| 7 | Cold-Pressed Oils | `OIL-SES-5L` | Gingelly Oil | மரச்செக்கு நல்லெண்ணெய் | 5 Liters | 1515 | ₹ 1,750 |
| 8 | Cold-Pressed Oils | `OIL-SES-1L` | Gingelly Oil | மரச்செக்கு நல்லெண்ணெய் | 1 Liter | 1515 | ₹ 350 |
| 9 | Cold-Pressed Oils | `OIL-SES-500M` | Gingelly Oil | மரச்செக்கு நல்லெண்ணெய் | 500 ml | 1515 | ₹ 175 |
| 10 | Grains & Millets | `GRN-WHT-1K` | Wheat | கோதுமை | 1 kg | 1001 | ₹ 70 |
| 11 | Grains & Millets | `GRN-RAG-1K` | Ragi | கேழ்வரகு / ராகி | 1 kg | 1008 | ₹ 70 |
| 12 | Grains & Millets | `GRN-GND-1K` | Groundnut | வேர்க்கடலை / நிலக்கடலை | 1 kg | 1202 | ₹ 150 |
| 13 | Grains & Millets | `GRN-CRN-1K` | Suyambu Corn | சுயம்பு மக்காச்சோளம் | 1 kg | 1005 | ₹ 32 |
| 14 | Spices & Masalas | `SPC-SAM-200G` | Sambar Powder | சாம்பார் பொடி | 200 g | 0910 | ₹ 60 |
| 15 | Spices & Masalas | `SPC-MUT-200G` | Mutton Masala | மட்டன் மசாலா பொடி | 200 g | 0910 | ₹ 75 |
| 16 | Cattle Feed | `FEE-COT-50K` | Cotton Seeds | பருத்தி கொட்டை | 50 kg | 1207 | ₹ 2,500 |
| 17 | Cattle Feed | `FEE-COT-1K` | Cotton Seeds | பருத்தி கொட்டை | 1 kg | 1207 | ₹ 50 |
| 18 | Cattle Feed | `FEE-GCS-40K` | Grained Cotton Seeds | அரைத்த பருத்தி கொட்டை | 40 kg | 1207 | ₹ 2,100 |
| 19 | Cattle Feed | `FEE-GCS-1K` | Grained Cotton Seeds | அரைத்த பருத்தி கொட்டை | 1 kg | 1207 | ₹ 50 |
| 20 | Cattle Feed | `FEE-RSN-50K` | Suyambu Nayam Cattle Feed | சுயம்பு நயம் மாட்டுத்தீவனம் | 50 kg | 2309 | ₹ 1,400 |
| 21 | Cattle Feed | `FEE-SCC-50K` | Corn Clay / Flour | சுயம்பு மக்காச்சோள மாவு | 50 kg | 1102 | ₹ 1,700 |
| 22 | Cattle Feed | `FEE-KBP-70K` | Krishi Bio Pass | கிருஷி பயோ பாஸ் மாட்டுத்தீவனம் | 70 kg | 2309 | ₹ 1,900 |
| 23 | Cattle Feed | `FEE-KBP-50K` | Krishi Bio Pass | கிருஷி பயோ பாஸ் மாட்டுத்தீவனம் | 50 kg | 2309 | ₹ 1,400 |
| 24 | Cattle Feed | `FEE-KBP-20K` | Krishi Bio Pass | கிருஷி பயோ பாஸ் மாட்டுத்தீவனம் | 20 kg | 2309 | ₹ 600 |
| 25 | Cattle Feed | `FEE-KRB-70K` | Krishi Pro-Best | கிருஷி புரோ-பெஸ்ட் மாட்டுத்தீவனம் | 70 kg | 2309 | ₹ 2,100 |
| 26 | Cattle Feed | `FEE-SNP-50K` | Suyambu Rice Bran | சுயம்பு அரிசி தவிடு | 50 kg | 2309 | ₹ 880 |
| 27 | By-Products | `FEE-CCW-1K` | Coconut Cake | தேங்காய் புண்ணாக்கு | 1 kg | 2306 | ₹ 40 |
| 28 | Poultry Feed | `FEE-CKN-50K` | Krishi Chicken Feed | கிருஷி கோழித்தீவனம் | 50 kg | 2309 | ₹ 1,550 |
| 29 | Premium Rice | `RIC-VRN-26K` | Veeran Cooking Rice | வீரன் சாப்பாடு அரிசி | 26 kg | 1006 | ₹ 1,700 |
| 30 | Premium Rice | `RIC-VRN-10K` | Veeran Cooking Rice | வீரன் சாப்பாடு அரிசி | 10 kg | 1006 | ₹ 700 |
| 31 | Premium Rice | `RIC-VRN-5K` | Veeran Cooking Rice | வீரன் சாப்பாடு அரிசி | 5 kg | 1006 | ₹ 370 |
| 32 | Premium Rice | `RIC-PON-26K` | A1 SSS Kollam Ponni | A1 SSS கொல்லம் பொன்னி அரிசி | 26 kg | 1006 | ₹ 2,500 |
| 33 | Premium Rice | `RIC-PON-10K` | A1 SSS Kollam Ponni | A1 SSS கொல்லம் பொன்னி அரிசி | 10 kg | 1006 | ₹ 900 |
| 34 | Premium Rice | `RIC-PON-5K` | A1 SSS Kollam Ponni | A1 SSS கொல்லம் பொன்னி அரிசி | 5 kg | 1006 | ₹ 500 |
| 35 | Premium Rice | `RIC-IR20-26K` | Veera Shivaji IR 20 | வீர சிவாஜி IR 20 அரிசி | 26 kg | 1006 | ₹ 1,300 |
| 36 | Premium Rice | `RIC-IR20-10K` | Veera Shivaji IR 20 | வீர சிவாஜி IR 20 அரிசி | 10 kg | 1006 | ₹ 550 |
| 37 | Premium Rice | `RIC-IR20-5K` | Veera Shivaji IR 20 | வீர சிவாஜி IR 20 அரிசி | 5 kg | 1006 | ₹ 300 |

---

## 4. 📲 ANDROID INSTALLATION & USAGE MODES

### Option A: Install via Chrome on Android (Instant PWA)
1. Open the project URL or [index.html](file:///C:/Users/sanmu/OneDrive/Documents/suiambu/RKG_Suyambu_Mobile_App/index.html) in Google Chrome on your Android phone.
2. Tap the **three vertical dots (⋮)** in the top-right corner.
3. Select **"Install App"** or **"Add to Home screen"**.
4. The **RKG Suyambu POS** icon (🌾) will appear in your Android app drawer with full offline standalone privileges.

### Option B: Packaging into Native Android APK (Capacitor / Cordova)
To package this app into an installable `.apk` file for Android:
```bash
npm install @capacitor/core @capacitor/cli @capacitor/android
npx cap init "RKG Suyambu POS" "com.rkgsuyambu.pos" --web-dir "."
npx cap add android
npx cap open android
# Build APK in Android Studio
```

---

## 5. 🤖 MASTER NEW CHAT PROMPT (COPY & PASTE INTO NEW SESSION)

> **Copy and paste everything within the block below when starting a new chat session:**

```text
================================================================================
RKG SUYAMBU ENTERPRISE — MASTER CONTEXT & WORKSPACE BOOTSTRAP PROMPT
================================================================================

Hi! Please load the full context of the RKG Suyambu Enterprise POS System.

📁 ACTIVE WORKSPACE PATH:
C:\Users\sanmu\OneDrive\Documents\suiambu\

📂 KEY PROJECT DIRECTORIES:
1. Android & Mobile POS App:
   C:\Users\sanmu\OneDrive\Documents\suiambu\RKG_Suyambu_Mobile_App\
   - index.html (Mobile UI & Cart Drawer)
   - app.js (Cart calculations, WhatsApp sharing, ESC/POS thermal printing)
   - style.css (Touch theme & 58mm/80mm thermal receipt styling)
   - catalog.json (Complete 37-Product Master Database)
   - manifest.json & sw.js (Offline PWA service worker)

2. Windows 11 Desktop POS:
   C:\Users\sanmu\OneDrive\Documents\suiambu\RKG_Suyambu_Windows11_POS\
   - New_Bill_Windows11.exe (Standalone WinForms POS)
   - RKG_Suyambu_Billing_Universal.cs (Full C# WinForms Source Code)
   - Launch_POS.bat & INSTRUCTIONS.txt

🔑 BUSINESS RULES & ACCESS CONTROL:
- CEO Mode PIN: 1234 (Full Admin & Audit Access)
- Cashier Mode PIN: 5678 (Counter Billing & Receipt Generation)
- Mandatory Customer Mobile: 10 digits required before completing any bill
- Allowed Payment Modes: UPI (GPay/PhonePe), CASH, CREDIT (Bank Transfer disabled)
- Bill Format: Retail Bill of Supply (GST removed for retail supply)
- Languages: Bilingual English + Tamil (e.g. மரச்செக்கு தேங்காய் எண்ணெய்)
- Printer Support: Bluetooth / USB Thermal ESC/POS (58mm/80mm & TVS RP 3200)

Please acknowledge this workspace context and let me know what task we should tackle next.
================================================================================
```

---
**RKG Suyambu Enterprise • உழவர் நலம் • மக்கள் நலம்**
