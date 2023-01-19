package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Tianyu Liu
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notch = new int[notches.length()];
        for (int i = 0; i < notches.length(); i++) {
            int num = perm.wrap(getr());
            _notch[i] = perm.alphabet().toInt(notches.charAt(i)) + num;
        }
    }

    @Override
    boolean atNotch() {
        for (int j = 0; j < _notch.length; j++) {
            if (_notch[j] == setting()) {
                return true;
            }
        }
        return false;
    }

    @Override
    void advance() {
        this.set(this.setting() + 1);
    }

    @Override
    String notches() {
        return "Moving Rotor" + name();
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    int[] getnotchlist() {
        return _notch;
    }

    /** A list used to store the notches. */
    private int[] _notch;

}
