// Global Variables
let currentTheme = localStorage.getItem('adminTheme') || 'light';
let salesChartInstance = null;

document.addEventListener('DOMContentLoaded', function () {
    applyTheme(currentTheme);

    const darkModeSwitch = document.getElementById('darkModeSwitch');
    if (darkModeSwitch) {
        darkModeSwitch.checked = (currentTheme === 'dark');
        updateDarkModeIcon(currentTheme);

        darkModeSwitch.addEventListener('change', function () {
            const newTheme = this.checked ? 'dark' : 'light';
            applyTheme(newTheme);
        });
    } else {
        console.warn("Dark mode switch element '#darkModeSwitch' not found.");
    }

    addModalFormListener('addUserForm', '#addUserModal', 'User added successfully!');
    addModalFormListener('addProductForm', '#addProductModal', 'Product added successfully!');
    addModalFormListener('addPromotionForm', '#addPromotionModal', 'Promotion added successfully!');
    addModalFormListener('addStaffForm', '#addStaffModal', 'Staff member added successfully!');

    if (document.getElementById('salesChart') && document.getElementById('total-users')) {
        fetchSalesChartData();
        fetchSummaryData();
    }
});

/**
 * Applies the selected theme (light/dark) to the HTML element and saves preference.
 * @param {string} theme - The theme to apply ('light' or 'dark').
 */
function applyTheme(theme) {
    document.documentElement.setAttribute('data-bs-theme', theme);
    localStorage.setItem('adminTheme', theme);
    currentTheme = theme;

    const darkModeSwitch = document.getElementById('darkModeSwitch');
    if (darkModeSwitch) {
        darkModeSwitch.checked = (theme === 'dark');
    }
    updateDarkModeIcon(theme);

    if (salesChartInstance) {
        updateChartTheme(salesChartInstance, theme);
    }
}

/**
 * Updates the dark mode toggle icon based on the current theme.
 * @param {string} theme - The current theme ('light' or 'dark').
 */
function updateDarkModeIcon(theme) {
    const iconElement = document.getElementById('darkModeIcon');
    if (iconElement) {
        if (theme === 'dark') {
            iconElement.classList.remove('fa-moon');
            iconElement.classList.add('fa-sun');
        } else {
            iconElement.classList.remove('fa-sun');
            iconElement.classList.add('fa-moon');
        }
    }
}

/**
 * Fetches sales data (currently uses placeholder) and renders the chart.
 */
function fetchSalesChartData() {
    const ctx = document.getElementById('salesChart');
    if (!ctx) return;

    console.warn("Using placeholder data for sales chart.");
    setTimeout(() => {
        const placeholderLabels = ['Apr 14', 'Apr 15', 'Apr 16', 'Apr 17', 'Apr 18', 'Apr 19', 'Apr 20'];
        const placeholderData = [120.50, 190.75, 150.00, 280.20, 210.90, 300.00, 255.50];
        renderSalesChart(ctx.getContext('2d'), placeholderLabels, placeholderData);
    }, 300);
}

/**
 * Renders the sales chart using Chart.js with theme awareness.
 * @param {CanvasRenderingContext2D} context - The canvas rendering context.
 * @param {string[]} labels - Array of labels for the X-axis.
 * @param {number[]} dataPoints - Array of data points for the Y-axis.
 */
function renderSalesChart(context, labels, dataPoints) {
    if (!context) return;
    if (salesChartInstance) { salesChartInstance.destroy(); }

    const isDark = currentTheme === 'dark';
    const gridColor = isDark ? 'rgba(255, 255, 255, 0.15)' : 'rgba(0, 0, 0, 0.1)';
    const tickColor = isDark ? 'rgba(255, 255, 255, 0.7)' : 'rgba(0, 0, 0, 0.7)';
    const labelColor = isDark ? 'rgba(255, 255, 255, 0.9)' : 'rgba(0, 0, 0, 0.9)';
    const pointBackgroundColor = isDark ? '#6c7eff' : '#4C60DF';
    const datasetFillColor = isDark ? 'rgba(108, 126, 255, 0.2)' : 'rgba(76, 96, 223, 0.1)';

    salesChartInstance = new Chart(context, {
        type: 'line',
        data: { labels: labels, datasets: [{ label: 'Total Sales (RM)', data: dataPoints, borderColor: pointBackgroundColor, backgroundColor: datasetFillColor, borderWidth: 2, fill: true, tension: 0.1, pointBackgroundColor: pointBackgroundColor, pointBorderColor: pointBackgroundColor }] },
        options: {
            responsive: true, maintainAspectRatio: false,
            scales: {
                y: { beginAtZero: true, grid: { color: gridColor }, ticks: { color: tickColor, callback: value => 'RM ' + value.toFixed(2) } },
                x: { grid: { color: gridColor }, ticks: { color: tickColor } }
            },
            plugins: {
                legend: { display: true, position: 'top', labels: { color: labelColor } },
                tooltip: {
                    bodyColor: isDark ? '#eee' : '#333', titleColor: isDark ? '#fff' : '#000', backgroundColor: isDark ? 'rgba(40, 40, 40, 0.9)' : 'rgba(255, 255, 255, 0.9)', borderColor: isDark ? 'rgba(100, 100, 100, 0.9)' : 'rgba(0, 0, 0, 0.1)', borderWidth: 1,
                    callbacks: { label: context => `${context.dataset.label || ''}: RM ${context.parsed.y.toFixed(2)}` }
                }
            }
        }
    });
}

/**
 * Updates the colors of an existing Chart.js instance based on the theme.
 * @param {Chart} chart - The Chart.js instance.
 * @param {string} theme - The current theme ('light' or 'dark').
 */
