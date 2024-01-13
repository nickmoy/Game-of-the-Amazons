package amazons;

/* NOTICE:
*  This file is a SUGGESTED skeleton.  NOTHING here or in any other source
*  file is sacred.  If any of it confuses you, throw it out and do it your way.
*/

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

import static amazons.Piece.EMPTY;
import static amazons.Piece.WHITE;
import static amazons.Piece.BLACK;
import static amazons.Piece.SPEAR;

/** The state of an Amazons Game.
 *  @author Nicholas Moy.
 */
class Board {

    /** The number of squares on a side of the board. */
    static final int SIZE = 10;

    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Board model) {
        _moves = model._moves;
        _turn = model._turn;
        _winner = EMPTY;
    }

    /** Clears the board to the initial position. */
    void init() {
        _moves = new ArrayList<>();
        _turn = WHITE;
        _winner = EMPTY;

        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                Square s = Square.sq(c, r);
                s.setPiece(EMPTY);
            }
        }

        Square.sq(0, 3).setPiece(WHITE);
        Square.sq(9, 3).setPiece(WHITE);
        Square.sq(3, 0).setPiece(WHITE);
        Square.sq(6, 0).setPiece(WHITE);

        Square.sq(0, 6).setPiece(BLACK);
        Square.sq(9, 6).setPiece(BLACK);
        Square.sq(3, 9).setPiece(BLACK);
        Square.sq(6, 9).setPiece(BLACK);

    }

    /** Return the Piece whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the number of moves (that have not been undone) for this
     *  board. */
    int numMoves() {
        return _moves.size();
    }

    /** Return the winner in the current position, or null if the game is
     *  not yet finished. */
    Piece winner() {
        Iterator<Move> cur = legalMoves();
        if (!cur.hasNext()) {
            return _turn.opponent();
        }
        return null;
    }

    /** Return the contents the square at S. */
    final Piece get(Square s) {
        return s.piece();
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        if (!Square.exists(col, row)) {
            throw new IndexOutOfBoundsException(
                    "Can't get square out of bounds");
        }
        return Square.sq(col, row).piece();
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        s.setPiece(p);
        _winner = null;
    }

    /** Set square (COL, ROW) to P. */
    final void put(Piece p, int col, int row) {
        Square.sq(col, row).setPiece(p);
        _winner = null;
    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, col - 'a', row - '1');
    }

    /** Return true iff FROM - TO is an unblocked queen move on the current
     *  board, ignoring the contents of ASEMPTY, if it is encountered.
     *  For this to be true, FROM-TO must be a queen move and the
     *  squares along it, other than FROM and ASEMPTY, must be
     *  empty. ASEMPTY may be null, in which case it has no effect. */
    boolean isUnblockedMove(Square from, Square to, Square asEmpty) {
        if (!from.isQueenMove(to)) {
            System.out.println(from.col() + " " + from.row());
            System.out.println(to.col() + " " + to.row());
            throw new IllegalArgumentException("Illegal move passed");
        }
        int dir = from.direction(to);
        from = from.queenMove(dir, 1);
        while (!from.equals(to)) {
            if (!(from.piece() == EMPTY) && !(from == asEmpty)) {
                return false;
            }
            from = from.queenMove(dir, 1);
        }
        if (to.piece() == EMPTY || to == asEmpty) {
            return true;
        }
        return false;
    }

    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        return from.piece() == _turn;
    }

    /** Return true iff FROM-TO is a valid first part of move, ignoring
     *  spear throwing. */
    boolean isLegal(Square from, Square to) {
        return isLegal(from) && from.isQueenMove(to) && to.piece() == EMPTY;
    }

    /** Return true iff FROM-TO(SPEAR) is a legal move in the current
     *  position. */
    boolean isLegal(Square from, Square to, Square spear) {
        return isLegal(from, to) && isUnblockedMove(from, to, null)
                && isUnblockedMove(to, spear, from);
    }

    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to(), move.spear());
    }

    /** Move FROM-TO(SPEAR), assuming this is a legal move. */
    void makeMove(Square from, Square to, Square spear) {
        _moves.add(Move.mv(from, to, spear));
        to.setPiece(from.piece());
        from.setPiece(EMPTY);
        spear.setPiece(SPEAR);
        _turn = _turn.opponent();
    }

    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        makeMove(move.from(), move.to(), move.spear());
    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        if (_moves.size() == 0) {
            return;
        }
        Move m = _moves.remove(_moves.size() - 1);
        m.spear().setPiece(EMPTY);
        m.from().setPiece(m.to().piece());
        m.to().setPiece(EMPTY);
        _turn = _turn.opponent();
    }

    /** Return an Iterator over the Squares that are reachable by an
     *  unblocked queen move from FROM. Does not pay attention to what
     *  piece (if any) is on FROM, nor to whether the game is finished.
     *  Treats square ASEMPTY (if non-null) as if it were EMPTY.  (This
     *  feature is useful when looking for Moves, because after moving a
     *  piece, one wants to treat the Square it came from as empty for
     *  purposes of spear throwing.) */
    Iterator<Square> reachableFrom(Square from, Square asEmpty) {
        return new ReachableFromIterator(from, asEmpty);
    }

    /** Return an Iterator over all legal moves on the current board. */
    Iterator<Move> legalMoves() {
        return new LegalMoveIterator(_turn);
    }

    /** Return an Iterator over all legal moves on the current board for
     *  SIDE (regardless of whose turn it is). */
    Iterator<Move> legalMoves(Piece side) {
        return new LegalMoveIterator(side);
    }

    /** An iterator used by reachableFrom. */
    private class ReachableFromIterator implements Iterator<Square> {

        /** Iterator of all squares reachable by queen move from FROM,
         *  treating ASEMPTY as empty. */
        ReachableFromIterator(Square from, Square asEmpty) {
            _from = from;
            _dir = -1;
            _steps = 0;
            _asEmpty = asEmpty;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _dir < 8;
        }

        @Override
        public Square next() {
            Square nSquare = _from.queenMove(_dir, _steps);
            toNext();
            return nSquare;
        }

        /** Advance _dir and _steps, so that the next valid Square is
         *  _steps steps in direction _dir from _from. */
        private void toNext() {
            if (!hasNext()) {
                return;
            }
            Square ns = _from.queenMove(_dir, _steps + 1);
            if (ns == null || (ns.piece() != EMPTY && ns != _asEmpty)) {
                _dir += 1;
                _steps = 0;
                toNext();
                return;
            }
            _steps += 1;
        }

        /** Starting square. */
        private Square _from;
        /** Current direction. */
        private int _dir;
        /** Current distance. */
        private int _steps;
        /** Square treated as empty. */
        private Square _asEmpty;
    }

    /** An iterator used by legalMoves. */
    private class LegalMoveIterator implements Iterator<Move> {

        /** All legal moves for SIDE (WHITE or BLACK). */
        LegalMoveIterator(Piece side) {
            _startingSquares = Square.iterator();
            _start = null;
            _pieceMoves = NO_SQUARES;
            _spearThrows = NO_SQUARES;
            _fromPiece = side;
            _j10 = Square.sq(9, 9);
            toNext();
        }

        @Override
        public boolean hasNext() {
            if (_last) {
                _last = false;
                return true;
            }
            return _startingSquares.hasNext()
                    || _spearThrows.hasNext()
                    || _pieceMoves.hasNext()
                    || _last;
        }

        @Override
        public Move next() {
            Move m = Move.mv(_start, _nextSquare, _spear);
            toNext();
            return m;
        }

        /** Advance so that the next valid Move is
         *  _start-_nextSquare(sp), where sp is the next value of
         *  _spearThrows. */
        private void toNext() {
            if (!_pieceMoves.hasNext() && !_spearThrows.hasNext()) {
                if (_startingSquares.hasNext()) {
                    _start = _startingSquares.next();
                } else {
                    return;
                }
                while (_start.piece() != _fromPiece
                        && _startingSquares.hasNext()) {
                    _start = _startingSquares.next();
                }
                if (_start.piece() == _fromPiece) {
                    _pieceMoves = reachableFrom(_start, null);
                }
                toNext();
                return;
            }
            if (_pieceMoves.hasNext() && !_spearThrows.hasNext()) {
                _nextSquare = _pieceMoves.next();
                _spearThrows = reachableFrom(_nextSquare, _start);
                toNext();
                return;
            }
            if (_spearThrows.hasNext()) {
                _spear = _spearThrows.next();
                if (_spear == _j10) {
                    _last = true;
                } else {
                    _last = false;
                }
            }
        }

        /** Color of side whose moves we are iterating. */
        private Piece _fromPiece;
        /** Current starting square. */
        private Square _start;
        /** Remaining starting squares to consider. */
        private Iterator<Square> _startingSquares;
        /** Current piece's new position. */
        private Square _nextSquare;
        /** Remaining moves from _start to consider. */
        private Iterator<Square> _pieceMoves;
        /** Remaining spear throws from _piece to consider. */
        private Iterator<Square> _spearThrows;
        /** Current spear throw's new position. */
        private Square _spear;
        /** Is the current move the last case. */
        private boolean _last;
        /** Last square (j10). */
        private Square _j10;
    }

    @Override
    public String toString() {
        String ans = "";
        for (int r = Board.SIZE - 1; r >= 0; r--) {
            String line = "   ";
            for (int c = 0; c < Board.SIZE; c++) {
                Square s = Square.sq(c, r);
                if (c != Board.SIZE - 1) {
                    line += s.piece() + " ";
                } else {
                    line += s.piece();
                }
            }
            ans += line + "\n";
        }
        return ans;
    }

    /** An empty iterator for initialization. */
    private static final Iterator<Square> NO_SQUARES =
        Collections.emptyIterator();

    /** Piece whose turn it is (BLACK or WHITE). */
    private Piece _turn;
    /** Cached value of winner on this board, or EMPTY if it has not been
     *  computed. */
    private Piece _winner;
    /** Arraylist containing the list of played moves with
     * even indexes corresponding to WHITE and odd to BLACK.
     */
    private ArrayList<Move> _moves;
}
