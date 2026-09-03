// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
// RKG SUYAMBU â€” ENTERPRISE ERP APPLICATION FRONTEND ENGINE
// â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•

let allProducts = [];
let allParties = [];
let cart = [];
let billingType = 'RETAIL_B2C';
let currentPosCat = 'ALL';
let activePartyPaymentId = null;

let authToken = localStorage.getItem("rkg_auth_token") || null;
let currentUser = null;

function getAuthHeaders() {
    const headers = { "Content-Type": "application/json" };
    if (authToken) {
        headers["Authorization"] = `Bearer ${authToken}`;
    }
    return headers;
}

async function apiFetch(url, options = {}) {
    options = options || {};
    options.headers = options.headers || {};
    if (authToken && !options.headers["Authorization"]) {
        options.headers["Authorization"] = `Bearer ${authToken}`;
    }
    if (!options.headers["Content-Type"] && !(options.body instanceof FormData)) {
        options.headers["Content-Type"] = "application/json";
    }
    try {
        const res = await fetch(url, options);
        if (res.status === 401 && !url.includes("/api/auth/login")) {
            console.warn("Session expired. Redirecting to login...");
            authToken = null;
            currentUser = null;
            localStorage.removeItem("rkg_auth_token");
            showLoginScreen();
        }
        return res;
    } catch (err) {
        console.error(`API Fetch Error (${url}):`, err);
        throw err;
    }
}

document.addEventListener("DOMContentLoaded", async () => {
    const dateElem = document.getElementById("current-date-display");
    if (dateElem) {
        dateElem.innerText = new Date().toLocaleDateString('en-IN', {
            weekday: 'short', year: 'numeric', month: 'short', day: 'numeric'
        });
    }
    await checkAuthSession();
});

// â”€â”€ 1. AUTHENTICATION & PROFILE â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

async function checkAuthSession() {
    if (!authToken) {
        showLoginScreen();
        return;
    }

    try {
        const res = await fetch("/api/auth/me", {
            headers: getAuthHeaders()
        });

        if (res.ok) {
            currentUser = await res.json();
            showAppLayout();
            updateUserProfileUI();
            initializeAppData();
        } else {
            authToken = null;
            currentUser = null;
            localStorage.removeItem("rkg_auth_token");
            showLoginScreen();
        }
    } catch (err) {
        console.error("Auth check failed:", err);
        showLoginScreen();
    }
}

function showLoginScreen() {
    const loginScreen = document.getElementById("login-screen");
    const appLayout = document.getElementById("app-layout");
    if (loginScreen) loginScreen.style.display = "flex";
    if (appLayout) appLayout.style.display = "none";
    if (window.lucide) lucide.createIcons();
}

function showAppLayout() {
    const loginScreen = document.getElementById("login-screen");
    const appLayout = document.getElementById("app-layout");
    if (loginScreen) loginScreen.style.display = "none";
    if (appLayout) appLayout.style.display = "block";
    if (window.lucide) lucide.createIcons();
    if (currentUser && currentUser.role === 'CASHIER' && typeof navigate === 'function') {
        navigate('pos');
    }
}

function updateUserProfileUI() {
    if (!currentUser) return;

    const nameElem = document.getElementById("user-display-name");
    const roleElem = document.getElementById("user-display-role");
    const avatarElem = document.getElementById("user-avatar-initials");
    const userMgmtBtn = document.getElementById("btn-user-mgmt");

    if (nameElem) nameElem.innerText = currentUser.full_name || currentUser.username;
    
    const roleLabels = {
        'ADMIN': 'ADMIN â€¢ à®®à¯à®¤à®©à¯à®®à¯ˆ à®¨à®¿à®°à¯à®µà®¾à®•à®¿',
        'CASHIER': 'CASHIER â€¢ à®µà®¿à®±à¯à®ªà®©à¯ˆ à®•à®¾à®šà®¾à®³à®°à¯',
        'MANAGER': 'MANAGER â€¢ à®‰à®±à¯à®ªà®¤à¯à®¤à®¿ à®®à¯‡à®²à®¾à®³à®°à¯',
        'ACCOUNTANT': 'ACCOUNTANT â€¢ à®•à®£à®•à¯à®•à®¾à®³à®°à¯'
    };

    if (roleElem) {
        roleElem.innerText = roleLabels[currentUser.role] || currentUser.role;
    }

    if (avatarElem) {
        const initial = (currentUser.full_name || currentUser.username || "U").charAt(0).toUpperCase();
        avatarElem.innerText = initial;
    }

    if (userMgmtBtn) {
        if (currentUser.role === 'ADMIN') {
            userMgmtBtn.classList.remove("hidden");
        } else {
            userMgmtBtn.classList.add("hidden");
        }
    }
}

async function handleLoginSubmit(event) {
    event.preventDefault();
    const usernameInput = document.getElementById("login-username");
    const passwordInput = document.getElementById("login-password");
    const errorBox = document.getElementById("login-error-msg");
    const errorText = document.getElementById("login-error-text");
    const submitBtn = document.getElementById("btn-login-submit");

    const username = usernameInput.value.trim();
    const password = passwordInput.value;

    if (!username || !password) {
        if (errorBox) {
            errorBox.classList.remove("hidden");
            errorText.innerText = "Please enter username and password (à®ªà®¯à®©à®°à¯ à®ªà¯†à®¯à®°à¯ à®®à®±à¯à®±à¯à®®à¯ à®•à®Ÿà®µà¯à®šà¯à®šà¯Šà®²à¯ à®‰à®³à¯à®³à®¿à®Ÿà®µà¯à®®à¯)";
        }
        return;
    }

    if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.innerHTML = `<span class="inline-block animate-spin mr-2">â³</span> Signing in...`;
    }

    try {
        const res = await fetch("/api/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password })
        });

        const data = await res.json();

        if (res.ok) {
            authToken = data.access_token;
            currentUser = data.user;
            localStorage.setItem("rkg_auth_token", authToken);

            if (errorBox) errorBox.classList.add("hidden");
            showAppLayout();
            updateUserProfileUI();
            initializeAppData();
            if (window.showToast) showToast(`Welcome back, ${currentUser.full_name || currentUser.username}!`, 'success');
        } else {
            if (errorBox) {
                errorBox.classList.remove("hidden");
                errorText.innerText = data.detail || "Invalid username or password (à®¤à®µà®±à®¾à®© à®µà®¿à®µà®°à®™à¯à®•à®³à¯)";
            }
        }
    } catch (err) {
        console.error("Login request failed:", err);
        if (errorBox) {
            errorBox.classList.remove("hidden");
            errorText.innerText = "Connection error. Please check server. (à®‡à®£à¯ˆà®ªà¯à®ªà¯ à®ªà®¿à®´à¯ˆ)";
        }
    } finally {
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.innerHTML = `<i data-lucide="log-in" class="w-4 h-4"></i><span>Sign In / à®‰à®³à¯à®¨à¯à®´à¯ˆà®•</span>`;
            if (window.lucide) lucide.createIcons();
        }
    }
}

function quickFillCredentials(username, password) {
    const un = document.getElementById("login-username");
    const pw = document.getElementById("login-password");
    if (un) un.value = username;
    if (pw) pw.value = password;
    const form = document.getElementById("login-form");
    if (form) {
        if (typeof form.requestSubmit === 'function') {
            form.requestSubmit();
        } else {
            form.submit();
        }
    }
}

function openResetPasswordModal() {

    const modal = document.getElementById("reset-password-modal");
    if (modal) {
        modal.classList.remove("hidden");
        const currentInputUser = document.getElementById("login-username")?.value.trim();
        if (currentInputUser) {
            document.getElementById("reset-target-user").value = currentInputUser;
        }
        document.getElementById("reset-new-password").value = "";
        document.getElementById("reset-ceo-password").value = "";
        document.getElementById("reset-modal-error").classList.add("hidden");
        if (window.lucide) lucide.createIcons();
    }
}

function closeResetPasswordModal() {
    const modal = document.getElementById("reset-password-modal");
    if (modal) modal.classList.add("hidden");
}

async function handleResetPasswordSubmit(event) {
    event.preventDefault();
    const username = document.getElementById("reset-target-user").value.trim();
    const newPassword = document.getElementById("reset-new-password").value;
    const ceoPassword = document.getElementById("reset-ceo-password").value;
    const errBox = document.getElementById("reset-modal-error");
    const submitBtn = document.getElementById("btn-reset-submit");

    if (!username || !newPassword || !ceoPassword) {
        errBox.innerText = "Please fill in all fields (à®…à®©à¯ˆà®¤à¯à®¤à¯ à®µà®¿à®µà®°à®™à¯à®•à®³à¯ˆà®¯à¯à®®à¯ à®‰à®³à¯à®³à®¿à®Ÿà®µà¯à®®à¯).";
        errBox.classList.remove("hidden");
        return;
    }

    submitBtn.disabled = true;
    submitBtn.innerHTML = "Processing...";

    try {
        const res = await fetch("/api/auth/reset-operator-password", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                username: username,
                new_password: newPassword,
                ceo_password: ceoPassword
            })
        });

        const data = await res.json();
        if (res.ok) {
            closeResetPasswordModal();
            if (window.showToast) {
                showToast(data.message || `Password for '${username}' updated!`, "success");
            } else {
                alert(data.message || `Password for '${username}' updated!`);
            }
            document.getElementById("login-username").value = username;
            document.getElementById("login-password").value = newPassword;
        } else {
            errBox.innerText = data.detail || "Password reset failed.";
            errBox.classList.remove("hidden");
        }
    } catch (e) {
        errBox.innerText = "Connection error. Please check server.";
        errBox.classList.remove("hidden");
    } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = `<i data-lucide="check" style="width:14px;height:14px"></i> Confirm & Reset`;
        if (window.lucide) lucide.createIcons();
    }
}

function togglePasswordVisibility() {
    const passInput = document.getElementById("login-password");
    const icon = document.getElementById("login-eye-icon");
    if (!passInput) return;

    if (passInput.type === "password") {
        passInput.type = "text";
        if (icon) icon.setAttribute("data-lucide", "eye-off");
    } else {
        passInput.type = "password";
        if (icon) icon.setAttribute("data-lucide", "eye");
    }
    if (window.lucide) lucide.createIcons();
}

async function logoutUser() {
    if (confirm("Are you sure you want to sign out? (à®µà¯†à®³à®¿à®¯à¯‡à®± à®µà®¿à®°à¯à®®à¯à®ªà¯à®•à®¿à®±à¯€à®°à¯à®•à®³à®¾?)")) {
        try {
            await fetch("/api/auth/logout", {
                method: "POST",
                headers: getAuthHeaders()
            });
        } catch (e) {}

        authToken = null;
        currentUser = null;
        localStorage.removeItem("rkg_auth_token");
        showLoginScreen();
    }
}

function initializeAppData() {
    loadProducts();
    loadParties();
    loadInventoryValuation();
    loadBatchHistory();
    loadPurchases();
    loadExpenses();
    loadReports();

    // Default batch and purchase rows if empty
    const inList = document.getElementById("batch-inputs-list");
    const outList = document.getElementById("batch-outputs-list");
    const purList = document.getElementById("purchase-items-list");
    if (inList && inList.children.length === 0) addBatchInputRow();
    if (outList && outList.children.length === 0) addBatchOutputRow();
    if (purList && purList.children.length === 0) addPurchaseItemRow();
}

// â”€â”€ 2. VIEW NAVIGATION â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

