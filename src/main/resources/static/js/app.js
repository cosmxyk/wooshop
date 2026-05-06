let token = localStorage.getItem('token');
let loggedInEmail = localStorage.getItem('loggedInEmail');

function updateAuthStatus() {
    const el = document.getElementById('auth-status');
    el.textContent = token ? `✅ ${loggedInEmail}` : '🔒 비로그인 상태';
}

updateAuthStatus();

function toast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    const el = document.createElement('div');
    el.className = `toast ${type}`;
    el.textContent = message;
    container.appendChild(el);
    setTimeout(() => el.remove(), 3000);
}

function setLoading(btnId, spinnerId, loading) {
    document.getElementById(btnId).disabled = loading;
    document.getElementById(spinnerId).style.display = loading ? 'block' : 'none';
}

function hideAll(...ids) {
    ids.forEach(id => { const el = document.getElementById(id); if (el) el.style.display = 'none'; });
}

async function request(method, path, body = null, auth = false) {
    const headers = { 'Content-Type': 'application/json' };
    if (auth && token) headers['Authorization'] = 'Bearer ' + token;
    const res = await fetch(path, { method, headers, body: body ? JSON.stringify(body) : null });
    const text = await res.text();
    let data;
    try { data = JSON.parse(text); } catch { data = text; }
    return { ok: res.ok, status: res.status, data };
}

/* 회원가입 */
async function register() {
    const email    = document.getElementById('reg-email').value.trim();
    const password = document.getElementById('reg-password').value;
    const name     = document.getElementById('reg-name').value.trim();

    hideAll('reg-info', 'reg-error');
    setLoading('reg-btn', 'reg-spinner', true);
    const { ok, data } = await request('POST', '/api/members/register', { email, password, name });
    setLoading('reg-btn', 'reg-spinner', false);

    if (ok) {
        document.getElementById('reg-status-text').textContent = '✅ 회원가입 성공';
        document.getElementById('reg-member-id').textContent   = data;
        document.getElementById('reg-member-name').textContent = name;
        document.getElementById('reg-member-email').textContent = email;
        document.getElementById('reg-info').style.display = 'block';
        toast(`${name}님 회원가입 완료!`, 'success');
    } else {
        document.getElementById('reg-error').textContent = `❌ ${data}`;
        document.getElementById('reg-error').style.display = 'block';
        toast('회원가입 실패', 'error');
    }
}

/* 로그인 */
async function login() {
    const email    = document.getElementById('login-email').value.trim();
    const password = document.getElementById('login-password').value;

    hideAll('login-info', 'login-error', 'token-box');
    setLoading('login-btn', 'login-spinner', true);
    const { ok, data } = await request('POST', '/api/members/login', { email, password });
    setLoading('login-btn', 'login-spinner', false);

    if (ok) {
        token = data;
        loggedInEmail = email;
        localStorage.setItem('token', token);
        localStorage.setItem('loggedInEmail', email);
        updateAuthStatus();

        document.getElementById('login-icon').textContent = '✅';
        document.getElementById('login-status-text').textContent = '로그인 성공';
        document.getElementById('login-email-text').textContent = email;
        document.getElementById('login-info').style.display = 'flex';

        const tokenBox = document.getElementById('token-box');
        tokenBox.textContent = '🔑 ' + token;
        tokenBox.style.display = 'block';

        toast(`${email} 로그인 성공!`, 'success');
    } else {
        document.getElementById('login-error').textContent = `❌ ${data}`;
        document.getElementById('login-error').style.display = 'block';
        toast('로그인 실패', 'error');
    }
}

/* 로그아웃 */
function logout() {
    token = null;
    loggedInEmail = null;
    localStorage.removeItem('token');
    localStorage.removeItem('loggedInEmail');
    updateAuthStatus();
    hideAll('login-info', 'token-box', 'login-error');
    toast('로그아웃 되었습니다.', 'info');
}

