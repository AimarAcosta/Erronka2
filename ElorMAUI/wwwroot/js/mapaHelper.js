window.mapaActual = null;

window.iniciarMapa = function (id, lat, lon, popupHtml) {
    if (window.mapaActual) {
        window.mapaActual.remove();
    }

    var map = L.map(id).setView([lat, lon], 15);
    window.mapaActual = map;

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap'
    }).addTo(map);

    // Marcador con HTML y el popup abierto por defecto
    L.marker([lat, lon]).addTo(map).bindPopup(popupHtml).openPopup();

    setTimeout(() => { map.invalidateSize(); }, 400);
};
};