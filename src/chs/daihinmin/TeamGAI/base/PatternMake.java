package chs.daihinmin.TeamGAI.base;

import jp.ac.uec.daihinmin.*;
import static jp.ac.uec.daihinmin.card.MeldFactory.PASS;
import static jp.ac.uec.daihinmin.card.MeldFactory.*;
import jp.ac.uec.daihinmin.card.*;
import jp.ac.uec.daihinmin.card.Rank;
//import jp.ac.uec.daihinmin.card.Melds.*;

public class PatternMake {
	boolean showFlag = false;

	public PatternMake() {
		// do nothing
	}

	// 基本になる役わけ関数
	public Melds patMake(Cards hands, Place place) {
		Cards tHands = hands;
		if (showFlag) {
			System.out.println("tHand is :" + tHands.toString());
		}
		// 作ったペアはここにしまう
		Melds makedMelds = Melds.EMPTY_MELDS;

		// ジョーカーを持っておらずスペードの３があれば役として登録
		if (tHands.contains(Card.S3) && !tHands.contains(Card.JOKER)) {
			makedMelds = makedMelds.add(createSingleMeld(Card.S3));
		}

		// size が大きい順にペアを作っていく
		for (int i = 4; i >= 2; i--) {
			Melds groups = Melds.parseGroupMelds(tHands.extract(Cards.JOKERS.not())).extract(Melds.sizeOf(i));
			if (!groups.isEmpty()) {
				if (showFlag) {
					System.out.println("Look groups" + groups.toString());
				}
				makedMelds = makedMelds.add(groups);
				// 作業用手札からカードを取り除く
				tHands = tHands.remove(Melds.project(groups));
			}
		}

		// 残ったカードで階段ができるなら作る（ジョーカーがあるなら代用）
		Melds sequence;
		do {
			sequence = Melds.parseSequenceMelds(tHands);
			if (!sequence.isEmpty()) {
				Melds max_seq = sequence.extract(Melds.MAX_SIZE);
				makedMelds = makedMelds.add(max_seq);
				tHands = tHands.remove(Melds.project(max_seq));
			}

		} while (!sequence.isEmpty());

		// ジョーカーを最強のカードにしておく
		if (tHands.contains(Card.JOKER)) {
			Suit suit = place.suits().size() == 0 ? Suit.SPADES : place.suits().get(0);
			Rank jRunk = place.order() == Order.NORMAL ? Rank.JOKER_HIGHEST : Rank.JOKER_LOWEST;
			makedMelds = makedMelds.add(createSingleMeldJoker(suit, jRunk));
			tHands = tHands.remove(Card.JOKER);
		}

		// それでも余ったカードを単騎にする
		makedMelds = makedMelds.add(Melds.parseSingleMelds(tHands));

		if (showFlag) {
			System.out.println("makedMelds is :" + makedMelds.toString());
		}
		return makedMelds;
	}

	// 最終決戦用役わけ関数
	public Melds patMakeFinal(Cards cards, Place place,Rules rules){
		//場がからの時は普通と同じ
		if( place.isRenew() ){
			return patMake(cards,place);
		}
		//場がからでないときは、上に重ねられる役を作る（なければ基本と同じ）
		Rank next_rank;
        try{next_rank = place.type() == Meld.Type.SEQUENCE?
                            rules.nextRankSequence(place.rank(),place.size(),place.order())
                            :place.order() == Order.NORMAL?place.rank().higher():place.rank().lower();
        } catch (IllegalArgumentException e){
		//出せるカードが無い時もいつもどうり、空を渡して計算させないようにしてもいいかもしれない
          //return patMake(cards);
        	
        	if(showFlag) System.out.println("pass 1");
        	return Melds.EMPTY_MELDS;
        }
        
		//現在の革命状況により強いカードを絞る
        Cards tCards = cards.extract(Cards.JOKERS.not());
        if(place.order() == Order.NORMAL){
        	tCards = tCards.extract( Cards.rankUnder(next_rank).or(Cards.rankOf(next_rank)) );
        }else{
        	tCards = tCards.extract( Cards.rankOver(next_rank).or(Cards.rankOf(next_rank)) );
        }
        if(cards.contains(Card.JOKER)){
        	tCards = tCards.add(Card.JOKER);
        }
        if(showFlag){
        	System.out.println("Show Stronger Cards");
        	System.out.println(tCards.toString());
        }
        
        //絞ったカードでできる役を作る
		Meld playableMeld = PASS;
		Melds playableMelds = Melds.EMPTY_MELDS;
		if(place.type() == Meld.Type.SEQUENCE){
			playableMelds = Melds.parseSequenceMelds(tCards);
		}else if(place.type() == Meld.Type.GROUP){
			playableMelds = Melds.parseGroupMelds(tCards);
		}else{
			playableMelds = Melds.parseSingleMelds(tCards);
		}
		//出せる組が見つからなかった時
		if(playableMelds.isEmpty()){
			if(showFlag) System.out.println("pass 2");
			return Melds.EMPTY_MELDS;
		}
		//縛れるなら縛る
		for(Meld m : playableMelds){
			if(place.suits() == m.suits()){
				playableMeld = m;
			}
		}
		if(playableMeld == PASS){
			playableMeld = playableMelds.get(0);
		}
		
		cards = cards.remove( playableMeld.asCards() );
		//return playableMelds.add( patMake(cards,place) );
		Melds hand = Melds.EMPTY_MELDS;
		hand = hand.add(playableMeld);
		return hand.add( patMake(cards,place) );
		
	}

}
