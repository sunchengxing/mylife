const I18N = {
  zh: {
    "app.title": "衣食住行",
    "tab.clothing": "衣", "tab.food": "食", "tab.housing": "住", "tab.transport": "行", "tab.profile": "我的",
    "clothing.title": "衣橱", "clothing.empty": "还没有穿搭记录",
    "clothing.add": "添加穿搭", "clothing.name": "名称", "clothing.category": "分类",
    "clothing.season": "季节", "clothing.cost": "花费", "clothing.note": "备注",
    "clothing.save": "保存", "clothing.cancel": "取消",
    "food.title": "美食", "food.empty": "还没有餐饮记录",
    "food.add": "添加记录", "food.name": "餐名", "food.category": "分类",
    "food.calories": "卡路里", "food.cost": "花费", "food.note": "备注",
    "food.save": "保存", "food.cancel": "取消",
    "housing.title": "居住", "housing.empty": "还没有居住记录",
    "housing.add": "添加记录", "housing.name": "名称", "housing.category": "分类",
    "housing.cost": "花费", "housing.note": "备注",
    "housing.save": "保存", "housing.cancel": "取消",
    "transport.title": "出行", "transport.empty": "还没有出行记录",
    "transport.add": "添加记录", "transport.name": "行程", "transport.category": "分类",
    "transport.cost": "花费", "transport.note": "备注",
    "transport.save": "保存", "transport.cancel": "取消",
    "profile.title": "我的", "profile.stats": "数据统计",
    "profile.totalRecords": "总记录", "profile.totalCost": "总花费",
    "profile.clothingCount": "穿搭", "profile.foodCount": "餐饮",
    "profile.housingCount": "居住", "profile.transportCount": "出行",
    "profile.settings": "设置", "profile.theme": "主题",
    "profile.light": "浅色", "profile.dark": "深色",
    "profile.language": "语言", "profile.zh": "中文", "profile.en": "English",
    "profile.sync": "云同步", "profile.syncOff": "未开启",
    "common.delete": "删除", "common.edit": "编辑",
    "category.daily": "日常", "category.special": "特殊",
    "season.spring": "春", "season.summer": "夏", "season.autumn": "秋", "season.winter": "冬",
    "currency": "¥",
  },
  en: {
    "app.title": "MyLife",
    "tab.clothing": "Wear", "tab.food": "Eat", "tab.housing": "Live", "tab.transport": "Go", "tab.profile": "Me",
    "clothing.title": "Wardrobe", "clothing.empty": "No clothing records yet",
    "clothing.add": "Add Item", "clothing.name": "Name", "clothing.category": "Category",
    "clothing.season": "Season", "clothing.cost": "Cost", "clothing.note": "Note",
    "clothing.save": "Save", "clothing.cancel": "Cancel",
    "food.title": "Food", "food.empty": "No food records yet",
    "food.add": "Add Record", "food.name": "Meal", "food.category": "Category",
    "food.calories": "Calories", "food.cost": "Cost", "food.note": "Note",
    "food.save": "Save", "food.cancel": "Cancel",
    "housing.title": "Housing", "housing.empty": "No housing records yet",
    "housing.add": "Add Record", "housing.name": "Name", "housing.category": "Category",
    "housing.cost": "Cost", "housing.note": "Note",
    "housing.save": "Save", "housing.cancel": "Cancel",
    "transport.title": "Travel", "transport.empty": "No travel records yet",
    "transport.add": "Add Record", "transport.name": "Trip", "transport.category": "Category",
    "transport.cost": "Cost", "transport.note": "Note",
    "transport.save": "Save", "transport.cancel": "Cancel",
    "profile.title": "Me", "profile.stats": "Statistics",
    "profile.totalRecords": "Total", "profile.totalCost": "Total Cost",
    "profile.clothingCount": "Wear", "profile.foodCount": "Eat",
    "profile.housingCount": "Live", "profile.transportCount": "Go",
    "profile.settings": "Settings", "profile.theme": "Theme",
    "profile.light": "Light", "profile.dark": "Dark",
    "profile.language": "Language", "profile.zh": "中文", "profile.en": "English",
    "profile.sync": "Cloud Sync", "profile.syncOff": "Disabled",
    "common.delete": "Delete", "common.edit": "Edit",
    "category.daily": "Daily", "category.special": "Special",
    "season.spring": "Spring", "season.summer": "Summer", "season.autumn": "Autumn", "season.winter": "Winter",
    "currency": "$",
  },
};

let currentLang = localStorage.getItem("lang") || "zh";

function t(key) {
  return (I18N[currentLang] && I18N[currentLang][key]) || key;
}

function setLang(lang) {
  currentLang = lang;
  localStorage.setItem("lang", lang);
  document.documentElement.lang = lang === "zh" ? "zh-CN" : "en";
  document.querySelectorAll("[data-i18n]").forEach((el) => {
    el.textContent = t(el.getAttribute("data-i18n"));
  });
}
