import java.util.Comparator;
import java.util.Vector;


public class Board {
	final static public int BLANK = 0;
    final static public int PAWN = 1;
    final static public int KNIGHT = 2;
    final static public int BISHOP = 3;
    final static public int ROOK = 4;
    final static public int QUEEN = 5;
    final static public int KING = 6;
	
    public static boolean kingMoved;
    public static boolean[] rookMoved = {false, false};
    
    Vector<Move> allMoves = new Vector<Move> ();
    Vector<Move> possibleMoves = new Vector<Move> ();
    Vector<Move> possibleCaptures = new Vector<Move> ();
    public Vector<Vector<Move>> killerMoves;
    
    public boolean whiteWin;
    public boolean blackWin;
    public boolean endGame;
    
    public int enpassant;
    
	int[] chessBoard;
	
	static int[] valuableVictim = {0, 100, 350, 350, 525, 1000, 100000};
	static int[] index = {
		0, 12, 15, 10, 1, 6, 6
	};
	
	static int[] moveTable = {
		0, -1, 1, 10, -10, 0, -1, 1, 10, -10, -9, -11, 9, 11, 0, -8, 8, -12, 12, -19, 19, -21, 21, 0 
	};
	
	static int[] pieceValue = {
		0, 100, 350, 350, 525, 1000, 100000
	};
	
	static char[] pieceChar = {
		' ', 'P', 'N', 'B', 'R', 'Q', 'K'
	};
	
	public Board (){
		kingMoved = false;
		whiteWin = false;
		blackWin = false;
		endGame = false;
		enpassant = -1;
		int[] temp = {
			4, 2, 3, 5, 6, 3, 2, 4,	7, 7, 
			1, 1, 1, 1, 1, 1, 1, 1, 7, 7, 
			0, 0, 0, 0, 0, 0, 0, 0, 7, 7, 
			0, 0, 0, 0, 0, 0, 0, 0, 7, 7,  
			0, 0, 0, 0, 0, 0, 0, 0, 7, 7,  
			0, 0, 0, 0, 0, 0, 0, 0, 7, 7,  
			-1,-1,-1,-1,-1,-1,-1,-1,7, 7, 
			-4,-2,-3,-5,-6,-3,-2,-4,7, 7  
		};
		chessBoard = temp;
	}
	public String boardInString(){
		int position;
		String boardString = new String();
		for ( int i=0; i<8; i++){
			for ( int j=0; j<8; j++ ){
				position = i*10 + j;
				if (chessBoard[position] > 0)
					boardString += "b" + pieceChar[Math.abs(chessBoard[position])] + " ";
				else if (chessBoard[position] < 0)
					boardString += "w" + pieceChar[Math.abs(chessBoard[position])] + " ";
				else
					boardString += "__ ";
			}
		}
		return boardString;
		
	}
	public Board (Board cb){
		chessBoard = new int[80];
		for (int i=0; i<80; i++)
			chessBoard[i] = cb.chessBoard[i];
	}
	
