package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private BigDecimal saldo = new BigDecimal(0);  //Code Smell - Type BigDecimal
  private List<Movimiento> movimientos = new ArrayList<>();

  //public Cuenta() {saldo = 0; } //Code Smell - Esto no hace falta, ya que se setea por default el saldo a 0, ademas que tenemos dos constructores

  public Cuenta(BigDecimal montoInicial, List<Movimiento> movimientos) { //Code Smell - montoInicial type BigDecimal
    this.saldo = montoInicial;
    this.movimientos = movimientos;
  }

  //public void setMovimientos(List<Movimiento> movimientos) {this.movimientos = movimientos;}

  public void poner(BigDecimal cuanto) {
    if ((cuanto.compareTo(new BigDecimal(0)) <= 0)) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }

    if (getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }

    new Movimiento(LocalDate.now(), cuanto, true).agregateA(this);
  }

  public void sacar(BigDecimal cuanto) {
    if (cuanto.compareTo(new BigDecimal(0)) <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
    if ((getSaldo().subtract(cuanto)).compareTo(new BigDecimal(0)) < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
    BigDecimal montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    BigDecimal limite = (new BigDecimal(1000)).subtract(montoExtraidoHoy);
    if (cuanto.compareTo(limite) > 0) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
              + " diarios, límite: " + limite);
    }
    new Movimiento(LocalDate.now(), cuanto, false).agregateA(this);
  }

  public void agregarMovimiento(LocalDate fecha, BigDecimal cuanto, boolean esDeposito) {
    Movimiento movimiento = new Movimiento(fecha, cuanto, esDeposito);
    movimientos.add(movimiento);
  }

  public BigDecimal getMontoExtraidoA(LocalDate fecha) {
    //BigDecimal sum = BigDecimal.ZERO;
    return getMovimientos().stream()
            .filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(fecha))
            .map(movimiento -> movimiento.getMonto()) //flatmap anteriormente
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    //.sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public BigDecimal getSaldo() {
    return saldo;
  }

  public void setSaldo(BigDecimal saldo) {
    this.saldo = saldo;
  }

}