package br.com.bix.images;

import java.nio.charset.StandardCharsets;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

public final class Fixture {

    private static final EasyRandom easyRandom;
    private static final EasyRandomParameters parameters;

    static {
        parameters = new EasyRandomParameters()
                .seed(123L)
                .objectPoolSize(100)
                .randomizationDepth(9)
                .charset(StandardCharsets.UTF_8)
                .stringLengthRange(0, 50)
                .collectionSizeRange(1, 5)
                .scanClasspathForConcreteTypes(true)
                .overrideDefaultInitialization(false)
                .ignoreRandomizationErrors(true);

        easyRandom = new EasyRandom(parameters);
    }

    public static <T> T make(final T mockClass) {
        return (T) easyRandom.nextObject(mockClass.getClass());
    }
}
