# KIRU+ Android — Screenshot QA Checklist

**Version:** 1.0  
**Última actualización:** 2026-05-25  
**Propósito:** Guía operativa para capturar screenshots listos para Google Play Store.  
**Prerequisito:** cuenta Developer desbloqueada, APK release instalado, emulador/device Pixel 6+ o Samsung Galaxy S22+, idioma configurado en ES o EN según sección.

---

## 1. Pantallas candidatas Play Store (P0 — obligatorias)

### P0.1 — Home Dashboard

**Qué debe verse:**
- Bienvenida con nombre de usuario o saludo genérico médico
- Cards activos: Biblioteca, Quiz, K-Tools, Dr. Kapibaya, Bitácora quirúrgica, Perlas
- TopAppBar con logo KIRU+
- BottomNav con 5 tabs visibles

**QUÉ NO debe verse:**
- Paywall roto o RevenueCat error
- Cards sin acción (que abran vacío)
- Debug banners
- Texto técnico (errores de red, IDs)
- Datos de prueba ("test@", "DR_HUERTA")

**Checklist:**
- [ ] ES screenshot capturado
- [ ] EN screenshot capturado
- [ ] Dark mode capturado
- [ ] Sin texto técnico visible
- [ ] Sin pantalla vacía detrás de cards

---

### P0.2 — Biblioteca — Tab Módulos

**Qué debe verse:**
- TopAppBar "Biblioteca" / "Library"
- Tab "Módulos de estudio" seleccionado
- Lista de módulos clínicos con títulos en español
- Ícono de libro + número de temas por módulo
- Scroll visible si hay más de 4 módulos

**QUÉ NO debe verse:**
- Estado vacío (error de parsing)
- Spinner infinito
- Módulos sin título
- Módulos sin contenido (pointCount = 0 si no hay temas)

**Checklist:**
- [ ] ES screenshot capturado
- [ ] EN screenshot capturado
- [ ] Dark mode capturado
- [ ] Al menos 3 módulos visibles en pantalla
- [ ] Sin spinners visibles

---

### P0.3 — Biblioteca — Tab Currículum expandido

**Qué debe verse:**
- Tab "Currículum" seleccionado
- Al menos un bloque expandido mostrando unidades y capítulos
- Capítulos disponibles con ícono de libro y botón "Iniciar quiz" / "Start quiz"
- Capítulos bloqueados con ícono de candado, sin botón de quiz
- Etiqueta "Bloque N: Título"

**QUÉ NO debe verse:**
- Botón de quiz en capítulos bloqueados
- Estado vacío (error de parsing)
- "null" o IDs técnicos visibles
- Texto desbordado en títulos largos

**Checklist:**
- [ ] ES screenshot capturado
- [ ] EN screenshot capturado
- [ ] Dark mode capturado
- [ ] Botón quiz solo en capítulos disponibles ✓
- [ ] Candado visible en capítulos bloqueados ✓

---

### P0.4 — Quiz por capítulo — Pregunta activa

**Qué debe verse:**
- TopAppBar con título del capítulo
- LinearProgressIndicator (ej. "Question 2 of 10")
- Texto de la pregunta claro y legible
- 4 opciones (A/B/C/D) con bordes visibles
- Ninguna opción seleccionada todavía (estado neutro)

**QUÉ NO debe verse:**
- Opciones vacías o sin texto
- Progreso en 0/0
- Colores hardcoded que se rompan en dark mode
- Error state

**Checklist:**
- [ ] ES screenshot capturado (pregunta médica real del asset)
- [ ] EN screenshot capturado
- [ ] Dark mode capturado
- [ ] Progreso visible (ej. "2 de 10")
- [ ] 4 opciones legibles

---

### P0.5 — Quiz por capítulo — Respuesta con explicación

**Qué debe verse:**
- Opción correcta en verde (tertiary M3)
- Opción incorrecta (si aplica) en rojo (error M3)
- Card de "Explicación" con rationale del asset
- Botón "Siguiente" / "Ver resultados" visible

**QUÉ NO debe verse:**
- Colores hex hardcoded
- Rationale vacío
- Botón "Siguiente" antes de responder
- Spinner

**Checklist:**
- [ ] ES screenshot capturado
- [ ] EN screenshot capturado
- [ ] Dark mode capturado
- [ ] Opción correcta destacada visualmente ✓
- [ ] Rationale visible ✓

---

### P0.6 — Dr. Kapibaya Chat

**Qué debe verse:**
- Chat interface con burbuja de bienvenida de Kapibaya
- Input de texto visible
- Avatar o nombre "Dr. Kapibaya"
- Respuesta ya cargada (no spinner)

**QUÉ NO debe verse:**
- Error de Supabase o conexión
- Chat vacío sin bienvenida
- Texto genérico placeholder

**Checklist:**
- [ ] ES screenshot capturado
- [ ] EN screenshot capturado
- [ ] Respuesta médica visible (no spinner)

---

### P0.7 — Bitácora Quirúrgica (Logbook)

**Qué debe verse:**
- Lista de procedimientos registrados o state vacío con instrucción clara
- Botón de agregar procedimiento visible
- Campos: fecha, procedimiento, notas

**QUÉ NO debe verse:**
- Error de Supabase
- Lista infinita de placeholders

**Checklist:**
- [ ] ES screenshot capturado
- [ ] EN screenshot capturado
- [ ] Sin datos de prueba ("test surgery")

