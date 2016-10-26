package chs.daihinmin.TeamGAI.base;

import jp.ac.uec.daihinmin.*;
import static jp.ac.uec.daihinmin.card.MeldFactory.*;
import jp.ac.uec.daihinmin.card.*;
import jp.ac.uec.daihinmin.card.Rank;
//import jp.ac.uec.daihinmin.card.Melds.*;

public class PatternMake {
	boolean showFlag = false;
	
	public PatternMake(){
		//do nothing
	}
	
	public Melds patMake(Cards hands, Place place){
		Cards tHands = hands;
		if(showFlag){
			System.out.println("tHand is :" + tHands.toString());
		}
		//作ったペアはここにしまう
		Melds makedMelds = Melds.EMPTY_MELDS;
		
		//ジョーカーを持っておらずスペードの３があれば役として登録
		if(tHands.contains(Card.S3) && !tHands.contains(Card.JOKER)){
			makedMelds = makedMelds.add(createSingleMeld(Card.S3));
		}
		
		//size が大きい順にペアを作っていく
		for(int i = 4; i >= 2; i--){
			Melds groups = Melds.parseGroupMelds(tHands.extract(Cards.JOKERS.not())).extract(Melds.sizeOf(i));
			if(!groups.isEmpty()){
				if(showFlag){
					System.out.println("Look groups" + groups.toString() );
				}
				makedMelds = makedMelds.add(groups);
				//作業用手札からカードを取り除く
				tHands = tHands.remove(Melds.project(groups));
			}
		}
		
		//残ったカードで階段ができるなら作る（ジョーカーがあるなら代用）
		Melds sequence;
		do{
			sequence = Melds.parseSequenceMelds(tHands);
			if(!sequence.isEmpty()){
				Melds max_seq = sequence.extract(Melds.MAX_SIZE);
				makedMelds = makedMelds.add(max_seq);
				tHands = tHands.remove( Melds.project(max_seq) );
			}
			
		}while(!sequence.isEmpty());
		
		
		//ジョーカーを最強のカードにしておく
		if( tHands.contains(Card.JOKER) ){
			Suit suit = place.suits().size()==0?Suit.SPADES:place.suits().get(0);
			Rank jRunk = place.order() == Order.NORMAL?Rank.JOKER_HIGHEST:Rank.JOKER_LOWEST;
			makedMelds = makedMelds.add(createSingleMeldJoker(suit, jRunk));
			tHands = tHands.remove(Card.JOKER);
		}
		
		//それでも余ったカードを単騎にする
		makedMelds = makedMelds.add(Melds.parseSingleMelds(tHands));
		
		if(showFlag){
			System.out.println("makedMelds is :" + makedMelds.toString());
		}
		return makedMelds;
	}

}
