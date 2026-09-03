/**
 * RKG SUIAMBU — Official Client-Facing Website Script
 * Cloud Firebase Firestore Catalog Engine (Unique Codes aa01..gg23)
 * Left Pop-out Navigation Drawer, WhatsApp Multi-Item Cart & Live Location Inquiry Desk
 * WhatsApp Hotline: +91 94425 76622
 */

// ── Google Firebase Client SDK Configuration ──────────────────────────
const firebaseConfig = {
  projectId: "rkg-suiambu",
  authDomain: "rkg-suiambu.firebaseapp.com",
  storageBucket: "rkg-suiambu.appspot.com"
};

let firestoreDb = null;
try {
  if (typeof firebase !== 'undefined') {
    if (!firebase.apps || !firebase.apps.length) {
      firebase.initializeApp(firebaseConfig);
    }
    firestoreDb = firebase.firestore();
    console.log("🔥 Firebase Client SDK connected to Firestore project: rkg-suiambu");
  }
} catch (err) {
  console.log("Firebase connection initialized with cloud fallback:", err);
}

let currentLang = 'en'; // 'en' or 'ta'

// Comprehensive Default Catalog (Always Instant & Offline-Ready)
const DEFAULT_PRODUCTS = [
  { id: "gg10", code: "gg10", doc_id: "gg10", name: "Krishi Pro-Best Cattle Feed (50kg)", tamil_name: "கிருஷி ப்ரோ-பெஸ்ட் மாட்டுத்தீவனம் (50kg)", category: "FEEDS", brand: "Krishi", uom: "50 KG Bag", price: 1650, wholesale_price: 1580, stock_qty: 120, status: "Available" },
  { id: "gg11", code: "gg11", doc_id: "gg11", name: "Krishi Supreme High-Yield Cattle Feed (50kg)", tamil_name: "கிருஷி சுப்ரீம் மாட்டுத்தீவனம் (50kg)", category: "FEEDS", brand: "Krishi", uom: "50 KG Bag", price: 1750, wholesale_price: 1680, stock_qty: 85, status: "Available" },
  { id: "gg05", code: "gg05", doc_id: "gg05", name: "Prime Cotton Seed Pellets / Paruthi Kottai (50kg)", tamil_name: "பருத்தி கொட்டை தவிடு & புண்ணாக்கு (50kg)", category: "FEEDS", brand: "Suyambu", uom: "50 KG Bag", price: 1850, wholesale_price: 1780, stock_qty: 60, status: "Available" },
  { id: "gg09", code: "gg09", doc_id: "gg09", name: "Nayam Thavudu Fine Rice Bran Feed (50kg)", tamil_name: "நயம் தவிடு மாட்டுத்தீவனம் (50kg)", category: "FEEDS", brand: "Suyambu", uom: "50 KG Bag", price: 1200, wholesale_price: 1150, stock_qty: 140, status: "Available" },
  { id: "cc01", code: "cc01", doc_id: "cc01", name: "Pure Groundnut Cake / Kadalai Punnakku (50kg)", tamil_name: "மரச்செக்கு கடலை புண்ணாக்கு (50kg)", category: "FEEDS", brand: "Suyambu", uom: "50 KG Bag", price: 2350, wholesale_price: 2280, stock_qty: 45, status: "Available" },
  { id: "gg14", code: "gg14", doc_id: "gg14", name: "Krishi Country Chicken & Poultry Feed (50kg)", tamil_name: "கிருஷி நாட்டுக் கோழி தீவனம் (50kg)", category: "FEEDS", brand: "Krishi", uom: "50 KG Bag", price: 1550, wholesale_price: 1480, stock_qty: 90, status: "Available" },
  { id: "aa01", code: "aa01", doc_id: "aa01", name: "Vaagai Wood-Pressed Pure Coconut Oil (1 Litre)", tamil_name: "வாகை மரச்செக்கு தூய தேங்காய் எண்ணெய் (1 லிட்டர்)", category: "OILS", brand: "Suyambu", uom: "1 Litre Bottle", price: 280, wholesale_price: 260, stock_qty: 250, status: "Available" },
  { id: "aa02", code: "aa02", doc_id: "aa02", name: "Vaagai Wood-Pressed Pure Coconut Oil (5 Litres)", tamil_name: "வாகை மரச்செக்கு தூய தேங்காய் எண்ணெய் (5 லிட்டர் கேன்)", category: "OILS", brand: "Suyambu", uom: "5 Litre Can", price: 1350, wholesale_price: 1280, stock_qty: 100, status: "Available" },
  { id: "aa04", code: "aa04", doc_id: "aa04", name: "Vaagai Wood-Pressed Pure Groundnut Oil (1 Litre)", tamil_name: "வாகை மரச்செக்கு தூய கடலை எண்ணெய் (1 லிட்டர்)", category: "OILS", brand: "Suyambu", uom: "1 Litre Bottle", price: 240, wholesale_price: 225, stock_qty: 180, status: "Available" },
  { id: "aa05", code: "aa05", doc_id: "aa05", name: "Vaagai Wood-Pressed Pure Groundnut Oil (5 Litres)", tamil_name: "வாகை மரச்செக்கு தூய கடலை எண்ணெய் (5 லிட்டர் கேன்)", category: "OILS", brand: "Suyambu", uom: "5 Litre Can", price: 1150, wholesale_price: 1090, stock_qty: 75, status: "Available" },
  { id: "aa07", code: "aa07", doc_id: "aa07", name: "Vaagai Wood-Pressed Pure Gingelly / Sesame Oil (1 Litre)", tamil_name: "வாகை மரச்செக்கு நல்லெண்ணெய் (1 லிட்டர்)", category: "OILS", brand: "Suyambu", uom: "1 Litre Bottle", price: 380, wholesale_price: 360, stock_qty: 120, status: "Available" },
  { id: "aa10", code: "aa10", doc_id: "aa10", name: "Suyambu Traditional Vaagai Pooja Lamp Oil (1 Litre)", tamil_name: "சுயம்பு பாரம்பரிய பூஜை விளக்கெண்ணெய் (1 லிட்டர்)", category: "OILS", brand: "Suyambu", uom: "1 Litre Bottle", price: 160, wholesale_price: 145, stock_qty: 300, status: "Available" },
  { id: "gg01", code: "gg01", doc_id: "gg01", name: "Organic Raw Kambu / Pearl Millet (1 KG)", tamil_name: "இயற்கை நாட்டு கம்பு (1 கிலோ)", category: "MILLETS", brand: "Suyambu", uom: "1 KG Pack", price: 55, wholesale_price: 48, stock_qty: 500, status: "Available" },
  { id: "gg02", code: "gg02", doc_id: "gg02", name: "Organic Raw Ragi / Finger Millet (1 KG)", tamil_name: "நாட்டு கேழ்வரகு / ராகி (1 கிலோ)", category: "MILLETS", brand: "Suyambu", uom: "1 KG Pack", price: 60, wholesale_price: 52, stock_qty: 450, status: "Available" },
  { id: "gg18", code: "gg18", doc_id: "gg18", name: "A1 SSS Deluxe Ponni Boiled Rice (25 KG)", tamil_name: "A1 SSS டீலக்ஸ் பொன்னி புழுங்கல் அரிசி (25 KG)", category: "GRAINS_RICE", brand: "SSS", uom: "25 KG Bag", price: 1450, wholesale_price: 1390, stock_qty: 80, status: "Available" },
  { id: "gg21", code: "gg21", doc_id: "gg21", name: "Veera Sivaji Traditional BPT Rice (25 KG)", tamil_name: "வீர சிவாஜி பாரம்பரிய BPT அரிசி (25 KG)", category: "GRAINS_RICE", brand: "Sivaji", uom: "25 KG Bag", price: 1550, wholesale_price: 1480, stock_qty: 60, status: "Available" },
  { id: "dd01", code: "dd01", doc_id: "dd01", name: "Suyambu Pure Country Sambar & Curry Masala (500g)", tamil_name: "சுயம்பு நாட்டு சாம்பார் & கறி மசாலா பொடி (500g)", category: "BY_PRODUCTS", brand: "Suyambu", uom: "500g Pouch", price: 140, wholesale_price: 125, stock_qty: 150, status: "Available" }
];

