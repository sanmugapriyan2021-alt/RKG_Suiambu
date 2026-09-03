/**
 * RKG SUIAMBU — CEO Master Control Panel Script
 * 100% Visual UI Controls — No JSON or Coding Required
 */

let ceoToken = sessionStorage.getItem("rkg_ceo_token") || "";
let pendingAction = null;
let allProductsCache = [];

document.addEventListener("DOMContentLoaded", () => {
    if (window.lucide) lucide.createIcons();
    if (ceoToken) {
        showDashboard();
    } else {
        showLogin();
    }
});

// ── Auth Handling ───────────────────────────────────────────────────
function showLogin() {
    document.getElementById("ceo-login-screen")?.classList.remove("hidden");
    document.getElementById("ceo-dashboard")?.classList.add("hidden");
}

function showDashboard() {
    document.getElementById("ceo-login-screen")?.classList.add("hidden");
    document.getElementById("ceo-dashboard")?.classList.remove("hidden");
    if (window.lucide) lucide.createIcons();
    
    // Load initial tab data
    loadCompanySettings();
    loadProducts();
    loadInquiries();
    loadUsers();
    loadAuditLogs();
}

async function handleCeoLogin(e) {
    e.preventDefault();
    const u = document.getElementById("ceo-input-username").value.trim();
    const p = document.getElementById("ceo-input-password").value;
    const errDiv = document.getElementById("ceo-login-error");
    const btn = document.getElementById("ceo-login-btn");

    btn.disabled = true;
    btn.innerHTML = `<span>🔄 Authenticating...</span>`;
    errDiv.classList.add("hidden");

    try {
        const res = await fetch("/api/ceo/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username: u, password: p })
        });
        const data = await res.json();
        if (res.ok && data.access_token) {
            ceoToken = data.access_token;
            sessionStorage.setItem("rkg_ceo_token", ceoToken);
            showDashboard();
        } else {
            errDiv.textContent = data.detail || "Invalid CEO credentials";
            errDiv.classList.remove("hidden");
        }
    } catch (err) {
        errDiv.textContent = "Server connection error. Please verify server is running.";
        errDiv.classList.remove("hidden");
    } finally {
        btn.disabled = false;
        btn.innerHTML = `<i data-lucide="shield-check" class="w-4 h-4"></i> Authenticate & Enter Master Control`;
        if (window.lucide) lucide.createIcons();
    }
}

function ceLogout() {
    ceoToken = "";
    sessionStorage.removeItem("rkg_ceo_token");
    showLogin();
}

function toggleCeoPassword() {
    const inp = document.getElementById("ceo-input-password");
    inp.type = inp.type === "password" ? "text" : "password";
}

// ── Tab Switching ───────────────────────────────────────────────────
function switchTab(tab) {
    const tabs = ['profile', 'products', 'stock', 'inquiries', 'users', 'audit'];
    tabs.forEach(t => {
        const btn = document.getElementById(`tab-${t}`);
        const sec = document.getElementById(`section-${t}`);
        if (btn) btn.classList.remove('active');
        if (sec) sec.classList.add('hidden');
    });

    const activeBtn = document.getElementById(`tab-${tab}`);
    const activeSec = document.getElementById(`section-${tab}`);
    if (activeBtn) activeBtn.classList.add('active');
    if (activeSec) activeSec.classList.remove('hidden');

    if (window.lucide) lucide.createIcons();
}


// ── TAB 1: Company Profile ──────────────────────────────────────────
async function loadCompanySettings() {
    try {
        const res = await fetch("/api/ceo/company-settings", {
            headers: { "Authorization": `Bearer ${ceoToken}` }
        });
        if (res.ok) {
            const d = await res.json();
            document.getElementById("cs-name").value = d.company_name || "";
            document.getElementById("cs-tamil").value = d.tamil_name || "";
            document.getElementById("cs-tagline").value = d.tagline || "";
            document.getElementById("cs-address").value = d.address || "";
            document.getElementById("cs-phone").value = d.phone || "";
            document.getElementById("cs-email").value = d.email || "";
            document.getElementById("cs-gstin").value = d.gstin || "";
            document.getElementById("cs-fssai").value = d.fssai || "";
            document.getElementById("cs-bank").value = d.bank_name || "";
            document.getElementById("cs-account").value = d.bank_account_no || "";
            document.getElementById("cs-ifsc").value = d.bank_ifsc || "";
            document.getElementById("cs-upi").value = d.upi_id || "";
        }
    } catch (e) {}
}

