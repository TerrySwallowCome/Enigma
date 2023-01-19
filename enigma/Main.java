package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Tianyu Liu
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        try {
            Machine thisMachine = readConfig();
            String setting = _input.nextLine();
            if (!setting.contains("*")) {
                throw error("Wrong format of setting line");
            }
            setUp(thisMachine, setting);
            String inputMSG = "";
            String result = "";
            while (_input.hasNextLine()) {
                String thisLine = _input.nextLine();
                if (thisLine.contains("*")) {
                    setUp(thisMachine, thisLine);
                } else {
                    inputMSG = thisLine.replace(" ", "");
                    printMessageLine(thisMachine.convert(inputMSG));
                }
            }
        } catch (NoSuchElementException excp) {
            throw error("Wrong next in process");
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            allRotors = new ArrayList<Rotor>();
            String az = _config.next();
            _alphabet = new Alphabet(az);
            if (az.contains("*")) {
                throw error("Wrong alphabet NO.1");
            }
            if (az.contains("(") | az.contains(")")) {
                throw error("Wrong alphabet NO.2");
            }
            int numRotors = _config.nextInt();
            int numPawls = _config.nextInt();
            while (_config.hasNext()) {
                allRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, numPawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String rotorName = _config.next();
            String notchString = _config.next();
            String cycles = "";
            while (_config.hasNext("\\(.*\\)")) {
                String nextCycle = "";
                String nextElement = _config.next();
                if (nextElement.contains(")(")) {
                    String[] C = nextElement.split("\\)\\(");
                    for (int i = 0; i < C.length; i++) {
                        nextCycle += C[i] + " ";
                    }
                } else {
                    nextCycle = nextElement;
                }
                if (nextCycle.equals("(AVOLDRWFIUQ)(BZKSMNHYC)")) {
                    throw error("right here!");
                }
                cycles += nextCycle + " ";
            }
            Permutation perm = new Permutation(cycles, _alphabet);
            if (notchString.charAt(0) == 'M') {
                String notches = "";
                for (int i = 1; i < notchString.length(); i++) {
                    for (int j = i + 1; j < notchString.length(); j++) {
                        if (notchString.charAt(i) == notchString.charAt(j)) {
                            throw error("No repeated notches.");
                        }
                    }
                }
                for (int i = 1; i < notchString.length(); i++) {
                    notches += notchString.charAt(i);
                }
                return new MovingRotor(rotorName, perm, notches);
            } else if (notchString.charAt(0) == 'N') {
                if (notchString.length() > 1) {
                    throw error(
                            "No notches in fixed rotors");
                }
                return new FixedRotor(rotorName, perm);
            } else if (notchString.charAt(0) == 'R') {
                if (notchString.length() > 1) {
                    throw error(
                            "No notches in reflectors.");
                }
                return new Reflector(rotorName, perm);
            } else {
                throw new EnigmaException("Incorrect notchString");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] setString = settings.split(" ");
        String[] rotorSet = new String[M.numRotors()];
        for (int i = 1; i <= M.numRotors(); i++) {
            rotorSet[i - 1] = setString[i];
        }
        if (!setString[0].equals("*")) {
            throw error("Wrong starting of setting");
        }
        for (int k = 0; k < M.numRotors(); k++) {
            for (int j = k + 1; j < M.numRotors(); j++) {
                if (rotorSet[k].equals(rotorSet[j])) {
                    throw error("No repeat rotors.");
                }
            }
        }
        if (M.numRotors() > setString.length - 2) {
            throw error("wrong rotor number.");
        }
        String setting = setString[M.numRotors() + 1];
        if (setting.length() != M.numRotors() - 1) {
            throw error("wrong setting format in setUp");
        }
        for (int i = 0; i < setting.length(); i++) {
            if (!_alphabet.contains(setting.charAt(i))) {
                throw error("Setting contains unknown character");
            }
        }
        int index = 2;
        String ring = "";
        if (M.numRotors() + 2 < setString.length) {
            if (setString[M.numRotors() + 2].contains("(")) {
                index = 2;
            } else {
                index = 3;
                ring = setString[M.numRotors() + 2];
            }
        }
        String plugboard = "";
        for (int j = M.numRotors() + index; j < setString.length; j++) {
            plugboard += setString[j] + " ";
        }
        Permutation perm = new Permutation(plugboard, _alphabet);
        M.setPlugboard(perm);
        M.insertRotors(rotorSet);
        if (!M.getRotor(0).reflecting()) {
            throw error("The rotor at index 0 must be a reflector.");
        }
        M.setRotors(setting);
        if (ring.length() != 0) {
            M.insertRings(ring);
        }
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String m = msg.replace(" ", "");
        String result = "";
        while (m.length() > 5) {
            result += m.substring(0, 5) + " ";
            m = m.substring(5, m.length());
        }
        result += m;
        _output.println(result);
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;

    /** All rotor. */
    private ArrayList<Rotor> allRotors;
}

