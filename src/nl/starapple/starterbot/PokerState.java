package nl.starapple.starterbot;

import java.util.HashMap;
import java.util.Map;
import nl.starapple.poker.Card;
import nl.starapple.poker.Hand;

public class PokerState {
	
	private int round, smallBlind, bigBlind;
	
	private boolean onButton;
	
	private int yourStack, opponentStack;
	
	private int pot;
	
        
        private String oppenentLastAction = "";
	private String opponentAction;
        public String myAction = "";
	
	private int currentBet;
	
	private Hand hand;
        public Hand OpHand;
	
	private Card[] table;
        
        private Card[] tableFlop;
        private Card[] tableTurn;
        private Card[] tableRiver;
        
        
	
	private Map<String,String> settings = new HashMap<String,String>();
	
	private String myName = "";
	
	private int[] sidepots;
        
        public String playstyle = "unknown";

    
        
        public int raises = 1;
        public int checks = 1;
        public int folds = 1;
        public int calls = 1;

    
        public int actionsMade = 1;
        
        
        public int seenFlop = 1;
        public int seenTurn = 1;
        public int seenRiver = 1;
        
        
        public int preFlopRaise = 1;
        public int preFlopCheck = 1;
        public int preFlopFold = 1;
        public int preFlopCall = 1;
        public int preFlopCheckRaise = 1;
        public int preFlopRaiseCall = 1;
        public int preFlopRaiseFold = 1;
        public int preFlopRaiseRaise = 1;
        
        
        public int flopRaise = 1;
        public int flopCheck = 1;
        public int flopFold = 1;
        public int flopCall = 1;
        public int flopCheckRaise = 1;
        public int flopRaiseCall = 1;
        public int flopRaiseFold = 1;
        public int flopRaiseRaise = 1;
        
        
        public int turnRaise = 1;
        public int turnCheck = 1;
        public int turnFold = 1;
        public int turnCall = 1;
        public int turnCheckRaise = 1;
        public int turnRaiseCall = 1;
        public int turnRaiseFold = 1;
        public int turnRaiseRaise = 1;
        
        
        public int riverRaise = 1;
        public int riverCheck = 1;
        public int riverFold = 1;
        public int riverCall = 1;
        public int riverCheckRaise = 1;
        public int riverRaiseCall = 1;
        public int riverRaiseFold = 1;
        public int riverRaiseRaise = 1;
        
        
        public int handsWon = 0;
        public int handsLost = 0;
        public int handsBullied = 0;
        
        public int preFlopActions = 1;
        public int flopActions = 1;
        public int turnActions = 1;
        public int riverActions = 1;
        
        
        
        
       
        
        
	
	protected void updateSetting(String key, String value) {
		settings.put(key, value);
		if( key.equals("yourBot") ) {
			myName = value;
		}
	}
        
        private int bettingRound =0;

	protected void updateMatch(String key, String value) {
		if( key.equals("round") ) {
			round = Integer.valueOf(value);
                        table = new Card[0];
                        bettingRound = 0;
                        
		} else if( key.equals("smallBlind") ) {
			smallBlind = Integer.valueOf(value);
		} else if( key.equals("bigBlind") ) {
			bigBlind = Integer.valueOf(value);
		} else if( key.equals("onButton") ) {
			onButton = value.equals(myName);
		} else if( key.equals("pot") ) {
			pot = Integer.valueOf(value);
		} else if( key.equals("table") ) {
                        bettingRound = 0;
			table = parseCards(value);
		} else if( key.equals("sidepots") ) {
                        bettingRound++;
			sidepots = parsePots(value);
                        currentBet = ( sidepots.length > 0 ) ? sidepots[0] : 0;
		} else {
			System.err.printf("Unknown match command: %s %s\n", key, value);
		}
	}

