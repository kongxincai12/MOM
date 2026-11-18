/* 简易 MES 前端公共库：登录态、请求封装、导航、表格/分页渲染 */
(function () {
  const LS_TOKEN = 'mes_token';
  const LS_USER = 'mes_user';

  function getToken() { return localStorage.getItem(LS_TOKEN); }
  function setAuth(token, user) {
    localStorage.setItem(LS_TOKEN, token);
    localStorage.setItem(LS_USER, JSON.stringify(user || {}));
  }
  function getUser() { try { return JSON.parse(localStorage.getItem(LS_USER)) || {}; } catch (e) { return {}; } }
  function clearAuth() { localStorage.removeItem(LS_TOKEN); localStorage.removeItem(LS_USER); }

  function qs(params) {
    if (!params) return '';
    const parts = [];
    for (const k in params) {
      const v = params[k];
      if (v !== undefined && v !== null && v !== '') parts.push(encodeURIComponent(k) + '=' + encodeURIComponent(v));
    }
    return parts.length ? ('?' + parts.join('&')) : '';
  }

  // 统一请求：自动带 Bearer、处理 401、解析 Result，业务码非 200 抛错
  async function request(path, opt) {
    opt = opt || {};
    const method = opt.method || 'GET';
    const url = '/api' + path + qs(opt.params);
    const headers = { 'Content-Type': 'application/json' };
    const token = getToken();
    if (token) headers['Authorization'] = 'Bearer ' + token;
    let resp;
    try {
      resp = await fetch(url, { method, headers, body: opt.body ? JSON.stringify(opt.body) : undefined });
    } catch (e) {
      throw new Error('网络请求失败，请检查后端是否启动');
    }
    if (resp.status === 401) {
      clearAuth();
      location.href = 'login.html';
      throw new Error('未登录或登录已过期');
    }
    let json;
    try { json = await resp.json(); } catch (e) { throw new Error('响应解析失败(HTTP ' + resp.status + ')'); }
    if (json.code !== 200) throw new Error(json.message || '请求失败');
    return json.data;
  }

  const get = (p, params) => request(p, { method: 'GET', params });
  const post = (p, body, params) => request(p, { method: 'POST', body, params });
  const put = (p, body) => request(p, { method: 'PUT', body });
  const del = (p) => request(p, { method: 'DELETE' });

  async function login(username, password) {
    const resp = await fetch('/api/auth/login', {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });
    const json = await resp.json();
    if (json.code !== 200) throw new Error(json.message || '登录失败');
    setAuth(json.data.token, { employeeId: json.data.employeeId, role: json.data.role });
    return json.data;
  }

  function requireLogin() {
    if (!getToken()) { location.href = 'login.html'; return false; }
    return true;
  }
  function logout() { clearAuth(); location.href = 'login.html'; }

  const NAV = [
    { href: 'index.html', label: '首页' },
    { href: 'work-order.html', label: '生产工单' },
    { href: 'production-report.html', label: '生产报工' },
    { href: 'material.html', label: '物料库存' },
    { href: 'equipment.html', label: '设备管理' },
    { href: 'quality.html', label: '质量管理' }
  ];

  function renderNav(active) {
    const nav = document.getElementById('nav');
    if (!nav) return;
    const u = getUser();
    nav.className = 'navbar';
    nav.innerHTML =
      '<div class="brand">简易 MES</div>' +
      '<div class="links">' + NAV.map(n =>
        '<a href="' + n.href + '" class="' + (n.href === active ? 'active' : '') + '">' + n.label + '</a>').join('') + '</div>' +
      '<div class="user"><span>工号 ' + (u.employeeId || '-') + ' (' + (u.role || '-') + ')</span>' +
      '<button class="btn-mini" onclick="MES.logout()">退出</button></div>';
  }

  // columns: [{label, key, render(row,index)->html}]  若不给 render 则直接展示 key
  function renderTable(container, columns, rows) {
    const el = typeof container === 'string' ? document.getElementById(container) : container;
    if (!el) return;
    if (!rows || !rows.length) { el.innerHTML = '<div class="empty">暂无数据</div>'; return; }
    let html = '<table class="grid"><thead><tr>' + columns.map(c => '<th>' + c.label + '</th>').join('') + '</tr></thead><tbody>';
    rows.forEach((row, i) => {
      html += '<tr>' + columns.map(c => {
        const val = c.render ? c.render(row, i) : (row[c.key] === null || row[c.key] === undefined ? '' : esc(row[c.key]));
        return '<td>' + val + '</td>';
      }).join('') + '</tr>';
    });
    html += '</tbody></table>';
    el.innerHTML = html;
  }

  // pageObj: MyBatis-Plus IPage {records,total,size,current,pages}
  function renderPager(container, pageObj, onGo) {
    const el = typeof container === 'string' ? document.getElementById(container) : container;
    if (!el) return;
    const cur = Number(pageObj.current) || 1;
    const pages = Number(pageObj.pages) || 0;
    el.innerHTML = '<div class="pager"><span>共 ' + pageObj.total + ' 条，第 ' + cur + '/' + Math.max(pages, 1) + ' 页</span>' +
      '<button data-go="' + (cur - 1) + '" ' + (cur <= 1 ? 'disabled' : '') + '>上一页</button>' +
      '<button data-go="' + (cur + 1) + '" ' + (cur >= pages ? 'disabled' : '') + '>下一页</button></div>';
    el.querySelectorAll('button[data-go]').forEach(b => {
      b.onclick = () => { const p = Number(b.dataset.go); if (p >= 1) onGo(p); };
    });
  }

  function toast(msg, type) {
    let t = document.querySelector('.toast');
    if (!t) { t = document.createElement('div'); t.className = 'toast'; document.body.appendChild(t); }
    t.textContent = msg;
    t.className = 'toast show ' + (type || 'info');
    clearTimeout(t._tm);
    t._tm = setTimeout(() => { t.className = 'toast'; }, 2600);
  }

  function esc(s) {
    if (s === null || s === undefined) return '';
    return String(s).replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
  }
  function fmtDate(s) {
    if (!s) return '';
    const d = new Date(s);
    if (isNaN(d.getTime())) return s;
    return d.toLocaleString('zh-CN', { hour12: false });
  }
  function statusTag(s) { return s ? '<span class="tag tag-' + esc(s) + '">' + esc(s) + '</span>' : ''; }

  // Tabs 初始化：给 .tabs 按钮绑定切换 .tab-panel
  function initTabs(tabsContainerId) {
    const box = document.getElementById(tabsContainerId);
    if (!box) return;
    const btns = box.querySelectorAll('.tabs button');
    const panels = box.querySelectorAll('.tab-panel');
    btns.forEach(btn => {
      btn.onclick = () => {
        btns.forEach(b => b.classList.remove('active'));
        panels.forEach(p => p.classList.remove('active'));
        btn.classList.add('active');
        const panel = box.querySelector('#' + btn.dataset.tab);
        if (panel) panel.classList.add('active');
        if (btn._onShow) btn._onShow();
      };
    });
  }

  window.MES = {
    getToken, setAuth, getUser, clearAuth, request, get, post, put, del,
    login, requireLogin, logout, renderNav, renderTable, renderPager,
    toast, esc, fmtDate, statusTag, initTabs
  };
})();