	public void GeneratePieceMoves(int position,boolean player){
		int side;
		int moveIndex;
		int nextPosition;
		
		int piece = chessBoard[position];
		
		if(piece < 0){
			piece = -piece;
			side = -1;
		}else{
			side = 1;
		}
		
		switch (piece){
			case PAWN:
				nextPosition = position + (side * 10) + 1;
				if (nextPosition >= 0 && nextPosition < 80 ){
					int nextMove = chessBoard[nextPosition];
					if (side*piece*nextMove < 0 && nextMove != 7){
						Move move = new Move(position, nextPosition);
						if(nextMove < 0)
							nextMove = -nextMove;
							move.setMVVLVA(pieceValue[nextMove]-pieceValue[piece]);
						possibleCaptures.add(move);
					}
				}

				nextPosition = position + (side * 10) - 1;
				if (nextPosition >= 0 && nextPosition < 80 ){
					int nextMove = chessBoard[nextPosition];
					if (side*piece*nextMove < 0 && nextMove != 7){
						Move move = new Move(position, nextPosition); 
						if(nextMove < 0)
							nextMove = -nextMove;
							move.setMVVLVA(pieceValue[nextMove]-pieceValue[piece]);
						possibleCaptures.add(move);
					}
				}

				nextPosition = position + (side * 20);
				if (nextPosition >= 0 && nextPosition < 80 ){
					
					int temp = (side > 0) ? 1 : 6;
					if (chessBoard[nextPosition] == 0 &&
						(position / 10) == temp &&
						((side < 0 && chessBoard[position-10]==0) ||
						(side > 0 && chessBoard[position+10]==0)))
					{
						Move move = new Move(position, nextPosition);
						possibleMoves.add(move);
					}
				}

				nextPosition = position + (side * 10);
				if ( nextPosition >= 0 && nextPosition < 80 ){
					if (chessBoard[nextPosition] == 0){
						Move move = new Move(position, nextPosition);
						possibleMoves.add(move);
					}
				}
				break;
			default:
				moveIndex = index[piece];
				nextPosition = position + moveTable[moveIndex];
				
				outer: while(true){
					inner: while (true){
						if (nextPosition < 0 || nextPosition >= 80 || chessBoard[nextPosition] == 7)
							break inner;
							
						int nextMove = chessBoard[nextPosition];
						
						if (nextMove*side > 0){
							break inner;
						}
						
						Move move = new Move(position, nextPosition); 
						
						if ( nextMove != 0 ){
							if(nextMove < 0)
								nextMove = -nextMove;
							move.setMVVLVA(pieceValue[nextMove]-pieceValue[piece]);
							possibleCaptures.add(move);
							break inner;
						}else{
							possibleMoves.add(move);
							
						}
						
						if (piece == KNIGHT || piece == KING){
							break inner;
						}
						
						nextPosition += moveTable[moveIndex];
					}
					moveIndex++;
					if (moveTable[moveIndex] == 0){
						break outer;
					}
					nextPosition = position + moveTable[moveIndex];
				}
		}
	}
	
	public void GeneratePossibleMoves(boolean player){
		int position;
		for (int i=0; i<8; i++){
			for (int j=0; j<8; j++ ){
				position = i*10 + j;

				if (chessBoard[position] == 0)
					continue;
				
				if ((player && chessBoard[position] < 0) || (!player && chessBoard[position] > 0)){
					GeneratePieceMoves(position,player);
				}
			}
		}
		
		possibleCaptures.sort(new Comparator<Move>(){
			public int compare(Move a,Move b){
				if(a.MVVLVA > b.MVVLVA)
					return -1;
				else if(a.MVVLVA == b.MVVLVA)
					return 0;
				else
					return 1;
			}
		});
		/*
		possibleMoves.sort(new Comparator<Move>(){
			public int compare(Move a,Move b){
				if(a.value > b.value)
					return -1;
				else if(a.value == b.value)
					return 0;
				else
					return 1;
			}
		});
		*/
		allMoves.addAll(possibleCaptures);
		allMoves.addAll(possibleMoves);
		possibleCaptures.clear();
		possibleMoves.clear();
	}
	
	public void ClearMoves (){
		allMoves.clear();
	}
	

	
	public boolean ValidMoves (Move move){
		if (chessBoard[move.from] < 0){
			GeneratePieceMoves (move.from,true);
			if (possibleCaptures.contains(move) || possibleMoves.contains(move)){
				possibleCaptures.clear();
				possibleMoves.clear();
				return true;
			}
		}
		System.out.println("Invalid Move, please enter again!");
		possibleCaptures.clear();
		possibleMoves.clear();
		return false;
	}
	
