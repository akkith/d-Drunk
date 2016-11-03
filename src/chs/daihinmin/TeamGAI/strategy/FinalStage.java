package chs.daihinmin.TeamGAI.strategy;

import jp.ac.uec.daihinmin.player.*;
import jp.ac.uec.daihinmin.*;
import jp.ac.uec.daihinmin.card.*;
import static jp.ac.uec.daihinmin.card.MeldFactory.PASS;
import static jp.ac.uec.daihinmin.card.MeldFactory.createSingleMeld;

import chs.daihinmin.TeamGAI.base.*;

import java.util.HashMap;
import java.util.ArrayList;

public class FinalStage {
	/**
	 * 終盤の動きを表した関数 前提：手札の役がまとめると３組以下 出せる役が何も無い時はこの関数を呼び出され無い
	 */
	
	//デバッグ用
	public boolean showFlag = true;

	//手札評価用
	PlayedCardList pList;
	HashMap<Meld, Double> meldHash = new HashMap<Meld, Double>();

	public Meld requestingPlay(Melds melds, Place place, Rules rules, PlayedCardList playedList, PatternMake pMaker) {
		/*
		if (place.lastMeld() == playedList.lastPlayedMeld) {
			// 最後に出したのが自分ならパス
			return PASS;
		}
		*/
		// スペードの３を持っていてジョーカーシングルが来たら出す
		if (place.hasJoker() && place.type() == Meld.Type.SINGLE && melds.contains(createSingleMeld(Card.S3))) {
			return createSingleMeld(Card.S3);
		}

		pList = playedList;
		
		int cntPowerfull = 0;
		meldHash.clear();
		// 出したら流せるような役
		Melds powerfullMelds = Melds.EMPTY_MELDS;
		// 出しても重ねられれる役
		Melds nonPowerfullMelds = Melds.EMPTY_MELDS;

		// 流せるやくかそうで無いかでわける
		for (Meld m : melds) {
			Double val = new Double(pList.calcMeldValue(m, rules, place.order()));
			meldHash.put(m, val);
			if (val <= 0) {
				++cntPowerfull;
				powerfullMelds = powerfullMelds.add(m);
			} else {
				nonPowerfullMelds = nonPowerfullMelds.add(m);
			}
		}

		// 場が空のとき
		if (place.isRenew()) {
			// あがるならあがる
			if (melds.size() == 1) {
				return melds.get(0);
			}

			Meld playMeld = null;
			Melds tMelds = Melds.EMPTY_MELDS;
			if (melds.size() <= cntPowerfull + 1) { // 場を流せないカードが一組以下ならそれ以外をだしていく
				// 一応 階段＞ペア＞単騎の優先順位

				tMelds = tMelds.add(powerfullMelds.extract(Melds.SEQUENCES));
				if (!tMelds.isEmpty()) {
					return tMelds.get(0);
				}
				tMelds = tMelds.add(powerfullMelds.extract(Melds.GROUPS));
				if (!tMelds.isEmpty()) {
					return tMelds.get(0);

				}
				tMelds = tMelds.add(powerfullMelds.extract(Melds.SINGLES));
				if (!tMelds.isEmpty()) {
					return tMelds.get(0);
				}

			} else { // そうでないなら場を確実には流せ無い組を出していく
						// 誰かの上に出せそうに無いものを優先
				tMelds = tMelds.add(nonPowerfullMelds.extract(Melds.SEQUENCES));
				if (!tMelds.isEmpty()) {
					return tMelds.get(0);
				}

				nonPowerfullMelds = Melds.sort(nonPowerfullMelds);
				if (nonPowerfullMelds.size() - 1 <= powerfullMelds.size()) {
					// 場を流せそうな組数よりそうでない組数の比較
					if (nonPowerfullMelds.size() > 1) {
						return nonPowerfullMelds.get(1);
					} else {
						return nonPowerfullMelds.get(0);
					}
				} else {
					// 弱いのから出すしか無い
					return nonPowerfullMelds.get(0);
				}

			}

		} else { // 場がからでないとき
			// 出せる手を除いて残った手札を見て判断
			Rank next_rank;
			Meld playable = PASS;
			Melds playables = Melds.EMPTY_MELDS;
			try {
				next_rank = place.type() == Meld.Type.SEQUENCE
						? rules.nextRankSequence(place.rank(), place.size(), place.order())
						: place.order() == Order.NORMAL ? place.rank().higher() : place.rank().lower();
			} catch (IllegalArgumentException e) {
				return PASS;
			}

			// 場に出されている役の,タイプ,枚数,ランク,革命中か否か,に合わせて,「出すことができる」候補に絞る
			playables = melds.extract(Melds.typeOf(place.type()).and(Melds.sizeOf(place.size())));

			playables = playables.extract(Melds.rankOf(next_rank)
					.or(place.order() == Order.NORMAL ? Melds.rankOver(next_rank) : Melds.rankUnder(next_rank)));

			int value = -1;
			for (Meld m : playables) {
				
				Melds nextHand = pMaker.patMake(Melds.project(melds.remove(m)), place, pList.jokerFlag);
				int tValue = meldValue(nextHand, m, rules, place.order());
				//int tValue = meldValue(melds.remove(m), m);
				if (showFlag) {
					System.out.println("Meld  :" + m.toString());
					System.out.println("point :" + tValue);
				}
				if (value < tValue) {
					playable = m;
					value = tValue;
				}
			}

			if (value >= 0) {
				return playable;
			}
			return PASS;

		}
		return PASS;

	}

	// 場が流せそうな役 - 場が流せなさそうな役 を価値とする
	public int meldValue(Melds melds, Meld meld, Rules rules, Order order) {
		
		int value = 0;
		if (meldHash.get(meld) <= 0) {
			value += 1;
			if(melds.size() <= 1){
				//勝てそうならあからさまな点数を返す
				return 100;
			}
		}
		for (Meld m : melds) {
			if(!meldHash.containsKey(m)){
				Double val = new Double(pList.calcMeldValue(m, rules, order));
				meldHash.put(m, val);
			}
			if (meldHash.get(m) == 0 && m.type() != Meld.Type.SEQUENCE) {
				value += 1;
			} else {
				value -= 1;
			}
		}
		return value;
	}
}
