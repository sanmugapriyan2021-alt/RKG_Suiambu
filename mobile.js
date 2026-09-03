/**
 * RKG SUYAMBU — Universal Enterprise Security & Management Engine
 * Primary Security Gateway: User_Login (CEO & Cashier Auth)
 * Tables: User_Login, Promo_Code, Profit, products, client_order_data, Change_History
 */

const SEED_PRODUCTS = [
  {
    "id": "aa01",
    "doc_id": "aa01",
    "code": "aa01",
    "name": "Pure Cold-Pressed Groundnut Oil 1L (மரச்செக்கு கடலை எண்ணெய் 1லி)",
    "product_name": "Pure Cold-Pressed Groundnut Oil 1L (மரச்செக்கு கடலை எண்ணெய் 1லி)",
    "tamil_name": "சுத்தமான மரச்செக்கு கடலை எண்ணெய் (1 லிட்டர் பாட்டில்)",
    "brand": "RKG Suyambu",
    "category": "FINISHED_GOOD",
    "uom": "LITRE",
    "unit_of_measure": "LITRE",
    "pack": "LITRE",
    "cost_price": 210.0,
    "price": 270.0,
    "selling_price": 270.0,
    "wholesale_price": 245.0,
    "tax_rate": 5.0,
    "stock_qty": 443,
    "current_stock": 443,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "aa02",
    "doc_id": "aa02",
    "code": "aa02",
    "name": "Pure Cold-Pressed Sesame/Gingelly Oil 1L (மரச்செக்கு நல்லெண்ணெய் 1லி)",
    "product_name": "Pure Cold-Pressed Sesame/Gingelly Oil 1L (மரச்செக்கு நல்லெண்ணெய் 1லி)",
    "tamil_name": "சுத்தமான மரச்செக்கு நல்லெண்ணெய் (1 லிட்டர் பாட்டில்)",
    "brand": "RKG Suyambu",
    "category": "FINISHED_GOOD",
    "uom": "LITRE",
    "unit_of_measure": "LITRE",
    "pack": "LITRE",
    "cost_price": 340.0,
    "price": 420.0,
    "selling_price": 420.0,
    "wholesale_price": 380.0,
    "tax_rate": 5.0,
    "stock_qty": 299,
    "current_stock": 299,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "aa03",
    "doc_id": "aa03",
    "code": "aa03",
    "name": "Suyambu Coconut Oil (5L Can)",
    "product_name": "Suyambu Coconut Oil (5L Can)",
    "tamil_name": "சுயம்பு தேங்காய் எண்ணெய் (5 லிட்டர்)",
    "brand": "Suyambu",
    "category": "FINISHED_GOOD",
    "uom": "CAN_5L",
    "unit_of_measure": "CAN_5L",
    "pack": "CAN_5L",
    "cost_price": 1300.0,
    "price": 1500.0,
    "selling_price": 1500.0,
    "wholesale_price": 1450.0,
    "tax_rate": 5.0,
    "stock_qty": 45,
    "current_stock": 45,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "aa04",
    "doc_id": "aa04",
    "code": "aa04",
    "name": "Suyambu Coconut Oil (1L Bottle)",
    "product_name": "Suyambu Coconut Oil (1L Bottle)",
    "tamil_name": "சுயம்பு தேங்காய் எண்ணெய் (1 லிட்டர்)",
    "brand": "Suyambu",
    "category": "FINISHED_GOOD",
    "uom": "LITRE",
    "unit_of_measure": "LITRE",
    "pack": "LITRE",
    "cost_price": 260.0,
    "price": 300.0,
    "selling_price": 300.0,
    "wholesale_price": 285.0,
    "tax_rate": 5.0,
    "stock_qty": 120,
    "current_stock": 120,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "aa05",
    "doc_id": "aa05",
    "code": "aa05",
    "name": "Suyambu Coconut Oil (500ml)",
    "product_name": "Suyambu Coconut Oil (500ml)",
    "tamil_name": "சுயம்பு தேங்காய் எண்ணெய் (500 மி.லி)",
    "brand": "Suyambu",
    "category": "FINISHED_GOOD",
    "uom": "PACKET_500ML",
    "unit_of_measure": "PACKET_500ML",
    "pack": "PACKET_500ML",
    "cost_price": 130.0,
    "price": 150.0,
    "selling_price": 150.0,
    "wholesale_price": 145.0,
    "tax_rate": 5.0,
    "stock_qty": 80,
    "current_stock": 80,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "aa06",
    "doc_id": "aa06",
    "code": "aa06",
    "name": "Suyambu Peanut Oil (5L Can)",
    "product_name": "Suyambu Peanut Oil (5L Can)",
    "tamil_name": "சுயம்பு கடலை எண்ணெய் (5 லிட்டர்)",
    "brand": "Suyambu",
    "category": "FINISHED_GOOD",
    "uom": "CAN_5L",
    "unit_of_measure": "CAN_5L",
    "pack": "CAN_5L",
    "cost_price": 1150.0,
    "price": 1300.0,
    "selling_price": 1300.0,
    "wholesale_price": 1250.0,
    "tax_rate": 5.0,
    "stock_qty": 35,
    "current_stock": 35,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "aa07",
    "doc_id": "aa07",
    "code": "aa07",
    "name": "Suyambu Peanut Oil (1L Bottle)",
    "product_name": "Suyambu Peanut Oil (1L Bottle)",
    "tamil_name": "சுயம்பு கடலை எண்ணெய் (1 லிட்டர்)",
    "brand": "Suyambu",
    "category": "FINISHED_GOOD",
    "uom": "LITRE",
    "unit_of_measure": "LITRE",
    "pack": "LITRE",
    "cost_price": 230.0,
    "price": 260.0,
    "selling_price": 260.0,
    "wholesale_price": 250.0,
    "tax_rate": 5.0,
    "stock_qty": 110,
    "current_stock": 110,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "aa08",
    "doc_id": "aa08",
    "code": "aa08",
    "name": "Suyambu Peanut Oil (500ml)",
    "product_name": "Suyambu Peanut Oil (500ml)",
    "tamil_name": "சுயம்பு கடலை எண்ணெய் (500 மி.லி)",
    "brand": "Suyambu",
    "category": "FINISHED_GOOD",
    "uom": "PACKET_500ML",
    "unit_of_measure": "PACKET_500ML",
    "pack": "PACKET_500ML",
    "cost_price": 120.0,
    "price": 140.0,
    "selling_price": 140.0,
    "wholesale_price": 135.0,
    "tax_rate": 5.0,
    "stock_qty": 75,
    "current_stock": 75,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "aa09",
    "doc_id": "aa09",
    "code": "aa09",
    "name": "Suyambu Gingelly Oil (5L Can)",
    "product_name": "Suyambu Gingelly Oil (5L Can)",
    "tamil_name": "சுயம்பு நல்லெண்ணெய் (5 லிட்டர்)",
    "brand": "Suyambu",
    "category": "FINISHED_GOOD",
    "uom": "CAN_5L",
    "unit_of_measure": "CAN_5L",
    "pack": "CAN_5L",
    "cost_price": 1550.0,
    "price": 1750.0,
    "selling_price": 1750.0,
    "wholesale_price": 1680.0,
    "tax_rate": 5.0,
    "stock_qty": 30,
    "current_stock": 30,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "aa10",
    "doc_id": "aa10",
    "code": "aa10",
    "name": "Suyambu Gingelly Oil (1L Bottle)",
    "product_name": "Suyambu Gingelly Oil (1L Bottle)",
    "tamil_name": "சுயம்பு நல்லெண்ணெய் (1 லிட்டர்)",
    "brand": "Suyambu",
    "category": "FINISHED_GOOD",
    "uom": "LITRE",
    "unit_of_measure": "LITRE",
    "pack": "LITRE",
    "cost_price": 310.0,
    "price": 350.0,
    "selling_price": 350.0,
    "wholesale_price": 335.0,
    "tax_rate": 5.0,
    "stock_qty": 95,
    "current_stock": 95,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "aa11",
    "doc_id": "aa11",
    "code": "aa11",
    "name": "Suyambu Gingelly Oil (500ml)",
    "product_name": "Suyambu Gingelly Oil (500ml)",
    "tamil_name": "சுயம்பு நல்லெண்ணெய் (500 மி.லி)",
    "brand": "Suyambu",
    "category": "FINISHED_GOOD",
    "uom": "PACKET_500ML",
    "unit_of_measure": "PACKET_500ML",
    "pack": "PACKET_500ML",
    "cost_price": 160.0,
    "price": 180.0,
    "selling_price": 180.0,
    "wholesale_price": 175.0,
    "tax_rate": 5.0,
    "stock_qty": 60,
    "current_stock": 60,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "aa12",
    "doc_id": "aa12",
    "code": "aa12",
    "name": "Suyambu Velakku Ennai (1L)",
    "product_name": "Suyambu Velakku Ennai (1L)",
    "tamil_name": "சுயம்பு விளக்கு எண்ணெய் (1 லிட்டர்)",
    "brand": "Suyambu",
    "category": "FINISHED_GOOD",
    "uom": "LITRE",
    "unit_of_measure": "LITRE",
    "pack": "LITRE",
    "cost_price": 200.0,
    "price": 240.0,
    "selling_price": 240.0,
    "wholesale_price": 225.0,
    "tax_rate": 5.0,
    "stock_qty": 50,
    "current_stock": 50,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "aa13",
    "doc_id": "aa13",
    "code": "aa13",
    "name": "Suyambu Velakku Ennai (500ml)",
    "product_name": "Suyambu Velakku Ennai (500ml)",
    "tamil_name": "சுயம்பு விளக்கு எண்ணெய் (500 மி.லி)",
    "brand": "Suyambu",
    "category": "FINISHED_GOOD",
    "uom": "PACKET_500ML",
    "unit_of_measure": "PACKET_500ML",
    "pack": "PACKET_500ML",
    "cost_price": 100.0,
    "price": 120.0,
    "selling_price": 120.0,
    "wholesale_price": 115.0,
    "tax_rate": 5.0,
    "stock_qty": 70,
    "current_stock": 70,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "cc01",
    "doc_id": "cc01",
    "code": "cc01",
    "name": "Groundnut Oil Cake / Pinac 50kg (கடலை புண்ணாக்கு)",
    "product_name": "Groundnut Oil Cake / Pinac 50kg (கடலை புண்ணாக்கு)",
    "tamil_name": "சுயம்பு கடலை புண்ணாக்கு (50 கிலோ மூட்டை)",
    "brand": "RKG Suyambu",
    "category": "BY_PRODUCT",
    "uom": "BAG_50KG",
    "unit_of_measure": "BAG_50KG",
    "pack": "BAG_50KG",
    "cost_price": 1700.0,
    "price": 2100.0,
    "selling_price": 2100.0,
    "wholesale_price": 1980.0,
    "tax_rate": 5.0,
    "stock_qty": -176,
    "current_stock": -176,
    "is_available": false,
    "status": "Not Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "cc02",
    "doc_id": "cc02",
    "code": "cc02",
    "name": "Sesame Oil Cake / Pinac 50kg (எள்ளு புண்ணாக்கு)",
    "product_name": "Sesame Oil Cake / Pinac 50kg (எள்ளு புண்ணாக்கு)",
    "tamil_name": "சுயம்பு எள்ளு புண்ணாக்கு (50 கிலோ மூட்டை)",
    "brand": "RKG Suyambu",
    "category": "BY_PRODUCT",
    "uom": "BAG_50KG",
    "unit_of_measure": "BAG_50KG",
    "pack": "BAG_50KG",
    "cost_price": 1900.0,
    "price": 2400.0,
    "selling_price": 2400.0,
    "wholesale_price": 2250.0,
    "tax_rate": 5.0,
    "stock_qty": 60,
    "current_stock": 60,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "cc03",
    "doc_id": "cc03",
    "code": "cc03",
    "name": "Suyambu Coconut Punnakku (1 kg)",
    "product_name": "Suyambu Coconut Punnakku (1 kg)",
    "tamil_name": "சுயம்பு தேங்காய் புண்ணாக்கு (1 கிலோ)",
    "brand": "Suyambu",
    "category": "BY_PRODUCT",
    "uom": "KG",
    "unit_of_measure": "KG",
    "pack": "KG",
    "cost_price": 32.0,
    "price": 40.0,
    "selling_price": 40.0,
    "wholesale_price": 38.0,
    "tax_rate": 5.0,
    "stock_qty": 400,
    "current_stock": 400,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "dd01",
    "doc_id": "dd01",
    "code": "dd01",
    "name": "RKG Special Cattle Feed Mash 50kg (மாட்டுத்தீவனம் மாவு)",
    "product_name": "RKG Special Cattle Feed Mash 50kg (மாட்டுத்தீவனம் மாவு)",
    "tamil_name": "ஆர்.கே.ஜி ஸ்பெஷல் மாட்டுத்தீவனம் மாவு (50 கிலோ பை)",
    "brand": "RKG",
    "category": "FINISHED_GOOD",
    "uom": "BAG_50KG",
    "unit_of_measure": "BAG_50KG",
    "pack": "BAG_50KG",
    "cost_price": 1150.0,
    "price": 1400.0,
    "selling_price": 1400.0,
    "wholesale_price": 1320.0,
    "tax_rate": 0.0,
    "stock_qty": 459,
    "current_stock": 459,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "dd02",
    "doc_id": "dd02",
    "code": "dd02",
    "name": "Suyambu Nayam Polished Thavudu (50 kg Bag)",
    "product_name": "Suyambu Nayam Polished Thavudu (50 kg Bag)",
    "tamil_name": "சுயம்பு நயம் தீட்டிய தவிடு (50 கிலோ)",
    "brand": "Suyambu",
    "category": "BY_PRODUCT",
    "uom": "BAG_50KG",
    "unit_of_measure": "BAG_50KG",
    "pack": "BAG_50KG",
    "cost_price": 750.0,
    "price": 880.0,
    "selling_price": 880.0,
    "wholesale_price": 840.0,
    "tax_rate": 0.0,
    "stock_qty": 90,
    "current_stock": 90,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "dd03",
    "doc_id": "dd03",
    "code": "dd03",
    "name": "Suyambu Corn Powder (50 kg Bag)",
    "product_name": "Suyambu Corn Powder (50 kg Bag)",
    "tamil_name": "சுயம்பு சோள மாவு / பொடி (50 கிலோ)",
    "brand": "Suyambu",
    "category": "FINISHED_GOOD",
    "uom": "BAG_50KG",
    "unit_of_measure": "BAG_50KG",
    "pack": "BAG_50KG",
    "cost_price": 1480.0,
    "price": 1700.0,
    "selling_price": 1700.0,
    "wholesale_price": 1620.0,
    "tax_rate": 0.0,
    "stock_qty": 50,
    "current_stock": 50,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "dd04",
    "doc_id": "dd04",
    "code": "dd04",
    "name": "Suyambu Sambar Powder (200g Packet)",
    "product_name": "Suyambu Sambar Powder (200g Packet)",
    "tamil_name": "சுயம்பு சாம்பார் பொடி (200 கிராம்)",
    "brand": "Suyambu",
    "category": "FINISHED_GOOD",
    "uom": "PACKET_200G",
    "unit_of_measure": "PACKET_200G",
    "pack": "PACKET_200G",
    "cost_price": 78.0,
    "price": 100.0,
    "selling_price": 100.0,
    "wholesale_price": 92.0,
    "tax_rate": 5.0,
    "stock_qty": 100,
    "current_stock": 100,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "dd05",
    "doc_id": "dd05",
    "code": "dd05",
    "name": "Suyambu Mutton Masala Powder (200g Packet)",
    "product_name": "Suyambu Mutton Masala Powder (200g Packet)",
    "tamil_name": "சுயம்பு மட்டன் மசாலா பொடி (200 கிராம்)",
    "brand": "Suyambu",
    "category": "FINISHED_GOOD",
    "uom": "PACKET_200G",
    "unit_of_measure": "PACKET_200G",
    "pack": "PACKET_200G",
    "cost_price": 78.0,
    "price": 100.0,
    "selling_price": 100.0,
    "wholesale_price": 92.0,
    "tax_rate": 5.0,
    "stock_qty": 100,
    "current_stock": 100,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "gg01",
    "doc_id": "gg01",
    "code": "gg01",
    "name": "RKG High Milk-Yield Feed Pellets 50kg (பால் பெருக்கும் தீவனம் உருண்டை)",
    "product_name": "RKG High Milk-Yield Feed Pellets 50kg (பால் பெருக்கும் தீவனம் உருண்டை)",
    "tamil_name": "ஆர்.கே.ஜி பால் பெருக்கும் மாட்டுத்தீவனம் உருண்டை (50 கிலோ பை)",
    "brand": "RKG",
    "category": "FINISHED_GOOD",
    "uom": "BAG_50KG",
    "unit_of_measure": "BAG_50KG",
    "pack": "BAG_50KG",
    "cost_price": 1350.0,
    "price": 1650.0,
    "selling_price": 1650.0,
    "wholesale_price": 1550.0,
    "tax_rate": 0.0,
    "stock_qty": 117,
    "current_stock": 117,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "gg02",
    "doc_id": "gg02",
    "code": "gg02",
    "name": "Cleaned & Packaged Kambu Rice 1kg (கம்பு அரிசி பாக்கெட்)",
    "product_name": "Cleaned & Packaged Kambu Rice 1kg (கம்பு அரிசி பாக்கெட்)",
    "tamil_name": "சுத்திகரிக்கப்பட்ட கம்பு அரிசி (1 கிலோ பாக்கெட்)",
    "brand": "RKG Suyambu",
    "category": "FINISHED_GOOD",
    "uom": "PACKET_1KG",
    "unit_of_measure": "PACKET_1KG",
    "pack": "PACKET_1KG",
    "cost_price": 45.0,
    "price": 65.0,
    "selling_price": 65.0,
    "wholesale_price": 55.0,
    "tax_rate": 5.0,
    "stock_qty": 1248,
    "current_stock": 1248,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "gg03",
    "doc_id": "gg03",
    "code": "gg03",
    "name": "Suyambu Cotton Seeds (50 kg Bag)",
    "product_name": "Suyambu Cotton Seeds (50 kg Bag)",
    "tamil_name": "சுயம்பு பருத்தி விதை (50 கிலோ மூட்டை)",
    "brand": "Suyambu",
    "category": "RAW_MATERIAL",
    "uom": "BAG_50KG",
    "unit_of_measure": "BAG_50KG",
    "pack": "BAG_50KG",
    "cost_price": 2200.0,
    "price": 2500.0,
    "selling_price": 2500.0,
    "wholesale_price": 2400.0,
    "tax_rate": 0.0,
    "stock_qty": 60,
    "current_stock": 60,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "gg04",
    "doc_id": "gg04",
    "code": "gg04",
    "name": "Suyambu Cotton Seeds (1 kg)",
    "product_name": "Suyambu Cotton Seeds (1 kg)",
    "tamil_name": "சுயம்பு பருத்தி விதை (1 கிலோ)",
    "brand": "Suyambu",
    "category": "RAW_MATERIAL",
    "uom": "KG",
    "unit_of_measure": "KG",
    "pack": "KG",
    "cost_price": 42.0,
    "price": 50.0,
    "selling_price": 50.0,
    "wholesale_price": 48.0,
    "tax_rate": 0.0,
    "stock_qty": 350,
    "current_stock": 350,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "gg05",
    "doc_id": "gg05",
    "code": "gg05",
    "name": "Suyambu Paruthi Seeds Processed (40 kg Bag)",
    "product_name": "Suyambu Paruthi Seeds Processed (40 kg Bag)",
    "tamil_name": "சுயம்பு பதப்படுத்திய பருத்திக் கொட்டை (40 கிலோ)",
    "brand": "Suyambu",
    "category": "FINISHED_GOOD",
    "uom": "BAG_40KG",
    "unit_of_measure": "BAG_40KG",
    "pack": "BAG_40KG",
    "cost_price": 1850.0,
    "price": 2100.0,
    "selling_price": 2100.0,
    "wholesale_price": 2000.0,
    "tax_rate": 0.0,
    "stock_qty": 40,
    "current_stock": 40,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "gg06",
    "doc_id": "gg06",
    "code": "gg06",
    "name": "Suyambu Nayam Mixed Feeds (50 kg Bag)",
    "product_name": "Suyambu Nayam Mixed Feeds (50 kg Bag)",
    "tamil_name": "சுயம்பு நயம் கலப்பு மாட்டுத்தீவனம் (50 கிலோ)",
    "brand": "Suyambu",
    "category": "FINISHED_GOOD",
    "uom": "BAG_50KG",
    "unit_of_measure": "BAG_50KG",
    "pack": "BAG_50KG",
    "cost_price": 1200.0,
    "price": 1400.0,
    "selling_price": 1400.0,
    "wholesale_price": 1350.0,
    "tax_rate": 0.0,
    "stock_qty": 85,
    "current_stock": 85,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "gg07",
    "doc_id": "gg07",
    "code": "gg07",
    "name": "Suyambu Corn (Maize Grain - 1 kg)",
    "product_name": "Suyambu Corn (Maize Grain - 1 kg)",
    "tamil_name": "சுயம்பு மக்காச்சோளம் (1 கிலோ)",
    "brand": "Suyambu",
    "category": "RAW_MATERIAL",
    "uom": "KG",
    "unit_of_measure": "KG",
    "pack": "KG",
    "cost_price": 26.0,
    "price": 32.0,
    "selling_price": 32.0,
    "wholesale_price": 30.0,
    "tax_rate": 0.0,
    "stock_qty": 800,
    "current_stock": 800,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "gg08",
    "doc_id": "gg08",
    "code": "gg08",
    "name": "Krishi Bio Pass Cattle Feed (70 kg Bag)",
    "product_name": "Krishi Bio Pass Cattle Feed (70 kg Bag)",
    "tamil_name": "கிருஷி பயோ பாஸ் மாட்டுத்தீவனம் (70 கிலோ)",
    "brand": "Krishi",
    "category": "FINISHED_GOOD",
    "uom": "BAG_70KG",
    "unit_of_measure": "BAG_70KG",
    "pack": "BAG_70KG",
    "cost_price": 1680.0,
    "price": 1900.0,
    "selling_price": 1900.0,
    "wholesale_price": 1820.0,
    "tax_rate": 0.0,
    "stock_qty": 40,
    "current_stock": 40,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "gg09",
    "doc_id": "gg09",
    "code": "gg09",
    "name": "Krishi Bio Pass Cattle Feed (50 kg Bag)",
    "product_name": "Krishi Bio Pass Cattle Feed (50 kg Bag)",
    "tamil_name": "கிருஷி பயோ பாஸ் மாட்டுத்தீவனம் (50 கிலோ)",
    "brand": "Krishi",
    "category": "FINISHED_GOOD",
    "uom": "BAG_50KG",
    "unit_of_measure": "BAG_50KG",
    "pack": "BAG_50KG",
    "cost_price": 1220.0,
    "price": 1400.0,
    "selling_price": 1400.0,
    "wholesale_price": 1340.0,
    "tax_rate": 0.0,
    "stock_qty": 65,
    "current_stock": 65,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "gg10",
    "doc_id": "gg10",
    "code": "gg10",
    "name": "Krishi Bio Pass Cattle Feed (20 kg Bag)",
    "product_name": "Krishi Bio Pass Cattle Feed (20 kg Bag)",
    "tamil_name": "கிருஷி பயோ பாஸ் மாட்டுத்தீவனம் (20 கிலோ)",
    "brand": "Krishi",
    "category": "FINISHED_GOOD",
    "uom": "BAG_20KG",
    "unit_of_measure": "BAG_20KG",
    "pack": "BAG_20KG",
    "cost_price": 520.0,
    "price": 600.0,
    "selling_price": 600.0,
    "wholesale_price": 570.0,
    "tax_rate": 0.0,
    "stock_qty": 45,
    "current_stock": 45,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "gg11",
    "doc_id": "gg11",
    "code": "gg11",
    "name": "Krishi ProBest Cattle Feed (70 kg Bag)",
    "product_name": "Krishi ProBest Cattle Feed (70 kg Bag)",
    "tamil_name": "கிருஷி ப்ரோபெஸ்ட் மாட்டுத்தீவனம் (70 கிலோ)",
    "brand": "Krishi",
    "category": "FINISHED_GOOD",
    "uom": "BAG_70KG",
    "unit_of_measure": "BAG_70KG",
    "pack": "BAG_70KG",
    "cost_price": 1880.0,
    "price": 2100.0,
    "selling_price": 2100.0,
    "wholesale_price": 2020.0,
    "tax_rate": 0.0,
    "stock_qty": 35,
    "current_stock": 35,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "gg12",
    "doc_id": "gg12",
    "code": "gg12",
    "name": "Krishi Chicken Feed (50 kg Bag)",
    "product_name": "Krishi Chicken Feed (50 kg Bag)",
    "tamil_name": "கிருஷி கோழித்தீவனம் (50 கிலோ மூட்டை)",
    "brand": "Krishi",
    "category": "FINISHED_GOOD",
    "uom": "BAG_50KG",
    "unit_of_measure": "BAG_50KG",
    "pack": "BAG_50KG",
    "cost_price": 2100.0,
    "price": 2400.0,
    "selling_price": 2400.0,
    "wholesale_price": 2300.0,
    "tax_rate": 0.0,
    "stock_qty": 30,
    "current_stock": 30,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "gg13",
    "doc_id": "gg13",
    "code": "gg13",
    "name": "Krishi Chicken Feed (1 kg)",
    "product_name": "Krishi Chicken Feed (1 kg)",
    "tamil_name": "கிருஷி கோழித்தீவனம் (1 கிலோ)",
    "brand": "Krishi",
    "category": "FINISHED_GOOD",
    "uom": "KG",
    "unit_of_measure": "KG",
    "pack": "KG",
    "cost_price": 40.0,
    "price": 50.0,
    "selling_price": 50.0,
    "wholesale_price": 48.0,
    "tax_rate": 0.0,
    "stock_qty": 250,
    "current_stock": 250,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "gg14",
    "doc_id": "gg14",
    "code": "gg14",
    "name": "Suyambu Wheat (1 kg)",
    "product_name": "Suyambu Wheat (1 kg)",
    "tamil_name": "சுயம்பு சம்பா கோதுமை (1 கிலோ)",
    "brand": "Suyambu",
    "category": "RAW_MATERIAL",
    "uom": "KG",
    "unit_of_measure": "KG",
    "pack": "KG",
    "cost_price": 58.0,
    "price": 70.0,
    "selling_price": 70.0,
    "wholesale_price": 66.0,
    "tax_rate": 0.0,
    "stock_qty": 300,
    "current_stock": 300,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "gg15",
    "doc_id": "gg15",
    "code": "gg15",
    "name": "Suyambu Ragi / Finger Millet (1 kg)",
    "product_name": "Suyambu Ragi / Finger Millet (1 kg)",
    "tamil_name": "சுயம்பு கேழ்வரகு / ராகி (1 கிலோ)",
    "brand": "Suyambu",
    "category": "RAW_MATERIAL",
    "uom": "KG",
    "unit_of_measure": "KG",
    "pack": "KG",
    "cost_price": 58.0,
    "price": 70.0,
    "selling_price": 70.0,
    "wholesale_price": 66.0,
    "tax_rate": 0.0,
    "stock_qty": 280,
    "current_stock": 280,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "gg16",
    "doc_id": "gg16",
    "code": "gg16",
    "name": "Suyambu Groundnut / Peanut (1 kg)",
    "product_name": "Suyambu Groundnut / Peanut (1 kg)",
    "tamil_name": "சுயம்பு வேர்க்கடலை / நிலக்கடலை (1 கிலோ)",
    "brand": "Suyambu",
    "category": "RAW_MATERIAL",
    "uom": "KG",
    "unit_of_measure": "KG",
    "pack": "KG",
    "cost_price": 125.0,
    "price": 150.0,
    "selling_price": 150.0,
    "wholesale_price": 142.0,
    "tax_rate": 0.0,
    "stock_qty": 220,
    "current_stock": 220,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "gg17",
    "doc_id": "gg17",
    "code": "gg17",
    "name": "Veeran Saapadu Rice (26 kg Bag)",
    "product_name": "Veeran Saapadu Rice (26 kg Bag)",
    "tamil_name": "வீரன் சாப்பாடு அரிசி (26 கிலோ மூட்டை)",
    "brand": "Veeran",
    "category": "FINISHED_GOOD",
    "uom": "BAG_26KG",
    "unit_of_measure": "BAG_26KG",
    "pack": "BAG_26KG",
    "cost_price": 1500.0,
    "price": 1700.0,
    "selling_price": 1700.0,
    "wholesale_price": 1620.0,
    "tax_rate": 0.0,
    "stock_qty": 40,
    "current_stock": 40,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "gg18",
    "doc_id": "gg18",
    "code": "gg18",
    "name": "Veeran Saapadu Rice (10 kg Bag)",
    "product_name": "Veeran Saapadu Rice (10 kg Bag)",
    "tamil_name": "வீரன் சாப்பாடு அரிசி (10 கிலோ)",
    "brand": "Veeran",
    "category": "FINISHED_GOOD",
    "uom": "BAG_10KG",
    "unit_of_measure": "BAG_10KG",
    "pack": "BAG_10KG",
    "cost_price": 600.0,
    "price": 700.0,
    "selling_price": 700.0,
    "wholesale_price": 660.0,
    "tax_rate": 0.0,
    "stock_qty": 50,
    "current_stock": 50,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "gg19",
    "doc_id": "gg19",
    "code": "gg19",
    "name": "Veeran Saapadu Rice (5 kg Bag)",
    "product_name": "Veeran Saapadu Rice (5 kg Bag)",
    "tamil_name": "வீரன் சாப்பாடு அரிசி (5 கிலோ)",
    "brand": "Veeran",
    "category": "FINISHED_GOOD",
    "uom": "BAG_5KG",
    "unit_of_measure": "BAG_5KG",
    "pack": "BAG_5KG",
    "cost_price": 320.0,
    "price": 370.0,
    "selling_price": 370.0,
    "wholesale_price": 350.0,
    "tax_rate": 0.0,
    "stock_qty": 60,
    "current_stock": 60,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "gg20",
    "doc_id": "gg20",
    "code": "gg20",
    "name": "A1 SSS Black: Kollam Ponni Rice (26 kg Bag)",
    "product_name": "A1 SSS Black: Kollam Ponni Rice (26 kg Bag)",
    "tamil_name": "A1 SSS பிளாக் கொள்ளம் பொன்னி அரிசி (26 கிலோ)",
    "brand": "A1 SSS",
    "category": "FINISHED_GOOD",
    "uom": "BAG_26KG",
    "unit_of_measure": "BAG_26KG",
    "pack": "BAG_26KG",
    "cost_price": 2200.0,
    "price": 2500.0,
    "selling_price": 2500.0,
    "wholesale_price": 2400.0,
    "tax_rate": 0.0,
    "stock_qty": 35,
    "current_stock": 35,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "gg21",
    "doc_id": "gg21",
    "code": "gg21",
    "name": "A1 SSS Black: Kollam Ponni Rice (10 kg Bag)",
    "product_name": "A1 SSS Black: Kollam Ponni Rice (10 kg Bag)",
    "tamil_name": "A1 SSS பிளாக் கொள்ளம் பொன்னி அரிசி (10 கிலோ)",
    "brand": "A1 SSS",
    "category": "FINISHED_GOOD",
    "uom": "BAG_10KG",
    "unit_of_measure": "BAG_10KG",
    "pack": "BAG_10KG",
    "cost_price": 780.0,
    "price": 900.0,
    "selling_price": 900.0,
    "wholesale_price": 850.0,
    "tax_rate": 0.0,
    "stock_qty": 45,
    "current_stock": 45,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "gg22",
    "doc_id": "gg22",
    "code": "gg22",
    "name": "A1 SSS Black: Kollam Ponni Rice (5 kg Bag)",
    "product_name": "A1 SSS Black: Kollam Ponni Rice (5 kg Bag)",
    "tamil_name": "A1 SSS பிளாக் கொள்ளம் பொன்னி அரிசி (5 கிலோ)",
    "brand": "A1 SSS",
    "category": "FINISHED_GOOD",
    "uom": "BAG_5KG",
    "unit_of_measure": "BAG_5KG",
    "pack": "BAG_5KG",
    "cost_price": 430.0,
    "price": 500.0,
    "selling_price": 500.0,
    "wholesale_price": 470.0,
    "tax_rate": 0.0,
    "stock_qty": 55,
    "current_stock": 55,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "gg23",
    "doc_id": "gg23",
    "code": "gg23",
    "name": "Veera Shivaji IR 20 Rice (26 kg Bag)",
    "product_name": "Veera Shivaji IR 20 Rice (26 kg Bag)",
    "tamil_name": "வீர சிவாஜி IR 20 அரிசி (26 கிலோ மூட்டை)",
    "brand": "Veera Shivaji",
    "category": "FINISHED_GOOD",
    "uom": "BAG_26KG",
    "unit_of_measure": "BAG_26KG",
    "pack": "BAG_26KG",
    "cost_price": 1120.0,
    "price": 1300.0,
    "selling_price": 1300.0,
    "wholesale_price": 1240.0,
    "tax_rate": 0.0,
    "stock_qty": 40,
    "current_stock": 40,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "gg24",
    "doc_id": "gg24",
    "code": "gg24",
    "name": "Veera Shivaji IR 20 Rice (10 kg Bag)",
    "product_name": "Veera Shivaji IR 20 Rice (10 kg Bag)",
    "tamil_name": "வீர சிவாஜி IR 20 அரிசி (10 கிலோ)",
    "brand": "Veera Shivaji",
    "category": "FINISHED_GOOD",
    "uom": "BAG_10KG",
    "unit_of_measure": "BAG_10KG",
    "pack": "BAG_10KG",
    "cost_price": 470.0,
    "price": 550.0,
    "selling_price": 550.0,
    "wholesale_price": 520.0,
    "tax_rate": 0.0,
    "stock_qty": 50,
    "current_stock": 50,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  },
  {
    "id": "gg25",
    "doc_id": "gg25",
    "code": "gg25",
    "name": "Veera Shivaji IR 20 Rice (5 kg Bag)",
    "product_name": "Veera Shivaji IR 20 Rice (5 kg Bag)",
    "tamil_name": "வீர சிவாஜி IR 20 அரிசி (5 கிலோ)",
    "brand": "Veera Shivaji",
    "category": "FINISHED_GOOD",
    "uom": "BAG_5KG",
    "unit_of_measure": "BAG_5KG",
    "pack": "BAG_5KG",
    "cost_price": 255.0,
    "price": 300.0,
    "selling_price": 300.0,
    "wholesale_price": 285.0,
    "tax_rate": 0.0,
    "stock_qty": 55,
    "current_stock": 55,
    "is_available": true,
    "status": "Available",
    "min_stock_alert": 10,
    "is_active": true
  }
];
const SEED_COMPANY = {
  "company_name": "RKG SUYAMBU CATTLE FEED & AGRO PRODUCTS",
  "tamil_name": "ஆர்.கே.ஜி சுயம்பு மாட்டுத்தீவனம் & ஆலை",
  "phone": "+91 94425 76622",
  "whatsapp": "+91 94425 76622",
  "email": "contact@rkgsuyambu.com",
  "address": "SF No. 142/2, Main Road, Erode-Tirupur Agri Corridor, Tamil Nadu 638056",
  "gstin": "33AAAAA9999Z1Z8",
  "fssai": "12424005000123",
  "dispatch_hours": "6:30 AM - 8:30 PM",
  "working_days": "Monday - Saturday"
};
const SEED_HISTORY = [
  {
    "timestamp": "2026-08-31T11:45:00Z",
    "changed_by": "CEO",
    "table_name": "products",
    "doc_id": "aa01",
    "item_name": "Pure Cold-Pressed Groundnut Oil 1L (மரச்செக்கு கடலை எண்ணெய்)",
    "field_changed": "Stock Quantity (இருப்பு)",
    "old_value": "0 units",
    "new_value": "15 units (Δ +15)",
    "remarks": "CEO manual stock replenishment (+15 units added to inventory)",
    "sync_status": "🟢 Synced to Firebase"
  },
  {
    "timestamp": "2026-08-31T10:15:00Z",
    "changed_by": "CEO",
    "table_name": "products",
    "doc_id": "gg23",
    "item_name": "Krishi Cattle Feed Mash 70kg (மாட்டுத்தீவனம்)",
    "field_changed": "Wholesale Price (மொத்த விலை)",
    "old_value": "₹1300 / bag",
    "new_value": "₹1350 / bag (Δ +₹50)",
    "remarks": "Factory tier wholesale rate update for dairy co-operatives",
    "sync_status": "🟢 Synced to Firebase"
  },
  {
    "timestamp": "2026-08-31T09:30:00Z",
    "changed_by": "CEO",
    "table_name": "client_order_data",
    "doc_id": "ORD-REQ-2026-0831-001",
    "item_name": "Order ORD-REQ-2026-0831-001 (S. Murugesan Dairy Farm)",
    "field_changed": "Payment Status (பணம் செலுத்திய நிலை)",
    "old_value": "Pending Verification",
    "new_value": "Paid (UPI Verified)",
    "remarks": "UPI Txn ID: UPI/424908129034/GPay confirmed by CEO",
    "sync_status": "🟢 Synced to Firebase"
  }
];
const SEED_ORDERS = [
  {
    "request_id": "ORD-REQ-2026-0831-001",
    "client_name": "S. Murugesan Dairy Farm",
    "client_mobile": "+91 94425 76622",
    "mode_of_order": "Website Inquiry Cart",
    "items_ordered": "aa01: 5, gg23: 20",
    "product_codes": "aa01, gg23",
    "total_quantity": 25,
    "total_amount": 28350.0,
    "mode_of_payment": "UPI",
    "upi_transaction_id": "UPI/424908129034/GPay",
    "payment_status": "Paid",
    "ip_serial_number": "IP-SER-10492",
    "order_status": "Processing",
    "timestamp": "2026-08-31T11:30:00Z"
  },
  {
    "request_id": "ORD-REQ-2026-0831-002",
    "client_name": "K. Sengottaiyan Agro Wholesales",
    "client_mobile": "+91 98421 33455",
    "mode_of_order": "WhatsApp Direct",
    "items_ordered": "aa03: 10, bb06: 15, gg23: 50",
    "product_codes": "aa03, bb06, gg23",
    "total_quantity": 75,
    "total_amount": 82500.0,
    "mode_of_payment": "UPI",
    "upi_transaction_id": "UPI/981245009122/PhonePe",
    "payment_status": "Full",
    "ip_serial_number": "IP-SER-10495",
    "order_status": "Dispatched",
    "timestamp": "2026-08-31T10:45:00Z"
  },
  {
    "request_id": "ORD-REQ-2026-0831-003",
    "client_name": "V. Ramasamy Organic Store",
    "client_mobile": "+91 97880 11223",
    "mode_of_order": "Mobile App Field Order",
    "items_ordered": "aa02: 12, cc11: 5",
    "product_codes": "aa02, cc11",
    "total_quantity": 17,
    "total_amount": 12840.0,
    "mode_of_payment": "Cash on Delivery",
    "upi_transaction_id": "N/A",
    "payment_status": "Cancelled",
    "ip_serial_number": "IP-SER-10501",
    "order_status": "Cancelled",
    "timestamp": "2026-08-31T09:15:00Z"
  },
  {
    "request_id": "ORD-REQ-2026-0831-004",
    "client_name": "P. Palanisamy Cattle Feeds",
    "client_mobile": "+91 96550 44991",
    "mode_of_order": "Direct Mill Order",
    "items_ordered": "gg23: 100",
    "product_codes": "gg23",
    "total_quantity": 100,
    "total_amount": 135000.0,
    "mode_of_payment": "UPI",
    "upi_transaction_id": "UPI/771239019283/Paytm",
    "payment_status": "Paid",
    "ip_serial_number": "IP-SER-10508",
    "order_status": "Delivered",
    "timestamp": "2026-08-31T08:00:00Z"
  }
];
const SEED_SALES = [
  {
    "id": "RKG-INV-2026-0831-01",
    "sale_type": "INVOICE",
    "invoice_number": "RKG/26-27/0003",
    "date": "2026-08-31",
    "customer_name": "S. Murugesan Dairy Farm",
    "customer_phone": "+91 94425 76622",
    "billing_type": "WHOLESALE_B2B",
    "items_summary": "Pure Cold-Pressed Groundnut Oil 1L × 5, Krishi Cattle Feed 70kg × 20",
    "items_count": 25,
    "grand_total": 28350.0,
    "paid_amount": 28350.0,
    "balance_due": 0.0,
    "payment_mode": "UPI (GPay)",
    "timestamp": "2026-08-31T11:30:00Z"
  },
  {
    "id": "RKG-INV-2026-0831-02",
    "sale_type": "INVOICE",
    "invoice_number": "RKG/26-27/0004",
    "date": "2026-08-31",
    "customer_name": "K. Sengottaiyan Agro Wholesales",
    "customer_phone": "+91 98421 33455",
    "billing_type": "WHOLESALE_B2B",
    "items_summary": "Suyambu Coconut Oil 5L × 10, Gingelly Oil 5L × 15, Krishi Feed 70kg × 50",
    "items_count": 75,
    "grand_total": 82500.0,
    "paid_amount": 82500.0,
    "balance_due": 0.0,
    "payment_mode": "UPI (PhonePe)",
    "timestamp": "2026-08-31T10:45:00Z"
  },
  {
    "id": "RKG-INV-2026-0831-03",
    "sale_type": "RETAIL",
    "invoice_number": "RKG/26-27/0005",
    "date": "2026-08-31",
    "customer_name": "M. Kumar (Walk-in Farmer)",
    "customer_phone": "+91 99440 22331",
    "billing_type": "RETAIL_B2C",
    "items_summary": "Cold-Pressed Groundnut Oil 1L × 2, Sesame Oil 1L × 1",
    "items_count": 3,
    "grand_total": 960.0,
    "paid_amount": 960.0,
    "balance_due": 0.0,
    "payment_mode": "CASH",
    "timestamp": "2026-08-31T09:15:00Z"
  },
  {
    "id": "RKG-INV-2026-0830-01",
    "sale_type": "INVOICE",
    "invoice_number": "RKG/26-27/0002",
    "date": "2026-08-30",
    "customer_name": "Ramasamy Gounder (Farmer)",
    "customer_phone": "+91 98423 44556",
    "billing_type": "RETAIL_B2C",
    "items_summary": "Suyambu Nayam Cattle Feed 50kg × 3, Coconut Oil 1L × 2",
    "items_count": 5,
    "grand_total": 3867.0,
    "paid_amount": 3290.0,
    "balance_due": 577.0,
    "payment_mode": "CASH",
    "timestamp": "2026-08-30T15:20:00Z"
  },
  {
    "id": "RKG-INV-2026-0829-01",
    "sale_type": "INVOICE",
    "invoice_number": "RKG/26-27/0001",
    "date": "2026-08-29",
    "customer_name": "Murugan Dairy Farm",
    "customer_phone": "+91 94422 77889",
    "billing_type": "WHOLESALE_B2B",
    "items_summary": "Krishi Cattle Feed Mash 70kg × 20 bags",
    "items_count": 20,
    "grand_total": 23595.0,
    "paid_amount": 10000.0,
    "balance_due": 13595.0,
    "payment_mode": "UPI",
    "timestamp": "2026-08-29T11:00:00Z"
  }
];
const SEED_PROMOS = [
  {
    "promo_code_number": "RKG-PRM-2026-A01",
    "promo_name": "Agro Harvest Special",
    "qr_code_number": "QR-RKG-9842-A01",
    "start_date": "2026-08-01",
    "end_date": "2026-12-31",
    "validity_percentage": 10.0,
    "max_discount_amount": 1000.0,
    "min_order_value": 1500.0,
    "number_of_purchase_credit": 100,
    "used_purchase_credits": 14,
    "is_active": true
  },
  {
    "promo_code_number": "RKG-PRM-2026-B02",
    "promo_name": "Dairy Farmers Cattle Feed Bulk Promo",
    "qr_code_number": "QR-RKG-9842-B02",
    "start_date": "2026-08-15",
    "end_date": "2026-11-30",
    "validity_percentage": 5.0,
    "max_discount_amount": 2500.0,
    "min_order_value": 5000.0,
    "number_of_purchase_credit": 50,
    "used_purchase_credits": 8,
    "is_active": true
  },
  {
    "promo_code_number": "RKG-PRM-2026-C03",
    "promo_name": "Pure Cold-Pressed Oil Festival Offer",
    "qr_code_number": "QR-RKG-9842-C03",
    "start_date": "2026-08-25",
    "end_date": "2026-10-15",
    "validity_percentage": 8.0,
    "max_discount_amount": 800.0,
    "min_order_value": 1000.0,
    "number_of_purchase_credit": 200,
    "used_purchase_credits": 26,
    "is_active": true
  },
  {
    "promo_code_number": "RKG-PRM-2026-D04",
    "promo_name": "New Customer Welcome Voucher",
    "qr_code_number": "QR-RKG-9842-D04",
    "start_date": "2026-08-01",
    "end_date": "2026-12-31",
    "validity_percentage": 12.0,
    "max_discount_amount": 500.0,
    "min_order_value": 800.0,
    "number_of_purchase_credit": 150,
    "used_purchase_credits": 45,
    "is_active": true
  }
];
const SEED_PROFITS = [
  {
    "promo_code_number": "RKG-PRM-2026-A01",
    "promo_name": "Agro Harvest Special",
    "number_of_bills_generated": 14,
    "number_of_purchase_credit": 14,
    "total_sales_amount": 48500.0,
    "total_discount_given": 4850.0,
    "net_revenue": 43650.0,
    "estimated_gross_profit": 11200.0,
    "last_invoice_number": "RKG/26-27/0003"
  },
  {
    "promo_code_number": "RKG-PRM-2026-B02",
    "promo_name": "Dairy Farmers Cattle Feed Bulk Promo",
    "number_of_bills_generated": 8,
    "number_of_purchase_credit": 8,
    "total_sales_amount": 142000.0,
    "total_discount_given": 7100.0,
    "net_revenue": 134900.0,
    "estimated_gross_profit": 28400.0,
    "last_invoice_number": "RKG/26-27/0004"
  },
  {
    "promo_code_number": "RKG-PRM-2026-C03",
    "promo_name": "Pure Cold-Pressed Oil Festival Offer",
    "number_of_bills_generated": 26,
    "number_of_purchase_credit": 26,
    "total_sales_amount": 34800.0,
    "total_discount_given": 2784.0,
    "net_revenue": 32016.0,
    "estimated_gross_profit": 9850.0,
    "last_invoice_number": "RKG/26-27/0005"
  },
  {
    "promo_code_number": "RKG-PRM-2026-D04",
    "promo_name": "New Customer Welcome Voucher",
    "number_of_bills_generated": 45,
    "number_of_purchase_credit": 45,
    "total_sales_amount": 42000.0,
    "total_discount_given": 5040.0,
    "net_revenue": 36960.0,
    "estimated_gross_profit": 10500.0,
    "last_invoice_number": "RKG/26-27/0002"
  }
];