function navigate(viewId) {
    document.querySelectorAll(".view-section").forEach(sec => sec.classList.remove("active"));
    document.querySelectorAll(".nav-item").forEach(item => item.classList.remove("active"));

    const targetSec = document.getElementById(`section-${viewId}`);
    const targetNav = document.getElementById(`nav-${viewId}`);

    if (targetSec) targetSec.classList.add("active");
    if (targetNav) targetNav.classList.add("active");

    const titles = {
        'pos': 'POS Billing & Invoicing',
        'inventory': 'Stock & Warehouse Manager',
        'production': 'Production & By-Product Converter',
        'purchases': 'Farmer Procurement Inward',
        'parties': 'Customers, Suppliers & Credit Ledger',
        'expenses': 'Factory & Shop Operating Expenses',
        'reports': 'Financial Accounting & Tax Filing Hub'
    };
    const badges = {
        'pos': 'Cattle Feed â€¢ Cold-Pressed Oils â€¢ Millets',
        'inventory': 'à®•à¯ˆà®¯à®¿à®°à¯à®ªà¯à®ªà¯ à®¨à®¿à®²à¯ˆ & à®•à®¿à®Ÿà®™à¯à®•à¯ à®®à¯‡à®²à®¾à®£à¯à®®à¯ˆ',
        'production': 'à®‰à®±à¯à®ªà®¤à¯à®¤à®¿ & à®‰à®ª à®ªà¯Šà®°à¯à®Ÿà¯à®•à®³à¯ à®®à®¤à®¿à®ªà¯à®ªà¯à®•à¯à®•à¯‚à®Ÿà¯à®Ÿà®²à¯',
        'purchases': 'à®µà®¿à®µà®šà®¾à®¯à®¿à®•à®³à¯ à®¨à¯‡à®°à®Ÿà®¿ à®•à¯Šà®³à¯à®®à¯à®¤à®²à¯',
        'parties': 'à®µà®¾à®Ÿà®¿à®•à¯à®•à¯ˆà®¯à®¾à®³à®°à¯ à®•à®Ÿà®©à¯ & à®µà®°à®µà¯ à®šà¯†à®²à®µà¯',
        'expenses': 'à®¤à®¿à®©à®šà®°à®¿ à®‡à®¯à®•à¯à®• à®šà¯†à®²à®µà¯à®•à®³à¯',
        'reports': 'à®†à®£à¯à®Ÿà¯ à®µà®°à®¿ à®¤à®¾à®•à¯à®•à®²à¯ & à®¤à®£à®¿à®•à¯à®•à¯ˆ à®…à®±à®¿à®•à¯à®•à¯ˆ'
    };

    const titleEl = document.getElementById("page-title");
    const badgeEl = document.getElementById("topbar-badge");
    if (titleEl) titleEl.innerText = titles[viewId] || 'RKG Suyambu ERP';
    if (badgeEl) badgeEl.innerText = badges[viewId] || '';

    // Refresh view-specific data
    if (viewId === 'pos') renderPosProducts();
    if (viewId === 'inventory') { loadProducts(); loadInventoryValuation(); }
    if (viewId === 'production') loadBatchHistory();
    if (viewId === 'purchases') loadPurchases();
    if (viewId === 'parties') loadParties();
    if (viewId === 'expenses') loadExpenses();
    if (viewId === 'reports') loadReports();

    if (window.lucide) lucide.createIcons();
}

// â”€â”€ 3. POS BILLING & CART â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

async function loadProducts() {
    try {
        const res = await apiFetch("/api/products/");
        allProducts = await res.json();
        renderPosProducts();
        renderInventoryTable();
        populateProductDropdowns();
    } catch (err) {
        console.error("Error loading products:", err);
    }
}

function setPosCatFilter(cat) {
    currentPosCat = cat;
    document.querySelectorAll(".pos-cat-btn").forEach(btn => {
        btn.classList.remove("btn-primary");
        btn.classList.add("btn-ghost");
    });
    if (event && event.target) {
        event.target.classList.remove("btn-ghost");
        event.target.classList.add("btn-primary");
    }
    renderPosProducts();
}

function filterPosProducts() {
    renderPosProducts();
}

function renderPosProducts() {
    const grid = document.getElementById("pos-product-grid");
    if (!grid) return;
    const query = (document.getElementById("pos-search")?.value || "").toLowerCase();
    grid.innerHTML = "";

    const filtered = allProducts.filter(p => {
        const pNameLower = (p.product_name || "").toLowerCase();
        const pTamilLower = (p.tamil_name || "").toLowerCase();

        let matchesCat = (currentPosCat === 'ALL');
        if (!matchesCat) {
            if (currentPosCat === 'OIL') {
                matchesCat = pNameLower.includes("oil") || pNameLower.includes("ennai") || pTamilLower.includes("à®Žà®£à¯à®£à¯†à®¯à¯");
            } else if (currentPosCat === 'CATTLE_FEED') {
                matchesCat = (pNameLower.includes("cattle") || pNameLower.includes("bio pass") || pNameLower.includes("probest") || pNameLower.includes("feed") || pNameLower.includes("thavudu") || pNameLower.includes("punnakku") || pNameLower.includes("paruthi") || pNameLower.includes("cotton")) && !pNameLower.includes("chicken");
            } else if (currentPosCat === 'CHICKEN_FEED') {
                matchesCat = pNameLower.includes("chicken") || pTamilLower.includes("à®•à¯‹à®´à®¿");
            } else if (currentPosCat === 'MILLETS') {
                matchesCat = pNameLower.includes("rice") || pNameLower.includes("wheat") || pNameLower.includes("ragi") || pNameLower.includes("corn") || pNameLower.includes("groundnut") || pTamilLower.includes("à®…à®°à®¿à®šà®¿") || pTamilLower.includes("à®•à¯‹à®¤à¯à®®à¯ˆ") || pTamilLower.includes("à®°à®¾à®•à®¿");
            } else if (currentPosCat === 'MASALA') {
                matchesCat = pNameLower.includes("powder") || pNameLower.includes("masala") || pNameLower.includes("sambar") || pTamilLower.includes("à®ªà¯Šà®Ÿà®¿") || pTamilLower.includes("à®®à®šà®¾à®²à®¾");
            } else {
                matchesCat = (p.category === currentPosCat);
            }
        }

        const matchesQuery = pNameLower.includes(query) || pTamilLower.includes(query);
        return matchesCat && matchesQuery && p.is_active;
    });

    if (filtered.length === 0) {
        grid.innerHTML = `<div class="col-span-3 text-center py-12 text-slate-400 text-xs">No matching products found in catalog</div>`;
        return;
    }

    filtered.forEach(p => {
        const price = (billingType === 'WHOLESALE_B2B' && p.wholesale_price > 0) ? p.wholesale_price : p.selling_price;
        const catBadge = p.category === 'FINISHED_GOOD' ? 'badge-finished' : (p.category === 'BY_PRODUCT' ? 'badge-byproduct' : 'badge-raw');
        
        const card = document.createElement("div");
        card.className = "bg-white p-3.5 rounded-xl border border-slate-200 hover:border-emerald-600 hover:shadow-md cursor-pointer transition-all flex flex-col justify-between";
        card.onclick = () => addToCart(p);

        const imgSrc = p.image_url || 'rkg-logo.png';
        card.innerHTML = `
            <div class="flex gap-2.5 items-start">
                <img src="${imgSrc}" class="w-12 h-12 rounded-lg object-contain bg-slate-50 border border-slate-200 flex-shrink-0 p-0.5" alt="${p.product_name}" loading="lazy">
                <div class="flex-1 min-w-0">
                    <div class="flex items-center justify-between mb-1">
                        <span class="badge ${catBadge}">${p.category.replace('_', ' ')}</span>
                        <span class="text-[10px] font-bold ${p.current_stock <= p.min_stock_alert ? 'text-rose-600' : 'text-slate-400'}">
                            ${p.current_stock} ${p.unit_of_measure}
                        </span>
                    </div>
                    <h4 class="font-bold text-xs text-slate-800 leading-snug truncate" title="${p.product_name}">${p.product_name}</h4>
                    ${p.tamil_name ? `<p class="text-[11px] text-emerald-700 font-semibold font-tamil truncate">${p.tamil_name}</p>` : ''}
                </div>
            </div>
            <div class="flex items-baseline justify-between mt-2.5 pt-1.5 border-t border-slate-100">
                <span class="text-[10px] text-slate-400">GST: ${p.tax_rate}%</span>
                <span class="text-sm font-black text-emerald-800">â‚¹ ${price.toFixed(2)}</span>
            </div>
        `;
        grid.appendChild(card);
    });
}

function setBillingType(type) {
    billingType = type;
    const btnRetail = document.getElementById("btn-retail");
    const btnWholesale = document.getElementById("btn-wholesale");

    if (btnRetail && btnWholesale) {
        if (type === 'RETAIL_B2C') {
            btnRetail.className = "btn btn-primary btn-sm";
            btnWholesale.className = "btn btn-ghost btn-sm";
        } else {
            btnWholesale.className = "btn btn-primary btn-sm";
            btnRetail.className = "btn btn-ghost btn-sm";
        }
    }
    renderPosProducts();
    updateCartTotals();
}

function addToCart(product) {
    if (!product || !product.id) return;
    if (product.current_stock <= 0) {
        if (window.showToast) showToast(`âš ï¸ '${product.product_name}' is out of stock in inventory! (à®‡à®°à¯à®ªà¯à®ªà¯ à®‡à®²à¯à®²à¯ˆ)`, "warning");
        return;
    }

    const existing = cart.find(item => item.product_id === product.id);
    const price = (billingType === 'WHOLESALE_B2B' && product.wholesale_price > 0) ? product.wholesale_price : product.selling_price;

    if (existing) {
        if (existing.quantity + 1 > product.current_stock) {
            if (window.showToast) showToast(`Cannot add more than available stock (${product.current_stock} ${product.unit_of_measure})`, "warning");
            return;
        }
        existing.quantity += 1;
    } else {
        cart.push({
            product_id: product.id,
            product_name: product.product_name,
            tamil_name: product.tamil_name,
            unit_of_measure: product.unit_of_measure,
            unit_price: price,
            tax_rate: product.tax_rate,
            quantity: 1
        });
    }
    renderCart();
}

function updateCartQty(productId, qty) {
    const item = cart.find(it => it.product_id === productId);
    if (item) {
        item.quantity = parseFloat(qty) || 0;
        if (item.quantity <= 0) {
            cart = cart.filter(it => it.product_id !== productId);
        }
    }
    renderCart();
}

function removeFromCart(productId) {
    cart = cart.filter(it => it.product_id !== productId);
    renderCart();
}

function updateCartQty(productId, newQty) {

    const qty = parseFloat(newQty);
    if (isNaN(qty) || qty <= 0) {
        removeFromCart(productId);
        return;
    }
    const it = cart.find(i => i.product_id === productId);
    if (it) {
        it.quantity = qty;
        renderCart();
    }
}

function adjustCartQty(productId, delta) {
    const it = cart.find(i => i.product_id === productId);
    if (it) {
        it.quantity = Math.max(0.1, Math.round((it.quantity + delta) * 10) / 10);
        if (it.quantity <= 0) {
            removeFromCart(productId);
        } else {
            renderCart();
        }
    }
}

