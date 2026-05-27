const STORES = ["clothing", "food", "housing", "transport"];

function isLoggedIn() {
  return !!localStorage.getItem("jwt");
}

function getLastSync() {
  return localStorage.getItem("lastSync") || "";
}

async function syncPush() {
  if (!isLoggedIn()) return { pushed: 0 };
  const since = getLastSync();
  let all = [];
  for (const store of STORES) {
    const items = await dbGetAll(store);
    const changed = since
      ? items.filter(i => i.updated_at > since)
      : items;
    all.push(...changed.map(i => ({ ...i, store })));
  }
  if (all.length === 0) return { pushed: 0 };
  const result = await apiPost("/sync", { records: all });
  return result || { pushed: 0 };
}

async function syncPull() {
  if (!isLoggedIn()) return { pulled: 0 };
  const since = getLastSync();
  const result = await apiGet(`/sync${since ? `?since=${encodeURIComponent(since)}` : ""}`);
  if (!result || !result.records) return { pulled: 0 };

  for (const rec of result.records) {
    const store = rec.store;
    if (!STORES.includes(store)) continue;
    const local = await dbGetById(store, rec.id);
    // Server-wins conflict resolution
    await dbPut(store, {
      id: rec.id,
      name: rec.name,
      category: rec.category || "",
      season: rec.season || "",
      calories: rec.calories || "",
      cost: rec.cost || "",
      note: rec.note || "",
      photo: rec.photo || "",
      created_at: rec.created_at,
      updated_at: rec.updated_at,
    });
  }

  const now = new Date().toISOString();
  localStorage.setItem("lastSync", now);
  return { pulled: result.records.length };
}

async function fullSync() {
  const pushResult = await syncPush();
  const pullResult = await syncPull();
  return { pushed: pushResult.pushed || 0, pulled: pullResult.pulled || 0 };
}
