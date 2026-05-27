const ProfilePage = {
  async render() {
    const [cc, fc, hc, tc] = await Promise.all([
      dbCount("clothing"), dbCount("food"), dbCount("housing"), dbCount("transport"),
    ]);
    const total = cc + fc + hc + tc;

    // Calculate total cost
    const allItems = await Promise.all([
      dbGetAll("clothing"), dbGetAll("food"), dbGetAll("housing"), dbGetAll("transport"),
    ]);
    const totalCost = allItems.flat().reduce((sum, i) => sum + (parseFloat(i.cost) || 0), 0);

    const container = document.getElementById("page-content");
    const theme = localStorage.getItem("theme") || "light";
    const lang = currentLang;
    const loggedIn = isLoggedIn();
    const username = localStorage.getItem("username") || "";
    const lastSync = getLastSync();

    container.innerHTML = `
      <div class="fade-in" style="max-width:480px;margin:0 auto">
        <!-- Auth Section -->
        <div class="card" style="margin-bottom:12px;text-align:center;padding:20px">
          ${loggedIn ? `
            <div style="font-size:14px;color:var(--text-secondary);margin-bottom:4px" data-i18n="profile.welcome">${t("profile.welcome")}</div>
            <div style="font-size:18px;font-weight:600;margin-bottom:12px">${esc(username)}</div>
            <button onclick="ProfilePage.logout()" style="font-size:13px;color:var(--text-secondary);text-decoration:underline">${t("profile.logout")}</button>
          ` : `
            <div style="font-size:14px;color:var(--text-secondary);margin-bottom:12px">${t("profile.sync")} — ${t("profile.syncOff")}</div>
            <div style="display:flex;gap:8px;justify-content:center">
              <button onclick="ProfilePage.showAuthModal('login')" style="padding:8px 24px;border-radius:8px;background:var(--text-primary);color:var(--bg-primary);font-size:13px;font-weight:500">${t("profile.login")}</button>
              <button onclick="ProfilePage.showAuthModal('register')" style="padding:8px 24px;border-radius:8px;border:1px solid var(--border);font-size:13px">${t("profile.register")}</button>
            </div>
          `}
        </div>

        <!-- Stats -->
        <h2 style="font-size:13px;font-weight:600;color:var(--text-secondary);text-transform:uppercase;letter-spacing:0.5px;margin-bottom:12px" data-i18n="profile.stats">${t("profile.stats")}</h2>
        <div class="stats-grid" style="margin-bottom:12px">
          <div class="stat-card"><div class="stat-value">${total}</div><div class="stat-label" data-i18n="profile.totalRecords">${t("profile.totalRecords")}</div></div>
          <div class="stat-card"><div class="stat-value">${t("currency")}${totalCost.toFixed(0)}</div><div class="stat-label" data-i18n="profile.totalCost">${t("profile.totalCost")}</div></div>
        </div>
        <div class="stats-grid" style="margin-bottom:24px">
          <div class="stat-card"><div class="stat-value">${cc}</div><div class="stat-label" data-i18n="profile.clothingCount">${t("profile.clothingCount")}</div></div>
          <div class="stat-card"><div class="stat-value">${fc}</div><div class="stat-label" data-i18n="profile.foodCount">${t("profile.foodCount")}</div></div>
          <div class="stat-card"><div class="stat-value">${hc}</div><div class="stat-label" data-i18n="profile.housingCount">${t("profile.housingCount")}</div></div>
          <div class="stat-card"><div class="stat-value">${tc}</div><div class="stat-label" data-i18n="profile.transportCount">${t("profile.transportCount")}</div></div>
        </div>

        <!-- Charts -->
        <h2 style="font-size:13px;font-weight:600;color:var(--text-secondary);text-transform:uppercase;letter-spacing:0.5px;margin-bottom:12px" data-i18n="chart.monthly">${t("chart.monthly")}</h2>
        <div class="card" style="margin-bottom:12px;padding:12px"><canvas id="chart-monthly" height="180"></canvas></div>
        <h2 style="font-size:13px;font-weight:600;color:var(--text-secondary);text-transform:uppercase;letter-spacing:0.5px;margin-bottom:12px" data-i18n="chart.category">${t("chart.category")}</h2>
        <div class="card" style="margin-bottom:24px;padding:12px"><canvas id="chart-category" height="200"></canvas></div>

        <!-- Data Management -->
        <h2 style="font-size:13px;font-weight:600;color:var(--text-secondary);text-transform:uppercase;letter-spacing:0.5px;margin-bottom:12px" data-i18n="export.title">${t("export.title")}</h2>
        <div class="card" style="margin-bottom:24px">
          <div style="display:flex;gap:8px;flex-wrap:wrap;padding:8px 0">
            <button onclick="DataIO.exportCSV()" style="padding:7px 16px;border-radius:8px;border:1px solid var(--border);font-size:12px">${t("export.csv")}</button>
            <button onclick="DataIO.exportJSON()" style="padding:7px 16px;border-radius:8px;border:1px solid var(--border);font-size:12px">${t("export.json")}</button>
            <label style="padding:7px 16px;border-radius:8px;border:1px solid var(--border);font-size:12px;cursor:pointer">
              ${t("export.importFile")}
              <input type="file" accept=".csv,.json" style="display:none" onchange="DataIO.importFile(this.files[0])">
            </label>
          </div>
        </div>

        <!-- Settings -->
        <h2 style="font-size:13px;font-weight:600;color:var(--text-secondary);text-transform:uppercase;letter-spacing:0.5px;margin-bottom:12px" data-i18n="profile.settings">${t("profile.settings")}</h2>
        <div class="card">
          <div style="display:flex;justify-content:space-between;align-items:center;padding:8px 0">
            <span style="font-size:14px" data-i18n="profile.theme">${t("profile.theme")}</span>
            <div style="display:inline-flex;background:var(--bg-tertiary);border-radius:8px;padding:2px;gap:2px">
              <button onclick="ProfilePage.setTheme('light')" style="padding:5px 14px;border-radius:6px;font-size:12px;font-weight:${theme==="light"?500:400};background:${theme==="light"?"var(--bg-primary)":"transparent"};box-shadow:${theme==="light"?"var(--shadow-sm)":"none"};color:${theme==="light"?"var(--text-primary)":"var(--text-secondary)"};transition:all 150ms">${t("profile.light")}</button>
              <button onclick="ProfilePage.setTheme('dark')" style="padding:5px 14px;border-radius:6px;font-size:12px;font-weight:${theme==="dark"?500:400};background:${theme==="dark"?"var(--bg-primary)":"transparent"};box-shadow:${theme==="dark"?"var(--shadow-sm)":"none"};color:${theme==="dark"?"var(--text-primary)":"var(--text-secondary)"};transition:all 150ms">${t("profile.dark")}</button>
            </div>
          </div>
          <div style="display:flex;justify-content:space-between;align-items:center;padding:8px 0">
            <span style="font-size:14px" data-i18n="profile.language">${t("profile.language")}</span>
            <div style="display:inline-flex;background:var(--bg-tertiary);border-radius:8px;padding:2px;gap:2px">
              <button onclick="ProfilePage.setLang('zh')" style="padding:5px 14px;border-radius:6px;font-size:12px;font-weight:${lang==="zh"?500:400};background:${lang==="zh"?"var(--bg-primary)":"transparent"};box-shadow:${lang==="zh"?"var(--shadow-sm)":"none"};color:${lang==="zh"?"var(--text-primary)":"var(--text-secondary)"};transition:all 150ms">${t("profile.zh")}</button>
              <button onclick="ProfilePage.setLang('en')" style="padding:5px 14px;border-radius:6px;font-size:12px;font-weight:${lang==="en"?500:400};background:${lang==="en"?"var(--bg-primary)":"transparent"};box-shadow:${lang==="en"?"var(--shadow-sm)":"none"};color:${lang==="en"?"var(--text-primary)":"var(--text-secondary)"};transition:all 150ms">${t("profile.en")}</button>
            </div>
          </div>
          ${loggedIn ? `
          <div style="display:flex;justify-content:space-between;align-items:center;padding:8px 0">
            <span style="font-size:14px" data-i18n="profile.sync">${t("profile.sync")}</span>
            <button onclick="ProfilePage.doSync()" id="btn-sync" style="padding:5px 14px;border-radius:6px;font-size:12px;background:var(--bg-primary);box-shadow:var(--shadow-sm);font-weight:500">${t("profile.syncNow")}</button>
          </div>
          ${lastSync ? `<div style="font-size:11px;color:var(--text-tertiary);margin-top:-4px">${t("profile.lastSync")}: ${fmtDate(lastSync)}</div>` : ""}
          ` : `
          <div style="display:flex;justify-content:space-between;align-items:center;padding:8px 0">
            <span style="font-size:14px" data-i18n="profile.sync">${t("profile.sync")}</span>
            <span style="font-size:12px;color:var(--text-tertiary)" data-i18n="profile.syncOff">${t("profile.syncOff")}</span>
          </div>
          `}
        </div>
      </div>`;
    setLang(currentLang);

    // Render charts
    ProfilePage.renderCharts(allItems);
  },

  renderCharts(allItems) {
    const flat = allItems.flat();
    if (flat.length === 0) return;
    if (typeof Chart === "undefined") return;

    // Monthly trend
    const monthly = {};
    flat.forEach(i => {
      if (!i.cost) return;
      const d = new Date(i.created_at);
      const key = `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,"0")}`;
      monthly[key] = (monthly[key] || 0) + parseFloat(i.cost);
    });
    const mLabels = Object.keys(monthly).sort().slice(-6);
    const mData = mLabels.map(k => monthly[k]);
    const mCtx = document.getElementById("chart-monthly");
    if (mCtx) {
      new Chart(mCtx, {
        type: "line",
        data: {
          labels: mLabels,
          datasets: [{ data: mData, borderColor: "#4A90D9", backgroundColor: "rgba(74,144,217,0.1)", fill: true, tension: 0.3, pointRadius: 4 }]
        },
        options: { responsive: true, plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true } } }
      });
    }

    // Category pie
    const catCosts = {};
    const storeNames = ["clothing", "food", "housing", "transport"];
    const storeLabels = [t("tab.clothing"), t("tab.food"), t("tab.housing"), t("tab.transport")];
    storeNames.forEach(s => {
      const storeItems = allItems[storeNames.indexOf(s)] || [];
      catCosts[s] = storeItems.reduce((sum, i) => sum + (parseFloat(i.cost) || 0), 0);
    });
    const pCtx = document.getElementById("chart-category");
    if (pCtx) {
      new Chart(pCtx, {
        type: "doughnut",
        data: {
          labels: storeLabels,
          datasets: [{ data: storeNames.map(s => catCosts[s]), backgroundColor: ["#4A90D9","#E8913A","#67B778","#D94F4F"] }]
        },
        options: { responsive: true, plugins: { legend: { position: "bottom" } } }
      });
    }
  },

  showAuthModal(mode) {
    const isLogin = mode === "login";
    const overlay = document.createElement("div");
    overlay.className = "modal-overlay";
    overlay.innerHTML = `
      <div class="modal fade-in">
        <div class="modal-header">
          <span class="modal-title">${isLogin ? t("profile.login") : t("profile.register")}</span>
          <button class="btn-icon" onclick="this.closest('.modal-overlay').remove()"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M18 6L6 18M6 6l12 12"/></svg></button>
        </div>
        <div class="modal-body">
          <div class="form-group"><label class="form-label">${t("profile.username")}</label><input class="form-input" id="f-auth-user"></div>
          <div class="form-group"><label class="form-label">${t("profile.password")}</label><input class="form-input" id="f-auth-pass" type="password"></div>
        </div>
        <div class="modal-footer">
          <button class="btn-icon" style="width:auto;padding:6px 16px;font-size:13px" onclick="this.closest('.modal-overlay').remove()">${t("clothing.cancel")}</button>
          <button class="fab" style="position:static;width:auto;height:auto;border-radius:8px;padding:6px 20px;font-size:13px" id="f-auth-submit">${isLogin ? t("profile.login") : t("profile.register")}</button>
        </div>
      </div>`;
    document.body.appendChild(overlay);
    overlay.querySelector("#f-auth-user").focus();
    overlay.querySelector("#f-auth-submit").onclick = async () => {
      const username = document.getElementById("f-auth-user").value.trim();
      const password = document.getElementById("f-auth-pass").value;
      if (!username || !password) return;
      const endpoint = isLogin ? "/auth/login" : "/auth/register";
      const result = await apiPost(endpoint, { username, password });
      if (result && result.token) {
        localStorage.setItem("jwt", result.token);
        localStorage.setItem("username", result.username);
        overlay.remove();
        // Auto sync after login
        await fullSync();
        ProfilePage.render();
      }
    };
  },

  logout() {
    localStorage.removeItem("jwt");
    localStorage.removeItem("username");
    localStorage.removeItem("lastSync");
    ProfilePage.render();
  },

  async doSync() {
    const btn = document.getElementById("btn-sync");
    if (btn) btn.textContent = t("profile.syncing");
    await fullSync();
    ProfilePage.render();
  },

  setTheme(theme) {
    localStorage.setItem("theme", theme);
    document.documentElement.setAttribute("data-theme", theme);
    const icon = document.getElementById("icon-theme");
    if (icon) {
      icon.innerHTML = theme === "light"
        ? '<path d="M21 12.79A9 9 0 0111.21 3 7 7 0 1012 21a9 9 0 009-8.21z"/>'
        : '<circle cx="12" cy="12" r="5"/><path d="M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42"/>';
    }
    ProfilePage.render();
  },

  setLang(lang) {
    setLang(lang);
    navigate(currentTab);
  },
};