function renderCart() {
    const tbody = document.getElementById("cart-items-body");
    const emptyMsg = document.getElementById("cart-empty-msg");
    if (!tbody) return;
    tbody.innerHTML = "";

    if (cart.length === 0) {
        if (emptyMsg) emptyMsg.classList.remove("hidden");
        updateCartTotals();
        return;
    }
    if (emptyMsg) emptyMsg.classList.add("hidden");

    cart.forEach(item => {
        const itemSubtotal = item.unit_price * item.quantity;
        const tr = document.createElement("tr");
        tr.className = "hover:bg-slate-50 border-b border-slate-100";
        tr.innerHTML = `
            <td class="py-2 px-3">
                <div class="font-bold text-slate-800 text-xs truncate max-w-[140px]" title="${item.product_name}">${item.product_name}</div>
                <div class="text-[10px] text-slate-400">â‚¹ ${item.unit_price.toFixed(2)} / ${item.unit_of_measure}</div>
            </td>
            <td class="py-2 px-1 text-center">
                <div style="display:flex;align-items:center;justify-content:center;gap:2px">
                    <button type="button" onclick="adjustCartQty(${item.product_id}, -1)" style="width:20px;height:22px;border:1px solid #cbd5e1;background:#fff;border-radius:4px;font-weight:900;font-size:11px;cursor:pointer">-</button>
                    <input type="number" min="0.1" step="any" value="${item.quantity}" onchange="updateCartQty(${item.product_id}, this.value)" style="width:36px;padding:2px 0;text-align:center;background:#fff;border:1px solid #cbd5e1;border-radius:4px;font-weight:800;font-size:11px">
                    <button type="button" onclick="adjustCartQty(${item.product_id}, +1)" style="width:20px;height:22px;border:1px solid #cbd5e1;background:#fff;border-radius:4px;font-weight:900;font-size:11px;cursor:pointer">+</button>
                </div>
            </td>
            <td class="py-2 px-2 text-right font-medium text-xs">â‚¹ ${item.unit_price.toFixed(2)}</td>
            <td class="py-2 px-2 text-right font-bold text-slate-900 text-xs">â‚¹ ${itemSubtotal.toFixed(2)}</td>
            <td class="py-2 px-1 text-center">
                <button onclick="removeFromCart(${item.product_id})" class="text-rose-500 hover:text-rose-700 font-bold" style="cursor:pointer" title="Remove item">âœ•</button>
            </td>
        `;
        tbody.appendChild(tr);
    });

    updateCartTotals();
}

function updateCartTotals() {
    let subtotal = 0;
    let totalTax = 0;

    cart.forEach(it => {
        const itemSub = it.unit_price * it.quantity;
        const itemTax = (itemSub * it.tax_rate / 100);
        subtotal += itemSub;
        totalTax += itemTax;
    });

    const discount = parseFloat(document.getElementById("cart-discount")?.value) || 0;
    const grandTotal = Math.max(0, Math.round(subtotal + totalTax - discount));

    const subEl = document.getElementById("cart-subtotal");
    const taxEl = document.getElementById("cart-tax");
    const grandEl = document.getElementById("cart-grand-total");
    const paidInput = document.getElementById("cart-paid-amount");

    if (subEl) subEl.innerText = `â‚¹ ${subtotal.toFixed(2)}`;
    if (taxEl) taxEl.innerText = `â‚¹ ${totalTax.toFixed(2)}`;
    if (grandEl) grandEl.innerText = `â‚¹ ${grandTotal.toFixed(2)}`;
    
    if (paidInput && (!paidInput.value || parseFloat(paidInput.value) === 0)) {
        paidInput.value = grandTotal;
    }

    calculateChangeReturn();
}

function handlePosPhoneInput(input) {
    const raw = input.value.replace(/[^0-9]/g, "").slice(0, 10);
    input.value = raw;
    const badge = document.getElementById("pos-phone-validation-badge");
    const nameInput = document.getElementById("pos-customer-name");

    if (raw.length === 10) {
        // Auto-search matching party
        if (Array.isArray(allParties)) {
            const matched = allParties.find(p => (p.phone || "").replace(/[^0-9]/g, "").includes(raw));
            if (matched) {
                if (nameInput) nameInput.value = matched.party_name;
                const pSelect = document.getElementById("cart-party-select");
                if (pSelect) pSelect.value = String(matched.id);
                if (badge) {
                    badge.style.display = "block";
                    badge.style.color = "#15803d";
                    badge.innerHTML = `âœ“ Registered Farmer/Party: <b>${matched.party_name}</b> (Balance: â‚¹${(matched.balance || 0).toFixed(2)})`;
                }
                return;
            }
        }
        if (badge) {
            badge.style.display = "block";
            badge.style.color = "#0369a1";
            badge.innerHTML = `âœ“ 10-Digit Mobile Number Validated`;
        }
    } else if (raw.length > 0) {
        if (badge) {
            badge.style.display = "block";
            badge.style.color = "#d97706";
            badge.innerHTML = `âš ï¸ Enter ${10 - raw.length} more digits`;
        }
    } else {
        if (badge) badge.style.display = "none";
    }
}

function setCashPreset(amount) {
    const paidInput = document.getElementById("cart-paid-amount");
    if (!paidInput) return;

    if (amount === 'EXACT') {
        const grandText = document.getElementById("cart-grand-total")?.innerText.replace(/[^0-9.]/g, "") || "0";
        paidInput.value = parseFloat(grandText) || 0;
    } else {
        paidInput.value = amount;
    }
    calculateChangeReturn();
}

function calculateChangeReturn() {
    const paidInput = document.getElementById("cart-paid-amount");
    const changeDisplay = document.getElementById("cart-change-display");
    if (!paidInput || !changeDisplay) return;

    const grandText = document.getElementById("cart-grand-total")?.innerText.replace(/[^0-9.]/g, "") || "0";
    const grandTotal = parseFloat(grandText) || 0;
    const paid = parseFloat(paidInput.value) || 0;

    if (paid > grandTotal && grandTotal > 0) {
        const change = paid - grandTotal;
        changeDisplay.innerHTML = `ðŸ’µ Change to Return: <span style="font-weight:900;color:#047857;font-size:13px">â‚¹ ${change.toFixed(2)}</span>`;
    } else if (paid < grandTotal && paid > 0) {
        const due = grandTotal - paid;
        changeDisplay.innerHTML = `ðŸ“‹ Balance Due (Credit): <span style="font-weight:800;color:#b91c1c">â‚¹ ${due.toFixed(2)}</span>`;
    } else {
        changeDisplay.innerHTML = "";
    }
}

function clearPosCart() {
    cart = [];
    if (document.getElementById("cart-discount")) document.getElementById("cart-discount").value = 0;
    if (document.getElementById("cart-paid-amount")) document.getElementById("cart-paid-amount").value = "";
    if (document.getElementById("pos-customer-phone")) document.getElementById("pos-customer-phone").value = "";
    if (document.getElementById("pos-customer-name")) document.getElementById("pos-customer-name").value = "";
    if (document.getElementById("pos-phone-validation-badge")) document.getElementById("pos-phone-validation-badge").style.display = "none";
    const pSelect = document.getElementById("cart-party-select");
    if (pSelect) pSelect.value = "";
    renderCart();
    const searchInput = document.getElementById("pos-search");
    if (searchInput) searchInput.focus();
}

