package chs.daihinmin.TeamGAI.base;

import jp.ac.uec.daihinmin.*;
import static jp.ac.uec.daihinmin.card.MeldFactory.*;
import jp.ac.uec.daihinmin.card.*;

public class PlayedCardList {
	// 自分の持っていないカード集合
	Cards cardList = null;
	// 軽量版リスト
	Cards cardListLite = null;
	Cards cardListLiteRev = null;
	// 自分の持っていないカードでできる役集合
	Melds meldsList = null;
	Melds singleMeldsList = null;
	Melds groupMeldsList = null;
	Melds sequenceMeldsList = null;
	// 最後に自分が出した役
	public Meld lastPlayedMeld = PASS;
	// ジョーカーが出たか否か
	public boolean jokerFlag = true;
	// 残りタイプ別組数
	public int namOfSingle = 0;
	public int namOfGroup = 0;
	public int namOfSequence = 0;

	// デバッグ用
	boolean showFlag = false;
	boolean dammyCheck = false;
	// デバッグ用カードリスト
	public Cards dammyCards = null;

	// 全カードのリストを作成
	public PlayedCardList() {
		cardList = Card.values();
		cardList = cardList.extract(Cards.JOKERS.not());
		cardListLite = cardList.extract(Cards.rankUnder(Rank.TEN));
		cardListLiteRev = cardList.extract(Cards.rankOver(Rank.SEVEN));
		if (dammyCheck) {
			dammyCards = cardList.extract(Cards.JOKERS.not());
			dammyCards = dammyCards.extract(Cards.rankUnder(Rank.valueOf(10)));
		}
	}

	// 使ったカードを抜く
	public void updateList(Meld meld) {
		cardList = cardList.remove(meld.asCards());
		cardListLite = cardListLite.remove(meld.asCards());
		cardListLiteRev = cardListLiteRev.remove(meld.asCards());
		if (meld.asCards().contains(Card.JOKER)) {
			jokerFlag = false;
		}
		if (dammyCheck) {
			dammyCards = dammyCards.remove(meld.asCards());
		}
	}

	public void updateList(Cards cards) {
		cardList = cardList.remove(cards);
		cardListLite = cardListLite.remove(cards);
		cardListLiteRev = cardListLiteRev.remove(cards);
		if (cards.contains(Card.JOKER)) {
			jokerFlag = false;
		}
		if (dammyCheck) {
			dammyCards = dammyCards.remove(cards);
		}
	}

	// 持ってい無いカードでの役集合生成
	public void updateMeldsList() {
		/*
		 * if(showFlag){ if(!dammyCheck) System.out.println("Cars : " +
		 * cardList.toString()); else System.out.println("Cars : " +
		 * dammyCards.toString()); }
		 */

		// meldsList = Melds.parseMelds(cardList.extract(Cards.JOKERS.not()));
//		Melds singleMelds;
//		Melds groupMelds;
//		Melds sequenceMelds;
//		meldsList = Melds.EMPTY_MELDS;
		singleMeldsList = Melds.EMPTY_MELDS;
		groupMeldsList = Melds.EMPTY_MELDS;
		sequenceMeldsList = Melds.EMPTY_MELDS;
//		singleMelds = Melds.parseSingleMelds(cardList);
//		groupMelds = Melds.parseGroupMelds(cardList);
//		sequenceMelds = Melds.parseSequenceMelds(cardList);
//		meldsList = meldsList.add(singleMelds);
//		meldsList = meldsList.add(groupMelds);
//		meldsList = meldsList.add(sequenceMelds);
		
		singleMeldsList = Melds.parseSingleMelds(cardList);
		groupMeldsList = Melds.parseGroupMelds(cardList);
		sequenceMeldsList = Melds.parseSequenceMelds(cardList);

//		// 数を数える
//		namOfSingle = singleMelds.size();
//		namOfGroup = groupMelds.size();
//		namOfSequence = sequenceMelds.size();

		if (dammyCheck) {
			// meldsList = Melds.parseMelds(dammyCards);
			meldsList = Melds.EMPTY_MELDS;
			singleMeldsList = Melds.parseSingleMelds(dammyCards);
			groupMeldsList = Melds.parseGroupMelds(dammyCards);
			sequenceMeldsList = Melds.parseSequenceMelds(dammyCards);
		}
//		meldsList = meldsList.add(singleMelds);
//		meldsList = meldsList.add(groupMelds);
//		meldsList = meldsList.add(sequenceMelds);

		// meldsList = Melds.parseMelds(cardList);
		/*
		 * if(showFlag){ System.out.println("melds");
		 * System.out.println(meldsList.toString()); }
		 */
	}