async function saveCompanySettings() {
    const payload = {
        company_name: document.getElementById("cs-name").value,
        tamil_name: document.getElementById("cs-tamil").value,
        tagline: document.getElementById("cs-tagline").value,
        address: document.getElementById("cs-address").value,
        phone: document.getElementById("cs-phone").value,
        email: document.getElementById("cs-email").value,
        gstin: document.getElementById("cs-gstin").value,
        fssai: document.getElementById("cs-fssai").value,
        bank_name: document.getElementById("cs-bank").value,
        bank_account_no: document.getElementById("cs-account").value,
        bank_ifsc: document.getElementById("cs-ifsc").value,
        upi_id: document.getElementById("cs-upi").value
    };

    openConfirmModal(
        "Save Company Profile",
        "Update the company name, phone, address, and bank details in database.",
        async (pw) => {
            const res = await fetch("/api/ceo/company-settings", {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${ceoToken}`,
                    "X-CEO-Confirm": pw
                },
                body: JSON.stringify(payload)
            });
            if (res.ok) {
                alert("Company Profile saved successfully!");
                closeConfirmModal();
                loadCompanySettings();
            } else {
                const err = await res.json();
                showConfirmError(err.detail || "Failed to save company settings.");
            }
        }
    );
}

// ── TAB 2: Products Master ──────────────────────────────────────────
async function loadProducts() {
    const tbody = document.getElementById("products-table-body");
    try {
        const res = await fetch("/api/products");
        if (res.ok) {
            allProductsCache = await res.json();
            renderProductsTable(allProductsCache);
        }
    } catch (e) {
        tbody.innerHTML = `<tr><td colspan="10" class="p-6 text-center text-red-400">Failed to load products</td></tr>`;
    }
}

function renderProductsTable(prods) {
    const tbody = document.getElementById("products-table-body");
    if (!prods.length) {
        tbody.innerHTML = `<tr><td colspan="10" class="p-6 text-center text-slate-500">No products found</td></tr>`;
        return;
    }

    tbody.innerHTML = prods.map(p => {
        const imgSrc = p.image_url || '/static/rkg-logo.png';
        return `
        <tr class="hover:bg-emerald-950/40 transition">
            <td class="p-3 font-bold text-white">
                <div class="flex items-center gap-2.5">
                    <img src="${imgSrc}" class="w-10 h-10 rounded-lg object-contain bg-slate-900 border border-emerald-800/60 p-0.5 flex-shrink-0" alt="${p.product_name}">
                    <div>
                        <div>${p.product_name}</div>
                        ${p.tamil_name ? `<div class="text-[11px] text-amber-300/80 font-tamil">${p.tamil_name}</div>` : ''}
                    </div>
                </div>
            </td>
            <td class="p-3 text-emerald-300 font-semibold">${p.category}</td>
            <td class="p-3 text-right font-mono text-slate-300">₹${p.cost_price?.toFixed(2)}</td>
            <td class="p-3 text-right font-mono font-bold text-amber-300">₹${p.selling_price?.toFixed(2)}</td>
            <td class="p-3 text-right font-mono font-bold text-emerald-400">₹${p.wholesale_price?.toFixed(2)}</td>
            <td class="p-3 text-right font-mono text-slate-300">${p.tax_rate}%</td>
            <td class="p-3 font-mono text-slate-400">${p.hsn_code || '-'}</td>
            <td class="p-3 text-right font-bold text-white">${p.current_stock} <span class="text-[10px] text-slate-400">${p.unit_of_measure}</span></td>
            <td class="p-3 text-center">
                <span class="px-2 py-0.5 rounded-full text-[10px] font-bold ${p.is_active ? 'bg-emerald-900 text-emerald-300 border border-emerald-600' : 'bg-red-950 text-red-300'}">
                    ${p.is_active ? 'Active' : 'Hidden'}
                </span>
            </td>
            <td class="p-3 text-center">
                <button onclick="openEditProductModal(${p.id})" class="px-2.5 py-1 rounded-lg bg-amber-500/20 hover:bg-amber-500 text-amber-300 hover:text-slate-950 font-bold text-xs transition border border-amber-400/40">
                    Edit Price ✏️
                </button>
            </td>
        </tr>
    `}).join('');

    // Also populate CEO Stock Inward dropdown & stock table
    const csProdSelect = document.getElementById("cs-product");
    if (csProdSelect) {
        csProdSelect.innerHTML = prods.map(p => 
            `<option value="${p.id}">${p.product_name} (${p.tamil_name || ''}) — Current: ${p.current_stock} ${p.unit_of_measure}</option>`
        ).join('');
    }

    const stockTbody = document.getElementById("stock-table-body");
    if (stockTbody) {
        stockTbody.innerHTML = prods.map(p => {
            const valuation = (p.current_stock * (p.cost_price || 0));
            const isLow = p.current_stock <= (p.min_stock_alert || 0);
            return `
            <tr class="hover:bg-emerald-950/40 transition">
                <td class="p-3 font-bold text-white">${p.product_name}</td>
                <td class="p-3 text-amber-300 font-tamil">${p.tamil_name || '-'}</td>
                <td class="p-3 text-emerald-300">${p.category}</td>
                <td class="p-3 text-right font-bold ${isLow ? 'text-rose-400' : 'text-emerald-400'} font-mono text-sm">
                    ${p.current_stock} <span class="text-[10px] text-slate-400">${p.unit_of_measure}</span>
                </td>
                <td class="p-3 text-right font-mono text-slate-300">₹${(p.cost_price || 0).toFixed(2)}</td>
                <td class="p-3 text-right font-mono font-black text-amber-300">₹${valuation.toLocaleString('en-IN', {minimumFractionDigits:2, maximumFractionDigits:2})}</td>
                <td class="p-3 text-center">
                    <span class="px-2 py-0.5 rounded-full text-[10px] font-bold ${isLow ? 'bg-rose-950 text-rose-300 border border-rose-800' : 'bg-emerald-950 text-emerald-300 border border-emerald-800'}">
                        ${isLow ? '⚠️ LOW STOCK' : '✅ HEALTHY'}
                    </span>
                </td>
            </tr>
            `;
        }).join('');
    }

    if (window.lucide) lucide.createIcons();
}

async function handleCeoStockInward(e) {
    e.preventDefault();
    const pid = parseInt(document.getElementById("cs-product").value);
    const qty = parseFloat(document.getElementById("cs-qty").value) || 0;
    const cost = parseFloat(document.getElementById("cs-cost").value) || null;
    const supplier = document.getElementById("cs-supplier").value.trim() || "Direct Factory Inward";
    const vehicle = document.getElementById("cs-vehicle").value.trim() || null;
    const notes = document.getElementById("cs-notes").value.trim() || null;
    const pw = document.getElementById("cs-ceo-password").value;
    const btn = document.getElementById("cs-submit-btn");

    if (!pid || qty <= 0) {
        alert("Please select a product and enter a valid quantity! (சரியான அளவை உள்ளிடுக)");
        return;
    }
    if (!pw) {
        alert("CEO Master Password is required to approve stock inward! (முதன்மை நிர்வாகி கடவுச்சொல் தேவை)");
        document.getElementById("cs-ceo-password").focus();
        return;
    }

    btn.disabled = true;
    btn.innerHTML = `<span>⏳ Inwarding Stock...</span>`;

    try {
        const res = await fetch("/api/ceo/stock/inward", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${ceoToken}`,
                "X-CEO-Confirm": pw
            },
            body: JSON.stringify({
                product_id: pid,
                quantity: qty,
                unit_cost: cost,
                supplier_name: supplier,
                vehicle_number: vehicle,
                notes: notes,
                reason: `CEO Approved Inward: +${qty} units from ${supplier}`
            })
        });

        const data = await res.json();
        if (res.ok) {
            alert(`🎉 Success!\n+${qty} units inwarded successfully into factory warehouse!\nNew Stock Balance: ${data.new_stock}`);
            document.getElementById("ceo-inward-form").reset();
            loadProducts();
            loadAuditLogs();
        } else {
            alert(`❌ Authorization Failed: ${data.detail || "Failed to inward stock."}`);
        }
    } catch (err) {
        alert("Server error connecting to stock API");
    } finally {
        btn.disabled = false;
        btn.innerHTML = `<i data-lucide="check-circle-2" class="w-4 h-4"></i> Approve & Inward Stock`;
        if (window.lucide) lucide.createIcons();
    }
}


