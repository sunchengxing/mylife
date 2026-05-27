const MAX_PHOTO_SIZE = 800; // max width/height in px
const JPEG_QUALITY = 0.7;

async function compressPhoto(file) {
  return new Promise((resolve) => {
    const reader = new FileReader();
    reader.onload = (e) => {
      const img = new Image();
      img.onload = () => {
        let w = img.width, h = img.height;
        if (w > MAX_PHOTO_SIZE || h > MAX_PHOTO_SIZE) {
          const ratio = Math.min(MAX_PHOTO_SIZE / w, MAX_PHOTO_SIZE / h);
          w = Math.round(w * ratio);
          h = Math.round(h * ratio);
        }
        const canvas = document.createElement("canvas");
        canvas.width = w;
        canvas.height = h;
        canvas.getContext("2d").drawImage(img, 0, 0, w, h);
        resolve(canvas.toDataURL("image/jpeg", JPEG_QUALITY));
      };
      img.src = e.target.result;
    };
    reader.readAsDataURL(file);
  });
}

function photoInputHTML(currentPhoto) {
  return `
    <div class="form-group">
      <label class="form-label">${t("photo.add")}</label>
      <div id="photo-preview" style="margin-top:4px">
        ${currentPhoto ? `<img src="${currentPhoto}" style="max-width:100%;max-height:160px;border-radius:8px;object-fit:cover"><button type="button" onclick="removePhotoPreview()" style="display:block;margin-top:4px;font-size:12px;color:var(--text-tertiary)">${t("photo.remove")}</button>` : ""}
      </div>
      <input type="file" accept="image/*" id="f-photo" style="margin-top:4px;font-size:12px" onchange="previewPhoto(this)">
    </div>`;
}

function previewPhoto(input) {
  if (!input.files || !input.files[0]) return;
  compressPhoto(input.files[0]).then(dataUrl => {
    const preview = document.getElementById("photo-preview");
    preview.innerHTML = `<img src="${dataUrl}" style="max-width:100%;max-height:160px;border-radius:8px;object-fit:cover"><button type="button" onclick="removePhotoPreview()" style="display:block;margin-top:4px;font-size:12px;color:var(--text-tertiary)">${t("photo.remove")}</button>`;
    preview.dataset.photo = dataUrl;
  });
}

function removePhotoPreview() {
  const preview = document.getElementById("photo-preview");
  preview.innerHTML = "";
  delete preview.dataset.photo;
  const input = document.getElementById("f-photo");
  if (input) input.value = "";
}

function getPhotoFromPreview() {
  const preview = document.getElementById("photo-preview");
  return preview?.dataset?.photo || "";
}
