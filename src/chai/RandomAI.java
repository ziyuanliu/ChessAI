package chai;

import java.util.Arrays;
import java.util.Random;

import chesspresso.position.Position;

public class RandomAI implements ChessAI {
	public short getMove(Position position) {
		short [] moves = position.getAllMoves();
		System.out.println("list of available moves are "+position);
		if(moves.length==0){
			return (short)0;
		}
		short move = moves[new Random().nextInt(moves.length)];
		
		return move;
	}
	

}
