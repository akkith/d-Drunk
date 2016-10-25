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





public  class FirstStage {
	//boolean showFlag = true;

	/**
	 * 序盤の動きを表した関数 singleCards、役カードの4以上１３以下のカードを出す 縛りは４〜１３以内のカードから弱い順に出す
	 */

	public static Meld requestingPlay(Melds melds, Place place, Rules rules) {
		// 場に何も出されてなければ
		boolean showFlag = true;
		if (place.isRenew()) {
			Melds mMelds = melds;
			Melds sMelds = melds.extract(Melds.SINGLES);
			Melds qMelds = melds.extract(Melds.SEQUENCES);
			//qMelds =  qMelds.extract(Melds.rankOver(Rank.THREE).and(Melds.rankUnder(Rank.JACK)));
			Melds gMelds = melds.extract(Melds.rankOver(Rank.THREE).and(Melds.rankUnder(Rank.JACK)));
			gMelds = gMelds.extract(Melds.GROUPS);
			// singleが４以上１３以下なら出す（８は要検討）、
			if (!sMelds.isEmpty() && (Rank.FOUR.toInt() <= sMelds.get(0).rank().toInt() && sMelds.get(0).rank().toInt() <= Rank.KING.toInt())
					/*&& sMelds.etract(Melds.rankOf(mMelds.extract(Melds.MAX_RANK)))  && melds.get(0).rank() == Rank.EIGHT */) {
				return sMelds.get(0);
			} else if (!qMelds.isEmpty() /*&& !qMelds.extract(Melds.rankOver(Rank.THREE).and(Melds.rankUnder(Rank.JACK)).isEmpty()*/) {
				for ( int i=0 ; i<qMelds.size();i++){
					//Melds qqMelds = qMelds.get(i);
					if (3 < qMelds.get(i).rank().toInt() && qMelds.get(i).rank().toInt() < 11 /*.contains((Melds.rankOver(Rank.THREE).and(Melds.rankUnder(Rank.JACK))))*/){
						return PASS;
					}else{
						return qMelds.get(0);
					}
				}
				return qMelds.get(0);
				
			}else{ 
				if (!gMelds.isEmpty()) {
					return gMelds.get(0);
				} else {
					return mMelds.get(0);
				}
			}
		} else {
			// 場が縛られている時
			if (!place.lockedSuits().equals(Suits.EMPTY_SUITS)) {
				// 場を縛っているスート集合に適合する役を抽出して,候補とする．
				melds = melds.extract(Melds.rankOver(Rank.THREE).and(Melds.rankUnder(Rank.JACK)));
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