let productsData = DEFAULT_PRODUCTS.map(p => ({
  ...p,
  image: "https://cdn.jsdelivr.net/gh/sanmugapriyan2021-alt/RKG_Suiambu@main/rkg-logo-official.jpg",
  source: "FIREBASE_CLOUD"
}));

let inquiryCart = JSON.parse(localStorage.getItem("rkg_inquiry_cart") || "[]");
let currentCategoryFilter = 'ALL';
let currentSearchQuery = '';
let currentViewMode = 'grid'; // 'grid' or 'table'

let companyData = {
  name: "RKG Suyambu Cattle Feed & Agro Products",
  company_name: "RKG Suyambu Cattle Feed & Agro Products",
  tamil_name: "ஆர்.கே.ஜி சுயம்பு மாட்டுத்தீவனம் & ஆலை",
  tagline: "Supreme Quality Cattle Feed, Pure Cold-Pressed Oils & Organic Millets",
  address: "SF No. 142/2, Main Road, Erode-Tirupur Agri Corridor, Tamil Nadu 638056",
  phone: "+91 94425 76622",
  whatsapp: "919442576622",
  email: "rkgsuyambu@gmail.com",
  gstin: "33AAAAA9999Z1M8",
  fssai: "12424005000124",
  dispatch_hours: "6:30 AM - 8:30 PM",
  working_days: "Mon - Sat (திங்கள் - சனி)"
};

// ── Left-to-Right Pop-out Navigation Drawer ───────────────────────────
function openLeftNavDrawer() {
  const drawer = document.getElementById("left-nav-drawer");
  const backdrop = document.getElementById("left-drawer-backdrop");
  if (!drawer || !backdrop) return;
  drawer.classList.remove("-translate-x-full");
  backdrop.classList.remove("hidden");
}

function closeLeftNavDrawer() {
  const drawer = document.getElementById("left-nav-drawer");
  const backdrop = document.getElementById("left-drawer-backdrop");
  if (!drawer || !backdrop) return;
  drawer.classList.add("-translate-x-full");
  backdrop.classList.add("hidden");
}

function selectDrawerCategory(cat) {
  closeLeftNavDrawer();
  const catBtn = Array.from(document.querySelectorAll('.cat-pill-btn')).find(b => {
    const fn = b.getAttribute('onclick') || '';
    return fn.includes(`'${cat}'`);
  });
  filterCategory(cat, catBtn);
  const productsSec = document.getElementById('products');
  if (productsSec) {
    productsSec.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }
}

// ── Generated Product Image Asset Matcher ───────────────────────────
function resolveProductImage(p) {
  if (p.image && p.image.length > 5 && !p.image.includes('/static/')) return p.image;
  const name = (p.name || p.product_name || '').toLowerCase();
  const code = (p.code || p.product_code || p.doc_id || '').toLowerCase();
  const uom = (p.uom || p.unit_of_measure || '').toLowerCase();

  // 1. Edible Oils
  if (name.includes('coconut') || code.startsWith('aa01') || code.startsWith('aa02') || code.startsWith('aa03')) {
    if (name.includes('5l') || name.includes('5 litre') || uom.includes('5l') || code === 'aa02') return 'products/coconut_oil_5l.png';
    if (name.includes('500') || uom.includes('500') || code === 'aa03') return 'products/coconut_oil_500ml.png';
    return 'products/coconut_oil_1l.png';
  }
  if (name.includes('peanut') || name.includes('groundnut oil') || name.includes('கடலை எண்ணெய்') || code.startsWith('aa04') || code.startsWith('aa05')) {
    if (name.includes('5l') || name.includes('5 litre') || uom.includes('5l') || code === 'aa05') return 'products/peanut_oil_5l.png';
    if (name.includes('500') || uom.includes('500') || code === 'aa06') return 'products/peanut_oil_500ml.png';
    return 'products/peanut_oil_1l.png';
  }
  if (name.includes('gingelly') || name.includes('sesame oil') || name.includes('நல்லெண்ணெய்') || code.startsWith('aa07') || code.startsWith('aa08')) {
    if (name.includes('5l') || name.includes('5 litre') || uom.includes('5l') || code === 'aa08') return 'products/gingelly_oil_5l.png';
    if (name.includes('500') || uom.includes('500') || code === 'aa09') return 'products/gingelly_oil_500ml.png';
    return 'products/gingelly_oil_1l.png';
  }
  if (name.includes('velakku') || name.includes('pooja') || name.includes('lamp') || name.includes('விளக்கு') || code.startsWith('aa10')) {
    return 'products/velakku_ennai_1l.png';
  }

  // 2. Cattle Feeds & Pellets
  if (name.includes('special') || name.includes('rkg special') || name.includes('ஸ்பெஷல்')) {
    return 'products/rkg_special_feed_50kg.png';
  }
  if (name.includes('pellet') || name.includes('milk feed') || name.includes('பால் பெருக்கும்')) {
    return 'products/rkg_feed_pellets_50kg.png';
  }
  if (name.includes('cotton') || name.includes('paruthi') || name.includes('பருத்தி') || code.startsWith('gg05') || code.startsWith('gg06')) {
    return 'products/cotton_seeds_50kg.png';
  }
  if (name.includes('bio pass') || name.includes('biopass')) {
    if (name.includes('70') || uom.includes('70')) return 'products/krishi_bio_pass_70kg.png';
    return 'products/krishi_bio_pass_50kg.png';
  }
  if (name.includes('pro-best') || name.includes('probest') || name.includes('supreme') || code.startsWith('gg10') || code.startsWith('gg11')) {
    return 'products/krishi_probest_70kg.png';
  }
  if (name.includes('chicken') || name.includes('poultry') || name.includes('கோழி') || code.startsWith('gg14')) {
    return 'products/krishi_chicken_feed_50kg.png';
  }
  if (name.includes('cake') || name.includes('punnakku') || name.includes('புண்ணாக்கு') || code.startsWith('cc01')) {
    if (name.includes('sesame') || name.includes('எள்ளு')) return 'products/sesame_oil_cake_50kg.png';
    return 'products/groundnut_oil_cake_50kg.png';
  }
  if (name.includes('corn') || name.includes('maize') || name.includes('சோளம்') || name.includes('thavudu') || name.includes('nayam') || code.startsWith('gg09')) {
    return 'products/corn_powder_50kg.png';
  }

  // 3. Rice Varieties
  if (name.includes('veeran') || code.startsWith('gg15') || code.startsWith('gg16')) {
    return 'products/veeran_saapadu_rice_26kg.png';
  }
  if (name.includes('ponni') || name.includes('sss') || name.includes('kollam') || code.startsWith('gg18') || code.startsWith('gg19')) {
    return 'products/kollam_ponni_rice_26kg.png';
  }
  if (name.includes('sivaji') || name.includes('shivaji') || name.includes('ir 20') || name.includes('bpt') || code.startsWith('gg21') || code.startsWith('gg22')) {
    return 'products/veera_shivaji_rice_26kg.png';
  }

  // 4. Millets & Grains
  if (name.includes('kambu') || name.includes('pearl millet') || name.includes('கம்பு') || code.startsWith('gg01')) {
    return 'products/cleaned_kambu_1kg.png';
  }
  if (name.includes('ragi') || name.includes('finger millet') || name.includes('கேழ்வரகு') || code.startsWith('gg02')) {
    return 'products/suyambu_ragi_1kg.png';
  }
  if (name.includes('wheat') || name.includes('கோதுமை') || code.startsWith('gg03')) {
    return 'products/suyambu_wheat_1kg.png';
  }
  if (name.includes('groundnut') || name.includes('peanut') || name.includes('வேர்க்கடலை') || code.startsWith('gg04')) {
    return 'products/suyambu_groundnut_1kg.png';
  }

  // 5. Masalas
  if (name.includes('sambar') || code.startsWith('dd01')) {
    return 'products/sambar_powder_200g.png';
  }
  if (name.includes('mutton') || name.includes('curry') || code.startsWith('dd02')) {
    return 'products/mutton_masala_200g.png';
  }

  return 'https://cdn.jsdelivr.net/gh/sanmugapriyan2021-alt/RKG_Suiambu@main/rkg-logo-official.jpg';
}

