CONVERSOR DE MONEDAS (Java - Consola)

Proyecto del reto ONE (Alura | Oracle).
App de consola que consulta ExchangeRate-API y convierte montos entre monedas seleccionadas.

🧩 ¿Qué hace?

Menú en consola para elegir moneda base y moneda destino.
Pide un monto y calcula el resultado usando tasas en tiempo real
Monedas soportadas en este reto: USD, ARS, BOB, BRL, CLP, COP, MXN
Manejo básico de errores (códigos HTTP ≠ 200, timeouts, respuesta inválida)

🛠️ Tecnologías:
Java 24.0.1 + (uso de java.net.http.HttpClient)
Gson (parseo de JSON a objetos Java / navegación por JSON)

🗂️ Estructura del proyecto:
ConversorMonedas/
├── README.md
├── src/
│   ├── src.iml
│   └── src/
│       ├── Conversor.java            // Menú + lógica de conversión (Paso 9 modularizado)
│       ├── ConversorApp.java         // Requests HTTP + parseo con Gson
│       ├── TasaResponse.java         // POJO para /pair
│       ├── TasasLatestResponse.java  // POJO para /latest
│       └── Main.java                 // Stub (no se usa)

✅ Requisitos

Java 11 o superior
Gson (por ejemplo gson-2.10.x.jar)
Conexión a internet

▶️ Compilar y ejecutar:
Ubícate en ConversorMonedas/src/src:

    Windows:
    javac -cp ".;C:\ruta\a\gson-2.10.1.jar" *.java
    java  -cp ".;C:\ruta\a\gson-2.10.1.jar" ConversorApp

    Linux / macOS:
    javac -cp ".:/ruta/a/gson-2.10.1.jar" *.java
    java  -cp ".:/ruta/a/gson-2.10.1.jar" ConversorApp

    Ajusta la ruta del JAR de Gson según dónde lo tengas.

🕹️ Uso:

Elige moneda base y moneda destino del listado (USD, ARS, BOB, BRL, CLP, COP, MXN).
Ingresa el monto.
La app intenta /pair y, si falla, usa /latest y filtra la tasa.
Se imprime la tasa y el resultado redondeado.

    Ejemplo de salida:

        Base: USD (United States Dollar)  ->  Destino: MXN (Mexican Peso)
        Tasa USD→MXN: 18.729800
        100.00 USD = 1872.98 MXN

🧪 Pruebas rápidas:

    Postman (opcional):

        GET /pair/USD/MXN → debe regresar result: "success" y conversion_rate

        GET /latest/USD → debe traer conversion_rates.MXN

    Consola: probar varios pares (USD↔MXN, MXN↔COP, etc.) y montos no numéricos (valida reintento)


Desarrollado por: Brenda Paola Navarro Alatorre.
