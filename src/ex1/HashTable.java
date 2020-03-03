package ex1;

// Original source code: https://gist.github.com/amadamala/3cdd53cb5a6b1c1df540981ab0245479
// Modified by Fernando Porrino Serrano for academic purposes.

import java.util.ArrayList;

public class HashTable {
    private int INITIAL_SIZE = 16;
    private int size = 0;
    private HashEntry[] entries = new HashEntry[INITIAL_SIZE];

    public int size(){
        return this.size;
    }

    public int realSize(){
        return this.INITIAL_SIZE;
    }

    public void put(String key, String value) {
        int hash = getHash(key);
        final HashEntry hashEntry = new HashEntry(key, value);
        int pos = 0;

        if(entries[hash] == null) {
            entries[hash] = hashEntry;
        }

        else {
            HashEntry temp = entries[hash];
            if (temp.key.equals(key)) {
                entries[hash] = hashEntry;
                return;
            }

            while(temp.next != null){
                temp = temp.next;
                pos++;
                //ERROR: En el caso de una clave primaria que sea colisionada, no se modificaba el valor, era necesario otro caso
                // aparte del indicado anteriormente.
                if (temp.key.equals(key)) {
                    temp.value = hashEntry.value;
                    return;
                }
            }
            temp.next = hashEntry;
            hashEntry.prev = temp;
            //????
            //hashEntry.prev = temp;
        }
        //ERROR: La variable size no se incrementaba al añadir un elemento.
        size++;

    }

    /**
     * Returns 'null' if the element is not found.
     */
    public String get(String key) {
        int hash = getHash(key);

        if(entries[hash] != null) {
            HashEntry temp = entries[hash];

            while( !temp.key.equals(key)) {
                //ERROR: Si la clave que buscamos no tiene valor y es la ultima, causaba NullPointerException.
                if (temp.next == null) return null;
                temp = temp.next;
            }
            return temp.value;
        }
        return null;
    }


    /*
        bucket[0] = [0, null] -> [11, null] -> [22, null]                                      Clave vacia.
        bucket[1] = [1233, First Element] -> [12310, First Colision] -> [1, Second Colision] -> [23, null]
        bucket[2] = [1234, Second Element]  -> [2, null] -> [13, null] -> [24, null]
     */
    public void drop(String key) {
        int hash = getHash(key);
        if(entries[hash] != null) {
            // ERROR: Caso base, elemento sin colision.
            HashTable.HashEntry temp = entries[hash];
            if (temp.key.equals(key)){
                if (temp.next != null) temp.next.prev = null;
                entries[hash] = temp.next;
                size--;
                return;
            }
            while( !temp.key.equals(key)){
                temp = temp.next;
            }
            System.out.println(temp.value);
                if(temp.next != null) temp.next.prev = temp.prev;   //esborrem temp, per tant actualitzem l'anterior al següent
                temp.prev.next = temp.next;                         //esborrem temp, per tant actualitzem el següent de l'anterior

            //ERROR: El size no es restaba.
            size--;
        }
    }

    private int getHash(String key) {
        // piggy backing on java string
        // hashcode implementation.
        // ERROR: Algunas claves el hash retorna un valor negativo, por eso le aplicamos el absoluto al resultado.
        return Math.abs(key.hashCode() % INITIAL_SIZE);
    }

    private class HashEntry {
        String key;
        String value;

        // Linked list of same hash entries.
        HashEntry next;
        HashEntry prev;

        public HashEntry(String key, String value) {
            this.key = key;
            this.value = value;
            this.next = null;
            this.prev = null;
        }

        @Override
        public String toString() {
            return "[" + key + ", " + value + "]";
        }
    }

    @Override
    public String toString() {
        int bucket = 0;
        StringBuilder hashTableStr = new StringBuilder();
        for (HashEntry entry : entries) {
            if(entry == null) {
                bucket++;
                continue;
            }
            hashTableStr.append("\n bucket[")
                    .append(bucket)
                    .append("] = ")
                    .append(entry.toString());
            bucket++;
            HashEntry temp = entry.next;
            while(temp != null) {
                hashTableStr.append(" -> ");
                hashTableStr.append(temp.toString());
                temp = temp.next;
            }
        }
        return hashTableStr.toString();
    }

    public ArrayList<String> getCollisionsForKey(String key) {
        return getCollisionsForKey(key, 1);
    }

    public ArrayList<String> getCollisionsForKey(String key, int quantity){
        /*
          Main idea:
          alphabet = {0, 1, 2}

          Step 1: "000"
          Step 2: "001"
          Step 3: "002"
          Step 4: "010"
          Step 5: "011"
           ...
          Step N: "222"

          All those keys will be hashed and checking if collides with the given one.
        * */

        final char[] alphabet = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        ArrayList<Integer> newKey = new ArrayList();
        ArrayList<String> foundKeys = new ArrayList();

        newKey.add(0);
        int collision = getHash(key);
        int current = newKey.size() -1;

        while (foundKeys.size() < quantity){
            //building current key
            String currentKey = "";
            for(int i = 0; i < newKey.size(); i++)
                currentKey += alphabet[newKey.get(i)];

            if(!currentKey.equals(key) && getHash(currentKey) == collision)
                foundKeys.add(currentKey);

            //increasing the current alphabet key
            newKey.set(current, newKey.get(current)+1);

            //overflow over the alphabet on current!
            if(newKey.get(current) == alphabet.length){
                int previous = current;
                do{
                    //increasing the previous to current alphabet key
                    previous--;
                    if(previous >= 0)  newKey.set(previous, newKey.get(previous) + 1);
                }
                while (previous >= 0 && newKey.get(previous) == alphabet.length);

                //cleaning
                for(int i = previous + 1; i < newKey.size(); i++)
                    newKey.set(i, 0);

                //increasing size on underflow over the key size
                if(previous < 0) newKey.add(0);

                current = newKey.size() -1;
            }
        }

        return  foundKeys;
    }

    public static void main(String[] args) {
        HashTable hashTable = new HashTable();
        
        // Put some key values.
        for(int i=0; i<30; i++) {
            final String key = String.valueOf(i);
            hashTable.put(key, key);
        }

        // Print the HashTable structure
        log("****   HashTable  ***");
        log(hashTable.toString());
        log("\nValue for key(20) : " + hashTable.get("20") );
    }

    private static void log(String msg) {
        System.out.println(msg);
    }
}