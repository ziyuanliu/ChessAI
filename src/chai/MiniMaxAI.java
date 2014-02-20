package chai;


import chesspresso.Chess;
import chesspresso.move.IllegalMoveException;
import chesspresso.position.Position;

public class MiniMaxAI implements ChessAI {
	
	public short getMove(Position position) {
		ChessMove move = maxVal(position,1);
		for(int i = 1; i < 6; i ++){
			ChessMove temp = maxVal(position,i);
			if (move.val < temp.val){
				move = temp;
			}
		}
		return move.move;
	}
	
	public ChessMove maxVal(Position pos, int maxDepth){

		if(pos.isTerminal()||maxDepth==0){
			return new ChessMove(utilHelper(pos),(short) 0);
		}

		ChessMove retval = new ChessMove(Integer.MIN_VALUE, (short) 0);
		
		for (short move: pos.getAllMoves()){
			try {
				pos.doMove(move);
				ChessMove tempMove = minVal(pos, maxDepth-1);

				if (tempMove.val>retval.val){
					retval.setVal(tempMove.val);
					retval.setMove(move);
				}
				pos.undoMove();
			} catch (IllegalMoveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return retval;
	}
	
	public ChessMove minVal(Position pos, int maxDepth){
//		System.out.println("min "+maxDepth);
		if(pos.isTerminal()||maxDepth==0){
			return new ChessMove(utilHelper(pos),(short) 0);
		}
		ChessMove retval = new ChessMove(Integer.MAX_VALUE, (short) 0);
		for (short move: pos.getAllMoves()){
			try {
				pos.doMove(move);
				ChessMove tempMove = maxVal(pos, maxDepth-1);
				if (tempMove.val<retval.val){
					retval.setVal(tempMove.val);
					retval.setMove(move);
				}
				pos.undoMove();
			} catch (IllegalMoveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return retval;
	}
	
	public int utilHelper(Position pos){
		if(pos.isStaleMate()){
			return 0;
		}
		
		if(pos.isMate()){ 
			if(pos.getToPlay()==Chess.WHITE){
				return Integer.MIN_VALUE;
			}else{
				return Integer.MAX_VALUE;
			} 
		}else{
			if(pos.getToPlay()==Chess.WHITE){
				return pos.getMaterial();
			}else{
				return -1*pos.getMaterial();
			}
		}
	}

	private class ChessMove{
		int val;
		short move;
		private ChessMove(int val, short move){
			this.val = val;
			this.move = move;
		}
		
		public void setVal(int val){
			this.val = val;
		}
		
		public void setMove(short move){
			this.move = move;
		}
		
		public String toString(){
			return "value "+val+" move "+move;
		}
	}
}