	protected void updateMove(String bot, String move, String amount) {
		if( bot.equals(myName) ) {
			if( move.equals("stack") ) {
				yourStack = Integer.valueOf(amount);
			} else if( move.equals("seat") ) {
				// ignored for now
			} else if( move.equals("hand") ) {
				Card[] cards = parseCards(amount);
                                
                                
				assert( cards.length == 2 ) : String.format("Did not receive two cards, instead: ``%s''", amount);
				hand = new Hand(cards[0], cards[1]);
                                
                                
			} else {
				// assume someone made some move
                                if(move.equals("wins"))
                                {
                                    
                                    handsLost++;
                                }
                                
                                if(move.equals("fold"))
                                {
                                    
                                    if(opponentAction.equals("raise"))
                                    {
                                        handsBullied++;
                                        
                                    }
                                }
                                
                                
                                        
                                myAction = move;
                            
                            
                            
			}
		} else {
			// assume it's the (only) villain
                    
                        if( move.equals("hand") ) {
				Card[] cards = parseCards(amount);
                                
                                
				assert( cards.length == 2 ) : String.format("Did not receive two cards, instead: ``%s''", amount);
				OpHand = new Hand(cards[0], cards[1]);
                                
                                
                                
                                
			}else
			if( move.equals("stack") ) {
				opponentStack = Integer.valueOf(amount);
			} else 
                              if(move.equals("wins"))
                                {
                                    
                                    handsWon++;
                                }else if(move.equals("fold") || move.equals("check") || move.equals("call") || move.equals("raise")){
                                
                                actionsMade++;
                                opponentAction = move;
                                if(move.equals("fold"))
                                {
                                    folds++;
                                }
                                
                                if(table == null || table.length == 0)
                                {
                                     preFlopActions++;
                                     if(myAction.equals("raise") && move.equals("fold"))
                                     {
                                         preFlopRaiseFold++; 
                                         preFlopFold++;
                                     }
                                     
                                     if(myAction.equals("raise") && move.equals("call"))
                                     {
                                         preFlopRaiseCall++;
                                         calls++;
                                         preFlopCall++;
                                     }
                                     
                                     if(myAction.equals("raise") && move.equals("raise"))
                                     {
                                         preFlopRaiseRaise++; 
                                         raises++;
                                     }
                                     
                                     if(myAction.equals("call") && move.equals("raise"))
                                     {
                                         preFlopCheckRaise++;
                                         raises++;
                                     }
                                     
                                     if(myAction.equals("call") && move.equals("check"))
                                     {
                                         preFlopCheck++; 
                                         checks++;
                                     }
                                     
                                    
                                }else
                                
                                if(table.length == 3)
                                {
                                    flopActions++; 
                                    if(myAction.equals("raise") && move.equals("fold"))
                                     {
                                         flopRaiseFold++; 
                                         flopFold++;
                                     }
                                     
                                     if(myAction.equals("raise") && move.equals("call"))
                                     {
                                         flopRaiseCall++;
                                         calls++;
                                         flopCall++;
                                     }
                                     
                                     if(myAction.equals("raise") && move.equals("raise"))
                                     {
                                         flopRaiseRaise++; 
                                         raises++;
                                         flopRaise++;
                                     }
                                     
                                     if(myAction.equals("check") && move.equals("raise"))
                                     {
                                         flopCheckRaise++;
                                         raises++;
                                         flopRaise++;
                                     }
                                     
                                     if(myAction.equals("check") && move.equals("check"))
                                     {
                                         flopCheck++; 
                                         checks++;
                                     }
                                     
                                    
                                }else if(table.length == 4)
                                {
                                     
                                    turnActions++;
                                    if(myAction.equals("raise") && move.equals("fold"))
                                     {
                                         turnRaiseFold++; 
                                         turnFold++;
                                     }
                                     
                                     if(myAction.equals("raise") && move.equals("call"))
                                     {
                                         turnRaiseCall++;
                                         calls++;
                                         turnCall++;
                                     }
                                     
                                     if(myAction.equals("raise") && move.equals("raise"))
                                     {
                                         turnRaiseRaise++; 
                                         raises++;
                                         turnRaise++;
                                         
                                     }
                                     
                                     if(myAction.equals("check") && move.equals("raise"))
                                     {
                                         turnCheckRaise++;
                                         raises++;
                                         turnRaise++;
                                     }
                                     
                                     if(myAction.equals("check") && move.equals("check"))
                                     {
                                         turnCheck++; 
                                         checks++;
                                     }
                                     
                                    
                                }else if(table.length == 5)
                                {
                                     
                                    riverActions++;
                                    if(myAction.equals("raise") && move.equals("fold"))
                                     {
                                         riverRaiseFold++;
                                         riverFold++; 
                                     }
                                     
                                     if(myAction.equals("raise") && move.equals("call"))
                                     {
                                         riverRaiseCall++;
                                         calls++;
                                         riverCall++;
                                     }
                                     
                                     if(myAction.equals("raise") && move.equals("raise"))
                                     {
                                         riverRaiseRaise++; 
                                         raises++;
                                         riverRaise++;
                                     }
                                     
                                     if(myAction.equals("check") && move.equals("raise"))
                                     {
                                         riverCheckRaise++;
                                         raises++;
                                         riverRaise++;
                                     }
                                     
                                     if(myAction.equals("check") && move.equals("check"))
                                     {
                                         riverCheck++; 
                                         checks++;
                                         
                                     }
                                }
                                System.err.println("*****"+move+"********");
			}else{
                                     opponentAction = move;
                                }
		}
	}

