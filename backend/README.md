# Backend (API)

Recomendado: Kotlin + Ktor (o Node.js + Express). Expone endpoints para autenticacion, lecturas de CO2, balance de carbono y acuñacion de tokens via Soroban.

## Endpoints (version inicial)

- POST `/auth/register`
  - body: `{ email, password, fullName }`
  - resp: `{ userId, email }`
- POST `/auth/login`
  - body: `{ email, password }`
  - resp: `{ accessToken }`
- GET `/users/me`
  - header: `Authorization: Bearer <token>`
  - resp: `{ userId, email, fullName }`
- POST `/plantations`
  - body: `{ name, location, areaHectares }`
  - resp: `{ plantationId }`
- POST `/readings`
  - body: `{ plantationId, co2Kg, readingAt }`
  - resp: `{ readingId }`
- GET `/balances/carbon`
  - resp: `{ totalCo2Kg, mintableTokens }`
- POST `/tokens/mint`
  - body: `{ amountTokens }`
  - resp: `{ txHash, amountTokens }`
- GET `/tokens/transactions`
  - resp: `[{ id, amountTokens, txHash, createdAt }]`

## Logica de negocio (resumen)
- `co2Kg` acumulado por usuario/plantacion.
- Regla inicial: `1 token = 1 kg CO2` (ajustable en configuracion backend).
- Solo acuñar tokens si hay saldo mintable y si el wallet del usuario esta verificado.
- Registrar `token_mints` con `txHash` de Soroban.

## Integracion con Soroban (Stellar)
- Mantener en configuracion: `network`, `rpc_url`, `contract_id`, `admin_secret` (solo en servidor).
- Backend invoca metodo `mint` del contrato con la direccion del usuario y el monto aprobado.

## Seguridad
- JWT para sesiones.
- Validacion de entrada y rate-limiting basico en `/tokens/mint`.
- Auditoria minima en cada mint y lectura.

## Quickstart (local)

1) Requisitos: JDK 17 y Gradle (o usa el wrapper si lo agregas).
2) Variables de entorno (o usa `application.conf`):
   - `JWT_SECRET`, `DATABASE_URL`, `SOROBAN_RPC_URL`, `SOROBAN_NETWORK`, `CARBON_CONTRACT_ID`, `ADMIN_SECRET`.
3) Ejecutar en desarrollo:
```bash
cd backend
./gradlew run        # si tienes wrapper, o gradle run
```
4) Probar endpoints:
```bash
curl -X GET http://localhost:8080/balances/carbon
```

## Notas
- Los handlers actuales son stubs. Conectaremos a Postgres y Soroban en los siguientes pasos.
- El contrato Soroban y su `contract_id` deben configurarse cuando despliegues en testnet.