	// 持ってい無いカードでの役集合生成(軽量版)
	public void updateMeldsListLite(Order order) {

		// meldsList = Melds.parseMelds(cardList.extract(Cards.JOKERS.not()));
		//Melds singleMelds;
		//Melds groupMelds;
		//Melds sequenceMelds;
		//meldsList = Melds.EMPTY_MELDS;
		meldsList = Melds.EMPTY_MELDS;
		singleMeldsList = Melds.EMPTY_MELDS;
		groupMeldsList = Melds.EMPTY_MELDS;
		sequenceMeldsList = Melds.EMPTY_MELDS;
		
		Cards cll;
		if(order == Order.NORMAL){
			cll = cardListLite;
		}else{
			cll = cardListLiteRev;
		}
		
		//singleMelds = Melds.parseSingleMelds(cll);
		//groupMelds = Melds.parseGroupMelds(cll);
		//sequenceMelds = Melds.parseSequenceMelds(cll);
		//meldsList = meldsList.add(singleMelds);
		//meldsList = meldsList.add(groupMelds);
		//meldsList = meldsList.add(sequenceMelds);
		
		singleMeldsList = Melds.parseSingleMelds(cll);
		groupMeldsList = Melds.parseGroupMelds(cll);
		sequenceMeldsList = Melds.parseSequenceMelds(cll);

//		// 数を数える
//		namOfSingle = singleMelds.size();
//		namOfGroup = groupMelds.size();
//		namOfSequence = sequenceMelds.size();

		meldsList = meldsList.add(singleMeldsList);
		meldsList = meldsList.add(groupMeldsList);
		meldsList = meldsList.add(sequenceMeldsList);
	}
	
	
//	public int getNamOfType(Meld.Type type) {
//		if (type == Meld.Type.SINGLE) {
//			return namOfSingle;
//		} else if (type == Meld.Type.GROUP) {
//			return namOfGroup;
//		} else if (type == Meld.Type.SEQUENCE) {
//			return namOfSequence;
//		}
//
//		return -1;
//	}

	// 最後に出した組を覚える
	public void setLastPlayedMeld(Meld meld) {
		lastPlayedMeld = meld;
	}

	// 手札の点数の平均値
	public double calcMeldsVps(Melds melds, Place place, Rules rules, Order order) {
		int size = melds.size();
		double value = 0;
		for (Meld meld : melds) {
			value += calcMeldValue(meld, place, rules, order);
		}
		return value / melds.size();
	}

	// 引数の役より強い役の数を返す
	public int calcMeldValue(Meld meld, Place place, Rules rules, Order order) {
		/*
		 * if(showFlag){ System.out.println("calcMeldValue in Meld :" +
		 * meld.toString()); System.out.println("melds type :" + meld.type()); }
		 */
		// Melds melds;

		switch(meld.type()){
		case SINGLE:
			meldsList = singleMeldsList;
			break;
		case GROUP:
			meldsList = singleMeldsList;
			break;
		case SEQUENCE:
			meldsList = singleMeldsList;
			break;
		default: 
			meldsList = Melds.EMPTY_MELDS;
			
		}
		
		if (meld.rank() == Rank.EIGHT) {
			return 0;
		}
		Rank next_rank;
		try {
			next_rank = meld.type() == Meld.Type.SEQUENCE
					? rules.nextRankSequence(meld.rank(), meld.asCards().size(), order)
					: order == Order.NORMAL ? meld.rank().higher() : meld.rank().lower();
		} catch (IllegalArgumentException e) {
			return 0;
		}
		Melds melds = meldsList
				.extract(Melds.typeOf(meld.type()).and(Melds.sizeOf(meld.asCards().size()).and(Melds.rankOf(next_rank)
						.or(order == Order.NORMAL ? Melds.rankOver(next_rank) : Melds.rankUnder(next_rank)))));
		if (place.lockedSuits() != Suits.EMPTY_SUITS) {
			melds = melds.extract(Melds.suitsOf(place.lockedSuits()));

		}

		return melds.size();
	}

	// 残っているカードを表示（デバッグ用）
	public void showCards() {
		// System.out.println("ALL Cards");
		// System.out.println(cardList.toString());
		Cards tcl = cardList.extract(Cards.JOKERS.not());
		Cards aCards = tcl.extract(Cards.suitOf(Suit.valueOf('S')));
		Cards dCards = tcl.extract(Cards.suitOf(Suit.valueOf('D')));
		Cards hCards = tcl.extract(Cards.suitOf(Suit.valueOf('H')));
		Cards cCards = tcl.extract(Cards.suitOf(Suit.valueOf('C')));

		System.out.println("spe:" + aCards.toString());
		System.out.println("dia:" + dCards.toString());
		System.out.println("hea" + hCards.toString());
		System.out.println("clu" + cCards.toString());
	}

	public void showDetail() {
		System.out.println("non used cards : " + cardList.size());
		System.out.println("non used melds : " + meldsList.size());
	}

}
