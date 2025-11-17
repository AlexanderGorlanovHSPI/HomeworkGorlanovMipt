package practiceJavaCollection;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Кастомная реализация динамического массива (аналог ArrayList)
 *
 * @param <A> тип элементов в списке
 */
public class CustomArrayList<A> implements CustomList<A> {

    /**
     * Внутренний массив для хранения элементов
     */
    private Object[] elements;

    /**
     * Коэффициент расширения массива
     */
    private static final double EXPANSION_FACTOR = 1.5;

    /**
     * Начальная емкость по умолчанию
     */
    private static final int DEFAULT_CAPACITY = 10;

    /**
     * Текущее количество элементов в списке
     */
    private int size;

    /**
     * Создает CustomArrayList с начальной емкостью по умолчанию
     */
    public CustomArrayList(){
        this.elements = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }

    /**
     * Создает CustomArrayList с указанной начальной емкостью
     *
     * @param initialCapacity начальная емкость списка
     * @throws IllegalArgumentException если initialCapacity меньше или равно 0
     */
    public CustomArrayList(int initialCapacity){
        if (initialCapacity <= 0){
            throw new IllegalArgumentException("Недопустимый размер: " + initialCapacity);
        }
        this.elements = new Object[initialCapacity];
        this.size = 0;
    }

    /**
     * Проверяет и при необходимости увеличивает емкость внутреннего массива
     */
    private void ensureCapacity(){
        if (size == elements.length){
            int newCapacity = (int) (elements.length * EXPANSION_FACTOR);
            Object[] extendedElements = new Object[newCapacity];
            System.arraycopy(elements,0,extendedElements,0,size);
            elements = extendedElements;
        }
    }

    /**
     * Проверяет валидность индекса
     *
     * @param index индекс для проверки
     * @throws IndexOutOfBoundsException если индекс невалиден
     */
    private void checkIndex(int index){
        if (index < 0 || index >= size){
            throw new IndexOutOfBoundsException("Индекс: " + index + ", Размер: " + size);
        }
    }

    /**
     * Внутренний класс итератора для CustomArrayList
     */
    private class CustomArrayListIterator implements Iterator<A>{

        /**
         * Текущая позиция итератора
         */
        private int currentIndex = 0;

        /**
         * Проверяет, есть ли следующий элемент
         *
         * @return true если есть следующий элемент, false в противном случае
         */
        @Override
        public boolean hasNext(){
            return currentIndex < size;
        }

        /**
         * Возвращает следующий элемент и перемещает итератор
         *
         * @return следующий элемент
         * @throws NoSuchElementException если больше нет элементов
         */
        @Override
        @SuppressWarnings("unchecked")
        public A next(){
            if (!hasNext()){
                throw new NoSuchElementException("Больше нет элементов");
            }

            return (A)elements[currentIndex++];
        }
    }

    /**
     * Добавляет элемент в конец списка. При необходимости увеличивает емкость массива
     *
     * @param element элемент для добавления
     * @throws IllegalArgumentException если передан null
     */
    @Override
    public void add(A element){
        if (element == null){
            throw new IllegalArgumentException("Элемент не может быть null");
        }
        ensureCapacity();
        elements[size] = element;
        size++;
    }

    /**
     * Получает элемент по указанному индексу
     *
     * @param index индекс элемента
     * @return элемент по указанному индексу
     * @throws IndexOutOfBoundsException если индекс выходит за границы списка
     */
    @Override
    @SuppressWarnings("unchecked")
    public A get(int index){
        checkIndex(index);
        return (A) elements[index];
    }

    /**
     * Удаляет элемент по указанному индексу и сдвигает последующие элементы влево
     *
     * @param index индекс элемента для удаления
     * @return удаленный элемент
     * @throws IndexOutOfBoundsException если индекс выходит за границы списка
     */
    @Override
    @SuppressWarnings("unchecked")
    public A remove(int index){
        checkIndex(index);

        A removedElement = (A) elements[index];

        // Сдвигаем элементы влево
        for (int i = index; i < size - 1; i++){
            elements[i] = elements[i+1];
        }
        // Очищаем последний элемент и уменьшаем размер
        elements[size-1] = null;
        size--;

        return removedElement;
    }

    /**
     * Возвращает количество элементов в списке
     *
     * @return количество элементов в списке
     */
    @Override
    public int size(){
        return size;
    }

    /**
     * Проверяет, пуст ли список
     *
     * @return true если список пуст, false в противном случае
     */
    @Override
    public boolean isEmpty(){
        return size == 0;
    }

    /**
     * Возвращает итератор для последовательного обхода элементов списка
     *
     * @return итератор для списка
     */
    @Override
    public Iterator<A> iterator(){
        return new CustomArrayListIterator();
    }
}
