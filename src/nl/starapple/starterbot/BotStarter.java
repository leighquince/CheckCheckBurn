package nl.starapple.starterbot;


import com.stevebrecher.*;
import nl.starapple.poker.*;

public class BotStarter implements Bot {

    public HandEval.HandCategory rankToCategory(int rank) {
        return HandEval.HandCategory.values()[rank >> HandEval.VALUE_SHIFT];
    }
    
    public static HandEval.HandCategory eval4cards(Card[] table) {

        long handCode = 0;
        for (Card card : table) {
            handCode += card.getNumber();
        }
        int[] cards = new int[12];
        int i = 0;
        for (Card card : table) {
            cards[i] = card.getHeight().ordinal();
            i++;
        }


        if (cards[0] == cards[1] && cards[0] == cards[2] && cards[0] == cards[3]) {
            return HandEval.HandCategory.FOUR_OF_A_KIND;
        } else if ((cards[0] == cards[1]
                && cards[0] == cards[2])
                || (cards[0] == cards[2]
                && cards[0] == cards[3])
                || (cards[0] == cards[1]
                && cards[0] == cards[3])
                || (cards[1] == cards[0]
                && cards[1] == cards[2])
                || (cards[1] == cards[2]
                && cards[1] == cards[3])
                || (cards[1] == cards[0]
                && cards[1] == cards[3])) {
            return HandEval.HandCategory.THREE_OF_A_KIND;
        } else if ((cards[0] == cards[1]
                && cards[2] == cards[3])
                || (cards[0] == cards[3]
                && cards[1] == cards[2])
                || (cards[0] == cards[2]
                && cards[1] == cards[3])) {

            return HandEval.HandCategory.TWO_PAIR;

        } else {
            return HandEval.HandCategory.NO_PAIR;
        }



    }

    public HandEval.HandCategory getHandCategory(Hand hand, Card[] table) {
        if (table == null || table.length == 0) {
            return hand.getCard1().getHeight() == hand.getCard2().getHeight()
                    ? HandEval.HandCategory.PAIR
                    : HandEval.HandCategory.NO_PAIR;
        }
        long handCode = hand.getCard1().getNumber() + hand.getCard2().getNumber();
        for (Card card : table) {
            handCode += card.getNumber();
        }
        if (table.length == 3) {
            return rankToCategory(HandEval.hand5Eval(handCode));
        }
        if (table.length == 4) {

            return rankToCategory(HandEval.hand6Eval(handCode));
        }
        return rankToCategory(HandEval.hand7Eval(handCode));
    }

    public HandEval.HandCategory getTableCategory(Card[] table) {

        long handCode = 0;
        for (Card card : table) {
            handCode += card.getNumber();
        }
        if (table.length == 3) {

            return eval3cards(table);
        }
        if (table.length == 4) {
            return eval4cards(table);
        }

        return rankToCategory(HandEval.hand5Eval(handCode));
    }