async function checkoutBill(printMode = 'THERMAL') {
    if (cart.length === 0) {
        if (window.showToast) showToast("Please add items to bill first!", "warning");
        return;
    }

    const partySelect = document.getElementById("cart-party-select");
    const partyId = partySelect.value ? parseInt(partySelect.value) : null;
    const paymentMode = document.getElementById("cart-payment-mode").value;
    const discount = parseFloat(document.getElementById("cart-discount")?.value) || 0;
    const paidAmount = parseFloat(document.getElementById("cart-paid-amount")?.value) || 0;
    const custPhone = (document.getElementById("pos-customer-phone")?.value || "").trim();
    const custName = (document.getElementById("pos-customer-name")?.value || "").trim() || (partySelect.value ? partySelect.options[partySelect.selectedIndex].text : "Walk-in Customer");

    const payload = {
        invoice_date: new Date().toISOString().split("T")[0],
        party_id: partyId,
        customer_name: custName,
        customer_phone: custPhone,
        billing_type: billingType,
        discount_amount: discount,
        paid_amount: paidAmount,
        payment_mode: paymentMode,
        items: cart.map(it => ({
            product_id: it.product_id,
            quantity: it.quantity,
            unit_price: it.unit_price,
            tax_rate: it.tax_rate
        }))
    };

    try {
        const res = await fetch("/api/billing/invoices", {
            method: "POST",
            headers: {
                ...getAuthHeaders(),
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (!res.ok) {
            const err = await res.json();
            if (window.showToast) showToast("Error: " + (err.detail || "Failed to create invoice"), "error");
            return;
        }

        const invoice = await res.json();
        showBillModal(invoice);
        adjustReceiptSize(printMode === 'A4' ? 'A4' : '80mm');

        clearPosCart();
        loadProducts(); // Refresh stocks
        loadParties(); // Refresh balances
        if (window.showToast) showToast(`âœ“ Bill #${invoice.invoice_number} created successfully!`, "success");
    } catch (err) {
        console.error("Checkout error:", err);
        if (window.showToast) showToast("Failed to process bill.", "error");
    }
}


let currentBilledInvoice = null;

function adjustReceiptSize(size) {
    const printArea = document.getElementById("print-area");
    const modalBox = document.querySelector("#bill-modal .modal-box");
    if (!printArea || !modalBox) return;

    if (size === "58mm") {
        printArea.style.width = "250px";
        printArea.style.fontSize = "9.5px";
        modalBox.style.maxWidth = "440px";
    } else if (size === "A4") {
        printArea.style.width = "100%";
        printArea.style.maxWidth = "680px";
        printArea.style.fontSize = "12px";
        modalBox.style.maxWidth = "780px";
    } else { // 80mm
        printArea.style.width = "340px";
        printArea.style.fontSize = "10.5px";
        modalBox.style.maxWidth = "540px";
    }
}

function showBillModal(inv) {
    currentBilledInvoice = inv;
    document.getElementById("modal-inv-no").innerText = `Bill: ${inv.invoice_number}`;
    document.getElementById("modal-inv-date").innerText = `Date: ${inv.invoice_date}`;
    document.getElementById("modal-cust-name").innerText = inv.customer_name || "Walk-in Customer";
    
    if (document.getElementById("modal-bill-type")) {
        document.getElementById("modal-bill-type").innerText = inv.billing_type || "RETAIL_B2C";
    }
    if (document.getElementById("modal-pay-mode")) {
        document.getElementById("modal-pay-mode").innerText = inv.payment_mode || "CASH";
    }
    
    document.getElementById("modal-subtotal").innerText = `â‚¹ ${inv.subtotal.toFixed(2)}`;
    document.getElementById("modal-tax").innerText = `â‚¹ ${inv.total_tax.toFixed(2)}`;
    
    const discRow = document.getElementById("modal-discount-row");
    const discEl = document.getElementById("modal-discount");
    if (discRow && discEl) {
        if (inv.discount_amount > 0) {
            discRow.style.display = "flex";
            discEl.innerText = `- â‚¹ ${inv.discount_amount.toFixed(2)}`;
        } else {
            discRow.style.display = "none";
        }
    }

    document.getElementById("modal-grand-total").innerText = `â‚¹ ${inv.grand_total.toFixed(2)}`;
    document.getElementById("modal-paid").innerText = `â‚¹ ${inv.paid_amount.toFixed(2)}`;
    
    const balRow = document.getElementById("modal-balance-row");
    const balEl = document.getElementById("modal-balance");
    if (balRow && balEl) {
        if (inv.balance_due > 0) {
            balRow.style.display = "flex";
            balEl.innerText = `â‚¹ ${inv.balance_due.toFixed(2)}`;
        } else {
            balRow.style.display = "none";
        }
    }

    // Refresh dynamic company text
    if (window.companyData) {
        const phoneSpans = document.querySelectorAll(".company-phone-text");
        phoneSpans.forEach(s => s.innerText = window.companyData.phone || "+91 8438539264");
        const gstinSpans = document.querySelectorAll(".company-gstin-text");
        gstinSpans.forEach(s => s.innerText = window.companyData.gstin || "33AAAAA0000A1Z5");
        const upiSpans = document.querySelectorAll(".company-upi-text");
        upiSpans.forEach(s => s.innerText = window.companyData.upi_id || "rkgsuyambu@upi");
        const bankAccSpans = document.querySelectorAll(".company-bank-acc");
        bankAccSpans.forEach(s => s.innerText = window.companyData.bank_account || "123456789012");
        const bankIfscSpans = document.querySelectorAll(".company-bank-ifsc");
        bankIfscSpans.forEach(s => s.innerText = window.companyData.bank_ifsc || "SBIN0001234");
    }

    const tbody = document.getElementById("modal-inv-items");
    if (tbody) {
        tbody.innerHTML = "";
        inv.items.forEach(it => {
            const tr = document.createElement("tr");
            tr.style.borderBottom = "1px dashed #e2e8f0";
            tr.innerHTML = `
                <td style="padding:4px 0">
                    <div style="font-weight:700">${it.product_name}</div>
                    ${it.hsn_code ? `<div style="font-size:9px;color:#64748b">HSN: ${it.hsn_code}</div>` : ''}
                </td>
                <td style="padding:4px 0;text-align:center">${it.quantity} ${it.unit_of_measure}</td>
                <td style="padding:4px 0;text-align:right">â‚¹ ${it.unit_price.toFixed(2)}</td>
                <td style="padding:4px 0;text-align:right;font-weight:700">â‚¹ ${it.total_amount.toFixed(2)}</td>
            `;
            tbody.appendChild(tr);
        });
    }

    // Default to 80mm
    const sizeSelect = document.getElementById("receipt-print-size");
    if (sizeSelect) sizeSelect.value = "80mm";
    adjustReceiptSize("80mm");

    document.getElementById("bill-modal").classList.remove("hidden");
    if (window.lucide) lucide.createIcons();
}

function closeBillModal() {
    document.getElementById("bill-modal").classList.add("hidden");
}

function printReceipt() {
    const printContent = document.getElementById("print-area");
    if (!printContent) {
        window.print();
        return;
    }
    
    const size = document.getElementById("receipt-print-size")?.value || "80mm";
    let pageSizeCss = "@page { margin: 0; size: 80mm auto; } body { width: 80mm; padding: 4mm; }";
    if (size === "58mm") {
        pageSizeCss = "@page { margin: 0; size: 58mm auto; } body { width: 58mm; padding: 2mm; font-size: 9.5px; }";
    } else if (size === "A4") {
        pageSizeCss = "@page { margin: 10mm; size: A4 portrait; } body { width: 100%; max-width: 180mm; margin: 0 auto; font-size: 12px; }";
    }

    const printWindow = window.open('', '_blank', 'width=520,height=700');
    if (printWindow) {
        printWindow.document.write(`
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>RKG Suyambu Receipt - ${currentBilledInvoice ? currentBilledInvoice.invoice_number : 'Cash Bill'}</title>
                <style>
                    ${pageSizeCss}
                    body {
                        font-family: 'Courier New', Courier, monospace;
                        margin: 0;
                        color: #000;
                        background: #fff;
                    }
                    .emblem-circle {
                        border-radius: 50%;
                        border: 1.5px solid #ca8a04;
                        width: 50px;
                        height: 50px;
                        object-fit: contain;
                        display: block;
                        margin: 0 auto 4px;
                    }
                    table { width: 100%; border-collapse: collapse; font-size: 10.5px; margin: 6px 0; }
                    th, td { padding: 3px 1px; }
                    th { border-bottom: 1px solid #000; }
                    @media print {
                        body { margin: 0; }
                    }
                </style>
            </head>
            <body>
                ${printContent.innerHTML}
                <script>
                    window.onload = function() {
                        window.focus();
                        window.print();
                        setTimeout(() => window.close(), 1000);
                    };
                <\/script>
            </body>
            </html>
        `);
        printWindow.document.close();
    } else {
        window.print();
    }
}

async function downloadPdfInvoice() {
    if (!currentBilledInvoice || !currentBilledInvoice.id) {
        if (window.showToast) showToast("No active invoice selected", "warning");
        return;
    }
    
    try {
        if (window.showToast) showToast("Generating official A4 Tax Invoice PDF...", "info");
        const res = await fetch(`/api/billing/invoices/${currentBilledInvoice.id}/pdf`, {
            headers: getAuthHeaders()
        });
        
        if (!res.ok) throw new Error("Failed to generate PDF");
        
        const blob = await res.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = `Invoice_${currentBilledInvoice.invoice_number.replace(/\//g, '_')}.pdf`;
        document.body.appendChild(a);
        a.click();
        a.remove();
        if (window.showToast) showToast("A4 Tax Invoice PDF downloaded!", "success");
    } catch(err) {
        console.error("PDF download failed, falling back to direct window open:", err);
        window.open(`/api/billing/invoices/${currentBilledInvoice.id}/pdf`, '_blank');
    }
}

// â”€â”€ 4. PARTIES & CREDIT LEDGER â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

async function loadParties() {
    try {
        const res = await apiFetch("/api/parties/");
        allParties = await res.json();
        
        // Populate POS dropdown
        const select = document.getElementById("cart-party-select");
        if (select) {
            select.innerHTML = `<option value="">â€” Cash Customer (à®šà®¿à®²à¯à®²à®±à¯ˆ) â€”</option>`;
            allParties.forEach(p => {
                select.innerHTML += `<option value="${p.id}">${p.party_name} (${p.city_village || 'Local'})</option>`;
            });
        }

        renderPartiesTable();
    } catch (err) {
        console.error("Error loading parties:", err);
    }
}

function renderPartiesTable(filteredList = null) {
    const parties = filteredList || allParties;
    const tbody = document.getElementById("parties-table-body");
    if (!tbody) return;
    tbody.innerHTML = "";

    let totalRecv = 0;
    let totalPay = 0;

    allParties.forEach(p => {
        if (p.outstanding_balance > 0) totalRecv += p.outstanding_balance;
        else if (p.outstanding_balance < 0) totalPay += Math.abs(p.outstanding_balance);
    });

    const recvEl = document.getElementById("party-total-recv");
    const payEl = document.getElementById("party-total-payable");
    const countEl = document.getElementById("party-total-count");
    if (recvEl) recvEl.innerText = `â‚¹ ${totalRecv.toLocaleString('en-IN')}`;
    if (payEl) payEl.innerText = `â‚¹ ${totalPay.toLocaleString('en-IN')}`;
    if (countEl) countEl.innerText = allParties.length;

    parties.forEach(p => {
        const tr = document.createElement("tr");
        const isRecv = p.outstanding_balance > 0;
        const isPay = p.outstanding_balance < 0;
        tr.innerHTML = `
            <td class="p-3">
                <div class="font-bold text-slate-800 text-xs">${p.party_name}</div>
                ${p.tamil_name ? `<div class="text-[11px] text-emerald-700 font-tamil">${p.tamil_name}</div>` : ''}
            </td>
            <td class="p-3 text-xs"><span class="badge ${p.party_type.startsWith('CUSTOMER') ? 'badge-finished' : 'badge-raw'}">${p.party_type.replace('_', ' ')}</span></td>
            <td class="p-3 text-xs">${p.phone || '-'}</td>
            <td class="p-3 text-xs">${p.city_village || '-'}</td>
            <td class="p-3 text-xs font-mono text-[11px]">${p.gstin || '-'}</td>
            <td class="p-3 text-right font-black ${isRecv ? 'text-emerald-700' : (isPay ? 'text-rose-600' : 'text-slate-600')} text-xs">
                â‚¹ ${Math.abs(p.outstanding_balance).toFixed(2)} ${isRecv ? '(Dr)' : (isPay ? '(Cr)' : '')}
            </td>
            <td class="p-3 text-center">
                <button onclick="recordPartyPaymentModal(${p.id}, '${p.party_name}', ${p.outstanding_balance})" class="btn btn-ghost btn-sm" style="font-size:10.5px">
                    <i data-lucide="wallet" class="w-3 h-3 text-emerald-700"></i> Collect / Pay
                </button>
            </td>
        `;
        tbody.appendChild(tr);
    });

    if (window.lucide) lucide.createIcons();
}

function filterParties() {
    const q = (document.getElementById("party-search")?.value || "").toLowerCase();
    const filtered = allParties.filter(p => 
        p.party_name.toLowerCase().includes(q) ||
        (p.tamil_name && p.tamil_name.toLowerCase().includes(q)) ||
        (p.phone && p.phone.includes(q)) ||
        (p.city_village && p.city_village.toLowerCase().includes(q))
    );
    renderPartiesTable(filtered);
}

function onCartPartyChange() {
    const partyId = document.getElementById("cart-party-select")?.value;
    if (partyId) {
        const party = allParties.find(p => p.id === parseInt(partyId));
        if (party && party.party_type === 'CUSTOMER_WHOLESALE') {
            setBillingType('WHOLESALE_B2B');
        }
    }
}

function openNewPartyModal() {
    document.getElementById("new-party-modal").classList.remove("hidden");
    if (window.lucide) lucide.createIcons();
}

function validatePartyPhoneLive() {
    const input = document.getElementById("np-party-phone");
    const status = document.getElementById("party-phone-status");
    if (!input) return false;

    let digits = input.value.replace(/\D/g, '');
    if (digits.startsWith("91") && digits.length > 10) digits = digits.substring(2);
    else if (digits.startsWith("0") && digits.length > 10) digits = digits.substring(1);
    if (digits.length > 10) digits = digits.substring(0, 10);
    input.value = digits;

    const repetitivePatterns = ["0000000000", "1111111111", "2222222222", "3333333333", "4444444444", "5555555555", "6666666666", "7777777777", "8888888888", "9999999999", "1234567890", "9876543210"];
    const uniqueDigits = new Set(digits.split('')).size;

    if (!digits) {
        if (status) { status.innerHTML = ""; status.style.display = "none"; }
        input.style.borderColor = "";
        return true;
    }

    if (digits.length < 10) {
        if (status) {
            status.innerHTML = `<span style="color:#ef4444;font-weight:bold">âŒ 10 Digits Required (${digits.length}/10)</span>`;
            status.style.display = "inline";
        }
        input.style.borderColor = "#ef4444";
        return false;
    }

    if (!['6','7','8','9'].includes(digits[0])) {
        if (status) {
            status.innerHTML = `<span style="color:#ef4444;font-weight:bold">âŒ Starts with 6,7,8,9</span>`;
            status.style.display = "inline";
        }
        input.style.borderColor = "#ef4444";
        return false;
    }

    if (uniqueDigits <= 2 || repetitivePatterns.includes(digits)) {
        if (status) {
            status.innerHTML = `<span style="color:#ef4444;font-weight:bold">âŒ Repetitive / Fake Number</span>`;
            status.style.display = "inline";
        }
        input.style.borderColor = "#ef4444";
        return false;
    }

    if (status) {
        status.innerHTML = `<span style="color:#16a34a;font-weight:bold">âœ… Valid 10-Digit Mobile</span>`;
        status.style.display = "inline";
    }
    input.style.borderColor = "#16a34a";
    return true;
}

async function submitNewParty() {
    const name = document.getElementById("np-party-name")?.value.trim();
    if (!name) {
        if (window.showToast) showToast("Please enter Party Name!", "warning");
        return;
    }

    const phoneRaw = document.getElementById("np-party-phone")?.value.trim();
    if (phoneRaw && !validatePartyPhoneLive()) {
        if (window.showToast) showToast("Please enter a valid 10-digit Indian mobile number (e.g. 9842111223)", "warning");
        document.getElementById("np-party-phone")?.focus();
        return;
    }

    const payload = {
        party_name: name,
        tamil_name: document.getElementById("np-party-tamil")?.value.trim() || null,
        party_type: document.getElementById("np-party-type")?.value || 'CUSTOMER_RETAIL',
        phone: phoneRaw || null,
        city_village: document.getElementById("np-party-city")?.value.trim() || null,
        gstin: document.getElementById("np-party-gstin")?.value.trim() || null,
        address: document.getElementById("np-party-address")?.value.trim() || null,
        initial_balance: parseFloat(document.getElementById("np-party-balance")?.value) || 0
    };

    try {
        const res = await fetch("/api/parties/", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            document.getElementById("new-party-modal").classList.add("hidden");
            loadParties();
            if (window.showToast) showToast(`Party '${name}' added successfully!`, "success");
        } else {
            const errData = await res.json().catch(() => ({}));
            if (window.showToast) showToast(errData.detail || "Failed to create party", "error");
        }
    } catch (err) {
        console.error(err);
    }
}


function recordPartyPaymentModal(partyId, partyName, currentBalance) {
    activePartyPaymentId = partyId;
    const title = document.getElementById("party-pay-title");
    const balInfo = document.getElementById("party-pay-balance-info");
    
    if (title) title.innerHTML = `<i data-lucide="banknote" class="w-4 h-4 text-emerald-700"></i> ${partyName}`;
    if (balInfo) {
        const isRecv = currentBalance > 0;
        balInfo.innerHTML = `<strong>Current Balance:</strong> â‚¹ ${Math.abs(currentBalance).toFixed(2)} ${isRecv ? '(Receivable / à®µà®° à®µà¯‡à®£à¯à®Ÿà®¿à®¯à®¤à¯)' : '(Payable / à®¤à®° à®µà¯‡à®£à¯à®Ÿà®¿à®¯à®¤à¯)'}`;
    }
    
    document.getElementById("party-pay-amount").value = Math.abs(currentBalance) > 0 ? Math.abs(currentBalance) : "";
    document.getElementById("party-payment-modal").classList.remove("hidden");
    if (window.lucide) lucide.createIcons();
}

async function submitPartyPayment() {
    if (!activePartyPaymentId) return;
    const amount = parseFloat(document.getElementById("party-pay-amount")?.value) || 0;
    if (amount <= 0) {
        if (window.showToast) showToast("Please enter a valid amount!", "warning");
        return;
    }

    const direction = document.querySelector('input[name="pay-direction"]:checked')?.value || "INWARD";
    const payload = {
        direction: direction,
        amount: amount,
        payment_mode: document.getElementById("party-pay-mode")?.value || "CASH",
        reference_no: document.getElementById("party-pay-ref")?.value.trim() || null,
        notes: document.getElementById("party-pay-notes")?.value.trim() || null
    };

    try {
        const res = await fetch(`/api/parties/${activePartyPaymentId}/payments`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            document.getElementById("party-payment-modal").classList.add("hidden");
            loadParties();
            if (window.showToast) showToast(`Payment of â‚¹ ${amount.toFixed(2)} recorded successfully!`, "success");
        } else {
            if (window.showToast) showToast("Failed to record payment", "error");
        }
    } catch (err) {
        console.error(err);
    }
}

// â”€â”€ 5. INVENTORY, VALUATION & ADJUSTMENTS â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

async function loadInventoryValuation() {
    try {
        const res = await apiFetch("/api/inventory/valuation");
        const val = await res.json();

        document.getElementById("inv-total-cost").innerText = `â‚¹ ${val.total_cost_valuation.toLocaleString('en-IN')}`;
        document.getElementById("inv-total-retail").innerText = `â‚¹ ${val.total_retail_valuation.toLocaleString('en-IN')}`;
        document.getElementById("inv-total-count").innerText = val.total_products;

        const lowCount = val.items.filter(it => it.is_low_stock).length;
        document.getElementById("inv-low-count").innerText = `${lowCount} Items`;
    } catch (err) {
        console.error("Error loading valuation:", err);
    }
}

function renderInventoryTable() {
    const tbody = document.getElementById("inventory-table-body");
    if (!tbody) return;
    tbody.innerHTML = "";

    allProducts.forEach(p => {
        const isLow = p.current_stock <= p.min_stock_alert;
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td class="p-3">
                <div class="font-bold text-slate-800 text-xs">${p.product_name}</div>
                ${p.tamil_name ? `<div class="text-[11px] text-emerald-700 font-tamil">${p.tamil_name}</div>` : ''}
            </td>
            <td class="p-3 text-xs"><span class="badge ${p.category === 'FINISHED_GOOD' ? 'badge-finished' : (p.category === 'BY_PRODUCT' ? 'badge-byproduct' : 'badge-raw')}">${p.category}</span></td>
            <td class="p-3 text-xs font-mono">${p.hsn_code || '-'}</td>
            <td class="p-3 text-right font-black ${isLow ? 'text-rose-600' : 'text-slate-800'} text-xs">${p.current_stock} ${p.unit_of_measure}</td>
            <td class="p-3 text-right text-xs">â‚¹ ${p.cost_price.toFixed(2)}</td>
            <td class="p-3 text-right font-bold text-emerald-800 text-xs">â‚¹ ${p.selling_price.toFixed(2)}</td>
            <td class="p-3 text-right text-xs">${p.tax_rate}%</td>
            <td class="p-3 text-right">
                ${isLow ? '<span class="badge badge-low">Low Stock</span>' : '<span class="badge badge-instock">In Stock</span>'}
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function populateProductDropdowns() {
    const adjSelect = document.getElementById("adj-product");
    const ledgerSelect = document.getElementById("ledger-product-filter");
    if (adjSelect) {
        adjSelect.innerHTML = allProducts.map(p => `<option value="${p.id}">${p.product_name} (Current: ${p.current_stock} ${p.unit_of_measure})</option>`).join("");
    }
    if (ledgerSelect) {
        ledgerSelect.innerHTML = `<option value="">â€” All Products â€”</option>` + allProducts.map(p => `<option value="${p.id}">${p.product_name}</option>`).join("");
    }
}

function openStockAdjModal() {
    populateProductDropdowns();
    document.getElementById("stock-adj-modal").classList.remove("hidden");
    if (window.lucide) lucide.createIcons();
}

async function submitStockAdj() {
    const pid = parseInt(document.getElementById("adj-product")?.value);
    const qty = parseFloat(document.getElementById("adj-qty")?.value) || 0;
    const type = document.getElementById("adj-type")?.value;
    const remarks = document.getElementById("adj-remarks")?.value.trim();
    const ceoPassword = document.getElementById("adj-ceo-password")?.value.trim();

    if (!pid || qty <= 0) {
        if (window.showToast) showToast("Please enter a valid quantity! (à®šà®°à®¿à®¯à®¾à®© à®…à®³à®µà¯ˆ à®‰à®³à¯à®³à®¿à®Ÿà¯à®•)", "warning");
        return;
    }

    if (!ceoPassword) {
        if (window.showToast) showToast("ðŸ‘‘ CEO Master Password required to approve stock! (à®®à¯à®¤à®©à¯à®®à¯ˆ à®¨à®¿à®°à¯à®µà®¾à®•à®¿ à®•à®Ÿà®µà¯à®šà¯à®šà¯Šà®²à¯ à®¤à¯‡à®µà¯ˆ)", "warning");
        document.getElementById("adj-ceo-password")?.focus();
        return;
    }

    try {
        const headers = { 
            "Content-Type": "application/json",
            "X-CEO-Confirm": ceoPassword,
            "X-CEO-Password": ceoPassword
        };
        if (authToken) headers["Authorization"] = `Bearer ${authToken}`;

        const res = await fetch("/api/inventory/adjust", {
            method: "POST",
            headers: headers,
            body: JSON.stringify({
                product_id: pid,
                quantity: qty,
                movement_type: type,
                remarks: remarks || "CEO Approved Stock Inward"
            })
        });

        if (res.ok) {
            document.getElementById("stock-adj-modal").classList.add("hidden");
            if (document.getElementById("adj-ceo-password")) document.getElementById("adj-ceo-password").value = "";
            loadProducts();
            loadInventoryValuation();
            if (window.showToast) showToast("ðŸ‘‘ CEO Stock Inward / Adjustment Approved Successfully!", "success");
        } else {
            const errData = await res.json().catch(() => ({}));
            if (window.showToast) showToast(errData.detail || "CEO Authorization Failed. Invalid Master Password.", "error");
        }
    } catch (err) {
        console.error(err);
        if (window.showToast) showToast("Server connection error during stock update", "error");
    }
}


async function openStockLedgerModal() {
    populateProductDropdowns();
    document.getElementById("stock-ledger-modal").classList.remove("hidden");
    if (window.lucide) lucide.createIcons();
    await loadStockLedger();
}

async function loadStockLedger() {
    const pid = document.getElementById("ledger-product-filter")?.value;
    const url = pid ? `/api/inventory/ledger?product_id=${pid}&limit=100` : `/api/inventory/ledger?limit=100`;
    try {
        const res = await apiFetch(url);
        const entries = await res.json();
        const tbody = document.getElementById("ledger-table-body");
        if (!tbody) return;
        tbody.innerHTML = "";

        entries.forEach(e => {
            const tr = document.createElement("tr");
            const isPos = e.quantity_change > 0;
            tr.innerHTML = `
                <td class="p-2.5 text-xs text-slate-500">${new Date(e.timestamp).toLocaleString('en-IN')}</td>
                <td class="p-2.5 text-xs font-bold text-slate-800">${e.product_name}</td>
                <td class="p-2.5 text-xs"><span class="badge ${isPos ? 'badge-finished' : 'badge-raw'}">${e.movement_type}</span></td>
                <td class="p-2.5 text-right font-bold ${isPos ? 'text-emerald-700' : 'text-rose-600'} text-xs">${isPos ? '+' : ''}${e.quantity_change}</td>
                <td class="p-2.5 text-right font-black text-slate-800 text-xs">${e.balance_after}</td>
                <td class="p-2.5 text-xs text-slate-500">${e.remarks || e.reference_id || '-'}</td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {
        console.error(err);
    }
}

async function showLowStockPanel() {
    try {
        const res = await apiFetch("/api/inventory/low-stock");
        const items = await res.json();
        const tbody = document.getElementById("low-stock-table");
        if (!tbody) return;
        tbody.innerHTML = "";

        if (items.length === 0) {
            tbody.innerHTML = `<tr><td colspan="4" class="p-6 text-center text-xs text-slate-400">All products are healthy above reorder level!</td></tr>`;
        } else {
            items.forEach(it => {
                const tr = document.createElement("tr");
                tr.innerHTML = `
                    <td class="p-3 font-bold text-xs text-slate-800">${it.product_name}</td>
                    <td class="p-3 text-xs"><span class="badge badge-raw">${it.category}</span></td>
                    <td class="p-3 text-right font-black text-rose-600 text-xs">${it.current_stock} ${it.unit}</td>
                    <td class="p-3 text-right text-xs text-slate-500">${it.min_stock_alert} ${it.unit}</td>
                `;
                tbody.appendChild(tr);
            });
        }
        document.getElementById("low-stock-modal").classList.remove("hidden");
        if (window.lucide) lucide.createIcons();
    } catch (err) {
        console.error(err);
    }
}

function openNewProductModal() {
    document.getElementById("new-product-modal").classList.remove("hidden");
    if (window.lucide) lucide.createIcons();
}

// â”€â”€ REAL-TIME TAMIL AUTO-TRANSLATION & TRANSLITERATION â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
let _translateTimeout = null;

async function translateEnglishToTamil(text) {
function sanitizeNumericInput(el) {
    if (!el) return;
    let val = el.value.trim();
    if (!val) return;

    // Allow only digits and a single decimal point
    val = val.replace(/[^0-9.]/g, '');
    const parts = val.split('.');
    if (parts.length > 2) {
        val = parts[0] + '.' + parts.slice(1).join('');
    }

    // Transform leading zeros: e.g. '0270' -> '270', '050' -> '50', '.5' -> '0.5'
    if (val.startsWith('.')) {
        val = '0' + val;
    } else if (/^0[0-9]/.test(val)) {
        val = val.replace(/^0+/, '');
        if (val === '' || val.startsWith('.')) val = '0' + val;
    }
    
    el.value = val;
    el.style.borderColor = '#16a34a';
}

function validateTextInput(el) {
    if (!el) return true;
    const val = el.value.trim();
    const errBox = document.getElementById("np-validation-error");
    
    if (val.length > 0 && val.length < 3) {
        if (errBox) {
            errBox.innerText = "Product name must have at least 3 characters.";
            errBox.classList.remove("hidden");
        }
        el.style.borderColor = '#dc2626';
        return false;
    }
    
    // Repetitive spam letters check (e.g. 'aaaaaa', '11111')
    const cleanNoSpace = val.replace(/\s+/g, '');
    if (cleanNoSpace.length >= 3 && new Set(cleanNoSpace).size <= 1) {
        if (errBox) {
            errBox.innerText = "Invalid product name: cannot contain repeated identical characters.";
            errBox.classList.remove("hidden");
        }
        el.style.borderColor = '#dc2626';
        return false;
    }

    if (errBox) errBox.classList.add("hidden");
    el.style.borderColor = '#16a34a';
    return true;
}

let _nameTranslateTimer = null;
function onProductNameInput(el) {
    validateTextInput(el);
    const tamInput = document.getElementById("np-tamil");
    const statusBadge = document.getElementById("np-translate-status");
    if (!el || !tamInput) return;

    const val = el.value.trim();
    if (!val || val.length < 2) {
        if (statusBadge) statusBadge.style.display = "none";
        return;
    }

    if (statusBadge) statusBadge.style.display = "inline";

    clearTimeout(_nameTranslateTimer);
    _nameTranslateTimer = setTimeout(async () => {
        try {
            const res = await fetch(`/api/translate/tamil?text=${encodeURIComponent(val)}`);
            if (res.ok) {
                const data = await res.json();
                if (data && data.translated_text) {
                    tamInput.value = data.translated_text;
                }
            }
        } catch (e) {
            console.warn("Translation failed", e);
        } finally {
            if (statusBadge) statusBadge.style.display = "none";
        }
    }, 400);
}

async function submitNewProduct() {
    const errBox = document.getElementById("np-validation-error");
    if (errBox) errBox.classList.add("hidden");

    const nameEl = document.getElementById("np-name");
    const name = nameEl?.value.trim();
    if (!name || !validateTextInput(nameEl)) {
        if (errBox) {
            errBox.innerText = "Please enter a valid product name (minimum 3 characters, no repetitive letters).";
            errBox.classList.remove("hidden");
        }
        nameEl?.focus();
        return;
    }

    const price = parseFloat(document.getElementById("np-price")?.value) || 0;
    if (price <= 0) {
        if (errBox) {
            errBox.innerText = "Price Target must be a valid positive amount in â‚¹.";
            errBox.classList.remove("hidden");
        }
        document.getElementById("np-price")?.focus();
        return;
    }

    const initStock = parseFloat(document.getElementById("np-initstk")?.value) || 0;
    const minStock = parseFloat(document.getElementById("np-minstk")?.value) || 10;
    const ceoPassword = document.getElementById("np-ceo-password")?.value.trim();

    if (!ceoPassword) {
        if (errBox) {
            errBox.innerText = "ðŸ‘‘ CEO Master Password is required to authorize adding new products to inventory!";
            errBox.classList.remove("hidden");
        }
        document.getElementById("np-ceo-password")?.focus();
        return;
    }

    const unit = document.getElementById("np-unit")?.value || 'KG';
    let category = 'FINISHED_GOOD';
    if (unit === 'LITRE') category = 'BY_PRODUCT';

    const payload = {
        product_name: name,
        tamil_name: document.getElementById("np-tamil")?.value.trim() || name,
        category: category,
        unit_of_measure: unit,
        hsn_code: null,
        tax_rate: 0,
        cost_price: price,
        selling_price: price,
        wholesale_price: price,
        min_stock_alert: minStock,
        initial_stock: initStock
    };

    try {
        const headers = { 
            "Content-Type": "application/json",
            "X-CEO-Confirm": ceoPassword,
            "X-CEO-Password": ceoPassword
        };
        if (authToken) headers["Authorization"] = `Bearer ${authToken}`;

        const res = await fetch("/api/products/", {
            method: "POST",
            headers: headers,
            body: JSON.stringify(payload)
        });

        const data = await res.json().catch(() => ({}));
        if (res.ok) {
            document.getElementById("new-product-modal").classList.add("hidden");
            if (document.getElementById("np-ceo-password")) document.getElementById("np-ceo-password").value = "";
            if (document.getElementById("np-name")) document.getElementById("np-name").value = "";
            if (document.getElementById("np-tamil")) document.getElementById("np-tamil").value = "";
            if (document.getElementById("np-price")) document.getElementById("np-price").value = "";
            if (document.getElementById("np-initstk")) document.getElementById("np-initstk").value = "";
            
            loadProducts();
            loadInventoryValuation();
            if (window.showToast) showToast(`Product '${data.product_name || name}' added to inventory successfully!`, "success");
            alert(`âœ… Product '${data.product_name || name}' successfully authorized by CEO and added to inventory!`);
        } else {
            const msg = data.detail || "Failed to create product. Check CEO credentials.";
            if (errBox) {
                errBox.innerText = msg;
                errBox.classList.remove("hidden");
            }
            if (window.showToast) showToast(msg, "error");
        }
    } catch (err) {
        console.error(err);
        if (errBox) {
            errBox.innerText = "Error connecting to server.";
            errBox.classList.remove("hidden");
        }
        if (window.showToast) showToast("Error connecting to server", "error");
    }
}


// â”€â”€ 6. PRODUCTION & BY-PRODUCT RECOVERY â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

function addBatchInputRow() {
    const container = document.getElementById("batch-inputs-list");
    if (!container) return;
    const div = document.createElement("div");
    div.className = "flex items-center gap-2 bg-white p-2 rounded-lg border border-amber-200 text-xs";
    
    let options = allProducts.map(p => `<option value="${p.id}">${p.product_name} (${p.unit_of_measure})</option>`).join("");
    div.innerHTML = `
        <select class="batch-in-prod form-select p-1 text-xs" style="flex:1">${options}</select>
        <input type="number" placeholder="Qty (KG)" step="any" class="batch-in-qty form-input p-1 text-right font-bold text-xs" style="width:90px">
        <button onclick="this.parentElement.remove()" class="text-red-500 hover:text-red-700 font-bold px-1">âœ•</button>
    `;
    container.appendChild(div);
}

function addBatchOutputRow() {
    const container = document.getElementById("batch-outputs-list");
    if (!container) return;
    const div = document.createElement("div");
    div.className = "flex items-center gap-2 bg-white p-2 rounded-lg border border-emerald-200 text-xs";
    
    let options = allProducts.map(p => `<option value="${p.id}">${p.product_name} (${p.unit_of_measure})</option>`).join("");
    div.innerHTML = `
        <select class="batch-out-prod form-select p-1 text-xs" style="flex:1">${options}</select>
        <input type="number" placeholder="Qty Output" step="any" class="batch-out-qty form-input p-1 text-right font-bold text-xs" style="width:90px">
        <button onclick="this.parentElement.remove()" class="text-red-500 hover:text-red-700 font-bold px-1">âœ•</button>
    `;
    container.appendChild(div);
}

async function executeBatch() {
    const inRows = document.querySelectorAll("#batch-inputs-list > div");
    const outRows = document.querySelectorAll("#batch-outputs-list > div");

    const inputs = [];
    inRows.forEach(row => {
        const pid = parseInt(row.querySelector(".batch-in-prod").value);
        const qty = parseFloat(row.querySelector(".batch-in-qty").value) || 0;
        if (qty > 0) inputs.push({ product_id: pid, quantity: qty });
    });

    const outputs = [];
    outRows.forEach(row => {
        const pid = parseInt(row.querySelector(".batch-out-prod").value);
        const qty = parseFloat(row.querySelector(".batch-out-qty").value) || 0;
        if (qty > 0) outputs.push({ product_id: pid, quantity: qty });
    });

    if (inputs.length === 0 || outputs.length === 0) {
        if (window.showToast) showToast("Specify at least 1 consumed input and 1 produced output!", "warning");
        return;
    }

    const payload = {
        batch_date: new Date().toISOString().split("T")[0],
        process_type: document.getElementById("prod-process-type")?.value || "GRAIN_MILLING",
        labor_cost: parseFloat(document.getElementById("prod-labor-cost")?.value) || 0,
        electricity_cost: parseFloat(document.getElementById("prod-power-cost")?.value) || 0,
        inputs: inputs,
        outputs: outputs
    };

    try {
        const res = await fetch("/api/production/batches", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        if (!res.ok) {
            if (window.showToast) showToast("Error executing production batch!", "error");
            return;
        }

        const data = await res.json();
        if (window.showToast) showToast(`Batch ${data.batch_number} executed successfully! Stock updated.`, "success");
        loadProducts();
        loadBatchHistory();
    } catch (err) {
        console.error("Batch error:", err);
    }
}

async function loadBatchHistory() {
    try {
        const res = await fetch("/api/production/batches");
        const batches = await res.json();
        const tbody = document.getElementById("batch-history-table");
        const bytbody = document.getElementById("byproduct-recovery-table");
        if (tbody) tbody.innerHTML = "";
        if (bytbody) bytbody.innerHTML = "";

        batches.forEach(b => {
            if (tbody) {
                const tr = document.createElement("tr");
                tr.innerHTML = `
                    <td class="p-3 font-bold text-emerald-800 text-xs">${b.batch_number}</td>
                    <td class="p-3 text-xs text-slate-500">${b.batch_date}</td>
                    <td class="p-3 text-xs font-semibold">${b.process_type.replace('_', ' ')}</td>
                    <td class="p-3 text-right font-medium text-xs">${b.total_input_qty} KG</td>
                    <td class="p-3 text-right font-black text-emerald-700 text-xs">${b.total_output_qty}</td>
                    <td class="p-3 text-right font-bold text-xs">${b.recovery_percentage}%</td>
                    <td class="p-3 text-right font-medium text-xs">â‚¹ ${b.total_production_cost.toFixed(2)}</td>
                `;
                tbody.appendChild(tr);
            }

            if (bytbody && b.outputs) {
                b.outputs.filter(o => o.category === 'BY_PRODUCT').forEach(bp => {
                    const bptr = document.createElement("tr");
                    bptr.innerHTML = `
                        <td class="p-3 font-bold text-xs text-slate-800">${b.batch_number}</td>
                        <td class="p-3 text-xs font-semibold text-purple-800">${bp.product_name}</td>
                        <td class="p-3 text-right font-bold text-xs">${bp.quantity} ${bp.unit_of_measure}</td>
                        <td class="p-3 text-right font-bold text-xs text-emerald-700">${((bp.quantity / (b.total_input_qty || 1)) * 100).toFixed(1)}%</td>
                    `;
                    bytbody.appendChild(bptr);
                });
            }
        });
    } catch (err) {
        console.error("Error loading batches:", err);
    }
}

// â”€â”€ 7. PROCUREMENT / PURCHASES â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

function addPurchaseItemRow() {
    const container = document.getElementById("purchase-items-list");
    if (!container) return;
    const div = document.createElement("div");
    div.className = "flex items-center gap-2 bg-white p-2 rounded-lg border border-slate-200 text-xs";

    let options = allProducts.filter(p => p.category === 'RAW_MATERIAL').map(p => `<option value="${p.id}">${p.product_name}</option>`).join("");
    if (!options) options = allProducts.map(p => `<option value="${p.id}">${p.product_name}</option>`).join("");

    div.innerHTML = `
        <select class="pur-in-prod form-select p-1 text-xs" style="flex:1">${options}</select>
        <input type="number" placeholder="Qty (KG)" step="any" class="pur-in-qty form-input p-1 text-right font-bold text-xs" style="width:90px">
        <input type="number" placeholder="Rate/KG (â‚¹)" step="any" class="pur-in-price form-input p-1 text-right font-bold text-xs" style="width:90px">
        <button onclick="this.parentElement.remove()" class="text-red-500 hover:text-red-700 font-bold px-1">âœ•</button>
    `;
    container.appendChild(div);
}

async function savePurchaseEntry() {
    const supplierName = document.getElementById("pur-supplier-name")?.value.trim();
    if (!supplierName) {
        if (window.showToast) showToast("Please enter Farmer / Supplier Name!", "warning");
        return;
    }

    const itemRows = document.querySelectorAll("#purchase-items-list > div");
    const items = [];
    itemRows.forEach(row => {
        const pid = parseInt(row.querySelector(".pur-in-prod").value);
        const qty = parseFloat(row.querySelector(".pur-in-qty").value) || 0;
        const price = parseFloat(row.querySelector(".pur-in-price").value) || 0;
        if (qty > 0 && price > 0) {
            items.push({ product_id: pid, quantity: qty, unit_price: price });
        }
    });

    if (items.length === 0) {
        if (window.showToast) showToast("Add at least 1 inward raw grain item!", "warning");
        return;
    }

    const payload = {
        purchase_date: new Date().toISOString().split("T")[0],
        supplier_name: supplierName,
        vehicle_number: document.getElementById("pur-vehicle-no")?.value.trim() || null,
        freight_charges: parseFloat(document.getElementById("pur-freight")?.value) || 0,
        paid_amount: parseFloat(document.getElementById("pur-paid")?.value) || 0,
        items: items
    };

    try {
        const res = await apiFetch("/api/purchases/", {
            method: "POST",
            body: JSON.stringify(payload)
        });

        if (!res.ok) {
            if (window.showToast) showToast("Error saving procurement inward!", "error");
            return;
        }

        const data = await res.json();
        if (window.showToast) showToast(`Inward ${data.purchase_number} saved & raw stock incremented!`, "success");
        document.getElementById("pur-supplier-name").value = "";
        document.getElementById("pur-vehicle-no").value = "";
        loadPurchases();
        loadProducts();
    } catch (err) {
        console.error("Purchase save error:", err);
    }
}

async function loadPurchases() {
    try {
        const res = await apiFetch("/api/purchases/");
        const purchases = await res.json();
        const tbody = document.getElementById("purchase-history-table");
        if (!tbody) return;
        tbody.innerHTML = "";

        purchases.forEach(p => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td class="p-3 font-bold text-emerald-800 text-xs">${p.purchase_number}</td>
                <td class="p-3 text-xs text-slate-500">${p.purchase_date}</td>
                <td class="p-3 text-xs font-semibold">${p.supplier_name}</td>
                <td class="p-3 text-xs font-mono">${p.vehicle_number || '-'}</td>
                <td class="p-3 text-right text-xs">â‚¹ ${p.subtotal.toFixed(2)}</td>
                <td class="p-3 text-right font-black text-xs">â‚¹ ${p.grand_total.toFixed(2)}</td>
                <td class="p-3 text-right text-emerald-700 font-bold text-xs">â‚¹ ${p.paid_amount.toFixed(2)}</td>
                <td class="p-3 text-right ${p.balance_due > 0 ? 'text-rose-600 font-bold' : 'text-slate-600'} text-xs">â‚¹ ${p.balance_due.toFixed(2)}</td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {
        console.error("Error loading purchases:", err);
    }
}

// â”€â”€ 8. EXPENSES â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

async function loadExpenses() {
    try {
        const res = await apiFetch("/api/expenses/");
        const expenses = await res.json();
        const tbody = document.getElementById("expenses-table-body");
        const badge = document.getElementById("exp-total-badge");
        if (!tbody) return;
        tbody.innerHTML = "";

        let total = 0;
        expenses.forEach(e => {
            total += e.amount;
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td class="p-3 text-xs text-slate-500">${e.expense_date}</td>
                <td class="p-3 font-semibold text-xs text-slate-800">${e.category}</td>
                <td class="p-3 text-xs">${e.paid_to || '-'}</td>
                <td class="p-3 text-xs text-slate-400">${e.notes || '-'}</td>
                <td class="p-3 text-right font-bold text-rose-600 text-xs">â‚¹ ${e.amount.toFixed(2)}</td>
            `;
            tbody.appendChild(tr);
        });

        if (badge) badge.innerText = `Total: â‚¹ ${total.toLocaleString('en-IN')}`;
    } catch (err) {
        console.error("Error loading expenses:", err);
    }
}

async function saveExpense() {
    const amount = parseFloat(document.getElementById("exp-amount")?.value) || 0;
    if (amount <= 0) {
        if (window.showToast) showToast("Please enter a valid expense amount!", "warning");
        return;
    }

    const payload = {
        expense_date: new Date().toISOString().split("T")[0],
        category: document.getElementById("exp-cat")?.value || "OTHER",
        amount: amount,
        paid_to: document.getElementById("exp-paid-to")?.value.trim() || null,
        notes: document.getElementById("exp-notes")?.value.trim() || null
    };

    try {
        const res = await apiFetch("/api/expenses/", {
            method: "POST",
            body: JSON.stringify(payload)
        });

        if (!res.ok) {
            if (window.showToast) showToast("Error adding expense!", "error");
            return;
        }

        document.getElementById("exp-amount").value = "";
        document.getElementById("exp-paid-to").value = "";
        document.getElementById("exp-notes").value = "";
        loadExpenses();
        if (window.showToast) showToast(`Expense of â‚¹ ${amount.toFixed(2)} recorded!`, "success");
    } catch (err) {
        console.error("Save expense error:", err);
    }
}

// â”€â”€ 9. FINANCIAL REPORTS & AUDIT â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

async function loadReports() {
    const startDate = document.getElementById("rep-start-date")?.value;
    const endDate = document.getElementById("rep-end-date")?.value;
    const params = new URLSearchParams();
    if (startDate) params.append("start_date", startDate);
    if (endDate) params.append("end_date", endDate);

    try {
        const resSum = await fetch(`/api/reports/financial-summary?${params.toString()}`);
        const sum = await resSum.json();

        document.getElementById("rep-total-sales").innerText = `â‚¹ ${sum.sales_summary.gross_turnover.toLocaleString('en-IN')}`;
        document.getElementById("rep-total-purchases").innerText = `â‚¹ ${sum.purchases_summary.gross_purchases.toLocaleString('en-IN')}`;
        document.getElementById("rep-total-expenses").innerText = `â‚¹ ${sum.expense_summary.total_expenses.toLocaleString('en-IN')}`;
        document.getElementById("rep-net-profit").innerText = `â‚¹ ${sum.profit_and_loss.net_profit.toLocaleString('en-IN')}`;

        // Income Statement Rows
        const plInc = document.getElementById("pl-income-rows");
        if (plInc) {
            plInc.innerHTML = `
                <tr class="border-b"><td class="py-2 text-slate-600">Gross Sales Revenue:</td><td class="py-2 text-right font-bold text-slate-800">â‚¹ ${sum.sales_summary.gross_turnover.toLocaleString('en-IN')}</td></tr>
                <tr class="border-b"><td class="py-2 text-slate-600">Total GST Collected (Output):</td><td class="py-2 text-right font-semibold text-emerald-700">â‚¹ ${sum.sales_summary.total_tax_collected.toLocaleString('en-IN')}</td></tr>
                <tr class="border-b"><td class="py-2 text-slate-600">Raw Material COGS (Purchases):</td><td class="py-2 text-right font-bold text-amber-800">- â‚¹ ${sum.purchases_summary.gross_purchases.toLocaleString('en-IN')}</td></tr>
                <tr class="border-b bg-emerald-50"><td class="py-2 font-bold text-emerald-900">Gross Margin:</td><td class="py-2 text-right font-black text-emerald-800">â‚¹ ${sum.profit_and_loss.gross_profit.toLocaleString('en-IN')}</td></tr>
            `;
        }

        // Expense Rows
        const plExp = document.getElementById("pl-expense-rows");
        if (plExp) {
            let catRows = '';
            for (const [k, v] of Object.entries(sum.expense_summary.by_category || {})) {
                catRows += `<tr class="border-b"><td class="py-1.5 text-slate-600">${k}:</td><td class="py-1.5 text-right font-semibold">â‚¹ ${v.toLocaleString('en-IN')}</td></tr>`;
            }
            plExp.innerHTML = catRows + `
                <tr class="border-b bg-rose-50"><td class="py-2 font-bold text-rose-900">Total Operating Expenses:</td><td class="py-2 text-right font-black text-rose-700">- â‚¹ ${sum.expense_summary.total_expenses.toLocaleString('en-IN')}</td></tr>
                <tr class="bg-emerald-100"><td class="py-2.5 font-black text-emerald-900 text-sm">Net Profit (EBIT):</td><td class="py-2.5 text-right font-black text-emerald-900 text-sm">â‚¹ ${sum.profit_and_loss.net_profit.toLocaleString('en-IN')}</td></tr>
            `;
        }

        // Multi-year
        const resHist = await apiFetch("/api/reports/multi-year-history");
        const hist = await resHist.json();
        const tbody = document.getElementById("multi-year-table-body");
        if (tbody) {
            tbody.innerHTML = "";
            hist.forEach(h => {
                const tr = document.createElement("tr");
                tr.innerHTML = `
                    <td class="p-3 font-bold text-slate-900 text-xs">${h.year} - ${h.year + 1}</td>
                    <td class="p-3 text-right text-xs">${h.invoices_count}</td>
                    <td class="p-3 text-right font-bold text-xs">â‚¹ ${h.sales_turnover.toLocaleString('en-IN')}</td>
                    <td class="p-3 text-right text-xs">â‚¹ ${h.purchase_total.toLocaleString('en-IN')}</td>
                    <td class="p-3 text-right text-xs">â‚¹ ${h.expenses_total.toLocaleString('en-IN')}</td>
                    <td class="p-3 text-right font-black text-emerald-700 text-xs">â‚¹ ${h.net_profit.toLocaleString('en-IN')}</td>
                `;
                tbody.appendChild(tr);
            });
        }

        // Populate other report tabs
        loadSalesRegisterReport();
        loadStockValReport();
        loadPartyBalanceReport();
    } catch (err) {
        console.error("Error loading reports:", err);
    }
}

function switchReportTab(tab) {
    document.querySelectorAll(".report-tab").forEach(t => t.classList.remove("active"));
    document.querySelectorAll(".report-tab-panel").forEach(p => p.classList.add("hidden"));

    const btn = document.getElementById(`rtab-${tab}`);
    const panel = document.getElementById(`rtab-panel-${tab}`);
    if (btn) btn.classList.add("active");
    if (panel) panel.classList.remove("hidden");

    if (tab === 'gst') loadGstReport();
}

async function loadSalesRegisterReport() {
    try {
        const res = await apiFetch("/api/billing/invoices");
        const invs = await res.json();
        const tbody = document.getElementById("sales-register-table");
        if (!tbody) return;
        tbody.innerHTML = "";

        invs.forEach(inv => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td class="p-2.5 font-bold text-emerald-800 text-xs">${inv.invoice_number}</td>
                <td class="p-2.5 text-xs text-slate-500">${inv.invoice_date}</td>
                <td class="p-2.5 text-xs font-semibold">${inv.customer_name}</td>
                <td class="p-2.5 text-xs"><span class="badge ${inv.billing_type === 'WHOLESALE_B2B' ? 'badge-finished' : 'badge-pkg'}">${inv.billing_type}</span></td>
                <td class="p-2.5 text-right text-xs">â‚¹ ${inv.taxable_amount.toFixed(2)}</td>
                <td class="p-2.5 text-right font-semibold text-emerald-700 text-xs">â‚¹ ${inv.total_tax.toFixed(2)}</td>
                <td class="p-2.5 text-right font-black text-slate-900 text-xs">â‚¹ ${inv.grand_total.toFixed(2)}</td>
                <td class="p-2.5 text-xs">${inv.payment_mode}</td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {}
}

async function loadStockValReport() {
    try {
        const res = await apiFetch("/api/inventory/valuation");
        const val = await res.json();
        const tbody = document.getElementById("stock-val-table");
        if (!tbody) return;
        tbody.innerHTML = "";

        val.items.forEach(it => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td class="p-2.5 font-bold text-slate-800 text-xs">${it.product_name}</td>
                <td class="p-2.5 text-xs"><span class="badge badge-finished">${it.category}</span></td>
                <td class="p-2.5 text-xs font-mono">${it.hsn_code || '-'}</td>
                <td class="p-2.5 text-right font-bold text-xs">${it.stock_qty}</td>
                <td class="p-2.5 text-right text-xs">â‚¹ ${it.cost_price.toFixed(2)}</td>
                <td class="p-2.5 text-right font-bold text-slate-800 text-xs">â‚¹ ${it.cost_value.toFixed(2)}</td>
                <td class="p-2.5 text-right font-black text-emerald-800 text-xs">â‚¹ ${it.retail_value.toFixed(2)}</td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {}
}

async function loadPartyBalanceReport() {
    const tbody = document.getElementById("party-balance-table");
    if (!tbody) return;
    tbody.innerHTML = "";

    allParties.forEach(p => {
        const isRecv = p.outstanding_balance > 0;
        const isPay = p.outstanding_balance < 0;
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td class="p-2.5 font-bold text-slate-800 text-xs">${p.party_name}</td>
            <td class="p-2.5 text-xs">${p.party_type.replace('_', ' ')}</td>
            <td class="p-2.5 text-xs">${p.phone || '-'}</td>
            <td class="p-2.5 text-right font-black text-xs ${isRecv ? 'text-emerald-700' : (isPay ? 'text-rose-600' : 'text-slate-600')}">
                â‚¹ ${Math.abs(p.outstanding_balance).toFixed(2)}
            </td>
            <td class="p-2.5 text-xs">
                ${isRecv ? '<span class="badge badge-instock">Receivable (à®•à®Ÿà®©à¯ à®µà®°à®µà¯)</span>' : (isPay ? '<span class="badge badge-low">Payable (à®šà¯†à®²à¯à®¤à¯à®¤ à®µà¯‡à®£à¯à®Ÿà®¿à®¯à®¤à¯)</span>' : '<span class="badge badge-finished">Settled</span>')}
            </td>
        `;
        tbody.appendChild(tr);
    });
}

async function loadGstReport() {
    const month = parseInt(document.getElementById("gst-month")?.value) || new Date().getMonth() + 1;
    const year = parseInt(document.getElementById("gst-year")?.value) || new Date().getFullYear();
    const container = document.getElementById("gst-report-content");
    if (!container) return;

    try {
        const res = await apiFetch(`/api/reports/gst-filing?month=${month}&year=${year}`);
        const gst = await res.json();

        container.innerHTML = `
            <div class="grid-3 mb-4">
                <div class="card p-4">
                    <div class="text-xs text-slate-500 font-bold uppercase">Total Taxable Turnover</div>
                    <div class="text-xl font-black text-slate-900 mt-1">â‚¹ ${gst.taxable_sales.toLocaleString('en-IN')}</div>
                </div>
                <div class="card p-4">
                    <div class="text-xs text-slate-500 font-bold uppercase">Output GST Liability (GSTR-1)</div>
                    <div class="text-xl font-black text-emerald-700 mt-1">â‚¹ ${gst.total_output_gst.toLocaleString('en-IN')}</div>
                    <div class="text-[10px] text-slate-400 mt-1">CGST: â‚¹ ${gst.cgst.toLocaleString('en-IN')} | SGST: â‚¹ ${gst.sgst.toLocaleString('en-IN')}</div>
                </div>
                <div class="card p-4">
                    <div class="text-xs text-slate-500 font-bold uppercase">Total Invoices Filed</div>
                    <div class="text-xl font-black text-blue-700 mt-1">${gst.invoice_count} Bills</div>
                </div>
            </div>
            <div class="bg-amber-50 border border-amber-200 rounded-xl p-4 text-xs text-amber-900">
                <strong>GST Notice for MSME:</strong> Cattle Feed (HSN 2309) & Raw Millets are Exempted (0% GST). Cold-Pressed Oils (HSN 1515) & Oil Cakes (HSN 2306) are taxed at 5%. Use the <strong>"CA Audit Excel"</strong> button above to download full GSTR-1 & ITC schedules.
            </div>
        `;
    } catch (err) {
        console.error(err);
    }
}

// â”€â”€ 10. USER MANAGEMENT & PASSWORD MODALS â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

function openUserMgmtModal() {
    loadUserMgmtTable();
    document.getElementById("user-mgmt-modal").classList.remove("hidden");
    if (window.lucide) lucide.createIcons();
}

async function loadUserMgmtTable() {
    try {
        const res = await fetch("/api/auth/users", { headers: getAuthHeaders() });
        const users = await res.json();
        const tbody = document.getElementById("user-mgmt-table");
        if (!tbody) return;
        tbody.innerHTML = "";

        users.forEach(u => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td class="p-2.5 font-bold text-slate-800 text-xs">${u.full_name || u.username}</td>
                <td class="p-2.5 font-mono text-xs text-slate-500">${u.username}</td>
                <td class="p-2.5 text-xs"><span class="badge ${u.role === 'ADMIN' ? 'badge-finished' : 'badge-raw'}">${u.role}</span></td>
                <td class="p-2.5 text-center text-xs">${u.is_active ? '<span class="badge badge-instock">Active</span>' : '<span class="badge badge-low">Disabled</span>'}</td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {
        console.error(err);
    }
}

async function createNewUser() {
    const fullName = document.getElementById("nu-fullname")?.value.trim();
    const username = document.getElementById("nu-username")?.value.trim();
    const password = document.getElementById("nu-password")?.value;
    const role = document.getElementById("nu-role")?.value || "CASHIER";

    if (!fullName || !username || !password) {
        if (window.showToast) showToast("Fill all required fields!", "warning");
        return;
    }

    try {
        const res = await fetch("/api/auth/users", {
            method: "POST",
            headers: getAuthHeaders(),
            body: JSON.stringify({
                full_name: fullName,
                username: username,
                password: password,
                role: role
            })
        });

        if (res.ok) {
            document.getElementById("nu-fullname").value = "";
            document.getElementById("nu-username").value = "";
            document.getElementById("nu-password").value = "";
            loadUserMgmtTable();
            if (window.showToast) showToast(`User '${username}' created successfully!`, "success");
        } else {
            const err = await res.json();
            if (window.showToast) showToast(err.detail || "Failed to create user", "error");
        }
    } catch (err) {
        console.error(err);
    }
}

function openChangePasswordModal() {
    document.getElementById("cpwd-old").value = "";
    document.getElementById("cpwd-new").value = "";
    document.getElementById("cpwd-confirm").value = "";
    document.getElementById("cpwd-error").classList.add("hidden");
    document.getElementById("change-pwd-modal").classList.remove("hidden");
    if (window.lucide) lucide.createIcons();
}

async function submitChangePassword() {
    const oldPass = document.getElementById("cpwd-old")?.value;
    const newPass = document.getElementById("cpwd-new")?.value;
    const confPass = document.getElementById("cpwd-confirm")?.value;
    const errBox = document.getElementById("cpwd-error");

    if (!oldPass || !newPass || !confPass) {
        errBox.innerText = "All password fields are required";
        errBox.classList.remove("hidden");
        return;
    }
    if (newPass !== confPass) {
        errBox.innerText = "New passwords do not match";
        errBox.classList.remove("hidden");
        return;
    }
    if (newPass.length < 6) {
        errBox.innerText = "Password must be at least 6 characters";
        errBox.classList.remove("hidden");
        return;
    }

    try {
        const res = await fetch("/api/auth/change-password", {
            method: "POST",
            headers: getAuthHeaders(),
            body: JSON.stringify({
                old_password: oldPass,
                new_password: newPass
            })
        });

        const data = await res.json();
        if (res.ok) {
            document.getElementById("change-pwd-modal").classList.add("hidden");
            if (window.showToast) showToast("Password updated successfully!", "success");
            alert("Password changed successfully! Please use your new password next time you sign in.");
        } else {
            errBox.innerText = data.detail || "Failed to update password";
            errBox.classList.remove("hidden");
        }
    } catch (err) {
        errBox.innerText = "Connection error";
        errBox.classList.remove("hidden");
    }
}

async function downloadCaAuditExcel() {
    try {
        const start = document.getElementById("rep-start-date")?.value || "";
        const end = document.getElementById("rep-end-date")?.value || "";
        let url = "/api/reports/export-excel";
        const params = [];
        if (start) params.push(`start_date=${start}`);
        if (end) params.push(`end_date=${end}`);
        if (params.length) url += `?${params.join("&")}`;

        if (window.showToast) showToast("Generating CA Audit Excel...", "info");
        const res = await fetch(url, { headers: getAuthHeaders() });
        if (!res.ok) throw new Error("Export failed");

        const blob = await res.blob();
        const downloadUrl = window.URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = downloadUrl;
        a.download = `RKG_CA_Audit_${new Date().toISOString().slice(0, 10)}.xlsx`;
        document.body.appendChild(a);
        a.click();
        a.remove();
        if (window.showToast) showToast("CA Audit Excel downloaded successfully!", "success");
    } catch (err) {
        if (window.showToast) showToast("Failed to download CA Audit Excel", "error");
        else alert("Failed to download CA Audit Excel");
    }
}

// â”€â”€ POS Cashier Keyboard Shortcuts â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
window.addEventListener("keydown", (e) => {
    // Only trigger if POS section is active and no modal is open
    const posSection = document.getElementById("section-pos");
    const activeModal = document.querySelector(".modal-backdrop:not(.hidden)");
    if (!posSection || !posSection.classList.contains("active") || activeModal) return;

    if (e.key === "F2") {
        e.preventDefault();
        const s = document.getElementById("pos-search");
        if (s) { s.focus(); s.select(); }
    } else if (e.key === "F4") {
        e.preventDefault();
        const p = document.getElementById("pos-customer-phone");
        if (p) { p.focus(); p.select(); }
    } else if (e.key === "F8") {
        e.preventDefault();
        const paid = document.getElementById("cart-paid-amount");
        if (paid) { paid.focus(); paid.select(); }
    } else if (e.key === "F9") {
        e.preventDefault();
        checkoutBill('THERMAL');
    } else if (e.key === "Escape") {
        if (cart.length > 0) {
            if (confirm("Reset current bill / à®•à®¾à®²à®¿ à®šà¯†à®¯à¯?")) {
                clearPosCart();
            }
        } else {
            const s = document.getElementById("pos-search");
            if (s) { s.value = ""; filterPosProducts(); }
        }
    }
});



