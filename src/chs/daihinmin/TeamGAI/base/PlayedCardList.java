package chs.daihinmin.TeamGAI.base;

import jp.ac.uec.daihinmin.*;
import static jp.ac.uec.daihinmin.card.MeldFactory.*;
import jp.ac.uec.daihinmin.card.*;

public class PlayedCardList {
	// 自分の持っていないカード集合
	Cards cardList = null;
	// 自分の持っていないカードでできる役集合
	Melds meldsList = null;
	// 最後に自分が出した役
	public Meld lastPlayedMeld = null;
	// ジョーカーが出たか否か
	public boolean jokerFlag = true;
	//残りタイプ別組数
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
		if (dammyCheck) {
			dammyCards = cardList.extract(Cards.JOKERS.not());
			dammyCards = dammyCards.extract(Cards.rankUnder(Rank.valueOf(10)));
		}
	}

	// 使ったカードを抜く
	public void updateList(Meld meld) {
		cardList = cardList.remove(meld.asCards());
		if (meld.asCards().contains(Card.JOKER)) {
			jokerFlag = false;
		}
		if (dammyCheck) {
			dammyCards = dammyCards.remove(meld.asCards());
		}
	}

	public void updateList(Cards cards) {
		cardList = cardList.remove(cards);
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
		Melds singleMelds;
		Melds groupMelds;
		Melds sequenceMelds;
		meldsList = Melds.EMPTY_MELDS;

		singleMelds = Melds.parseSingleMelds(cardList);
		groupMelds = Melds.parseGroupMelds(cardList);
		sequenceMelds = Melds.parseSequenceMelds(cardList);
		meldsList = meldsList.add(singleMelds);
		meldsList = meldsList.add(groupMelds);
		meldsList = meldsList.add(sequenceMelds);
		
		//数を数える
		namOfSingle = singleMelds.size();
		namOfGroup = groupMelds.size();
		namOfSequence = sequenceMelds.size();

		if (dammyCheck) {
			// meldsList = Melds.parseMelds(dammyCards);
			meldsList = Melds.EMPTY_MELDS;
			singleMelds = Melds.parseSingleMelds(dammyCards);
			groupMelds = Melds.parseGroupMelds(dammyCards);
			sequenceMelds = Melds.parseSequenceMelds(dammyCards);
		}
		meldsList = meldsList.add(singleMelds);
		meldsList = meldsList.add(groupMelds);
		meldsList = meldsList.add(sequenceMelds);

		// meldsList = Melds.parseMelds(cardList);
		/*
		 * if(showFlag){ System.out.println("melds");
		 * System.out.println(meldsList.toString()); }
		 */
	}

	//
	public int getNamOfType(Meld.Type type){
		if(type == Meld.Type.SINGLE){
			return namOfSingle;
		}else if(type == Meld.Type.GROUP){
			return namOfGroup;
		}else if(type == Meld.Type.SEQUENCE){
			return namOfSequence;
		}
		
		return -1;
	}
	
	
	//最後に出した組を覚える
	public void setLastPlayedMeld(Meld meld) {
		lastPlayedMeld = meld;
	}

	//手札の点数の平均値
	public double calcMeldsVps(Melds melds, Rules rules, Order order) {
		int size = melds.size();
		double value = 0;
		for (Meld meld : melds) {
			value += calcMeldValue(meld, rules, order);
		}
		return value / melds.size();
	}

	// 引数の役より強い役の数を返す
	public int calcMeldValue(Meld meld, Rules rules, Order order) {
		/*
		 * if(showFlag){ System.out.println("calcMeldValue in Meld :" +
		 * meld.toString()); System.out.println("melds type :" + meld.type()); }
		 */
		// Melds melds;
		
		if(meld.rank() == Rank.EIGHT){
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