    @Override
    public PokerMove getMove(PokerState state, Long timeOut) {

        Hand hand = state.getHand();
        String handCategory = getHandCategory(hand, state.getTable()).toString();
        String tableCategory = getTableCategory(state.getTable()).toString();
        System.err.printf("\nmy hand is %s, opponent action is %s (%d)\nThe community has " + tableCategory + "\n", handCategory, state.getOpponentAction(), state.getCurrentBet());
        int height1 = hand.getCard1().getHeight().ordinal();
        int height2 = hand.getCard2().getHeight().ordinal();
        Card[] table = state.getTable();



            System.err.println("current bet: "+state.onButton());
            System.err.println("betting round: "+state.getBettingRound());
            System.err.println("Oppentent pre flop raise "+ makePercent(state.preFlopRaise,state.preFlopActions));
            System.err.println("Oppentent flop raise "+ makePercent(state.flopRaise,state.flopActions));
            System.err.println("Oppentent turn raise "+ makePercent(state.turnRaise,state.turnActions));
            System.err.println("Oppentent river raise "+ makePercent(state.riverRaise,state.riverActions));
            System.err.println("Oppentent preFlop blufable "+  ((double)state.preFlopRaiseCall
                                                           +(double)state.preFlopRaiseRaise
                                                            /(double)state.preFlopFold)*100);
            System.err.println("Oppentent flop blufable "+  ((double)state.flopRaiseCall
                                                           +(double)state.flopRaiseRaise
                                                            /(double)state.flopFold)*100);
            System.err.println("Oppentent turn blufable "+  ((double)state.turnRaiseCall
                                                           +(double)state.turnRaiseRaise
                                                            /(double)state.turnFold)*100);
            System.err.println("Oppentent river blufable "+  ((double)state.turnRaiseCall
                                                           +(double)state.turnRaiseRaise
                                                            /(double)state.turnFold)*100);
            
           
            
            System.err.println("Oppentent Actinos:"+ state.actionsMade);
            
            









        if (state.getTable() == null || state.getTable().length == 0) {

            //System.err.println("\nBetting Round 1");
            return bettingStatergyRound1(height1, height2, handCategory, 
                    tableCategory, state, cardsSuited(hand.getCard1().getSuit(),
                    hand.getCard2().getSuit()));
        } else {


            double EHS = EHS(hand,table);
            System.err.println("My hand strength: "+EHS);



            int random = (int) (Math.random() * 4) + 1;

            /*if(handCategory.equals("PAIR") && (height1 < 10 && height2 < 10) && state.getTable().length ==3)
            {
                return readPlayer(state, hand,table);

            }*/

            if (EHS >= 0.9) {

                switch (random) {
                    case 1:
                        return confident(state);

                    case 2:
                        return tease(state);

                    case 3:
                        return confident(state);

                    case 4:
                        return betPot(state, random);


                }


                return allIn(state);

            } else if (EHS >= 0.8) {
                if(state.getOpponentAction().equals("raise"))
                {
                    return new PokerMove("call",0);
                }

                random = (int) (Math.random() * 3) + 1;
                switch (random) {
                    case 1:
                        return tease(state);

                    case 2:
                        return confident(state);

                    case 3:
                        return betPot(state, 2);


                }

                return confident(state);

            } else if (EHS< 0.8) {

                return readPlayer(state, hand,table,EHS);

            } else if ((EHS >= 0.7 && state.getCurrentBet() < (state.getYourStack() / 6)) || (state.getYourStack() < 150 && state.getCurrentBet() > state.getYourStack())) {


                return new PokerMove("call", 0);

            } else {

                return new PokerMove("check", 0);
            }
        }
    }



    /**
     * @param args
     */
    public static void main(String[] args) {
        Parser parser = new Parser(new BotStarter());
        parser.run();


    }



    public static HandEval.HandCategory eval3cards(Card[] table) {
        long handCode = 0;
        for (Card card : table) {
            handCode += card.getNumber();
        }
        int[] cards = new int[12];
        int i = 0;
        for (Card card : table) {
            cards[i] = card.getHeight().ordinal();
            i++;
        }


        if (cards[0] == cards[1] && cards[0] == cards[2]) {
            return HandEval.HandCategory.THREE_OF_A_KIND;
        } else if (cards[0] == cards[1] || cards[0] == cards[2] || cards[1] == cards[2]) {
            return HandEval.HandCategory.PAIR;
        } else {
            return HandEval.HandCategory.NO_PAIR;
        }



    }


    public static boolean cardsSuited(CardSuit card1, CardSuit card2)
    {


        if(card1 == card2)
        {

            return true;
        }else{

            return false;
        }
    }


    public static boolean cardsConnected(int card1, int card2) {

        //System.err.print("\nCard 1: " + card1 + "\nCard 2: " + card2);
        if (card1 == card2) {
            return false;
        }

        if (card1 > card2) {
            if (card1 - card2 == 1) {
                return true;
            }
        } else {
            if (card2 - card1 == 1) {
                return true;
            }
        }

        return false;



    }

    public static PokerMove allIn(PokerState state) {
        return new PokerMove("raise", state.getYourStack() - state.getCurrentBet());

    }

    public static PokerMove check() {
        return new PokerMove("check", 0);

    }

    public static PokerMove call() {
        return new PokerMove("call", 0);

    }

    public static PokerMove fold() {
        return new PokerMove("fold", 0);

    }
    
    public static double makePercent(int number1, int number2)
    {
        
        return ((double)number1/(double)number2)*100;
    }