// ── View Mode Switcher (Grid vs Table) ────────────────────────────────
function switchProductViewMode(mode) {
  currentViewMode = mode;
  const gridEl = document.getElementById("products-grid");
  const tableWrap = document.getElementById("products-table-wrap");
  const gridBtn = document.getElementById("view-mode-grid");
  const tableBtn = document.getElementById("view-mode-table");

  if (mode === 'table') {
    if (gridEl) gridEl.classList.add("hidden");
    if (tableWrap) tableWrap.classList.remove("hidden");
    if (tableBtn) {
      tableBtn.classList.add("bg-amber-500", "text-slate-950");
      tableBtn.classList.remove("text-slate-300");
    }
    if (gridBtn) {
      gridBtn.classList.remove("bg-amber-500", "text-slate-950");
      gridBtn.classList.add("text-slate-300");
    }
    renderProductsTable();
  } else {
    if (gridEl) gridEl.classList.remove("hidden");
    if (tableWrap) tableWrap.classList.add("hidden");
    if (gridBtn) {
      gridBtn.classList.add("bg-amber-500", "text-slate-950");
      gridBtn.classList.remove("text-slate-300");
    }
    if (tableBtn) {
      tableBtn.classList.remove("bg-amber-500", "text-slate-950");
      tableBtn.classList.add("text-slate-300");
    }
    renderProductsGrid();
  }
}

// ── Load Products Catalog from Firebase Cloud ─────────────────────────
async function loadProductsCatalog() {
  if (firestoreDb) {
    try {
      // Check both 'products' and 'PRODUCTS' collections
      let snapshot = await firestoreDb.collection("products").get();
      if (snapshot.empty) {
        snapshot = await firestoreDb.collection("PRODUCTS").get();
      }

      if (!snapshot.empty) {
        const firestoreList = [];
        snapshot.forEach(doc => {
          const data = doc.data();
          firestoreList.push({
            id: doc.id,
            doc_id: doc.id,
            ...data
          });
        });

        // Strict Filter: ONLY authorized products where stock is strictly AVAILABLE (> 0)
        const availableProducts = firestoreList
          .filter(p => {
            const stockVal = Number(p.current_stock !== undefined ? p.current_stock : (p.stock_qty !== undefined ? p.stock_qty : (p.stock !== undefined ? p.stock : 0)));
            const priceVal = Number(p.selling_price || p.price || 0);
            const statusStr = String(p.status || '').toLowerCase();
            const isActive = (p.is_active === undefined || p.is_active === true);
            return isActive && stockVal > 0 && priceVal > 0 && statusStr !== 'out of stock' && statusStr !== 'open rate';
          })
          .map((p, idx) => {
            const uCode = p.product_code || p.code || p.doc_id || `aa${String(idx+1).padStart(2,'0')}`;
            const stockVal = Number(p.current_stock !== undefined ? p.current_stock : (p.stock_qty !== undefined ? p.stock_qty : (p.stock !== undefined ? p.stock : 0)));
            const priceVal = Number(p.selling_price || p.price || 0);
            const wholeVal = Number(p.wholesale_price || (priceVal * 0.95));

            return {
              ...p,
              id: p.id || uCode,
              code: uCode,
              doc_id: uCode,
              brand: p.brand || "Suyambu",
              name: p.product_name || p.name,
              tamil_name: p.tamil_name || "",
              category: p.category || "FEEDS",
              uom: p.unit_of_measure || p.uom || "Standard",
              price: priceVal,
              wholesale_price: wholeVal,
              stock_qty: stockVal,
              status: "Available",
              image: resolveProductImage(p),
              source: "FIREBASE_FIRESTORE"
            };
          });

        if (availableProducts.length > 0) {
          productsData = availableProducts;
          updateCounts();
          if (currentViewMode === 'table') renderProductsTable();
          else renderProductsGrid();
          return;
        }
      }
    } catch (fsErr) {
      console.log("Firebase Firestore products read notice:", fsErr.message);
    }
  }

  // REST API fallback with strict stock > 0 check
  try {
    const res = await fetch("/api/public/products?t=" + new Date().getTime());
    if (res.ok) {
      const data = await res.json();
      if (Array.isArray(data) && data.length > 0) {
        productsData = data
          .filter(p => {
            const stockVal = Number(p.current_stock !== undefined ? p.current_stock : (p.stock_qty !== undefined ? p.stock_qty : (p.stock !== undefined ? p.stock : 0)));
            const priceVal = Number(p.selling_price || p.price || 0);
            const statusStr = String(p.status || '').toLowerCase();
            return stockVal > 0 && priceVal > 0 && statusStr !== 'out of stock' && statusStr !== 'open rate';
          })
          .map((p, idx) => {
            const uCode = p.product_code || p.code || p.doc_id || `aa${String(idx+1).padStart(2,'0')}`;
            const stockVal = Number(p.current_stock !== undefined ? p.current_stock : (p.stock_qty !== undefined ? p.stock_qty : 0));
            return {
              ...p,
              id: p.id || uCode,
              code: uCode,
              doc_id: uCode,
              brand: p.brand || "Suyambu",
              name: p.product_name || p.name,
              tamil_name: p.tamil_name || "",
              category: p.category || "FEEDS",
              uom: p.unit_of_measure || p.uom || "Standard",
              price: Number(p.selling_price || p.price || 0),
              wholesale_price: Number(p.wholesale_price || 0),
              stock_qty: stockVal,
              status: "Available",
              image: resolveProductImage(p),
              source: "REST_API"
            };
          });

        updateCounts();
        if (currentViewMode === 'table') renderProductsTable();
        else renderProductsGrid();
        return;
      }
    }
  } catch (e) {}

  // Filter default catalog for available stock only
  productsData = DEFAULT_PRODUCTS
    .filter(p => Number(p.stock_qty || 0) > 0)
    .map(p => ({
      ...p,
      image: resolveProductImage(p),
      source: "AUTHORIZED_CATALOG"
    }));

  updateCounts();
  if (currentViewMode === 'table') renderProductsTable();
  else renderProductsGrid();
}

