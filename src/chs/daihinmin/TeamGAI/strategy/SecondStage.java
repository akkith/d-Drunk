package chs.daihinmin.TeamGAI.strategy;

import jp.ac.uec.daihinmin.player.*;
import jp.ac.uec.daihinmin.*;
import jp.ac.uec.daihinmin.card.*;
import static jp.ac.uec.daihinmin.card.MeldFactory.PASS;

import chs.daihinmin.TeamGAI.base.*;

import java.util.HashMap;

public class SecondStage {
	/**
	 * 中盤の動きを表した関数
	 */

	HashMap<Meld, Double> meldHash = new HashMap<Meld, Double>();

	public Meld requestingPlay(Melds melds, Place place, Rules rules, PlayedCardList pList) {
		double nomalValue = 0;
		// 手札の役の点数表を作る
		meldHash.clear();
		for (Meld m : melds) {
			Double val = new Double( pList.calcMeldValue(m, rules, place.order()) );
			meldHash.put(m, val);
			nomalValue += val;
		}
		nomalValue /= melds.size();

		// 場がからのとき
		if (place.isRenew()) {
			// 革命できてそのほうが強そうならやる
			Melds canRevol = melds.extract(Melds.GROUPS.and(Melds.sizeOver(3)).or(Melds.SEQUENCES.and(Melds.sizeOver(4))));
			if (!canRevol.isEmpty()) {
				double reverseValue = pList.calcMeldsVps(melds, rules, place.order().reverse());
				if (nomalValue > reverseValue) {
					return canRevol.extract(Melds.MAX_RANK).get(0);
				}
			}
			// そうでなければスペ３単騎以外の弱いのを出す
			Melds playMelds = melds.extract(Melds.SINGLES
					.and(Melds.rankOf(Rank.valueOf(3)))
					.and(Melds.suitsOf(Suits.valueOf(Suit.SPADES))));
			if(playMelds.isEmpty()) return PASS;
			return playMelds.extract(Melds.MIN_RANK).get(0);
		} else {
			// 場にカードが入っているとき
			if (place.lastMeld() == pList.lastPlayedMeld) {
				// 最後に出したのが自分ならパス
				return PASS;
			}

			//ここからいつもの
			Rank next_rank;
			try {
				next_rank = place.type() == Meld.Type.SEQUENCE
						? rules.nextRankSequence(place.rank(), place.size(), place.order())
						: place.order() == Order.NORMAL ? place.rank().higher() : place.rank().lower();
			} catch (IllegalArgumentException e) {
				// 場に出されている役より，ランクの大きい役が存在しないとき
				return PASS;
			}

			// 場に出されている役の,タイプ,枚数,ランク,革命中か否か,に合わせて,「出すことができる」候補に絞る
			melds = melds.extract(Melds.typeOf(place.type()).and(Melds.sizeOf(place.size())));

			melds = melds.extract(Melds.rankOf(next_rank)
					.or(place.order() == Order.NORMAL ? Melds.rankOver(next_rank) : Melds.rankUnder(next_rank)));
			//ここまでいつもの
			if(melds.isEmpty()) return PASS;
			Melds.sort(melds);
			
			// 弱いのが出せるなら出す
			if(meldHash.get(melds.get(0)) > nomalValue){
				return melds.get(0);
			}
			// 強いのが出せるなら出す
			return PASS;
		}
		
	}
}
