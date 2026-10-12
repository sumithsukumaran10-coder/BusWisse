/**
 * BusWise Frontend Application Master Controller
 * Handles SPA tab switching, Chart.js graphs, CRUD modals, and REST API communication.
 */

// Application State
const state = {
  currentTab: "dashboard",
  charts: {},
  buses: { page: 0, size: 10, search: "", status: "" },
  routes: { page: 0, size: 10, search: "" },
  trips: { page: 0, size: 10, status: "", busId: "", routeId: "" },
  fuel: { page: 0, size: 10, busId: "" },
  expenses: { page: 0, size: 10, category: "" },
};

document.addEventListener("DOMContentLoaded", () => {
  setupNavigation();
  loadCurrentTab();
  setupGlobalEvents();
});

/**
 * Navigation Router
 */
function setupNavigation() {
  const navLinks = document.querySelectorAll("#sidebar .nav-link");
  navLinks.forEach((link) => {
    link.addEventListener("click", (e) => {
      e.preventDefault();
      const targetTab = link.getAttribute("data-tab");
      switchTab(targetTab);
    });
  });

  const sidebarToggle = document.getElementById("sidebar-toggle");
  if (sidebarToggle) {
    sidebarToggle.addEventListener("click", () => {
      document.getElementById("sidebar").classList.toggle("show");
    });
  }
}

function switchTab(tabName) {
  state.currentTab = tabName;

  // Update sidebar active class
  document.querySelectorAll("#sidebar .nav-link").forEach((l) => {
    l.classList.toggle("active", l.getAttribute("data-tab") === tabName);
  });

  // Update tab content visibility
  document.querySelectorAll(".tab-pane").forEach((pane) => {
    pane.classList.remove("active");
    if (pane.id === `tab-${tabName}`) {
      pane.classList.add("active");
    }
  });

  // Update Top Navbar Heading
  const titles = {
    dashboard: ["Fleet Operations Dashboard", "Real-time fleet status, revenue, fuel & demand metrics"],
    buses: ["Bus Fleet Management", "Complete vehicle registration, specifications and status monitoring"],
    routes: ["Transit Corridor Management", "Route networks, terminal stations, and corridor metrics"],
    trips: ["Trip Scheduling & Dispatch", "Live dispatching, lifecycle transitions, and passenger loads"],
    passengers: ["Passenger Demand Analytics", "Seat occupancy rates, high/low demand corridors & recommendations"],
    fuel: ["Fuel Efficiency & Logs", "Refueling logs, km/L fleet benchmarks, and anomaly detection"],
    profitability: ["Operating Profitability", "Trip economics, revenue vs operating costs, and route margins"],
    sustainability: ["Green Fleet & Emissions", "Fuel-derived CO₂ emissions estimates & decarbonization pathways"],
    alerts: ["Operational Alerts", "Automated rules-based alerts for maintenance, fuel & capacity anomalies"],
    reports: ["Reports & Data Exports", "Download verified operational datasets in standard CSV format"],
  };

  const [title, sub] = titles[tabName] || ["BusWise", "Intelligent Bus Transport System"];
  document.getElementById("page-title").textContent = title;
  document.getElementById("page-subtitle").textContent = sub;

  loadCurrentTab();
}

function loadCurrentTab() {
  switch (state.currentTab) {
    case "dashboard":
      loadDashboard();
      break;
    case "buses":
      loadBuses();
      break;
    case "routes":
      loadRoutes();
      break;
    case "trips":
      loadTrips();
      break;
    case "passengers":
      loadPassengers();
      break;
    case "fuel":
      loadFuel();
      break;
    case "profitability":
      loadProfitability();
      break;
    case "sustainability":
      loadSustainability();
      break;
    case "alerts":
      loadAlerts();
      break;
    case "reports":
      // Static links already present
      break;
  }
  updateGlobalAlertBadge();
}

/**
 * -----------------------------------------------------------------------------
 * MODULE 1: DASHBOARD
 * -----------------------------------------------------------------------------
 */
async function loadDashboard() {
  try {
    const summary = await api.get("/api/dashboard/summary");

    // Fleet KPIs
    document.getElementById("kpi-total-buses").textContent = summary.totalBuses || 0;
    document.getElementById("kpi-available-buses").textContent = summary.availableBuses || 0;
    document.getElementById("kpi-ontrip-buses").textContent = summary.onTripBuses || 0;
    document.getElementById("kpi-maint-buses").textContent = summary.maintenanceBuses || 0;

    // Operation KPIs
    document.getElementById("kpi-total-routes").textContent = summary.totalRoutes || 0;
    document.getElementById("kpi-total-passengers").textContent = formatNumber(summary.totalPassengersCarried);
    document.getElementById("kpi-avg-occupancy").textContent = `${summary.averageOccupancyRate || 0}%`;
    document.getElementById("kpi-avg-efficiency").textContent = `${summary.averageFuelEfficiency || 0} km/L`;

    // Financial KPIs
    document.getElementById("kpi-total-revenue").textContent = formatCurrency(summary.totalRevenue);
    document.getElementById("kpi-operating-cost").textContent = formatCurrency(summary.totalOperatingCost);
    document.getElementById("kpi-net-profit").textContent = formatCurrency(summary.estimatedOperatingProfit);
    document.getElementById("kpi-profit-margin").textContent = `${summary.profitMarginPercentage || 0}%`;

    // Alert Badge in Dashboard
    const alertCountEl = document.getElementById("kpi-active-alerts");
    if (alertCountEl) alertCountEl.textContent = summary.activeAlertsCount || 0;

    // Render Charts
    renderBusStatusChart(summary.busStatusDistribution);
    renderRevenueExpenseChart(summary.totalRevenue, summary.totalFuelCost, summary.totalOtherExpenses);
    renderPassengerDemandChart(summary.routePassengerDemand);

    // Recent Trips Table
    renderRecentTripsTable(summary.recentTrips || []);

    // Recent Alerts Widget
    renderDashboardAlerts(summary.recentAlerts || []);
  } catch (err) {
    console.error("Error loading dashboard metrics:", err);
  }
}