/* 상품 등록 */
async function registerProduct() {
    const productCode   = document.getElementById('p-code').value.trim();
    const name          = document.getElementById('p-name').value.trim();
    const price         = parseFloat(document.getElementById('p-price').value);
    const stockQuantity = parseInt(document.getElementById('p-stock').value);

    hideAll('product-reg-info', 'product-reg-error');
    setLoading('product-reg-btn', 'product-reg-spinner', true);
    const { ok, data } = await request('POST', '/api/products', { productCode, name, price, stockQuantity }, true);
    setLoading('product-reg-btn', 'product-reg-spinner', false);

    if (ok) {
        document.getElementById('product-reg-status-text').textContent = '✅ 등록 성공';
        document.getElementById('product-reg-id').textContent          = data;
        document.getElementById('product-reg-name-text').textContent   = name;
        document.getElementById('product-reg-price-text').textContent  = `₩${price.toLocaleString()}`;
        document.getElementById('product-reg-stock-text').textContent  = `${stockQuantity}개`;
        document.getElementById('product-reg-info').style.display = 'block';
        toast(`${name} 등록 완료!`, 'success');
    } else {
        document.getElementById('product-reg-error').textContent = `❌ ${data}`;
        document.getElementById('product-reg-error').style.display = 'block';
        toast('상품 등록 실패', 'error');
    }
}

/* 상품 단건 조회 */
async function getProduct() {
    const rawId = document.getElementById('p-id').value.trim();

    hideAll('product-detail', 'product-get-error');

    if (!rawId) {
        document.getElementById('product-get-error').textContent = '❌ 상품 ID를 입력해주세요.';
        document.getElementById('product-get-error').style.display = 'block';
        return;
    }

    if (isNaN(rawId) || !Number.isInteger(Number(rawId)) || Number(rawId) <= 0) {
        document.getElementById('product-get-error').textContent = '❌ 상품 ID는 양의 정수여야 합니다.';
        document.getElementById('product-get-error').style.display = 'block';
        return;
    }

    setLoading('product-get-btn', 'product-get-spinner', true);
    const { ok, data } = await request('GET', `/api/products/${rawId}`);
    setLoading('product-get-btn', 'product-get-spinner', false);

    if (ok) {
        document.getElementById('detail-id').textContent    = data.productId;
        document.getElementById('detail-code').textContent  = data.productCode;
        document.getElementById('detail-name').textContent  = data.name;
        document.getElementById('detail-price').textContent = `₩${data.price.toLocaleString()}`;
        document.getElementById('product-detail').style.display = 'block';
        toast('상품 조회 성공!', 'success');
    } else {
        document.getElementById('product-get-error').textContent = `❌ ${data}`;
        document.getElementById('product-get-error').style.display = 'block';
        toast('상품 조회 실패', 'error');
    }
}

/* 상품 전체 조회 */
async function getProducts() {
    const grid = document.getElementById('product-card-grid');
    grid.innerHTML = '';

    setLoading('products-btn', 'products-spinner', true);
    const { ok, data } = await request('GET', '/api/products');
    setLoading('products-btn', 'products-spinner', false);

    if (!ok) {
        grid.innerHTML = `<div class="empty-state">❌ 조회 실패: ${data}</div>`;
        toast('목록 조회 실패', 'error');
        return;
    }

    if (!data.length) {
        grid.innerHTML = '<div class="empty-state">📭 등록된 상품이 없습니다.</div>';
        toast('등록된 상품이 없습니다.', 'info');
        return;
    }

    data.forEach(p => {
        const card = document.createElement('div');
        card.className = 'product-card';
        card.innerHTML = `
            <div class="product-card-code">${p.productCode}</div>
            <div class="product-card-name">${p.name}</div>
            <div class="product-card-price">₩${p.price.toLocaleString()}</div>
            <div class="product-card-id">ID: ${p.productId}</div>
        `;
        grid.appendChild(card);
    });

    toast(`상품 ${data.length}개 조회 완료!`, 'success');
}
