# FacilGimApp

Aplicación Android desarrollada como proyecto fin de grado. Permite gestionar entrenamientos y ejercicios desde un dispositivo móvil.

## Características
- **Gestión de ejercicios y entrenamientos**: creación, edición, búsqueda y relación entre ellos.
- **Series y tipos de entrenamiento**: manejo completo de series y categorías.
- **Usuarios y autenticación**: registro, login y administración de usuarios.
- **Estadísticas y calendario** en la pantalla principal.

La interfaz utiliza un controlador de navegación con fragmentos para secciones como login, listado de entrenamientos, detalle, usuario, etc.

## Tecnologías
- Java 11
- Android SDK 26-35
- [Retrofit](https://square.github.io/retrofit/) y OkHttp para la API REST
- Material Components y Glide para la interfaz
- Arquitectura MVVM con LiveData y ViewModel
- Gradle Kotlin DSL con Dokka para documentación

## Estructura del repositorio
- `Proyecto/` – proyecto Android con el módulo `app`
  - `app/src/main/java` – código fuente de la aplicación
  - `app/src/main/res` – recursos (layouts, cadenas, imágenes)
  - `app/release` – APK de distribución
- `docs/javadoc/` – documentación generada

## Documentación
En `docs/javadoc` se incluye la API generada mediante Dokka. Por ejemplo, la interfaz [`ApiService`](docs/javadoc/com/proyecto/facilgimapp/network/ApiService.html) describe todos los endpoints disponibles para ejercicios, entrenamientos, series, tipos y usuarios.

## Compilación y pruebas
Se compila con el wrapper de Gradle:

```bash
./gradlew assembleDebug
```

Las pruebas unitarias se ejecutan con:

```bash
./gradlew test
```

## Estado del servidor
La app comprueba la salud del backend mediante el endpoint `/actuator/health`.


## Licencia
El proyecto se distribuye sin una licencia específica.
