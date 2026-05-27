let currentTab = "clothing";

const pages = {
  clothing: ClothingPage,
  food: FoodPage,
  housing: HousingPage,
  transport: TransportPage,
  profile: ProfilePage,
};

const fabStores = { clothing: "clothing", food: "food", housing: "housing", transport: "transport" };

function esc(s) {
  const d = document.createElement("div");
  d.textContent = s || "";
  return d.innerHTML;
}

function fmtDate(iso) {
  const d = new Date(iso);
  const lang = currentLang === "zh" ? "zh-CN" : "en-US";
  return d.toLocaleDateString(lang, { month: "short", day: "numeric" }) +
    " " + d.toLocaleTimeString(lang, { hour: "2-digit", minute: "2-digit" });
}

function navigate(tab) {
  currentTab = tab;

  // Update tabbar
  document.querySelectorAll(".tabbar-item").forEach((el) => {
    el.classList.toggle("active", el.dataset.tab === tab);
  });

  // Update topbar title
  const titleKey = tab === "profile" ? "profile.title" : `${tab}.title`;
  const titleEl = document.querySelector(".topbar-title");
  if (titleEl) {
    titleEl.textContent = t(titleKey);
    titleEl.setAttribute("data-i18n", titleKey);
  }

  // Update FAB
  const existingFab = document.querySelector(".fab");
  if (existingFab) existingFab.remove();

  if (fabStores[tab]) {
    const fab = document.createElement("button");
    fab.className = "fab";
    fab.innerHTML = `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 5v14M5 12h14"/></svg>`;
    fab.onclick = () => pages[tab].showModal();
    document.body.appendChild(fab);
  }

  // Render page
  pages[tab].render();
}

// ─── Theme ───
function initTheme() {
  const saved = localStorage.getItem("theme") || "light";
  document.documentElement.setAttribute("data-theme", saved);
}

function toggleTheme() {
  const current = document.documentElement.getAttribute("data-theme") || "light";
  const next = current === "light" ? "dark" : "light";
  document.documentElement.setAttribute("data-theme", next);
  localStorage.setItem("theme", next);
  // Update theme icon
  const icon = document.getElementById("icon-theme");
  if (icon) {
    icon.innerHTML = next === "light"
      ? '<path d="M21 12.79A9 9 0 0111.21 3 7 7 0 1012 21a9 9 0 009-8.21z"/>'
      : '<circle cx="12" cy="12" r="5"/><path d="M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42"/>';
  }
  if (currentTab === "profile") ProfilePage.render();
}

// ─── Language ───
function toggleLang() {
  const next = currentLang === "zh" ? "en" : "zh";
  setLang(next);
  navigate(currentTab);
}

// ─── Init ───
document.addEventListener("DOMContentLoaded", () => {
  initTheme();
  setLang(currentLang);

  // Tab clicks
  document.querySelectorAll(".tabbar-item").forEach((el) => {
    el.addEventListener("click", () => navigate(el.dataset.tab));
  });

  // Topbar buttons
  document.getElementById("btn-theme").addEventListener("click", toggleTheme);
  document.getElementById("btn-lang").addEventListener("click", toggleLang);

  // Hash routing
  const hashTab = window.location.hash.slice(1);
  if (pages[hashTab]) navigate(hashTab);
  else navigate("clothing");

  window.addEventListener("hashchange", () => {
    const tab = window.location.hash.slice(1);
    if (pages[tab]) navigate(tab);
  });
});
