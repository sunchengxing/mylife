const API_BASE = "/api";

function getAuthHeader() {
  const token = localStorage.getItem("jwt");
  return token ? { Authorization: `Bearer ${token}` } : {};
}

async function apiGet(path) {
  try {
    const r = await fetch(`${API_BASE}${path}`, { headers: { ...getAuthHeader() } });
    if (r.status === 401) { onAuthError(); return null; }
    return r.ok ? await r.json() : null;
  } catch { return null; }
}

async function apiPost(path, body) {
  try {
    const r = await fetch(`${API_BASE}${path}`, {
      method: "POST",
      headers: { "Content-Type": "application/json", ...getAuthHeader() },
      body: JSON.stringify(body),
    });
    if (r.status === 401) { onAuthError(); return null; }
    return r.ok ? await r.json() : null;
  } catch { return null; }
}

async function apiPut(path, body) {
  try {
    const r = await fetch(`${API_BASE}${path}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json", ...getAuthHeader() },
      body: JSON.stringify(body),
    });
    if (r.status === 401) { onAuthError(); return null; }
    return r.ok ? await r.json() : null;
  } catch { return null; }
}

async function apiDelete(path) {
  try {
    const r = await fetch(`${API_BASE}${path}`, {
      method: "DELETE",
      headers: { ...getAuthHeader() },
    });
    if (r.status === 401) { onAuthError(); return false; }
    return r.ok;
  } catch { return false; }
}

function onAuthError() {
  localStorage.removeItem("jwt");
  localStorage.removeItem("username");
  if (currentTab === "profile") ProfilePage.render();
}
