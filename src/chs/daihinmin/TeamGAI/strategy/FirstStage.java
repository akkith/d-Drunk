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

		boolean showFlag = false;
		//シングルJOKERにスペ３出す
		if(place.hasJoker() && place.type() == Meld.Type.SINGLE && melds.contains(createSingleMeld(Card.S3))){
			return createSingleMeld(Card.S3);			
		}
		// 場に何も出されてなければ
		if (place.isRenew() && !place.isReverse()) {
			//未革命状態の時
//			Melds.sort(melds);
			//最初の手札を所持
			Melds mMelds = melds;
			//階段の役を保持し、除く
			Melds qMelds = melds.extract(Melds.SEQUENCES);
//			melds = melds.remove(qMelds);
			//n枚組の役を保持し、除く
			Melds gMelds = melds.extract(Melds.GROUPS);
//			melds = melds.remove(gMelds);
			//革命できる４枚カードを抜く
			gMelds = gMelds.extract(Melds.sizeUnder(4));
//			gMelds = gMelds.extract(Melds.GROUPS);
			//シングル
			Melds sMelds = melds.extract(Melds.SINGLES);
			sMelds = sMelds.extract(Melds.rankOver(Rank.THREE).and(Melds.rankUnder(Rank.EIGHT)).or(Melds.rankOver(Rank.EIGHT).and(Melds.rankUnder(Rank.JACK))));
//			s2Melds = sMelds.extract(Melds.rankOver(Rank.EIGHT).and(Melds.rankUnder(Rank.JACK)));
			sMelds = sMelds.extract(Melds.SINGLES);
//			
			// singleが４以上１３以下なら出す（８は要検討）、
			if (!sMelds.isEmpty() && 4 <= sMelds.get(0).rank().toInt() && sMelds.get(0).rank().toInt() <= 10) {
				return sMelds.get(0);
			} else if (!qMelds.isEmpty()) {
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
			//Melds.sort(melds);
			Melds mMelds = melds;
			//階段の役を保持し、除く
			Melds qMelds = melds.extract(Melds.SEQUENCES);
			//melds = melds.remove(qMelds);
			//pareを作る
			Melds gMelds = melds.extract(Melds.GROUPS);
			//melds = melds.remove(gMelds);
			//革命できる４枚カードを抜く
			gMelds = gMelds.extract(Melds.sizeUnder(4));
			//gMelds = gMelds.extract(Melds.GROUPS);
			//残った役をシングルに
			Melds sMelds = melds.extract(Melds.SINGLES);
			sMelds = sMelds.extract(Melds.rankOver(Rank.THREE).and(Melds.rankUnder(Rank.EIGHT)).or(Melds.rankOver(Rank.EIGHT).and(Melds.rankUnder(Rank.ACE))));
			sMelds = sMelds.extract(Melds.SINGLES);
			//singleが４以上ACEいかなら出す
			if (!sMelds.isEmpty() && 4 <= sMelds.get(0).rank().toInt() /*&& sMelds.get(0).rank()*/ ) {
				return sMelds.get(sMelds.size()-1);
			} else if (!qMelds.isEmpty()) {
				return qMelds.get(qMelds.size()-1);
			}else{ 
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
//		melds = melds.extract(place.order() == Order.NORMAL ? Melds.MIN_RANK : Melds.MAX_RANK);

		if (melds.isEmpty()) {
			return PASS;
		} else {
			return melds.get(0);
		}
	}

}
