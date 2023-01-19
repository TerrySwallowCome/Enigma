package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                    e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                    c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                    ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                    ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }


    @Test
    public void testInvertChar() {
        String az = "(ABCDEFGHIJKLMNOPQRSTUVWXYZ)";
        Permutation perm2 = new Permutation(az, new Alphabet(UPPER_STRING));
        assertEquals('Z', perm2.invert('A'));
        assertEquals('Y', perm2.invert('Z'));
        assertEquals('M', perm2.invert('N'));
        assertEquals(2, perm2.invert(3));
        assertEquals(25, perm2.invert(0));
        assertEquals(24, perm2.invert(25));
        assertEquals(26, perm2.size());
        assertTrue(perm2.derangement());

        Permutation p = new Permutation("(A)", new Alphabet("ABCD"));
        assertEquals('A', p.invert('A'));
        assertEquals('B', p.invert('B'));
        assertEquals('C', p.invert('C'));
        assertEquals('D', p.invert('D'));
        assertEquals(0,  p.invert(0));
    }

    @Test
    public void testDerangement() {
        Permutation perm3 = new Permutation("", new Alphabet(UPPER_STRING));
        assertFalse(perm3.derangement());
        assertEquals('B', perm3.permute('B'));
        assertEquals(2, perm3.permute(2));
        assertEquals(26, perm3.size());
    }

    @Test
    public void testPermuteChar() {
        Permutation abcd = new Permutation("(ADC)", new Alphabet("ADC"));
        assertEquals('A', abcd.permute('C'));
        assertTrue(abcd.derangement());
        Permutation now = new Permutation("(BC)  (D)", new Alphabet("ABCD"));
        assertEquals('D', now.permute('D'));
    }

    @Test
    public void testDiffCycleSize() {
        Permutation perm5 = new Permutation(" ", new Alphabet(UPPER_STRING));
        assertEquals('A', perm5.invert('A'));
        assertEquals(0, perm5.invert(0));
        assertEquals(26, perm5.size());
    }

    @Test(expected = EnigmaException.class)
    public void testNotInAlphabet() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        p.invert('F');
        Permutation p1 = new Permutation("(MICH)", new Alphabet("MICH"));
        p.invert('A');
        p.permute(5);
        p.invert('B');
    }

    @Test(expected = EnigmaException.class)
    public void testEmptyAlphabet() {
        Permutation perm6 = new Permutation("", new Alphabet(""));
        perm6.invert('F');
    }

    @Test(expected = EnigmaException.class)
    public void testErrorCycle() {
        Permutation perm7 = new Permutation("ADC", new Alphabet("ADC"));
        Permutation perm8 = new Permutation("(ACEFG)", new Alphabet("CE"));
    }


}







