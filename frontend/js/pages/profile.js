const ProfilePage = {
  async render() {
    const [cc, fc, hc, tc] = await Promise.all([
      dbCount("clothing"), dbCount("food"), dbCount("housing"), dbCount("transport"),
    ]);
    const total = cc + fc + hc + tc;
    const container = document.getElementById("page-content");
    const theme = localStorage.getItem("theme") || "light";
    const lang = currentLang;

    container.innerHTML = `
      <div class="fade-in" style="max-width:480px;margin:0 auto">
        <!-- Stats -->
        <h2 style="font-size:13px;font-weight:600;color:var(--text-secondary);text-transform:uppercase;letter-spacing:0.5px;margin-bottom:12px" data-i18n="profile.stats">${t("profile.stats")}</h2>
        <div class="stats-grid" style="margin-bottom:12px">
          <div class="stat-card"><div class="stat-value">${total}</div><div class="stat-label" data-i18n="profile.totalRecords">${t("profile.totalRecords")}</div></div>
          <div class="stat-card"><div class="stat-value">${t("currency")}--</div><div class="stat-label" data-i18n="profile.totalCost">${t("profile.totalCost")}</div></div>
        </div>
        <div class="stats-grid" style="margin-bottom:24px">
          <div class="stat-card"><div class="stat-value">${cc}</div><div class="stat-label" data-i18n="profile.clothingCount">${t("profile.clothingCount")}</div></div>
          <div class="stat-card"><div class="stat-value">${fc}</div><div class="stat-label" data-i18n="profile.foodCount">${t("profile.foodCount")}</div></div>
          <div class="stat-card"><div class="stat-value">${hc}</div><div class="stat-label" data-i18n="profile.housingCount">${t("profile.housingCount")}</div></div>
          <div class="stat-card"><div class="stat-value">${tc}</div><div class="stat-label" data-i18n="profile.transportCount">${t("profile.transportCount")}</div></div>
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
          <div style="display:flex;justify-content:space-between;align-items:center;padding:8px 0">
            <span style="font-size:14px" data-i18n="profile.sync">${t("profile.sync")}</span>
            <span style="font-size:12px;color:var(--text-tertiary)" data-i18n="profile.syncOff">${t("profile.syncOff")}</span>
          </div>
        </div>
      </div>`;
    setLang(currentLang);
  },
  setTheme(theme) {
    localStorage.setItem("theme", theme);
    document.documentElement.setAttribute("data-theme", theme);
    ProfilePage.render();
  },
  setLang(lang) {
    setLang(lang);
    navigate(currentTab);
  },
};
