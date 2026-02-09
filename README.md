# ElorAdmin - Sistema de Gestión Educativa

**ElorAdmin** es una aplicación web para la gestión de centros educativos desarrollada con Angular 19+. Permite la gestión de usuarios, reuniones, horarios y matrículas según diferentes roles.

## 📋 Descripción

Sistema de administración educativa que permite:
- **God (Superadmin)**: CRUD completo de todos los usuarios, estadísticas globales
- **Admin (Idazkaritza)**: Gestión de profesores y estudiantes (no puede gestionar gods/admins)
- **Profesor (Irakaslea)**: Ver horario, gestionar reuniones, buscar alumnos, crear reuniones
- **Estudiante (Ikaslea)**: Ver su información, horario y reuniones pendientes

## 🛠️ Tecnologías

### Frontend
- **Angular 19+** (Standalone Components)
- **TypeScript 5.8**
- **Bootstrap 5** (CSS responsive)
- **MapLibre GL** (Mapas interactivos)
- **i18n** (Multiidioma: Euskera, Español, Inglés)

### Backend
- **Node.js + Express**
- **TypeORM**
- **MySQL/PostgreSQL** (conexión directa por red, sin API)

## 📁 Estructura del Proyecto

```
frontend/
├── src/
│   ├── app/
│   │   ├── pages/
│   │   │   ├── home-admin/      # Panel administrador
│   │   │   ├── home-god/        # Panel superadmin
│   │   │   ├── home-student/    # Panel estudiante
│   │   │   ├── home-teacher/    # Panel profesor
│   │   │   └── login/           # Página de login
│   │   ├── services/
│   │   │   ├── auth.ts          # Servicio de autenticación
│   │   │   ├── auth.guard.ts    # Guard de rutas
│   │   │   ├── users.ts         # CRUD usuarios
│   │   │   ├── meetings.ts      # Gestión reuniones
│   │   │   ├── schedule.ts      # Horarios
│   │   │   ├── centros.ts       # Centros educativos
│   │   │   ├── ciclos.ts        # Ciclos formativos
│   │   │   └── translation.ts   # Traducciones
│   │   ├── shared/
│   │   │   ├── centro-selector/ # Selector de centros con mapa
│   │   │   ├── navbar/          # Barra de navegación
│   │   │   └── footer/          # Pie de página
│   │   └── pipes/
│   │       └── translate.pipe.ts
│   └── assets/
│       ├── data/centros.json    # Datos de OpenData Euskadi
│       └── i18n/                # Archivos de traducción
│           ├── eu.json          # Euskera
│           ├── es.json          # Español
│           └── en.json          # Inglés
backend/
├── src/
│   ├── entities/                # Entidades TypeORM
│   └── index.ts                 # Servidor Express
```

## 🚀 Instalación

### Requisitos previos
- Node.js 18+
- npm o pnpm

### Pasos

1. **Clonar repositorio**
```bash
git clone <repo-url>
cd Erronka2
```

2. **Instalar dependencias del frontend**
```bash
cd frontend
npm install
```

3. **Instalar dependencias del backend**
```bash
cd backend
npm install
```

4. **Configurar base de datos**
  - Crear base de datos MySQL/PostgreSQL (puede estar en otro equipo)
  - Configurar credenciales y la IP en `backend/src/data-source.ts` para conectar por red

5. **Ejecutar backend**
```bash
cd backend
npm start
```
El backend se conecta directamente a la base de datos por red (no hay API).

6. **Ejecutar frontend**
```bash
cd frontend
ng serve
```

7. **Abrir aplicación**
   - Navegar a `http://localhost:4200`

## 👥 Roles y Permisos

| Funcionalidad | God | Admin | Profesor | Estudiante |
|---------------|-----|-------|----------|------------|
| Ver usuarios | ✅ | ✅ | ❌ | ❌ |
| Crear usuarios | ✅ | ✅* | ❌ | ❌ |
| Eliminar usuarios | ✅* | ✅** | ❌ | ❌ |
| Ver horario | ✅ | ✅ | ✅ | ✅ |
| Gestionar reuniones | ✅ | ✅ | ✅ | ❌ |
| Crear reuniones | ✅ | ✅ | ✅ | ❌ |
| Buscar alumnos | ✅ | ✅ | ✅ | ❌ |

