const API_BASE_URL = "";
const COURSE_TICKERS = ["USD/RUB", "EUR/USD", "EUR/RUB"];
let currentCourseIndex = 0;

async function request(url, options = {}) {
    const response = await fetch(API_BASE_URL + url, {
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {})
        },
        credentials: "include",
        ...options
    });

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || `Ошибка запроса: ${response.status}`);
    }

    const contentType = response.headers.get("content-type");

    if (contentType && contentType.includes("application/json")) {
        return await response.json();
    }

    return await response.text();
}

// =======================
// Регистрация
// =======================

async function registerUser() {
    const name = document.getElementById("registerName")?.value.trim();
    const email = document.getElementById("registerEmail")?.value.trim();
    const password = document.getElementById("registerPassword")?.value.trim();

    if (!name || !email || !password) {
        alert("Заполните все поля регистрации");
        return;
    }

    try {
        const user = await request("/api/sign-up", {
            method: "POST",
            body: JSON.stringify({
                username: name,
                email: email,
                password: password
            })
        });

        localStorage.setItem("currentUser", JSON.stringify(user));

        alert("Регистрация выполнена успешно");
        window.location.href = "/index.html";
    } catch (error) {
        console.error(error);
        alert("Ошибка регистрации: " + error.message);
    }
}

// =======================
// Авторизация
// =======================

async function loginUser() {
    const email = document.getElementById("loginEmail")?.value.trim();
    const password = document.getElementById("loginPassword")?.value.trim();

    if (!email || !password) {
        alert("Введите email и пароль");
        return;
    }

    try {
        const user = await request("/api/sign-in", {
            method: "POST",
            body: JSON.stringify({
                email: email,
                password: password
            })
        });

        localStorage.setItem("currentUser", JSON.stringify(user));

        alert("Вход выполнен успешно");
        window.location.href = "/index.html";
    } catch (error) {
        console.error(error);
        alert("Ошибка входа: " + error.message);
    }
}

// =======================
// Выход
// =======================

function logoutUser() {
    localStorage.removeItem("currentUser");
    window.location.href = "/login.html";
}

// =======================
// Текущий пользователь
// =======================

async function loadCurrentUser() {
    try {
        const user = await request("/api/user/me", {
            method: "GET"
        });

        setText("userName", user.username || user.name || "Пользователь");
        setText("userEmail", user.email || "");

        setText("sidebarUserName", user.username || user.name || "Пользователь");

        if (user.roles) {
            if (Array.isArray(user.roles)) {
                setText("sidebarUserRole", user.roles[0]);
            } else {
                setText("sidebarUserRole", user.roles);
            }
        }

        localStorage.setItem("currentUser", JSON.stringify(user));

        return user;
    } catch (error) {
        console.error("Ошибка загрузки пользователя:", error);
        setText("userName", "Не авторизован");
        setText("userEmail", "");
        return null;
    }
}

// =======================
// Портфель
// =======================

async function loadPortfolio() {
    try {
        const portfolio = await request("/api/portfolio", {
            method: "GET"
        });

        localStorage.setItem("currentPortfolio", JSON.stringify(portfolio));

        setText("portfolioName", portfolio.name);
        setText("portfolioCurrency", portfolio.currency);
        setText("startBalance", formatMoney(portfolio.startBalance, portfolio.currency));
        setText("cashBalance", formatMoney(portfolio.cashBalance, portfolio.currency));
        setText("portfolioCreatedAt", formatDateTime(portfolio.createdAt));

        return portfolio;
    } catch (error) {
        console.error("Ошибка загрузки портфеля:", error);
        return null;
    }
}

// =======================
// Активы
// =======================

