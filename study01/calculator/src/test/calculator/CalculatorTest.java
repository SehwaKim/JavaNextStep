package calculator;

import nextstep.calculator.Calculator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CalculatorTest {
    private Calculator cal;

    @Before
    public void setUp() {
        cal = new Calculator();
        System.out.println("before!!");
    }

    @Test
    public void add() {
        assertEquals(cal.add(3,4), 7);
        System.out.println("add");
    }

    @Test
    public void subtract() {
        assertEquals(cal.subtract(3,4), -1);
        System.out.println("subtract");
    }

    @After
    public void teardown() {
        System.out.println("Tear Down!!");
    }
}
