package enigma;
import static enigma.EnigmaException.*;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Tianyu Liu
 */
class Alphabet {
    /** Characters within this alphabet. */
    private int[] _chars;

    /**
     * A new alphabet containing CHARS. The K-th character has index
     * K (numbering from 0). No character may be duplicated.
     */
    Alphabet(String chars) {
        _chars = new int[chars.length()];
        for (int i = 0; i < chars.length(); i++) {
            char j = chars.charAt(i);
            _chars[i] = j;
        }
    }

    /**
     * A default alphabet of all upper-case characters.
     */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /**
     * Returns the size of the alphabet.
     */
    int size() {
        return _chars.length;
    }

    /**
     *  Return _chars of alphabet.
     */
    int[] chars() {
        return _chars;
    }

    /**
     * Returns true if CH is in this alphabet.
     */
    boolean contains(char ch) {
        for (int i = 0; i < size(); i++) {
            if (_chars[i] == ch) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns character number INDEX in the alphabet, where
     * 0 <= INDEX < size().
     */
    char toChar(int index) {
        char result = (char) _chars[index];
        return result;
    }

    /**
     * Returns the index of character CH which must be in
     * the alphabet. This is the inverse of toChar().
     */
    int toInt(char ch) {
        for (int i = 0; i < _chars.length; i++) {
            if (ch == _chars[i]) {
                return i;
            }
        }
        throw error("Wrong in alphabet");
    }
}

