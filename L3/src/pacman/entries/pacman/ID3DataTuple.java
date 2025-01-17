package pacman.entries.pacman;

import dataRecording.DataTuple;
import pacman.game.Constants;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ID3DataTuple extends DataTuple {

	public void discretizeAll() {





		ID3currentScore =  discretizeCurrentScore(currentScore);
		//ID3currentLevelTime = discretizeCurrentLevelTime(currentLevelTime);
		ID3numOfPillsLeft = discretizeNumberOfPills(numOfPillsLeft);
		ID3numOfPowerPillsLeft = discretizeNumberOfPowerPills(numOfPowerPillsLeft);
		ID3blinkyDist = discretizeDistance(blinkyDist);
		ID3inkyDist = discretizeDistance(inkyDist);
		ID3pinkyDist = discretizeDistance(pinkyDist);
		ID3sueDist = discretizeDistance(sueDist);


		attributes.add(ID3currentScore.toString());
		//attributes.add(ID3currentLevelTime.toString());
		//attributes.add(ID3numOfPillsLeft.toString());
		attributes.add(ID3numOfPowerPillsLeft.toString());


		//attributes.add(String.valueOf(this.isBlinkyEdible));
		//attributes.add(String.valueOf(this.isInkyEdible));
		//attributes.add(String.valueOf(this.isPinkyEdible));
		//attributes.add(String.valueOf(this.isSueEdible));

		attributes.add(vulnerableTag(isBlinkyEdible,ID3blinkyDist));
		attributes.add(vulnerableTag(isInkyEdible,ID3inkyDist));
		attributes.add(vulnerableTag(isPinkyEdible,ID3pinkyDist));
		attributes.add(vulnerableTag(isSueEdible,ID3sueDist));




		attributes.add(ID3blinkyDist.toString());
		attributes.add(ID3inkyDist.toString());
		attributes.add(ID3pinkyDist.toString());
		attributes.add(ID3sueDist.toString());
		attributes.add(DirectionChosen.toString());

		labels.add("currentScore");
		//labels.add("currentLevelTime");
		//labels.add("numOfPillsLeft");
		labels.add("numOfPowerPillsLeft");

		//labels.add("isBlinkyEdible");
		//labels.add("isInkyEdible");
		//labels.add("isPinkyEdible");
		//labels.add("isSueEdible");

		labels.add("isBlinkyVulnerable");
		labels.add("isInkyVulnerable");
		labels.add("isPinkyVulnerable");
		labels.add("isSueVulnerable");

		labels.add("blinkyDist");
		labels.add("inkyDist");
		labels.add("pinkyDist");
		labels.add("sueDist");
		labels.add("directionChosen");
	}

	public String vulnerableTag(boolean isEdible, DiscreteTag tag) {
		String returnTagString = null;
		ArrayList<DiscreteTag> tags = new ArrayList<>(Arrays.asList(DiscreteTag.values()));
		tags.indexOf(tag);
		ArrayList<DiscreteTag> revTags = new ArrayList<>();
		for (int i = tags.size() - 1; i >= 0; i--) {
			revTags.add(tags.get(i));
		}

		if (isEdible) {
			returnTagString = (String.valueOf(revTags.get(tags.indexOf(tag))));
		} else {
			returnTagString = (DiscreteTag.LOW.name());
		}
		return returnTagString;
	}



	List<String> labels = new ArrayList<>();
	List<String> attributes = new ArrayList<>();

	public List<String> getLabels() {
		return labels;
	}

	public List<String> getAttributes() {
		return attributes;
	}


	public MOVE DirectionChosen;


	DiscreteTag ID3currentScore = null;
	DiscreteTag ID3currentLevelTime = null;
	DiscreteTag ID3numOfPillsLeft = null;
	DiscreteTag ID3numOfPowerPillsLeft = null;
	DiscreteTag ID3blinkyDist = null;
	DiscreteTag ID3inkyDist = null;
	DiscreteTag ID3pinkyDist = null;
	DiscreteTag ID3sueDist = null;




	// General game state this - not normalized!
	public int mazeIndex;
	public int currentLevel;
	public int pacmanPosition;
	public int pacmanLivesLeft;
	public int currentScore;
	public int totalGameTime;
	public int currentLevelTime;
	public int numOfPillsLeft;
	public int numOfPowerPillsLeft;

	// Ghost this, dir, dist, edible - BLINKY, INKY, PINKY, SUE
	public boolean isBlinkyEdible = false;
	public boolean isInkyEdible = false;
	public boolean isPinkyEdible = false;
	public boolean isSueEdible = false;

	public int blinkyDist = -1;
	public int inkyDist = -1;
	public int pinkyDist = -1;
	public int sueDist = -1;

	public MOVE blinkyDir;
	public MOVE inkyDir;
	public MOVE pinkyDir;
	public MOVE sueDir;

	// Util data - useful for normalization
	public int numberOfNodesInLevel;
	public int numberOfTotalPillsInLevel;
	public int numberOfTotalPowerPillsInLevel;
	private int maximumDistance = 150;

	public ID3DataTuple(Game game, MOVE move) {
		super(game, move);
		if (move == MOVE.NEUTRAL) {
			move = game.getPacmanLastMoveMade();
		}

		this.DirectionChosen = move;

		this.mazeIndex = game.getMazeIndex();
		this.currentLevel = game.getCurrentLevel();
		this.pacmanPosition = game.getPacmanCurrentNodeIndex();
		this.pacmanLivesLeft = game.getPacmanNumberOfLivesRemaining();
		this.currentScore = game.getScore();
		this.totalGameTime = game.getTotalTime();
		this.currentLevelTime = game.getCurrentLevelTime();
		this.numOfPillsLeft = game.getNumberOfActivePills();
		this.numOfPowerPillsLeft = game.getNumberOfActivePowerPills();



		if (game.getGhostLairTime(GHOST.BLINKY) == 0) {
			this.isBlinkyEdible = game.isGhostEdible(GHOST.BLINKY);
			this.blinkyDist = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(GHOST.BLINKY));
		}

		if (game.getGhostLairTime(GHOST.INKY) == 0) {
			this.isInkyEdible = game.isGhostEdible(GHOST.INKY);
			this.inkyDist = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(GHOST.INKY));
		}

		if (game.getGhostLairTime(GHOST.PINKY) == 0) {
			this.isPinkyEdible = game.isGhostEdible(GHOST.PINKY);
			this.pinkyDist = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(GHOST.PINKY));
		}

		if (game.getGhostLairTime(GHOST.SUE) == 0) {
			this.isSueEdible = game.isGhostEdible(GHOST.SUE);
			this.sueDist = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(GHOST.SUE));
		}

		this.blinkyDir = game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(GHOST.BLINKY), DM.PATH);
		this.inkyDir = game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(GHOST.INKY), DM.PATH);
		this.pinkyDir = game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(GHOST.PINKY), DM.PATH);
		this.sueDir = game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(GHOST.SUE), DM.PATH);

		this.numberOfNodesInLevel = game.getNumberOfNodes();
		this.numberOfTotalPillsInLevel = game.getNumberOfPills();
		this.numberOfTotalPowerPillsInLevel = game.getNumberOfPowerPills();


	}

	public ID3DataTuple(String data) {
		super(data);
		String[] dataSplit = data.split(";");

		this.DirectionChosen = MOVE.valueOf(dataSplit[0]);
		this.mazeIndex = Integer.parseInt(dataSplit[1]);
		this.currentLevel = Integer.parseInt(dataSplit[2]);
		this.pacmanPosition = Integer.parseInt(dataSplit[3]);
		this.pacmanLivesLeft = Integer.parseInt(dataSplit[4]);
		this.currentScore = Integer.parseInt(dataSplit[5]);
		this.totalGameTime = Integer.parseInt(dataSplit[6]);
		this.currentLevelTime = Integer.parseInt(dataSplit[7]);
		this.numOfPillsLeft = Integer.parseInt(dataSplit[8]);
		this.numOfPowerPillsLeft = Integer.parseInt(dataSplit[9]);
		this.isBlinkyEdible = Boolean.parseBoolean(dataSplit[10]);
		this.isInkyEdible = Boolean.parseBoolean(dataSplit[11]);
		this.isPinkyEdible = Boolean.parseBoolean(dataSplit[12]);
		this.isSueEdible = Boolean.parseBoolean(dataSplit[13]);
		this.blinkyDist = Integer.parseInt(dataSplit[14]);
		this.inkyDist = Integer.parseInt(dataSplit[15]);
		this.pinkyDist = Integer.parseInt(dataSplit[16]);
		this.sueDist = Integer.parseInt(dataSplit[17]);
		this.blinkyDir = MOVE.valueOf(dataSplit[18]);
		this.inkyDir = MOVE.valueOf(dataSplit[19]);
		this.pinkyDir = MOVE.valueOf(dataSplit[20]);
		this.sueDir = MOVE.valueOf(dataSplit[21]);
		this.numberOfNodesInLevel = Integer.parseInt(dataSplit[22]);
		this.numberOfTotalPillsInLevel = Integer.parseInt(dataSplit[23]);
		this.numberOfTotalPowerPillsInLevel = Integer.parseInt(dataSplit[24]);
	}


}