const API_BASE = (window.location.protocol === 'file:' || window.location.origin === 'null')
  ? (localStorage.getItem('rkg_api_base') || 'http://192.168.1.5:8000')
  : '';

let currentUserSession = JSON.parse(sessionStorage.getItem("rkg_user_session") || "null");
let isUserAuthenticated = !!currentUserSession;
let activeTab = 'sales'; // Default Landing Tab: Today's Sales
let selectedSalesDate = new Date().toISOString().slice(0, 10);
let selectedLoginUserRole = 'CEO';

let productsData = JSON.parse(localStorage.getItem("rkg_local_products") || "null") || SEED_PRODUCTS;
let ordersData = JSON.parse(localStorage.getItem("rkg_local_orders") || "null") || SEED_ORDERS;
let salesData = JSON.parse(localStorage.getItem("rkg_local_sales") || "null") || SEED_SALES;
let promoData = JSON.parse(localStorage.getItem("rkg_local_promos") || "null") || SEED_PROMOS;
let profitData = JSON.parse(localStorage.getItem("rkg_local_profits") || "null") || SEED_PROFITS;
let changeHistoryData = JSON.parse(localStorage.getItem("rkg_local_history") || "null") || SEED_HISTORY;
let companyData = JSON.parse(localStorage.getItem("rkg_local_company") || "null") || SEED_COMPANY;
let pendingSyncQueue = JSON.parse(localStorage.getItem("rkg_sync_queue") || "[]");
let selectedProductForPriceEdit = null;


