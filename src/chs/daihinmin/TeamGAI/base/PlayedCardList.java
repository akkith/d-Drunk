package chs.daihinmin.TeamGAI.base;

import jp.ac.uec.daihinmin.*;
import static jp.ac.uec.daihinmin.card.MeldFactory.*;
import jp.ac.uec.daihinmin.card.*;

public class PlayedCardList {
	Cards cardList = null;

	// 全カードのリストを作成
	public PlayedCardList() {
		cardList = Card.values();
	}

	// 使ったカードを抜く
	public void updateList(Meld meld) {
		cardList = cardList.remove(meld.asCards());
	}

	// 引数の役より強い役の数を返す
	public int calcMeldValue(Meld meld, Rules rules, Order order) {
		Melds melds = Melds.parseMelds(cardList);
				Rank next_rank;
		try {
			next_rank = meld.type() == Meld.Type.SEQUENCE
					? rules.nextRankSequence(meld.rank(), meld.asCards().size(), order)
					: order == Order.NORMAL ? meld.rank().higher() : meld.rank().lower();
		} catch (IllegalArgumentException e) {
			return 0;
		}
		melds = melds.extract(Melds.typeOf(meld.type()).and(Melds.sizeOf(meld.asCards().size())
				.and(Melds.rankOf(next_rank).or(order == Order.NORMAL ? Melds.rankUnder(next_rank)
						: Melds.rankOver(next_rank)))));
		
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

}
