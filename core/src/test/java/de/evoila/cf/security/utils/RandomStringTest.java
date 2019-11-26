package de.evoila.cf.security.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RandomStringTest {

    @Mock
    private Random mockRandom;

    @Test
    void onlyLowerCase() {
        when(mockRandom.nextInt(52))
                .thenAnswer(new Answer() {
                                int index = 0;
                                @Override
                                public Object answer(InvocationOnMock invocationOnMock) {
                                    if (index == 52) {
                                        index = 0;
                                    }
                                    return index++;
                                }
                            }
                );
        RandomString randomString = new RandomString(104, mockRandom, false,true);
        String string = randomString.nextString();
        assertEquals(string.length(), 104);
        assertEquals("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz", string);
    }

    @Test
    void lowerAndUpperCase() {
        when(mockRandom.nextInt(52))
                .thenAnswer(new Answer() {
                                int index = 0;
                                @Override
                                public Object answer(InvocationOnMock invocationOnMock) {
                                    if (index == 52) {
                                        index = 0;
                                    }
                                    return index++;
                                }
                            }
                );
        RandomString randomString = new RandomString(52, mockRandom, false,false);
        String string = randomString.nextString();
        assertEquals(string.length(), 52);
        assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", string);

    }

    @Test
    void lowerCaseAndNumbers() {
        when(mockRandom.nextInt(62))
                .thenAnswer(new Answer() {
                                int index = 0;
                                @Override
                                public Object answer(InvocationOnMock invocationOnMock) {
                                    if (index == 62) {
                                        index = 0;
                                    }
                                    return index++;
                                }
                            }
                );
        RandomString randomString = new RandomString(62, mockRandom, true,true);
        String string = randomString.nextString();
        assertEquals(string.length(), 62);
        assertEquals("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz0123456789", string);

    }

    @Test
    void lowerAndUpperCaseAndNumbers() {
        when(mockRandom.nextInt(62))
                .thenAnswer(new Answer() {
                                int index = 0;
                                @Override
                                public Object answer(InvocationOnMock invocationOnMock) {
                                    if (index == 62) {
                                        index = 0;
                                    }
                                    return index++;
                                }
                            }
                );
        RandomString randomString = new RandomString(62, mockRandom, true,false);
        String string = randomString.nextString();
        assertEquals(string.length(), 62);
        assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", string);

    }

    @Test
    void lengthTooSmall() {
        assertThrows(IllegalArgumentException.class, () -> new RandomString(0));
    }

    @Test
    void lengthNegative() {
        assertThrows(IllegalArgumentException.class, () -> new RandomString(-3));
    }

    @Test
    void randomNull() {
        assertThrows(NullPointerException.class, () -> new RandomString(12, null, false, false));
    }
}
