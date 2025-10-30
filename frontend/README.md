# Frontend (Android)

El codigo Android actual vive en la raiz del repo (`app/`, `gradle/`, etc.). Para no romper el build, aun no fue movido. Este directorio sirve como punto de migracion futura a `frontend/android`.

## Integracion prevista
- Login/Registro → consumir `/auth/register` y `/auth/login` del backend.
- Home → mostrar `totalCo2Kg` y `mintableTokens` desde `/balances/carbon`.
- Enviar lecturas → POST `/readings`.
- Acuñar tokens → POST `/tokens/mint` y mostrar resultado.

## Consideraciones
- Al migrar, crear modulo Android dentro de `frontend/android` y actualizar `settings.gradle.kts`.
