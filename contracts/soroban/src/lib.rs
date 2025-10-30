#![no_std]
use soroban_sdk::{contract, contractimpl, symbol_short, Address, Env, Symbol, Vec};

const ADMIN_KEY: Symbol = symbol_short!("ADMIN");

#[contract]
pub struct CarbonToken;

#[contractimpl]
impl CarbonToken {
    pub fn init(env: Env, admin: Address) {
        if env.storage().instance().has(&ADMIN_KEY) {
            panic!("already initialized");
        }
        admin.require_auth();
        env.storage().instance().set(&ADMIN_KEY, &admin);
    }

    pub fn admin(env: Env) -> Address {
        env.storage().instance().get(&ADMIN_KEY).expect("not initialized")
    }

    // Simplificado: emite evento de mint autorizado por admin.
    // En una integracion real, este contrato llamaria a un contrato de token estandar
    // pasando `to` y `amount`. Aqui solo registramos el evento para el backend.
    pub fn mint(env: Env, to: Address, amount: i128) {
        let admin: Address = Self::admin(env.clone());
        admin.require_auth();
        if amount <= 0 {
            panic!("amount must be positive");
        }
        // Emitir evento: ("mint", to, amount)
        let mut topics: Vec<Symbol> = Vec::new(&env);
        topics.push_back(symbol_short!("mint"));
        env.events().publish((topics,), (to, amount));
    }
}