function updateCounts() {
  const countEl = document.getElementById("count-all");
  if (countEl) countEl.textContent = productsData.length;
}

// Filter Helper
function getFilteredProducts() {
  return productsData.filter(p => {
    if (currentCategoryFilter !== 'ALL') {
      if (currentCategoryFilter === 'BY_PRODUCTS' && p.category !== 'FEEDS' && !p.code.startsWith('dd') && !p.code.startsWith('cc')) return false;
      else if (currentCategoryFilter === 'GRAINS_RICE' && p.category !== 'GRAINS') return false;
      else if (currentCategoryFilter !== 'BY_PRODUCTS' && currentCategoryFilter !== 'GRAINS_RICE' && p.category !== currentCategoryFilter) return false;
    }
    
    if (currentSearchQuery.trim() !== '') {
      const q = currentSearchQuery.toLowerCase();
      const n = (p.name || '').toLowerCase();
      const tn = (p.tamil_name || '').toLowerCase();
      const b = (p.brand || '').toLowerCase();
      const code = (p.code || '').toLowerCase();
      return n.includes(q) || tn.includes(q) || b.includes(q) || code.includes(q);
    }
    return true;
  });
}

// ── Render Products in Grid (Cards View) ──────────────────────────────
function renderProductsGrid() {
  const container = document.getElementById("products-grid");
  if (!container) return;

  const filtered = getFilteredProducts();

  if (filtered.length === 0) {
    container.innerHTML = `
      <div class="col-span-full py-16 text-center text-slate-500">
        <i data-lucide="package-search" class="w-12 h-12 mx-auto text-amber-500 mb-3"></i>
        <p class="text-lg font-bold text-white">${currentLang === 'ta' ? 'பொருட்கள் எதுவும் கிடைக்கவில்லை' : 'No products found'}</p>
        <p class="text-xs text-slate-400 mt-1">${currentLang === 'ta' ? 'வேறு வார்த்தையை தேடி முயற்சிக்கவும்.' : 'Try changing your search term or category filter.'}</p>
      </div>
    `;
    if (window.lucide) lucide.createIcons();
    return;
  }

  container.innerHTML = filtered.map(p => {
    const displayName = currentLang === 'ta' ? (p.tamil_name || p.name) : p.name;
    const subName = currentLang === 'ta' ? p.name : (p.tamil_name || '');
    const displayUom = p.uom || 'Unit';
    const productImg = p.image || "https://cdn.jsdelivr.net/gh/sanmugapriyan2021-alt/RKG_Suiambu@main/rkg-logo-official.jpg";
    const brandName = p.brand || "Suyambu";
    const uCode = p.code || p.doc_id || "";
    const isPriceAvailable = p.price && p.price > 0;
    const priceDisplay = `₹${p.price.toLocaleString('en-IN')}`;

    const whatsappDirectText = encodeURIComponent(
      `வணக்கம் / Hello RKG Suyambu!\n\n` +
      `I want to order / inquire about:\n` +
      `*Product:* ${p.name}\n` +
      `*Brand:* ${brandName}\n` +
      `*Unique Code:* [${uCode}]\n` +
      `*Pack:* ${displayUom}\n` +
      `*Price:* ₹${p.price}\n` +
      (p.stock_qty <= 0 ? `*Current Stock:* Out of Stock (Inquiring for next fresh production batch)\n` : `*Stock:* In Stock (${p.stock_qty} available)\n`) +
      `Please share delivery / supply timeline.`
    );

    return `
      <div class="card-gold-glow bg-white rounded-3xl p-4 flex flex-col justify-between relative border border-slate-200 shadow-sm hover:shadow-xl transition-all duration-300">
        
        <div>
          <!-- Product Photo Showcase -->
          <div class="product-img-wrap mb-3 shadow-inner relative group cursor-pointer" onclick="openQuickViewModal('${p.id}')">
            <img src="${productImg}" alt="${p.name}" loading="lazy" class="group-hover:scale-105 transition duration-300">
            
            <!-- Unique Code & Brand Badge -->
            <div class="product-badge-overlay font-tamil">
              <span class="w-1.5 h-1.5 rounded-full bg-amber-400 animate-pulse"></span>
              <span>[${uCode}] ${brandName}</span>
            </div>

            <div class="absolute inset-0 bg-slate-950/40 opacity-0 group-hover:opacity-100 transition flex items-center justify-center rounded-2xl">
              <span class="px-3 py-1.5 rounded-xl bg-amber-500 text-slate-950 text-xs font-black flex items-center gap-1.5 shadow-lg">
                <i data-lucide="eye" class="w-3.5 h-3.5"></i> ${currentLang === 'ta' ? 'முழு விபரம்' : 'Quick View'}
              </span>
            </div>
          </div>

          <!-- Product Titles & Category -->
          <div class="mb-2">
            <div class="flex items-center justify-between text-[10px] font-bold text-amber-600 mb-1">
              <span class="px-2 py-0.5 rounded-md bg-amber-50 text-amber-800 border border-amber-200/60 font-black">${brandName}</span>
              <span class="text-emerald-700 font-semibold uppercase">${p.category}</span>
            </div>
            <h3 class="text-sm font-extrabold text-slate-900 leading-snug font-tamil hover:text-emerald-800 transition cursor-pointer line-clamp-2" onclick="openQuickViewModal('${p.id}')">
              ${displayName}
            </h3>
            ${subName ? `<p class="text-[10px] text-emerald-700 font-bold mt-0.5 line-clamp-1">${subName}</p>` : ''}
          </div>

          <!-- Specifications Chips -->
          <div class="flex items-center justify-between gap-1 mb-3 text-[10px] bg-slate-50 p-2 rounded-xl border border-slate-200 font-semibold text-slate-700">
            <div>Pack: <b class="text-emerald-800">${displayUom}</b></div>
            <div>Code: <b class="text-slate-900 font-mono">[${uCode}]</b></div>
          </div>
        </div>

        <!-- Pricing & Dual Action Buttons (Add to Cart + Direct WhatsApp) -->
        <div class="pt-2 border-t border-slate-100">
          <div class="flex items-baseline justify-between mb-2">
            <div>
              <div class="text-[9px] text-slate-400 uppercase font-bold">Retail Price</div>
              <div class="text-xl font-black text-slate-900">${priceDisplay}</div>
            </div>
            ${p.wholesale_price && p.wholesale_price > 0 ? `
              <div class="text-right">
                <div class="text-[9px] text-emerald-700 uppercase font-bold">Wholesale</div>
                <div class="text-xs font-black text-emerald-800">₹${p.wholesale_price.toLocaleString('en-IN')}</div>
              </div>
            ` : ''}
          </div>

          <!-- Stock Status Badge -->
          <div class="mb-2 flex items-center justify-between text-[10px]">
            ${p.stock_qty > 0 ? `
              <span class="inline-flex items-center gap-1 font-bold text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded-md border border-emerald-200">
                <span class="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
                <span>In Stock (${p.stock_qty})</span>
              </span>
            ` : `
              <span class="inline-flex items-center gap-1 font-bold text-rose-700 bg-rose-50 px-2 py-0.5 rounded-md border border-rose-200">
                <span class="w-1.5 h-1.5 rounded-full bg-rose-500"></span>
                <span>Not Available</span>
              </span>
            `}
          </div>

          <div class="grid grid-cols-2 gap-1.5">
            ${p.stock_qty > 0 ? `
              <button onclick="addToInquiryCart('${p.id}')"
                      class="flex items-center justify-center gap-1 px-2 py-2 rounded-xl bg-amber-500 hover:bg-amber-400 text-slate-950 font-black text-[11px] transition shadow-sm">
                <i data-lucide="plus-circle" class="w-3.5 h-3.5"></i>
                <span>Add Cart</span>
              </button>
            ` : `
              <button disabled
                      class="flex items-center justify-center gap-1 px-2 py-2 rounded-xl bg-slate-200 text-slate-400 font-bold text-[11px] cursor-not-allowed">
                <span>Not Available</span>
              </button>
            `}
            <a href="https://wa.me/919442576622?text=${whatsappDirectText}" target="_blank"
               class="flex items-center justify-center gap-1 px-2 py-2 rounded-xl bg-emerald-800 hover:bg-emerald-900 text-white font-black text-[11px] transition shadow-sm">
              <i data-lucide="message-circle" class="w-3.5 h-3.5 text-amber-400"></i>
              <span>Inquire</span>
            </a>
          </div>
        </div>
      </div>
    `;
  }).join('');

  if (window.lucide) lucide.createIcons();
}

