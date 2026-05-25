# KIRU+ Android — Candidatos a Screenshots para Google Play Store

**Estado:** lista preparatoria — no requiere Play Console para generarse.  
**Fecha:** 2026-05-24  
**Objetivo:** tener listo el set de screenshots antes de que se desbloquee la cuenta de Play Console.

---

## Formato requerido por Google Play

- Tamaño mínimo: 320px en el lado más corto.
- Tamaño máximo: 3840px en cualquier lado.
- Relación de aspecto: 16:9 o 9:16 (portrait preferido).
- Formatos: JPG o PNG, 24-bit, sin alpha.
- Mínimo 2 screenshots; máximo 8.
- Recomendado: capturar en Pixel 8 Pro (6.7 in) con pantalla en español.

---

## Screenshots prioritarios (P0 — obligatorios)

| # | Pantalla | Estado a capturar | Notas |
|---|----------|-------------------|-------|
| 1 | **Home** | Bienvenida con Quick Actions + Library card visible | Capturar con datos reales de usuario |
| 2 | **Biblioteca — Módulos** | Lista de 88 módulos clínicos con scroll visible | Tab "Módulos de estudio" activo |
| 3 | **Biblioteca — Currículum** | Bloque expandido mostrando unidades y capítulos | Tab "Currículum" activo |
| 4 | **Quiz** | Pregunta en progreso con opciones + barra de progreso | No mostrar respuesta correcta (spoiler) |
| 5 | **Logbook** | Lista de procedimientos registrados (mínimo 2 entradas visibles) | Datos de prueba médicamente plausibles |
| 6 | **Dr. Kapibaya** | Conversación activa con respuesta médica visible | Pregunta sobre ATLS o Tokyo Guidelines |

## Screenshots secundarios (P1 — recomendados)

| # | Pantalla | Estado a capturar | Notas |
|---|----------|-------------------|-------|
| 7 | **K-Tools** | Menú de calculadoras con categorías expandidas | Mostrar categoría Emergencia visible |
| 8 | **Perlas clínicas** | Lista de perlas con categoría seleccionada | Perlas de UCI/Cuidados críticos |

---

## Pantallas que NO deben capturarse para Play Store

| Pantalla | Razón |
|----------|-------|
| Login / Register | Muestra campos vacíos — no atractivo para tienda |
| Disclaimer médico | Texto legal — solo confunde en screenshot |
| Settings | Pantalla de configuración sin valor de marketing |
| AccountDeletion / DataExport | Flujos legales/destructivos |
| Paywall | No implementado aún (Sprint B pendiente) |

---

## Proceso de captura (sin Play Console)

1. Arrancar la app en emulador Pixel 8 Pro (1080 × 2400, densidad 420dpi).
2. Idioma del sistema: Español.
3. Tema: System (Light).
4. Usar Android Studio → Device Manager → Screenshot (botón en la barra lateral) o `adb exec-out screencap -p > screenshot.png`.
5. Guardar en `docs/screenshots/` (no commitear archivos >5 MB en el repo).
6. Revisar que no haya PII real (nombre de usuario real, email personal, etc.).

---

## Checklist antes de subir al Play Console

- [ ] Resolución mínima verificada (320px lado corto)
- [ ] Sin PII real visible
- [ ] Screenshots en español
- [ ] Sin mensajes de error visibles
- [ ] Sin UI vacía o en estado "cargando"
- [ ] Descargo médico no aparece en ningún screenshot
- [ ] Revisados por legal antes de publicar