    public static PokerMove readPlayer(PokerState state,Hand hand, Card[] table,double EHS){
        
        //when on preflop
        if(table.length==0 || table == null)
        {
           
           if(state.getOpponentAction().equals("raise") && makePercent(state.preFlopRaise,state.preFlopActions) > 40     && state.getCurrentBet() < state.getYourStack()/4)
           {
               return call();
           }
           
            
           if(((double)state.preFlopRaiseCall+(double)state.preFlopRaiseRaise/(double)state.preFlopFold)*100 < 90)
           {
               return tease(state);
           }
           
           
            
            
            
        }
        //if oppent has checked or its my on button
                
            
        if(table.length == 3){


           if(state.getOpponentAction().equals("raise") && makePercent(state.flopRaise,state.flopActions) > 60 && state.getCurrentBet() < state.getYourStack()/4 && EHS > 0.4)
           {
               return call();
           }
                //see how often they fold when raised
            if(((double)state.flopRaiseCall+(double)state.flopRaiseRaise/(double)state.flopFold)*100 < 90 && state.getRound() > 3)
            {
                return tease(state);
            }

            

        }

        if(table.length == 4){

            if(state.getOpponentAction().equals("raise") && makePercent(state.turnRaise,state.turnActions) > 60 && state.getCurrentBet() < state.getYourStack()/4 && EHS > 0.4)
           {
               return call();
           }

            //see how often they fold when raised
            if(((double)state.turnRaiseCall+(double)state.turnRaiseRaise/(double)state.turnFold)*100 < 90 && state.getRound() > 3)
            {
                return tease(state);
            }



        }

        if(table.length == 5){
            
            if(state.getOpponentAction().equals("raise") && makePercent(state.riverRaise,state.riverActions) > 80 && state.getCurrentBet() < state.getYourStack()/6 && EHS > 0.4)
           {
               return call();
           }

            if(state.getOpponentAction().equals("raise") && makePercent(state.flopRaise,state.flopActions) > 60 && state.getCurrentBet() < state.getYourStack()/4 && EHS > 0.4)
           {
               return call();
           }

            



        }


        
        return new PokerMove("check",0);
        

        



    }

    public static PokerMove tease(PokerState state) {
        return new PokerMove("raise", state.getBigBlind() <= state.getOpponentStack() / 5
                ? state.getOpponentStack() / 5
                : state.getBigBlind());

    }

    public static PokerMove causious(PokerState state) {
        return new PokerMove("raise", state.getBigBlind() + state.getBigBlind() / 6);

    }

    public static PokerMove confident(PokerState state) {
        return new PokerMove("raise", state.getBigBlind() <= state.getYourStack() / 4
                ? state.getYourStack() / 4
                : state.getBigBlind());

    }

    public static PokerMove betPot(PokerState state, int multiplier) {
        return new PokerMove("raise", state.getPot() != 0
                ? state.getPot() * multiplier
                : state.getBigBlind());

    }

    public static PokerMove RR(PokerState state)
    {
        int random = (int) (Math.random() * 4) + 1;
        switch (random) {
                    case 1:
                        return confident(state);

                    case 2:
                        return tease(state);

                    case 3:
                        return causious(state);

                    case 4:
                        return betPot(state, random);
                    default:
                        return causious(state);
                }

    }

    public static PokerMove RC(PokerState state)
    {
        int random = (int) (Math.random() * 4) + 1;

        //if oppenent raised call them
        if(state.getOpponentAction().equals("raise"))
        {

           return call();
        }


        switch (random) {
            case 1:
                return confident(state);

            case 2:
                return tease(state);

            case 3:
                return causious(state);

            case 4:
                return betPot(state, random);
            default:
                return causious(state);
                }

    }

    public static PokerMove CC(PokerState state)
    {
        return call();

    }

    public static PokerMove CF(PokerState state)
    {
                //if oppenent raised fold
                if(state.getOpponentAction().equals("raise"))
                {

                    readPlayer(state,state.getHand(),state.getTable(),0);
                }

                return call();

    }
    
    
    
    public static PokerMove F(PokerState state, double HS)
    {

        
        if(state.getCurrentBet()/(state.getPot()+state.getCurrentBet()) > HS )
        {
            return call();
        }
        return check();

    }
    
    public static PokerMove F(PokerState state)
    {

        
        return readPlayer(state,state.getHand(),state.getTable(),0);

    }