// ── Render Products in Interactive Firebase Data Table ────────────────
function renderProductsTable() {
  const tbody = document.getElementById("products-table-body");
  if (!tbody) return;

  const filtered = getFilteredProducts();

  if (filtered.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" class="p-8 text-center text-slate-400">No products found matching criteria.</td></tr>`;
    return;
  }

  tbody.innerHTML = filtered.map((p, idx) => {
    const displayName = currentLang === 'ta' ? (p.tamil_name || p.name) : p.name;
    const uCode = p.code || p.doc_id || "";
    const isAvailable = p.stock_qty > 0;

    const whatsappDirectText = encodeURIComponent(
      `வணக்கம் / Hello RKG Suyambu!\n\n` +
      `Inquiring about: *${p.name}* [${uCode}]\n` +
      `*Pack:* ${p.uom} | *Price:* ₹${p.price}\n` +
      `*Stock:* ${isAvailable ? `In Stock (${p.stock_qty})` : 'Out of Stock'}`
    );

    return `
      <tr class="hover:bg-emerald-900/40 transition">
        <td class="p-3.5 font-mono font-bold text-amber-400">${uCode}</td>
        <td class="p-3.5 font-bold text-white flex items-center gap-2">
          <img src="${p.image}" class="w-8 h-8 rounded-lg object-contain bg-slate-900 border border-emerald-700">
          <div>
            <div>${displayName}</div>
            ${p.tamil_name && currentLang !== 'ta' ? `<div class="text-[10px] text-emerald-400 font-tamil">${p.tamil_name}</div>` : ''}
          </div>
        </td>
        <td class="p-3.5 font-semibold text-slate-300">${p.brand}</td>
        <td class="p-3.5 text-slate-300 font-medium">${p.uom}</td>
        <td class="p-3.5 font-black text-amber-300">₹${p.price.toLocaleString('en-IN')}</td>
        <td class="p-3.5">
          ${isAvailable ? `
            <span class="inline-flex items-center gap-1 font-bold text-emerald-300 bg-emerald-950 px-2 py-0.5 rounded-md border border-emerald-700 text-[10px]">
              <span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span> In Stock (${p.stock_qty})
            </span>
          ` : `
            <span class="inline-flex items-center gap-1 font-bold text-rose-300 bg-rose-950 px-2 py-0.5 rounded-md border border-rose-800 text-[10px]">
              <span class="w-1.5 h-1.5 rounded-full bg-rose-500"></span> Not Available
            </span>
          `}
        </td>
        <td class="p-3.5 text-center">
          <div class="inline-flex gap-1.5">
            ${isAvailable ? `
              <button onclick="addToInquiryCart('${p.id}')" class="px-2.5 py-1 rounded-lg bg-amber-500 hover:bg-amber-400 text-slate-950 font-black text-[10px] transition">
                + Cart
              </button>
            ` : `
              <button disabled class="px-2.5 py-1 rounded-lg bg-slate-800 text-slate-500 font-bold text-[10px] cursor-not-allowed">
                Out of Stock
              </button>
            `}
            <a href="https://wa.me/919442576622?text=${whatsappDirectText}" target="_blank"
               class="px-2.5 py-1 rounded-lg bg-emerald-700 hover:bg-emerald-600 text-white font-bold text-[10px] transition">
              Inquire
            </a>
          </div>
        </td>
      </tr>
    `;
  }).join('');
}

// ── Filter by Category Buttons ────────────────────────────────────────
function filterCategory(cat, btn) {
  currentCategoryFilter = cat;
  document.querySelectorAll('.cat-pill-btn').forEach(b => {
    b.classList.remove('active', 'bg-emerald-900', 'text-amber-300', 'border-amber-400', 'shadow-md');
    b.classList.add('bg-slate-900', 'text-slate-300', 'border-slate-700');
  });
  if (btn) {
    btn.classList.add('active', 'bg-emerald-900', 'text-amber-300', 'border-amber-400', 'shadow-md');
    btn.classList.remove('bg-slate-900', 'text-slate-300', 'border-slate-700');
  }
  if (currentViewMode === 'table') {
    renderProductsTable();
  } else {
    renderProductsGrid();
  }
}

// ── Search Handler ───────────────────────────────────────────────────
function onSearchProducts(e) {
  currentSearchQuery = e.target.value || '';
  if (currentViewMode === 'table') {
    renderProductsTable();
  } else {
    renderProductsGrid();
  }
}

// ── WhatsApp Multi-Item Inquiry Cart System ───────────────────────────
function addToInquiryCart(productId) {
  const p = productsData.find(x => x.id === productId);
  if (!p) return;

  const existing = inquiryCart.find(item => item.id === p.id);
  if (existing) {
    existing.qty += 1;
  } else {
    inquiryCart.push({
      id: p.id,
      doc_id: p.code || p.doc_id,
      name: p.name,
      tamil_name: p.tamil_name,
      brand: p.brand,
      uom: p.uom,
      price: p.price,
      image: p.image || "https://cdn.jsdelivr.net/gh/sanmugapriyan2021-alt/RKG_Suiambu@main/rkg-logo-official.jpg",
      qty: 1
    });
  }

  saveCart();
  renderCartDrawer();
  toggleCartDrawer(true);
  sendIpTelemetry("ADD_TO_CART", { product_id: p.id, name: p.name, code: p.code, price: p.price });
}

function updateCartQty(productId, delta) {
  const item = inquiryCart.find(x => x.id === productId);
  if (!item) return;
  item.qty += delta;
  if (item.qty <= 0) {
    inquiryCart = inquiryCart.filter(x => x.id !== productId);
  }
  saveCart();
  renderCartDrawer();
}

function removeFromCart(productId) {
  inquiryCart = inquiryCart.filter(x => x.id !== productId);
  saveCart();
  renderCartDrawer();
}

function saveCart() {
  localStorage.setItem("rkg_inquiry_cart", JSON.stringify(inquiryCart));
  updateCartBadge();
}

function updateCartBadge() {
  const totalCount = inquiryCart.reduce((sum, i) => sum + i.qty, 0);
  const badge1 = document.getElementById("cart-count-badge");
  const badge2 = document.getElementById("float-cart-count");
  if (badge1) badge1.textContent = totalCount;
  if (badge2) badge2.textContent = totalCount;
}

