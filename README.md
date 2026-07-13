# Simulador-Concurrente-Parque
##  Descripción

Este proyecto es un **simulador de parque de diversiones** desarrollado en Java que modela el comportamiento de un parque con múltiples atracciones y un flujo continuo de visitantes. La simulación se basa en **programación concurrente y multihilo**, donde cada visitante es un hilo independiente que interactúa con recursos compartidos (las atracciones) de forma sincronizada.

El objetivo principal es resolver los desafíos clásicos de la concurrencia:
- **Exclusión mutua** para evitar condiciones de carrera.
- **Sincronización** entre hilos para coordinar el acceso a capacidades limitadas.
- **Prevención de interbloqueos** (deadlocks) mediante un diseño cuidadoso.
- **Temporización** para simular el paso del tiempo y forzar eventos (cierres, salidas por tiempo).

El sistema incluye una variedad de atracciones, cada una con su propia lógica de sincronización: Tren turístico, Teatro, Realidad Virtual, Premios, Comedor, Autos Chocadores, Montaña Rusa y Barco Pirata. Además, cuenta con un reloj interno que avanza el horario del parque y activa el cierre de atracciones y del parque en horarios predefinidos.

---

## Características Principales

- **Simulación multihilo**: cientos de visitantes (hilos) concurren en el parque.
- **Atracciones con lógica propia**:
  - *Tren Turístico*: capacidad limitada, temporizador de salida, maquinista.
  - *Teatro*: formación de grupos de 5, espera hasta llenar aforo o timeout.
  - *Realidad Virtual*: uso de equipos (gafas, manoplas, bases) como recursos compartidos.
  - *Premios*: intercambio de fichas por premios usando `Exchanger`.
  - *Comedor*: mesas para 4 comensales con `CyclicBarrier`.
  - *Montaña Rusa*, *Autos Chocadores*, *Barco Pirata*: espera hasta llenar capacidad o timeout.
- **Gestión del tiempo**:
  - Reloj interno que avanza cada 5 segundos (simulando 30 minutos reales).
  - Cierre del parque a las 18:00, cierre de atracciones a las 19:00 y cierre total a las 23:00.
- **Mecanismos de sincronización variados**:
  - `Semaphore` para control de aforo.
  - `ReentrantLock` + `Condition` para esperas condicionales.
  - `CyclicBarrier` para sincronización de grupos.
  - `Exchanger` para el intercambio de fichas/premios.
  - Métodos `synchronized` para secciones críticas.
- **Registro de eventos** en consola para seguir la simulación en tiempo real.

---

## ️ Tecnologías y Conceptos Aplicados

- **Lenguaje**: Java 17
- **Paradigma**: Programación Orientada a Objetos (POO)
- **Concurrencia**:
  - `java.util.concurrent`: `Semaphore`, `ReentrantLock`, `Condition`, `CyclicBarrier`, `Exchanger`, `BlockingQueue`, `ScheduledExecutorService`.
  - Hilos (`Thread`, `Runnable`).
- **Estructuras de datos**: `ArrayBlockingQueue` para colas de espera.
- **Patrones de diseño**: 
  - **Monitor** (sincronización implícita).
  - **Productor‑Consumidor** (en el Tren con colas).
  - **Exchanger** (Premios).
---

src/
├── parque/
│ ├── Parque.java # Clase principal que orquesta todo
│ ├── Visitante.java # Hilo que representa un visitante
│ ├── Mainn.java # Punto de entrada (main)
├── atracciones/
│ ├── Tren.java
│ ├── Teatro.java
│ ├── RealidadVirtual.java
│ ├── Premios.java
│ ├── Comedor.java
│ ├── AutosChocadores.java
│ ├── MontanaRusa.java
│ └── BarcoPirata.java
├── personal/
│ ├── Maquinista.java
| └── Encargado.java