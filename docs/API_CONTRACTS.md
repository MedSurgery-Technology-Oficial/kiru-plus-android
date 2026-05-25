# API Contracts — KIRU+ Shared Backend

**Last updated:** 2026-05-24  
**Supabase project:** `tttxmupjteqpljtfgmgo.supabase.co`  
**Owner:** MedSurgery Technology

> This file is the canonical reference for every backend call made by the Android client.  
> **Do not change Edge Function signatures without updating this file and coordinating with iOS.**

---

## Authentication

All requests use the Supabase session JWT automatically injected by `supabase-kt` via `Authorization: Bearer <jwt>`.  
Public endpoints (e.g., anon read-only) use the publishable anon key via header `apikey: <SUPABASE_ANON_KEY>`.

---

## Edge Functions

### `ask_kapibaya_stream`

Streaming Kapibaya AI chat response (Server-Sent Events).

| Field | Value |
|-------|-------|
| URL | `{SUPABASE_URL}/functions/v1/ask_kapibaya_stream` |
| Method | POST |
| Auth | `apikey: SUPABASE_ANON_KEY` (no JWT required) |
| Content-Type | `application/json` |
| Accept | `text/event-stream` |

**Request body:**
```json
{
  "message": "<user message string>",
  "conversationId": "<uuid string>",
  "userId": "<user id string>"
}
```

**SSE event types:**

| Event field | Payload | Meaning |
|-------------|---------|---------|
| `chunk` | `{"content": "<text>"}` | Partial response text; append to buffer |
| `done` | (no data) | Stream complete |
| `error` | `{"error": "<message>"}` | Hard failure; display error to user |

**Android implementation:** `KapibayaRepositoryImpl.kt` via OkHttp SSE.

---

### `ask_kapibaya`

Non-streaming Kapibaya AI chat (single response). Used as fallback.

| Field | Value |
|-------|-------|
| URL | `{SUPABASE_URL}/functions/v1/ask_kapibaya` |
| Method | POST |
| Auth | Session JWT (auto via supabase-kt) |
| Content-Type | `application/json` |

**Request body:**
```json
{
  "message": "<user message string>",
  "conversationId": "<uuid string>"
}
```

**Response:**
```json
{
  "content": "<full response text>"
}
```

---

### `process_account_deletions`

Soft-deletes the authenticated user's account and all associated data.

| Field | Value |
|-------|-------|
| URL | `{SUPABASE_URL}/functions/v1/process_account_deletions` |
| Method | POST |
| Auth | Session JWT (required — anon rejected) |
| Body | (none) |

**Response:** HTTP 200 on success; HTTP 4xx/5xx on failure.

**Android implementation:** `AuthRepositoryImpl.requestAccountDeletion()` via `supabase.functions.invoke(...)`.

---

### `process_data_export`

Exports all data associated with the authenticated user (GDPR Article 20).

| Field | Value |
|-------|-------|
| URL | `{SUPABASE_URL}/functions/v1/process_data_export` |
| Method | POST |
| Auth | Session JWT (required) |
| Body | (none) |

**Response:** HTTP 200 with export payload or download link; HTTP 4xx/5xx on failure.

**Android implementation:** `AuthRepositoryImpl.requestDataExport()` via `supabase.functions.invoke(...)`.

---

### `revenuecat_webhook`

Receives purchase validation events from RevenueCat (server-to-server, not called by the app directly).

| Field | Value |
|-------|-------|
| URL | `{SUPABASE_URL}/functions/v1/revenuecat_webhook` |
| Method | POST |
| Auth | RevenueCat webhook secret (header `Authorization`) |
| Caller | RevenueCat backend, NOT the Android/iOS app |

**Not called by Android client directly.** Listed here for completeness and backend change awareness.

---

## Supabase Postgrest (REST API)

### `store_products` table

Used by `StoreRepositoryImpl` to populate the Store catalog.

| Field | Value |
|-------|-------|
| URL | `{SUPABASE_URL}/rest/v1/store_products` |
| Method | GET |
| Auth | Session JWT (RLS gated — requires authenticated user) |
| Filters | `select=*`, `order=sort_order.asc` |

**Columns read by Android:** `id`, `title`, `subtitle`, `price_display`, `currency`, `image_url`, `stripe_payment_link`, `product_type`, `sort_order`.

---

## Version freeze policy

- **Breaking change** (rename/remove field, change auth model): requires dual-platform coordinated deploy. Never break a deployed field.
- **Additive change** (new optional field): safe to deploy; both platforms ignore unknown fields.
- **New function**: always additive; old clients unaffected.
- **During iOS App Review**: backend is frozen. No migrations, no function deploys, no RLS changes. Coordinate with iOS owner before any action.
