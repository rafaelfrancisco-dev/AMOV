package com.a21230528.chess.Logic;

import static com.a21230528.chess.Utils.Utils.isIntInArray;

/**
 * Created by rafaelfrancisco on 29/12/17.
 */

public class ChessMoves {

    private static final String[] cols = {"a", "b", "c", "d", "e", "f", "g", "h"};

    public static boolean movePiece(int piece, int postitionTo, SpaceAdapter adapter, boolean isAI){
        if (piece == postitionTo)
            return false;

        if (adapter.whoAmI == adapter.currentPlayer || isAI){
            Piece temp = adapter.pieces[piece];

            if (isColor(adapter.currentPlayer, temp)){
                int currentLine = getWhichLine(piece);

                switch (temp){
                    case PAWN_WHITE:
                        if (!canPawnMoveWhite(piece, postitionTo, adapter.pieces) || getWhichLine(postitionTo) != (currentLine - 1)){
                            return false;
                        }
                        break;
                    case PAWN_BLACK:
                        if (!canPawnMoveBlack(piece, postitionTo, adapter.pieces) || getWhichLine(postitionTo) != (currentLine + 1)){
                            return false;
                        }
                        break;
                    case ROOK_WHITE:
                        if (!canRookMove(piece, postitionTo, adapter.pieces)){
                            return false;
                        }
                        break;
                    case ROOK_BLACK:
                        if (!canRookMove(piece, postitionTo, adapter.pieces)){
                            return false;
                        }
                        break;
                    case QUEEN_WHITE:
                        if (!canBishopMove(piece, postitionTo, adapter.pieces) && !canRookMove(piece, postitionTo, adapter.pieces)){
                            return false;
                        }
                        break;
                    case QUEEN_BLACK:
                        if (!canBishopMove(piece, postitionTo, adapter.pieces) && !canRookMove(piece, postitionTo, adapter.pieces)){
                            return false;
                        }
                        break;
                    case KING_WHITE:
                        if (!canKingMove(piece, postitionTo, adapter.pieces)){
                            return false;
                        }
                        break;
                    case KING_BLACK:
                        if (!canKingMove(piece, postitionTo, adapter.pieces)){
                            return false;
                        }
                        break;
                    case KNIGHT_WHITE:
                        if (!canKnightMove(piece, postitionTo, adapter.pieces)){
                            return false;
                        }
                        break;
                    case KNIGHT_BLACK:
                        if (!canKnightMove(piece, postitionTo, adapter.pieces)){
                            return false;
                        }
                        break;
                    case BISHOP_WHITE:
                        if (!canBishopMove(piece, postitionTo, adapter.pieces)){
                            return false;
                        }
                        break;
                    case BISHOP_BLACK:
                        if (!canBishopMove(piece, postitionTo, adapter.pieces)){
                            return false;
                        }
                        break;
                    case EMPTY:
                        return false;
                }

                //Generate move message
                adapter.lastMessage = moveDescription(piece, postitionTo, adapter);

                if (adapter.pieces[postitionTo] != Piece.EMPTY){
                    //Eats piece
                    if (adapter.currentPlayer == Player.WHITE){
                        adapter.eatenWHITE.add(adapter.pieces[postitionTo]);
                    } else {
                        adapter.eatenBLACK.add(adapter.pieces[postitionTo]);
                    }
                }

                adapter.pieces[piece] = Piece.EMPTY;
                adapter.pieces[postitionTo] = temp;

                if (getWhichLine(postitionTo) == 0 && temp == Piece.PAWN_WHITE){
                    adapter.pieces[postitionTo] = Piece.QUEEN_WHITE;
                }

                if (getWhichLine(postitionTo) == 7 && temp == Piece.PAWN_BLACK){
                    adapter.pieces[postitionTo] = Piece.QUEEN_BLACK;
                }

                if (adapter.currentPlayer == Player.WHITE)
                    adapter.currentPlayer = Player.BLACK;
                else
                    adapter.currentPlayer = Player.WHITE;

                adapter.currentTurn++;

                return true;
            }
        }

        return false;
    }

    private static boolean isColor(Player player, Piece piece){
        if (Player.WHITE == player){
            switch (piece){
                case PAWN_WHITE:
                    return true;
                case ROOK_WHITE:
                    return true;
                case QUEEN_WHITE:
                    return true;
                case KING_WHITE:
                    return true;
                case KNIGHT_WHITE:
                    return true;
                case BISHOP_WHITE:
                    return true;
            }

            return false;
        } else {
            switch (piece) {
                case PAWN_BLACK:
                    return true;
                case ROOK_BLACK:
                    return true;
                case QUEEN_BLACK:
                    return true;
                case KING_BLACK:
                    return true;
                case KNIGHT_BLACK:
                    return true;
                case BISHOP_BLACK:
                    return true;
            }

            return false;
        }
    }