function renderBusStatusChart(data) {
  const ctx = document.getElementById("chart-bus-status");
  if (!ctx) return;
  if (state.charts.busStatus) state.charts.busStatus.destroy();

  const labels = Object.keys(data || { Available: 0, "On Trip": 0, Maintenance: 0 });
  const values = Object.values(data || { Available: 0, "On Trip": 0, Maintenance: 0 });

  state.charts.busStatus = new Chart(ctx, {
    type: "doughnut",
    data: {
      labels: labels,
      datasets: [
        {
          data: values,
          backgroundColor: ["#10b981", "#3b82f6", "#ef4444"],
          borderWidth: 2,
          borderColor: "#ffffff",
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { position: "bottom" },
      },
    },
  });
}

function renderRevenueExpenseChart(revenue, fuelCost, otherCost) {
  const ctx = document.getElementById("chart-revenue-expenses");
  if (!ctx) return;
  if (state.charts.revenueExpense) state.charts.revenueExpense.destroy();

  state.charts.revenueExpense = new Chart(ctx, {
    type: "bar",
    data: {
      labels: ["Total Revenue", "Fuel Expenses", "Other Operating Expenses"],
      datasets: [
        {
          label: "Amount (INR)",
          data: [revenue || 0, fuelCost || 0, otherCost || 0],
          backgroundColor: ["#0284c7", "#f59e0b", "#64748b"],
          borderRadius: 6,
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: false },
        tooltip: {
          callbacks: {
            label: (ctx) => formatCurrency(ctx.raw),
          },
        },
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            callback: (v) => "₹" + (v >= 1000 ? v / 1000 + "k" : v),
          },
        },
      },
    },
  });
}

