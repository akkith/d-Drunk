 package chs.daihinmin.TeamGAI.strategy;

import static jp.ac.uec.daihinmin.card.MeldFactory.PASS;
import static jp.ac.uec.daihinmin.card.MeldFactory.createSingleMeldJoker;

import java.util.ArrayList;

//import jp.ac.hosei.daihinmin.fujita2.MeldSet;
//import jp.ac.hosei.daihinmin.fujita2.Utils;
import jp.ac.uec.daihinmin.player.*;
import jp.ac.uec.daihinmin.*;
import jp.ac.uec.daihinmin.card.*;

public class Dashiosimi {
	boolean showFlag = false;

	/**
     * 交換カードを決定するメソッド．
     * @return 交換相手に渡すカード集合
     */
	public static Cards requestingGivingCards(Cards hand, Rules rules, int rank) {
		Cards result = Cards.EMPTY_CARDS;
		// 手札を昇順にソート．たとえば，JOKER S2 HA ... D4 D3
		Cards sortedHand = Cards.sort(hand);
		//元のカードを保持
		Cards Hand = sortedHand;
		sortedHand = sortedHand.remove(Card.JOKER);
		//単騎出ししかできないカード
		//作業用ハンド
		Cards hogeHand = sortedHand;
		hogeHand= hogeHand.remove(Melds.project(Melds.parseSequenceMelds(sortedHand))); 
		hogeHand = hogeHand.remove(Melds.project(Melds.parseGroupMelds(sortedHand))); 
		Cards singleCards = hogeHand;
		singleCards = Cards.sort(singleCards);
		
		int givenSize = Rules.sizeGivenCards(rules,rank);
		//平民より上か？
		int diffrank = Rules.heiminRank(rules)-rank;
		if (diffrank > 0){
			//３は渡さない
			singleCards = singleCards.remove(Card.D3,Card.H3,Card.S3,Card.C3);
			sortedHand = sortedHand.remove(Card.D3,Card.H3,Card.S3,Card.C3);
			//８も渡さない
			singleCards = singleCards.remove(Card.D8,Card.H8,Card.S8,Card.C8);
			sortedHand = sortedHand.remove(Card.D8,Card.H8,Card.S8,Card.C8);
			if(givenSize == 1){
				if(singleCards.isEmpty() /*&& sortedHand.isEmpty()*/){
					if(!sortedHand.isEmpty()){
						result = result.add(Hand.get(0));
					}else{
						result = result.add(sortedHand.get(0));
					}
				}else{
					result = result.add(singleCards.get(0));
				}
			}else if(givenSize == 2){
				if(singleCards.isEmpty() /*&& sortedHand.isEmpty()*/){
					if(sortedHand.isEmpty()){
						result = result.add(Hand.get(0));
						result = result.add(Hand.get(1));
					}else if(sortedHand.size() == 1){
						result = result.add(sortedHand.get(0));
						Hand = Hand.remove(sortedHand.get(0));
						result = result.add(Hand.get(0));
					}else{
						result = result.add(sortedHand.get(0));
						result = result.add(sortedHand.get(1));
					}
				}else if(singleCards.size() == 1){
					if(sortedHand.isEmpty()){
						result = result.add(singleCards.get(0));
						Hand = Hand.remove(singleCards.get(0));
						result = result.add(Hand.get(0));
					}else{
						result = result.add(singleCards.get(0));
						sortedHand = sortedHand.remove(singleCards.get(0));
						result = result.add(sortedHand.get(0));
					}
				}else{
					result = result.add(singleCards.get(0));
					result = result.add(singleCards.get(1));
				}
			}
		}
		
		
		// 渡すカードの枚数だけ，resultにカードを追加
//		for (int i = 0; i < Rules.sizeGivenCards(rules, rank); i++) {
//			result = result.add(
//					sortedHand.get(/* 平民より上か？ 注:07年度のルールでは平民以上の時のみ選ぶことができる */
//							Rules.heiminRank(rules) < rank ? sortedHand.size() - 1 - i /* 平民より下 */
//									: i /* 平民より上 */));
//		}
//		// たとえば，大貧民なら D3 D4
//		// たとえば，大富豪なら JOKER S2
		return result;
	}
	
	//HT
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
	//HT
	
//	public static ArrayList<MeldSet> parseMelds(Cards cards) {
//		Melds sequence = Melds.parseSequenceMelds(cards);
//		// group meld
//		MeldSet meldSet = parseGroupMelds(cards);
//		
//		if(sequence.isEmpty()) {
//			ArrayList<MeldSet> list = new ArrayList<MeldSet>();
//			list.add(meldSet);
//			return list;
//		} else {
//			Meld meld = sequence.get(0);
//
//			cards = cards.remove(meld.asCards());
//			
//			// sequence を採用した MeldSet のリスト
//			ArrayList<MeldSet> list = parseMelds(cards);
//			
//			// sequence をそれぞれの要素に加える
//			for(MeldSet set: list) {
//				set.sequence = set.sequence.add(meld);
//			}
//			
//			// sequence を採用しない  MeldSet もリストに加える
//			list.add(meldSet);
//			
//			return list;
//		}
//	}
//	 
//	
//	/**
//	 * GroupMelds を見つけ出す
//	 * 最大枚数の GoupMeld だけを見つける
//	 * @param cards
//	 * @return Groupが設定されたMeldSet 
//	 */
//	public static MeldSet parseGroupMelds(Cards cards) {
//		// JOKER があった場合、取り除く
//		cards = cards.remove(Card.JOKER);
//		
//		MeldSet set = new MeldSet();
//		
//		for(Card card: cards) {
//			int rank = card.rank().toInt() - 3;
//			int suit = card.suit().ordinal();
//			set.cards[suit][rank] = card;
//		}
//		
//		return set;
//	}
//	
//	/**
//	 * カード集合から、JOKER と組み合わせ役のカードを除く
//	 * @param cards 対象とするカード集合
//	 * @return 組み合わせ役が除かれたカード集合
//	 */
//	public static Cards removeCombinationMelds(Cards cards) {
//		// JOKER を除く
//		cards = cards.remove(Card.JOKER);
//
//		ArrayList<MeldSet> meldSets = parseMelds(cards);
//		
//		MeldSet min = meldSets.get(0);
//		int size = min.size();
//		
//		for(int i = 1; i < meldSets.size(); i++) {
//			int tmp = meldSets.get(i).size();
//			if(size > tmp) {
//				size = tmp;
//				min = meldSets.get(i);
//			}
//		}
//		
//		// 階段役を抽出
//		Melds sequenceMelds = min.sequence;
//		
//		// 階段役を取り除く
//		for(Meld meld: sequenceMelds) {
//			cards = cards.remove(meld.asCards());
//		}
//		
//		// グループ役を抽出 
//		Melds groupMelds = Melds.parseGroupMelds(cards);
//
//		// グループ役を取り除く
//		for(Meld meld: groupMelds) {
//			for(Card card: meld.asCards()) {
//				cards = cards.remove(card);
//			}
//		}
//
//		return cards;
//	}

}
