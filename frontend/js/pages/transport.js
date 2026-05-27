const TransportPage = {
  async render() {
    const items = await dbGetAll("transport");
    const container = document.getElementById("page-content");
    if (items.length === 0) {
      container.innerHTML = `<div class="empty-state fade-in"><svg class="empty-state-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1"><circle cx="6" cy="17" r="2"/><circle cx="18" cy="17" r="2"/><path d="M6 15H4V8l3-5h8l3 5v7h-2M4 8h14M6 15h12M10 3v5M14 3v5"/></svg><div class="empty-state-text" data-i18n="transport.empty">${t("transport.empty")}</div></div>`;
      setLang(currentLang); return;
    }
    container.innerHTML = items.map((item, i) => `
      <div class="card fade-in" style="animation-delay:${Math.min(i * 40, 300)}ms">
        <div style="display:flex;justify-content:space-between;align-items:flex-start">
          <div>
            <div class="card-title">${esc(item.name)}</div>
            <div class="card-meta">${item.category ? `<span class="tag">${esc(item.category)}</span> ` : ""}${item.cost ? `${t("currency")}${esc(item.cost)} · ` : ""}${fmtDate(item.created_at)}</div>
          </div>
          <div style="display:flex;gap:2px">
            <button class="btn-icon" onclick="TransportPage.edit('${item.id}')"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M11 4H4v16h16v-7M18.5 2.5l3 3L10 17H7v-3L18.5 2.5z"/></svg></button>
            <button class="btn-icon" onclick="TransportPage.remove('${item.id}')"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M3 6h18M8 6V4h8v2M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6"/></svg></button>
          </div>
        </div>
        ${item.note ? `<div class="card-body">${esc(item.note)}</div>` : ""}
      </div>`).join("");
    setLang(currentLang);
  },
  showModal(item) {
    const overlay = document.createElement("div"); overlay.className = "modal-overlay";
    overlay.innerHTML = `<div class="modal fade-in"><div class="modal-header"><span class="modal-title">${t("transport.add")}</span><button class="btn-icon" onclick="this.closest('.modal-overlay').remove()"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M18 6L6 18M6 6l12 12"/></svg></button></div><div class="modal-body"><div class="form-group"><label class="form-label">${t("transport.name")}</label><input class="form-input" id="f-name" value="${esc(item?.name||"")}"></div><div class="form-group"><label class="form-label">${t("transport.category")}</label><input class="form-input" id="f-category" value="${esc(item?.category||"")}"></div><div class="form-group"><label class="form-label">${t("transport.cost")}</label><input class="form-input" id="f-cost" type="number" value="${item?.cost||""}"></div><div class="form-group"><label class="form-label">${t("transport.note")}</label><textarea class="form-input" id="f-note">${esc(item?.note||"")}</textarea></div></div><div class="modal-footer"><button class="btn-icon" style="width:auto;padding:6px 16px;font-size:13px" onclick="this.closest('.modal-overlay').remove()">${t("transport.cancel")}</button><button class="fab" style="position:static;width:auto;height:auto;border-radius:8px;padding:6px 20px;font-size:13px" id="f-save">${t("transport.save")}</button></div></div>`;
    document.body.appendChild(overlay); overlay.querySelector("#f-name").focus();
    overlay.querySelector("#f-save").onclick = async () => {
      const name = document.getElementById("f-name").value.trim(); if (!name) return;
      await dbPut("transport", { id: item?.id||genId(), name, category: document.getElementById("f-category").value.trim(), cost: document.getElementById("f-cost").value||"", note: document.getElementById("f-note").value.trim(), created_at: item?.created_at||new Date().toISOString(), updated_at: new Date().toISOString() });
      overlay.remove(); TransportPage.render();
    };
  },
  edit(id) { dbGetAll("transport").then(items => { const item = items.find(i => i.id === id); if (item) TransportPage.showModal(item); }); },
  async remove(id) { await dbDelete("transport", id); TransportPage.render(); },
};
