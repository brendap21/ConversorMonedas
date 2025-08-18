import java.io.IOException;
import java.util.*;

public class Conversor {

    private static final String API_KEY = resolveApiKey();

    private static String resolveApiKey() {
        // 1) Variable de entorno
        String k = System.getenv("EXCHANGE_API_KEY");
        if (k != null && !k.isBlank()) return k;

        // 2) Propiedad de sistema: -DEXCHANGE_API_KEY=xxx
        k = System.getProperty("EXCHANGE_API_KEY");
        if (k != null && !k.isBlank()) return k;

        // 3) Archivo local (NO versionado): config.properties
        try (java.io.InputStream in = new java.io.FileInputStream("config.properties")) {
            java.util.Properties p = new java.util.Properties();
            p.load(in);
            k = p.getProperty("EXCHANGE_API_KEY");
            if (k != null && !k.isBlank()) return k;
        } catch (java.io.IOException ignored) {}

        throw new IllegalStateException(
                "Falta EXCHANGE_API_KEY. Configúrala como variable de entorno, " +
                        "propiedad del sistema o en config.properties (no lo subas al repo)."
        );
    }

    private static final String API_BASE = "https://v6.exchangerate-api.com/v6/";

    // Monedas soportadas (filtradas)
    private static final List<String> MONEDAS =
            Arrays.asList("USD", "ARS", "BOB", "BRL", "CLP", "COP", "MXN");

    // Nombres para mostrar en el menú
    private static final Map<String, String> NOMBRES = new HashMap<>();
    static {
        NOMBRES.put("USD", "United States Dollar");
        NOMBRES.put("ARS", "Argentine Peso");
        NOMBRES.put("BOB", "Bolivian Boliviano");
        NOMBRES.put("BRL", "Brazilian Real");
        NOMBRES.put("CLP", "Chilean Peso");
        NOMBRES.put("COP", "Colombian Peso");
        NOMBRES.put("MXN", "Mexican Peso");
    }

    public static void eleccionUserMenu() throws IOException, InterruptedException {
        Scanner sc = new Scanner(System.in);
        sc.useLocale(Locale.US); // decimales con punto

        while (true) {
            mostrarMenu();
            String base = pedirMoneda(sc, "Elige moneda BASE");
            String target = pedirMonedaDiferente(sc, "Elige moneda DESTINO", base);
            double monto = pedirMonto(sc, "Monto en " + base + ": ");

            // 1) Obtener tasa (método modular)
            double tasa = obtenerTasa(base, target);

            if (tasa <= 0.0) {
                System.out.println("No se pudo obtener la tasa. Intenta de nuevo.");
                if (!deseaContinuar(sc)) break;
                continue;
            }

            // 2) Convertir (método modular)
            double convertido = convertir(monto, tasa);

            // 3) Mostrar resultado (método modular)
            imprimirResultado(base, target, monto, tasa, convertido);

            if (!deseaContinuar(sc)) {
                System.out.println("¡Listo! Gracias por usar el conversor.");
                break;
            }
        }
    }

    // ----------------- LÓGICA DE CONVERSIÓN (modular) -----------------

    /** Obtiene la tasa base->target. Intenta vía /pair y cae a /latest si falla. */
    private static double obtenerTasa(String base, String target) throws IOException, InterruptedException {
        // Intento 1: endpoint /pair
        String urlPair = API_BASE + API_KEY + "/pair/" + base + "/" + target;
        double rate = ConversorApp.obtenerTasa(urlPair);
        if (rate > 0.0) return rate;

        // Intento 2: endpoint /latest (filtrado por target)
        String urlLatest = API_BASE + API_KEY + "/latest/" + base;
        Map<String, Double> tasas = ConversorApp.obtenerTasasLatest(urlLatest);
        if (tasas != null && tasas.containsKey(target)) {
            return tasas.get(target);
        }
        return 0.0;
    }

    /** Aplica la tasa al monto. */
    private static double convertir(double monto, double tasa) {
        return monto * tasa;
    }

    /** Redondea a n decimales. */
    private static double redondear(double valor, int decimales) {
        double factor = Math.pow(10, decimales);
        return Math.round(valor * factor) / factor;
    }

    /** Imprime un resumen bonito de la conversión. */
    private static void imprimirResultado(String base, String target, double monto, double tasa, double convertido) {
        System.out.printf(Locale.US,
                "%nBase: %s (%s)  ->  Destino: %s (%s)%n",
                base, NOMBRES.get(base), target, NOMBRES.get(target));
        System.out.printf(Locale.US, "Tasa %s→%s: %.6f%n", base, target, tasa);
        System.out.printf(Locale.US, "%.2f %s = %.2f %s%n",
                redondear(monto, 2), base, redondear(convertido, 2), target);
    }

    // ----------------- HELPERS DE ENTRADA -----------------

    private static void mostrarMenu() {
        System.out.println("\n=== Conversor de Monedas ===");
        System.out.println("Monedas disponibles:");
        for (int i = 0; i < MONEDAS.size(); i++) {
            String c = MONEDAS.get(i);
            System.out.printf("  %d) %s - %s%n", i + 1, c, NOMBRES.get(c));
        }
    }

    private static String pedirMoneda(Scanner sc, String titulo) {
        while (true) {
            System.out.println("\n" + titulo + ":");
            System.out.print("Opción: ");
            if (sc.hasNextInt()) {
                int op = sc.nextInt();
                if (op >= 1 && op <= MONEDAS.size()) return MONEDAS.get(op - 1);
            } else {
                sc.next(); // limpiar token inválido
            }
            System.out.println("Opción inválida. Intenta de nuevo.");
        }
    }

    private static String pedirMonedaDiferente(Scanner sc, String titulo, String distintaDe) {
        while (true) {
            String m = pedirMoneda(sc, titulo);
            if (!m.equals(distintaDe)) return m;
            System.out.println("La moneda destino debe ser distinta a la base.");
        }
    }

    private static double pedirMonto(Scanner sc, String prompt) {
        System.out.print(prompt);
        while (!sc.hasNextDouble()) {
            sc.next();
            System.out.print("Ingresa un número válido (usa punto . para decimales): ");
        }
        return sc.nextDouble();
    }

    private static boolean deseaContinuar(Scanner sc) {
        System.out.print("\n¿Deseas otra conversión? (s/n): ");
        String otra = sc.next().trim().toLowerCase(Locale.ROOT);
        return otra.equals("s");
    }
}