async function loadAssets() {
    const container = document.getElementById("assetsContainer");

    try {
        const assets = await request("/api/assets", {
            method: "GET"
        });

        localStorage.setItem("assets", JSON.stringify(assets));

        if (!container) {
            return assets;
        }

        container.innerHTML = "";

        for (const asset of assets) {
            let latestPrice = "Нет данных";
            let quoteTime = "";

            try {
                const quote = await request(`/api/quotes/latest/asset/${asset.id}`, {
                    method: "GET"
                });

                latestPrice = `${quote.price} ${asset.currency ?? ""}`;
                quoteTime = quote.ts ? `Обновлено: ${formatDateTime(quote.ts)}` : "";
            } catch (error) {
                console.warn(`Курс для актива ${asset.ticker} не найден`, error);
            }

            const card = document.createElement("div");
            card.className = "asset-card";

            card.innerHTML = `
                <div>
                    <h3>${asset.ticker}</h3>
                    <p>${asset.name}</p>
                    <span class="asset-update-time">${quoteTime}</span>
                </div>

                <div>
                    <strong>${latestPrice}</strong>
                    <button onclick="goToBuyAsset(${asset.id})">Купить</button>
                    <button class="outline-btn" onclick="goToCourseChart(${asset.id})">
                        График курса
                    </button>
                </div>
            `;

            container.appendChild(card);
        }

        return assets;
    } catch (error) {
        console.error("Ошибка загрузки активов:", error);

        if (container) {
            container.innerHTML = "<p>Не удалось загрузить список активов</p>";
        }

        return [];
    }
}

function goToBuyAsset(assetId) {
    localStorage.setItem("selectedAssetId", assetId);
    window.location.href = "/buy-asset.html";
}

function goToCourseChart(assetId) {
    localStorage.setItem("selectedAssetId", assetId);
    window.location.href = "/course-chart.html";
}

function goToCourseChartFromMain() {
    const quoteRaw = localStorage.getItem("mainChartQuote");

    if (quoteRaw) {
        const quote = JSON.parse(quoteRaw);
        localStorage.setItem("selectedAssetId", quote.assetId);
    }

    window.location.href = "/course-chart.html";
}

let currentMainChartIndex = 0;

async function rotateMainChartRate() {
    const ticker = COURSE_TICKERS[currentMainChartIndex];

    await loadMainChartRate(ticker);

    currentMainChartIndex++;

    if (currentMainChartIndex >= COURSE_TICKERS.length) {
        currentMainChartIndex = 0;
    }
}

// =======================
// Последняя котировка
// =======================

let previousRateValue = null;

async function loadLatestQuoteByTicker(ticker = "USD/RUB") {
    try {
        const quote = await request(`/api/quotes/latest?ticker=${encodeURIComponent(ticker)}`, {
            method: "GET"
        });

        const newRateValue = Number(quote.price);
        const currentRateElement = document.getElementById("currentRate");
        const rateCard = document.getElementById("rateCard");
        const rateDirection = document.getElementById("rateDirection");

        if (currentRateElement) {
            currentRateElement.classList.add("fade-out");

            setTimeout(() => {
                currentRateElement.textContent = `${quote.ticker} ${quote.price}`;
                currentRateElement.classList.remove("fade-out");
                currentRateElement.classList.add("fade-in");

                setTimeout(() => {
                    currentRateElement.classList.remove("fade-in");
                }, 300);
            }, 300);
        }

        if (rateCard && rateDirection) {
            rateCard.classList.remove("rate-up", "rate-down");
            rateDirection.classList.remove("up", "down");
            rateDirection.textContent = "";

            if (previousRateValue !== null) {
                if (newRateValue > previousRateValue) {
                    rateCard.classList.add("rate-up");
                    rateDirection.classList.add("up");
                    rateDirection.textContent = "↑";
                } else if (newRateValue < previousRateValue) {
                    rateCard.classList.add("rate-down");
                    rateDirection.classList.add("down");
                    rateDirection.textContent = "↓";
                }
            }

            setTimeout(() => {
                rateCard.classList.remove("rate-up", "rate-down");
            }, 900);
        }

        previousRateValue = newRateValue;

        setText("latestQuoteTime", `Обновлено: ${formatDateTime(quote.ts)}`);

        localStorage.setItem("latestQuote", JSON.stringify(quote));

        return quote;
    } catch (error) {
        console.error("Ошибка загрузки курса:", error);
        setText("currentRate", "Курс не найден");
        setText("latestQuoteTime", "Обновите курс через API");
        return null;
    }
}

