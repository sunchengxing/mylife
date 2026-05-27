const HousingPage = {
  async render() {
    SearchFilter.clearFilters();
    const items = await dbGetAll("housing");
    const container = document.getElementById("page-content");
    if (items.length === 0) {
      container.innerHTML = `<div class="empty-state fade-in"><svg class="empty-state-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1"><path d="M3 12l9-8 9 8M5 10v10h14V10M9 20v-6h6v6"/></svg><div class="empty-state-text" data-i18n="housing.empty">${t("housing.empty")}</div></div>`;
      setLang(currentLang); return;
    }
    container.innerHTML = SearchFilter.renderBar("housing") +
      SearchFilter.renderSummary(items, "housing") +
      this._renderCards(items);
    setLang(currentLang);
    SearchFilter.bindEvents(HousingPage, "housing");
  },

  _renderCards(items) {
    const filtered = SearchFilter.filterItems(items);
    return filtered.map((item, i) => `
      <div class="card fade-in" style="animation-delay:${Math.min(i * 40, 300)}ms">
        ${item.photo ? `<img src="${item.photo}" style="width:100%;max-height:180px;object-fit:cover;border-radius:var(--radius-md);margin-bottom:8px">` : ""}
        <div style="display:flex;justify-content:space-between;align-items:flex-start">
          <div>
            <div class="card-title">${esc(item.name)}</div>
            <div class="card-meta">${item.category ? `<span class="tag">${esc(item.category)}</span> ` : ""}${item.cost ? `${t("currency")}${esc(item.cost)} · ` : ""}${fmtDate(item.created_at)}</div>
          </div>
          <div style="display:flex;gap:2px">
            <button class="btn-icon" onclick="HousingPage.edit('${item.id}')"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M11 4H4v16h16v-7M18.5 2.5l3 3L10 17H7v-3L18.5 2.5z"/></svg></button>
            <button class="btn-icon" onclick="HousingPage.remove('${item.id}')"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M3 6h18M8 6V4h8v2M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6"/></svg></button>
          </div>
        </div>
        ${item.note ? `<div class="card-body">${esc(item.note)}</div>` : ""}
      </div>`).join("");
  },

  async renderFiltered() {
    const items = await dbGetAll("housing");
    const container = document.getElementById("page-content");
    container.innerHTML = SearchFilter.renderBar("housing") +
      SearchFilter.renderSummary(items, "housing") +
      this._renderCards(items);
    setLang(currentLang);
    SearchFilter.bindEvents(HousingPage, "housing");
  },

  showModal(item) {
    const overlay = document.createElement("div"); overlay.className = "modal-overlay";
    overlay.innerHTML = `<div class="modal fade-in"><div class="modal-header"><span class="modal-title">${t("housing.add")}</span><button class="btn-icon" onclick="this.closest('.modal-overlay').remove()"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M18 6L6 18M6 6l12 12"/></svg></button></div><div class="modal-body"><div class="form-group"><label class="form-label">${t("housing.name")}</label><input class="form-input" id="f-name" value="${esc(item?.name||"")}"></div><div class="form-group"><label class="form-label">${t("housing.category")}</label><input class="form-input" id="f-category" value="${esc(item?.category||"")}"></div><div class="form-group"><label class="form-label">${t("housing.cost")}</label><input class="form-input" id="f-cost" type="number" value="${item?.cost||""}"></div><div class="form-group"><label class="form-label">${t("housing.note")}</label><textarea class="form-input" id="f-note">${esc(item?.note||"")}</textarea></div>${photoInputHTML(item?.photo||"")}</div><div class="modal-footer"><button class="btn-icon" style="width:auto;padding:6px 16px;font-size:13px" onclick="this.closest('.modal-overlay').remove()">${t("housing.cancel")}</button><button class="fab" style="position:static;width:auto;height:auto;border-radius:8px;padding:6px 20px;font-size:13px" id="f-save">${t("housing.save")}</button></div></div>`;
    document.body.appendChild(overlay); overlay.querySelector("#f-name").focus();
    overlay.querySelector("#f-save").onclick = async () => {
      const name = document.getElementById("f-name").value.trim(); if (!name) return;
      await dbPut("housing", { id: item?.id||genId(), name, category: document.getElementById("f-category").value.trim(), cost: document.getElementById("f-cost").value||"", note: document.getElementById("f-note").value.trim(), photo: getPhotoFromPreview(), created_at: item?.created_at||new Date().toISOString(), updated_at: new Date().toISOString() });
      overlay.remove(); HousingPage.render();
    };
  },
  edit(id) { dbGetAll("housing").then(items => { const item = items.find(i => i.id === id); if (item) HousingPage.showModal(item); }); },
  async remove(id) { await dbDelete("housing", id); HousingPage.render(); },
};
