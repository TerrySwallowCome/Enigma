package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Tianyu Liu
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        set = 0;
        _r = 0;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    int getr() {
        return _r;
    }
    /** Return my current setting. */
    int setting() {
        return set;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        set = permutation().wrap(posn);
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        set = alphabet().toInt(cposn);
    }

    /** use R. */
    void useR(char r) {
        _r = alphabet().toInt(r);
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        int partOne = permutation().permute(p + set - _r);
        int stepOne = permutation().wrap(partOne - set + _r);
        int result = permutation().wrap(stepOne);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(result));
        }
        return result;
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        int partOne = permutation().invert(e + set - _r);
        int stepOne = permutation().wrap(partOne - set + _r);
        int result = permutation().wrap(stepOne);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(result));
        }
        return result;
    }

    /** Returns the positions of the notches, as a string giving the letters
     *  on the ring at which they occur. */
    String notches() {
        return "";
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }


    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** Return the notch_list of this rotor. */
    int[] getnotchlist() {
        return null;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

    /** Setting number. */
    private int set;

    /** Ring. */
    private int _r;


}
