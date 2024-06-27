import java.util.HashMap;
import java.util.Scanner;

// Excepción personalizada para saldo de efectivo insuficiente
class SaldoEfectivoInsuficiente extends Exception {
    public SaldoEfectivoInsuficiente(String message) {
        super(message);
    }
}

// Excepción personalizada para saldo de cuenta insuficiente
class SaldoCuentaInsuficiente extends Exception {
    public SaldoCuentaInsuficiente(String message) {
        super(message);
    }
}

class Cuenta {
    private String numeroCuenta;
    private String contrasena;
    private double saldo;

    public Cuenta(String numeroCuenta, String contrasena, double saldoInicial) {
        this.numeroCuenta = numeroCuenta;
        this.contrasena = contrasena;
        this.saldo = saldoInicial;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public boolean autenticar(String contrasena) {
        return this.contrasena.equals(contrasena);
    }

    public double getSaldo() {
        return saldo;
    }

    public void depositar(double monto) {
        saldo += monto;
    }

    public void retirar(double monto) throws SaldoCuentaInsuficiente {
        if (monto > saldo) {
            throw new SaldoCuentaInsuficiente("Saldo insuficiente en la cuenta.");
        }
        saldo -= monto;
    }

    public void transferir(Cuenta destino, double monto) throws SaldoCuentaInsuficiente {
        retirar(monto);
        destino.depositar(monto);
    }
}

class CajeroAutomatico {
    private HashMap<String, Cuenta> cuentas;
    private double saldoEfectivo;

    public CajeroAutomatico(double saldoInicial) {
        this.saldoEfectivo = saldoInicial;
        this.cuentas = new HashMap<>();
    }

    public void agregarCuenta(Cuenta cuenta) {
        cuentas.put(cuenta.getNumeroCuenta(), cuenta);
    }

    public Cuenta autenticar(String numeroCuenta, String contrasena) {
        Cuenta cuenta = cuentas.get(numeroCuenta);
        if (cuenta != null && cuenta.autenticar(contrasena)) {
            return cuenta;
        }
        return null;
    }

    public void retirarEfectivo(Cuenta cuenta, double monto) throws SaldoEfectivoInsuficiente, SaldoCuentaInsuficiente {
        if (monto > saldoEfectivo) {
            throw new SaldoEfectivoInsuficiente("Saldo insuficiente en el cajero.");
        }
        cuenta.retirar(monto);
        saldoEfectivo -= monto;
    }

    public double getSaldoEfectivo() {
        return saldoEfectivo;
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CajeroAutomatico cajero = new CajeroAutomatico(100000);

        // Agregar cuentas de ejemplo
        cajero.agregarCuenta(new Cuenta("1234", "contrasena1", 5000));
        cajero.agregarCuenta(new Cuenta("5678", "contrasena2", 3000));

        System.out.print("Ingrese número de cuenta: ");
        String numeroCuenta = scanner.nextLine();
        System.out.print("Ingrese contraseña: ");
        String contrasena = scanner.nextLine();

        Cuenta cuenta = cajero.autenticar(numeroCuenta, contrasena);
        if (cuenta == null) {
            System.out.println("Autenticación fallida.");
            return;
        }

        System.out.println("Autenticación exitosa.");
        boolean salir = false;
        while (!salir) {
            System.out.println("\nSeleccione una opción:");
            System.out.println("1. Ver saldo");
            System.out.println("2. Depósito a cuenta propia");
            System.out.println("3. Depósito a otra cuenta");
            System.out.println("4. Transferencia a otra cuenta");
            System.out.println("5. Retiro de efectivo");
            System.out.println("6. Salir");

            int opcion = scanner.nextInt();
            switch (opcion) {
                case 1:
                    System.out.println("Saldo actual: $" + cuenta.getSaldo());
                    break;
                case 2:
                    System.out.print("Ingrese monto a depositar: ");
                    double montoDeposito = scanner.nextDouble();
                    cuenta.depositar(montoDeposito);
                    System.out.println("Depósito exitoso. Saldo actual: $" + cuenta.getSaldo());
                    break;
                case 3:
                    System.out.print("Ingrese número de cuenta destino: ");
                    scanner.nextLine(); // Limpiar buffer
                    String cuentaDestino = scanner.nextLine();
                    System.out.print("Ingrese monto a depositar: ");
                    double montoDepositoOtraCuenta = scanner.nextDouble();
                    Cuenta destino = cajero.autenticar(cuentaDestino, ""); // Autenticar solo por número de cuenta
                    if (destino != null) {
                        destino.depositar(montoDepositoOtraCuenta);
                        System.out.println("Depósito exitoso a la cuenta " + cuentaDestino + ". Saldo actual: $" + cuenta.getSaldo());
                    } else {
                        System.out.println("Cuenta destino no encontrada.");
                    }
                    break;
                case 4:
                    System.out.print("Ingrese número de cuenta destino: ");
                    scanner.nextLine(); // Limpiar buffer
                    String cuentaTransferencia = scanner.nextLine();
                    System.out.print("Ingrese monto a transferir: ");
                    double montoTransferencia = scanner.nextDouble();
                    Cuenta cuentaDestinoTransferencia = cajero.autenticar(cuentaTransferencia, ""); // Autenticar solo por número de cuenta
                    if (cuentaDestinoTransferencia != null) {
                        try {
                            cuenta.transferir(cuentaDestinoTransferencia, montoTransferencia);
                            System.out.println("Transferencia exitosa a la cuenta " + cuentaTransferencia + ". Saldo actual: $" + cuenta.getSaldo());
                        } catch (SaldoCuentaInsuficiente e) {
                            System.out.println(e.getMessage());
                        }
                    } else {
                        System.out.println("Cuenta destino no encontrada.");
                    }
                    break;
                case 5:
                    System.out.print("Ingrese monto a retirar: ");
                    double montoRetiro = scanner.nextDouble();
                    try {
                        cajero.retirarEfectivo(cuenta, montoRetiro);
                        System.out.println("Retiro exitoso. Saldo actual: $" + cuenta.getSaldo());
                    } catch (SaldoEfectivoInsuficiente | SaldoCuentaInsuficiente e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 6:
                    salir = true;
                    System.out.println("Gracias por usar el cajero automático.");
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
        scanner.close();
    }
}
