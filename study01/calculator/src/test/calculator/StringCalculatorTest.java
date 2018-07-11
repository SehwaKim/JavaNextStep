package calculator;

import nextstep.calculator.StringCalculator;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StringCalculatorTest {
    private StringCalculator stringCalculator;

    @Before
    public void setUp() {
        stringCalculator = new StringCalculator();
    }

    @Test
    public void add_null_또는_빈문자() {
        assertThat(stringCalculator.add(null)).isEqualTo(0);
        assertThat(stringCalculator.add("")).isEqualTo(0);
    }

    @Test
    public void add_숫자하나() {
        assertThat(stringCalculator.add("100")).isEqualTo(100);
    }

    @Test
    public void add_쉼표_또는_콜론_구분자() {
        assertThat(stringCalculator.add("100,2:3")).isEqualTo(105);
    }

    @Test
    public void add_custom_구분자() {
        assertThat(stringCalculator.add("//@\n100@2@3")).isEqualTo(105);
    }

    @Test(expected = RuntimeException.class)
    public void add_negative() {
        stringCalculator.add("-1:0:3");
        stringCalculator.add("//@\n1@2@3@-9");
    }
}
