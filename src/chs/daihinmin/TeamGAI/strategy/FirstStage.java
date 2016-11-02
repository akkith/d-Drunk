package chs.daihinmin.TeamGAI.strategy;
//
//import jp.ac.uec.daihinmin.player.*;
//import jp.ac.uec.daihinmin.*;
//import jp.ac.uec.daihinmin.card.*;
//import static jp.ac.uec.daihinmin.card.MeldFactory.PASS;
//import static jp.ac.uec.daihinmin.card.MeldFactory.createSingleMeldJoker;
//import chs.daihinmin.TeamGAI.strategy.*;
//import chs.daihinmin.TeamGAI.base.*;

import static jp.ac.uec.daihinmin.card.MeldFactory.PASS;
import static jp.ac.uec.daihinmin.card.MeldFactory.createSingleMeldJoker;
import jp.ac.uec.daihinmin.player.*;
import jp.ac.uec.daihinmin.*;
import jp.ac.uec.daihinmin.card.*;

import chs.daihinmin.TeamGAI.strategy.*;
import chs.daihinmin.TeamGAI.base.*;
import static jp.ac.uec.daihinmin.card.MeldFactory.createSingleMeld;





public  class FirstStage {
	//boolean showFlag = true;

	/**
	 * 序盤の動きを表した関数 singleCards、役カードの4以上１３以下のカードを出す 縛りは４〜１３以内のカードから弱い順に出す
	 */