async function loadLatestQuoteByAssetId(assetId) {
    try {
        const quote = await request(`/api/quotes/latest/asset/${assetId}`, {
            method: "GET"
        });

        localStorage.setItem("latestQuote", JSON.stringify(quote));

        setText("buyAssetTicker", quote.ticker);
        setText("buyAssetPrice", `${quote.price}`);
        setText("buyAssetRate", `${quote.price}`);
        setText("chartTicker", quote.ticker);
        setText("chartCurrentPrice", `${quote.price}`);

        return quote;
    } catch (error) {
        console.error("Ошибка загрузки котировки по assetId:", error);
        return null;
    }
}

async function rotateCurrentRate() {
    const ticker = COURSE_TICKERS[currentCourseIndex];

    await loadLatestQuoteByTicker(ticker);

    currentCourseIndex++;

    if (currentCourseIndex >= COURSE_TICKERS.length) {
        currentCourseIndex = 0;
    }
}

// =======================
// Страница покупки
// =======================

async function initBuyAssetPage() {
    const assetId = localStorage.getItem("selectedAssetId") || "1";

    const quote = await loadLatestQuoteByAssetId(assetId);
    const portfolio = await loadPortfolio();

    if (!quote) {
        alert("Не удалось загрузить курс актива");
        return;
    }

    const qtyInput = document.getElementById("buyQty");

    if (qtyInput) {
        qtyInput.addEventListener("input", () => recalculateBuyTotal(quote.price));
        recalculateBuyTotal(quote.price);
    }

    if (portfolio) {
        setText("buyPortfolioCash", formatMoney(portfolio.cashBalance, portfolio.currency));
        setText("buyPortfolioStart", formatMoney(portfolio.startBalance, portfolio.currency));
        setText("buyPortfolioName", portfolio.name);
        setText("buyPortfolioCurrency", portfolio.currency);
    }
}

function recalculateBuyTotal(price, ticker) {
    const qty = Number(document.getElementById("buyQty")?.value || 0);
    const numericPrice = Number(price || 0);
    const fee = 0;
    const total = qty * numericPrice + fee;

    setText("buyQtyView", qty.toFixed(2));
    setText("buyPriceView", numericPrice.toFixed(6));
    setText("buyTotalView", total.toFixed(2));

    setText("previewAssetTicker", ticker || "");
    setText("previewBuyQty", qty.toFixed(2));
    setText("previewBuyTotal", total.toFixed(2));

    const priceInput = document.getElementById("buyPriceInput");
    if (priceInput) {
        priceInput.value = numericPrice.toFixed(6);
    }

    const feeInput = document.getElementById("buyFeeInput");
    if (feeInput) {
        feeInput.value = fee.toFixed(2);
    }
}

async function buyAsset() {
    const assetId = Number(localStorage.getItem("selectedAssetId") || 1);
    const qty = Number(document.getElementById("buyQty")?.value || 0);

    if (!qty || qty <= 0) {
        alert("Введите количество больше нуля");
        return;
    }

    try {
        const result = await request("/api/trades/buy", {
            method: "POST",
            body: JSON.stringify({
                assetId: assetId,
                qty: qty
            })
        });

        localStorage.setItem("lastTradeResult", JSON.stringify(result));

        alert("Покупка выполнена успешно");
        window.location.href = "/deal-result.html";
    } catch (error) {
        console.error("Ошибка покупки:", error);
        alert("Ошибка покупки: " + error.message);
    }
}

// =======================
// Результат сделки
// =======================

async function loadDealResult() {
    const resultRaw = localStorage.getItem("lastTradeResult");

    if (!resultRaw) {
        alert("Данные о последней сделке не найдены");
        window.location.href = "/index.html#assets";
        return;
    }

    const result = JSON.parse(resultRaw);

    setText("dealType", result.type || "BUY");
    setText("dealTicker", result.ticker || "");
    setText("dealQty", formatNumber(result.qty));
    setText("dealPrice", formatNumber(result.price));
    setText("dealFee", formatNumber(result.fee));
    setText("dealTotal", formatNumber(result.total));
    setText("dealDate", formatDateTime(result.ts));
    setText("dealTransactionId", result.transactionId);

    await loadPortfolioForDealResult();
}

