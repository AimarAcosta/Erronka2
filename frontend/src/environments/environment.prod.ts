/**
 * ENVIRONMENT.PROD.TS - Configuración de entorno de producción
 * Define la URL del backend para el entorno de producción
 * Se usa cuando ejecutamos 'ng build --prod'
 */
export const environment = {
  production: true,
  apiUrl: 'http://10.5.104.124:8080/api',
  mapboxToken: 'pk.eyJ1IjoiZWxvcnJpZXRhIiwiYSI6ImNsdTJ2a2E0MzAyZ3cya3A2Z3h4djZ1OXMifQ.XXXXXXXXXX'
};