\* God no puede eliminarse a sí mismo  
\** Admin no puede crear/eliminar gods ni admins

## 🌍 Multiidioma

La aplicación soporta 3 idiomas:
- 🟢 **Euskera** (eu) - Idioma por defecto
- 🟡 **Español** (es)
- 🔵 **English** (en)

El selector de idioma está disponible en la barra de navegación.

## 🗺️ Selector de Centros

El componente de selección de centros incluye:
- **Mapa interactivo** con MapLibre
- **Filtros**: Tipo, Territorio, Municipio
- **Lista paginada** (10 centros por página)
- **Datos**: OpenData Euskadi (centros educativos de Euskadi)

## 📱 Responsive Design

La aplicación es completamente responsive:
- Desktop (1200px+)
- Tablet (768px - 1199px)
- Mobile (<768px)

## 🔐 Autenticación

- Login basado en usuario/contraseña
- Guards de Angular para proteger rutas
- Redirección automática según rol

## 📝 Convenciones de Código

- **Métodos en español**: `obtenerUsuarios()`, `crearReunion()`, `eliminarUsuario()`
- **Componentes standalone**: Sin módulos tradicionales
- **Servicios inyectables**: Patrón singleton con `providedIn: 'root'`

## 🧪 Testing

```bash
# Tests unitarios
ng test

# Tests e2e
ng e2e
```

## 📄 Licencia

Proyecto académico - 2º DAM (Desarrollo de Aplicaciones Multiplataforma)

---

**Autor**: Estudiante 2º DAM  
**Centro**: CIFP Elorrieta-Erreka Mari

---

## 🛠️ Guía paso a paso para crear este proyecto desde cero (o uno similar)

### 1. Planificación y análisis
- Define el objetivo del sistema y los roles de usuario.
- Haz un esquema de las entidades principales (usuarios, reuniones, horarios, etc).
- Decide la tecnología: Angular para frontend, Node.js/Express para backend, MySQL/PostgreSQL para base de datos.

### 2. Preparación del entorno
- Instala Node.js y npm.
- Instala Angular CLI globalmente:
  ```bash
  npm install -g @angular/cli
  ```
- Instala un gestor de base de datos (MySQL/PostgreSQL).

### 3. Creación del frontend
- Crea el proyecto Angular:
  ```bash
  ng new <nombre-proyecto> --routing --style=css
  ```
- Estructura las carpetas: `pages`, `services`, `shared`, `assets`.
- Crea componentes standalone para cada página y rol.
- Implementa servicios para la lógica de negocio y llamadas a la API.
- Añade internacionalización (i18n) y archivos de traducción.
- Aplica diseño responsive y profesional (Bootstrap, CSS propio).

### 4. Creación del backend
- Crea una carpeta para el backend y ejecuta:
  ```bash
  npm init -y
  npm install express typeorm mysql2 pg cors
  ```
- Define las entidades en TypeORM.
- Configura la conexión a la base de datos en `data-source.ts` (pon la IP del equipo remoto si la base está en otro equipo).
- Añade control de acceso según roles.

### 5. Configuración de la base de datos
- Crea la base de datos y tablas según las entidades.
- Configura credenciales en el backend.

### 6. Integración frontend-backend
- El frontend consume datos a través del backend, que consulta directamente la base de datos.
- No hay API ni endpoints, solo conexión directa.

### 7. Autenticación y autorización
- Implementa login y guards en Angular.
- Añade control de permisos en el backend.

### 8. Internacionalización y accesibilidad
- Añade archivos de traducción.
- Implementa selector de idioma.
- Asegúrate de que la interfaz sea accesible y usable.

### 9. Testing y validación
- Implementa tests unitarios y e2e en Angular.
- Prueba la conexión y consultas del backend a la base de datos.

