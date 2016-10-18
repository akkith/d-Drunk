package chs.daihinmin.TeamGAI.strategy;

import static jp.ac.uec.daihinmin.card.MeldFactory.PASS;
import static jp.ac.uec.daihinmin.card.MeldFactory.createSingleMeldJoker;
import jp.ac.uec.daihinmin.player.*;
import jp.ac.uec.daihinmin.*;
import jp.ac.uec.daihinmin.card.*;

public class Dashiosimi {
	boolean showFlag = true;

	//カードの交換
	public static Cards requestingGivingCards(Cards hand, Rules rules, int rank) {
		Cards result = Cards.EMPTY_CARDS;
		// 手札を昇順にソート．たとえば，JOKER S2 HA ... D4 D3
		Cards sortedHand = Cards.sort(hand);
		// 渡すカードの枚数だけ，resultにカードを追加
		for (int i = 0; i < Rules.sizeGivenCards(rules, rank); i++) {
			result = result.add(
					sortedHand.get(/* 平民より上か？ 注:07年度のルールでは平民以上の時のみ選ぶことができる */
							Rules.heiminRank(rules) < rank ? sortedHand.size() - 1 - i /* 平民より下 */
									: i /* 平民より上 */));
		}
		// たとえば，大貧民なら D3 D4
		// たとえば，大富豪なら JOKER S2
		return result;
	}
	
	//現状出し惜しみは役の作り方で再現しているため、ここはサンプルとほとんど同じ
	public Meld requestingPlay(Melds melds, Place place, Rules rules) {
		// 場に何のカードも出されていなければ,
		if (place.isRenew()) {
			// 候補の中で最弱のランクを持つ役を抽出して,候補とする．
			melds = melds.extract(Melds.MIN_RANK);
		} else {
			// 場が縛られていれば
			if (!place.lockedSuits().equals(Suits.EMPTY_SUITS)) {
				// 場を縛っているスート集合に適合する役を抽出して,候補とする．
				melds = melds.extract(Melds.suitsOf(place.lockedSuits()));
				if(showFlag){
					System.out.println("Suits Lock");
				}
			}
			if(showFlag){
				System.out.println("start extracted meld -> " + melds.toString());
			}
			// next_rank := 場に出されている役のランクの,直ぐ上のランク
			Rank next_rank;
			try {
				next_rank = place.type() == Meld.Type.SEQUENCE
						? rules.nextRankSequence(place.rank(), place.size(), place.order())
						: place.order() == Order.NORMAL ? place.rank().higher()
								: place.rank().lower();
			} catch (IllegalArgumentException e) {
				// 場に出されている役より，ランクの大きい役が存在しないとき
				if(showFlag){
					System.out.println("No stronger cards");
				}
				return PASS;
			}
			
			if(showFlag){
				System.out.println("upper meld is " + melds.toString());
			}
			
			// 場に出されている役の,タイプ,枚数,ランク,革命中か否か,に合わせて,「出すことができる」候補に絞る
			melds = melds.extract( Melds.typeOf(place.type()).and(Melds.sizeOf(place.size())) );
			
			melds = melds.extract( Melds.rankOf(next_rank).or(place.order() == Order.NORMAL ? Melds.rankOver(next_rank)
							: Melds.rankUnder(next_rank)));
			
			
			if(showFlag){
				System.out.println("end extracted meld is " + melds.toString());
				System.out.println("next_rank is " + next_rank.toString());
			}
		}
		
		//残った候補の中からさらに絞る．たとえば，場のオーダが通常のとき 最も弱い役を候補に残す．
        melds = melds.extract(place.order() == Order.NORMAL?Melds.MIN_RANK: Melds.MAX_RANK);
        
        if(melds.isEmpty()){
        	return PASS;
        }else{
        	return melds.get(0);
        }
	}

}