function filterProductsTable() {
    const q = document.getElementById("product-search").value.toLowerCase();
    const filtered = allProductsCache.filter(p => 
        p.product_name.toLowerCase().includes(q) || 
        (p.tamil_name && p.tamil_name.toLowerCase().includes(q))
    );
    renderProductsTable(filtered);
}

function openEditProductModal(id) {
    const p = allProductsCache.find(x => x.id === id);
    if (!p) return;

    document.getElementById("edit-product-id").value = p.id;
    document.getElementById("ep-name").value = p.product_name || "";
    document.getElementById("ep-tamilname").value = p.tamil_name || "";
    document.getElementById("ep-sell").value = p.selling_price || 0;
    document.getElementById("ep-wholesale").value = p.wholesale_price || 0;
    document.getElementById("ep-cost").value = p.cost_price || 0;
    document.getElementById("ep-tax").value = p.tax_rate || 0;
    document.getElementById("ep-hsn").value = p.hsn_code || "";
    document.getElementById("ep-minstk").value = p.min_stock_alert || 10;
    document.getElementById("ep-ceo-password").value = "";
    document.getElementById("ep-error").classList.add("hidden");

    document.getElementById("edit-product-modal").classList.remove("hidden");
}

function closeEditProductModal() {
    document.getElementById("edit-product-modal").classList.add("hidden");
}