async function loadMainChartRate(ticker = "USD/RUB") {
    try {
        const quote = await request(`/api/quotes/latest?ticker=${encodeURIComponent(ticker)}`, {
            method: "GET"
        });

        setText("mainChartTicker", quote.ticker);
        setText("mainChartCurrentRate", `${quote.price}`);
        setText("mainChartUpdatedAt", `Обновлено: ${formatDateTime(quote.ts)}`);

        localStorage.setItem("selectedAssetId", quote.assetId);
        localStorage.setItem("mainChartQuote", JSON.stringify(quote));

        return quote;
    } catch (error) {
        console.error("Ошибка загрузки курса для графика:", error);

        setText("mainChartTicker", "Курс не найден");
        setText("mainChartCurrentRate", "-");
        setText("mainChartUpdatedAt", "Нет данных о последней котировке");

        return null;
    }
}



function formatNumber(value) {
    if (value === null || value === undefined || value === "") {
        return "0.00";
    }

    const number = Number(value);

    if (Number.isNaN(number)) {
        return value;
    }

    return number.toFixed(6);
}

async function loadPortfolioForDealResult() {
    try {
        const portfolio = await request("/api/portfolio", {
            method: "GET"
        });

        setText("resultPortfolioName", portfolio.name);
        setText("resultPortfolioCurrency", portfolio.currency);
        setText("resultStartBalance", formatMoney(portfolio.startBalance, portfolio.currency));
        setText("resultCashBalance", formatMoney(portfolio.cashBalance, portfolio.currency));

        localStorage.setItem("currentPortfolio", JSON.stringify(portfolio));

        return portfolio;
    } catch (error) {
        console.error("Ошибка загрузки портфеля после сделки:", error);

        setText("resultPortfolioName", "Ошибка загрузки");
        setText("resultPortfolioCurrency", "-");
        setText("resultStartBalance", "-");
        setText("resultCashBalance", "-");

        return null;
    }
}

// =======================
// Транзакции
// =======================

async function loadTransactions() {
    const tbody = document.getElementById("transactionsTableBody");

    if (!tbody) {
        return [];
    }

    try {
        const transactions = await request("/api/transactions", {
            method: "GET"
        });

        tbody.innerHTML = "";

        if (!transactions || transactions.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6">Операции пока отсутствуют</td>
                </tr>
            `;
            return [];
        }

        transactions.forEach(transaction => {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${formatDateTime(transaction.ts)}</td>
                <td>${transaction.ticker}</td>
                <td><span class="badge buy">${transaction.type}</span></td>
                <td>${formatNumber(transaction.qty)}</td>
                <td>${formatNumber(transaction.price)}</td>
                <td>${formatNumber(transaction.fee)}</td>
            `;

            tbody.appendChild(row);
        });

        return transactions;
    } catch (error) {
        console.error("Ошибка загрузки истории операций:", error);

        tbody.innerHTML = `
            <tr>
                <td colspan="6">Не удалось загрузить историю операций</td>
            </tr>
        `;

        return [];
    }
}

// =======================
// Обновление курса вручную
// =======================

async function updateUsdRubQuote() {
    try {
        await request("/api/admin/quotes/update-usd-rub", {
            method: "POST"
        });

        alert("Курс USD/RUB обновлён");
        await loadLatestQuoteByTicker("USD/RUB");
    } catch (error) {
        console.error("Ошибка обновления курса:", error);
        alert("Ошибка обновления курса: " + error.message);
    }
}

// =======================
// Инициализация страниц
// =======================

async function initDashboardPage() {
    await loadCurrentUser();
    await loadPortfolio();
    await loadAssets();
    await loadTransactions();

    await rotateCurrentRate();
    await loadMainChartRate("USD/RUB");

    setInterval(rotateCurrentRate, 10000);

    setInterval(() => {
        loadMainChartRate("USD/RUB");
    }, 10000);
} 