    public static PokerMove bettingStatergyRound1(int height1, int height2,
                String handCategory, String tableCategory, PokerState state,
                boolean suited) {


        boolean cardsConnected = cardsConnected(height1, height2);
        int random = (int) (Math.random() * 4) + 1;



        //pair actions
        if(handCategory.equals("PAIR"))
        {

            //RR on QQ>
            if(height1 >= 10 && height2 >= 10)
            {
             return RR(state);

            }

            //RC on JJ
            if(height1 == 9)
            {


                return RC(state);

            }


            //CC on 1010 or 99
            if(height1 == 8 || height1 == 7)
            {
              return CC(state);
            }

            //CF on 88 or 77
            if(height1 == 6 || height1 == 5)
            {


                return CF(state);

            }


            //check anything less that 77
            
            
            
            return F(state);
            



        }else
        if(suited)
        {//suited actions



            //RR on AK suited
            if((height1 == 12 || height2 == 12) && (height1 == 11 || height2 == 11))
            {
                return RR(state);

            }

            //RC on AQ AJ KQ suited
            if(((height1 == 12 || height2== 12) && (height1 == 10 || height2 == 10)) ||
               ((height1 == 12 || height2== 12) && (height1 == 9  || height2 == 9 )) ||
               ((height1 == 11 || height2== 11) && (height1 == 10 || height2 == 10))
              ){


                return RC(state);



            }


            //CC on A10 KJ QJ
            if(((height1 == 12 || height2== 12) && (height1 == 8  || height2 == 8 )) ||
               ((height1 == 11 || height2== 11) && (height1 == 9  || height2 == 9 )) ||
               ((height1 == 10 || height2== 10) && (height1 == 9  || height2 == 9 ))
              ){


                return CC(state);



            }

            //CF on A9 K10 Q10 J10
            if(((height1 == 12 || height2== 12) && (height1 == 7  || height2 == 7 )) ||
               ((height1 == 11 || height2== 11) && (height1 == 8  || height2 == 8 )) ||
               ((height1 == 10 || height2== 10) && (height1 == 8  || height2 == 8 )) ||
               ((height1 == 9  || height2== 9 ) && (height1 == 8  || height2 == 8 ))
              ){


                return CF(state);



            }


            //check anything less
            

            return F(state);

        }else{
        //unsuited actions



            //CC on AK AQ
            if(((height1 == 12 || height2== 12) && (height1 == 11  || height2 == 11 )) ||
               ((height1 == 12 || height2== 12) && (height1 == 10  || height2 == 10 ))
              ){


                return CC(state);



            }


            //CF on KQ
            if(((height1 == 11 || height2== 11) && (height1 == 10 || height2 == 10))
              ){
                return CF(state);
            }
            
            
            //check anything less that 77
           
            return F(state);
        }


    }

    



    public long rankCard(Hand hand, Card[] table){
        long myhandCode = hand.getCard1().getNumber() + hand.getCard2().getNumber();
        for (Card card : table) {
            myhandCode += card.getNumber();
        }

        int myHandCode = 0;
        if (table.length == 3) {
            myHandCode = HandEval.hand5Eval(myhandCode);
        }

        if (table.length == 4) {
            myHandCode = HandEval.hand6Eval(myhandCode);
        }

        if (table.length == 5) {
            myHandCode = HandEval.hand7Eval(myhandCode);
        }


        return myHandCode;


    }

    public Card[] generateDeck(Hand hand, Card[] table)
    {
        Card[] tableCards = new Card[table.length];
        int pos = 0;
        for (Card card : table) {
            tableCards[pos] = card;
            pos++;
        }

        Card[] freshDeck = new Card[52];
        for (int i = 0; i < 52; i++) {
            freshDeck[i] = new Card(i);
        }



        int tracker = 0;
        Card[] possible = new Card[50 - table.length];
        for (int i = 0; i < freshDeck.length; i++) {

            if (table.length == 3) {
                if (!(new Card(i).getNumber() == hand.getCard1().getNumber()
                        || new Card(i).getNumber() == hand.getCard2().getNumber()
                        || new Card(i).getNumber() == tableCards[0].getNumber()
                        || new Card(i).getNumber() == tableCards[1].getNumber()
                        || new Card(i).getNumber() == tableCards[2].getNumber())) {
                    possible[tracker] = new Card(i);
                    tracker++;
                }
            }

            if (table.length == 4) {
                if (!(new Card(i).getNumber() == hand.getCard1().getNumber()
                        || new Card(i).getNumber() == hand.getCard2().getNumber()
                        || new Card(i).getNumber() == tableCards[0].getNumber()
                        || new Card(i).getNumber() == tableCards[1].getNumber()
                        || new Card(i).getNumber() == tableCards[2].getNumber()
                        || new Card(i).getNumber() == tableCards[3].getNumber())) {
                    possible[tracker] = new Card(i);
                    tracker++;
                }
            }

            if (table.length == 5) {
                if (!(new Card(i).getNumber() == hand.getCard1().getNumber()
                        || new Card(i).getNumber() == hand.getCard2().getNumber()
                        || new Card(i).getNumber() == tableCards[0].getNumber()
                        || new Card(i).getNumber() == tableCards[1].getNumber()
                        || new Card(i).getNumber() == tableCards[2].getNumber()
                        || new Card(i).getNumber() == tableCards[3].getNumber()
                        || new Card(i).getNumber() == tableCards[4].getNumber())) {
                    possible[tracker] = new Card(i);
                    tracker++;
                }
            }



        }


        return possible;

    }

