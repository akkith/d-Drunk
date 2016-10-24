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
	SecondStage secStage = new SecondStage();
	PatternMake patMaker = new PatternMake();
	PlayedCardList cardList;

	boolean isGameStart = false;

	/*
	 * public Cards requestingGivingCards(){
	 * 
	 * return null;
	 */
	public void gameStarted() {
		super.gameStarted();
		cardList = new PlayedCardList();
		isGameStart = false;
	}

	public void played(Integer num, Meld playedMeld) {
		super.played(num, playedMeld);
		// 場に出たカードを記録
		cardList.updateList(playedMeld);
	}

	public Cards requestingGivingCards() {
		Cards hand = this.hand();
		Rules rules = this.rules();
		int rank = this.rank();

		// Cards result = Default.requestingGivingCards();
		Cards result = Dashiosimi.requestingGivingCards(hand, rules, rank);
		return result;
	}

	public Meld requestingPlay() {
		//初回のみ手札のカードを未登場カードリストから除く
		if(!isGameStart){
			isGameStart = true;
			cardList.updateList(this.hand());
		}
		
		//現在のカード状況で考えられる役を登録
		cardList.updateMeldsList();
		
		// 役の作成
		//Melds melds = Melds.parseMelds(this.hand());
		Melds melds = patMaker.patMake(this.hand());
		// 場の状況
		Place place = this.place();								
		// ルール
		Rules rules = this.rules();

		//提出用の役
		Meld playMeld = secStage.requestingPlay(melds, place, rules, cardList);
		//Meld playMeld = nomalStrategy.requestingPlay(melds, place, rules);
		
		//return defaultStrategy.requestingPlay(melds, place, rules);
		cardList.updateList(playMeld);
		cardList.showCards();
		
		//自分の使ったカードを記録
		cardList.setLastPlayedMeld(playMeld);
		return playMeld;
	}

}