// ── Initialization & Network Monitor ─────────────────────────────────
document.addEventListener("DOMContentLoaded", () => {
  if (window.lucide) lucide.createIcons();
  
  // Set default calendar date
  const picker = document.getElementById("sales-date-picker");
  if (picker) picker.value = selectedSalesDate;

  // Set default date for promo generator
  const pStart = document.getElementById("new-promo-start");
  const pEnd = document.getElementById("new-promo-end");
  if (pStart) pStart.value = new Date().toISOString().slice(0, 10);
  if (pEnd) {
    const endDt = new Date();
    endDt.setMonth(endDt.getMonth() + 3);
    pEnd.value = endDt.toISOString().slice(0, 10);
  }

  // Network listeners
  window.addEventListener("online", handleNetworkOnline);
  window.addEventListener("offline", handleNetworkOffline);
  updateNetworkStatusBadge();

  setInterval(flushPendingSyncQueue, 8000);
  checkAuthStatus();
});

function handleNetworkOnline() {
  updateNetworkStatusBadge();
  showToast("🌐 Network active. Synchronizing with Firebase Cloud...");
  flushPendingSyncQueue();
}

function handleNetworkOffline() {
  updateNetworkStatusBadge();
  showToast("📡 Offline mode active. All operations cached locally.");
}