function toggleCartDrawer(forceOpen = null) {
  const drawer = document.getElementById("cart-drawer");
  const backdrop = document.getElementById("cart-drawer-backdrop");
  if (!drawer || !backdrop) return;

  const isOpen = !drawer.classList.contains("translate-x-full");
  const shouldOpen = forceOpen !== null ? forceOpen : !isOpen;

  if (shouldOpen) {
    drawer.classList.remove("translate-x-full");
    backdrop.classList.remove("hidden");
  } else {
    drawer.classList.add("translate-x-full");
    backdrop.classList.add("hidden");
  }
}

let appliedCartPromo = null;
const PROMO_CODES = {
  "RKG5": { type: "percent", value: 5, label: "5% Special Mill Discount" },
  "SUYAMBU": { type: "percent", value: 5, label: "5% Suyambu Agro Offer" },
  "KONGU10": { type: "percent", value: 10, label: "10% Kongu Regional Promo" },
  "FARMER50": { type: "flat", value: 50, label: "₹50 Direct Farmer Discount" },
  "MILL100": { type: "flat", value: 100, label: "₹100 Factory Dispatch Off" }
};

function applyCartPromoCode() {
  const input = document.getElementById("cart-customer-promo");
  if (!input) return;
  const code = input.value.trim().toUpperCase();
  
  if (!code) {
    alert("Please enter a promo code (e.g. RKG5, KONGU10, FARMER50).");
    return;
  }

  if (PROMO_CODES[code]) {
    appliedCartPromo = { code, ...PROMO_CODES[code] };
    alert(`Promo Code '${code}' Applied! (${PROMO_CODES[code].label})`);
    renderCartDrawer();
  } else {
    alert(`Invalid promo code '${code}'. Try using 'RKG5' or 'KONGU10'.`);
  }
}

function renderCartDrawer() {
  updateCartBadge();
  const container = document.getElementById("cart-items-container");
  const subtotalEl = document.getElementById("cart-subtotal-price");
  const discountRow = document.getElementById("cart-discount-row");
  const discountAmountEl = document.getElementById("cart-discount-amount");
  const totalEl = document.getElementById("cart-total-price");
  if (!container) return;

  if (inquiryCart.length === 0) {
    container.innerHTML = `
      <div class="py-16 text-center text-slate-400 text-xs">
        <i data-lucide="shopping-bag" class="w-10 h-10 mx-auto text-emerald-600 mb-2"></i>
        <p class="font-bold text-white">Your Inquiry Cart is empty</p>
        <p class="text-[11px] text-slate-400 mt-1">Click "+ Cart" on any product to prepare your itemized WhatsApp quote.</p>
      </div>
    `;
    if (subtotalEl) subtotalEl.textContent = "₹0";
    if (totalEl) totalEl.textContent = "₹0";
    if (discountRow) discountRow.classList.add("hidden");
    if (window.lucide) lucide.createIcons();
    return;
  }

  let subtotalSum = 0;
  container.innerHTML = inquiryCart.map(item => {
    const itemTotal = item.price * item.qty;
    subtotalSum += itemTotal;

    return `
      <div class="p-3 rounded-2xl bg-emerald-950/80 border border-emerald-800 flex items-center justify-between gap-3">
        <img src="${item.image}" alt="${item.name}" class="w-12 h-12 object-contain rounded-xl bg-slate-900 border border-emerald-700">
        
        <div class="flex-1 min-w-0">
          <div class="font-bold text-xs text-white truncate">${item.name}</div>
          <div class="text-[10px] text-amber-300 font-semibold">[${item.doc_id}] • ₹${item.price.toLocaleString('en-IN')} each</div>
          <div class="text-xs font-black text-emerald-400 mt-0.5">₹${itemTotal.toLocaleString('en-IN')}</div>
        </div>

        <div class="flex items-center gap-1.5 bg-slate-900 px-2 py-1 rounded-xl border border-emerald-700">
          <button onclick="updateCartQty('${item.id}', -1)" class="w-5 h-5 flex items-center justify-center font-black text-amber-400 hover:text-white">-</button>
          <span class="text-xs font-bold text-white w-4 text-center">${item.qty}</span>
          <button onclick="updateCartQty('${item.id}', 1)" class="w-5 h-5 flex items-center justify-center font-black text-amber-400 hover:text-white">+</button>
        </div>

        <button onclick="removeFromCart('${item.id}')" class="text-slate-500 hover:text-red-400 p-1 text-[11px] font-bold">Remove</button>
      </div>
    `;
  }).join('');

  let discount = 0;
  if (appliedCartPromo) {
    if (appliedCartPromo.type === "percent") {
      discount = (subtotalSum * appliedCartPromo.value) / 100;
    } else {
      discount = appliedCartPromo.value;
    }
  }

  const finalTotal = Math.max(0, subtotalSum - discount);

  if (subtotalEl) subtotalEl.textContent = `₹${subtotalSum.toLocaleString('en-IN')}`;
  if (discountRow && discountAmountEl) {
    if (discount > 0) {
      discountRow.classList.remove("hidden");
      discountAmountEl.textContent = `-₹${discount.toLocaleString('en-IN')} (${appliedCartPromo.code})`;
    } else {
      discountRow.classList.add("hidden");
    }
  }
  if (totalEl) totalEl.textContent = `₹${finalTotal.toLocaleString('en-IN')}`;
  if (window.lucide) lucide.createIcons();
}

function sendCartToWhatsApp() {
  if (inquiryCart.length === 0) {
    alert("Please add items to your cart before proceeding.");
    return;
  }

  const nameInput = document.getElementById("cart-customer-name");
  const phoneInput = document.getElementById("cart-customer-phone");
  const addressInput = document.getElementById("cart-customer-address");
  const promoInput = document.getElementById("cart-customer-promo");

  const name = nameInput ? nameInput.value.trim() : "";
  const phone = phoneInput ? phoneInput.value.trim() : "";
  const address = addressInput ? addressInput.value.trim() : "";
  const promoCode = appliedCartPromo ? appliedCartPromo.code : (promoInput ? promoInput.value.trim().toUpperCase() : "");

  if (!name) {
    alert("Please enter your Name / உங்கள் பெயரை உள்ளிடவும்.");
    if (nameInput) nameInput.focus();
    return;
  }

  let finalAddress = address || "Direct Factory Pickup / Location on WhatsApp";
  let itemsList = "";
  let subtotalSum = 0;

  inquiryCart.forEach((item, idx) => {
    const itemTotal = item.price * item.qty;
    subtotalSum += itemTotal;
    const uCode = item.doc_id || item.code || `item${idx+1}`;
    itemsList += `${idx + 1}. *[${uCode}] ${item.name}*\n   Qty: ${item.qty} x ${item.uom || 'Unit'} = ₹${itemTotal.toLocaleString('en-IN')}\n`;
  });

  let discount = 0;
  if (appliedCartPromo) {
    if (appliedCartPromo.type === "percent") {
      discount = (subtotalSum * appliedCartPromo.value) / 100;
    } else {
      discount = appliedCartPromo.value;
    }
  }

  const finalTotal = Math.max(0, subtotalSum - discount);

  let promoSection = "";
  if (promoCode && discount > 0) {
    promoSection = `• *Promo Applied:* ${promoCode} (-₹${discount.toLocaleString('en-IN')})\n`;
  }

  const waText = encodeURIComponent(
    `*NEW MULTI-ITEM ORDER INQUIRY — RKG SUIAMBU*\n` +
    `========================================\n` +
    `• *Customer Name:* ${name}\n` +
    (phone ? `• *Contact Phone:* ${phone}\n` : '') +
    `• *Delivery Location:* ${finalAddress}\n` +
    promoSection +
    `========================================\n` +
    `*ITEMS ORDERED:*\n` +
    itemsList +
    `========================================\n` +
    `• *Subtotal:* ₹${subtotalSum.toLocaleString('en-IN')}\n` +
    (discount > 0 ? `• *Discount:* -₹${discount.toLocaleString('en-IN')}\n` : '') +
    `• *FINAL ESTIMATE:* ₹${finalTotal.toLocaleString('en-IN')}\n\n` +
    `Please confirm stock availability and delivery schedule.`
  );

  // Direct Firebase Firestore Real-time Order Telemetry
  if (firestoreDb && typeof firebase !== 'undefined') {
    try {
      firestoreDb.collection("client_orders").add({
        customer_name: name,
        phone: phone,
        delivery_address: finalAddress,
        items: inquiryCart,
        items_count: inquiryCart.length,
        final_total: finalTotal,
        created_at: firebase.firestore.FieldValue.serverTimestamp()
      }).catch(err => console.log("Firebase order write notice:", err));
    } catch(e) {}
  }

  // Log purchase order to Firebase IP_Data_Input table
  sendIpTelemetry("PURCHASE_ORDER_SUBMITTED", {
    customer_name: name,
    phone: phone,
    delivery_address: finalAddress,
    items_count: inquiryCart.length,
    final_total: finalTotal
  });

  window.open(`https://wa.me/919442576622?text=${waText}`, '_blank');

  // Refresh cart
  inquiryCart = [];
  appliedCartPromo = null;
  localStorage.removeItem("rkg_inquiry_cart");

  if (nameInput) nameInput.value = "";
  if (phoneInput) phoneInput.value = "";
  if (addressInput) addressInput.value = "";
  if (promoInput) promoInput.value = "";

  updateCartBadge();
  renderCartDrawer();

  setTimeout(() => {
    toggleCartDrawer(false);
  }, 600);
}

