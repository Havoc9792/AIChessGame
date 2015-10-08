import java.util.Scanner;


public class Chess {
	
	private static Scanner sc;

	public static void main (String[] args){
		Board cb = new Board();
		AI ac = new AI();
		ac.SetDepth(6);
		sc = new Scanner(System.in);
		int from, to;

		while (true){
			System.out.println(cb.Evaluation(true));
			
			if (cb.whiteWin){
				System.out.println("You Win");
				return;
			}else if (cb.blackWin){
				System.out.println("You Lose");
				return;
			}
			
			cb.PrintBoard();
			do{
				System.out.println("Please input [row][column]:");
				System.out.println("From:");
				from = sc.nextInt();
				int decimal = 0;
				int unit = 0;
				decimal = from / 10;
				unit = from % 10;
				from = (decimal-1) * 10 + (unit-1);
				System.out.println("To:");
				to = sc.nextInt();
				decimal = to / 10;
				unit = to % 10;
				to = (decimal-1) * 10 + (unit-1);
			}while (!cb.ValidMoves(new Move (from, to)));
			
			cb.MakeMove(new Move(from, to), true);
			//ac.startSearch(cb, ac.GetDepth(), -1000000, 1000000,false);
			System.out.println("AI is thinking!");
			ac.AlphaBetaSearch(cb, ac.GetDepth(), -1000000, 1000000);
			cb.MakeMove(ac.bestMove, false);
		}

	}
}
