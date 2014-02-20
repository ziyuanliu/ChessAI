package chai;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import chai.ColorChessAI.ChessMove;
import chai.ColorChessAI.TransNode;
import chesspresso.Chess;
import chesspresso.game.Game;
import chesspresso.move.IllegalMoveException;
import chesspresso.move.Move;
import chesspresso.pgn.PGNReader;
import chesspresso.position.Position;

public class AlphaBetaAI extends ColorChessAI {
	ArrayList<Game> openingBook;
	int moveCtr;
	
	public AlphaBetaAI(int depth){
		super(depth);
		openingBook = new ArrayList<Game>();
		readOpening();
		moveCtr = 0; 
	}
	
	public void readOpening(){
		File f;
		try {
			f = new File("book.pgn");
			FileInputStream fis = new FileInputStream(f);
			PGNReader pgnReader = new PGNReader(fis, "book.pgn");

			//hack: we know there are only 120 games in the opening book
			for (int i = 0; i < 120; i++)  {
			  Game g = pgnReader.parseGame();
			  g.gotoStart();
			  openingBook.add(g); 
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public short getMove(Position position) {
		this.color = position.getToPlay();
		
		try{
			if(moveCtr<1){ 
				moveCtr++;
				System.out.println("begin");
				Game g = openingBook.get(new Random().nextInt(openingBook.size()));
				Position currentPos = g.getPosition();
			    while (currentPos.getToPlay()!=this.color){
			    	g.goForward();
			    	currentPos = g.getPosition();
			    }
				return g.getNextMove().getShortMoveDesc();
			}
			
			ChessMove move = alphabeta(position, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, this.color==position.getToPlay());
			System.out.println("Move picked is "+move);
			moveCtr++;
			return move.move;
		}catch (Exception e){
			e.printStackTrace();
			return (short)0;
		}
		
	}
	
	public ChessMove alphabeta(Position pos, int maxDepth, int alpha, int beta, boolean isMaximizing){
		if(transTable.containsKey(pos.hashCode())&&maxDepth<transTable.get(pos.hashCode()).depth){
			TransNode tempNode = transTable.get(pos.hashCode());
			return new ChessMove(tempNode.value-1,(short) 0);
		}
		
		if (maxDepth == 0 || pos.isTerminal()){
			int val = isMaximizing ? utilHelper(pos) : -1*utilHelper(pos);
			TransNode tempNode = new TransNode(val,maxDepth);
			transTable.put(pos.hashCode(), tempNode);
			return new ChessMove(val, (short) 0);
		}
		
		ChessMove retval = new ChessMove(isMaximizing ? 1 * Integer.MIN_VALUE : Integer.MAX_VALUE, (short) 0);
			
		for (short move: pos.getAllMoves()){
			try {
				pos.doMove(move);
				
				if(isMaximizing){
					ChessMove tempMove = alphabeta(pos, maxDepth-1, alpha, beta, false);
					alpha = Math.max(alpha, tempMove.val);
//					System.out.println("maximizing "+alpha+" "+beta+" "+tempMove.val);
					pos.undoMove();
					
					if (tempMove.val>retval.val){
						retval.setVal(tempMove.val);
						retval.setMove(move);
					}
					
					if(beta<=alpha){
						retval.setVal(tempMove.val);
						retval.setMove(move);
						break;
					}

				}else{
					ChessMove tempMove = alphabeta(pos, maxDepth-1, alpha, beta, true);
					beta = Math.min(beta, tempMove.val);
					pos.undoMove();
					
					if (tempMove.val<retval.val){
						retval.setVal(tempMove.val);
						retval.setMove(move);
					}
					
					if(beta<=alpha){
						retval.setVal(tempMove.val);
						retval.setMove(move);
						break;
					}

				}
				
				
			} catch (IllegalMoveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return retval;
	}
	public ChessMove maxVal(Position pos, int maxDepth, int alpha, int beta){
		if(transTable.containsKey(pos.hashCode())&&maxDepth<transTable.get(pos.hashCode()).depth){
			TransNode tempNode = transTable.get(pos.hashCode());
			return new ChessMove(tempNode.value,(short) 0);
		}
		
		if(pos.isTerminal()||maxDepth==0){
			int val = utilHelper(pos);
			TransNode tempNode = new TransNode(val,maxDepth);
			transTable.put(pos.hashCode(), tempNode);
			return new ChessMove(val,(short) 0);
		}

		ChessMove retval = new ChessMove(Integer.MIN_VALUE, (short) 0);
		for (short move: pos.getAllMoves()){
			try {
				pos.doMove(move);
				ChessMove tempMove = minVal(pos, maxDepth-1, alpha, beta);
				if(Move.isPromotion(move)){
					tempMove.val = Integer.MAX_VALUE-1;
				}
				if (tempMove.val>retval.val){
//					System.out.println("new max val "+tempMove+" "+move);
					retval.setVal(tempMove.val);
					retval.setMove(move);
				}
				pos.undoMove();
				
				if (retval.val>=beta){
					TransNode tempNode = new TransNode(retval.val,maxDepth);
					transTable.put(pos.hashCode(), tempNode);
					return retval;
				}
				
				alpha = Math.max(retval.val, alpha);
			} catch (IllegalMoveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		TransNode tempNode = new TransNode(retval.val,maxDepth);
		transTable.put(pos.hashCode(), tempNode);
		return retval;
	}
	
	public ChessMove minVal(Position pos, int maxDepth,int alpha, int beta){
		
		if(transTable.containsKey(pos.hashCode())&&maxDepth<transTable.get(pos.hashCode()).depth){
			TransNode tempNode = transTable.get(pos.hashCode());
			return new ChessMove(tempNode.value,(short) 0);
		}
		
		if(pos.isTerminal()||maxDepth==0){
			int val = utilHelper(pos);
			TransNode tempNode = new TransNode(val,maxDepth);
			transTable.put(pos.hashCode(), tempNode);
			return new ChessMove(val,(short) 0);
		}
		
		ChessMove retval = new ChessMove(Integer.MAX_VALUE, (short) 0);
		for (short move: pos.getAllMoves()){
			try {
				pos.doMove(move);
				ChessMove tempMove = maxVal(pos, maxDepth-1, alpha, beta);
				if(Move.isPromotion(move)){
					tempMove.val = Integer.MIN_VALUE+1;
				}
				
				
				if (tempMove.val<retval.val){
					retval.setVal(tempMove.val);
					retval.setMove(move);
				}
				
				pos.undoMove();
				
				if (retval.val<=alpha){
					TransNode tempNode = new TransNode(retval.val,maxDepth);
					transTable.put(pos.hashCode(), tempNode);
					return retval;
				}
				
				beta = Math.min(retval.val, beta);

			} catch (IllegalMoveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		TransNode tempNode = new TransNode(retval.val,maxDepth);
		transTable.put(pos.hashCode(), tempNode);
		return retval;
	}
	
	public int utilHelper(Position pos){
		if(pos.isStaleMate()){
			return 0;
		}
		if(pos.isMate()){
			return Integer.MIN_VALUE;
		}
		
		return (int) (pos.getMaterial()+pos.getDomination()/2);
	}

}