    public double getWinPercentage(Hand hand, Card[] table) {
        double wins = 0;
        double losses = 0;
        double tied = 0;
        int splitpot = 0;
        long myHandCode = rankCard(hand,table);

        Card[] possible = generateDeck(hand, table);

        Card[] tableCards = new Card[table.length];
        int pos = 0;
        for (Card card : table) {
            tableCards[pos] = card;
            pos++;
        }

        for (int i = 0; i < possible.length; i++) {
            for (int j = i; j < possible.length; j++) {
                if (j != i) {
                    long handCode = possible[i].getNumber()
                            + possible[j].getNumber();

                    for (Card card : tableCards) {
                        handCode += card.getNumber();
                    }




                    int possibleHandCode = 0;

                    if (table.length == 3) {

                        possibleHandCode = HandEval.hand5Eval(handCode);
                    }
                    if (table.length == 4) {

                        possibleHandCode = HandEval.hand6Eval(handCode);
                    }
                    if (table.length == 5) {

                        possibleHandCode = HandEval.hand7Eval(handCode);
                    }


                    if (possibleHandCode > myHandCode) {

                        losses++;
                        //System.err.println("loose hand " + (losses + wins) + ": [" + possible[i].toString() + ", " + possible[j].toString() + "]");
                    } else if (possibleHandCode < myHandCode) {
                        wins++;
                        //System.err.println("win hand " + (losses + wins) + ": [" + possible[i].toString() + ", " + possible[j].toString() + "]");
                    } else {
                        tied++;
                        //System.err.println(" draw hand " + (losses + wins) + ": [" + possible[i].toString() + ", " + possible[j].toString() + "]");
                    }

                }
            }

        }

        double handstrength = (wins+(tied/2))/(wins+losses+tied);

        //System.err.println("\nwins = " + wins);
        //System.err.println("\nlosses = " + losses);
        //System.err.println("\nwin% = " + winpercen);
        return handstrength;
    }