---

## 2. Pantallas candidatas Play Store (P1 — recomendadas)

### P1.1 — K-Tools (Calculadoras clínicas)

**Qué debe verse:**
- Menú de calculadoras por categoría (ej. Escalas, Fórmulas)
- Al menos 3 calculadoras listadas con nombres reconocibles (APACHE II, SOFA, etc.)
- Íconos o etiquetas por categoría con `heading()` accesible

**QUÉ NO debe verse:**
- Lista vacía
- Calculadora sin nombre

**Checklist:**
- [ ] ES screenshot capturado
- [ ] EN screenshot capturado

---

### P1.2 — Perlas Clínicas

**Qué debe verse:**
- Lista de perlas con título y categoría
- Imagen de perla o placeholder adecuado
- Scroll visible

**QUÉ NO debe verse:**
- Lista vacía (si las perlas están en el asset)
- Imágenes rotas

**Checklist:**
- [ ] ES screenshot capturado
- [ ] EN screenshot capturado

---

## 3. Pantallas a EVITAR en Play Store

| Pantalla | Razón |
|---|---|
| Splash screen | No aporta valor de features |
| Login / Register | No muestra funcionalidad |
| Paywall / RevenueCat | Pendiente de configuración Android |
| Settings | No atractivo visualmente |
| Error states | Nunca en Play Store |
| Account Deletion | Negativo |
| WebView (políticas) | No demuestra funcionalidad |
| Estado vacío de Logbook | Solo si el CTA es claro |

---

## 4. Checklist por idioma

### Español (valores/strings.xml primer idioma)

- [ ] `locale` del device en `es` o `es-MX`
- [ ] "Biblioteca" (no "Library") en TopAppBar
- [ ] "Iniciar quiz" (no "Start quiz") en botón de capítulo
- [ ] "Pregunta N de M" en progreso
- [ ] "Siguiente" / "Ver resultados" en botones de quiz
- [ ] "Explicación" en card de rationale
- [ ] "Dr. Kapibaya" en chat (invariante)

### English (values-en, valores por defecto)

- [ ] `locale` del device en `en-US`
- [ ] "Library" en TopAppBar
- [ ] "Start quiz" en botón de capítulo
- [ ] "Question N of M" en progreso
- [ ] "Next" / "See results" en botones de quiz
- [ ] "Explanation" en card de rationale

---

## 5. Checklist dark mode

| Elemento | Verificar |
|---|---|
| Fondo de cards | `surfaceVariant` (no blanco hardcoded) |
| Texto principal | `onSurface` (no negro hardcoded) |
| Opción correcta | `tertiary` / `tertiaryContainer` |
| Opción incorrecta | `error` / `errorContainer` |
| Card de rationale | `secondaryContainer` |
| Progreso quiz | M3 LinearProgressIndicator |
| Botones | M3 `Button` / `OutlinedButton` / `TextButton` |

Activar dark mode: Settings → Display → Dark theme.

---

## 6. Datos sensibles — prohibidos en screenshots

- Emails reales (`@medsurgery.academy`, `@gmail.com`)
- IDs de Supabase o RevenueCat
- API keys o tokens
- Nombres de usuarios reales
- Datos de pacientes (aunque sean ficticios y realistas)
- Mensajes de error con stack traces
- Credenciales de cuenta de prueba

---

## 7. Resolución y formato recomendados

- **Resolución mínima:** 1080 × 1920 px (portrait)
- **Aspect ratio:** 9:16 o 9:20
- **Formato:** PNG sin compresión
- **Dispositivo:** Pixel 6 Pro / Pixel 9 (Pixel UI limpia, sin marcos de fabricante)
- **Sin marcos de dispositivo** en la primera subida; Play puede añadir el marco automáticamente

---

## 8. Comando para capturar desde ADB

```bash
# Screenshot en device conectado
adb exec-out screencap -p > screenshot_$(date +%Y%m%d_%H%M%S).png

# Pull del device
adb pull /sdcard/Pictures/Screenshots/
```

---

## 9. Estado actual por pantalla

| Pantalla | Implementada | Tests UI | A11y | Dark mode | Screenshot listo |
|---|---|---|---|---|---|
| Home | ✅ | – | parcial | ✅ | ❌ pendiente |
| Biblioteca — Módulos | ✅ | ✅ compile | ✅ | ✅ | ❌ pendiente |
| Biblioteca — Currículum | ✅ | ✅ compile | ✅ | ✅ | ❌ pendiente |
| Quiz por capítulo | ✅ | ✅ compile | ✅ | ✅ | ❌ pendiente |
| K-Tools | ✅ | – | ✅ | ✅ | ❌ pendiente |
| Perlas | ✅ | – | parcial | ✅ | ❌ pendiente |
| Dr. Kapibaya | ✅ | – | parcial | ✅ | ❌ pendiente |
| Logbook | ✅ | – | parcial | ✅ | ❌ pendiente |

**Notas:**
- Tests UI marcados "compile" requieren dispositivo físico o emulador para correr: `./gradlew connectedDebugAndroidTest`
- A11y "parcial" = contentDescriptions presentes, pendiente auditoría con TalkBack en device real
- Dark mode "✅" = usa M3 tokens, sin colores hardcoded confirmado por lint

---

*Actualizar este documento después de cada sprint que agregue o modifique pantallas.*