    private static int[] getLineOfEight(int line){
        int[] temp = new int[8];
        int startingNumber = line * 8;

        for (int i = 0; i < temp.length; i++){
            temp[i] = startingNumber;
            startingNumber++;
        }

        return temp;
    }

    private static int[] getColumnOfEight(int column){
        int[] temp = new int[8];

        for (int i = 0; i < temp.length; i++){
            temp[i] = column;
            column = column + 8;
        }

        return temp;
    }

    private static int[][] getFullMatrix(){
        int[][] fullMatrix = new int[8][8];

        for (int i = 0; i < 8; i++){
            fullMatrix[i] = getLineOfEight(i);
        }

        return fullMatrix;
    }

    private static int getWhichLine(int pos){
        int counter = 0;

        for (int i = 0; i < 8; i++){
            if (isIntInArray(getLineOfEight(i), pos)){
                return counter;
            }

            counter++;
        }

        return counter;
    }

    private static int getWhichColumn(int pos){
        int counter = 0;

        for (int i = 0; i < 8; i++){
            if (isIntInArray(getColumnOfEight(i), pos)){
                return counter;
            }

            counter++;
        }

        return counter;
    }

    private static boolean oppositeColor(Piece piece, Piece otherPiece) {
        return piece.name().contains("WHITE") && otherPiece.name().contains("BLACK") || piece.name().contains("BLACK") && otherPiece.name().contains("WHITE");

    }

    private static boolean canPawnMoveWhite(int pos, int posTo, Piece[] pieces){
        int difs = Math.abs(pos - posTo);

        if (pieces[pos - 7] != Piece.EMPTY && difs == 7 && oppositeColor(pieces[pos], pieces[pos - 7])){
            return true;
        }
        if (pieces[pos - 9] != Piece.EMPTY && difs == 9 && oppositeColor(pieces[pos], pieces[pos - 9])){
            return true;
        }

        return (pieces[posTo] == Piece.EMPTY && (pos - posTo) == 8);
    }

    private static boolean canPawnMoveBlack(int pos, int posTo, Piece[] pieces){
        int difs = Math.abs(pos - posTo);

        if (pieces[pos + 7] != Piece.EMPTY && difs == 7 && oppositeColor(pieces[pos], pieces[pos + 7])){
            return true;
        }
        if (pieces[pos + 9] != Piece.EMPTY && difs == 9 && oppositeColor(pieces[pos], pieces[pos + 9])){
            return true;
        }

        return (pieces[posTo] == Piece.EMPTY && (posTo - pos) == 8);
    }

    private static boolean canRookMove(int pos, int posTo, Piece[] pieces){
        int line = getWhichLine(pos);
        int column = getWhichColumn(pos);

        int lineTo = getWhichLine(posTo);
        int columnTo = getWhichColumn(posTo);

        Piece orignalPiece = pieces[pos];

        if (columnTo == column){
            if (posTo - pos > 0) {
                pos = pos + 8;

                while (pos >= 0 && pos <= 63 && pos != posTo) {
                    if (pieces[pos] == Piece.EMPTY) {
                        pos = pos + 8;
                    } else {
                        return false;
                    }
                }

                return pieces[pos] == Piece.EMPTY || oppositeColor(orignalPiece, pieces[pos]);
            } else {
                pos = pos - 8;

                while (pos >= 0 && pos <= 63 && pos != posTo){
                    if (pieces[pos] == Piece.EMPTY){
                        pos = pos - 8;
                    } else {
                        return false;
                    }
                }

                return pieces[pos] == Piece.EMPTY || oppositeColor(orignalPiece, pieces[pos]);
            }
        } else if (line == lineTo){
            if (posTo - pos > 0) {
                pos = pos + 1;

                while (pos >= 0 && pos <= 63 && pos != posTo) {
                    if (pieces[pos] == Piece.EMPTY) {
                        pos = pos + 1;
                    } else {
                        return false;
                    }
                }

                return pieces[pos] == Piece.EMPTY || oppositeColor(orignalPiece, pieces[pos]);
            } else {
                pos = pos - 1;

                while (pos >= 0 && pos <= 63 && pos != posTo){
                    if (pieces[pos] == Piece.EMPTY){
                        pos = pos - 1;
                    } else {
                        return false;
                    }
                }

                return pieces[pos] == Piece.EMPTY || oppositeColor(orignalPiece, pieces[pos]);
            }
        }

        return false;
    }

