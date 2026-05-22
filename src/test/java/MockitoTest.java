import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

class MockitoTest {

    @Test
    void testMock() {
        Runnable runnable = mock(Runnable.class);
    }
}