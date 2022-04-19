package Utils;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.NotAMockException;

import java.util.HashMap;
import java.util.Map;

public class Mocks {
    private static Map<String, MockedStatic> mocks = new HashMap<>();

    private static <T> MockedStatic<T> createStaticMock(Class<T> classToMock) {
        MockedStatic<T> tMockedStatic = Mockito.mockStatic(classToMock);
        mocks.put(classToMock.getName(), tMockedStatic);
        return tMockedStatic;
    }

    public static <T> MockedStatic<T> mockStatic(Class<T> classToMock) {
        MockedStatic<T> tMockedStatic = null;
        String className = classToMock.getName();
        if (mocks.containsKey(className)) {
            tMockedStatic = mocks.get(className);
            try {
                tMockedStatic.reset();
            } catch (NotAMockException e) {
                tMockedStatic = createStaticMock(classToMock);
            }
        } else {
            tMockedStatic = createStaticMock(classToMock);
        }
        return tMockedStatic;
    }

    public static void resetMocks() {
        mocks.forEach((name, mock) -> mock.close());
        mocks.clear();
    }
}
