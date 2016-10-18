import static jp.ac.uec.daihinmin.card.MeldFactory.PASS;
import static jp.ac.uec.daihinmin.card.MeldFactory.createSingleMeldJoker;
import jp.ac.uec.daihinmin.player.*;
import jp.ac.uec.daihinmin.*;
import jp.ac.uec.daihinmin.card.*;

import chs.daihinmin.TeamGAI.strategy.*;
import chs.daihinmin.TeamGAI.base.*;

public class dDrunk extends BotSkeleton {
	Default defaultStrategy = new Default();
	Dashiosimi nomalStrategy = new Dashiosimi();
	PatternMake patMaker = new PatternMake();

	/*
	 * public Cards requestingGivingCards(){
	 * 
	 * return null; }
	 */
	public Cards requestingGivingCards() {
		Cards hand = this.hand();
		Rules rules = this.rules();
		int rank = this.rank();
		
		//Cards result = Default.requestingGivingCards();
		Cards result = Dashiosimi.requestingGivingCards(hand, rules, rank);
		return result;
	}

	public Meld requestingPlay() { 
		// 役の作成
		//Melds melds = Melds.parseMelds(this.hand());
		Melds melds = patMaker.patMake(this.hand());
		// 場の状況
		Place place = this.place();								
		// ルール
		Rules rules = this.rules();

		//提出用の役
		Meld playMeld = nomalStrategy.requestingPlay(melds, place, rules);
		
		//return defaultStrategy.requestingPlay(melds, place, rules);
		return playMeld;
	}

}
