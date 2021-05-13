package dds.monedero.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Movimiento {
  private LocalDate fecha;
  //En ningún lenguaje de programación usen jamás doubles para modelar dinero en el mundo real
  //siempre usen numeros de precision arbitraria, como BigDecimal en Java y similares
  private BigDecimal monto; //Code Smell - el tipo debe ser BigDecimal, jamás Double
  private boolean esDeposito;

  public Movimiento(LocalDate fecha, BigDecimal monto, boolean esDeposito) {
    this.fecha = fecha;
    this.monto = monto;
    this.esDeposito = esDeposito;
  }

  public BigDecimal getMonto() {
    return monto;
  }

  public LocalDate getFecha() {
    return fecha;
  }

  public boolean fueDepositado(LocalDate fecha) {
    return esDeposito && esDeLaFecha(fecha);
  } //Code Smell - no hace falta usar isDeposito() en la clase ya que tengo acceso a el atributo this.esDeposito

  public boolean fueExtraido(LocalDate fecha) {
    return !esDeposito && esDeLaFecha(fecha);
  } //Code Smell - No hace falta utilizar el isExtraccion() tranquilamente podriamos ponerle !this.esDeposito

  public boolean esDeLaFecha(LocalDate fecha) {
    return this.fecha.equals(fecha);
  }

  public boolean isDeposito() {
    return esDeposito;
  }

  //public boolean isExtraccion() { return !esDeposito; } // No se utiliza en ningun metodo fuera de la clase, podemos comentarlo

  public void agregateA(Cuenta cuenta) {
    cuenta.setSaldo(calcularValor(cuenta));
    cuenta.agregarMovimiento(fecha, monto, esDeposito);
  }

  public BigDecimal calcularValor(Cuenta cuenta) { //Code Smell - Devuelve un BigDecimal con el cambio que hicimos arriba
    if (esDeposito) {
      return (cuenta.getSaldo()).add(getMonto());
    } else {
      return (cuenta.getSaldo()).subtract(getMonto());
    }
  }
}
