import jp.ac.uec.daihinmin.player.*;
import jp.ac.uec.daihinmin.*;
import jp.ac.uec.daihinmin.card.*;

import chs.daihinmin.TeamGAI.strategy.*;

public class dDrunk extends BotSkeleton {
	Default defaultStrategy = new Default();
	/*
	 * public Cards requestingGivingCards(){
	 * 
	 * return null; }
	 */
	public Cards requestingGivingCards() {
		Cards result = Cards.EMPTY_CARDS;
		// 手札を昇順にソート．たとえば，JOKER S2 HA ... D4 D3
		Cards sortedHand = Cards.sort(this.hand());
		// 渡すカードの枚数だけ，resultにカードを追加
		for (int i = 0; i < Rules.sizeGivenCards(this.rules(), this.rank()); i++) {
			result = result.add(
					sortedHand.get(/* 平民より上か？ 注:07年度のルールでは平民以上の時のみ選ぶことができる */
							Rules.heiminRank(this.rules()) < this.rank() ? sortedHand.size() - 1 - i /* 平民より下 */
									: i /* 平民より上 */));
		}
		// たとえば，大貧民なら D3 D4
		// たとえば，大富豪なら JOKER S2
		return result;
	}

	public Meld requestingPlay() {
		// 役の作成
		Melds melds = Melds.parseMelds(this.hand());
		// 場の状況
		Place place = this.place();
		// ルール
		Rules rules = this.rules();

		return defaultStrategy.requestingPlay(melds, place, rules);
	}

}
