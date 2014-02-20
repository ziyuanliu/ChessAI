package chai;

import chesspresso.move.IllegalMoveException;
import chesspresso.position.Position;

public class MiniMaxAI extends ColorChessAI {
	
	public MiniMaxAI(int d) {
		super(d);
		// TODO Auto-generated constructor stub
	}

	public short getMove(Position position) {
		this.color = position.getToPlay();
		ChessMove move = minimax(position, 1, color==position.getToPlay());
		for(int i = 1; i < this.maxDepth; i ++){
			ChessMove temp = minimax(position,i,color==position.getToPlay());
			if (move.val < temp.val){
				move = temp;
			}
		}
		return move.move;
	}
	
	public ChessMove minimax(Position pos, int maxDepth, boolean isMx){
		if(transTable.containsKey(pos.hashCode())&&maxDepth<transTable.get(pos.hashCode()).depth){
			TransNode tempNode = transTable.get(pos.hashCode());
			return new ChessMove(tempNode.value-1,(short) 0);
		}
		
		if (maxDepth == 0 || pos.isTerminal()){
			int val = isMx ? utilHelper(pos) : -1*utilHelper(pos);
			TransNode tempNode = new TransNode(val,maxDepth);
			transTable.put(pos.hashCode(), tempNode);
			return new ChessMove(val, (short) 0);
		}
		
		boolean isMaximizing = this.color==pos.getToPlay();
		
		ChessMove retval = new ChessMove(isMaximizing ? 1 * Integer.MIN_VALUE : Integer.MAX_VALUE, (short) 0);
			
		for (short move: pos.getAllMoves()){
			try {
				pos.doMove(move);
				ChessMove tempMove = minimax(pos, maxDepth-1, !isMaximizing);

				if(isMx){
					if (tempMove.val>retval.val){
						retval.setVal(tempMove.val);
						retval.setMove(move);
					}
				}else{
					if (tempMove.val<retval.val){
						retval.setVal(tempMove.val);
						retval.setMove(move);
					}
				}
				
				pos.undoMove();
			} catch (IllegalMoveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
//		TransNode tempNode = new TransNode(retval.val,maxDepth);
//		transTable.put(pos.hashCode(), tempNode);
		return retval;
	}
	
	
	public int utilHelper(Position pos){
		if(pos.isStaleMate()){
			return 0;
		}
		if(pos.isMate()){
			return Integer.MIN_VALUE;
		}
		
//		return (new Random()).nextInt();
		return (int) (pos.getMaterial()+pos.getDomination()/2);
	}
}
