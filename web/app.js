"use strict";
(() => {
  // Static site only. No requests, accounts, storage, cookies, analytics or service workers.
  const navButton = document.getElementById("nav-toggle");
  const navLinks = document.getElementById("nav-links");
  if (navButton && navLinks) {
    const closeMenu = () => { navLinks.classList.remove("open"); navButton.setAttribute("aria-expanded", "false"); };
    navButton.addEventListener("click", () => {
      const open = navLinks.classList.toggle("open");
      navButton.setAttribute("aria-expanded", String(open));
    });
    navLinks.querySelectorAll("a").forEach(a => a.addEventListener("click", closeMenu));
    document.addEventListener("keydown", e => { if (e.key === "Escape") closeMenu(); });
  }

  const screenshots = [
    ["01-home.png", "Home"],
    ["02-subscriptions.png", "Subscriptions"],
    ["03-add-subscription.png", "Add subscription"],
    ["04-service-library.png", "Service library"],
    ["05-details.png", "Subscription details"],
    ["06-calendar.png", "Renewal calendar"],
    ["07-payment-details.png", "Payment details"],
    ["08-insights.png", "Spending insights"],
    ["09-reminders.png", "Reminders"],
    ["10-settings.png", "Settings"],
    ["11-categories.png", "Categories"],
    ["12-history.png", "Payment history"],
    ["13-archive.png", "Archive"],
    ["14-backup.png", "Backup and restore"],
    ["15-budget.png", "Monthly budget"],
    ["16-savings.png", "Savings"],
    ["17-trials.png", "Trial tracker"],
    ["18-pro.png", "Edition overview"],
    ["19-privacy.png", "Privacy"],
    ["20-about.png", "About"]
  ];
  const featured = new Set(["01-home.png","02-subscriptions.png","06-calendar.png","08-insights.png"]);
  const panel = document.getElementById("screens-panel");
  const button = document.getElementById("gallery-expand");
  const tabButtons = Array.from(document.querySelectorAll("[role=tab][data-edition]"));
  const dialog = document.getElementById("screenshot-dialog");
  const modalImage = document.getElementById("dialog-image");
  const modalTitle = document.getElementById("dialog-title");
  const closeDialog = document.getElementById("dialog-close");
  let edition = "free";
  let expanded = false;
  let previousFocus = null;

  const source = file => "assets/screenshots/" + edition + "/" + file;
  const render = () => {
    if (!panel) return;
    const shown = expanded ? screenshots : screenshots.filter(([file]) => featured.has(file));
    panel.replaceChildren();
    shown.forEach(([file,label]) => {
      const figure = document.createElement("figure");
      figure.className = "screen-item";
      const trigger = document.createElement("button");
      trigger.type = "button";
      trigger.className = "screenshot-open";
      trigger.setAttribute("aria-label", "Expand " + edition + " " + label + " screenshot");
      trigger.dataset.file = file;
      const img = document.createElement("img");
      img.src = source(file);
      img.alt = "Actual Android emulator screenshot of SubliX " + edition + ": " + label;
      img.loading = "lazy";
      img.width = 1080;
      img.height = 1920;
      trigger.appendChild(img);
      figure.appendChild(trigger);
      const caption = document.createElement("figcaption");
      caption.textContent = label;
      figure.appendChild(caption);
      panel.appendChild(figure);
    });
    tabButtons.forEach(tab => {
      const selected = tab.dataset.edition === edition;
      tab.setAttribute("aria-selected", String(selected));
      tab.tabIndex = selected ? 0 : -1;
    });
    panel.setAttribute("aria-labelledby", "tab-" + edition);
    if (button) {
      button.setAttribute("aria-expanded", String(expanded));
      button.textContent = expanded ? "Show fewer screens ↑" : "Show all 20 screens ↓";
    }
  };

  tabButtons.forEach((tab,index) => {
    tab.addEventListener("click", () => { edition = tab.dataset.edition; render(); });
    tab.addEventListener("keydown", event => {
      if (!["ArrowLeft","ArrowRight"].includes(event.key)) return;
      event.preventDefault();
      const next = tabButtons[(index + (event.key === "ArrowRight" ? 1 : -1) + tabButtons.length) % tabButtons.length];
      next.focus();
      next.click();
    });
  });
  if (button) button.addEventListener("click", () => { expanded = !expanded; render(); });

  if (panel && dialog && modalImage && modalTitle && closeDialog) {
    panel.addEventListener("click", event => {
      const trigger = event.target.closest("button.screenshot-open");
      if (!trigger) return;
      const match = screenshots.find(([file]) => file === trigger.dataset.file);
      if (!match || !dialog.showModal) return;
      previousFocus = trigger;
      modalImage.src = source(match[0]);
      modalImage.alt = "Actual SubliX " + edition + " Android emulator capture: " + match[1];
      modalTitle.textContent = "SubliX " + (edition === "pro" ? "Pro" : "Free") + " · " + match[1];
      dialog.showModal();
      closeDialog.focus();
    });
    const close = () => { dialog.close(); if (previousFocus && previousFocus.isConnected) previousFocus.focus(); };
    closeDialog.addEventListener("click", close);
    dialog.addEventListener("click", e => { if (e.target === dialog) close(); });
    dialog.addEventListener("close", () => { modalImage.removeAttribute("src"); });
  }
  render();
})();