	public void PrintBoard (){
		int position;
		String boardString = new String();
		boardString += "  1  2  3  4  5  6  7  8\n";
		for ( int i=0; i<8; i++){
			boardString += (i+1);
			boardString += " ";
			for ( int j=0; j<8; j++ ){
				position = i*10 + j;
				if (chessBoard[position] > 0)
					boardString += "b" + pieceChar[Math.abs(chessBoard[position])] + " ";
				else if (chessBoard[position] < 0)
					boardString += "w" + pieceChar[Math.abs(chessBoard[position])] + " ";
				else
					boardString += "__ ";
			}
			boardString += "\n";
		}
		System.out.println(boardString);
		/*
		boardString = new String();
		for ( int i=0; i<chessBoard.length; i++){
			if((i+1) % 10 == 0 && i!=0){
				if(chessBoard[i] >=0){
				boardString += " ";
				boardString += chessBoard[i];
				boardString += "\n";
				}else{
					boardString += chessBoard[i];
					boardString += "\n";
					
				}
			}else{
				if(chessBoard[i] >=0){
				boardString += " ";
				boardString += chessBoard[i];
				}else{
					boardString += chessBoard[i];
					
				}
			}
		}
		System.out.println(boardString);
		*/
	}
	
	public void MakeMove (Move move, boolean player){
		int from = chessBoard[move.from];
		int to = chessBoard[move.to];
		if (to == 6){
			whiteWin = true;
		}else if (to == -6){
			blackWin = true;
		}else if (from == -1 || from == 1){
			int temp1 = (from > 0) ? 1 : 6;
			int temp2 = (from > 0) ? 3 : 4;
			if ((move.from / 10) == temp1 && (move.to / 10) == temp2)
			{
				enpassant = move.to;
			}
		}else if (player && from == -1 && move.to < 8){
			chessBoard[move.to] = -5;
		}else if (!player && from == 1 && move.to >= 70){
			chessBoard[move.to] = 5;
		}else if (move.from == 70){
			rookMoved[0] = true;
		}else if (move.from == 77){
			rookMoved[1] = true;
		}else if (move.from == 74){
			kingMoved = true;
		}
			chessBoard[move.to] = from;
		chessBoard[move.from] = 0;
		enpassant = -1;
	}
	
	public boolean CastleMove (int position){
		if (!kingMoved){
			if (position == 70 && !rookMoved[0] && chessBoard[71] == 0 && chessBoard[72] == 0 && chessBoard[73] == 0){
				GeneratePossibleMoves(false);
				boolean canMove = true;
				outer: for (int i=71; i<74; i++){
					for (Move move: allMoves){
						if (move.to == i){
							canMove = false;
							break outer;
						}
					}
				}
				allMoves.clear();
				if (!canMove)
					return false;
				else{
					chessBoard[71] = 6;
					chessBoard[72] = 4;
					chessBoard[70] = 0;
					chessBoard[74] = 0;
					ClearMoves();
					kingMoved = true;
					rookMoved[0] = true;
					return true;
				}
			}else if (position == 77 && !rookMoved[1] && chessBoard[75] == 0 && chessBoard[76] == 0){
				GeneratePossibleMoves(false);
				boolean canMove = true;
				outer: for (int i=75; i<77; i++){
					for (Move move: allMoves){
						if (move.to == i){
							canMove = false;
							break outer;
						}
					}
				}
				allMoves.clear();
				if (!canMove)
					return false;
				else{
					chessBoard[76] = 6;
					chessBoard[75] = 4;
					chessBoard[77] = 0;
					chessBoard[74] = 0;
					ClearMoves();
					kingMoved = true;
					rookMoved[0] = true;
					return true;
				}
			}
		}
		return false;
	}
	public int DoublePawn(){
		int position;
		int whitePawnCount = 0;
		int blackPawnCount = 0;
		int value = 0;
		for(int j = 0;j<8;j++){
			for(int i = 0;i<8;i++){
				position = i*10+j;
				if(chessBoard[position] == 1){
					blackPawnCount++;
				}
				if(chessBoard[position] == -1){
					whitePawnCount++;
				}
			}
			if(whitePawnCount >= 2)
				value += whitePawnCount * 5;
			if(blackPawnCount >= 2)
				value -= blackPawnCount * 5;
			whitePawnCount = 0;
			blackPawnCount = 0;
		}
		return value;
	}
	public Board MovePiece (Move move, boolean player){
		Board cb = new Board(this);
		if (player && cb.chessBoard[move.from] == -1 && move.to < 8){
			cb.chessBoard[move.to] = -5;
		}else if (!player && cb.chessBoard[move.from] == 1 && move.to >= 70){
			cb.chessBoard[move.to] = 5;
		}else{
			cb.chessBoard[move.to] = cb.chessBoard[move.from];
		}
		cb.chessBoard[move.from] = 0;
		return cb;
	}
	