async function initProfilePage() {
    await loadCurrentUser();
    await loadPortfolio();
    await loadTransactions();
}

async function initCourseChartPage() {
    const assetId = localStorage.getItem("selectedAssetId") || "1";
    await loadLatestQuoteByAssetId(assetId);
}

function getProfitClass(value) {
    const number = Number(value || 0);

    if (number > 0) {
        return "profit-positive";
    }

    if (number < 0) {
        return "profit-negative";
    }

    return "profit-neutral";
}

function formatProfit(value) {
    const number = Number(value || 0);

    if (Number.isNaN(number)) {
        return "0.00";
    }

    if (number > 0) {
        return "+" + number.toFixed(2);
    }

    return number.toFixed(2);
}

async function loadPositions() {
    const tbody = document.getElementById("positionsTableBody");

    if (!tbody) {
        return [];
    }

    try {
        const positions = await request("/api/positions", {
            method: "GET"
        });

        tbody.innerHTML = "";

        if (!positions || positions.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="8">Купленные активы пока отсутствуют</td>
                </tr>
            `;

            setText("positionsCount", "0");
            setText("positionsTotalValue", "0.00");
            setText("positionsTotalProfit", "0.00");

            return [];
        }

        let totalValue = 0;
        let totalProfit = 0;

        positions.forEach(position => {
            const currentValue = Number(position.currentValue || 0);
            const profit = Number(position.profit || 0);

            totalValue += currentValue;
            totalProfit += profit;

            const row = document.createElement("tr");

            row.innerHTML = `
                <td>
                    <div class="asset-cell">
                        <strong>${position.ticker || "-"}</strong>
                        <span>ID актива: ${position.assetId}</span>
                    </div>
                </td>

                <td>${position.assetName || "-"}</td>

                <td>${formatNumber(position.qty)}</td>

                <td>${formatNumber(position.avgPrice)}</td>

                <td>${formatNumber(position.currentPrice)}</td>

                <td>${formatNumber(position.currentValue)}</td>

                <td>
                    <span class="${getProfitClass(position.profit)}">
                        ${formatProfit(position.profit)}
                    </span>
                </td>

                <td>
                    <button class="small-action-btn" onclick="goToBuyAssetFromPosition(${position.assetId})">
                        Купить ещё
                    </button>
                </td>
            `;

            tbody.appendChild(row);
        });

        setText("positionsCount", positions.length);
        setText("positionsTotalValue", formatNumber(totalValue));
        setText("positionsTotalProfit", formatProfit(totalProfit));

        const totalProfitElement = document.getElementById("positionsTotalProfit");

        if (totalProfitElement) {
            totalProfitElement.className = "big " + getProfitClass(totalProfit);
        }

        return positions;
    } catch (error) {
        console.error("Ошибка загрузки купленных активов:", error);

        tbody.innerHTML = `
            <tr>
                <td colspan="8">Не удалось загрузить купленные активы</td>
            </tr>
        `;

        setText("positionsCount", "-");
        setText("positionsTotalValue", "-");
        setText("positionsTotalProfit", "-");

        return [];
    }
}

function goToBuyAssetFromPosition(assetId) {
    localStorage.setItem("selectedAssetId", assetId);
    window.location.href = "/buy-asset.html";
}

// =======================
// Вспомогательные функции
// =======================

function setText(id, value) {
    const element = document.getElementById(id);

    if (element) {
        element.textContent = value ?? "";
    }
}

function formatMoney(value, currency = "USD") {
    if (value === null || value === undefined || value === "") {
        return "0.00" + currency;
    }

    const number = Number(value);

    if (Number.isNaN(number)) {
        return value + " " + currency;
    }

    return `${number.toFixed(2)} ${currency}`;
}

async function initPositionsPage() {
    await loadCurrentUser();
    await loadPositions();
}

function formatDateTime(value) {
    if (!value) {
        return "";
    }

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
        return value;
    }

    return date.toLocaleString("ru-RU");
}