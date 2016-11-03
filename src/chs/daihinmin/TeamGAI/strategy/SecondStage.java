package chs.daihinmin.TeamGAI.strategy;

import jp.ac.uec.daihinmin.player.*;
import jp.ac.uec.daihinmin.*;
import jp.ac.uec.daihinmin.card.*;
import static jp.ac.uec.daihinmin.card.MeldFactory.PASS;
import static jp.ac.uec.daihinmin.card.MeldFactory.createSingleMeld;

import chs.daihinmin.TeamGAI.base.*;

import java.util.HashMap;

public class SecondStage {
	/**
	 * 中盤の動きを表した関数
	 */

	HashMap<Meld, Double> meldHash = new HashMap<Meld, Double>();
	public boolean showFlag = true;

	public Meld requestingPlay(Melds melds, Place place, Rules rules, PlayedCardList pList) {
		//スペードの３を持っていてジョーカーシングルが来たら出す
		if(place.hasJoker() && place.type() == Meld.Type.SINGLE && melds.contains(createSingleMeld(Card.S3))){
			return createSingleMeld(Card.S3);			
		}
		double nomalValue = 0;
		int removeMeldNum = 0;
		// 手札の役の点数表を作る
		meldHash.clear();
		for (Meld m : melds) {
			Double val = new Double( pList.calcMeldValue(m, place,rules, place.order()) );
			meldHash.put(m, val);
			nomalValue += val;
			//場が流せなさそうで上に重ねられ無いのは平均値から除く
			if(m.type() == Meld.Type.SEQUENCE || (m.type() == Meld.Type.GROUP && m.asCards().size() >= 3)){
				nomalValue -= val;
				--removeMeldNum;
			}
		}
		nomalValue /= (melds.size() - removeMeldNum);

		// 場がからのとき
		if (place.isRenew()) {
			if(showFlag){
				System.out.println("place is clean");
			}
			if(melds.size() == 1) return melds.get(0);
			
			// 革命できてそのほうが強そうならやる
			Melds canRevol = melds.extract(Melds.GROUPS.and(Melds.sizeOver(3)).or(Melds.SEQUENCES.and(Melds.sizeOver(4))));
			if (!canRevol.isEmpty()) {
				double reverseValue = pList.calcMeldsVps(melds, place,rules, place.order().reverse());
				if (nomalValue > reverseValue) {
					return canRevol.extract(Melds.MAX_RANK).get(0);
				}
			}
			// そうでなければスペ３単騎以外の弱いのを出す
			Melds playMelds = melds;
			if(pList.jokerFlag){
				playMelds = melds.extract(Melds.SINGLES
					.and(Melds.rankOf(Rank.valueOf(3)))
					.and(Melds.suitsOf(Suits.valueOf(Suit.SPADES)))
					.not());
			}
			if(playMelds.isEmpty()) return PASS;
			if(place.order() == Order.NORMAL){
				return playMelds.extract(Melds.MIN_RANK).get(0);
			}else{
				return playMelds.extract(Melds.MAX_RANK).get(0);
			}
		} else {
			// 場にカードが入っているとき
			if(showFlag){
				System.out.println("place is not clean");
			}
			/*
			if (place.lastMeld() == pList.lastPlayedMeld) {
				// 最後に出したのが自分ならパス
				return PASS;
			}
			*/

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
			
			if(showFlag){
				System.out.println("value check");
				System.out.println("meld point : " + melds.get(0).toString() + " : " + meldHash.get(melds.get(0)) );
				for(Meld m : melds){
					System.out.println("meld: " + m.toString() + " : " + meldHash.get(m) );
				}
				System.out.println("nomalValue : " + nomalValue);
			}
			
			// 弱いのが出せるなら出す
			if(meldHash.get(melds.get(0)) >= nomalValue){
				return melds.get(0);
			}
			//8は例外的に考える
			if(melds.get(0).rank() == Rank.EIGHT){
				return melds.get(0);
			}
			//強いなかの平均以下なら出す
			double averageValue = 0;
			int cnt = 0;
			for(Meld m : melds){
				averageValue += meldHash.get(m);
				cnt++;
			}
			averageValue /= cnt;
			if(meldHash.get(melds.get(0)) >= averageValue){
				return melds.get(0);
			}
			
			return PASS;
		}
		
	}
}
