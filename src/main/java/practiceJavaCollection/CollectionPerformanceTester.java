package practiceJavaCollection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class CollectionPerformanceTester {
    private ArrayList<Integer> arrayList;
    private LinkedList<Integer> linkedList;
    private ArrayList<Integer> arrayListFilled;
    private LinkedList<Integer> linkedListFilled;
    private long timeStartArrayList;
    private long arrayListDeltaTime;
    private long timeStartLinkedList;
    private long linkedListDeltaTime;
    int counter = 0;

    @BeforeEach
    void setUp() {
        arrayList = new ArrayList<>();
        linkedList = new LinkedList<>();
        arrayListFilled = new ArrayList<>();
        linkedListFilled = new LinkedList<>();


        for (int i = 0; i < 10000; i++) {
            arrayListFilled.add(i);
            linkedListFilled.add(i);
        }

        timeStartArrayList = System.currentTimeMillis();
    }

    @Test
    void testTimeAddtoEnd(){
        for (int i = 0; i < 10000; i++){
            arrayList.add(i);
        }

        arrayListDeltaTime = System.currentTimeMillis() - timeStartArrayList;

        timeStartLinkedList = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++){
            linkedList.add(i);
        }

        linkedListDeltaTime = System.currentTimeMillis() - timeStartLinkedList;

        System.out.println("Добавление в конец");
        System.out.printf("%s | %s%n",arrayListDeltaTime, linkedListDeltaTime);
        counter++;
    }

    @Test
    void testTimeAddtoStart(){
        for (int i = 0; i < 10000; i++){
            arrayList.add(0,i);
        }

        arrayListDeltaTime = System.currentTimeMillis() - timeStartArrayList;

        timeStartLinkedList = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++){
            linkedList.add(0,i);
        }

        linkedListDeltaTime = System.currentTimeMillis() - timeStartLinkedList;

        System.out.println("Добавление в начало");
        System.out.printf("%s | %s%n",arrayListDeltaTime, linkedListDeltaTime);
        counter++;
    }

    @Test
    void testTimeAddtoCenter(){
        for (int i = 0; i < 10000; i++){
            arrayList.add(arrayList.size() / 2, i);
        }

        arrayListDeltaTime = System.currentTimeMillis() - timeStartArrayList;

        timeStartLinkedList = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++){
            linkedList.add(linkedList.size() / 2, i);
        }

        linkedListDeltaTime = System.currentTimeMillis() - timeStartLinkedList;

        System.out.println("Вставка в середину");
        System.out.printf("%s | %s%n",arrayListDeltaTime, linkedListDeltaTime);
        counter++;
    }

    @Test
    void testTimeAccessByIndex(){
        for (int i = 0; i < 10000; i++){
            arrayListFilled.get(i);
        }

        arrayListDeltaTime = System.currentTimeMillis() - timeStartArrayList;

        timeStartLinkedList = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++){
            linkedListFilled.get(i);
        }

        linkedListDeltaTime = System.currentTimeMillis() - timeStartLinkedList;

        System.out.println("Доступ по индексу");
        System.out.printf("%s | %s%n",arrayListDeltaTime, linkedListDeltaTime);
    }

    @Test
    void testTimeRemoveFromStart(){
        for (int i = 0; i < 10000; i++){
            arrayListFilled.remove(0);
        }

        arrayListDeltaTime = System.currentTimeMillis() - timeStartArrayList;

        timeStartLinkedList = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++){
            linkedListFilled.remove(0);
        }

        linkedListDeltaTime = System.currentTimeMillis() - timeStartLinkedList;

        System.out.println("Удаление из начала");
        System.out.printf("%s | %s%n",arrayListDeltaTime, linkedListDeltaTime);
    }

    @Test
    void testTimeRemoveFromEnd(){
        for (int i = 0; i < 10000; i++){
            arrayListFilled.remove(10000-i-1);
        }

        arrayListDeltaTime = System.currentTimeMillis() - timeStartArrayList;

        timeStartLinkedList = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++){
            linkedListFilled.remove(10000-i-1);
        }

        linkedListDeltaTime = System.currentTimeMillis() - timeStartLinkedList;

        System.out.println("Удаление из конца");
        System.out.printf("%s | %s%n",arrayListDeltaTime, linkedListDeltaTime);
    }
}
