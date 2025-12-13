// ajustes.js - Funcionalidad de Ajustes del Sistema

document.addEventListener('DOMContentLoaded', () => {
    const themeSelect = document.getElementById('theme-select');
    const fontSizeSelect = document.getElementById('font-size-select');
    const notificationsToggle = document.getElementById('notifications-toggle');
    const emailUpdatesToggle = document.getElementById('email-updates-toggle');
    const dataSharingToggle = document.getElementById('data-sharing-toggle');
    const languageSelect = document.getElementById('language-select');
    const timezoneSelect = document.getElementById('timezone-select');
    const autoSaveToggle = document.getElementById('auto-save-toggle');
    const resetBtn = document.getElementById('reset-btn');
    const saveBtn = document.querySelector('.save-btn');
    const statusMessage = document.getElementById('status-message');
    const body = document.body;

    // ===== FUNCIONES AUXILIARES =====
    function applySettings(settings) {
        // Tema
        body.classList.remove('theme-dark', 'theme-light', 'theme-gold');
        body.classList.add(`theme-${settings.theme}`);

        // Tamaño fuente
        body.style.fontSize = settings.fontSize === 'small' ? '14px' :
                              settings.fontSize === 'medium' ? '16px' :
                              '18px';

        // Toggles
        notificationsToggle.checked = settings.notifications;
        emailUpdatesToggle.checked = settings.emailUpdates;
        dataSharingToggle.checked = settings.dataSharing;
        autoSaveToggle.checked = settings.autoSave;

        // Idioma y zona horaria
        languageSelect.value = settings.language;
        timezoneSelect.value = settings.timezone;
    }

    function loadSettings() {
        const settings = JSON.parse(localStorage.getItem('ajustes')) || {
            theme: 'dark',
            fontSize: 'medium',
            notifications: true,
            emailUpdates: false,
            dataSharing: false,
            autoSave: false,
            language: 'es',
            timezone: 'utc-5'
        };
        applySettings(settings);
        return settings;
    }

    function saveSettings() {
        const settings = {
            theme: themeSelect.value,
            fontSize: fontSizeSelect.value,
            notifications: notificationsToggle.checked,
            emailUpdates: emailUpdatesToggle.checked,
            dataSharing: dataSharingToggle.checked,
            autoSave: autoSaveToggle.checked,
            language: languageSelect.value,
            timezone: timezoneSelect.value
        };
        localStorage.setItem('ajustes', JSON.stringify(settings));
        applySettings(settings);
        statusMessage.textContent = '✅ Configuración guardada correctamente';
        setTimeout(() => statusMessage.textContent = '', 3000);
    }

    function resetSettings() {
        localStorage.removeItem('ajustes');
        const settings = loadSettings();
        statusMessage.textContent = '🔄 Configuración restablecida';
        setTimeout(() => statusMessage.textContent = '', 3000);
    }

    // ===== EVENTOS =====
    saveBtn.addEventListener('click', e => {
        e.preventDefault();
        saveSettings();
    });

    resetBtn.addEventListener('click', resetSettings);

    // ===== CARGAR CONFIGURACIÓN INICIAL =====
    loadSettings();
});
