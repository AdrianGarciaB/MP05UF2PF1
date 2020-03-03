package ex2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HashTableTest {

    private HashTable defaultCase(){
        HashTable defaultHashTable = new HashTable();
        defaultHashTable.put("1233", "First Element");
        defaultHashTable.put("12310", "First Colision");
        defaultHashTable.put("1", "Second Colision");
        defaultHashTable.put("1234", "Second Element");
        System.out.println(defaultHashTable);
        return defaultHashTable;
    }

    /*
    Casuisticas:
        1. Añadir un elemento y comprobar que este añadido
        2. Añadimos un elemento ya existente, si este ya existe unicamente actualizara su valor
        3. Añadimos un elemento con una clave que colisiona con el del caso 1.
        4. Actualizamos el valor de una clave colisionada.
     */
    @org.junit.jupiter.api.Test
    void put() {
        HashTable hashTable = new HashTable();

        hashTable.put("1234", "Hello");
        assertEquals(1, hashTable.size());
        System.out.println(hashTable);

        // Si introducimos el mismo indice, el valor deberia modificarse.
        hashTable.put("1234", "World");
        assertEquals("World", hashTable.get("1234"));
        assertEquals("\n bucket[2] = [1234, World]", hashTable.toString());

        // Forzamos una colision para comprobar si un caso con el mismo indice se actualizaria.
        // Claves que colisionan: ["1233", "12310"]
        hashTable.put("1233", "Hello");
        hashTable.put("12310", "Hello1");
        hashTable.put("12310", "Hello2");

        hashTable.put("1", "1");
        hashTable.put("12", "12");

        assertEquals("Hello2", hashTable.get("12310"));
        assertEquals(
                "\n bucket[1] = [1233, Hello] -> [12310, Hello2] -> [1, 1] -> [12, 12]" +
                "\n bucket[2] = [1234, World]", hashTable.toString()
        );


        //Probamos de modificar un valor que se encuentra en el medio
        hashTable.put("1", "Uno");
        assertEquals("Uno", hashTable.get("1"));


    }

    /*
    Casuisticas:
        1. Intentamos obtener un elemento que no existe.
        2. Intentamos obtener un elemento que no existe de la LinkedList.
        3. Obtenemos un elemento que existe.
        4. Obtenemos un elemento que existe de la LinkedList.
        5. Obtenemos el ultimo elemento que existe de la LinkedList
        5. Pedimos una clave muy alta.

        bucket[0] = [0, null] -> [11, null] -> [22, null]                                      Clave vacia.
        bucket[1] = [1233, First Element] -> [12310, First Colision] -> [1, Second Colision] -> [23, null]
        bucket[2] = [1234, Second Element]  -> [2, null] -> [13, null] -> [24, null]
     */
    @org.junit.jupiter.api.Test
    void get() {
        HashTable hashTable = defaultCase();
        // Pedimos un elemento que no exista
        assertNull(hashTable.get("0"));

        // Pedimos un elemento que no exista en la LinkedList
        assertNull(hashTable.get("23"));

        // Pedimos un elemento que existe
        assertEquals("First Element", hashTable.get("1233"));

        // Pedimos un elemento colisionado que existe en la LinkedList
        assertEquals("First Colision", hashTable.get("12310"));

        // Pedimos el ultimo elemento colisionado que existe en la LinkedList
        assertEquals("Second Colision", hashTable.get("1"));

        // Pedimos una clave que el resultado de su hash al aplicarle el modulo, su posicion supera al tamaño de la array.
        hashTable.get("12345634224324319");
    }

    /*
    Casuisticas:
        1. Comprobamos el tamaño de la tabla cuando borramos un elemento que no existe.
        2. Borramos el primer elemento de la tabla con elementos delante.
        3. Borramos un elemento del medio de la LinkedList
        4. Borramos el ultimo elemento de la LinkedList.
        5. Borramos un elemento de la LinkedList que colisiona pero no existe.

        bucket[0] = [0, null] -> [11, null] -> [22, null]                                      Clave vacia.
        bucket[1] = [1233, First Element] -> [12310, First Colision] -> [1, Second Colision] -> [23, null]
        bucket[2] = [1234, Second Element]  -> [2, null] -> [13, null] -> [24, null]
     */
    @org.junit.jupiter.api.Test
    void drop() {
        HashTable hashTable = defaultCase();
        // Comprobamos que el tamaño no quedara en negativo en caso de eliminar un elemento que
        // nunca existio.
        hashTable.drop("Clave que no existe");
        assertEquals(4, hashTable.size());
        assertEquals(
                "\n bucket[1] = [1233, First Element] -> [12310, First Colision] -> [1, Second Colision]" +
                        "\n bucket[2] = [1234, Second Element]", hashTable.toString()
        );

        // Borramos el primer elemento de la tabla
        hashTable.drop("1233");
        assertEquals(3, hashTable.size());
        assertEquals(
                "\n bucket[1] = [12310, First Colision] -> [1, Second Colision]" +
                        "\n bucket[2] = [1234, Second Element]", hashTable.toString()
        );
        // Volvemos al caso base
        hashTable = defaultCase();
        // Borramos un elemento entre el primer elemento y el ultimo de la tabla
        hashTable.drop("12310");
        assertEquals(3, hashTable.size());
        assertEquals(
                "\n bucket[1] = [1233, First Element] -> [1, Second Colision]" +
                        "\n bucket[2] = [1234, Second Element]", hashTable.toString()
        );

        //region Borramos un elemento del medio de la LinkedList
        // Volvemos al caso base
        hashTable = defaultCase();
        // Borramos el ultimo elemento de la tabla colisionada.
        hashTable.drop("1");
        assertEquals(3, hashTable.size());
        assertEquals(
                "\n bucket[1] = [1233, First Element] -> [12310, First Colision]" +
                        "\n bucket[2] = [1234, Second Element]", hashTable.toString()
        );
        //endregion
    }

    @org.junit.jupiter.api.Test
    void realSize() {
        // Casuisticas:
        // 1. Añadimos un elemento para ver que no aumenta el realSize.
        HashTable hashTable = new HashTable();
        hashTable.put("1234", "Prueba");
        assertEquals(16, hashTable.realSize());

        // 2. Obtenemos un elemento para ver que no aumenta el realSize.
        hashTable.get("1234");
        assertEquals(16, hashTable.realSize());

        // 3. Actualizamos un elemento para ver que no aumenta el realSize.
        hashTable.put("1234", String.valueOf(hashTable.realSize()));
        assertEquals(16, hashTable.realSize());

        // 4. Borramos un elemento para ver que no aumenta el realSize.
        hashTable.drop("1234");
        assertEquals(16, hashTable.realSize());

    }
}