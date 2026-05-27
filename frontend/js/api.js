const API_BASE = "/api";

async function apiGet(path) {
  try {
    const r = await fetch(`${API_BASE}${path}`);
    return r.ok ? await r.json() : null;
  } catch { return null; }
}

async function apiPost(path, body) {
  try {
    const r = await fetch(`${API_BASE}${path}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
    return r.ok ? await r.json() : null;
  } catch { return null; }
}

async function apiPut(path, body) {
  try {
    const r = await fetch(`${API_BASE}${path}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
    return r.ok ? await r.json() : null;
  } catch { return null; }
}

async function apiDelete(path) {
  try {
    const r = await fetch(`${API_BASE}${path}`, { method: "DELETE" });
    return r.ok;
  } catch { return false; }
}
