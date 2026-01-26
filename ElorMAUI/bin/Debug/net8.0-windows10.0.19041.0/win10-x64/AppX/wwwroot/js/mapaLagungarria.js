// wwwroot/js/mapaHelper.js

window.iniciarMapa = function (idContenedor, lat, lon, nombreCentro) {
    // 1. Comprobar si ya existe un mapa y borrarlo (Limpieza)
    if (window.mapaActual) {
        window.mapaActual.remove();
        window.mapaActual = null;
    }

    // 2. Comprobar si Leaflet está cargado
    if (typeof L === 'undefined') {
        console.error("Leaflet no ha cargado. Revisa el index.html");
        return;
    }

    // 3. Crear el mapa
    var map = L.map(idContenedor).setView([lat, lon], 15);

    // Guardamos la referencia globalmente para poder borrarlo luego
    window.mapaActual = map;

    // 4. Añadir la capa visual (Tiles)
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap',
        maxZoom: 19
    }).addTo(map);

    // 5. Añadir marcador
    L.marker([lat, lon]).addTo(map)
        .bindPopup("<b>" + nombreCentro + "</b>")
        .openPopup();

    // 6. EL TRUCO DE ORO: Forzar el redibujado tras medio segundo
    // Esto arregla el fallo de que se vea gris o incompleto
    setTimeout(function () {
        map.invalidateSize();
        console.log("Mapa redibujado correctamente");
    }, 500);
};