package amazons;

import java.util.Iterator;
import static java.lang.Math.*;
import static amazons.Piece.*;

/** A Player that automatically generates moves.
 *  @author Nicholas Moy.
 */
class AI extends Player {

    /** A position magnitude indicating a win (for white if positive, black
     *  if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;
    /** 1st threshold to increase depth. */
    private static final int THRESHOLD1 = 20;
    /** 2nd threshold to increase depth. */
    private static final int THRESHOLD2 = 25;
    /** 3rd threshold to increase depth. */
    private static final int THRESHOLD3 = 35;
    /** 4th threshold to increase depth. */
    private static final int THRESHOLD4 = 40;

    /** A new AI with no piece or controller (intended to produce
     *  a template). */
    AI() {
        this(null, null);
    }

    /** A new AI playing PIECE under control of CONTROLLER. */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move move = findMove();
        _controller.reportMove(move);
        return move.toString();
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        if (_myPiece == WHITE) {
            findMove(b, maxDepth(b), true, 1, -INFTY, INFTY);
        } else {
            findMove(b, maxDepth(b), true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove, int sense,
                         int alpha, int beta) {
        if (depth == 0 || board.winner() != null) {
            return staticScore(board);
        }

        int best;
        Move bestM;
        if (sense == 1) {
            best = -INFTY;
        } else {
            best = INFTY;
        }
        bestM = null;
        Iterator<Move> iter = board.legalMoves(myPiece());
        Move next;
        while (iter.hasNext()) {
            next = iter.next();
            board.makeMove(next);
            int response = findMove(board, depth - 1,
                    false, sense * -1, alpha, beta);
            board.undo();
            if (sense == 1) {
                if (response >= best) {
                    best = response;
                    bestM = next;
                    alpha = max(alpha, response);
                    if (beta <= alpha) {
                        break;
                    }
                }
            } else if (sense == -1) {
                if (response <= best) {
                    best = response;
                    bestM = next;
                    beta = min(beta, response);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            if (saveMove) {
                _lastFoundMove = bestM;
            }
        }
        return best;
    }

    /** Return a heuristically determined maximum search depth
     *  based on characteristics of BOARD. */
    private int maxDepth(Board board) {
        int N = board.numMoves();
        if (N > THRESHOLD1) {
            return 2;
        } else if (N > THRESHOLD2) {
            return 3;
        } else if (N > THRESHOLD3) {
            return 4;
        } else if (N > THRESHOLD4) {
            return 5;
        }
        return 1;
    }


    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        Piece winner = board.winner();
        if (winner == BLACK) {
            return -WINNING_VALUE;
        } else if (winner == WHITE) {
            return WINNING_VALUE;
        }

        Iterator<Move> max = board.legalMoves(WHITE);
        Iterator<Move> min = board.legalMoves(BLACK);

        int wmoves = 0;
        int bmoves = 0;

        while (max.hasNext()) {
            wmoves += 1;
            max.next();
        }
        while (min.hasNext()) {
            bmoves += 1;
            min.next();
        }
        return wmoves - bmoves;
    }


}