async function submitEditProduct() {
    const id = document.getElementById("edit-product-id").value;
    const pw = document.getElementById("ep-ceo-password").value;
    const errDiv = document.getElementById("ep-error");

    if (!pw) {
        errDiv.textContent = "Please enter CEO password to confirm price revision.";
        errDiv.classList.remove("hidden");
        return;
    }

    const payload = {
        product_name: document.getElementById("ep-name").value,
        tamil_name: document.getElementById("ep-tamilname").value,
        selling_price: parseFloat(document.getElementById("ep-sell").value),
        wholesale_price: parseFloat(document.getElementById("ep-wholesale").value),
        cost_price: parseFloat(document.getElementById("ep-cost").value),
        tax_rate: parseFloat(document.getElementById("ep-tax").value),
        hsn_code: document.getElementById("ep-hsn").value,
        min_stock_alert: parseFloat(document.getElementById("ep-minstk").value),
        reason: "Visual Price Update from CEO Panel"
    };

    try {
        const res = await fetch(`/api/ceo/products/${id}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${ceoToken}`,
                "X-CEO-Confirm": pw
            },
            body: JSON.stringify(payload)
        });
        if (res.ok) {
            alert("Product and price updated successfully!");
            closeEditProductModal();
            loadProducts();
            loadAuditLogs();
        } else {
            const data = await res.json();
            errDiv.textContent = data.detail || "Failed to update product.";
            errDiv.classList.remove("hidden");
        }
    } catch (e) {
        errDiv.textContent = "Network error.";
        errDiv.classList.remove("hidden");
    }
}

// ── Add Product Handling ────────────────────────────────────────────
function openAddProductModal() {
    document.getElementById("ap-name").value = "";
    document.getElementById("ap-tamilname").value = "";
    document.getElementById("ap-category").value = "FINISHED_GOOD";
    document.getElementById("ap-unit").value = "KG";
    document.getElementById("ap-sell").value = "";
    document.getElementById("ap-wholesale").value = "";
    document.getElementById("ap-cost").value = "";
    document.getElementById("ap-tax").value = "0.00";
    document.getElementById("ap-hsn").value = "";
    document.getElementById("ap-stock").value = "0";
    document.getElementById("ap-minstk").value = "10";
    document.getElementById("ap-ceo-password").value = "";
    document.getElementById("ap-error").classList.add("hidden");

    document.getElementById("add-product-modal").classList.remove("hidden");
    if (window.lucide) lucide.createIcons();
}

function sanitizeNumericInput(el) {
    if (!el) return;
    let val = el.value.trim();
    if (!val) return;

    val = val.replace(/[^0-9.]/g, '');
    const parts = val.split('.');
    if (parts.length > 2) {
        val = parts[0] + '.' + parts.slice(1).join('');
    }

    if (val.startsWith('.')) {
        val = '0' + val;
    } else if (/^0[0-9]/.test(val)) {
        val = val.replace(/^0+/, '');
        if (val === '' || val.startsWith('.')) val = '0' + val;
    }
    el.value = val;
}

