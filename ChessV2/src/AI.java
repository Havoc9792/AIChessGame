import java.util.Hashtable;
import java.util.Vector;


public class AI {
	Move bestMove = new Move();
	int maxDepth;
	boolean noCapture = true;
	public void startSearch(Board cb, int depth, int alpha, int beta, boolean player){
		//AlphaBetaSearch( cb,  depth,  alpha,  beta,  player);
	}
	public int AlphaBetaSearch(Board cb, int depth, int alpha, int beta){
		int value = 0;
		value = MaxValue(cb,depth,alpha,beta);
		return value;
	}
	public int MaxValue(Board cb, int depth, int alpha, int beta){
		//AI
		if (depth == 0){
			
			return cb.Evaluation(false);
		}
		int value;
			value = -1000000;
		cb.GeneratePossibleMoves(false);
		int check = 0;
		for (Move nextMove: cb.allMoves){
			check++;
			value = Math.max(value, MinValue(cb.MovePiece(nextMove, false),depth-1,alpha,beta));
			
			if (value >= beta){
				return value;
			}
			if(value > alpha){
				alpha = value;
				if (depth == maxDepth){
					bestMove = nextMove;
				}
				
			}
		}
		//System.out.println("Max : " + check);
		cb.allMoves.clear();
		return value;
		
	}
	public int MinValue(Board cb, int depth, int alpha, int beta){
		//Player
		if (depth == 0){
			
			return cb.Evaluation(true);
		}
		int value;
			value = 1000000;
		cb.GeneratePossibleMoves(true);
		int check = 0;
		
		for (Move nextMove: cb.allMoves){
			check++;

			value = Math.min(value, MaxValue(cb.MovePiece(nextMove, true),depth-1,alpha,beta));
			
			if (value <= alpha){
				return value;
			}
			beta = Math.min(beta,value);
		}
		//System.out.println("Min : " + check);
		cb.allMoves.clear();
		return value;
	}
	
	public void SetDepth (int depth){
		maxDepth = depth;
	}
	
	public int GetDepth (){
		return maxDepth;
	}
}
