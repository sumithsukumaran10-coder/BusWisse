/**
 * BusWise API Client & UI Helper Utilities
 * Provides standardized Fetch API methods, JSON handling, and toast notifications.
 */

const api = {
  async get(endpoint) {
    return this.request(endpoint, { method: "GET" });
  },

  async post(endpoint, data) {
    return this.request(endpoint, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });
  },

  async put(endpoint, data) {
    return this.request(endpoint, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });
  },

  async delete(endpoint) {
    return this.request(endpoint, { method: "DELETE" });
  },

  async request(endpoint, options = {}) {
    try {
      const response = await fetch(endpoint, options);
      const json = await response.json();

      if (!response.ok) {
        let errorMsg = json.message || "An unexpected error occurred.";
        if (json.validationErrors && Object.keys(json.validationErrors).length > 0) {
          const fieldMsgs = Object.entries(json.validationErrors)
            .map(([field, msg]) => `• ${field}: ${msg}`)
            .join("\n");
          errorMsg = `${errorMsg}\n${fieldMsgs}`;
        }
        throw new Error(errorMsg);
      }

      return json.data !== undefined ? json.data : json;
    } catch (err) {
      console.error(`API Error on [${options.method || "GET"} ${endpoint}]:`, err);
      showToast(err.message || "Network or server connection failed", "danger");
      throw err;
    }
  },
};

/**
 * Toast Notification System
 */
function showToast(message, type = "success") {
  const container = document.getElementById("toast-container");
  if (!container) return;

  const toastId = "toast-" + Date.now();
  const iconClass =
    type === "success"
      ? "bi-check-circle-fill text-success"
      : type === "danger"
      ? "bi-exclamation-octagon-fill text-danger"
      : type === "warning"
      ? "bi-exclamation-triangle-fill text-warning"
      : "bi-info-circle-fill text-primary";

  const toastHtml = `
    <div id="${toastId}" class="toast align-items-center border-0 shadow-lg mb-2" role="alert" aria-live="assertive" aria-atomic="true">
      <div class="d-flex p-2 bg-white rounded-3 border-start border-4 ${type === 'danger' ? 'border-danger' : type === 'warning' ? 'border-warning' : 'border-success'}">
        <div class="toast-body d-flex align-items-start gap-2 text-dark">
          <i class="bi ${iconClass} fs-5"></i>
          <div>
            <strong>${type.toUpperCase()}</strong>
            <div style="white-space: pre-line; font-size: 0.88rem;">${escapeHtml(message)}</div>
          </div>
        </div>
        <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
      </div>
    </div>
  `;

  container.insertAdjacentHTML("beforeend", toastHtml);
  const toastElem = document.getElementById(toastId);
  const bsToast = new bootstrap.Toast(toastElem, { delay: 6000 });
  bsToast.show();

  toastElem.addEventListener("hidden.bs.toast", () => {
    toastElem.remove();
  });
}

/**
 * Format Currency (INR)
 */
function formatCurrency(amount) {
  if (amount === null || amount === undefined) return "₹0.00";
  const num = typeof amount === "number" ? amount : parseFloat(amount);
  return new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 2,
  }).format(isNaN(num) ? 0 : num);
}

/**
 * Format Numbers with commas
 */
function formatNumber(num) {
  if (num === null || num === undefined) return "0";
  return new Intl.NumberFormat("en-IN").format(num);
}

/**
 * Format Date Time
 */
function formatDateTime(dtStr) {
  if (!dtStr) return "-";
  const dt = new Date(dtStr);
  if (isNaN(dt.getTime())) return dtStr;
  return dt.toLocaleString("en-IN", {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

function formatDate(dStr) {
  if (!dStr) return "-";
  const d = new Date(dStr);
  if (isNaN(d.getTime())) return dStr;
  return d.toLocaleDateString("en-IN", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
}

function escapeHtml(str) {
  if (!str) return "";
  return String(str)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}