	private int[] parsePots(String value) {
		if( value.endsWith("]") ) { value = value.substring(0, value.length()-1); }
		if( value.startsWith("[") ) { value = value.substring(1); }
		if( value.length() == 0 ) { return new int[0]; }
		String[] parts = value.split(",");
		int[] pots = new int[parts.length];
		for( int i = 0; i < parts.length; ++i ) {
			pots[i] = Integer.valueOf(parts[i]);
		}
		return pots;
	}

	private Card[] parseCards(String value) {
		if( value.endsWith("]") ) { value = value.substring(0, value.length()-1); }
		if( value.startsWith("[") ) { value = value.substring(1); }
		if( value.length() == 0 ) { return new Card[0]; }
		String[] parts = value.split(",");
		Card[] cards = new Card[parts.length];
		for( int i = 0; i < parts.length; ++i ) {
			cards[i] = Card.getCard(parts[i]);
		}
		return cards;
	}

	public int getRound() {
		return round;
	}

	public int getSmallBlind() {
		return smallBlind;
	}

	public int getBigBlind() {
		return bigBlind;
	}
        
        public int getBettingRound(){
            
            return bettingRound;
        }
        

	public boolean onButton() {
		return onButton;
	}

	public int getYourStack() {
		return yourStack;
	}

	public int getOpponentStack() {
		return opponentStack;
	}
	
	public int getPot() {
		return pot;
	}
	
	public String getOpponentAction() {
		return opponentAction;
	}
	
	public int getCurrentBet() {
		return currentBet;
	}

	public Hand getHand() {
		return hand;
	}

	public Card[] getTable() {
		return table;
	}
	
	public String getSetting(String key) {
		return settings.get(key);
	}

	public int[] getSidepots() {
		return sidepots;
	}
        
        public int getRaiseFolds()
        {
            
            return preFlopRaiseFold + flopRaiseFold + turnRaiseFold + riverRaiseFold;
        }
        
        public int getCheckCheck()
        {
            
            return preFlopCheck+ flopCheck + turnCheck + riverCheck;
        }
        
        public int getCheckRaise()
        {
            
            return preFlopCheckRaise+ flopCheckRaise + turnCheckRaise + riverCheckRaise;
        }
        
        
        
        public double getPer(int action)
        {
            return  (((double)action/(double)actionsMade))*100;
        }
        
        public String getPlaystyle() {
        return playstyle;
    }

    public int getRaises() {
        return raises;
    }

    public int getChecks() {
        return checks;
    }

    public int getFolds() {
        return folds;
    }

    public int getSeenFlop() {
        return seenFlop;
    }

    public int getSeenTurn() {
        return seenTurn;
    }

    public int getSeenRiver() {
        return seenRiver;
    }

    public int getPreFlopRaise() {
        return preFlopRaise;
    }

    public int getPreFlopCheck() {
        return preFlopCheck;
    }

    public int getPreFlopFold() {
        return preFlopFold;
    }

    public int getPreFlopCheckRaise() {
        return preFlopCheckRaise;
    }

    public int getPreFlopRaiseCall() {
        return preFlopRaiseCall;
    }

    public int getPreFlopRaiseFold() {
        return preFlopRaiseFold;
    }

    public int getPreFlopRaiseRaise() {
        return preFlopRaiseRaise;
    }

    public int getFlopRaise() {
        return flopRaise;
    }

    public int getFlopCheck() {
        return flopCheck;
    }

    public int getFlopFold() {
        return flopFold;
    }

    public int getFlopCheckRaise() {
        return flopCheckRaise;
    }

    public int getFlopRaiseCall() {
        return flopRaiseCall;
    }

    public int getFlopRaiseFold() {
        return flopRaiseFold;
    }

    public int getFlopRaiseRaise() {
        return flopRaiseRaise;
    }

    public int getTurnRaise() {
        return turnRaise;
    }

    public int getTurnCheck() {
        return turnCheck;
    }

    public int getTurnFold() {
        return turnFold;
    }

    public int getTurnCheckRaise() {
        return turnCheckRaise;
    }

    public int getTurnRaiseCall() {
        return turnRaiseCall;
    }

    public int getTurnRaiseFold() {
        return turnRaiseFold;
    }

    public int getTurnRaiseRaise() {
        return turnRaiseRaise;
    }

    public int getRiverRaise() {
        return riverRaise;
    }

    public int getRiverCheck() {
        return riverCheck;
    }

    public int getRiverFold() {
        return riverFold;
    }

    public int getRiverCheckRaise() {
        return riverCheckRaise;
    }

    public int getRiverRaiseCall() {
        return riverRaiseCall;
    }

    public int getRiverRaiseFold() {
        return riverRaiseFold;
    }

