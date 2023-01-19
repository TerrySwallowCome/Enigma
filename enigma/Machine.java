package enigma;

import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Tianyu Liu
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numrotor = numRotors;
        _pawl = pawls;
        _all = allRotors.toArray(new Rotor[numRotors]);
        _plugboard = new Permutation(" ", AZ);
        _rotorslot = new Rotor[_numrotor];
    }


    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numrotor;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawl;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        return _rotorslot[k];
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        for (int i = 0; i < rotors.length; i++) {
            for (int j = 0; j < _all.length; j++) {
                Rotor targetRotor = _all[j];
                if (rotors[i].equals(targetRotor.name())) {
                    _rotorslot[i] = targetRotor;
                }
            }
        }
        if (_rotorslot.length != _numrotor) {
            throw error("Wrong insertRotor in machine, %s", _rotorslot.length);
        }
    }

    void insertRings(String ring) {
        for (int i = 1; i < _rotorslot.length; i++) {
            _rotorslot[i].useR(ring.charAt(i - 1));
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (_rotorslot.length != _numrotor) {
            throw error("Dismatched rotorslot length and setting");
        }
        for (int i = 1; i < _numrotor; i++) {
            _rotorslot[i].set(setting.charAt(i - 1));
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {

        int pawNum = this.numRotors() - this.numPawls();
        boolean[] flag = new boolean[this.numRotors()];
        for (int i = _rotorslot.length - 1; i > pawNum; i--) {
            if (this.getRotor(i).atNotch()) {
                if (this.getRotor(i).rotates()) {
                    flag[i] = true;
                }
                if (this.getRotor(i - 1).rotates() && i > pawNum) {
                    flag[i - 1] = true;
                }
            }
        }
        flag[flag.length - 1] = true;
        for (int j = pawNum; j < this.numRotors(); j++) {
            if (flag[j]) {
                this.getRotor(j).advance();
            }
        }
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        int output1 = c;
        for (int i = _rotorslot.length - 1; i > 0; i--) {
            Rotor targetStage1 = _rotorslot[i];
            output1 = targetStage1.convertForward(output1);

        }
        Rotor targetStage2 = _rotorslot[0];
        int output2 = targetStage2.convertForward(output1);
        int output3 = output2;
        for (int j = 1; j < _rotorslot.length; j++) {
            Rotor targetStage3 = _rotorslot[j];
            output3 = targetStage3.convertBackward(output3);
        }
        int result = output3;
        return result;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        for (int i = 0; i < msg.length(); i++) {
            char ch = msg.charAt(i);
            int c = _alphabet.toInt(ch);
            int output = convert(c);
            char finalOutput = _alphabet.toChar(output);
            result += String.valueOf(finalOutput);
        }
        return result;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
    /** Num of rotor that can be inserted into this machine. */
    private int _numrotor;
    /** Num of pawl of this machine. */
    private int _pawl;
    /** Rotors that have been inserted. */
    private Rotor[] _rotorslot;
    /** Current plugboard. */
    private Permutation _plugboard;
    /** All the rotors that are available. */
    private Rotor[] _all;
    /** An easy alphabet. */
    private static final Alphabet AZ = new Alphabet(TestUtils.UPPER_STRING);
}
