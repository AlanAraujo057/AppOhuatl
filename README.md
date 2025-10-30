# AppOhuatl Monorepo

Estructura para app móvil (Kotlin/Android), backend, base de datos y contrato Soroban (Stellar). Esta estructura permite escalar sin bloquear el estado actual del proyecto Android.

## Estructura

- `frontend/`
  - `android/` → Documentación e integración con el proyecto Android actual (el código Android existente sigue en la raíz por ahora)
- `backend/`
  - `README.md` → Especificación de API
  - `src/` → Código del backend (a definir; sugerido Ktor/Node)
- `database/`
  - `schema.sql` → Esquema inicial (usuarios, plantaciones, lecturas, mints, wallets)
- `contracts/`
  - `soroban/` → Contrato Soroban (Stellar) en Rust

## Notas sobre el proyecto Android existente
El proyecto Android actual vive en la raíz (`app/`, `gradle/`, etc.). Para no romper builds, NO movimos archivos aún. El plan es migrarlo a `frontend/android` cuando definamos el nuevo módulo.

## Flujo funcional
1. Usuarios se registran/inician sesión (backend).
2. Se registran plantaciones y se envían lecturas de CO₂ capturado.
3. El backend calcula tokens mintables y ejecuta `mint` en Soroban.
4. El usuario ve su balance de carbono y puede convertir a moneda.

## Próximos pasos
- Implementar backend (Ktor recomendado) según `backend/README.md`.
- Completar contrato Soroban y desplegar en red de pruebas.
- Conectar app Android con endpoints del backend.