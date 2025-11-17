package practiceJavaCollection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

class CustomArrayListTest {

    private CustomList<String> list;
    private CustomList<Integer> intList;

    @BeforeEach
    void setUp() {
        list = new CustomArrayList<>();
        intList = new CustomArrayList<>();
    }

    @Test
    void testAdd() {
        list.add("first");
        list.add("second");
        list.add("third");

        assertEquals(3, list.size());
        assertEquals("first", list.get(0));
        assertEquals("second", list.get(1));
        assertEquals("third", list.get(2));
    }

    @Test
    void testAddNullThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> list.add(null)
        );
        assertEquals("Элемент не может быть null", exception.getMessage());
    }

    @Test
    void testDynamicExpansion() {
        for (int i = 0; i < 20; i++) {
            intList.add(i);
        }

        assertEquals(20, intList.size());
        assertEquals(0, intList.get(0));
        assertEquals(19, intList.get(19));
    }

    @Test
    void testGet() {
        list.add("element1");
        list.add("element2");

        assertEquals("element1", list.get(0));
        assertEquals("element2", list.get(1));
    }

    @Test
    void testGetWithInvalidIndex() {
        list.add("element");

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(list.size()));
    }

    @Test
    void testRemove() {
        list.add("first");
        list.add("second");
        list.add("third");

        String removed = list.remove(1);

        assertEquals("second", removed);
        assertEquals(2, list.size());
        assertEquals("first", list.get(0));
        assertEquals("third", list.get(1));

        removed = list.remove(0);
        assertEquals("first", removed);
        assertEquals(1, list.size());
        assertEquals("third", list.get(0));

        removed = list.remove(0);
        assertEquals("third", removed);
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
    }

    @Test
    void testRemoveWithInvalidIndex() {
        list.add("element");

        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1));
    }

    @Test
    void testShiftAfterRemove() {
        for (int i = 0; i < 5; i++) {
            intList.add(i);
        }

        intList.remove(2); // Удаляем 2

        assertEquals(4, intList.size());
        assertEquals(0, intList.get(0));
        assertEquals(1, intList.get(1));
        assertEquals(3, intList.get(2));
        assertEquals(4, intList.get(3));
    }

    @Test
    void testSize() {
        assertEquals(0, list.size());

        list.add("element");
        assertEquals(1, list.size());

        list.add("another");
        assertEquals(2, list.size());

        list.remove(0);
        assertEquals(1, list.size());
    }

    @Test
    void testIsEmpty() {
        assertTrue(list.isEmpty());

        list.add("element");
        assertFalse(list.isEmpty());

        list.remove(0);
        assertTrue(list.isEmpty());
    }

    @Test
    void testIterator() {
        list.add("first");
        list.add("second");
        list.add("third");

        Iterator<String> iterator = list.iterator();

        assertTrue(iterator.hasNext());
        assertEquals("first", iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals("second", iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals("third", iterator.next());

        assertFalse(iterator.hasNext());
    }

    @Test
    void testIteratorOnEmptyList() {
        Iterator<String> iterator = list.iterator();
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void testIteratorRemoveThrowsException() {
        list.add("element");
        Iterator<String> iterator = list.iterator();
        iterator.next();

        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    void testConstructorWithInitialCapacity() {
        CustomList<Integer> customList = new CustomArrayList<>(5);
        assertTrue(customList.isEmpty());

        for (int i = 0; i < 5; i++) {
            customList.add(i);
        }
        assertEquals(5, customList.size());
    }

    @Test
    void testConstructorWithInvalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new CustomArrayList<>(0));
        assertThrows(IllegalArgumentException.class, () -> new CustomArrayList<>(-1));
    }

    @Test
    void testComplexScenario() {
        for (int i = 0; i < 10; i++) {
            intList.add(i * 10);
        }

        assertEquals(10, intList.size());
        assertFalse(intList.isEmpty());

        assertEquals(30, intList.remove(3));
        assertEquals(70, intList.remove(6));

        assertEquals(8, intList.size());

        assertEquals(0, intList.get(0));
        assertEquals(10, intList.get(1));
        assertEquals(20, intList.get(2));
        assertEquals(40, intList.get(3));
        assertEquals(80, intList.get(6));

        intList.add(100);
        intList.add(110);

        assertEquals(10, intList.size());
        assertEquals(110, intList.get(9));

        int sum = 0;
        Iterator<Integer> iterator = intList.iterator();
        while (iterator.hasNext()) {
            sum += iterator.next();
        }

        assertTrue(sum > 0);
    }
}