// ── Footer 2-Column Inquiry Submission & Cancel Flow ─────────────────
function submitFooterInquiryToWhatsApp(e) {
  e.preventDefault();
  const name = document.getElementById("inq-fullname")?.value.trim() || "";
  const phone = document.getElementById("inq-phone")?.value.trim() || "";
  const purpose = document.getElementById("inq-purpose")?.value || "General Inquiry";
  const message = document.getElementById("inq-message")?.value.trim() || "";

  if (!name || !phone) {
    alert("Please enter both Name and Phone Number.");
    return;
  }

  // Write directly to Firebase Firestore
  if (firestoreDb && typeof firebase !== 'undefined') {
    try {
      firestoreDb.collection("client_inquiries").add({
        name: name,
        phone: phone,
        purpose: purpose,
        message: message,
        created_at: firebase.firestore.FieldValue.serverTimestamp()
      }).catch(err => console.log("Firebase inquiry write notice:", err));
    } catch(e) {}
  }

  // Log client inquiry telemetry in Firebase IP_Data_Input table
  sendIpTelemetry("INQUIRY_SUBMITTED", {
    name: name,
    phone: phone,
    purpose: purpose,
    message: message
  });

  const waText = encodeURIComponent(
    `*NEW CLIENT INQUIRY — RKG SUIAMBU MILL*\n` +
    `========================================\n` +
    `• *Client Name:* ${name}\n` +
    `• *Mobile Number:* ${phone}\n` +
    `• *Inquiry Purpose:* ${purpose}\n` +
    (message ? `• *Message & Requirements:* ${message}\n` : '') +
    `========================================\n` +
    `Please share pricing, direct factory quotation and delivery timeline.`
  );

  window.open(`https://wa.me/919442576622?text=${waText}`, '_blank');
  
  // Reset form
  const form = document.getElementById("footer-inquiry-form");
  if (form) form.reset();

  alert(`Thank you ${name}! Your inquiry has been sent to our WhatsApp desk.`);
}

function promptCancelFooterInquiry() {
  const modal = document.getElementById("cancel-confirm-modal");
  if (modal) modal.classList.remove("hidden");
}

function closeCancelConfirmModal() {
  const modal = document.getElementById("cancel-confirm-modal");
  if (modal) modal.classList.add("hidden");
}

function confirmCancelFooterInquiry() {
  const form = document.getElementById("footer-inquiry-form");
  if (form) form.reset();
  closeCancelConfirmModal();
}

// ── Product Quick View Modal ─────────────────────────────────────────
function openQuickViewModal(productId) {
  const p = productsData.find(x => x.id === productId);
  if (!p) return;

  const modal = document.getElementById("quickview-modal");
  const content = document.getElementById("quickview-content");
  if (!modal || !content) return;

  const displayName = currentLang === 'ta' ? (p.tamil_name || p.name) : p.name;
  const isAvailable = p.stock_qty > 0;

  const whatsappOrderText = encodeURIComponent(
    `வணக்கம் / Hello RKG Suyambu!\n\n` +
    `I would like to order / inquire about:\n` +
    `*Product:* ${p.name} [${p.code}]\n` +
    `*Pack:* ${p.uom || 'Standard'}\n` +
    `*Retail Price:* ₹${p.price}\n\n` +
    `Please confirm stock availability and door delivery schedule.`
  );

  content.innerHTML = `
    <div class="grid grid-cols-1 sm:grid-cols-2 gap-6 items-center">
      <div class="rounded-2xl overflow-hidden border border-emerald-800 bg-slate-950 p-3 shadow-inner">
        <img src="${p.image || 'https://cdn.jsdelivr.net/gh/sanmugapriyan2021-alt/RKG_Suiambu@main/rkg-logo-official.jpg'}" alt="${p.name}" class="w-full h-64 object-contain rounded-xl">
      </div>
      <div class="space-y-3">
        <span class="inline-block text-[10px] font-black uppercase tracking-wider text-amber-400 bg-amber-950 px-2.5 py-1 rounded-full border border-amber-500/40">
          [${p.code}] ${p.brand}
        </span>
        <h3 class="text-lg font-black font-tamil text-white leading-snug">${displayName}</h3>
        
        <!-- Stock status -->
        <div>
          ${isAvailable ? `
            <span class="inline-flex items-center gap-1 font-bold text-emerald-400 bg-emerald-950 px-2.5 py-1 rounded-lg border border-emerald-700 text-xs">
              <span class="w-2 h-2 rounded-full bg-emerald-400"></span> In Stock (${p.stock_qty} available)
            </span>
          ` : `
            <span class="inline-flex items-center gap-1 font-bold text-rose-400 bg-rose-950 px-2.5 py-1 rounded-lg border border-rose-800 text-xs">
              <span class="w-2 h-2 rounded-full bg-rose-500"></span> Not Available (Batch in Production)
            </span>
          `}
        </div>

        <div class="flex items-baseline justify-between pt-2 border-t border-emerald-800">
          <div>
            <div class="text-[9px] text-slate-400 uppercase font-bold">Retail Price</div>
            <div class="text-2xl font-black text-amber-400">₹${p.price.toLocaleString('en-IN')}</div>
          </div>
          ${p.wholesale_price ? `
            <div class="text-right">
              <div class="text-[9px] text-emerald-400 uppercase font-bold">Wholesale Rate</div>
              <div class="text-base font-black text-white">₹${p.wholesale_price.toLocaleString('en-IN')}</div>
            </div>
          ` : ''}
        </div>

        <div class="flex gap-2 pt-2">
          ${isAvailable ? `
            <button onclick="addToInquiryCart('${p.id}'); closeQuickViewModal()"
                    class="flex-1 py-3 rounded-xl bg-amber-500 hover:bg-amber-400 text-slate-950 font-black text-xs uppercase tracking-wider transition shadow-lg flex items-center justify-center gap-1.5">
              <i data-lucide="shopping-cart" class="w-4 h-4"></i>
              <span>Add to Cart</span>
            </button>
          ` : `
            <button disabled
                    class="flex-1 py-3 rounded-xl bg-slate-800 text-slate-500 font-bold text-xs cursor-not-allowed">
              <span>Out of Stock</span>
            </button>
          `}
          <a href="https://wa.me/919442576622?text=${whatsappOrderText}" target="_blank"
             class="flex-1 py-3 rounded-xl bg-emerald-800 hover:bg-emerald-700 text-white font-black text-xs uppercase tracking-wider transition shadow-lg flex items-center justify-center gap-1.5">
            <i data-lucide="message-circle" class="w-4 h-4 text-amber-400"></i>
            <span>Direct WhatsApp</span>
          </a>
        </div>
      </div>
    </div>
  `;

  modal.classList.remove("hidden");
  if (window.lucide) lucide.createIcons();
}

