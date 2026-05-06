# ConsumoAPIREST-Android

Aplicación Android que demuestra el uso de **Retrofit** para consumir APIs REST y **Room** para persistencia de datos local, siguiendo las guías del curso de Construcción de Aplicaciones Móviles.

## Objetivo

Esta aplicación implementa una arquitectura moderna con los siguientes componentes:

- **Retrofit**: Consumo de la API JSONPlaceholder para obtener posts
- **Room**: Base de datos local para persistencia y caché
- **Hilt**: Inyección de dependencias
- **Jetpack Compose**: UI moderna y declarativa
- **MVVM**: Arquitectura Modelo-Vista-ViewModel
- **Repository Pattern**: Separación de responsabilidades

## Funcionalidades

- **Lista de Posts**: Muestra posts obtenidos desde la API
- **Posts Favoritos**: Sistema de favoritos persistido localmente
- **Detalle de Post**: Vista detallada de cada post
- **Modo Offline**: Funciona con datos cacheados en Room
- **Refrescar Datos**: Sincronización con la API

## Arquitectura

```
app/
├── data/
│   ├── local/           # Room Database
│   │   ├── entity/      # Entidades de la BD
│   │   ├── dao/         # Data Access Objects
│   │   └── AppDatabase.kt
│   ├── remote/          # Retrofit API
│   │   ├── api/         # Interfaces de API
│   │   ├── dto/         # Data Transfer Objects
│   │   └── NetworkModule.kt
│   └── repository/      # Repositories
├── di/                  # Módulos Hilt
├── presentation/
│   ├── ui/              # Composables UI
│   └── viewmodel/       # ViewModels
└── MainActivity.kt
```

## Tecnologías

- **Kotlin**: Lenguaje de programación
- **Jetpack Compose**: UI framework
- **Room**: Base de datos local
- **Retrofit**: Cliente HTTP
- **Hilt**: Inyección de dependencias
- **Coroutines**: Programación asíncrona
- **Navigation**: Navegación entre pantallas

## Guías Referenciadas

Este proyecto se basa en las siguientes guías del curso:

- [Persistencia de Datos en Android](https://caflorezvi.github.io/guias-apps-moviles/17.persistencia.html)
- [Retrofit y Comunicación con APIs REST](https://caflorezvi.github.io/guias-apps-moviles/18.apis-rest.html)

## Instalación

1. Clonar el repositorio:
```bash
git clone https://github.com/JuanesArangoi/ConsumoAPIREST-Android.git
```

2. Abrir el proyecto en Android Studio

3. Sincronizar el proyecto con Gradle

4. Ejecutar en un dispositivo o emulador

## Requisitos

- Android Studio Hedgehog o superior
- Android SDK 24 (Android 7.0) o superior
- Kotlin 1.9.10

## Configuración

La aplicación utiliza la API pública **JSONPlaceholder** (https://jsonplaceholder.typicode.com/) que no requiere configuración adicional.

## Uso

1. **Lista Principal**: Muestra todos los posts obtenidos de la API
2. **Favoritos**: Click en el ícono ❤️ para marcar como favorito
3. **Filtrar**: Click en el ícono de favoritos en la barra superior para ver solo favoritos
4. **Refrescar**: Click en el ícono de refrescar para sincronizar con la API
5. **Detalle**: Click en cualquier post para ver detalles completos

## Flujo de Datos

1. La aplicación intenta cargar posts desde la API REST
2. Los datos se guardan en la base de datos Room
3. La UI muestra los datos desde Room (Flow)
4. Los cambios se reflejan automáticamente en la UI
5. Los favoritos se persisten localmente

## Componentes Principales

### Room Database
- **PostEntity**: Entidad para posts
- **PostDao**: Interfaz de acceso a datos
- **AppDatabase**: Configuración de la base de datos

### Retrofit API
- **JsonPlaceholderApi**: Interface de la API
- **PostDto**: Modelo de datos de la API
- **NetworkModule**: Configuración de Retrofit

### UI Components
- **PostListScreen**: Lista de posts
- **PostDetailScreen**: Detalle de post
- **PostViewModel**: Lógica de negocio

## Licencia

Este proyecto es parte de la actividad académica del curso de Construcción de Aplicaciones Móviles.