function renderPassengerDemandChart(data) {
  const ctx = document.getElementById("chart-passenger-demand");
  if (!ctx) return;
  if (state.charts.passengerDemand) state.charts.passengerDemand.destroy();

  const labels = Object.keys(data || {});
  const values = Object.values(data || {});

  state.charts.passengerDemand = new Chart(ctx, {
    type: "bar",
    data: {
      labels: labels.length ? labels : ["No Data"],
      datasets: [
        {
          label: "Passengers Carried",
          data: values.length ? values : [0],
          backgroundColor: "#0ea5e9",
          borderRadius: 6,
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: { y: { beginAtZero: true } },
    },
  });
}

function renderRecentTripsTable(trips) {
  const tbody = document.getElementById("recent-trips-tbody");
  if (!tbody) return;

  if (!trips || trips.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted py-4">No trips scheduled yet. Click 'Schedule Trip' to begin.</td></tr>`;
    return;
  }

  tbody.innerHTML = trips
    .map(
      (t) => `
      <tr>
        <td><strong>#${t.id}</strong></td>
        <td><span class="badge bg-light text-dark border">${escapeHtml(t.busRegistrationNumber)}</span></td>
        <td>${escapeHtml(t.routeName)}</td>
        <td>${formatDateTime(t.departureTime)}</td>
        <td>${getStatusBadge(t.status)}</td>
        <td>${t.passengerCount} <small class="text-muted">(${t.occupancyRate}%)</small></td>
        <td><strong>${formatCurrency(t.ticketRevenue)}</strong></td>
      </tr>
    `
    )
    .join("");
}

function renderDashboardAlerts(alerts) {
  const container = document.getElementById("dashboard-alerts-container");
  if (!container) return;

  if (!alerts || alerts.length === 0) {
    container.innerHTML = `<div class="text-muted text-center py-4"><i class="bi bi-shield-check fs-2 text-success"></i><p class="mt-2 mb-0">All systems normal. No active operational alerts.</p></div>`;
    return;
  }

  container.innerHTML = alerts
    .map(
      (a) => `
      <div class="p-3 mb-2 rounded-3 border-start border-4 ${
        a.severity === "CRITICAL"
          ? "border-danger bg-danger-subtle"
          : a.severity === "WARNING"
          ? "border-warning bg-warning-subtle"
          : "border-primary bg-primary-subtle"
      }">
        <div class="d-flex justify-content-between align-items-center mb-1">
          <strong class="text-dark fs-6">${escapeHtml(a.title)}</strong>
          <span class="badge ${
            a.severity === "CRITICAL" ? "bg-danger" : a.severity === "WARNING" ? "bg-warning text-dark" : "bg-primary"
          }">${a.severity}</span>
        </div>
        <p class="mb-1 text-secondary small">${escapeHtml(a.message)}</p>
        <div class="d-flex justify-content-between align-items-center">
          <small class="text-muted">${formatDateTime(a.createdAt)}</small>
          <button class="btn btn-sm btn-outline-secondary py-0 px-2" onclick="acknowledgeAlertItem(${a.id})">Acknowledge</button>
        </div>
      </div>
    `
    )
    .join("");
}

/**
 * -----------------------------------------------------------------------------
 * MODULE 2: BUS FLEET MANAGEMENT
 * -----------------------------------------------------------------------------
 */
async function loadBuses() {
  const tbody = document.getElementById("buses-tbody");
  if (!tbody) return;

  const search = document.getElementById("bus-search")?.value || "";
  const status = document.getElementById("bus-status-filter")?.value || "";

  try {
    const pageData = await api.get(`/api/buses?page=${state.buses.page}&size=${state.buses.size}&search=${encodeURIComponent(search)}&status=${encodeURIComponent(status)}`);
    const buses = pageData.content || [];

    if (buses.length === 0) {
      tbody.innerHTML = `<tr><td colspan="8" class="empty-state">
        <i class="bi bi-bus-front"></i>
        <h5>No buses found</h5>
        <p>Register new buses to build your fleet inventory.</p>
        <button class="btn btn-primary" onclick="openAddBusModal()">Add New Bus</button>
      </td></tr>`;
      renderPagination("buses-pagination", pageData, (p) => { state.buses.page = p; loadBuses(); });
      return;
    }

    tbody.innerHTML = buses
      .map(
        (b) => `
        <tr>
          <td><strong>#${b.id}</strong></td>
          <td><span class="badge bg-dark-subtle text-dark border fw-bold">${escapeHtml(b.registrationNumber)}</span></td>
          <td>${escapeHtml(b.busModel)}</td>
          <td>${b.seatingCapacity} seats</td>
          <td><span class="badge bg-secondary-subtle text-secondary">${b.fuelType}</span></td>
          <td>${formatNumber(b.odometerReading)} km</td>
          <td>${getBusStatusBadge(b.status)}</td>
          <td>
            <div class="btn-group btn-group-sm">
              <button class="btn btn-outline-primary" title="Edit Bus" onclick="openEditBusModal(${b.id})"><i class="bi bi-pencil"></i></button>
              <button class="btn btn-outline-danger" title="Delete Bus" onclick="deleteBusItem(${b.id}, '${escapeHtml(b.registrationNumber)}')"><i class="bi bi-trash"></i></button>
            </div>
          </td>
        </tr>
      `
      )
      .join("");

    renderPagination("buses-pagination", pageData, (p) => { state.buses.page = p; loadBuses(); });
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="8" class="text-danger text-center py-4">Failed to load buses: ${escapeHtml(err.message)}</td></tr>`;
  }
}

function openAddBusModal() {
  document.getElementById("bus-modal-form").reset();
  document.getElementById("bus-id").value = "";
  document.getElementById("busModalLabel").textContent = "Register New Fleet Bus";
  document.getElementById("bus-status").value = "AVAILABLE";
  const modal = new bootstrap.Modal(document.getElementById("busModal"));
  modal.show();
}

async function openEditBusModal(id) {
  try {
    const bus = await api.get(`/api/buses/${id}`);
    document.getElementById("bus-id").value = bus.id;
    document.getElementById("bus-registration").value = bus.registrationNumber;
    document.getElementById("bus-model").value = bus.busModel;
    document.getElementById("bus-capacity").value = bus.seatingCapacity;
    document.getElementById("bus-fuel-type").value = bus.fuelType;
    document.getElementById("bus-odometer").value = bus.odometerReading;
    document.getElementById("bus-status").value = bus.status;
    document.getElementById("bus-notes").value = bus.notes || "";
    document.getElementById("busModalLabel").textContent = `Edit Bus #${bus.registrationNumber}`;
    const modal = new bootstrap.Modal(document.getElementById("busModal"));
    modal.show();
  } catch (err) {
    showToast("Could not load bus details", "danger");
  }
}

async function saveBusForm(e) {
  e.preventDefault();
  const id = document.getElementById("bus-id").value;
  const payload = {
    registrationNumber: document.getElementById("bus-registration").value.trim(),
    busModel: document.getElementById("bus-model").value.trim(),
    seatingCapacity: parseInt(document.getElementById("bus-capacity").value, 10),
    fuelType: document.getElementById("bus-fuel-type").value,
    odometerReading: parseFloat(document.getElementById("bus-odometer").value || 0),
    status: document.getElementById("bus-status").value,
    notes: document.getElementById("bus-notes").value.trim(),
  };

  try {
    if (id) {
      await api.put(`/api/buses/${id}`, payload);
      showToast("Bus details updated successfully!");
    } else {
      await api.post("/api/buses", payload);
      showToast("New bus registered successfully!");
    }
    bootstrap.Modal.getInstance(document.getElementById("busModal")).hide();
    loadBuses();
  } catch (err) {
    // Handled in api client toast
  }
}

async function deleteBusItem(id, reg) {
  if (!confirm(`Are you sure you want to delete bus ${reg}? This action cannot be undone.`)) {
    return;
  }
  try {
    await api.delete(`/api/buses/${id}`);
    showToast(`Bus ${reg} deleted successfully.`);
    loadBuses();
  } catch (err) {
    // Error handled
  }
}

/**
 * -----------------------------------------------------------------------------
 * MODULE 3: ROUTE MANAGEMENT
 * -----------------------------------------------------------------------------
 */
async function loadRoutes() {
  const tbody = document.getElementById("routes-tbody");
  if (!tbody) return;

  const search = document.getElementById("route-search")?.value || "";

  try {
    const pageData = await api.get(`/api/routes?page=${state.routes.page}&size=${state.routes.size}&search=${encodeURIComponent(search)}`);
    const routes = pageData.content || [];

    if (routes.length === 0) {
      tbody.innerHTML = `<tr><td colspan="7" class="empty-state">
        <i class="bi bi-map"></i>
        <h5>No Routes Configured</h5>
        <p>Define origin-destination corridors to schedule trips.</p>
        <button class="btn btn-primary" onclick="openAddRouteModal()">Create Route</button>
      </td></tr>`;
      renderPagination("routes-pagination", pageData, (p) => { state.routes.page = p; loadRoutes(); });
      return;
    }

    tbody.innerHTML = routes
      .map(
        (r) => `
        <tr>
          <td><span class="badge bg-primary-subtle text-primary fw-bold">${escapeHtml(r.routeNumber)}</span></td>
          <td><strong>${escapeHtml(r.routeName)}</strong></td>
          <td>${escapeHtml(r.sourceLocation)}</td>
          <td>${escapeHtml(r.destinationLocation)}</td>
          <td>${r.distanceKm} km</td>
          <td>${Math.floor(r.estimatedDurationMinutes / 60)}h ${r.estimatedDurationMinutes % 60}m</td>
          <td>
            <div class="btn-group btn-group-sm">
              <button class="btn btn-outline-primary" title="Edit Route" onclick="openEditRouteModal(${r.id})"><i class="bi bi-pencil"></i></button>
              <button class="btn btn-outline-danger" title="Delete Route" onclick="deleteRouteItem(${r.id}, '${escapeHtml(r.routeNumber)}')"><i class="bi bi-trash"></i></button>
            </div>
          </td>
        </tr>
      `
      )
      .join("");

    renderPagination("routes-pagination", pageData, (p) => { state.routes.page = p; loadRoutes(); });
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="7" class="text-danger text-center py-4">Failed to load routes: ${escapeHtml(err.message)}</td></tr>`;
  }
}

function openAddRouteModal() {
  document.getElementById("route-modal-form").reset();
  document.getElementById("route-id").value = "";
  document.getElementById("routeModalLabel").textContent = "Add New Transit Route Corridor";
  const modal = new bootstrap.Modal(document.getElementById("routeModal"));
  modal.show();
}

async function openEditRouteModal(id) {
  try {
    const route = await api.get(`/api/routes/${id}`);
    document.getElementById("route-id").value = route.id;
    document.getElementById("route-number").value = route.routeNumber;
    document.getElementById("route-name").value = route.routeName;
    document.getElementById("route-source").value = route.sourceLocation;
    document.getElementById("route-destination").value = route.destinationLocation;
    document.getElementById("route-distance").value = route.distanceKm;
    document.getElementById("route-duration").value = route.estimatedDurationMinutes;
    document.getElementById("route-description").value = route.description || "";
    document.getElementById("routeModalLabel").textContent = `Edit Route ${route.routeNumber}`;
    const modal = new bootstrap.Modal(document.getElementById("routeModal"));
    modal.show();
  } catch (err) {
    showToast("Could not load route details", "danger");
  }
}

async function saveRouteForm(e) {
  e.preventDefault();
  const id = document.getElementById("route-id").value;
  const payload = {
    routeNumber: document.getElementById("route-number").value.trim(),
    routeName: document.getElementById("route-name").value.trim(),
    sourceLocation: document.getElementById("route-source").value.trim(),
    destinationLocation: document.getElementById("route-destination").value.trim(),
    distanceKm: parseFloat(document.getElementById("route-distance").value),
    estimatedDurationMinutes: parseInt(document.getElementById("route-duration").value, 10),
    description: document.getElementById("route-description").value.trim(),
  };

  try {
    if (id) {
      await api.put(`/api/routes/${id}`, payload);
      showToast("Route updated successfully!");
    } else {
      await api.post("/api/routes", payload);
      showToast("New route created successfully!");
    }
    bootstrap.Modal.getInstance(document.getElementById("routeModal")).hide();
    loadRoutes();
  } catch (err) {
    // Handled in api client toast
  }
}

async function deleteRouteItem(id, routeNo) {
  if (!confirm(`Are you sure you want to delete route ${routeNo}?`)) return;
  try {
    await api.delete(`/api/routes/${id}`);
    showToast(`Route ${routeNo} deleted.`);
    loadRoutes();
  } catch (err) {
    // Handled
  }
}

/**
 * -----------------------------------------------------------------------------
 * MODULE 4: TRIP MANAGEMENT & DISPATCH
 * -----------------------------------------------------------------------------
 */
async function loadTrips() {
  const tbody = document.getElementById("trips-tbody");
  if (!tbody) return;

  const status = document.getElementById("trip-status-filter")?.value || "";
  const busId = document.getElementById("trip-bus-filter")?.value || "";
  const routeId = document.getElementById("trip-route-filter")?.value || "";

  try {
    const pageData = await api.get(`/api/trips?page=${state.trips.page}&size=${state.trips.size}&status=${encodeURIComponent(status)}&busId=${encodeURIComponent(busId)}&routeId=${encodeURIComponent(routeId)}`);
    const trips = pageData.content || [];

    if (trips.length === 0) {
      tbody.innerHTML = `<tr><td colspan="9" class="empty-state">
        <i class="bi bi-calendar-check"></i>
        <h5>No Trips Found</h5>
        <p>Schedule a trip by assigning an available fleet bus to a route.</p>
        <button class="btn btn-primary" onclick="openScheduleTripModal()">Schedule New Trip</button>
      </td></tr>`;
      renderPagination("trips-pagination", pageData, (p) => { state.trips.page = p; loadTrips(); });
      return;
    }

    tbody.innerHTML = trips
      .map(
        (t) => `
        <tr>
          <td><strong>#${t.id}</strong></td>
          <td><span class="badge bg-light text-dark border">${escapeHtml(t.busRegistrationNumber)}</span></td>
          <td>${escapeHtml(t.routeName)}</td>
          <td>
            <div>${formatDateTime(t.departureTime)}</div>
            <small class="text-muted">Exp: ${formatDateTime(t.expectedArrivalTime)}</small>
          </td>
          <td>${getStatusBadge(t.status)}</td>
          <td>
            ${t.passengerCount} / ${t.busCapacity}
            <div class="progress mt-1" style="height: 5px;">
              <div class="progress-bar ${t.occupancyRate >= 90 ? 'bg-danger' : t.occupancyRate >= 60 ? 'bg-success' : 'bg-warning'}" style="width: ${Math.min(t.occupancyRate, 100)}%"></div>
            </div>
          </td>
          <td>${formatCurrency(t.ticketRevenue)}</td>
          <td>${formatCurrency(t.estimatedProfit)}</td>
          <td>
            <div class="btn-group btn-group-sm">
              <button class="btn btn-outline-primary" title="Update Status / Passengers" onclick="openUpdateTripModal(${t.id})">
                <i class="bi bi-arrow-repeat"></i> Update
              </button>
              <button class="btn btn-outline-danger" title="Delete Trip" onclick="deleteTripItem(${t.id})">
                <i class="bi bi-trash"></i>
              </button>
            </div>
          </td>
        </tr>
      `
      )
      .join("");

    renderPagination("trips-pagination", pageData, (p) => { state.trips.page = p; loadTrips(); });
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="9" class="text-danger text-center py-4">Failed to load trips: ${escapeHtml(err.message)}</td></tr>`;
  }
}

async function openScheduleTripModal() {
  document.getElementById("trip-modal-form").reset();
  try {
    // Populate Buses dropdown (AVAILABLE buses)
    const buses = await api.get("/api/buses/all");
    const busSelect = document.getElementById("trip-bus-select");
    busSelect.innerHTML = buses
      .map((b) => `<option value="${b.id}" ${b.status !== "AVAILABLE" ? "disabled" : ""}>${b.registrationNumber} (${b.busModel} - ${b.status})</option>`)
      .join("");

    // Populate Routes dropdown
    const routes = await api.get("/api/routes/all");
    const routeSelect = document.getElementById("trip-route-select");
    routeSelect.innerHTML = routes
      .map((r) => `<option value="${r.id}">${r.routeNumber}: ${r.routeName} (${r.distanceKm} km)</option>`)
      .join("");

    // Set default departure to now + 30 mins, arrival to + 3.5 hrs
    const now = new Date();
    now.setMinutes(now.getMinutes() + 30);
    const depStr = now.toISOString().slice(0, 16);
    now.setHours(now.getHours() + 3);
    const arrStr = now.toISOString().slice(0, 16);

    document.getElementById("trip-departure").value = depStr;
    document.getElementById("trip-expected-arrival").value = arrStr;

    const modal = new bootstrap.Modal(document.getElementById("scheduleTripModal"));
    modal.show();
  } catch (err) {
    showToast("Error loading trip options", "danger");
  }
}

async function saveScheduleTripForm(e) {
  e.preventDefault();
  const payload = {
    busId: parseInt(document.getElementById("trip-bus-select").value, 10),
    routeId: parseInt(document.getElementById("trip-route-select").value, 10),
    departureTime: document.getElementById("trip-departure").value.replace("T", " ") + ":00",
    expectedArrivalTime: document.getElementById("trip-expected-arrival").value.replace("T", " ") + ":00",
    passengerCount: parseInt(document.getElementById("trip-passengers").value || 0, 10),
    ticketRevenue: parseFloat(document.getElementById("trip-revenue").value || 0),
    additionalExpenses: parseFloat(document.getElementById("trip-expenses").value || 0),
    overloadException: document.getElementById("trip-overload").checked,
    overloadReason: document.getElementById("trip-overload-reason").value.trim(),
    notes: document.getElementById("trip-notes").value.trim(),
  };

  try {
    await api.post("/api/trips", payload);
    showToast("Trip scheduled successfully!");
    bootstrap.Modal.getInstance(document.getElementById("scheduleTripModal")).hide();
    loadTrips();
    updateGlobalAlertBadge();
  } catch (err) {
    // Handled in api client
  }
}

async function openUpdateTripModal(id) {
  try {
    const trip = await api.get(`/api/trips/${id}`);
    document.getElementById("update-trip-id").value = trip.id;
    document.getElementById("update-trip-title").textContent = `Trip #${trip.id} (${trip.routeName})`;
    document.getElementById("update-trip-status").value = trip.status;
    document.getElementById("update-trip-passengers").value = trip.passengerCount;
    document.getElementById("update-trip-revenue").value = trip.ticketRevenue;
    document.getElementById("update-trip-expenses").value = trip.additionalExpenses;
    document.getElementById("update-trip-overload").checked = !!trip.overloadException;
    document.getElementById("update-trip-overload-reason").value = trip.overloadReason || "";
    document.getElementById("update-trip-notes").value = trip.notes || "";

    const modal = new bootstrap.Modal(document.getElementById("updateTripModal"));
    modal.show();
  } catch (err) {
    showToast("Failed to load trip", "danger");
  }
}

async function saveUpdateTripForm(e) {
  e.preventDefault();
  const id = document.getElementById("update-trip-id").value;
  const payload = {
    status: document.getElementById("update-trip-status").value,
    passengerCount: parseInt(document.getElementById("update-trip-passengers").value || 0, 10),
    ticketRevenue: parseFloat(document.getElementById("update-trip-revenue").value || 0),
    additionalExpenses: parseFloat(document.getElementById("update-trip-expenses").value || 0),
    overloadException: document.getElementById("update-trip-overload").checked,
    overloadReason: document.getElementById("update-trip-overload-reason").value.trim(),
    notes: document.getElementById("update-trip-notes").value.trim(),
  };

  try {
    await api.put(`/api/trips/${id}/status`, payload);
    showToast("Trip status updated successfully!");
    bootstrap.Modal.getInstance(document.getElementById("updateTripModal")).hide();
    loadTrips();
    updateGlobalAlertBadge();
  } catch (err) {
    // Handled
  }
}

async function deleteTripItem(id) {
  if (!confirm(`Are you sure you want to delete Trip #${id}?`)) return;
  try {
    await api.delete(`/api/trips/${id}`);
    showToast(`Trip #${id} deleted.`);
    loadTrips();
  } catch (err) {
    // Handled
  }
}

/**
 * -----------------------------------------------------------------------------
 * MODULE 5: PASSENGER DEMAND MANAGEMENT
 * -----------------------------------------------------------------------------
 */
async function loadPassengers() {
  try {
    const data = await api.get("/api/analytics/passengers");

    document.getElementById("pax-total").textContent = formatNumber(data.totalPassengersCarried);
    document.getElementById("pax-avg-occ").textContent = `${data.averageOccupancyRate || 0}%`;
    document.getElementById("pax-high-count").textContent = data.fullyBookedOrOverloadedTrips || 0;
    document.getElementById("pax-low-count").textContent = data.underutilizedTrips || 0;

    // Render Recommendations
    const recContainer = document.getElementById("pax-recommendations-container");
    if (recContainer) {
      recContainer.innerHTML = (data.operationalRecommendations || [])
        .map(
          (rec) => `
        <div class="recommendation-card mb-2">
          <i class="bi bi-lightbulb-fill text-warning me-2 fs-5"></i>
          <span>${escapeHtml(rec)}</span>
        </div>
      `
        )
        .join("");
    }

    // Render Route Breakdown Table
    const tbody = document.getElementById("pax-routes-tbody");
    if (tbody) {
      tbody.innerHTML = (data.routeDemandList || [])
        .map(
          (r) => `
        <tr>
          <td><span class="badge bg-primary-subtle text-primary">${escapeHtml(r.routeNumber)}</span></td>
          <td><strong>${escapeHtml(r.routeName)}</strong></td>
          <td>${formatNumber(r.totalPassengers)}</td>
          <td>
            <div class="d-flex align-items-center gap-2">
              <span class="fw-bold">${r.averageOccupancyRate}%</span>
              <div class="progress flex-grow-1" style="height: 6px;">
                <div class="progress-bar ${r.averageOccupancyRate >= 85 ? 'bg-danger' : r.averageOccupancyRate < 40 ? 'bg-warning' : 'bg-success'}" style="width: ${Math.min(r.averageOccupancyRate, 100)}%"></div>
              </div>
            </div>
          </td>
          <td>
            <span class="badge ${r.demandCategory === 'HIGH' ? 'bg-danger' : r.demandCategory === 'LOW' ? 'bg-warning text-dark' : 'bg-success'}">
              ${r.demandCategory}
            </span>
          </td>
        </tr>
      `
        )
        .join("");
    }
  } catch (err) {
    console.error("Failed to load passenger analytics:", err);
  }
}

/**
 * -----------------------------------------------------------------------------
 * MODULE 6: FUEL & FLEET EFFICIENCY
 * -----------------------------------------------------------------------------
 */
async function loadFuel() {
  try {
    const analytics = await api.get("/api/analytics/fuel");

    document.getElementById("fuel-total-liters").textContent = `${formatNumber(analytics.totalFuelConsumedLiters)} L`;
    document.getElementById("fuel-total-cost").textContent = formatCurrency(analytics.totalFuelCost);
    document.getElementById("fuel-avg-efficiency").textContent = `${analytics.fleetAverageEfficiency || 0} km/L`;
    document.getElementById("fuel-inefficient-count").textContent = analytics.inefficientBusesCount || 0;

    // Bus Efficiency Comparison Table
    const compTbody = document.getElementById("fuel-comparison-tbody");
    if (compTbody) {
      compTbody.innerHTML = (analytics.busEfficiencyList || [])
        .map(
          (b) => `
        <tr>
          <td><span class="badge bg-light text-dark border">${escapeHtml(b.registrationNumber)}</span></td>
          <td>${escapeHtml(b.busModel)}</td>
          <td><span class="badge bg-secondary-subtle text-secondary">${b.fuelType}</span></td>
          <td>${formatNumber(b.totalDistanceKm)} km</td>
          <td>${formatNumber(b.totalLitersConsumed)} L</td>
          <td>${formatCurrency(b.totalCost)}</td>
          <td>
            <strong class="${b.flaggedInefficient ? 'text-danger' : 'text-success'}">${b.averageEfficiencyKmPerLiter} km/L</strong>
            ${b.flaggedInefficient ? '<i class="bi bi-exclamation-triangle-fill text-danger ms-1" title="Below 3.8 km/L baseline"></i>' : ''}
          </td>
        </tr>
      `
        )
        .join("");
    }

    // Load Fuel Refueling Logs Table
    loadFuelLogsTable();
  } catch (err) {
    console.error("Failed to load fuel analytics:", err);
  }
}

async function loadFuelLogsTable() {
  const tbody = document.getElementById("fuel-logs-tbody");
  if (!tbody) return;

  try {
    const pageData = await api.get(`/api/fuel?page=${state.fuel.page}&size=${state.fuel.size}`);
    const records = pageData.content || [];

    if (records.length === 0) {
      tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted py-4">No refueling logs yet.</td></tr>`;
      return;
    }

    tbody.innerHTML = records
      .map(
        (f) => `
        <tr>
          <td>${formatDate(f.recordDate)}</td>
          <td><span class="badge bg-light text-dark border">${escapeHtml(f.busRegistrationNumber)}</span></td>
          <td>${f.distanceTravelledKm} km</td>
          <td>${f.fuelConsumedLiters} L</td>
          <td>${formatCurrency(f.fuelPricePerLiter)}</td>
          <td><strong>${formatCurrency(f.totalFuelCost)}</strong></td>
          <td><span class="badge ${f.fuelEfficiencyKmPerLiter < 3.8 ? 'bg-danger-subtle text-danger' : 'bg-success-subtle text-success'}">${f.fuelEfficiencyKmPerLiter} km/L</span></td>
        </tr>
      `
      )
      .join("");
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="7" class="text-danger text-center py-4">Error loading fuel logs</td></tr>`;
  }
}

async function openRecordFuelModal() {
  document.getElementById("fuel-modal-form").reset();
  try {
    const buses = await api.get("/api/buses/all");
    const busSelect = document.getElementById("fuel-bus-select");
    busSelect.innerHTML = buses.map((b) => `<option value="${b.id}">${b.registrationNumber} (${b.busModel})</option>`).join("");

    const trips = await api.get("/api/trips/all?status=COMPLETED");
    const tripSelect = document.getElementById("fuel-trip-select");
    tripSelect.innerHTML = `<option value="">-- No Specific Trip --</option>` +
      trips.map((t) => `<option value="${t.id}">Trip #${t.id} - ${t.routeName} (${t.busRegistrationNumber})</option>`).join("");

    document.getElementById("fuel-date").value = new Date().toISOString().slice(0, 10);
    const modal = new bootstrap.Modal(document.getElementById("recordFuelModal"));
    modal.show();
  } catch (err) {
    showToast("Error loading modal options", "danger");
  }
}

async function saveFuelForm(e) {
  e.preventDefault();
  const tripVal = document.getElementById("fuel-trip-select").value;
  const payload = {
    busId: parseInt(document.getElementById("fuel-bus-select").value, 10),
    tripId: tripVal ? parseInt(tripVal, 10) : null,
    recordDate: document.getElementById("fuel-date").value,
    distanceTravelledKm: parseFloat(document.getElementById("fuel-distance").value),
    fuelConsumedLiters: parseFloat(document.getElementById("fuel-liters").value),
    fuelPricePerLiter: parseFloat(document.getElementById("fuel-price").value),
    notes: document.getElementById("fuel-notes").value.trim(),
  };

  try {
    await api.post("/api/fuel", payload);
    showToast("Fuel record logged successfully!");
    bootstrap.Modal.getInstance(document.getElementById("recordFuelModal")).hide();
    loadFuel();
    updateGlobalAlertBadge();
  } catch (err) {
    // Handled
  }
}

/**
 * -----------------------------------------------------------------------------
 * MODULE 7: EXPENSES & PROFITABILITY
 * -----------------------------------------------------------------------------
 */
async function loadProfitability() {
  try {
    const data = await api.get("/api/analytics/profitability");

    document.getElementById("prof-revenue").textContent = formatCurrency(data.totalRevenue);
    document.getElementById("prof-fuel-cost").textContent = formatCurrency(data.totalFuelCost);
    document.getElementById("prof-other-cost").textContent = formatCurrency(data.totalOtherExpenses);
    document.getElementById("prof-total-cost").textContent = formatCurrency(data.totalOperatingCost);
    document.getElementById("prof-net-profit").textContent = formatCurrency(data.netOperatingProfit);
    document.getElementById("prof-margin").textContent = `${data.profitMarginPercentage || 0}%`;

    // Render Route Profitability Rollup
    const tbody = document.getElementById("prof-routes-tbody");
    if (tbody) {
      tbody.innerHTML = (data.routeProfitabilityList || [])
        .map(
          (r) => `
        <tr>
          <td><span class="badge bg-primary-subtle text-primary">${escapeHtml(r.routeNumber)}</span></td>
          <td><strong>${escapeHtml(r.routeName)}</strong></td>
          <td>${r.totalTrips}</td>
          <td>${formatCurrency(r.totalRevenue)}</td>
          <td>${formatCurrency(r.totalOperatingCost)}</td>
          <td><strong class="${r.netProfit >= 0 ? 'text-success' : 'text-danger'}">${formatCurrency(r.netProfit)}</strong></td>
          <td><span class="badge ${r.profitMarginPercentage >= 20 ? 'bg-success' : r.profitMarginPercentage < 0 ? 'bg-danger' : 'bg-warning text-dark'}">${r.profitMarginPercentage}%</span></td>
        </tr>
      `
        )
        .join("");
    }

    // Top Trips
    const topTbody = document.getElementById("prof-top-trips-tbody");
    if (topTbody) {
      topTbody.innerHTML = (data.topProfitableTrips || [])
        .map(
          (t) => `
        <tr>
          <td>#${t.tripId}</td>
          <td>${escapeHtml(t.routeName)}</td>
          <td>${formatCurrency(t.revenue)}</td>
          <td>${formatCurrency(t.cost)}</td>
          <td><strong class="text-success">${formatCurrency(t.profit)}</strong></td>
        </tr>
      `
        )
        .join("");
    }
  } catch (err) {
    console.error("Failed to load profitability metrics:", err);
  }
}

async function openRecordExpenseModal() {
  document.getElementById("expense-modal-form").reset();
  try {
    const buses = await api.get("/api/buses/all");
    const busSelect = document.getElementById("exp-bus-select");
    busSelect.innerHTML = `<option value="">-- No Specific Bus --</option>` +
      buses.map((b) => `<option value="${b.id}">${b.registrationNumber} (${b.busModel})</option>`).join("");

    const trips = await api.get("/api/trips/all");
    const tripSelect = document.getElementById("exp-trip-select");
    tripSelect.innerHTML = `<option value="">-- No Specific Trip --</option>` +
      trips.map((t) => `<option value="${t.id}">Trip #${t.id} - ${t.routeName}</option>`).join("");

    document.getElementById("exp-date").value = new Date().toISOString().slice(0, 10);
    const modal = new bootstrap.Modal(document.getElementById("recordExpenseModal"));
    modal.show();
  } catch (err) {
    showToast("Error loading expense options", "danger");
  }
}

async function saveExpenseForm(e) {
  e.preventDefault();
  const busVal = document.getElementById("exp-bus-select").value;
  const tripVal = document.getElementById("exp-trip-select").value;

  const payload = {
    busId: busVal ? parseInt(busVal, 10) : null,
    tripId: tripVal ? parseInt(tripVal, 10) : null,
    expenseDate: document.getElementById("exp-date").value,
    category: document.getElementById("exp-category").value,
    amount: parseFloat(document.getElementById("exp-amount").value),
    description: document.getElementById("exp-desc").value.trim(),
  };

  try {
    await api.post("/api/expenses", payload);
    showToast("Operating expense recorded successfully!");
    bootstrap.Modal.getInstance(document.getElementById("recordExpenseModal")).hide();
    loadProfitability();
  } catch (err) {
    // Handled
  }
}

/**
 * -----------------------------------------------------------------------------
 * MODULE 8: SUSTAINABILITY & EMISSIONS
 * -----------------------------------------------------------------------------
 */
async function loadSustainability() {
  try {
    const data = await api.get("/api/analytics/sustainability");

    document.getElementById("sust-total-kg").textContent = `${formatNumber(data.estimatedTotalCo2EmissionsKg)} kg`;
    document.getElementById("sust-total-tonnes").textContent = `${data.estimatedTotalCo2EmissionsTonnes || 0} t`;
    document.getElementById("sust-ev-saved").textContent = `${formatNumber(data.estimatedCo2AvoidedKgByEv)} kg`;
    document.getElementById("sust-pax-km").textContent = `${data.averageCo2GramsPerPassengerKm || 0} g/pax-km`;
    document.getElementById("sust-methodology-note").textContent = data.methodologyNote || "";

    // Recommendations
    const recContainer = document.getElementById("sust-recommendations-container");
    if (recContainer) {
      recContainer.innerHTML = (data.sustainabilityRecommendations || [])
        .map(
          (r) => `
        <div class="recommendation-card mb-2">
          <i class="bi bi-tree-fill text-success me-2 fs-5"></i>
          <span>${escapeHtml(r)}</span>
        </div>
      `
        )
        .join("");
    }

    // Bus emissions table
    const tbody = document.getElementById("sust-buses-tbody");
    if (tbody) {
      tbody.innerHTML = (data.busEmissionsList || [])
        .map(
          (b) => `
        <tr>
          <td><span class="badge bg-light text-dark border">${escapeHtml(b.registrationNumber)}</span></td>
          <td>${escapeHtml(b.busModel)}</td>
          <td><span class="badge bg-secondary-subtle text-secondary">${b.fuelType}</span></td>
          <td>${formatNumber(b.fuelConsumedLiters)} L</td>
          <td><strong>${formatNumber(b.estimatedCo2Kg)} kg CO₂</strong></td>
        </tr>
      `
        )
        .join("");
    }
  } catch (err) {
    console.error("Failed to load sustainability metrics:", err);
  }
}

/**
 * -----------------------------------------------------------------------------
 * MODULE 9: OPERATIONAL ALERTS
 * -----------------------------------------------------------------------------
 */
async function loadAlerts() {
  const tbody = document.getElementById("alerts-tbody");
  if (!tbody) return;

  try {
    const alerts = await api.get("/api/alerts/all");

    if (alerts.length === 0) {
      tbody.innerHTML = `<tr><td colspan="6" class="empty-state">
        <i class="bi bi-shield-check text-success"></i>
        <h5>No Alerts Recorded</h5>
        <p>The rules engine has detected no maintenance, fuel or capacity anomalies.</p>
        <button class="btn btn-outline-primary" onclick="triggerAlertScan()">Run Rules Scan</button>
      </td></tr>`;
      return;
    }

    tbody.innerHTML = alerts
      .map(
        (a) => `
        <tr class="${a.acknowledged ? 'opacity-75' : 'table-light fw-medium'}">
          <td>
            <span class="badge ${a.severity === 'CRITICAL' ? 'bg-danger' : a.severity === 'WARNING' ? 'bg-warning text-dark' : 'bg-primary'}">
              ${a.severity}
            </span>
          </td>
          <td><strong>${escapeHtml(a.title)}</strong></td>
          <td>${escapeHtml(a.message)}</td>
          <td>${formatDateTime(a.createdAt)}</td>
          <td>
            <span class="badge ${a.acknowledged ? 'bg-success' : 'bg-secondary'}">
              ${a.acknowledged ? 'Acknowledged' : 'Active'}
            </span>
          </td>
          <td>
            ${
              !a.acknowledged
                ? `<button class="btn btn-sm btn-outline-primary" onclick="acknowledgeAlertItem(${a.id})">Acknowledge</button>`
                : `<span class="text-muted small"><i class="bi bi-check2"></i> Cleared</span>`
            }
          </td>
        </tr>
      `
      )
      .join("");
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="6" class="text-danger text-center py-4">Error loading alerts: ${escapeHtml(err.message)}</td></tr>`;
  }
}

async function acknowledgeAlertItem(id) {
  try {
    await api.put(`/api/alerts/${id}/acknowledge`);
    showToast("Alert acknowledged.");
    if (state.currentTab === "alerts") loadAlerts();
    if (state.currentTab === "dashboard") loadDashboard();
    updateGlobalAlertBadge();
  } catch (err) {
    // Handled
  }
}

async function triggerAlertScan() {
  try {
    await api.post("/api/alerts/scan");
    showToast("Fleet performance scan finished. Alerts refreshed.");
    loadAlerts();
    updateGlobalAlertBadge();
  } catch (err) {
    // Handled
  }
}

async function updateGlobalAlertBadge() {
  try {
    const alerts = await api.get("/api/alerts");
    const count = alerts.length;
    const badge = document.getElementById("global-alert-badge");
    if (badge) {
      badge.textContent = count;
      badge.style.display = count > 0 ? "inline-block" : "none";
    }
  } catch (err) {
    // Silent
  }
}

/**
 * -----------------------------------------------------------------------------
 * UI Helpers & Global Form Event Binding
 * -----------------------------------------------------------------------------
 */
function getStatusBadge(status) {
  const map = {
    SCHEDULED: '<span class="badge badge-status badge-status-scheduled"><i class="bi bi-clock"></i> Scheduled</span>',
    IN_PROGRESS: '<span class="badge badge-status badge-status-inprogress"><i class="bi bi-play-circle"></i> In Progress</span>',
    COMPLETED: '<span class="badge badge-status badge-status-completed"><i class="bi bi-check-circle"></i> Completed</span>',
    CANCELLED: '<span class="badge badge-status badge-status-cancelled"><i class="bi bi-x-circle"></i> Cancelled</span>',
  };
  return map[status] || `<span class="badge bg-secondary">${status}</span>`;
}

function getBusStatusBadge(status) {
  const map = {
    AVAILABLE: '<span class="badge badge-status badge-status-available"><i class="bi bi-check2"></i> Available</span>',
    ON_TRIP: '<span class="badge badge-status badge-status-ontrip"><i class="bi bi-broadcast"></i> On Trip</span>',
    MAINTENANCE: '<span class="badge badge-status badge-status-maintenance"><i class="bi bi-wrench"></i> Maintenance</span>',
  };
  return map[status] || `<span class="badge bg-secondary">${status}</span>`;
}

function renderPagination(elemId, pageData, onPageSelect) {
  const container = document.getElementById(elemId);
  if (!container) return;

  const totalPages = pageData.totalPages || 0;
  const currentPage = pageData.number || 0;

  if (totalPages <= 1) {
    container.innerHTML = "";
    return;
  }

  let html = `<ul class="pagination pagination-sm mb-0">`;
  html += `<li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
    <a class="page-link" href="#" onclick="event.preventDefault(); (${onPageSelect.toString()})(${currentPage - 1})">Prev</a>
  </li>`;

  for (let i = 0; i < totalPages; i++) {
    html += `<li class="page-item ${i === currentPage ? 'active' : ''}">
      <a class="page-link" href="#" onclick="event.preventDefault(); (${onPageSelect.toString()})(${i})">${i + 1}</a>
    </li>`;
  }

  html += `<li class="page-item ${currentPage >= totalPages - 1 ? 'disabled' : ''}">
    <a class="page-link" href="#" onclick="event.preventDefault(); (${onPageSelect.toString()})(${currentPage + 1})">Next</a>
  </li></ul>`;

  container.innerHTML = html;
}

function setupGlobalEvents() {
  // Bind form submits
  document.getElementById("bus-modal-form")?.addEventListener("submit", saveBusForm);
  document.getElementById("route-modal-form")?.addEventListener("submit", saveRouteForm);
  document.getElementById("trip-modal-form")?.addEventListener("submit", saveScheduleTripForm);
  document.getElementById("update-trip-form")?.addEventListener("submit", saveUpdateTripForm);
  document.getElementById("fuel-modal-form")?.addEventListener("submit", saveFuelForm);
  document.getElementById("expense-modal-form")?.addEventListener("submit", saveExpenseForm);

  // Search & filter bindings
  document.getElementById("bus-search")?.addEventListener("input", debounce(() => { state.buses.page = 0; loadBuses(); }, 300));
  document.getElementById("bus-status-filter")?.addEventListener("change", () => { state.buses.page = 0; loadBuses(); });

  document.getElementById("route-search")?.addEventListener("input", debounce(() => { state.routes.page = 0; loadRoutes(); }, 300));

  document.getElementById("trip-status-filter")?.addEventListener("change", () => { state.trips.page = 0; loadTrips(); });
  document.getElementById("trip-bus-filter")?.addEventListener("change", () => { state.trips.page = 0; loadTrips(); });
  document.getElementById("trip-route-filter")?.addEventListener("change", () => { state.trips.page = 0; loadTrips(); });
}

function debounce(func, wait) {
  let timeout;
  return function (...args) {
    clearTimeout(timeout);
    timeout = setTimeout(() => func.apply(this, args), wait);
  };
}