function closeQuickViewModal() {
  const modal = document.getElementById("quickview-modal");
  if (modal) modal.classList.add("hidden");
}

// ── Language Switcher Engine ─────────────────────────────────────────
function toggleLanguage(lang) {
  currentLang = lang;
  
  const btnEn = document.getElementById("lang-btn-en");
  const btnTa = document.getElementById("lang-btn-ta");
  if (btnEn && btnTa) {
    if (lang === 'ta') {
      btnTa.classList.add("bg-amber-500", "text-slate-950");
      btnTa.classList.remove("text-slate-300", "hover:bg-slate-700");
      btnEn.classList.remove("bg-amber-500", "text-slate-950");
      btnEn.classList.add("text-slate-300", "hover:bg-slate-700");
    } else {
      btnEn.classList.add("bg-amber-500", "text-slate-950");
      btnEn.classList.remove("text-slate-300", "hover:bg-slate-700");
      btnTa.classList.remove("bg-amber-500", "text-slate-950");
      btnTa.classList.add("text-slate-300", "hover:bg-slate-700");
    }
  }

  // Update all data-i18n attributes
  document.querySelectorAll("[data-i18n]").forEach(el => {
    const taText = el.getAttribute("data-i18n-ta");
    const enText = el.getAttribute("data-i18n-en");

    if (lang === 'ta' && taText) {
      el.innerHTML = taText;
    } else if (lang === 'en' && enText) {
      el.innerHTML = enText;
    }
  });

  // Update search input placeholder
  const searchInput = document.getElementById("product-search-input");
  if (searchInput) {
    searchInput.placeholder = lang === 'ta'
      ? "தயாரிப்பு பெயர், குறியீடு கொண்டு தேடவும் (aa01, எண்ணெய், அரிசி...)"
      : "Search product name, code (aa01, rice, oil...)";
  }

  // Re-render product views
  if (currentViewMode === 'table') {
    renderProductsTable();
  } else {
    renderProductsGrid();
  }
}

// ── Company Data Engine (Pull live from Firebase Firestore 'company_info') ──
async function loadCompanyInfo() {
  if (firestoreDb) {
    try {
      const doc = await firestoreDb.collection("company_info").doc("profile").get();
      if (doc.exists) {
        companyData = { ...companyData, ...doc.data() };
        updateCompanyDOM();
        return;
      }
    } catch (e) {
      console.log("Firebase company_info status:", e.message);
    }
  }

  try {
    const res = await fetch("/api/company-info?t=" + new Date().getTime());
    if (res.ok) {
      const data = await res.json();
      companyData = { ...companyData, ...data };
      updateCompanyDOM();
    }
  } catch (err) {
    console.log("Using cached company data from Firebase Cloud.");
  }
}

function updateCompanyDOM() {
  const compName = companyData.company_name || companyData.name || "RKG SUIAMBU";
  document.querySelectorAll(".company-name-text").forEach(el => el.textContent = compName);
  document.querySelectorAll(".company-tamil-text").forEach(el => el.textContent = companyData.tamil_name || "");
  document.querySelectorAll(".company-phone-text").forEach(el => el.textContent = companyData.phone || "+91 94425 76622");
  document.querySelectorAll(".company-email-text").forEach(el => el.textContent = companyData.email || "rkgsuyambu@gmail.com");
  document.querySelectorAll(".company-address-text").forEach(el => el.textContent = companyData.address || "");
  document.querySelectorAll(".company-gstin-text").forEach(el => el.textContent = companyData.gstin || "");
  document.querySelectorAll(".company-fssai-text").forEach(el => el.textContent = companyData.fssai || "");
  document.querySelectorAll(".company-hours-text").forEach(el => el.textContent = companyData.dispatch_hours || "6:30 AM - 8:30 PM");
  document.querySelectorAll(".company-days-text").forEach(el => el.textContent = companyData.working_days || "Mon - Sat (திங்கள் - சனி)");

  // Update dynamic WhatsApp links
  const cleanWA = (companyData.whatsapp || companyData.phone || "919442576622").replace(/[^0-9]/g, '');
  document.querySelectorAll("a[href*='wa.me']").forEach(link => {
    const currHref = link.getAttribute("href");
    if (currHref && !currHref.includes("text=")) {
      link.setAttribute("href", `https://wa.me/${cleanWA}`);
    }
  });
}

// ── IP & Device Telemetry Engine (IP_Data_Input / InputIP in Firebase) ──
function getOrCreateDeviceToken() {
  let token = localStorage.getItem("rkg_device_mac_token");
  if (!token) {
    token = "MAC-DEV-" + Math.random().toString(36).substring(2, 10).toUpperCase() + "-" + Date.now().toString(36).toUpperCase();
    localStorage.setItem("rkg_device_mac_token", token);
  }
  return token;
}

async function sendIpTelemetry(actionType = "PAGE_VISIT", purchaseData = "None") {
  try {
    const payload = {
      action_type: actionType,
      device_fingerprint: `${navigator.platform} | ${navigator.userAgent.slice(0, 80)} | ${screen.width}x${screen.height}`,
      mac_token: getOrCreateDeviceToken(),
      purchase_data: typeof purchaseData === 'object' ? JSON.stringify(purchaseData) : String(purchaseData),
      page_url: window.location.pathname + window.location.hash,
      session_id: getOrCreateDeviceToken(),
      timestamp: new Date().toISOString()
    };

    if (firestoreDb && typeof firebase !== 'undefined') {
      firestoreDb.collection("IP_Data_Input").add({
        ...payload,
        created_at: firebase.firestore.FieldValue.serverTimestamp()
      }).catch(() => {});
    }

    fetch("/api/public/log-ip", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    }).catch(() => {});
  } catch (err) {}
}

// ── Initialization ───────────────────────────────────────────────────
document.addEventListener("DOMContentLoaded", () => {
  if (window.lucide) lucide.createIcons();
  toggleLanguage('en');
  loadCompanyInfo();
  loadProductsCatalog();
  renderCartDrawer();
  sendIpTelemetry("PAGE_VISIT", "Client loaded RKG Suiambu website");
});

// Auto-sync when internet reconnects + background poll every 30s
window.addEventListener('online', () => {
  loadCompanyInfo();
  loadProductsCatalog();
  sendIpTelemetry("RECONNECTED_ONLINE", "Client reconnected to live mill network");
});
setInterval(() => {
  loadCompanyInfo();
  loadProductsCatalog();
}, 30000);