    private static boolean canBishopMove(int pos, int posTo, Piece[] pieces){
        Piece originalPiece = pieces[pos];
        int originalPos = pos;

        int[][] matrix = getFullMatrix();
        int x = 0, y = 0;
        int xTemp, yTemp;

        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                if (matrix[i][j] == pos){
                    x = i;
                    y = j;

                    break;
                }
            }
        }

        if (Math.abs(pos - posTo) == 1 || Math.abs(pos - posTo) == 8)
            return false;

        if (posTo > pos){
            xTemp = x;
            yTemp = y;

            if (getWhichColumn(pos) < getWhichColumn(posTo)){
                while (x < 8 && y < 8 && pos != posTo){
                    if (xTemp > 7 || xTemp < 0 || yTemp > 7 || yTemp < 0)
                        break;

                    pos = matrix[xTemp][yTemp];

                    if (pos == posTo)
                        return pieces[pos] == Piece.EMPTY || oppositeColor(originalPiece, pieces[posTo]);

                    if (pieces[pos] == Piece.EMPTY || pos == originalPos){
                        xTemp++;
                        yTemp++;
                    } else
                        return false;
                }
            } else {
                while (x < 8 && y < 8 && pos != posTo){
                    if (xTemp > 7 || xTemp < 0 || yTemp > 7 || yTemp < 0)
                        break;

                    pos = matrix[xTemp][yTemp];

                    if (pos == posTo)
                        return pieces[pos] == Piece.EMPTY || oppositeColor(originalPiece, pieces[posTo]);

                    if (pieces[pos] == Piece.EMPTY || pos == originalPos){
                        xTemp++;
                        yTemp--;
                    } else
                        return false;
                }
            }
        } else {
            xTemp = x;
            yTemp = y;

            if (getWhichColumn(pos) < getWhichColumn(posTo)){
                while (x < 8 && y < 8 && pos != posTo){
                    if (xTemp > 7 || xTemp < 0 || yTemp > 7 || yTemp < 0)
                        break;

                    pos = matrix[xTemp][yTemp];

                    if (pos == posTo)
                        return pieces[pos] == Piece.EMPTY || oppositeColor(originalPiece, pieces[posTo]);

                    if (pieces[pos] == Piece.EMPTY || pos == originalPos){
                        xTemp--;
                        yTemp++;
                    } else
                        return false;
                }
            } else {
                while (x < 8 && y < 8 && pos != posTo){
                    if (xTemp > 7 || xTemp < 0 || yTemp > 7 || yTemp < 0)
                        break;

                    pos = matrix[xTemp][yTemp];

                    if (pos == posTo)
                        return pieces[pos] == Piece.EMPTY || oppositeColor(originalPiece, pieces[posTo]);

                    if (pieces[pos] == Piece.EMPTY || pos == originalPos){
                        xTemp--;
                        yTemp--;
                    } else
                        return false;
                }
            }
        }

        return pieces[pos] == Piece.EMPTY || oppositeColor(originalPiece, pieces[posTo]);
    }

    private static boolean canKnightMove(int pos, int posTo, Piece[] pieces){
        if (posTo == (pos + 6) || posTo == (pos + 15) || posTo == (pos - 6) || posTo == (pos - 15)){
            if (pieces[posTo] == Piece.EMPTY)
                return true;
            else
                return oppositeColor(pieces[pos], pieces[posTo]);
        }

        if (posTo == (pos + 17) || posTo == (pos + 10) || posTo == (pos - 17) || posTo == (pos - 10)){
            if (pieces[posTo] == Piece.EMPTY)
                return true;
            else
                return oppositeColor(pieces[pos], pieces[posTo]);
        }

        return false;
    }

    private static boolean canKingMove(int pos, int posTo, Piece[] pieces){
        if (Math.abs(pos - posTo) <= 9)
            return pieces[posTo] == Piece.EMPTY || oppositeColor(pieces[pos], pieces[posTo]);
        else
            return false;
    }

    private static String moveDescription(int posFrom, int posTo, SpaceAdapter adapter) {
        return getLocalizedPieceName(adapter.pieces[posFrom]) + cols[getWhichColumn(posFrom)] + (getWhichLine(posFrom) + 1) + " " + getLocalizedPieceName(adapter.pieces[posTo]) + cols[getWhichColumn(posTo)] + (getWhichLine(posTo) + 1);
    }

    private static String getLocalizedPieceName(Piece piece){
        switch (piece) {
            case PAWN_WHITE:
                return "";
            case PAWN_BLACK:
                return "";
            case ROOK_WHITE:
                return "R";
            case ROOK_BLACK:
                return "R";
            case QUEEN_WHITE:
                return "Q";
            case QUEEN_BLACK:
                return "Q";
            case KING_WHITE:
                return "K";
            case KING_BLACK:
                return "K";
            case KNIGHT_WHITE:
                return "N";
            case KNIGHT_BLACK:
                return "N";
            case BISHOP_WHITE:
                return "B";
            case BISHOP_BLACK:
                return "B";
            case EMPTY:
                return "";
        }

        return "";
    }
}