	static int KnightOutpost[] = {
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	    0, 0, 4, 8, 8, 4, 0, 0, 0, 0,
	    0, 4,17,26,26,17, 4, 0, 0, 0,
	    0, 8,26,35,35,26, 8, 0, 0, 0,
	    0, 4,17,17,17,17, 4, 0, 0, 0,
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0
	};
	
	static int BishopOutpost[] = {
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 5, 8, 8, 8, 8, 5, 0, 0, 0,
		0,10,21,21,21,21,10, 0, 0, 0,
		0, 5,10,10,10,10, 5, 0, 0, 0,
		0, 0, 5, 5, 5, 5, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0
	};
	
	static int pawnPieceTable[] = {	
		0,  0,  0,  0,  0,  0,  0,  0, 0, 0,
		50, 50, 50, 50, 50, 50, 50, 50, 0, 0,
		10, 10, 20, 30, 30, 20, 10, 10, 0, 0,
		5,  5, 10, 25, 25, 10,  5,  5, 0, 0,
		0,  0,  0, 20, 20,  0,  0,  0, 0, 0,
		5, -5,-10,  0,  0,-10, -5,  5, 0, 0,
		5, 10, 10,-20,-20, 10, 10,  5, 0, 0,
		0,  0,  0,  0,  0,  0,  0,  0, 0, 0
	};
	
	static int knightPieceTable[] = {	
		-50,-40,-30,-30,-30,-30,-40,-50, 0, 0,
		-40,-20,  0,  0,  0,  0,-20,-40, 0, 0,
		-30,  0, 10, 15, 15, 10,  0,-30, 0, 0,
		-30,  5, 15, 20, 20, 15,  5,-30, 0, 0,
		-30,  0, 15, 20, 20, 15,  0,-30, 0, 0,
		-30,  5, 10, 15, 15, 10,  5,-30, 0, 0,
		-40,-20,  0,  5,  5,  0,-20,-40, 0, 0,
		-50,-40,-30,-30,-30,-30,-40,-50, 0, 0
	};
	
	static int bishopPieceTable[] = {	
		-20,-10,-10,-10,-10,-10,-10,-20, 0, 0,
		-10,  0,  0,  0,  0,  0,  0,-10, 0, 0,
		-10,  0,  5, 10, 10,  5,  0,-10, 0, 0,
		-10,  5,  5, 10, 10,  5,  5,-10, 0, 0,
		-10,  0, 10, 10, 10, 10,  0,-10, 0, 0,
		-10, 10, 10, 10, 10, 10, 10,-10, 0, 0,
		-10,  5,  0,  0,  0,  0,  5,-10, 0, 0,
		-20,-10,-10,-10,-10,-10,-10,-20, 0, 0
	};
	
	static int rookPieceTable[] = {	
		0,  0,  0,  0,  0,  0,  0,  0, 0, 0,
		5, 10, 10, 10, 10, 10, 10,  5, 0, 0,
		-5,  0,  0,  0,  0,  0,  0, -5, 0, 0,
		-5,  0,  0,  0,  0,  0,  0, -5, 0, 0,
		-5,  0,  0,  0,  0,  0,  0, -5, 0, 0,
		-5,  0,  0,  0,  0,  0,  0, -5, 0, 0,
		-5,  0,  0,  0,  0,  0,  0, -5, 0, 0,
		0,  0,  0,  5,  5,  0,  0,  0, 0, 0
	};
	
