package practiceJavaCollection;

import java.util.Iterator;
/**
 * Интерфейс для кастомного списка с базовыми операциями
 *
 * @param <A> тип элементов в списке
 */
public interface CustomList<A> extends Iterable<A>{
    /**
     * Добавляет элемент в конец списка
     *
     * @param element элемент для добавления
     * @throws IllegalArgumentException если передан null
     */
    void add(A element);

    /**
     * Получает элемент по указанному индексу
     *
     * @param index индекс элемента
     * @return элемент по указанному индексу
     * @throws IndexOutOfBoundsException если индекс выходит за границы списка
     */
    A get(int index);

    /**
     * Удаляет элемент по указанному индексу
     *
     * @param index индекс элемента для удаления
     * @return удаленный элемент
     * @throws IndexOutOfBoundsException если индекс выходит за границы списка
     */
    A remove(int index);

    /**
     * Возвращает количество элементов в списке
     *
     * @return количество элементов в списке
     */
    int size();

    /**
     * Проверяет, пуст ли список
     *
     * @return true если список пуст, false в противном случае
     */
    boolean isEmpty();

    /**
     * Возвращает итератор для последовательного обхода элементов списка
     *
     * @return итератор для списка
     */
    @Override
    Iterator<A> iterator();
}