let _ceoNameTranslateTimer = null;
function onCeoProductNameInput(el) {
    const tamInput = document.getElementById("ap-tamilname");
    const statusBadge = document.getElementById("ap-translate-status");
    if (!el || !tamInput) return;

    const val = el.value.trim();
    if (!val || val.length < 2) {
        if (statusBadge) statusBadge.classList.add("hidden");
        return;
    }

    if (statusBadge) statusBadge.classList.remove("hidden");

    clearTimeout(_ceoNameTranslateTimer);
    _ceoNameTranslateTimer = setTimeout(async () => {
        try {
            const res = await fetch(`/api/translate/tamil?text=${encodeURIComponent(val)}`);
            if (res.ok) {
                const data = await res.json();
                if (data && data.translated_text) {
                    tamInput.value = data.translated_text;
                }
            }
        } catch (e) {
            console.warn("CEO translation failed", e);
        } finally {
            if (statusBadge) statusBadge.classList.add("hidden");
        }
    }, 400);
}

function closeAddProductModal() {
    document.getElementById("add-product-modal").classList.add("hidden");
}

async function submitAddProduct() {
    const name = document.getElementById("ap-name").value.trim();
    const sellPrice = parseFloat(document.getElementById("ap-sell").value) || 0;
    const pw = document.getElementById("ap-ceo-password").value;
    const errDiv = document.getElementById("ap-error");

    if (!name || name.length < 3) {
        errDiv.textContent = "Please enter a valid product name (minimum 3 characters).";
        errDiv.classList.remove("hidden");
        return;
    }
    if (sellPrice <= 0) {
        errDiv.textContent = "Please enter a valid target price in ₹.";
        errDiv.classList.remove("hidden");
        return;
    }
    if (!pw) {
        errDiv.textContent = "Please enter CEO password to authorize adding product.";
        errDiv.classList.remove("hidden");
        return;
    }

    const unit = document.getElementById("ap-unit").value;
    let category = "FINISHED_GOOD";
    if (unit === "LITRE") category = "BY_PRODUCT";

    const payload = {
        product_name: name,
        tamil_name: document.getElementById("ap-tamilname").value.trim() || name,
        category: category,
        unit_of_measure: unit,
        selling_price: sellPrice,
        wholesale_price: sellPrice,
        cost_price: sellPrice,
        tax_rate: 0,
        hsn_code: null,
        initial_stock: parseFloat(document.getElementById("ap-stock").value) || 0,
        min_stock_alert: parseFloat(document.getElementById("ap-minstk").value) || 10,
        reason: "New product created by CEO"
    };

    try {
        const res = await fetch("/api/ceo/products", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${ceoToken}`,
                "X-CEO-Confirm": pw
            },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            alert(`✅ Product '${name}' added to catalog successfully!`);
            closeAddProductModal();
            loadProducts();
            loadAuditLogs();
        } else {
            const data = await res.json();
            errDiv.textContent = data.detail || "Failed to add product.";
            errDiv.classList.remove("hidden");
        }
    } catch (e) {
        errDiv.textContent = "Network connection error.";
        errDiv.classList.remove("hidden");
    }
}

// ── TAB 3: Website Leads & Inquiries ────────────────────────────────
async function loadInquiries() {
    const tbody = document.getElementById("inquiries-table-body");
    try {
        const res = await fetch("/api/ceo/inquiries", {
            headers: { "Authorization": `Bearer ${ceoToken}` }
        });
        if (res.ok) {
            const data = await res.json();
            if (!data.items || !data.items.length) {
                tbody.innerHTML = `<tr><td colspan="8" class="p-6 text-center text-slate-500">No website leads yet</td></tr>`;
                return;
            }

            tbody.innerHTML = data.items.map(i => {
                const dateStr = i.created_at ? new Date(i.created_at).toLocaleString('en-IN') : '-';
                const waLink = `https://wa.me/${i.phone.replace(/[^0-9]/g, '')}?text=${encodeURIComponent('Hello ' + i.name + ', regarding your RKG Suyambu inquiry for ' + (i.product_interest || 'cattle feed'))}`;

                return `
                    <tr class="hover:bg-emerald-950/40 transition">
                        <td class="p-3 text-slate-400 whitespace-nowrap">${dateStr}</td>
                        <td class="p-3 font-bold text-white">${i.name}</td>
                        <td class="p-3 font-mono text-amber-300 font-bold">${i.phone}</td>
                        <td class="p-3 text-emerald-300 font-semibold">${i.party_type}</td>
                        <td class="p-3 text-slate-300">${i.location || '-'}</td>
                        <td class="p-3 text-amber-200 font-bold">${i.product_interest || '-'}</td>
                        <td class="p-3 text-slate-300 font-bold">${i.quantity || '-'}</td>
                        <td class="p-3 text-center">
                            <div class="flex items-center justify-center gap-1.5">
                                <a href="${waLink}" target="_blank" class="px-2.5 py-1 rounded-lg bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-[11px] inline-flex items-center gap-1">
                                    <i data-lucide="message-circle" class="w-3 h-3"></i> WhatsApp
                                </a>
                                <a href="tel:${i.phone}" class="px-2.5 py-1 rounded-lg bg-slate-800 hover:bg-slate-700 text-amber-300 font-bold text-[11px] inline-flex items-center gap-1">
                                    <i data-lucide="phone" class="w-3 h-3"></i> Call
                                </a>
                            </div>
                        </td>
                    </tr>
                `;
            }).join('');

            if (window.lucide) lucide.createIcons();
        }
    } catch (e) {
        tbody.innerHTML = `<tr><td colspan="8" class="p-6 text-center text-red-400">Failed to load leads</td></tr>`;
    }
}

