# U2P4Conversor – Conversor de unidades (Android)

**Práctica 4 · PMDM · 2º DAM**
**Autor:** Jose Juan Alemany Marquez

Aplicación Android (Java, Android Studio) que convierte cantidades entre unidades de medida, en los dos sentidos. Empezó como un conversor de pulgadas a centímetros (parte guiada) y se amplió en las actividades 1 a 6.

## Vídeo explicativo

▶️ **Enlace al vídeo (YouTube, oculto):** _pendiente de añadir_ → `https://youtu.be/XXXXXXXXXXX`

## Qué hace la aplicación

- **3 conversiones, todas en doble sentido:** pulgadas ↔ centímetros, kilogramos ↔ libras y kilómetros ↔ millas.
- Se elige la conversión en un **Spinner** y el sentido con un **Switch** («Invertir sentido»). El título y la pista del campo se actualizan solos.
- El resultado se muestra con **dos decimales** en un `TextView`.
- **Validaciones** (una sola excepción capturada por el único `try/catch` de `setUI()`):
  - campo vacío → «El valor no puede estar vacío»
  - valor menor que 1 → «Sólo números >=1»
  - texto que no es un número → «Introduce un número válido»
  
  El error se ve en rojo bajo el botón *Convertir* y el `TextView` solo es visible cuando hay error.
- **Logs** (`LogsConversor`) de todos los métodos del ciclo de vida y de cada botón pulsado.
- **Conserva el estado al girar** el dispositivo (resultado y error).
- **Layout horizontal propio** (`layout-land`) con dos columnas, y fondo específico para horizontal.
- **Botón de ayuda (?)** que abre `HelpActivity` con instrucciones y explicación de los errores.
- **Valoración** con `RatingBar` guardada en `SharedPreferences` (extra).
- Textos de la interfaz en `strings.xml`; comentarios `// TODO: Activ X.Y` en cada cambio.

## Ramas de Git

| Rama | Contenido |
|------|-----------|
| `guided` | Parte A guiada: pulgadas → centímetros, icono, fondo, `try/catch`. |
| `activ1` | Conversión en ambos sentidos (Switch) y dos decimales. |
| `activ2` | Excepciones (`>=1`, vacío) y `TextView` de error en rojo. |
| `activ3` | Logs del ciclo de vida y de los botones. |
| `activ4` | Estado conservado al girar la pantalla. |
| `activ5` | Nuevas conversiones, layout horizontal y pantalla de ayuda. |
| `activ6` | Valoración persistente (ampliación extra). |
| `main` | App completa: fusión de todas las ramas. |

## Estructura

```
app/src/main/java/dam/josejuan/u2p4conversor/
├── MainActivity.java   # pantalla principal
├── Conversion.java     # una conversión (unidades + factor), válida en los dos sentidos
└── HelpActivity.java   # pantalla de ayuda
app/src/main/res/
├── layout/ y layout-land/   # activity_main (vertical y horizontal), activity_help
├── drawable/ y drawable-land/   # fondo (meter_background) y icono de ayuda
└── mipmap-*/                # icono de la aplicación
```

## Respuestas y justificaciones

### 4.1 ¿Qué pasa con la información de los `TextView` al girar el dispositivo y por qué?

Se pierde. Al girar cambia la configuración y Android **destruye la Activity y crea una nueva** (`onDestroy` → `onCreate`). La interfaz se infla otra vez desde el layout, así que los `TextView` vuelven a su estado inicial: un `TextView` no guarda su texto por defecto y su visibilidad tampoco, por lo que el resultado desaparece y el `TextView` de error vuelve a estar oculto. Las variables de la Activity también se pierden. Los `EditText`, el `Switch` y el `Spinner` con `id` sí recuperan su estado automáticamente.

### 4.2 Solución

Se sobrescriben `onSaveInstanceState` (guarda resultado, mensaje de error y su visibilidad en el `Bundle`) y `onRestoreInstanceState` (los recupera). Ambos escriben un `Log`. La restauración va después de `super` porque el `Switch` restaurado llama a su listener, que limpia los mensajes.

### 5.1 Diseño de la UI

- Un único `Spinner` + un `Switch` evitan duplicar campos y botones por cada conversión: la pantalla es igual para 3 o para 10 conversiones.
- Las conversiones se definen en una tabla (`CONVERSIONES`) con la clase `Conversion` (unidades y factor). Añadir una nueva solo requiere una línea en esa tabla y una en `R.array.conversiones`.
- Un único método `convertir` aplica las mismas validaciones a todas las conversiones.
- En horizontal se usa un layout de dos columnas (selección a la izquierda, datos y resultado a la derecha) para que todo se vea sin desplazarse.

### 6 Valoración (extra)

- Interfaz: un `RatingBar` de 5 estrellas con un texto encima («Valora la aplicación» / «Gracias por tu valoración») en la pantalla principal.
- `onStart`: lee la valoración de `SharedPreferences`. Si ya existe, la muestra y pone el `RatingBar` como indicador (solo lectura). Si no, se puede valorar y cambiar la valoración mientras la Activity esté en primer plano.
- `onStop`: guarda la valoración (`apply()`), salvo que sea un giro de pantalla (`isChangingConfigurations()`), porque en ese caso el usuario sigue en primer plano.
- Así, cuando la app se cierra o pasa a segundo plano y se vuelve a mostrar, la valoración ya no se puede cambiar.

## Cómo ejecutarla

1. Clonar el repositorio y abrirlo con Android Studio (se descarga Gradle 8.7 y las dependencias).
2. Ejecutar la configuración `app` en un AVD o dispositivo (minSdk 24).