	static int queenPieceTable[] = {	
		-20,-10,-10, -5, -5,-10,-10,-20, 0, 0,
		-10,  0,  0,  0,  0,  0,  0,-10, 0, 0,
		-10,  0,  5,  5,  5,  5,  0,-10, 0, 0,
		 -5,  0,  5,  5,  5,  5,  0, -5, 0, 0,
		  0,  0,  5,  5,  5,  5,  0, -5, 0, 0,
		-10,  5,  5,  5,  5,  5,  0,-10, 0, 0,
		-10,  0,  5,  0,  0,  0,  0,-10, 0, 0,
		-20,-10,-10, -5, -5,-10,-10,-20, 0, 0
	};
	
	static int kingMGPieceTable[] = {	
		-30,-40,-40,-50,-50,-40,-40,-30, 0, 0,
		-30,-40,-40,-50,-50,-40,-40,-30, 0, 0,
		-30,-40,-40,-50,-50,-40,-40,-30, 0, 0,
		-30,-40,-40,-50,-50,-40,-40,-30, 0, 0,
		-20,-30,-30,-40,-40,-30,-30,-20, 0, 0,
		-10,-20,-20,-20,-20,-20,-20,-10, 0, 0,
		 20, 20,  0,  0,  0,  0, 20, 20, 0, 0,
		 20, 30, 10,  0,  0, 10, 30, 20, 0, 0
	};
	