// ── TAB 4: Users Management ─────────────────────────────────────────
async function loadUsers() {
    const tbody = document.getElementById("users-table-body");
    try {
        const res = await fetch("/api/ceo/users", {
            headers: { "Authorization": `Bearer ${ceoToken}` }
        });
        if (res.ok) {
            const users = await res.json();
            tbody.innerHTML = users.map(u => `
                <tr class="hover:bg-emerald-950/40 transition">
                    <td class="p-3 font-bold text-white">
                        <div>${u.full_name}</div>
                        ${u.tamil_name ? `<div class="text-[11px] text-amber-300/80 font-tamil">${u.tamil_name}</div>` : ''}
                    </td>
                    <td class="p-3 font-mono text-emerald-300">${u.username}</td>
                    <td class="p-3 font-extrabold text-amber-300">${u.role}</td>
                    <td class="p-3">
                        <span class="px-2 py-0.5 rounded-full text-[10px] font-bold ${u.is_active ? 'bg-emerald-900 text-emerald-300' : 'bg-red-950 text-red-300'}">
                            ${u.is_active ? 'Active' : 'Disabled'}
                        </span>
                    </td>
                    <td class="p-3 text-center">
                        <button onclick="promptResetPassword('${u.username}')" class="px-2.5 py-1 rounded-lg bg-amber-500/20 hover:bg-amber-500 text-amber-300 hover:text-slate-950 font-bold text-[11px] transition">
                            Reset Password 🔑
                        </button>
                    </td>
                </tr>
            `).join('');
            if (window.lucide) lucide.createIcons();
        }
    } catch (e) {}
}

function openCreateUserPanel() {
    document.getElementById("create-user-panel")?.classList.remove("hidden");
}

function closeCreateUserPanel() {
    document.getElementById("create-user-panel")?.classList.add("hidden");
}

async function submitCreateUser() {
    const payload = {
        full_name: document.getElementById("nu-fullname").value,
        tamil_name: document.getElementById("nu-tamilname").value,
        username: document.getElementById("nu-username").value,
        password: document.getElementById("nu-password").value,
        role: document.getElementById("nu-role").value
    };

    openConfirmModal(
        "Create Operator Account",
        `Authorize creating staff operator account for '${payload.username}'.`,
        async (pw) => {
            const res = await fetch("/api/ceo/users", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${ceoToken}`,
                    "X-CEO-Confirm": pw
                },
                body: JSON.stringify(payload)
            });
            if (res.ok) {
                alert("Operator created successfully!");
                closeConfirmModal();
                closeCreateUserPanel();
                loadUsers();
                loadAuditLogs();
            } else {
                const err = await res.json();
                showConfirmError(err.detail || "Failed to create user.");
            }
        }
    );
}

function promptResetPassword(username) {
    const newPw = prompt(`Enter new password for operator '${username}':`);
    if (!newPw) return;

    openConfirmModal(
        "Reset Operator Password",
        `Change password for operator account '${username}'.`,
        async (ceoPw) => {
            const res = await fetch(`/api/ceo/users/${username}/password`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${ceoToken}`,
                    "X-CEO-Confirm": ceoPw
                },
                body: JSON.stringify({ new_password: newPw, reason: "CEO password reset" })
            });
            if (res.ok) {
                alert(`Password for ${username} reset successfully!`);
                closeConfirmModal();
                loadAuditLogs();
            } else {
                const err = await res.json();
                showConfirmError(err.detail || "Failed to reset password.");
            }
        }
    );
}