function updateNetworkStatusBadge() {
  const badge = document.getElementById("net-status-badge");
  const text = document.getElementById("net-status-text");
  if (!badge || !text) return;

  const queueCount = pendingSyncQueue.length;
  if (!navigator.onLine) {
    badge.className = "px-2 py-0.5 rounded text-[9px] font-mono font-bold bg-amber-950 text-amber-300 border border-amber-600 flex items-center gap-1";
    text.textContent = queueCount > 0 ? `Offline (${queueCount} Queued)` : `Offline Standalone`;
  } else {
    badge.className = "px-2 py-0.5 rounded text-[9px] font-mono font-bold bg-emerald-950 text-emerald-300 border border-emerald-700 flex items-center gap-1";
    text.textContent = queueCount > 0 ? `Syncing (${queueCount})` : `Cloud Sync`;
  }
}

// ── User_Login Security Gateway ──────────────────────────────────────
function selectLoginUser(role) {
  selectedLoginUserRole = role;
  const inputUserId = document.getElementById("login-user-id");
  const btnCeo = document.getElementById("btn-user-ceo");
  const btnCashier = document.getElementById("btn-user-cashier");
  const pinInput = document.getElementById("login-pin-input");

  if (inputUserId) inputUserId.value = role;

  if (role === 'CEO') {
    if (btnCeo) btnCeo.className = "py-2.5 rounded-xl font-bold text-xs bg-amber-500 text-slate-950 flex items-center justify-center gap-1.5 transition cursor-pointer";
    if (btnCashier) btnCashier.className = "py-2.5 rounded-xl font-bold text-xs bg-slate-800 text-slate-300 hover:bg-slate-700 flex items-center justify-center gap-1.5 transition cursor-pointer";
    if (pinInput) pinInput.placeholder = "Enter CEO Password (e.g. 1234)";
  } else {
    if (btnCashier) btnCashier.className = "py-2.5 rounded-xl font-bold text-xs bg-emerald-500 text-slate-950 flex items-center justify-center gap-1.5 transition cursor-pointer";
    if (btnCeo) btnCeo.className = "py-2.5 rounded-xl font-bold text-xs bg-slate-800 text-slate-300 hover:bg-slate-700 flex items-center justify-center gap-1.5 transition cursor-pointer";
    if (pinInput) pinInput.placeholder = "Enter Cashier Password (e.g. 5678)";
  }
}

function checkAuthStatus() {
  const loginScreen = document.getElementById("ceo-login-screen");
  const appShell = document.getElementById("authenticated-app-shell");
  
  if (isUserAuthenticated && currentUserSession) {
    if (loginScreen) loginScreen.classList.add("hidden");
    if (appShell) appShell.classList.remove("hidden");
    updateUserSessionUI();
    if (window.lucide) lucide.createIcons();
    switchTab('sales');
    loadAllAppData();
  } else {
    if (loginScreen) loginScreen.classList.remove("hidden");
    if (appShell) appShell.classList.add("hidden");
  }
}

