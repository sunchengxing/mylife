const ClothingPage = {
  async render() {
    const items = await dbGetAll("clothing");
    const container = document.getElementById("page-content");

    if (items.length === 0) {
      container.innerHTML = `
        <div class="empty-state fade-in">
          <svg class="empty-state-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1"><path d="M6 2l-4 4 4 2v12h8V8l4-2 4-4h-4l-2 2H8L6 2z"/></svg>
          <div class="empty-state-text" data-i18n="clothing.empty">${t("clothing.empty")}</div>
        </div>`;
      setLang(currentLang);
      return;
    }

    container.innerHTML = items.map((item, i) => `
      <div class="card fade-in" style="animation-delay:${Math.min(i * 40, 300)}ms" data-id="${item.id}">
        <div style="display:flex;justify-content:space-between;align-items:flex-start">
          <div>
            <div class="card-title">${esc(item.name)}</div>
            <div class="card-meta">
              ${item.category ? `<span class="tag">${esc(item.category)}</span> ` : ""}
              ${item.season ? `<span class="tag">${esc(item.season)}</span> ` : ""}
              ${item.cost ? `${t("currency")}${esc(item.cost)}` : ""}
              · ${fmtDate(item.created_at)}
            </div>
          </div>
          <div style="display:flex;gap:2px">
            <button class="btn-icon" onclick="ClothingPage.edit('${item.id}')"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M11 4H4v16h16v-7M18.5 2.5l3 3L10 17H7v-3L18.5 2.5z"/></svg></button>
            <button class="btn-icon" onclick="ClothingPage.remove('${item.id}')"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M3 6h18M8 6V4h8v2M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6"/></svg></button>
          </div>
        </div>
        ${item.note ? `<div class="card-body">${esc(item.note)}</div>` : ""}
      </div>`).join("");

    setLang(currentLang);
  },

  showModal(item) {
    const overlay = document.createElement("div");
    overlay.className = "modal-overlay";
    overlay.innerHTML = `
      <div class="modal fade-in">
        <div class="modal-header">
          <span class="modal-title" data-i18n="clothing.add">${t("clothing.add")}</span>
          <button class="btn-icon" onclick="this.closest('.modal-overlay').remove()"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M18 6L6 18M6 6l12 12"/></svg></button>
        </div>
        <div class="modal-body">
          <div class="form-group"><label class="form-label" data-i18n="clothing.name">${t("clothing.name")}</label><input class="form-input" id="f-name" value="${esc(item?.name || "")}"></div>
          <div class="form-group"><label class="form-label" data-i18n="clothing.category">${t("clothing.category")}</label><input class="form-input" id="f-category" value="${esc(item?.category || "")}"></div>
          <div class="form-group"><label class="form-label" data-i18n="clothing.season">${t("clothing.season")}</label><input class="form-input" id="f-season" value="${esc(item?.season || "")}"></div>
          <div class="form-group"><label class="form-label" data-i18n="clothing.cost">${t("clothing.cost")}</label><input class="form-input" id="f-cost" type="number" value="${item?.cost || ""}"></div>
          <div class="form-group"><label class="form-label" data-i18n="clothing.note">${t("clothing.note")}</label><textarea class="form-input" id="f-note">${esc(item?.note || "")}</textarea></div>
        </div>
        <div class="modal-footer">
          <button class="btn-icon" style="width:auto;padding:6px 16px;font-size:13px" data-i18n="clothing.cancel" onclick="this.closest('.modal-overlay').remove()">${t("clothing.cancel")}</button>
          <button class="fab" style="position:static;width:auto;height:auto;border-radius:8px;padding:6px 20px;font-size:13px;gap:6px" data-i18n="clothing.save" id="f-save">${t("clothing.save")}</button>
        </div>
      </div>`;
    document.body.appendChild(overlay);
    overlay.querySelector("#f-name").focus();

    overlay.querySelector("#f-save").onclick = async () => {
      const name = document.getElementById("f-name").value.trim();
      if (!name) return;
      const record = {
        id: item?.id || genId(),
        name,
        category: document.getElementById("f-category").value.trim(),
        season: document.getElementById("f-season").value.trim(),
        cost: document.getElementById("f-cost").value || "",
        note: document.getElementById("f-note").value.trim(),
        created_at: item?.created_at || new Date().toISOString(),
        updated_at: new Date().toISOString(),
      };
      await dbPut("clothing", record);
      overlay.remove();
      ClothingPage.render();
    };
  },

  edit(id) {
    dbGetAll("clothing").then((items) => {
      const item = items.find((i) => i.id === id);
      if (item) ClothingPage.showModal(item);
    });
  },

  async remove(id) {
    await dbDelete("clothing", id);
    ClothingPage.render();
  },
};
