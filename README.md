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
Es importante para pruebas en local crear un certificado HTTPS 
```bash
# Genera la llave privada del servidor
openssl genrsa -out server.key 2048

#  Crea la petición de firma (CSR), con CN=10.0.2.2
openssl req -new -key server.key \
  -out server.csr \
  -subj "/C=ES/ST=Madrid/L=Madrid/O=MiApp/OU=Dev/CN=10.0.2.2"

#  Firmar el CSR con el CA, incluyendo SAN para 10.0.2.2 y localhost
cat > extfile.cnf <<EOF
subjectAltName = IP:10.0.2.2, IP:127.0.0.1
EOF

openssl x509 -req -in server.csr \
  -CA ca.crt -CAkey ca.key -CAcreateserial \
  -out server.crt -days 365 -sha256 \
  -extfile extfile.cnf
```
Añadirlo a la carpeta raw y añadir el certificado a network_security_config.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <!-- Aplica a toda la app -->
    <base-config>
        <trust-anchors>
            <!--  confia en los CAs del sistema -->
            <certificates src="system" />
            <!--     añade el CA embebida, ca cambialo por el nombre de tu certificado -->
               <certificates src="@raw/ca" />
           </trust-anchors>
       </base-config>
   </network-security-config>
```

## Estado del servidor
La app comprueba la salud del backend mediante el endpoint `/actuator/health`.


## Licencia
El proyecto se distribuye sin una licencia específica.