// ── TAB 5: Audit Logs ───────────────────────────────────────────────
async function loadAuditLogs() {
    const tbody = document.getElementById("audit-table-body");
    try {
        const res = await fetch("/api/ceo/audit-logs", {
            headers: { "Authorization": `Bearer ${ceoToken}` }
        });
        if (res.ok) {
            const data = await res.json();
            if (!data.logs || !data.logs.length) {
                tbody.innerHTML = `<tr><td colspan="6" class="p-6 text-center text-slate-500">No logs found</td></tr>`;
                return;
            }

            tbody.innerHTML = data.logs.map(l => `
                <tr class="hover:bg-emerald-950/40 transition">
                    <td class="p-3 text-slate-400 whitespace-nowrap">${new Date(l.timestamp).toLocaleString('en-IN')}</td>
                    <td class="p-3 font-bold text-amber-400">${l.performed_by}</td>
                    <td class="p-3 font-extrabold text-emerald-300">${l.action_type}</td>
                    <td class="p-3 font-mono text-slate-300">${l.target_table}</td>
                    <td class="p-3 text-slate-200">${l.target_label || l.target_id || '-'}</td>
                    <td class="p-3 text-slate-400 text-[11px]">${l.new_value || l.reason || '-'}</td>
                </tr>
            `).join('');
        }
    } catch (e) {}
}

// ── Re-Authentication Confirm Modal ─────────────────────────────────
function openConfirmModal(title, desc, onExecute) {
    document.getElementById("confirm-title").textContent = title;
    document.getElementById("confirm-description").textContent = desc;
    document.getElementById("confirm-ceo-password").value = "";
    document.getElementById("confirm-error").classList.add("hidden");
    pendingAction = onExecute;
    document.getElementById("confirm-modal").classList.remove("hidden");
}

function closeConfirmModal() {
    document.getElementById("confirm-modal").classList.add("hidden");
    pendingAction = null;
}

function showConfirmError(msg) {
    const errDiv = document.getElementById("confirm-error");
    errDiv.textContent = msg;
    errDiv.classList.remove("hidden");
}

async function executeConfirmedAction() {
    const pw = document.getElementById("confirm-ceo-password").value;
    if (!pw) {
        showConfirmError("Please enter your CEO master password.");
        return;
    }
    if (pendingAction) {
        await pendingAction(pw);
    }
}

