package calculator;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SplitTest {
    @Test
    public void split() {
        assertThat("1".split(",")).isEqualTo(new String[]{"1"});
        assertThat("1,2".split(",")).isEqualTo(new String[]{"1", "2"});
    }
}