	public static Meld requestingPlay(Melds melds, Place place, Rules rules,PlayedCardList pList) {

		boolean showFlag = true;
		//シングルJOKERにスペ３出す
		if(place.hasJoker() && place.type() == Meld.Type.SINGLE && melds.contains(createSingleMeld(Card.S3))){
			return createSingleMeld(Card.S3);			
		}
		// 場に何も出されてなければ
		if (place.isRenew() && !place.isReverse()) {
			//未革命状態の時
			Melds.sort(melds);
			Melds mMelds = melds;
			//階段の役を保持し、除く
			Melds qMelds = melds.extract(Melds.SEQUENCES);
			melds = melds.remove(qMelds);
			//階段役から３とJACK以上を抜きさらに階段を作る
			qMelds = qMelds.extract(Melds.rankOver(Rank.THREE).and(Melds.rankUnder(Rank.JACK)));
			qMelds = qMelds.extract(Melds.SEQUENCES);
			//n枚組の役を保持し、除く
			//qMelds =  qMelds.extract(Melds.rankOver(Rank.THREE).and(Melds.rankUnder(Rank.JACK)));
			Melds gMelds = melds.extract(Melds.rankOver(Rank.THREE).and(Melds.rankUnder(Rank.JACK)));
			gMelds = gMelds.extract(Melds.GROUPS);
			melds = melds.remove(gMelds);
			//残った役をシングルに
			Melds sMelds = melds.extract(Melds.rankOver(Rank.THREE).and(Melds.rankUnder(Rank.JACK)));
			sMelds = sMelds.extract(Melds.SINGLES);
			// singleが４以上１３以下なら出す（８は要検討）、
			if (!sMelds.isEmpty() /*&& (Rank.FOUR.toInt() <= sMelds.get(0).rank().toInt() && sMelds.get(0).rank().toInt() <= Rank.KING.toInt())
					&& sMelds.etract(Melds.rankOf(mMelds.extract(Melds.MAX_RANK)))  && melds.get(0).rank() == Rank.EIGHT */) {
				return sMelds.get(0);
			} else if (!qMelds.isEmpty() /*&& !qMelds.extract(Melds.rankOver(Rank.THREE).and(Melds.rankUnder(Rank.JACK)).isEmpty()*/) {
//				for ( int i=0 ; i<qMelds.size();i++){
//					//Melds qqMelds = qMelds.get(i);
//					if (3 < qMelds.get(i).rank().toInt() && qMelds.get(i).rank().toInt() < 11 /*.contains((Melds.rankOver(Rank.THREE).and(Melds.rankUnder(Rank.JACK))))*/){
//						return PASS;
//					}else{
//						return qMelds.get(0);
//					}
//				}
				return qMelds.get(0);
				
			}else{ 
				if (!gMelds.isEmpty()) {
					return gMelds.get(0);
				} else {
					return mMelds.get(0);
				}
			}
		} else if(place.isRenew() && place.isReverse()){
			//革命状態の時、３と２を残す
			Melds.sort(melds);
			Melds mMelds = melds;
			//出し惜しみするために、３と２以外のを残す
			//Melds dMelds = melds.extract(Melds.rankOver(Rank.THREE).and(Melds.rankUnder(Rank.ACE)));
			//階段の役を保持し、除く
			Melds qMelds = melds.extract(Melds.SEQUENCES);
			melds = melds.remove(qMelds);
			//階段役から３と２を抜きさらに階段を作る
			qMelds = qMelds.extract(Melds.rankOver(Rank.THREE).and(Melds.rankUnder(Rank.ACE)));
			qMelds = qMelds.extract(Melds.SEQUENCES);
			//n枚組の役を保持し、除く
			//qMelds =  qMelds.extract(Melds.rankOver(Rank.THREE).and(Melds.rankUnder(Rank.JACK)));
			//Melds gMelds = melds.extract(Melds.rankOver(Rank.THREE).and(Melds.rankUnder(Rank.ACE)));
			//n枚組の役から３と２を抜きさらにn枚組を作る
			Melds gMelds = melds.extract(Melds.rankOver(Rank.THREE).and(Melds.rankUnder(Rank.ACE)));
			gMelds = melds.extract(Melds.GROUPS);
			melds = melds.remove(gMelds);

			//革命できる４枚カードを抜く
			gMelds = gMelds.extract(Melds.sizeUnder(4));
			gMelds = gMelds.extract(Melds.GROUPS);
			
			//残った役をシングルに
			Melds sMelds = melds.extract(Melds.SINGLES);
			//singleが４以上ACEいかなら出す
			if (!sMelds.isEmpty() /*&& (Rank.FOUR.toInt() <= sMelds.get(0).rank().toInt() && sMelds.get(0).rank().toInt() <= Rank.KING.toInt())
					&& sMelds.extract(Melds.rankOf(mMelds.extract(Melds.MAX_RANK)))  && melds.get(0).rank() == Rank.EIGHT */) {
				return sMelds.get(sMelds.size()-1);
			} else if (!qMelds.isEmpty() /*&& !qMelds.extract(Melds.rankOver(Rank.THREE).and(Melds.rankUnder(Rank.JACK)).isEmpty()*/) {
				//階段を出す
//				for ( int i=0 ; i<qMelds.size();i++){
//					//Melds qqMelds = qMelds.get(i);
//					if (3 < qMelds.get(i).rank().toInt() && qMelds.get(i).rank().toInt() < 11 /*.contains((Melds.rankOver(Rank.THREE).and(Melds.rankUnder(Rank.JACK))))*/){
//						return PASS;
//					}else{
//						return qMelds.get(0);
//					}
//				}
				return qMelds.get(qMelds.size()-1);
				
			}else{ 
				//n枚組を出す
				
				if (!gMelds.isEmpty()) {
					return gMelds.get(gMelds.size()-1);
				} else {
					return mMelds.get(mMelds.size()-1);
				}
			}
			
		
		} else{
			if (place.lastMeld() == pList.lastPlayedMeld) {
				// 最後に出したのが自分ならパス
				return PASS;
			}
			if (!place.isReverse()){
				melds = melds.extract(Melds.rankOver(Rank.THREE).and(Melds.rankUnder(Rank.JACK)));
			}else{
				melds = melds.extract(Melds.rankOver(Rank.THREE).and(Melds.rankUnder(Rank.ACE)));
			}
			// 場が縛られている時
			if (!place.lockedSuits().equals(Suits.EMPTY_SUITS)) {
				// 場を縛っているスート集合に適合する役を抽出して,候補とする．
				if(!place.isReverse()){
					melds = melds.extract(Melds.rankOver(Rank.THREE).and(Melds.rankUnder(Rank.JACK)));
				}else{
					melds = melds.extract(Melds.rankOver(Rank.THREE).and(Melds.rankUnder(Rank.ACE)));
				}
				melds = melds.extract(Melds.suitsOf(place.lockedSuits()));
			}
			Rank next_rank;
			try {
				next_rank = place.type() == Meld.Type.SEQUENCE
						? rules.nextRankSequence(place.rank(), place.size(), place.order())
						: place.order() == Order.NORMAL ? place.rank().higher() : place.rank().lower();
			} catch (IllegalArgumentException e) {
				// 場に出されている役より，ランクの大きい役が存在しないとき
				if (showFlag) {
					System.out.println("No stronger cards");
				}
				return PASS;
			}

			if (showFlag) {
				System.out.println("upper meld is " + melds.toString());
			}

			// 場に出されている役の,タイプ,枚数,ランク,革命中か否か,に合わせて,「出すことができる」候補に絞る
			melds = melds.extract(Melds.typeOf(place.type()).and(Melds.sizeOf(place.size())));

			melds = melds.extract(Melds.rankOf(next_rank)
					.or(place.order() == Order.NORMAL ? Melds.rankOver(next_rank) : Melds.rankUnder(next_rank)));

			if (showFlag) {
				System.out.println("end extracted meld is " + melds.toString());
				System.out.println("next_rank is " + next_rank.toString());
			}
		}

		// 残った候補の中からさらに絞る．たとえば，場のオーダが通常のとき 最も弱い役を候補に残す．
		melds = melds.extract(place.order() == Order.NORMAL ? Melds.MIN_RANK : Melds.MAX_RANK);

		if (melds.isEmpty()) {
			return PASS;
		} else {
			return melds.get(0);
		}
	}

}