function updateUserSessionUI() {
  const roleBadge = document.getElementById("user-role-badge");
  const userLabel = document.getElementById("header-user-label");
  if (!currentUserSession) return;

  const role = currentUserSession.role || "CEO";
  if (roleBadge) {
    roleBadge.textContent = role === 'CEO' ? '👑 CEO' : '💳 Cashier';
    roleBadge.className = role === 'CEO' 
      ? 'px-2 py-0.5 rounded text-[9px] font-mono font-bold bg-amber-950 text-amber-300 border border-amber-500/40'
      : 'px-2 py-0.5 rounded text-[9px] font-mono font-bold bg-emerald-950 text-emerald-300 border border-emerald-500/40';
  }

  if (userLabel) {
    userLabel.textContent = role === 'CEO' ? 'CEO தலைமை நிர்வாகம்' : 'Cashier காசாளர் மேலாண்மை';
  }
}

async function handleUnifiedUserLogin() {
  const userId = document.getElementById("login-user-id")?.value || selectedLoginUserRole || "CEO";
  const pinInput = document.getElementById("login-pin-input");
  const errEl = document.getElementById("login-error-msg");
  const password = (pinInput?.value || "").trim();

  if (!password) {
    if (errEl) {
      errEl.textContent = "Please enter password (கடவுச்சொல் தேவை)";
      errEl.classList.remove("hidden");
    }
    return;
  }

  // 1. Instant offline fallback credentials
  const cleanPass = password.toLowerCase();
  let localAuthSuccess = false;
  let userSessionData = null;

  if (userId.toUpperCase() === 'CEO' && ["1234", "94425", "admin", "ceo", "0000", "rkg123"].includes(cleanPass)) {
    localAuthSuccess = true;
    userSessionData = { user_id: "CEO", username: "CEO Master Admin", role: "CEO", permissions: ["ALL"] };
  } else if (userId.toUpperCase() === 'CASHIER' && ["5678", "cashier", "1234", "0000"].includes(cleanPass)) {
    localAuthSuccess = true;
    userSessionData = { user_id: "Cashier", username: "Counter Cashier", role: "CASHIER", permissions: ["BILLING", "SALES_VIEW", "PROMO_VERIFY"] };
  }

  // 2. Cloud User_Login Table Verification
  try {
    const res = await fetch(`${API_BASE}/api/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ user_id: userId, password: password })
    });

    if (res.ok) {
      const data = await res.json();
      userSessionData = data;
      localAuthSuccess = true;
    }
  } catch (err) {}

  if (localAuthSuccess && userSessionData) {
    currentUserSession = userSessionData;
    isUserAuthenticated = true;
    sessionStorage.setItem("rkg_user_session", JSON.stringify(userSessionData));
    sessionStorage.setItem("rkg_ceo_session", "true");

    if (errEl) errEl.classList.add("hidden");
    const loginScreen = document.getElementById("ceo-login-screen");
    const appShell = document.getElementById("authenticated-app-shell");
    if (loginScreen) loginScreen.classList.add("hidden");
    if (appShell) appShell.classList.remove("hidden");
    updateUserSessionUI();
    if (window.lucide) lucide.createIcons();

    showToast(`✅ Welcome ${userSessionData.username || userId}! Access Granted.`);
    switchTab('sales');
    loadAllAppData();
  } else {
    if (errEl) {
      errEl.textContent = `Invalid password for user '${userId}'. Try Master PIN (1234 / 5678)`;
      errEl.classList.remove("hidden");
    }
  }
}

// ── Tab Navigation ───────────────────────────────────────────────────
function switchTab(tabId) {
  activeTab = tabId;
  const tabs = ['sales', 'inventory', 'prices', 'orders', 'company'];
  
  closeCompanyHistorySubPage(false);
  closePromoProfitSubPage(false);

  tabs.forEach(t => {
    const pane = document.getElementById(`pane-${t}`);
    const navBtn = document.getElementById(`nav-${t}`);
    
    if (t === tabId) {
      if (pane) pane.classList.remove("hidden");
      if (navBtn) {
        navBtn.classList.add("text-amber-400");
        navBtn.classList.remove("text-slate-400");
      }
    } else {
      if (pane) pane.classList.add("hidden");
      if (navBtn) {
        navBtn.classList.remove("text-amber-400");
        navBtn.classList.add("text-slate-400");
      }
    }
  });

  if (window.lucide) lucide.createIcons();

  if (tabId === 'sales') loadSales(selectedSalesDate);
  else if (tabId === 'inventory') loadInventory();
  else if (tabId === 'prices') loadPriceCatalog();
  else if (tabId === 'orders') loadOrders();
  else if (tabId === 'company') loadCompanyData();
}

function refreshActiveTab() {
  if (activeTab === 'sales') loadSales(selectedSalesDate);
  else if (activeTab === 'inventory') loadInventory();
  else if (activeTab === 'prices') loadPriceCatalog();
  else if (activeTab === 'orders') loadOrders();
  else if (activeTab === 'company') {
    loadCompanyData();
    loadPromoCodes();
    loadProfitAnalytics();
  }
  flushPendingSyncQueue();
  showToast("🔄 Refreshed with latest Firebase Cloud database");
}

function loadAllAppData() {
  loadSales(selectedSalesDate);
  loadInventory();
  loadPriceCatalog();
  loadOrders();
  loadCompanyData();
  loadPromoCodes();
  loadProfitAnalytics();
  loadChangeHistory();
  flushPendingSyncQueue();
}

// ── TAB 1: TODAY'S SALES & CALENDAR FILTER ────────────────────────────
async function loadSales(dateStr) {
  selectedSalesDate = dateStr || new Date().toISOString().slice(0, 10);
  
  const dateLabel = document.getElementById("sales-date-label");
  const todayStr = new Date().toISOString().slice(0, 10);
  if (dateLabel) {
    if (selectedSalesDate === "all") dateLabel.textContent = "Showing sales for: All Dates";
    else if (selectedSalesDate === todayStr) dateLabel.textContent = `Showing sales for: Today (${selectedSalesDate})`;
    else dateLabel.textContent = `Showing sales for: ${selectedSalesDate}`;
  }

  renderSalesView(salesData, selectedSalesDate);

  try {
    const res = await fetch(`${API_BASE}/api/ceo/sales?date=${selectedSalesDate}&t=` + Date.now());
    if (res.ok) {
      const data = await res.json();
      if (data.sales) {
        salesData = data.sales.length > 0 ? data.sales : salesData;
        localStorage.setItem("rkg_local_sales", JSON.stringify(salesData));
        renderSalesView(salesData, selectedSalesDate, data.summary);
      }
    }
  } catch (err) {}
}

function handleSalesDateFilter(chosenDate) {
  if (!chosenDate) return;
  setSalesDatePresetButton(null);
  loadSales(chosenDate);
}

function setSalesDatePreset(preset) {
  const picker = document.getElementById("sales-date-picker");
  const today = new Date();
  
  if (preset === 'today') {
    selectedSalesDate = today.toISOString().slice(0, 10);
    if (picker) picker.value = selectedSalesDate;
  } else if (preset === 'yesterday') {
    const yest = new Date(today);
    yest.setDate(yest.getDate() - 1);
    selectedSalesDate = yest.toISOString().slice(0, 10);
    if (picker) picker.value = selectedSalesDate;
  } else if (preset === 'all') {
    selectedSalesDate = "all";
    if (picker) picker.value = "";
  }

  setSalesDatePresetButton(preset);
  loadSales(selectedSalesDate);
}

function setSalesDatePresetButton(preset) {
  const btnToday = document.getElementById("btn-date-today");
  const btnYest = document.getElementById("btn-date-yesterday");
  const btnAll = document.getElementById("btn-date-all");
  
  [btnToday, btnYest, btnAll].forEach(b => {
    if (b) b.className = "flex-1 py-1 rounded-lg text-[10px] font-bold bg-slate-800 hover:bg-slate-700 text-slate-300 transition";
  });

  if (preset === 'today' && btnToday) btnToday.className = "flex-1 py-1 rounded-lg text-[10px] font-bold bg-amber-500 text-slate-950 transition";
  else if (preset === 'yesterday' && btnYest) btnYest.className = "flex-1 py-1 rounded-lg text-[10px] font-bold bg-amber-500 text-slate-950 transition";
  else if (preset === 'all' && btnAll) btnAll.className = "flex-1 py-1 rounded-lg text-[10px] font-bold bg-amber-500 text-slate-950 transition";
}

function renderSalesView(items, dateFilter, customSummary) {
  const container = document.getElementById("sales-items-container");
  const badgeCount = document.getElementById("badge-sales-count");
  const revEl = document.getElementById("sales-total-revenue");
  const cntEl = document.getElementById("sales-total-count");
  const upiEl = document.getElementById("sales-upi-total");
  const cashEl = document.getElementById("sales-cash-total");

  let filtered = items;
  if (dateFilter && dateFilter !== "all") {
    filtered = items.filter(s => (s.date || "").startsWith(dateFilter) || (s.timestamp || "").startsWith(dateFilter));
  }

  let totalRev = 0, totalUPI = 0, totalCash = 0;

  if (customSummary) {
    totalRev = customSummary.total_revenue || 0;
    totalUPI = customSummary.upi_total || 0;
    totalCash = customSummary.cash_total || 0;
  } else {
    filtered.forEach(s => {
      const g = parseFloat(s.grand_total || 0);
      totalRev += g;
      if ((s.payment_mode || "").toLowerCase().includes("upi")) totalUPI += g;
      else totalCash += g;
    });
  }

  if (revEl) revEl.textContent = `₹${totalRev.toLocaleString('en-IN')}`;
  if (cntEl) cntEl.textContent = `${filtered.length} Sales transactions`;
  if (upiEl) upiEl.textContent = `₹${totalUPI.toLocaleString('en-IN')}`;
  if (cashEl) cashEl.textContent = `₹${totalCash.toLocaleString('en-IN')}`;
  if (badgeCount) badgeCount.textContent = `${filtered.length} Invoices`;

  if (!container) return;

  if (filtered.length === 0) {
    container.innerHTML = `<div class="text-center py-8 text-slate-500 text-xs">No sales records found for selected date (${dateFilter}).</div>`;
    return;
  }

  container.innerHTML = filtered.map(s => {
    const invNo = s.invoice_number || s.id || "INV-N/A";
    const name = s.customer_name || "Counter Client";
    const phone = s.customer_phone || "";
    const bType = s.billing_type || "RETAIL_B2C";
    const itemsDesc = s.items_summary || "Agro Products";
    const grand = parseFloat(s.grand_total || 0).toLocaleString('en-IN');
    const payMode = s.payment_mode || "UPI";
    const ts = s.timestamp ? new Date(s.timestamp).toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit', hour12: true }) : "Time N/A";

    let payBadge = "bg-emerald-950 text-emerald-300 border-emerald-700/80";
    if (payMode.toLowerCase().includes("cash")) payBadge = "bg-amber-950 text-amber-300 border-amber-700/80";

    return `
      <div class="bg-slate-900 border border-slate-800 hover:border-emerald-700/70 rounded-2xl p-4 space-y-3 transition shadow-md">
        <div class="flex justify-between items-start">
          <div class="space-y-0.5">
            <div class="flex items-center gap-1.5">
              <span class="px-2 py-0.5 rounded font-mono font-black text-[10px] bg-emerald-950 text-emerald-300 border border-emerald-600/50">
                ${invNo}
              </span>
              <span class="px-2 py-0.5 rounded text-[9px] font-bold bg-slate-800 text-slate-300 uppercase">
                ${bType}
              </span>
            </div>
            <div class="text-xs font-bold text-white pt-1">${name}</div>
            ${phone ? `<div class="text-[10px] text-slate-400 font-mono">${phone}</div>` : ''}
          </div>

          <div class="text-right space-y-1">
            <div class="text-sm font-mono font-black text-amber-300">₹${grand}</div>
            <span class="inline-block px-2 py-0.5 rounded-full font-bold text-[9px] uppercase border ${payBadge}">
              ${payMode}
            </span>
          </div>
        </div>

        <div class="bg-slate-950/80 border border-slate-800 rounded-xl p-2.5 space-y-1">
          <div class="text-[10px] text-slate-400 font-semibold">Items Sold:</div>
          <div class="text-xs text-slate-200 leading-snug">${itemsDesc}</div>
        </div>

        <div class="flex justify-between items-center text-[10px] text-slate-400 pt-1">
          <span>Date: <span class="font-mono text-slate-300">${s.date || selectedSalesDate}</span></span>
          <span>Time: <span class="font-mono text-slate-300">${ts}</span></span>
        </div>
      </div>
    `;
  }).join('');

  if (window.lucide) lucide.createIcons();
}

// ── TAB 2: INVENTORY STOCKS ───────────────────────────────────────────
async function loadInventory() {
  renderInventoryList(productsData);
  const badge = document.getElementById("badge-inv-count");
  if (badge) badge.textContent = `${productsData.length} Items`;

  try {
    const res = await fetch(`${API_BASE}/api/ceo/inventory?t=` + Date.now());
    if (res.ok) {
      const data = await res.json();
      if (data.inventory && data.inventory.length > 0) {
        productsData = data.inventory;
        localStorage.setItem("rkg_local_products", JSON.stringify(productsData));
        renderInventoryList(productsData);
        if (badge) badge.textContent = `${productsData.length} Items`;
      }
    }
  } catch (err) {}
}

function renderInventoryList(items) {
  const container = document.getElementById("inventory-items-container");
  if (!container) return;

  if (items.length === 0) {
    container.innerHTML = `<div class="text-center py-6 text-slate-500 text-xs">No matching inventory products.</div>`;
    return;
  }

  container.innerHTML = items.map(item => {
    const isAvail = (item.stock_qty || 0) > 0;
    const docId = item.doc_id || item.code || item.id || "";
    const name = item.product_name || item.name || docId;
    const tamil = item.tamil_name || "";
    const uom = item.pack || item.uom || item.unit_of_measure || "";
    const qty = parseInt(item.stock_qty || 0);

    return `
      <div class="bg-slate-900 border border-slate-800 hover:border-emerald-700/60 rounded-2xl p-3.5 space-y-3 transition shadow-sm">
        <div class="flex justify-between items-start">
          <div class="space-y-0.5">
            <div class="flex items-center gap-2">
              <span class="px-2 py-0.5 rounded font-mono font-bold text-[10px] bg-amber-950 text-amber-300 border border-amber-500/40">
                ${docId}
              </span>
              <span class="text-xs font-bold text-white">${name}</span>
            </div>
            ${tamil ? `<div class="text-[11px] text-emerald-400 font-tamil">${tamil}</div>` : ''}
            <div class="text-[10px] text-slate-400 font-medium">Pack: ${uom}</div>
          </div>

          <div class="text-right">
            ${isAvail ? `
              <span class="inline-flex items-center gap-1 font-bold text-emerald-300 bg-emerald-950 px-2 py-0.5 rounded-md border border-emerald-700 text-[10px]">
                <span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span> In Stock
              </span>
            ` : `
              <span class="inline-flex items-center gap-1 font-bold text-rose-300 bg-rose-950 px-2 py-0.5 rounded-md border border-rose-800 text-[10px]">
                <span class="w-1.5 h-1.5 rounded-full bg-rose-500"></span> 0 / Not Available
              </span>
            `}
          </div>
        </div>

        <div class="flex items-center justify-between pt-2 border-t border-slate-800/80">
          <div class="text-xs text-slate-300 font-bold">
            Live Count: <span class="font-mono font-black text-amber-300 text-sm" id="stock-val-${docId}">${qty}</span>
          </div>

          <div class="flex items-center gap-1.5">
            <button onclick="adjustStockCount('${docId}', -5)" class="w-7 h-7 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 font-bold text-xs flex items-center justify-center cursor-pointer">-5</button>
            <button onclick="adjustStockCount('${docId}', -1)" class="w-7 h-7 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 font-bold text-xs flex items-center justify-center cursor-pointer">-1</button>
            
            <input type="number" step="1" min="0" value="${qty}" id="input-stock-${docId}" onchange="saveDirectStock('${docId}', this.value)" class="w-14 bg-slate-950 border border-slate-700 rounded-lg py-1 text-center font-mono font-bold text-xs text-amber-300 focus:outline-none focus:border-amber-400">

            <button onclick="adjustStockCount('${docId}', 1)" class="w-7 h-7 rounded-lg bg-emerald-950 border border-emerald-700 hover:bg-emerald-900 text-emerald-300 font-bold text-xs flex items-center justify-center cursor-pointer">+1</button>
            <button onclick="adjustStockCount('${docId}', 5)" class="w-7 h-7 rounded-lg bg-emerald-950 border border-emerald-700 hover:bg-emerald-900 text-emerald-300 font-bold text-xs flex items-center justify-center cursor-pointer">+5</button>
          </div>
        </div>
      </div>
    `;
  }).join('');

  if (window.lucide) lucide.createIcons();
}

function filterInventoryList() {
  const q = (document.getElementById("search-inv")?.value || "").toLowerCase().trim();
  if (!q) {
    renderInventoryList(productsData);
    return;
  }
  const filtered = productsData.filter(i => {
    return (i.doc_id || i.code || "").toLowerCase().includes(q) ||
           (i.product_name || i.name || "").toLowerCase().includes(q) ||
           (i.tamil_name || "").toLowerCase().includes(q);
  });
  renderInventoryList(filtered);
}

async function adjustStockCount(docId, delta) {
  const item = productsData.find(x => (x.doc_id || x.code || x.id) === docId);
  if (!item) return;
  const current = parseInt(item.stock_qty || 0);
  const newQty = Math.max(0, current + delta);
  await saveDirectStock(docId, newQty);
}

async function saveDirectStock(docId, newQty) {
  const cleanQty = Math.max(0, parseInt(newQty) || 0);
  const item = productsData.find(x => (x.doc_id || x.code || x.id) === docId);
  const oldQty = item ? parseInt(item.stock_qty || 0) : 0;
  const diff = cleanQty - oldQty;
  const diffStr = diff >= 0 ? `+${diff}` : `${diff}`;

  if (item) {
    item.stock_qty = cleanQty;
    item.status = cleanQty > 0 ? "Available" : "Not Available";
  }
  localStorage.setItem("rkg_local_products", JSON.stringify(productsData));
  renderInventoryList(productsData);

  const isOnline = navigator.onLine;
  const netLabel = isOnline ? "[Cloud Sync]" : "[Offline Mobile Data]";
  const detailedRemark = `${netLabel} Stock quantity changed from ${oldQty} to ${cleanQty} units (Delta ${diffStr})`;

  showToast(`✅ ${docId} Stock Updated: ${oldQty} ➔ ${cleanQty} units`);

  recordDetailedHistory(
    "products",
    docId,
    item ? `${item.name} (${docId})` : docId,
    "Stock Quantity (இருப்பு எண்ணிக்கை)",
    `${oldQty} units`,
    `${cleanQty} units (${diffStr})`,
    detailedRemark
  );

  queueAndSyncAction("inventory_update", {
    doc_id: docId,
    stock_qty: cleanQty,
    old_qty: oldQty,
    updated_by: currentUserSession?.username || "CEO"
  }, `${API_BASE}/api/ceo/inventory/update`);
}

// ── TAB 3: PRODUCT PRICE EDITOR ───────────────────────────────────────
async function loadPriceCatalog() {
  renderPriceCatalog(productsData);

  try {
    const res = await fetch(`${API_BASE}/api/public/products?t=` + Date.now());
    if (res.ok) {
      const data = await res.json();
      if (Array.isArray(data) && data.length > 0) {
        productsData = data;
        localStorage.setItem("rkg_local_products", JSON.stringify(productsData));
        renderPriceCatalog(productsData);
      }
    }
  } catch (err) {}
}

function renderPriceCatalog(items) {
  const container = document.getElementById("price-items-container");
  if (!container) return;

  if (items.length === 0) {
    container.innerHTML = `<div class="text-center py-6 text-slate-500 text-xs">No products found.</div>`;
    return;
  }

  container.innerHTML = items.map(p => {
    const docId = p.code || p.doc_id || p.id;
    return `
      <div class="bg-slate-900 border border-slate-800 hover:border-amber-500/50 rounded-2xl p-3.5 space-y-3 transition shadow-sm">
        <div class="flex justify-between items-start">
          <div>
            <div class="flex items-center gap-2">
              <span class="px-2 py-0.5 rounded font-mono font-bold text-[10px] bg-emerald-950 text-emerald-300 border border-emerald-600/40">
                ${docId}
              </span>
              <span class="text-xs font-bold text-white">${p.name}</span>
            </div>
            ${p.tamil_name ? `<div class="text-[11px] text-amber-400 font-tamil">${p.tamil_name}</div>` : ''}
            <div class="text-[10px] text-slate-400">Pack: ${p.uom || p.pack || 'Standard'} | Brand: ${p.brand || 'Suyambu'}</div>
          </div>

          <button onclick="openPriceModal('${p.id || p.doc_id}')" class="px-2.5 py-1.5 rounded-xl bg-amber-400 hover:bg-amber-300 text-slate-950 font-black text-xs transition flex items-center gap-1 cursor-pointer">
            <i data-lucide="edit-3" class="w-3 h-3"></i>
            <span>Edit Price</span>
          </button>
        </div>

        <div class="flex justify-between items-center pt-2 border-t border-slate-800 text-xs">
          <div>
            <span class="text-slate-400 text-[10px]">Retail Price:</span>
            <span class="font-mono font-black text-amber-300 ml-1">₹${p.price || p.selling_price}</span>
          </div>
          <div>
            <span class="text-slate-400 text-[10px]">Wholesale:</span>
            <span class="font-mono font-bold text-emerald-400 ml-1">₹${p.wholesale_price || (p.price * 0.95).toFixed(0)}</span>
          </div>
        </div>
      </div>
    `;
  }).join('');

  if (window.lucide) lucide.createIcons();
}

function filterPriceCatalog() {
  const q = (document.getElementById("search-price")?.value || "").toLowerCase().trim();
  if (!q) {
    renderPriceCatalog(productsData);
    return;
  }
  const filtered = productsData.filter(p => {
    return (p.code || "").toLowerCase().includes(q) ||
           (p.doc_id || "").toLowerCase().includes(q) ||
           (p.name || "").toLowerCase().includes(q) ||
           (p.tamil_name || "").toLowerCase().includes(q);
  });
  renderPriceCatalog(filtered);
}

function openPriceModal(productId) {
  const p = productsData.find(x => (x.id || x.doc_id) === productId);
  if (!p) return;

  selectedProductForPriceEdit = p;
  document.getElementById("modal-edit-code").textContent = p.code || p.doc_id;
  document.getElementById("modal-edit-name").textContent = p.name;
  document.getElementById("modal-input-price").value = p.price || p.selling_price;
  document.getElementById("modal-input-wholesale").value = p.wholesale_price || Math.round((p.price || p.selling_price) * 0.95);
  document.getElementById("modal-input-reason").value = "";

  const modal = document.getElementById("price-edit-modal");
  if (modal) modal.classList.remove("hidden");
}

function closePriceModal() {
  const modal = document.getElementById("price-edit-modal");
  if (modal) modal.classList.add("hidden");
  selectedProductForPriceEdit = null;
}

async function submitPriceUpdate() {
  if (!selectedProductForPriceEdit) return;

  const docId = selectedProductForPriceEdit.id || selectedProductForPriceEdit.doc_id;
  const newPrice = parseFloat(document.getElementById("modal-input-price")?.value || 0);
  const newWholesale = parseFloat(document.getElementById("modal-input-wholesale")?.value || newPrice * 0.95);
  const userReason = document.getElementById("modal-input-reason")?.value.trim() || "Market Rate Update";

  if (newPrice <= 0) {
    alert("Price must be greater than 0");
    return;
  }

  const oldPrice = selectedProductForPriceEdit.price || selectedProductForPriceEdit.selling_price;
  const oldWholesale = selectedProductForPriceEdit.wholesale_price || Math.round(oldPrice * 0.95);
  
  selectedProductForPriceEdit.price = newPrice;
  selectedProductForPriceEdit.selling_price = newPrice;
  selectedProductForPriceEdit.wholesale_price = newWholesale;
  localStorage.setItem("rkg_local_products", JSON.stringify(productsData));

  closePriceModal();
  renderPriceCatalog(productsData);
  showToast(`✅ Price updated for ${docId}: ₹${oldPrice} ➔ ₹${newPrice}`);

  const isOnline = navigator.onLine;
  const netLabel = isOnline ? "[Cloud Sync]" : "[Offline Mobile Data]";
  const detailedRemark = `${netLabel} Retail price changed from ₹${oldPrice} to ₹${newPrice}, Wholesale from ₹${oldWholesale} to ₹${newWholesale}. Reason: ${userReason}`;

  recordDetailedHistory(
    "products",
    docId,
    `${selectedProductForPriceEdit.name} (${docId})`,
    "Selling & Wholesale Price (விலை மாற்றம்)",
    `₹${oldPrice} (Wholesale: ₹${oldWholesale})`,
    `₹${newPrice} (Wholesale: ₹${newWholesale})`,
    detailedRemark
  );

  queueAndSyncAction("price_update", {
    doc_id: docId,
    price: newPrice,
    wholesale_price: newWholesale,
    reason: detailedRemark
  }, `${API_BASE}/api/ceo/products/update-price`);
}

// ── TAB 4: CHECK ORDERS (Client Order Data) ───────────────────────────
async function loadOrders() {
  renderOrdersList(ordersData);

  try {
    const res = await fetch(`${API_BASE}/api/ceo/orders?t=` + Date.now());
    if (res.ok) {
      const data = await res.json();
      if (Array.isArray(data) && data.length > 0) {
        ordersData = data;
        localStorage.setItem("rkg_local_orders", JSON.stringify(ordersData));
        renderOrdersList(ordersData);
      }
    }
  } catch (err) {}
}

function renderOrdersList(items) {
  const container = document.getElementById("orders-items-container");
  if (!container) return;

  if (items.length === 0) {
    container.innerHTML = `<div class="text-center py-8 text-slate-500 text-xs">No client orders recorded.</div>`;
    return;
  }

  container.innerHTML = items.map(ord => {
    const reqId = ord.request_id || "REQ-N/A";
    const ipSerial = ord.ip_serial_number || "IP-SER-UNKNOWN";
    const name = ord.client_name || "Direct Client";
    const phone = ord.client_mobile || "";
    const mode = ord.mode_of_order || "Website Cart";
    const itemsRaw = ord.items_ordered || ord.product_codes || "";
    const qty = ord.total_quantity || 1;
    const amount = ord.total_amount ? `₹${parseFloat(ord.total_amount).toLocaleString('en-IN')}` : "₹0";
    const payMode = ord.mode_of_payment || "UPI";
    const upiTxn = ord.upi_transaction_id || "N/A";
    const payStatus = ord.payment_status || "Paid";

    let payBadge = "bg-emerald-950 text-emerald-300 border-emerald-700/80";
    if (payStatus.toLowerCase() === 'full') payBadge = "bg-blue-950 text-blue-300 border-blue-700/80";
    else if (payStatus.toLowerCase() === 'cancelled') payBadge = "bg-rose-950 text-rose-300 border-rose-700/80";

    const codeChips = itemsRaw.split(',').map(seg => {
      const parts = seg.trim().split(':');
      const code = parts[0].trim();
      const count = parts[1] ? parts[1].trim() : '';
      
      const prod = productsData.find(x => (x.code || x.doc_id || x.id) === code);
      const prodName = prod ? prod.name.split('(')[0].trim() : code;

      return `
        <span class="inline-flex items-center gap-1 px-2 py-0.5 rounded-md text-[10px] font-mono font-bold bg-slate-950 border border-emerald-600/40 text-amber-300">
          <span class="text-emerald-400">${code}</span>
          ${count ? `<span class="text-white font-sans font-normal">× ${count}</span>` : ''}
          ${prod ? `<span class="text-slate-400 font-sans font-normal text-[9px]">(${prodName})</span>` : ''}
        </span>
      `;
    }).join(' ');

    return `
      <div class="bg-slate-900 border border-slate-800 hover:border-emerald-700/70 rounded-2xl p-4 space-y-3 transition shadow-md">
        <div class="flex justify-between items-start">
          <div class="space-y-0.5">
            <div class="flex items-center gap-1.5">
              <span class="px-2 py-0.5 rounded font-mono font-black text-[10px] bg-amber-950 text-amber-300 border border-amber-500/50">
                ${reqId}
              </span>
              <span class="px-2 py-0.5 rounded font-mono font-bold text-[9px] bg-purple-950 text-purple-300 border border-purple-700/50">
                ${ipSerial}
              </span>
            </div>
            <div class="text-xs font-bold text-white pt-1">${name}</div>
            <div class="text-[10px] text-slate-400 font-mono flex items-center gap-1">
              <i data-lucide="phone" class="w-3 h-3 text-amber-400"></i>
              <span>${phone}</span>
            </div>
          </div>

          <div class="text-right space-y-1">
            <span class="inline-block px-2.5 py-0.5 rounded-full font-bold text-[10px] uppercase border ${payBadge}">
              ● ${payStatus}
            </span>
            <div class="text-xs font-mono font-black text-amber-300">${amount}</div>
          </div>
        </div>

        <div class="bg-slate-950/80 border border-slate-800 rounded-xl p-2.5 space-y-2">
          <div class="flex justify-between items-center text-[10px]">
            <span class="text-slate-400 font-semibold">Mode of Order:</span>
            <span class="font-bold text-slate-200">${mode}</span>
          </div>

          <div class="space-y-1">
            <div class="text-[10px] text-slate-400 font-semibold flex justify-between">
              <span>Items Ordered (Product Code):</span>
              <span class="text-slate-300 font-mono">Qty: ${qty}</span>
            </div>
            <div class="flex flex-wrap gap-1.5 pt-0.5">
              ${codeChips}
            </div>
          </div>

          <div class="pt-2 border-t border-slate-800/80 grid grid-cols-2 gap-2 text-[10px]">
            <div>
              <span class="text-slate-400 block font-semibold">Mode of Payment:</span>
              <span class="font-bold text-emerald-400">${payMode}</span>
            </div>
            <div>
              <span class="text-slate-400 block font-semibold">UPI / Txn ID:</span>
              <span class="font-mono text-amber-300 font-bold truncate block" title="${upiTxn}">${upiTxn}</span>
            </div>
          </div>
        </div>

        <div class="flex items-center justify-between pt-1 gap-2">
          <span class="text-[10px] font-bold text-slate-400">Update Status:</span>
          <div class="flex items-center gap-1.5">
            <button onclick="updateOrderStatus('${reqId}', 'Paid')" class="px-2.5 py-1 rounded-lg text-[10px] font-bold bg-emerald-950 hover:bg-emerald-900 text-emerald-300 border border-emerald-700 transition cursor-pointer">
              Paid
            </button>
            <button onclick="updateOrderStatus('${reqId}', 'Full')" class="px-2.5 py-1 rounded-lg text-[10px] font-bold bg-blue-950 hover:bg-blue-900 text-blue-300 border border-blue-700 transition cursor-pointer">
              Full
            </button>
            <button onclick="updateOrderStatus('${reqId}', 'Cancelled')" class="px-2.5 py-1 rounded-lg text-[10px] font-bold bg-rose-950 hover:bg-rose-900 text-rose-300 border border-rose-800 transition cursor-pointer">
              Cancel
            </button>
          </div>
        </div>
      </div>
    `;
  }).join('');

  if (window.lucide) lucide.createIcons();
}

function filterOrdersList() {
  const q = (document.getElementById("search-orders")?.value || "").toLowerCase().trim();
  if (!q) {
    renderOrdersList(ordersData);
    return;
  }
  const filtered = ordersData.filter(o => {
    return (o.request_id || "").toLowerCase().includes(q) ||
           (o.client_name || "").toLowerCase().includes(q) ||
           (o.client_mobile || "").toLowerCase().includes(q) ||
           (o.product_codes || "").toLowerCase().includes(q) ||
           (o.items_ordered || "").toLowerCase().includes(q) ||
           (o.ip_serial_number || "").toLowerCase().includes(q) ||
           (o.upi_transaction_id || "").toLowerCase().includes(q);
  });
  renderOrdersList(filtered);
}

async function updateOrderStatus(requestId, newStatus) {
  const ord = ordersData.find(x => x.request_id === requestId);
  if (!ord) return;

  const oldStatus = ord.payment_status || "Pending";
  ord.payment_status = newStatus;
  ord.order_status = newStatus === 'Cancelled' ? 'Cancelled' : (newStatus === 'Full' ? 'Dispatched' : 'Processing');
  localStorage.setItem("rkg_local_orders", JSON.stringify(ordersData));

  renderOrdersList(ordersData);
  showToast(`✅ Order ${requestId} Status: ${oldStatus} ➔ ${newStatus}`);

  const isOnline = navigator.onLine;
  const netLabel = isOnline ? "[Cloud Sync]" : "[Offline Mobile Data]";
  const detailedRemark = `${netLabel} Order status updated for client ${ord.client_name} (${ord.client_mobile}). Payment mode: ${ord.mode_of_payment}, IP Serial: ${ord.ip_serial_number}`;

  recordDetailedHistory(
    "client_order_data",
    requestId,
    `Order ${requestId} (${ord.client_name})`,
    "Payment & Order Status (ஆர்டர் நிலை)",
    oldStatus,
    newStatus,
    detailedRemark
  );

  queueAndSyncAction("order_status_update", {
    request_id: requestId,
    payment_status: newStatus,
    order_status: ord.order_status,
    remarks: detailedRemark
  }, `${API_BASE}/api/ceo/orders/update-status`);
}

// ── TAB 5: COMPANY DATA ───────────────────────────────────────────────
async function loadCompanyData() {
  document.getElementById("ceo-company-name").value = companyData.company_name || "";
  document.getElementById("ceo-tamil-name").value = companyData.tamil_name || "";
  document.getElementById("ceo-gstin").value = companyData.gstin || "";
  document.getElementById("ceo-fssai").value = companyData.fssai || "";
  document.getElementById("ceo-phone").value = companyData.phone || "";
  document.getElementById("ceo-whatsapp").value = companyData.whatsapp || "";
  document.getElementById("ceo-email").value = companyData.email || "";
  document.getElementById("ceo-address").value = companyData.address || "";
  document.getElementById("ceo-hours").value = companyData.dispatch_hours || "6:30 AM - 8:30 PM";
  document.getElementById("ceo-days").value = companyData.working_days || "Monday - Saturday";

  try {
    const res = await fetch(`${API_BASE}/api/company-info?t=` + Date.now());
    if (res.ok) {
      const data = await res.json();
      companyData = { ...companyData, ...data };
      localStorage.setItem("rkg_local_company", JSON.stringify(companyData));
    }
  } catch (err) {}
}

async function handleCompanyDataSave(e) {
  e.preventDefault();
  const oldGSTIN = companyData.gstin || "";
  const oldFSSAI = companyData.fssai || "";

  const payload = {
    company_name: document.getElementById("ceo-company-name")?.value.trim(),
    tamil_name: document.getElementById("ceo-tamil-name")?.value.trim(),
    gstin: document.getElementById("ceo-gstin")?.value.trim(),
    fssai: document.getElementById("ceo-fssai")?.value.trim(),
    phone: document.getElementById("ceo-phone")?.value.trim(),
    whatsapp: document.getElementById("ceo-whatsapp")?.value.trim(),
    email: document.getElementById("ceo-email")?.value.trim(),
    address: document.getElementById("ceo-address")?.value.trim(),
    dispatch_hours: document.getElementById("ceo-hours")?.value.trim(),
    working_days: document.getElementById("ceo-days")?.value.trim(),
    updated_by: currentUserSession?.username || "CEO"
  };

  companyData = { ...companyData, ...payload };
  localStorage.setItem("rkg_local_company", JSON.stringify(companyData));
  showToast("✅ Company Statutory Data saved!");

  const isOnline = navigator.onLine;
  const netLabel = isOnline ? "[Cloud Sync]" : "[Offline Mobile Data]";
  const detailedRemark = `${netLabel} Company statutory info updated: GSTIN (${payload.gstin}), FSSAI (${payload.fssai}), Phone (${payload.phone})`;

  recordDetailedHistory(
    "data",
    "company_info",
    "Company Statutory Info (நிறுவன விவரங்கள்)",
    "Statutory Parameters",
    `GSTIN: ${oldGSTIN}, FSSAI: ${oldFSSAI}`,
    `GSTIN: ${payload.gstin}, FSSAI: ${payload.fssai}`,
    detailedRemark
  );

  queueAndSyncAction("company_data_update", payload, `${API_BASE}/api/ceo/company-data/update`);
}

// ── PROMO CODES & PROFIT SUB-PAGE CONTROLS ────────────────────────────
function openPromoProfitSubPage() {
  const paneCompany = document.getElementById("pane-company");
  const panePromo = document.getElementById("pane-promo-profit");
  if (paneCompany) paneCompany.classList.add("hidden");
  if (panePromo) panePromo.classList.remove("hidden");
  loadPromoCodes();
  loadProfitAnalytics();
  if (window.lucide) lucide.createIcons();
}

function closePromoProfitSubPage(returnToCompany = true) {
  const panePromo = document.getElementById("pane-promo-profit");
  const paneCompany = document.getElementById("pane-company");
  if (panePromo) panePromo.classList.add("hidden");
  if (returnToCompany && paneCompany) paneCompany.classList.remove("hidden");
  if (window.lucide) lucide.createIcons();
}

async function loadPromoCodes() {
  renderPromoCodes(promoData);

  try {
    const res = await fetch(`${API_BASE}/api/promo-codes?t=` + Date.now());
    if (res.ok) {
      const data = await res.json();
      if (Array.isArray(data) && data.length > 0) {
        promoData = data;
        localStorage.setItem("rkg_local_promos", JSON.stringify(promoData));
        renderPromoCodes(promoData);
      }
    }
  } catch (err) {}
}

function renderPromoCodes(promos) {
  const container = document.getElementById("promo-codes-container");
  if (!container) return;

  if (promos.length === 0) {
    container.innerHTML = `<div class="text-center py-6 text-slate-500 text-xs">No active promo codes generated yet.</div>`;
    return;
  }

  container.innerHTML = promos.map(p => {
    const codeNum = p.promo_code_number || p.doc_id || "PRM-N/A";
    const qrNum = p.qr_code_number || `QR-${codeNum}`;
    const name = p.promo_name || "Agro Promotion";
    const pct = p.validity_percentage || 10;
    const maxDisc = p.max_discount_amount ? `Max ₹${p.max_discount_amount}` : '';
    const minVal = p.min_order_value ? `Min Order ₹${p.min_order_value}` : '';
    const start = p.start_date || '2026-08-01';
    const end = p.end_date || '2026-12-31';
    const credits = p.number_of_purchase_credit || 100;
    const used = p.used_purchase_credits || 0;

    return `
      <div class="bg-slate-900 border border-slate-800 hover:border-emerald-500/60 rounded-2xl p-4 space-y-3 transition shadow-md">
        <div class="flex justify-between items-start">
          <div>
            <div class="flex items-center gap-1.5">
              <span class="px-2 py-0.5 rounded font-mono font-black text-[11px] bg-emerald-950 text-emerald-300 border border-emerald-500/50">
                ${codeNum}
              </span>
              <span class="px-2 py-0.5 rounded font-mono font-bold text-[9px] bg-purple-950 text-purple-300 border border-purple-700/50">
                ${qrNum}
              </span>
            </div>
            <div class="text-xs font-bold text-white pt-1.5">${name}</div>
          </div>

          <div class="text-right">
            <span class="inline-block px-2.5 py-1 rounded-full font-mono font-black text-xs bg-amber-950 text-amber-300 border border-amber-500/60">
              ${pct}% OFF
            </span>
          </div>
        </div>

        <div class="bg-slate-950 border border-slate-800 rounded-xl p-2.5 grid grid-cols-2 gap-2 text-[10px]">
          <div>
            <span class="text-slate-400 block font-semibold">Valid Period:</span>
            <span class="font-mono text-slate-200">${start} to ${end}</span>
          </div>
          <div>
            <span class="text-slate-400 block font-semibold">Purchase Credits (Bills):</span>
            <span class="font-mono text-emerald-400 font-bold">${used} / ${credits} Used</span>
          </div>
        </div>

        <div class="flex justify-between items-center text-[10px] text-slate-400 pt-0.5">
          <span>${minVal}</span>
          <span class="text-amber-400 font-bold">${maxDisc}</span>
        </div>
      </div>
    `;
  }).join('');

  if (window.lucide) lucide.createIcons();
}

async function loadProfitAnalytics() {
  renderProfitSummary(profitData);

  try {
    const res = await fetch(`${API_BASE}/api/profit/analytics?t=` + Date.now());
    if (res.ok) {
      const data = await res.json();
      if (Array.isArray(data) && data.length > 0) {
        profitData = data;
        localStorage.setItem("rkg_local_profits", JSON.stringify(profitData));
        renderProfitSummary(profitData);
      }
    }
  } catch (err) {}
}

function renderProfitSummary(profits) {
  const container = document.getElementById("profit-summary-container");
  const grandTotalEl = document.getElementById("profit-grand-total");
  if (!container) return;

  let totalRev = 0;
  let totalProfit = 0;
  let totalBills = 0;

  profits.forEach(p => {
    totalRev += parseFloat(p.total_sales_amount || 0);
    totalProfit += parseFloat(p.estimated_gross_profit || 0);
    totalBills += parseInt(p.number_of_bills_generated || p.number_of_purchase_credit || 0);
  });

  if (grandTotalEl) {
    grandTotalEl.textContent = `Gross Profit: ₹${totalProfit.toLocaleString('en-IN')}`;
  }

  container.innerHTML = `
    <div class="grid grid-cols-3 gap-2 pb-2 text-center">
      <div class="bg-slate-950 border border-slate-800 rounded-xl p-2">
        <span class="text-[9px] text-slate-400 uppercase font-bold block">Total Promo Sales</span>
        <span class="text-xs font-mono font-black text-white">₹${totalRev.toLocaleString('en-IN')}</span>
      </div>
      <div class="bg-slate-950 border border-slate-800 rounded-xl p-2">
        <span class="text-[9px] text-slate-400 uppercase font-bold block">Bills Generated</span>
        <span class="text-xs font-mono font-black text-amber-400">${totalBills} Bills</span>
      </div>
      <div class="bg-slate-950 border border-slate-800 rounded-xl p-2">
        <span class="text-[9px] text-slate-400 uppercase font-bold block">Net Profit</span>
        <span class="text-xs font-mono font-black text-emerald-400">₹${totalProfit.toLocaleString('en-IN')}</span>
      </div>
    </div>

    <div class="space-y-1.5 pt-1">
      ${profits.map(p => {
        const bills = p.number_of_bills_generated || p.number_of_purchase_credit || 0;
        const sales = parseFloat(p.total_sales_amount || 0).toLocaleString('en-IN');
        const profit = parseFloat(p.estimated_gross_profit || 0).toLocaleString('en-IN');

        return `
          <div class="flex justify-between items-center bg-slate-950/60 border border-slate-800/80 rounded-lg px-2.5 py-1.5 text-[10px]">
            <div class="space-y-0.5">
              <span class="font-mono font-bold text-amber-300">${p.promo_code_number}</span>
              <div class="text-slate-400 truncate max-w-[130px]">${p.promo_name}</div>
            </div>
            <div class="text-right">
              <div class="font-mono font-bold text-white">₹${sales} (${bills} bills)</div>
              <div class="font-mono text-emerald-400 font-black">+₹${profit} Profit</div>
            </div>
          </div>
        `;
      }).join('')}
    </div>
  `;
}

function openCreatePromoModal() {
  const modal = document.getElementById("create-promo-modal");
  if (modal) modal.classList.remove("hidden");
}

function closeCreatePromoModal() {
  const modal = document.getElementById("create-promo-modal");
  if (modal) modal.classList.add("hidden");
}

async function submitNewPromoCode() {
  const name = document.getElementById("new-promo-name")?.value.trim();
  const pct = parseFloat(document.getElementById("new-promo-percentage")?.value || 10.0);
  const credits = parseInt(document.getElementById("new-promo-credits")?.value || 100);
  const start = document.getElementById("new-promo-start")?.value;
  const end = document.getElementById("new-promo-end")?.value;
  const maxDisc = parseFloat(document.getElementById("new-promo-max-discount")?.value || 1000);
  const minVal = parseFloat(document.getElementById("new-promo-min-order")?.value || 1500);

  if (!name) {
    alert("Please enter Promo Offer Name");
    return;
  }

  const seqNum = promoData.length + 1;
  const seqLetter = String.fromCharCode(65 + ((seqNum - 1) % 26));
  const codeNum = `RKG-PRM-2026-${seqLetter}${seqNum < 10 ? '0' + seqNum : seqNum}`;
  const qrNum = `QR-RKG-9842-${seqLetter}${seqNum < 10 ? '0' + seqNum : seqNum}`;

  const newPromo = {
    promo_code_number: codeNum,
    promo_name: name,
    qr_code_number: qrNum,
    start_date: start,
    end_date: end,
    validity_percentage: pct,
    max_discount_amount: maxDisc,
    min_order_value: minVal,
    number_of_purchase_credit: credits,
    used_purchase_credits: 0,
    is_active: true
  };

  const newProfit = {
    promo_code_number: codeNum,
    promo_name: name,
    number_of_bills_generated: 0,
    number_of_purchase_credit: 0,
    total_sales_amount: 0.0,
    total_discount_given: 0.0,
    net_revenue: 0.0,
    estimated_gross_profit: 0.0,
    last_invoice_number: ""
  };

  promoData.unshift(newPromo);
  profitData.unshift(newProfit);
  localStorage.setItem("rkg_local_promos", JSON.stringify(promoData));
  localStorage.setItem("rkg_local_profits", JSON.stringify(profitData));

  closeCreatePromoModal();
  renderPromoCodes(promoData);
  renderProfitSummary(profitData);

  showToast(`✅ Generated Promo ${codeNum} with QR: ${qrNum}!`);

  recordDetailedHistory(
    "Promo_Code",
    codeNum,
    `Promo ${codeNum} (${name})`,
    "Generated Alphanumeric Sequence",
    "None",
    `${pct}% Discount (Credits: ${credits})`,
    `Generated QR: ${qrNum}, validity: ${start} to ${end}`
  );

  queueAndSyncAction("promo_code_create", newPromo, `${API_BASE}/api/promo-codes/create`);
}

// ── HISTORY SUB-PAGE CONTROLS ─────────────────────────────────────────
function openCompanyHistorySubPage() {
  const paneCompany = document.getElementById("pane-company");
  const paneHistory = document.getElementById("pane-company-history");
  if (paneCompany) paneCompany.classList.add("hidden");
  if (paneHistory) paneHistory.classList.remove("hidden");
  loadChangeHistory();
  if (window.lucide) lucide.createIcons();
}

function closeCompanyHistorySubPage(returnToCompany = true) {
  const paneHistory = document.getElementById("pane-company-history");
  const paneCompany = document.getElementById("pane-company");
  if (paneHistory) paneHistory.classList.add("hidden");
  if (returnToCompany && paneCompany) paneCompany.classList.remove("hidden");
  if (window.lucide) lucide.createIcons();
}

async function loadChangeHistory() {
  renderChangeHistory(changeHistoryData);
  const badge = document.getElementById("badge-history-count");
  if (badge) badge.textContent = `${changeHistoryData.length} Logs`;

  try {
    const res = await fetch(`${API_BASE}/api/ceo/change-history?t=` + Date.now());
    if (res.ok) {
      const data = await res.json();
      if (Array.isArray(data) && data.length > 0) {
        changeHistoryData = data;
        localStorage.setItem("rkg_local_history", JSON.stringify(changeHistoryData));
        renderChangeHistory(changeHistoryData);
        if (badge) badge.textContent = `${changeHistoryData.length} Logs`;
      }
    }
  } catch (err) {}
}

function renderChangeHistory(logs) {
  const container = document.getElementById("history-container");
  if (!container) return;

  if (logs.length === 0) {
    container.innerHTML = `<div class="text-center py-6 text-slate-500 text-xs">No modification history recorded yet.</div>`;
    return;
  }

  container.innerHTML = logs.map(log => {
    const ts = log.timestamp ? new Date(log.timestamp).toLocaleString('en-IN', {
      day: '2-digit', month: 'short', year: 'numeric',
      hour: '2-digit', minute: '2-digit', hour12: true
    }) : "Recent";

    let badgeColor = "bg-amber-950 text-amber-300 border-amber-700/60";
    if (log.table_name === 'products') badgeColor = "bg-emerald-950 text-emerald-300 border-emerald-700/60";
    else if (log.table_name === 'client_order_data') badgeColor = "bg-teal-950 text-teal-300 border-teal-700/60";
    else if (log.table_name === 'Promo_Code') badgeColor = "bg-pink-950 text-pink-300 border-pink-700/60";
    else if (log.table_name === 'data') badgeColor = "bg-purple-950 text-purple-300 border-purple-700/60";

    const syncBadge = log.sync_status || (navigator.onLine ? "🟢 Synced" : "🟠 Queued");

    return `
      <div class="bg-slate-900 border border-slate-800 rounded-2xl p-4 space-y-2.5 shadow-sm">
        <div class="flex justify-between items-start">
          <div class="flex items-center gap-1.5">
            <span class="px-2 py-0.5 rounded text-[9px] font-mono font-bold uppercase border ${badgeColor}">
              ${log.table_name || 'System'}
            </span>
            <span class="font-mono text-xs font-bold text-amber-400">${log.doc_id || ''}</span>
          </div>
          <div class="text-right">
            <span class="text-[9px] font-mono text-slate-400 block">${ts}</span>
            <span class="text-[9px] font-mono text-emerald-400 font-bold block">${syncBadge}</span>
          </div>
        </div>

        <div class="text-xs font-bold text-white">
          ${log.item_name || log.doc_id || 'System Parameter'}
        </div>

        <div class="text-[11px] font-semibold text-amber-300">
          Field: <span class="text-slate-200">${log.field_changed}</span>
        </div>

        <div class="bg-slate-950 border border-slate-800/80 rounded-xl p-2.5 grid grid-cols-2 gap-2 text-xs">
          <div>
            <span class="text-[10px] text-slate-400 block font-semibold">Previous Value</span>
            <span class="font-mono text-slate-300 font-bold break-words">${log.old_value || 'None'}</span>
          </div>
          <div>
            <span class="text-[10px] text-emerald-400 block font-semibold">New Value</span>
            <span class="font-mono text-emerald-300 font-black break-words">${log.new_value || 'None'}</span>
          </div>
        </div>

        ${log.remarks ? `
          <div class="text-[11px] text-slate-300 bg-slate-950/60 border border-slate-800 rounded-lg p-2 leading-relaxed">
            <span class="font-bold text-amber-400">Intimation:</span> ${log.remarks}
          </div>
        ` : ''}
      </div>
    `;
  }).join('');

  if (window.lucide) lucide.createIcons();
}

function recordDetailedHistory(table, docId, itemName, field, oldVal, newVal, remarks) {
  const isOnline = navigator.onLine;
  const newEntry = {
    timestamp: new Date().toISOString(),
    changed_by: currentUserSession?.username || "CEO",
    table_name: table,
    doc_id: docId,
    item_name: itemName,
    field_changed: field,
    old_value: String(oldVal),
    new_value: String(newVal),
    remarks: remarks,
    sync_status: isOnline ? "🟢 Synced to Firebase" : "🟠 Offline Queued"
  };
  changeHistoryData.unshift(newEntry);
  localStorage.setItem("rkg_local_history", JSON.stringify(changeHistoryData));
}

// ── Multi-Tier Offline Sync Queue Engine ──────────────────────────────
function queueAndSyncAction(actionType, payload, url) {
  const queueItem = {
    id: 'SYNC-' + Date.now() + '-' + Math.random().toString(36).substr(2, 5),
    actionType: actionType,
    payload: payload,
    url: url,
    timestamp: new Date().toISOString()
  };

  pendingSyncQueue.push(queueItem);
  localStorage.setItem("rkg_sync_queue", JSON.stringify(pendingSyncQueue));
  updateNetworkStatusBadge();

  if (navigator.onLine) {
    flushPendingSyncQueue();
  }
}

async function flushPendingSyncQueue() {
  if (!navigator.onLine || pendingSyncQueue.length === 0) return;

  const currentQueue = [...pendingSyncQueue];
  for (const item of currentQueue) {
    try {
      const res = await fetch(item.url, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(item.payload)
      });

      if (res.ok) {
        pendingSyncQueue = pendingSyncQueue.filter(q => q.id !== item.id);
        localStorage.setItem("rkg_sync_queue", JSON.stringify(pendingSyncQueue));
      }
    } catch (err) {
      break;
    }
  }

  updateNetworkStatusBadge();
}

// ── Bottom Toast Banner ──────────────────────────────────────────────
let toastTimeout = null;
function showToast(msg) {
  const banner = document.getElementById("toast-banner");
  const msgEl = document.getElementById("toast-msg");
  if (!banner || !msgEl) return;

  msgEl.textContent = msg;
  banner.classList.remove("hidden");
  
  if (toastTimeout) clearTimeout(toastTimeout);
  toastTimeout = setTimeout(() => {
    banner.classList.add("hidden");
  }, 3500);
}

function hideToast() {
  const banner = document.getElementById("toast-banner");
  if (banner) banner.classList.add("hidden");
}