    public int getRiverRaiseRaise() {
        return riverRaiseRaise;
    }

    public int getHandsWon() {
        return handsWon;
    }

    public int getHandsLost() {
        return handsLost;
    }

    public int getHandsBullied() {
        return handsBullied;
    }

    public void setPlaystyle(String playstyle) {
        this.playstyle = playstyle;
    }

    public void setRaises(int raises) {
        this.raises = raises;
    }

    public void setChecks(int checks) {
        this.checks = checks;
    }

    public void setFolds(int folds) {
        this.folds = folds;
    }

    public void setSeenFlop(int seenFlop) {
        this.seenFlop = seenFlop;
    }

    public void setSeenTurn(int seenTurn) {
        this.seenTurn = seenTurn;
    }

    public void setSeenRiver(int seenRiver) {
        this.seenRiver = seenRiver;
    }

    public void setPreFlopRaise(int preFlopRaise) {
        this.preFlopRaise = preFlopRaise;
    }

    public void setPreFlopCheck(int preFlopCheck) {
        this.preFlopCheck = preFlopCheck;
    }

    public void setPreFlopFold(int preFlopFold) {
        this.preFlopFold = preFlopFold;
    }

    public void setPreFlopCheckRaise(int preFlopCheckRaise) {
        this.preFlopCheckRaise = preFlopCheckRaise;
    }

    public void setPreFlopRaiseCall(int preFlopRaiseCall) {
        this.preFlopRaiseCall = preFlopRaiseCall;
    }

    public void setPreFlopRaiseFold(int preFlopRaiseFold) {
        this.preFlopRaiseFold = preFlopRaiseFold;
    }

    public void setPreFlopRaiseRaise(int preFlopRaiseRaise) {
        this.preFlopRaiseRaise = preFlopRaiseRaise;
    }

    public void setFlopRaise(int flopRaise) {
        this.flopRaise = flopRaise;
    }

    public void setFlopCheck(int flopCheck) {
        this.flopCheck = flopCheck;
    }

    public void setFlopFold(int flopFold) {
        this.flopFold = flopFold;
    }

    public void setFlopCheckRaise(int flopCheckRaise) {
        this.flopCheckRaise = flopCheckRaise;
    }

    public void setFlopRaiseCall(int flopRaiseCall) {
        this.flopRaiseCall = flopRaiseCall;
    }

    public void setFlopRaiseFold(int flopRaiseFold) {
        this.flopRaiseFold = flopRaiseFold;
    }

    public void setFlopRaiseRaise(int flopRaiseRaise) {
        this.flopRaiseRaise = flopRaiseRaise;
    }

    public void setTurnRaise(int turnRaise) {
        this.turnRaise = turnRaise;
    }

    public void setTurnCheck(int turnCheck) {
        this.turnCheck = turnCheck;
    }

    public void setTurnFold(int turnFold) {
        this.turnFold = turnFold;
    }

    public void setTurnCheckRaise(int turnCheckRaise) {
        this.turnCheckRaise = turnCheckRaise;
    }

    public void setTurnRaiseCall(int turnRaiseCall) {
        this.turnRaiseCall = turnRaiseCall;
    }

    public void setTurnRaiseFold(int turnRaiseFold) {
        this.turnRaiseFold = turnRaiseFold;
    }

    public void setTurnRaiseRaise(int turnRaiseRaise) {
        this.turnRaiseRaise = turnRaiseRaise;
    }

    public void setRiverRaise(int riverRaise) {
        this.riverRaise = riverRaise;
    }

    public void setRiverCheck(int riverCheck) {
        this.riverCheck = riverCheck;
    }

    public void setRiverFold(int riverFold) {
        this.riverFold = riverFold;
    }

    public void setRiverCheckRaise(int riverCheckRaise) {
        this.riverCheckRaise = riverCheckRaise;
    }

    public void setRiverRaiseCall(int riverRaiseCall) {
        this.riverRaiseCall = riverRaiseCall;
    }

    public void setRiverRaiseFold(int riverRaiseFold) {
        this.riverRaiseFold = riverRaiseFold;
    }

    public void setRiverRaiseRaise(int riverRaiseRaise) {
        this.riverRaiseRaise = riverRaiseRaise;
    }

    public void setHandsWon(int handsWon) {
        this.handsWon = handsWon;
    }

    public void setHandsLost(int handsLost) {
        this.handsLost = handsLost;
    }

    public void setHandsBullied(int handsBullied) {
        this.handsBullied = handsBullied;
    }
    
    public int getActionsMade() {
        return actionsMade;
    }

    public void setActionsMade(int actionsMade) {
        this.actionsMade = actionsMade;
    }
}
