package chai;

import java.util.HashMap;

import chesspresso.position.Position;

public class ColorChessAI implements ChessAI{
	HashMap<Integer,TransNode> transTable;
	int color;
	int maxDepth;
	public ColorChessAI(int d){
		this.maxDepth = d;
		transTable = new HashMap<Integer,TransNode>();
	}
	@Override
	public short getMove(Position position) {
		// TODO Auto-generated method stub
		return 0;
	}

	class ChessMove{
		int val;
		short move;
		ChessMove(int val, short move){
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
	
	class TransNode{
		int value; 
		int depth;
		
		TransNode(int val, int dep){
			this.value = val;
			this.depth = dep;
		}
		
		public void setVal(int val){
			this.value = val;
		}
		
		public void setDepth(int dep){
			this.depth = dep;
		}
	}
}