// ── LIVE PASSWORD STRENGTH EVALUATOR ──────────────────────────────────────────
function evaluatePasswordLive(pw) {
    if (!pw) return { score: 0, text: "Enter Password", color: "#64748b", pct: 0, missing: ["8+ characters required"] };
    let score = 0;
    const missing = [];
    if (pw.length >= 8) score++; else missing.push("8+ characters");
    if (/[a-z]/.test(pw) && /[A-Z]/.test(pw)) score++; else missing.push("Upper & Lowercase");
    if (/[0-9]/.test(pw)) score++; else missing.push("Number (0-9)");
    if (/[!@#$%^&*(),.?":{}|<>\-_+=\[\]\\\/]/.test(pw)) score++; else missing.push("Special Symbol (@,#,etc)");
    
    // Repetitive character penalty
    const uniqueChars = new Set(pw.split('')).size;
    if (uniqueChars < 4 && pw.length >= 6) {
        score = Math.max(0, score - 2);
        missing.push("Avoid repetitive characters");
    }

    const levels = [
        { text: "Very Weak (மிக பலவீனமானது)", color: "#ef4444", pct: 15 },
        { text: "Weak (பலவீனமானது)", color: "#f97316", pct: 35 },
        { text: "Fair (மிதமானது)", color: "#eab308", pct: 60 },
        { text: "Strong (வலுவானது)", color: "#22c55e", pct: 85 },
        { text: "Very Strong (மிகவும் பாதுகாப்பானது)", color: "#10b981", pct: 100 }
    ];
    const lvl = levels[Math.min(score, 4)];
    return { score, text: lvl.text, color: lvl.color, pct: lvl.pct, missing };
}

function handleLivePasswordStrength(inputId, barId, labelId, badgesId) {
    const input = document.getElementById(inputId);
    const bar = document.getElementById(barId);
    const label = document.getElementById(labelId);
    const badges = document.getElementById(badgesId);
    if (!input) return;

    const res = evaluatePasswordLive(input.value);
    if (bar) {
        bar.style.width = `${res.pct}%`;
        bar.style.background = res.color;
    }
    if (label) {
        label.textContent = `Strength: ${res.text}`;
        label.style.color = res.color;
    }
    if (badges) {
        badges.innerHTML = res.missing.map(m => 
            `<span class="px-2 py-0.5 rounded-full bg-slate-800 text-slate-400 border border-slate-700">⚠️ ${m}</span>`
        ).join('') + (res.score >= 3 ? `<span class="px-2 py-0.5 rounded-full bg-emerald-950 text-emerald-300 border border-emerald-700">✅ High Security Passphrase</span>` : '');
    }
}

// ── LIVE INDIAN PHONE VALIDATION ─────────────────────────────────────────────
function sanitizeAndValidatePhone(inputElem, statusElem) {
    if (!inputElem) return false;
    let digits = inputElem.value.replace(/\D/g, '');
    if (digits.startsWith("91") && digits.length > 10) {
        digits = digits.substring(2);
    } else if (digits.startsWith("0") && digits.length > 10) {
        digits = digits.substring(1);
    }
    if (digits.length > 10) {
        digits = digits.substring(0, 10);
    }
    inputElem.value = digits;

    const repetitivePatterns = ["0000000000", "1111111111", "2222222222", "3333333333", "4444444444", "5555555555", "6666666666", "7777777777", "8888888888", "9999999999", "1234567890", "9876543210"];
    const uniqueDigits = new Set(digits.split('')).size;

    if (!digits) {
        if (statusElem) { statusElem.innerHTML = ""; statusElem.style.display = "none"; }
        inputElem.style.borderColor = "";
        return true;
    }

    if (digits.length < 10) {
        if (statusElem) {
            statusElem.innerHTML = `<span class="text-rose-400 font-bold">❌ 10 digits required (${digits.length}/10)</span>`;
            statusElem.style.display = "block";
        }
        inputElem.style.borderColor = "#ef4444";
        return false;
    }

    if (!['6','7','8','9'].includes(digits[0])) {
        if (statusElem) {
            statusElem.innerHTML = `<span class="text-rose-400 font-bold">❌ Must start with 6, 7, 8, or 9</span>`;
            statusElem.style.display = "block";
        }
        inputElem.style.borderColor = "#ef4444";
        return false;
    }

    if (uniqueDigits <= 2 || repetitivePatterns.includes(digits)) {
        if (statusElem) {
            statusElem.innerHTML = `<span class="text-rose-400 font-bold">❌ Repetitive or invalid fake number</span>`;
            statusElem.style.display = "block";
        }
        inputElem.style.borderColor = "#ef4444";
        return false;
    }

    if (statusElem) {
        statusElem.innerHTML = `<span class="text-emerald-400 font-bold">✅ Valid 10-Digit Mobile Number</span>`;
        statusElem.style.display = "block";
    }
    inputElem.style.borderColor = "#10b981";
    return true;
}

// ── Cloud Firestore Sync ─────────────────────────────────────────────
async function triggerCloudSync() {
    const btn = document.getElementById("cloud-sync-btn");
    const originalHTML = btn ? btn.innerHTML : "";
    if (btn) {
        btn.disabled = true;
        btn.innerHTML = `<i data-lucide="loader-2" class="w-3.5 h-3.5 animate-spin text-amber-400"></i> Syncing...`;
        if (window.lucide) lucide.createIcons();
    }

    try {
        const res = await fetch("/api/system/firebase-sync", { method: "POST" });
        const data = await res.json();
        if (data.success) {
            const counts = data.synced_counts || {};
            const total = Object.values(counts).reduce((a, b) => a + b, 0);
            alert(`✅ Cloud Sync Success!\n\n${total} total records successfully mirrored to Google Cloud Firestore.\n\n• Products: ${counts.products || 0}\n• Invoices: ${counts.invoices || 0}\n• Customers/Parties: ${counts.parties || 0}\n• Batches: ${counts.batches || 0}\n• Purchases: ${counts.purchases || 0}\n• Inquiries: ${counts.inquiries || 0}`);
        } else {
            alert(`⚠️ Cloud Sync Notice:\n${data.error || data.message || "Failed to sync"}`);
        }
    } catch (e) {
        alert(`❌ Sync Request Failed: ${e.message}`);
    } finally {
        if (btn) {
            btn.disabled = false;
            btn.innerHTML = originalHTML;
            if (window.lucide) lucide.createIcons();
        }
    }
}


