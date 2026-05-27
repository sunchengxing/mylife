const DataIO = {
  async exportCSV() {
    const allItems = await Promise.all([
      dbGetAll("clothing"), dbGetAll("food"), dbGetAll("housing"), dbGetAll("transport"),
    ]);
    const stores = ["clothing", "food", "housing", "transport"];
    const rows = [];
    rows.push(["store", "id", "name", "category", "season", "calories", "cost", "note", "created_at"]);
    stores.forEach((store, si) => {
      allItems[si].forEach(item => {
        rows.push([store, item.id, item.name, item.category || "", item.season || "", item.calories || "", item.cost || "", (item.note || "").replace(/\n/g, " "), item.created_at]);
      });
    });
    const csv = rows.map(r => r.map(c => `"${String(c).replace(/"/g, '""')}"`).join(",")).join("\n");
    this._download(csv, "mylife-export.csv", "text/csv");
  },

  async exportJSON() {
    const allItems = await Promise.all([
      dbGetAll("clothing"), dbGetAll("food"), dbGetAll("housing"), dbGetAll("transport"),
    ]);
    const stores = ["clothing", "food", "housing", "transport"];
    const data = {};
    stores.forEach((store, si) => { data[store] = allItems[si]; });
    this._download(JSON.stringify(data, null, 2), "mylife-export.json", "application/json");
  },

  async importFile(file) {
    if (!file) return;
    const text = await file.text();
    const ext = file.name.split(".").pop().toLowerCase();

    if (ext === "json") {
      try {
        const data = JSON.parse(text);
        for (const store of ["clothing", "food", "housing", "transport"]) {
          if (Array.isArray(data[store])) {
            for (const item of data[store]) {
              await dbPut(store, { ...item, id: item.id || genId() });
            }
          }
        }
      } catch { alert("Invalid JSON"); return; }
    } else if (ext === "csv") {
      const lines = text.split("\n").slice(1); // skip header
      for (const line of lines) {
        const cols = this._parseCSVLine(line);
        if (cols.length < 9) continue;
        const [store, id, name, category, season, calories, cost, note, created_at] = cols;
        if (!["clothing", "food", "housing", "transport"].includes(store)) continue;
        await dbPut(store, { id: id || genId(), name, category, season, calories, cost, note, created_at: created_at || new Date().toISOString(), updated_at: new Date().toISOString() });
      }
    }

    navigate(currentTab);
  },

  _parseCSVLine(line) {
    const result = [];
    let current = "", inQuotes = false;
    for (let i = 0; i < line.length; i++) {
      const ch = line[i];
      if (inQuotes) {
        if (ch === '"') {
          if (i + 1 < line.length && line[i + 1] === '"') { current += '"'; i++; }
          else inQuotes = false;
        } else current += ch;
      } else {
        if (ch === '"') inQuotes = true;
        else if (ch === ",") { result.push(current); current = ""; }
        else current += ch;
      }
    }
    result.push(current);
    return result;
  },

  _download(content, filename, type) {
    const blob = new Blob([content], { type });
    const a = document.createElement("a");
    a.href = URL.createObjectURL(blob);
    a.download = filename;
    a.click();
    URL.revokeObjectURL(a.href);
  },
};
