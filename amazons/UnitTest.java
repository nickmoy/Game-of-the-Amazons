package amazons;

import org.junit.Test;

import static amazons.Piece.*;
import static org.junit.Assert.*;
import ucb.junit.textui;

import java.util.Iterator;

/** The suite of all JUnit tests for the amazons package.
 *  @author Nicholas Moy.
 */
public class UnitTest {

    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /** Tests basic correctness of put and get on the initialized board. */
    @Test
    public void testBasicPutGet() {
        Board b = new Board();
        b.put(BLACK, Square.sq(3, 5));
        assertEquals(b.get(3, 5), BLACK);
        b.put(WHITE, Square.sq(9, 9));
        assertEquals(b.get(9, 9), WHITE);
        b.put(EMPTY, Square.sq(3, 5));
        assertEquals(b.get(3, 5), EMPTY);
    }

    /** Tests proper identification of legal/illegal queen moves. */
    @Test
    public void testIsQueenMove() {
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(1, 5)));
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(2, 7)));
        assertFalse(Square.sq(0, 0).isQueenMove(Square.sq(5, 1)));
        assertTrue(Square.sq(1, 1).isQueenMove(Square.sq(9, 9)));
        assertTrue(Square.sq(2, 7).isQueenMove(Square.sq(8, 7)));
        assertTrue(Square.sq(3, 0).isQueenMove(Square.sq(3, 4)));
        assertTrue(Square.sq(7, 9).isQueenMove(Square.sq(0, 2)));
    }

    /** Tests toString for initial board state and a smiling board state. :) */
    @Test
    public void testToString() {
        Board b = new Board();
        assertEquals(INIT_BOARD_STATE, b.toString());
        makeSmile(b);
        assertEquals(SMILE, b.toString());
    }

    /** Tests the ReachableFromIterator in Board. */
    @Test
    public void testReachableFromIterator1() {
        Board b = new Board();
        Iterator<Square> iter = b.reachableFrom(
                Square.sq(0, 0), null);
        assertTrue(iter.hasNext());
        Square s = iter.next();
        assertEquals(0, s.col());
        assertEquals(1, s.row());
        s = iter.next();
        assertEquals(0, s.col());
        assertEquals(2, s.row());
        s = iter.next();
        assertEquals(1, s.col());
        assertEquals(1, s.row());
        s = iter.next();
        assertEquals(2, s.col());
        assertEquals(2, s.row());
        s = iter.next();
        s = iter.next();
        s = iter.next();
        s = iter.next();
        s = iter.next();
        s = iter.next();
        s = iter.next();
        System.out.println(b.toString());
        s = iter.next();
        assertEquals(1, s.col());
        assertEquals(0, s.row());
        s = iter.next();
        assertEquals(2, s.col());
        assertEquals(0, s.row());
        assertFalse(iter.hasNext());

        b = new Board();

        b.put(SPEAR, Square.sq(1, 0));
        b.put(SPEAR, Square.sq(0, 1));
        b.put(SPEAR, Square.sq(1, 1));
        iter = b.reachableFrom(Square.sq(0, 0), null);
        assertFalse(iter.hasNext());

    }

    @Test
    public void testReachableFromIterator2() {
        Board b = new Board();

        b.put(WHITE, Square.sq(0, 9));
        b.put(SPEAR, Square.sq(0, 8));
        b.put(SPEAR, Square.sq(1, 9));

        Iterator<Square> iter = b.reachableFrom(Square.sq(0, 9), null);
        assertTrue(iter.hasNext());

        b = new Board();
        b.put(SPEAR, Square.sq(8, 8));
        b.put(SPEAR, Square.sq(8, 7));
        b.put(SPEAR, Square.sq(9, 7));
        System.out.println(b.toString());
        iter = b.reachableFrom(Square.sq(9, 8), null);
        assertTrue(iter.hasNext());
        iter.next();
        assertTrue(iter.hasNext());

        b = new Board();
        b.put(SPEAR, Square.sq(6, 9));
        b.put(SPEAR, Square.sq(6, 8));
        b.put(SPEAR, Square.sq(7, 8));
        b.put(SPEAR, Square.sq(8, 8));
        b.put(SPEAR, Square.sq(9, 8));
        System.out.println(b.toString());
        iter = b.reachableFrom(Square.sq(7, 9), null);
        assertTrue(iter.hasNext());
        System.out.println(iter.next());
        assertTrue(iter.hasNext());
        System.out.println(iter.next());
        assertFalse(iter.hasNext());
    }

    /** Tests the legalMoveIterator in Board.java. */
    @Test
    public void testLegalMoveIterator() {
        Board b = new Board();
        Iterator<Move> iter = b.legalMoves(WHITE);
        for (int i = 2; i <= 8; i++) {
            Move m = iter.next();
            assertEquals(Square.sq(3, 0), m.from());
            assertEquals(Square.sq(3, 1), m.to());
            assertEquals(Square.sq(3, i), m.spear());
        }

        b = new Board();

        b.put(EMPTY, Square.sq(0, 3));
        b.put(EMPTY, Square.sq(9, 3));
        b.put(EMPTY, Square.sq(3, 0));
        b.put(EMPTY, Square.sq(6, 0));

        b.put(WHITE, Square.sq(0, 0));
        b.put(SPEAR, Square.sq(1, 0));
        b.put(SPEAR, Square.sq(0, 1));
        b.put(SPEAR, Square.sq(1, 1));

        iter = b.legalMoves(WHITE);
        assertFalse(iter.hasNext());

        b = new Board();
        b.put(EMPTY, Square.sq(0, 3));
        b.put(EMPTY, Square.sq(9, 3));
        b.put(EMPTY, Square.sq(3, 0));
        b.put(EMPTY, Square.sq(6, 0));

        b.put(WHITE, Square.sq(0, 0));
        b.put(SPEAR, Square.sq(1, 0));
        b.put(SPEAR, Square.sq(0, 1));
        b.put(SPEAR, Square.sq(1, 1));

        iter = b.legalMoves(WHITE);
        assertFalse(iter.hasNext());

    }

    @Test
    public void testLegalMoveIterator2() {
        Board b = new Board();
        b.put(EMPTY, Square.sq(0, 3));
        b.put(EMPTY, Square.sq(9, 3));
        b.put(EMPTY, Square.sq(3, 0));
        b.put(EMPTY, Square.sq(6, 0));

        b.put(WHITE, Square.sq(0, 9));
        b.put(SPEAR, Square.sq(0, 8));
        b.put(SPEAR, Square.sq(1, 9));
        System.out.println(b.toString());

        Iterator<Move> iter = b.legalMoves(WHITE);
        assertTrue(iter.hasNext());

        b = new Board();
        b.put(EMPTY, Square.sq(0, 3));
        b.put(EMPTY, Square.sq(9, 3));
        b.put(EMPTY, Square.sq(3, 0));
        b.put(EMPTY, Square.sq(6, 0));

        b.put(WHITE, Square.sq(9, 9));
        b.put(SPEAR, Square.sq(6, 9));
        b.put(SPEAR, Square.sq(6, 8));
        b.put(SPEAR, Square.sq(7, 8));
        b.put(SPEAR, Square.sq(8, 8));
        b.put(SPEAR, Square.sq(8, 7));
        b.put(SPEAR, Square.sq(9, 7));
        System.out.println(b.toString());

        iter = b.legalMoves(WHITE);
        int numMoves = 0;
        while (iter.hasNext()) {
            Move m = iter.next();
            System.out.println(m);
            numMoves += 1;
        }
        assertEquals(7, numMoves);
    }

    @Test
    public void testWin() {
        Board b = new Board();

        b.put(EMPTY, Square.sq(0, 6));
        b.put(EMPTY, Square.sq(9, 6));
        b.put(EMPTY, Square.sq(3, 9));
        b.put(EMPTY, Square.sq(6, 9));

        b.put(BLACK, Square.sq(0, 9));
        b.put(BLACK, Square.sq(0, 8));
        b.put(BLACK, Square.sq(1, 9));
        b.put(BLACK, Square.sq(1, 8));

        b.put(SPEAR, Square.sq(0, 7));
        b.put(SPEAR, Square.sq(1, 7));
        b.put(SPEAR, Square.sq(2, 7));
        b.put(SPEAR, Square.sq(2, 8));

        System.out.println(b.toString());
    }

    /** Tests the queenMove method in Board.java. */
    @Test
    public void testQueenMove() {
        Board b = new Board();
        Square s = Square.sq(0, 0);
        s = s.queenMove(2, 2);
        assertEquals(2, s.col());
        assertEquals(0, s.row());
        s = s.queenMove(0, 2);
        assertEquals(2, s.col());
        assertEquals(2, s.row());
    }

    private void makeSmile(Board b) {
        b.put(EMPTY, Square.sq(0, 3));
        b.put(EMPTY, Square.sq(0, 6));
        b.put(EMPTY, Square.sq(9, 3));
        b.put(EMPTY, Square.sq(9, 6));
        b.put(EMPTY, Square.sq(3, 0));
        b.put(EMPTY, Square.sq(3, 9));
        b.put(EMPTY, Square.sq(6, 0));
        b.put(EMPTY, Square.sq(6, 9));
        for (int col = 1; col < 4; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(2, 7));
        for (int col = 6; col < 9; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(7, 7));
        for (int lip = 3; lip < 7; lip += 1) {
            b.put(WHITE, Square.sq(lip, 2));
        }
        b.put(WHITE, Square.sq(2, 3));
        b.put(WHITE, Square.sq(7, 3));
    }

    static final String INIT_BOARD_STATE =
              "   - - - B - - B - - -\n"
            + "   - - - - - - - - - -\n"
            + "   - - - - - - - - - -\n"
            + "   B - - - - - - - - B\n"
            + "   - - - - - - - - - -\n"
            + "   - - - - - - - - - -\n"
            + "   W - - - - - - - - W\n"
            + "   - - - - - - - - - -\n"
            + "   - - - - - - - - - -\n"
            + "   - - - W - - W - - -\n";

    static final String SMILE =
              "   - - - - - - - - - -\n"
            + "   - S S S - - S S S -\n"
            + "   - S - S - - S - S -\n"
            + "   - S S S - - S S S -\n"
            + "   - - - - - - - - - -\n"
            + "   - - - - - - - - - -\n"
            + "   - - W - - - - W - -\n"
            + "   - - - W W W W - - -\n"
            + "   - - - - - - - - - -\n"
            + "   - - - - - - - - - -\n";
}