function updateChartTheme(chart, theme) {
    if (!chart) return;
    const isDark = theme === 'dark';
    const gridColor = isDark ? 'rgba(255, 255, 255, 0.15)' : 'rgba(0, 0, 0, 0.1)';
    const tickColor = isDark ? 'rgba(255, 255, 255, 0.7)' : 'rgba(0, 0, 0, 0.7)';
    const labelColor = isDark ? 'rgba(255, 255, 255, 0.9)' : 'rgba(0, 0, 0, 0.9)';
    const pointBackgroundColor = isDark ? '#6c7eff' : '#4C60DF';
    const datasetFillColor = isDark ? 'rgba(108, 126, 255, 0.2)' : 'rgba(76, 96, 223, 0.1)';
    const tooltipBodyColor = isDark ? '#eee' : '#333';
    const tooltipTitleColor = isDark ? '#fff' : '#000';
    const tooltipBgColor = isDark ? 'rgba(40, 40, 40, 0.9)' : 'rgba(255, 255, 255, 0.9)';
    const tooltipBorderColor = isDark ? 'rgba(100, 100, 100, 0.9)' : 'rgba(0, 0, 0, 0.1)';

    chart.options.scales.y.grid.color = gridColor;
    chart.options.scales.y.ticks.color = tickColor;
    chart.options.scales.x.grid.color = gridColor;
    chart.options.scales.x.ticks.color = tickColor;
    chart.options.plugins.legend.labels.color = labelColor;
    chart.options.plugins.tooltip.bodyColor = tooltipBodyColor;
    chart.options.plugins.tooltip.titleColor = tooltipTitleColor;
    chart.options.plugins.tooltip.backgroundColor = tooltipBgColor;
    chart.options.plugins.tooltip.borderColor = tooltipBorderColor;

    // Update dataset colors
    chart.data.datasets.forEach(dataset => {
        dataset.borderColor = pointBackgroundColor;
        dataset.backgroundColor = datasetFillColor;
        dataset.pointBackgroundColor = pointBackgroundColor;
        dataset.pointBorderColor = pointBackgroundColor;
    });
    chart.update();
}

/**
 * Fetches summary data (currently uses placeholder) and updates the cards.
 */
function fetchSummaryData() {
    if (!document.getElementById('total-users')) return;

    console.warn("Using placeholder data for summary cards.");
    setTimeout(() => {
        updateSummaryCard('total-users', 150);
        updateSummaryCard('total-products', 85);
        updateSummaryCard('total-orders', 520);
        updateSummaryCard('total-sales', 12345.67, 'RM');
    }, 300);
}

/**
 * Updates the text content of a summary card element.
 * @param {string} elementId - The ID of the <p> element holding the value.
 * @param {number|string} value - The value to display.
 * @param {string} [prefix=''] - Optional prefix (e.g., currency symbol).
 */
function updateSummaryCard(elementId, value, prefix = '') {
    const element = document.getElementById(elementId);
    if (element) {
        let displayValue = 'N/A';
        if (value !== null && value !== undefined && value !== 'Error') {
            if (typeof value === 'number' && prefix === 'RM') {
                displayValue = prefix + ' ' + value.toFixed(2);
            } else {
                displayValue = prefix + (prefix ? ' ' : '') + value.toString();
            }
        } else if (value === 'Error') {
            displayValue = 'Error';
        }
        element.textContent = displayValue;
    }
}

/**
 * Displays a Bootstrap Toast message.
 * @param {string} message - The message to display in the toast.
 * @param {string} [type='success'] - The type of toast ('success', 'danger', 'warning', 'info').
 */
function showToast(message, type = 'success') {
    const toastContainer = document.querySelector('.toast-container');
    if (!toastContainer) { console.error('Toast container not found.'); return; }

    let iconClass = 'fa-check-circle';
    let textBgClass = 'text-bg-success';
    switch (type) {
        case 'danger': iconClass = 'fa-exclamation-triangle'; textBgClass = 'text-bg-danger'; break;
        case 'warning': iconClass = 'fa-exclamation-circle'; textBgClass = 'text-bg-warning'; break;
        case 'info': iconClass = 'fa-info-circle'; textBgClass = 'text-bg-info'; break;
    }
    const toastId = `toast-${Date.now()}`;
    const toastHTML = `
    <div id="${toastId}" class="toast align-items-center ${textBgClass} border-0" role="alert" aria-live="assertive" aria-atomic="true">
      <div class="d-flex">
        <div class="toast-body"><i class="fas ${iconClass} me-2"></i>${message}</div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
      </div>
    </div>`;
    toastContainer.insertAdjacentHTML('beforeend', toastHTML);
    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement, { delay: 5000 });
    toastElement.addEventListener('hidden.bs.toast', () => { toastElement.remove(); });
    toast.show();
}

/**
 * Adds a submit event listener to a modal form.
 * @param {string} formId - The ID of the form element.
 * @param {string} modalSelector - The CSS selector for the modal element.
 * @param {string} successMessage - The message to show in the success toast.
 */
function addModalFormListener(formId, modalSelector, successMessage) {
    const form = document.getElementById(formId);
    const modalElement = document.querySelector(modalSelector);
    if (form && modalElement) {
        form.addEventListener('submit', function (event) {
            event.preventDefault();
            const modalInstance = bootstrap.Modal.getInstance(modalElement);
            if (modalInstance) { modalInstance.hide(); }
            showToast(successMessage, 'success');
            form.reset();
        });
    } else {
        if (!form) console.warn(`Form with ID "${formId}" not found.`);
        if (!modalElement) console.warn(`Modal with selector "${modalSelector}" not found.`);
    }
}