	static int kingEGPieceTable[] = {	
		-50,-40,-30,-20,-20,-30,-40,-50, 0, 0,
		-30,-20,-10,  0,  0,-10,-20,-30, 0, 0,
		-30,-10, 20, 30, 30, 20,-10,-30, 0, 0,
		-30,-10, 30, 40, 40, 30,-10,-30, 0, 0,
		-30,-10, 30, 40, 40, 30,-10,-30, 0, 0,
		-30,-10, 20, 30, 30, 20,-10,-30, 0, 0,
		-30,-30,  0,  0,  0,  0,-30,-30, 0, 0,
		-50,-30,-30,-30,-30,-30,-30,-50, 0, 0
	};
	public int KingSafety(boolean player){
		int value = 0;
		int kingPos = 0;
		int side = 0;
		int position = 0;
		int moveIndex = 0;
		int nextPosition = 0;
		if(player)
			side = -1;
		else
			side = 1;
		for (int i=0; i<8; i++){
			for (int j=0; j<8; j++){
				position = i*10 + j;
				if(chessBoard[position] == (6*side))
					kingPos = position;
			}
		}
		moveIndex = index[BISHOP];
		nextPosition = kingPos + moveTable[moveIndex];
		
		outer: while(true){
			inner: while (true){
				if (nextPosition < 0 || nextPosition >= 80 || chessBoard[nextPosition] == 7)
					break inner;
					
				int nextMove = chessBoard[nextPosition];
				
				if (nextMove*side > 0){
					break outer;
				}
				
				
				if ( nextMove != 0 ){
					if(nextMove < 0)
						nextMove = -nextMove;
					if(nextMove == BISHOP)
						value -= 10;
					else if(nextMove == QUEEN)
						value -= 30;
					break inner;
				}
				
				nextPosition += moveTable[moveIndex];
			}
			moveIndex++;
			if (moveTable[moveIndex] == 0){
				break outer;
			}
			nextPosition = position + moveTable[moveIndex];
		}
		moveIndex = index[ROOK];
		nextPosition = kingPos + moveTable[moveIndex];
		outer: while(true){
			inner: while (true){
				if (nextPosition < 0 || nextPosition >= 80 || chessBoard[nextPosition] == 7)
					break inner;
					
				int nextMove = chessBoard[nextPosition];
				
				if (nextMove*side > 0){
					break outer;
				}
				
				
				if ( nextMove != 0 ){
					if(nextMove < 0)
						nextMove = -nextMove;
					if(nextMove == ROOK)
						value -= 20;
					else if(nextMove == QUEEN)
						value -= 30;
					break inner;
				}
				
				nextPosition += moveTable[moveIndex];
			}
			moveIndex++;
			if (moveTable[moveIndex] == 0){
				break outer;
			}
			nextPosition = position + moveTable[moveIndex];
		}
		moveIndex = index[BISHOP];
		nextPosition = kingPos + moveTable[moveIndex];
		
		outer: while(true){
			inner: while (true){
				if (nextPosition < 0 || nextPosition >= 80 || chessBoard[nextPosition] == 7)
					break inner;
					
				int nextMove = chessBoard[nextPosition];
				
				if (nextMove*side > 0){
					break outer;
				}
				
				
				if ( nextMove != 0 ){
					if(nextMove < 0)
						nextMove = -nextMove;
					if(nextMove == BISHOP)
						value -= 10;
					else if(nextMove == QUEEN)
						value -= 30;
					break inner;
				}
				
				nextPosition += moveTable[moveIndex];
			}
			moveIndex++;
			if (moveTable[moveIndex] == 0){
				break outer;
			}
			nextPosition = position + moveTable[moveIndex];
		}
		moveIndex = index[KNIGHT];
		nextPosition = kingPos + moveTable[moveIndex];
		outer: while(true){
			inner: while (true){
				if (nextPosition < 0 || nextPosition >= 80 || chessBoard[nextPosition] == 7)
					break inner;
					
				int nextMove = chessBoard[nextPosition];
				
				if (nextMove*side > 0){
					break inner;
				}
				
				
				if ( nextMove != 0 ){
					if(nextMove < 0)
						nextMove = -nextMove;
					if(nextMove == KNIGHT)
						value -= 10;
					break inner;
				}
				
				nextPosition += moveTable[moveIndex];
			}
			moveIndex++;
			if (moveTable[moveIndex] == 0){
				break outer;
			}
			nextPosition = position + moveTable[moveIndex];
		}
		return value;
	}
	public int Evaluation(boolean player){
		int side, temp, posAdv = 0, kingSafety = 0, outpostAdv = 0, piece, totalValue = 0, position = 0, count = 0;
		for (int i=0; i<8; i++){
			for (int j=0; j<8; j++){
				position = i*10 + j;
				
				if (chessBoard[position] == 0)
					continue;
				
				count++;
				// Material Advantage

				piece = chessBoard[position];
				
				if (piece < 0){
					piece = -piece;
					totalValue -= pieceValue[piece];
					temp = position;
					side = -1;
				}else{
					totalValue += pieceValue[piece];
					temp = 77 - position;
					side = 1;
				}
				
				// Position Advantage
				
				switch (piece){
					case PAWN:
						posAdv += pawnPieceTable[temp]*side;
						break;
					case KNIGHT:
						posAdv += knightPieceTable[temp]*side;
						outpostAdv += KnightOutpost[temp]*side;
						break;
					case BISHOP:
						posAdv += bishopPieceTable[temp]*side;
						outpostAdv += BishopOutpost[temp]*side;
						break;
					case ROOK:
						posAdv += rookPieceTable[temp]*side;
						break;
					case QUEEN:
						posAdv += queenPieceTable[temp]*side;
						break;
					case KING:
						if (endGame)
							posAdv += kingEGPieceTable[temp]*side;
						else
							posAdv += kingMGPieceTable[temp]*side;
						break;
				}
			}
		}
		
		
		
		
		totalValue += posAdv;
		//totalValue += outpostAdv;
		
			
		if (count < 10){
			endGame = true;
			totalValue += KingSafety(player);
			totalValue += KingSafety(!player);
		}
		if (player){
			totalValue = -totalValue;
			totalValue -= DoublePawn();
		}else{
			totalValue += DoublePawn();
			
		}
		return totalValue;
	}
	
}
