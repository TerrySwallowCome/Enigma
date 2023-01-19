package enigma;
import java.util.HashMap;
import java.util.Scanner;


/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Tianyu Liu
 */
class Permutation {
    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycle = cycles;
        map1 = new HashMap<>();
        map2 = new HashMap<>();
        Scanner a = new Scanner(cycles);
        while (a.hasNext()) {
            String next = a.next().trim();
            next = next.replace("(", "");
            next = next.replace(")", "");
            addCycle(next);
        }
        for (int i = 0; i < alphabet().size(); i++) {
            if (!map1.containsKey(i)) {
                map1.put(i, i);
                map2.put(i, i);
            }
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        int last = _alphabet.toInt(cycle.charAt(cycle.length() - 1));
        int first = _alphabet.toInt(cycle.charAt(0));
        map1.put(last, first);
        map2.put(first, last);

        for (int i = 0; i < cycle.length() - 1; i++) {
            int i1 = _alphabet.toInt(cycle.charAt(i));
            int i2 = _alphabet.toInt(cycle.charAt(i + 1));
            map1.put(i1, i2);
        }
        for (int j = 0; j < cycle.length() - 1; j++) {
            int i1 = _alphabet.toInt(cycle.charAt(j + 1));
            int i2 = _alphabet.toInt(cycle.charAt(j));
            map2.put(i1, i2);
        }
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int result = p % size();
        if (result < 0) {
            result += size();
        }
        return result;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int wrapped = wrap(p);
        return map1.get(wrapped);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int wrapped = wrap(c);
        return map2.get(wrapped);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int targetInt = _alphabet.toInt(p);
        return _alphabet.toChar(permute(targetInt));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int targetInt = (_alphabet.toInt(c));
        return _alphabet.toChar(invert(targetInt));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (Integer a: map1.keySet()) {
            if (a.equals(map1.get(a))) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
    /** Unused stuff. */
    private String _cycle;
    /** Forward hashmap. */
    private HashMap<Integer, Integer> map1;
    /** Backward hashmap. */
    private HashMap<Integer, Integer> map2;

}