### 10. Despliegue
- Prepara scripts de build para frontend y backend.
- Documenta el proceso de despliegue.

### 11. Documentación
- Escribe un README detallado con estructura, tecnologías, pasos de instalación y uso.
- Añade instrucciones para cambiar la IP de la base de datos entre local y remota.

---

## 🛠️ Guía ultra detallada para crear este proyecto desde cero (para principiantes)

### 1. Preparar el entorno

#### 1.1 Instalar Node.js y npm
- Ve a https://nodejs.org y descarga la versión LTS.
- Instala siguiendo los pasos del instalador.
- Abre una terminal (PowerShell o CMD).
- Escribe:
  ```bash
  node -v
  npm -v
  ```
- Si ves los números de versión, está listo.

#### 1.2 Instalar Angular CLI
- En la terminal, escribe:
  ```bash
  npm install -g @angular/cli
  ```
- Esto instala la herramienta para crear proyectos Angular.

#### 1.3 Instalar MySQL o PostgreSQL
- Descarga MySQL (https://dev.mysql.com/downloads/installer/) o PostgreSQL (https://www.postgresql.org/download/).
- Instala y crea una base de datos vacía (por ejemplo, llamada `eloradmin`).
- Apunta el usuario y contraseña.

### 2. Crear el frontend (Angular)

#### 2.1 Crear el proyecto
- En la terminal, navega a la carpeta donde quieres el proyecto.
- Escribe:
  ```bash
  ng new eloradmin --routing --style=css
  cd eloradmin
  ```
- Esto crea la estructura básica.

#### 2.2 Instalar dependencias
- Escribe:
  ```bash
  npm install bootstrap maplibre-gl
  ```
- Bootstrap es para el diseño, MapLibre para mapas.

#### 2.3 Crear carpetas y componentes
- En `src/app/`, crea carpetas:
  - `pages` (para cada rol: admin, god, teacher, student, login)
  - `services` (para lógica y llamadas a la API)
  - `shared` (navbar, footer, selector de centro)
- Para crear un componente:
  ```bash
  ng generate component pages/home-admin
  ng generate component pages/login
  ```
- Repite para cada página.

#### 2.4 Crear servicios
- Para crear un servicio:
  ```bash
  ng generate service services/users
  ng generate service services/auth
  ```
- Los servicios gestionan datos y llamadas a la API.

#### 2.5 Configurar internacionalización
- Crea carpeta `assets/i18n`.
- Añade archivos `es.json`, `eu.json`, `en.json` con traducciones.
- Usa el servicio de traducción en los componentes.

#### 2.6 Configurar diseño
- En `src/styles.css`, importa Bootstrap:
  ```css
  @import 'bootstrap/dist/css/bootstrap.min.css';
  ```
- Personaliza los estilos en los archivos CSS de cada página.

#### 2.7 Configurar rutas
- En `app.routes.ts`, define rutas para cada página.
- Usa guards para proteger rutas según el rol.

### 3. Crear el backend (Node.js + Express)

#### 3.1 Crear el proyecto
- En la terminal, navega a la carpeta `backend`.
- Escribe:
  ```bash
  npm init -y
  npm install express typeorm mysql2 pg cors
  ```
- Esto crea el backend y las dependencias.

#### 3.2 Crear estructura
- Crea carpeta `src/entities` para las entidades (por ejemplo, User.ts, Reunion.ts).
- Crea `src/data-source.ts` para la configuración de la base de datos.
- Crea `src/index.ts` para el servidor principal.

#### 3.3 Definir entidades
- En cada archivo de entidad, define la estructura de la tabla:
  ```typescript
  import { Entity, PrimaryGeneratedColumn, Column } from 'typeorm';
  @Entity()
  export class User {
    @PrimaryGeneratedColumn()
    id: number;
    @Column()
    nombre: string;
    @Column()
    rol: string;
    // ...otros campos
  }
  ```

#### 3.4 Configurar la base de datos
- En `data-source.ts`, pon los datos de tu base:
  ```typescript
  host: 'localhost', // o la IP del equipo remoto
  username: 'tu_usuario',
  password: 'tu_contraseña',
  database: 'eloradmin',
  ```

#### 3.5 Control de acceso
- En el backend, comprueba el rol del usuario antes de permitir acciones.

### 4. Conectar frontend y backend

- El frontend consume datos a través del backend, que consulta directamente la base de datos.
- No hay API ni endpoints, solo conexión directa.

### 5. Autenticación

- Implementa login en el backend para comprobar usuario y contraseña contra la base de datos.
- En el frontend, guarda la sesión y usa guards para proteger rutas según el rol.

### 6. Internacionalización
- Usa el servicio de traducción para mostrar textos según el idioma seleccionado.
- Añade un selector de idioma en la navbar.

### 7. Testing

#### 7.1 Frontend
- Para tests unitarios:
  ```bash
  ng test
  ```
- Para tests e2e:
  ```bash
  ng e2e
  ```

#### 7.2 Backend
- Usa herramientas como DBeaver o MySQL Workbench para probar la conexión y consultas.

### 8. Despliegue

#### 8.1 Preparar build
- En el frontend:
  ```bash
  ng build --prod
  ```
- En el backend, asegúrate de que la base de datos esté configurada y el servidor corriendo.

---

## 🛠️ Guía ultra detallada para consultas directas a la base de datos en red (sin API)

### Contexto
En este proyecto, el backend no expone una API REST, sino que realiza consultas directas a una base de datos MySQL/PostgreSQL ubicada en otro equipo (por ejemplo, usando phpMyAdmin). El frontend consume datos a través del backend, que se conecta por red a la base de datos.

### 1. Preparar la base de datos remota
- En el equipo remoto, instala MySQL/PostgreSQL y phpMyAdmin.
- Crea la base de datos y las tablas necesarias.
- Configura el usuario de la base de datos para permitir conexiones desde otros equipos (no solo localhost).
- Apunta la IP del equipo remoto y el puerto de la base de datos (por defecto: 3306 para MySQL, 5432 para PostgreSQL).

### 2. Configurar el backend para consultas por red
- En `backend/src/data-source.ts`, pon la IP del equipo remoto en la propiedad `host`:
  ```typescript
  host: '10.5.104.124', // ejemplo de IP remota
  ```
- Pon el usuario, contraseña y nombre de la base de datos.
- Asegúrate de que el puerto sea el correcto.
- Si el equipo remoto tiene firewall, abre el puerto de la base de datos.

### 3. Probar la conexión
- En el backend, ejecuta el servidor:
  ```bash
  npm start
  ```
- Si la conexión es correcta, el backend podrá consultar y modificar datos en la base remota.
- Si hay error de conexión, revisa:
  - IP y puerto
  - Usuario y contraseña
  - Permisos de usuario
  - Firewall y reglas de red

### 4. Cambiar a base de datos local
- Para usar una base de datos local, cambia la propiedad `host` en `data-source.ts` a:
  ```typescript
  host: 'localhost',
  ```
- Asegúrate de que la base de datos local tenga las mismas tablas y datos.
- Reinicia el backend.

### 5. Acceso y gestión desde phpMyAdmin
- Puedes gestionar la base de datos desde phpMyAdmin en el equipo remoto.
- Si necesitas importar/exportar datos, usa las herramientas de phpMyAdmin.

### 6. Consejos para principiantes
- Si nunca has hecho esto:
  - Pide ayuda para configurar permisos de MySQL/PostgreSQL.
  - Usa herramientas como DBeaver o MySQL Workbench para probar la conexión desde tu equipo.
  - Si el backend no conecta, revisa la configuración de red y firewall.
  - Documenta cada cambio y prueba.

---

**Resumen:**
- El backend consulta directamente la base de datos por red, usando la IP del equipo remoto.
- Cambia la IP por `localhost` para usar una base local.
- No hay API REST, solo conexión directa a la base de datos.