     public double[] HandPotential(Hand hand, Card[] table) {
         double handPotential = 0;


         //0 = ahead
         //1 = behind
         //2 = tied

         double[][] HP = {{0,0,0},{0,0,0},{0,0,0}};
         double[] HPTotal = {0,0,0};
         int index = 0;



         //rank my current hand
        long myHandCode = rankCard(hand,table);




        //generate a pack of cards
        Card[] possible = generateDeck(hand, table);


        //remove known cards from pack of cards
        Card[] tableCards = new Card[table.length];
        int pos = 0;
        for (Card card : table) {
            tableCards[pos] = card;
            pos++;
        }


        //go through all possible opponent cards and compare to my ranked cards
        for (int i = 0; i < possible.length; i++) {
            for (int j = i; j < possible.length; j++) {
                if (j != i) {
                    long handCode = possible[i].getNumber()
                            + possible[j].getNumber();

                    for (Card card : tableCards) {
                        handCode += card.getNumber();
                    }




                    int possibleHandCode = 0;

                    if (table.length == 3) {

                        possibleHandCode = HandEval.hand5Eval(handCode);
                    }
                    if (table.length == 4) {

                        possibleHandCode = HandEval.hand6Eval(handCode);
                    }
                    if (table.length == 5) {

                        possibleHandCode = HandEval.hand7Eval(handCode);
                    }


                    if (possibleHandCode > myHandCode) {

                        index = 1;


                    } else if (possibleHandCode < myHandCode) {

                        index = 0;





                    } else {

                        index = 2;
                    }



                    if (table.length == 3) {


                        //generate turn and river for potential from cards
                        for (int q = 0; q < possible.length; q++) {
                            for (int w = q; w < possible.length; w++) {
                                if (j != i && j != q && j != w && i != q && i != w && q!=w) {


                                    //generate opponent potential
                                    long ophandCode = possible[i].getNumber()
                                            + possible[j].getNumber()
                                            + possible[q].getNumber()
                                            + possible[w].getNumber();
                                    for (Card card : tableCards) {
                                        ophandCode += card.getNumber();
                                    }


                                    //generate my potential
                                    long mehandCode = hand.getCard1().getNumber()
                                            + hand.getCard2().getNumber()
                                            + possible[q].getNumber()
                                            + possible[w].getNumber();
                                    for (Card card : tableCards) {
                                        mehandCode += card.getNumber();
                                    }





                                    long rankedopHandCode = HandEval.hand7Eval(ophandCode);
                                    long rankedmehandCode = HandEval.hand7Eval(mehandCode);



                                    //compare ranked potentials and keep track
                                    if (rankedopHandCode > rankedmehandCode) {

                                        HP[index][1] ++;
                                        HPTotal[1]++;


                                    } else if (rankedopHandCode < rankedmehandCode) {

                                        HP[index][0] ++;
                                        HPTotal[0]++;


                                    } else {

                                        HP[index][2] ++;
                                        HPTotal[2]++;
                                    }






                                }
                            }
                        }

                    }else if (table.length == 4) {




                        //generate river card
                        for (int q = 0; q < possible.length; q++) {
                                if (j != i && j != q && i != q ) {


                                    //generate opponent potential
                                    long ophandCode = possible[i].getNumber()
                                            + possible[j].getNumber()
                                            + possible[q].getNumber();
                                    for (Card card : tableCards) {
                                        ophandCode += card.getNumber();
                                    }


                                    //generate my potential
                                    long mehandCode = hand.getCard1().getNumber()
                                            + hand.getCard2().getNumber()
                                            + possible[q].getNumber();
                                    for (Card card : tableCards) {
                                        mehandCode += card.getNumber();
                                    }





                                    long rankedopHandCode = HandEval.hand7Eval(ophandCode);
                                    long rankedmehandCode = HandEval.hand7Eval(mehandCode);


                                    //compare ranked potentials and keep track
                                    if (rankedopHandCode > rankedmehandCode) {

                                        HP[index][1] ++;
                                        HPTotal[1]++;


                                    } else if (rankedopHandCode < rankedmehandCode) {

                                        HP[index][0] ++;
                                        HPTotal[0]++;

                                    } else {

                                        HP[index][2] ++;
                                        HPTotal[2]++;
                                    }


                            }
                        }

                    }

                }
            }

        }
        
        
        double[] returnValue = new double[2];
        
        if(table.length == 5)
        {
            returnValue[0] = 0;
            returnValue[1] = 0;
            
        }

        /* Npot: were ahead but fell behind.	 */
        double Npot = ((HP[0][1]+(HP[2][1]/2)+(HP[0][2]/2))/(HP[0][0]+HP[0][1]+HP[0][2]));

        /* Ppot: were behind but moved ahead.	 */
        double Ppot = (HP[1][0]+(HP[1][2]/2)+(HP[2][0]/2))/(HP[1][0]+HP[1][1]+HP[1][2]);

        System.err.println("       |\tAhead \t|\tTied\t|\tBehind");
        System.err.println("Ahead  |\t"+HP[0][0]+"\t|\t "+HP[0][2]+"\t|\t "+HP[0][1]);
        System.err.println("Tied   |\t"+HP[2][0]+"\t|\t "+HP[2][2]+"\t|\t "+HP[2][1]);
        System.err.println("Behind |\t"+HP[1][0]+"\t|\t "+HP[1][2]+"\t|\t "+HP[1][1]);
        System.err.println("total |\t"+HPTotal[0]+"\t|\t "+HPTotal[1]+"\t|\t "+HPTotal[2]);

         
         returnValue[0] = Ppot;
         returnValue[1] = Npot;
         
         

         return returnValue;



     }


     public double EHS(Hand hand, Card[] table)
     {


         double HS = getWinPercentage(hand, table);
         
         if(table.length == 5)
         {
             
             return HS;
             
         }else{
         double PPot = HandPotential(hand, table)[0];
         double NPot = HandPotential(hand, table)[1];
         double EHS = (HS) + ((1-HS) * PPot);
         

         return EHS;
         }

     }
}
