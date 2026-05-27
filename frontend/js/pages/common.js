const SearchFilter = {
  currentQuery: "",
  currentCategory: "",
  currentFrom: "",
  currentTo: "",

  renderBar(storeName) {
    return `
      <div class="search-bar" style="margin-bottom:12px">
        <div style="display:flex;gap:8px;align-items:center">
          <div style="flex:1;position:relative">
            <input class="form-input" id="search-input" placeholder="${t("search.placeholder")}" value="${esc(SearchFilter.currentQuery)}" style="padding-left:32px;width:100%">
            <svg style="position:absolute;left:10px;top:50%;transform:translateY(-50%);width:14px;height:14px;color:var(--text-tertiary)" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/></svg>
          </div>
          <button class="btn-icon" id="btn-filter" title="${t("common.filter")}" style="flex-shrink:0">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M4 6h16M7 12h10M10 18h4"/></svg>
          </button>
        </div>
        <div id="filter-panel" style="display:none;margin-top:8px;padding:10px;background:var(--bg-card);border:1px solid var(--border);border-radius:var(--radius-md)">
          <div style="display:grid;grid-template-columns:1fr 1fr;gap:8px">
            <div class="form-group">
              <label class="form-label">${t("filter.category")}</label>
              <input class="form-input" id="filter-category" value="${esc(SearchFilter.currentCategory)}" style="font-size:12px">
            </div>
            <div style="display:flex;gap:4px;align-items:end">
              <div class="form-group" style="flex:1">
                <label class="form-label">${t("filter.from")}</label>
                <input class="form-input" id="filter-from" type="date" value="${SearchFilter.currentFrom}" style="font-size:12px">
              </div>
              <div class="form-group" style="flex:1">
                <label class="form-label">${t("filter.to")}</label>
                <input class="form-input" id="filter-to" type="date" value="${SearchFilter.currentTo}" style="font-size:12px">
              </div>
            </div>
          </div>
          <button onclick="SearchFilter.clearFilters()" style="margin-top:6px;font-size:12px;color:var(--text-secondary)">${t("filter.clear")}</button>
        </div>
      </div>`;
  },

  bindEvents(pageObj, storeName) {
    const input = document.getElementById("search-input");
    if (input) {
      let timer;
      input.addEventListener("input", () => {
        clearTimeout(timer);
        timer = setTimeout(() => {
          SearchFilter.currentQuery = input.value.trim();
          pageObj.renderFiltered();
        }, 250);
      });
    }

    const filterBtn = document.getElementById("btn-filter");
    if (filterBtn) {
      filterBtn.addEventListener("click", () => {
        const panel = document.getElementById("filter-panel");
        panel.style.display = panel.style.display === "none" ? "block" : "none";
      });
    }

    const catInput = document.getElementById("filter-category");
    const fromInput = document.getElementById("filter-from");
    const toInput = document.getElementById("filter-to");
    [catInput, fromInput, toInput].forEach(el => {
      if (el) el.addEventListener("change", () => {
        SearchFilter.currentCategory = catInput?.value || "";
        SearchFilter.currentFrom = fromInput?.value || "";
        SearchFilter.currentTo = toInput?.value || "";
        pageObj.renderFiltered();
      });
    });
  },

  filterItems(items) {
    let filtered = items;
    const q = SearchFilter.currentQuery.toLowerCase();
    if (q) {
      filtered = filtered.filter(i =>
        (i.name || "").toLowerCase().includes(q) ||
        (i.note || "").toLowerCase().includes(q)
      );
    }
    if (SearchFilter.currentCategory) {
      filtered = filtered.filter(i => (i.category || "").toLowerCase() === SearchFilter.currentCategory.toLowerCase());
    }
    if (SearchFilter.currentFrom) {
      filtered = filtered.filter(i => i.created_at >= SearchFilter.currentFrom);
    }
    if (SearchFilter.currentTo) {
      filtered = filtered.filter(i => i.created_at <= SearchFilter.currentTo + "T23:59:59");
    }
    return filtered;
  },

  clearFilters() {
    SearchFilter.currentQuery = "";
    SearchFilter.currentCategory = "";
    SearchFilter.currentFrom = "";
    SearchFilter.currentTo = "";
    const input = document.getElementById("search-input");
    if (input) input.value = "";
    const cat = document.getElementById("filter-category");
    if (cat) cat.value = "";
    const from = document.getElementById("filter-from");
    if (from) from.value = "";
    const to = document.getElementById("filter-to");
    if (to) to.value = "";
  },

  renderSummary(items, storeKey) {
    const now = new Date();
    const monthStart = `${now.getFullYear()}-${String(now.getMonth()+1).padStart(2,"0")}`;
    const monthItems = items.filter(i => i.created_at && i.created_at.startsWith(monthStart));
    const monthCost = monthItems.reduce((s, i) => s + (parseFloat(i.cost) || 0), 0);
    return `
      <div style="display:flex;justify-content:space-between;align-items:center;padding:8px 12px;background:var(--bg-card);border:1px solid var(--border);border-radius:var(--radius-md);margin-bottom:12px;font-size:12px;color:var(--text-secondary)">
        <span>${monthItems.length} ${t(storeKey + ".title")}</span>
        <span>${t("currency")}${monthCost.toFixed(0)}</span>
      </div>`;
  },
};
