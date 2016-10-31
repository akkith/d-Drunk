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
	FinalStage finStage = new FinalStage();
	PatternMake patMaker = new PatternMake();
	// HT
	FirstStage firstStage = new FirstStage();
	PlayedCardList cardList;

	// シークエンス記録変数
	int seqNum = 0;
	// 最初の一回の設定用
	boolean isGameStart = false;

	//デバッグ表示用
	boolean showFlag = true;
	// 時間計測用
	boolean timerFlag = true;

	/*
	 * public Cards requestingGivingCards(){
	 * 
	 * return null;
	 */
	public void gameStarted() {
		super.gameStarted();
		cardList = new PlayedCardList();
		isGameStart = false;
		// 年のため二回目
		seqNum = 0;
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
		if(showFlag){
			System.out.println("Player:" + this.number());
		}
		long start = 0, end = 0; // 時間計測用
		if (timerFlag)
			start = System.currentTimeMillis();
		// 初回のみ手札のカードを未登場カードリストから除く
		if (!isGameStart) {
			isGameStart = true;
			cardList.updateList(this.hand());
		}

		// 現在のカード状況で考えられる役を登録
		cardList.updateMeldsList();

		// 場の状況
		Place place = this.place();
		// ルール
		Rules rules = this.rules();
		// 役の作成
		//Melds melds = patMaker.patMake(this.hand(), place);
		Melds melds;
		Meld playMeld = PASS;
		//提出用の役
		//Meld playMeld = FirstStage.requestingPlay(melds, place, rules);
		
		if(seqNum == 0){
			if(showFlag){
				System.out.println("==sequence second==");
			}
			melds = patMaker.patMake(this.hand(), place);
			playMeld = secStage.requestingPlay(melds, place, rules, cardList);
			if(melds.size() <= 4 && playMeld != PASS){
				++seqNum;
			}
		}else{
			if(showFlag){
				System.out.println("==sequence final==");
			}
			melds = patMaker.patMakeFinal(this.hand(), place, rules);
			if(showFlag){
				System.out.println(melds.toString());
			}
			if(melds == Melds.EMPTY_MELDS){
				return PASS;
			}
			playMeld = finStage.requestingPlay(melds, place, rules, cardList);
		}
		//return defaultStrategy.requestingPlay(melds, place, rules);
		
		cardList.updateList(playMeld);
		// cardList.showCards();

		// 自分の使ったカードを記録
		cardList.setLastPlayedMeld(playMeld);

		if (timerFlag) {
			end = System.currentTimeMillis();
			//cardList.showDetail();
			System.out.println((end - start) + "ms");
		}
		return playMeld;
	}

}
