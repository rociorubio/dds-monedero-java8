package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.*;

public class MonederoTest {
  private Cuenta cuenta;

  @BeforeEach
  void init() {
    List movimientos = new ArrayList<>();
    movimientos.add(new Movimiento(LocalDate.now(), new BigDecimal(500),true));
    movimientos.add(new Movimiento( LocalDate.parse("2020-10-30"), new BigDecimal(1550),false));
    cuenta = new Cuenta(new BigDecimal(1800.50),movimientos);
  }

  @Test
  void Poner() {
    cuenta.poner(new BigDecimal(1500));
  }

  @Test
  void PonerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.poner(new BigDecimal(-1500)));
  }

  @Test
  void TresDepositos() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
    cuenta.poner(new BigDecimal(1500));
    cuenta.poner(new BigDecimal(456));
    cuenta.poner(new BigDecimal(1900));
  });
  }

  @Test
  void MasDeTresDepositos() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
      cuenta.poner(new BigDecimal(1500));
      cuenta.poner(new BigDecimal(456));
      cuenta.poner(new BigDecimal(1900));
      cuenta.poner(new BigDecimal(245));
    });
  }

  @Test
  void ExtraerMasQueElSaldo() {
    assertThrows(SaldoMenorException.class, () -> {
      cuenta.setSaldo(new BigDecimal(90));
      cuenta.sacar(new BigDecimal(1001));
    });
  }

  @Test
  public void ExtraerMasDe1000() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(new BigDecimal(5000));
      cuenta.sacar(new BigDecimal(1001));
    });
  }

  @Test
  public void ExtraerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.sacar(new BigDecimal(-500)));
  }

  @Test
  public void GetSaldo() {
    assertEquals(new BigDecimal(1800.50), cuenta.getSaldo());
  }

  @Test
  public void SetSaldo() {
    cuenta.setSaldo(new BigDecimal(200));
    assertEquals(new BigDecimal(200), cuenta.getSaldo());
  }

  @Test
  public void getMontoExtraidoA() {
    cuenta.agregarMovimiento(LocalDate.parse("2021-01-01"), new BigDecimal(250), false);
    assertEquals(cuenta.getMontoExtraidoA(LocalDate.parse("2021-01-01")), new BigDecimal(250));
    assertEquals(cuenta.getMontoExtraidoA(LocalDate.parse("2020-10-30")), new BigDecimal(1550));
  }

  @Test
  public void fueExtraido() {
    Movimiento extraccion = new Movimiento( LocalDate.parse("2020-10-30"), new BigDecimal(1550),false);
    assertTrue(extraccion.fueExtraido(LocalDate.parse("2020-10-30")));
  }
}