package jenkins.internal.enumeration;

import junit.framework.TestCase;

public class DescriptionSourceTest extends TestCase {

    public void testGetDisplayString() {
        assertEquals("Beschreibung" , DescriptionSource.BESCHREIBUNG.getDisplayString());
    }
}