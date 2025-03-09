
package tdd.bsk.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tdd.bsk.*;

public class tests {

	/*
	 * There is no limit of the number of test cases.
	 * The goal is to TEST the implementation as thorough as possible.
	 *
	 * Check the README.md in Task 2 for the expected program behavior.
	 *
	 * There is at least ONE bug in the code. Try to find them out!
	 * You DO NOT need to correct the implementation!
	 *
	 * NOTE: Found 3 bugs (resulting in 5 failed test cases below):
	 * 	1. implementation of adding bonus points whenever
	 * 	2. implementation of having two strikes in the bonus
	 * 	3. implementation of adding multiple bonus frames
	 * (MORE INFO below in the test cases!)
	 *
	 * Online Bowling Game Score Calculator: https://bowlinggenius.com
	 */

	private BowlingGame game;

	@BeforeEach
	public void setup() {
		game = new BowlingGame();
	}

	/** Req 1: A frame consists of two throws */
	@Test
	public void testFrameHasTwoThrows() throws BowlingException {
		Frame frame = new Frame(4, 5);
		assertEquals(4, frame.getFirstThrow());
		assertEquals(5, frame.getSecondThrow());
	}

	/** Req 2 & 3: Test valid range (0-10) for first and second throw */
	@Test
	public void testValidThrowsInFrame() throws BowlingException {
		Frame frame = new Frame(0, 10);
		assertEquals(0, frame.getFirstThrow());
		assertEquals(10, frame.getSecondThrow());
	}

	/** Req 4: Frame score should be the sum of two throws */
	@Test
	public void testFrameScoreCalculation() throws BowlingException {
		Frame frame = new Frame(3, 6);
		assertEquals(9, frame.score());
	}

	/** Req 5: Detecting a strike */
	@Test
	public void testStrikeDetection() throws BowlingException {
		Frame frame = new Frame(10, 0);
		assertTrue(frame.isStrike());
	}

	/** Req 6: Strike bonus calculation */
	@Test
	public void testStrikeBonus() throws BowlingException {
		game.addFrame(new Frame(10, 0)); // Strike
		game.addFrame(new Frame(5, 3));  // Next frame
		assertEquals(26, game.score());
	}

	/** Req 7: Detecting a spare */
	@Test
	public void testSpareDetection() throws BowlingException {
		Frame frame = new Frame(6, 4);
		assertTrue(frame.isSpare());
	}

	/** Req 8: Spare bonus calculation */
	@Test
	public void testSpareBonus() throws BowlingException {
		game.addFrame(new Frame(7, 3)); // Spare
		game.addFrame(new Frame(4, 2)); // Next frame
		assertEquals(20, game.score());
	}

	/** Req 9: A game consists of 10 frames */
	@Test
	public void testGameHasTenFrames() throws BowlingException {
		for (int i = 0; i < 10; i++) {
			game.addFrame(new Frame(4, 5));
		}
		assertTrue(game.isLastFrame());
	}

	/** Req 10: Game score calculation */
	@Test
	public void testGameScoreCalculation() throws BowlingException {
		game.addFrame(new Frame(4, 5));
		game.addFrame(new Frame(7, 2));
		game.addFrame(new Frame(3, 6));
		assertEquals(27, game.score());
	}

	/** Req 11: Spare in the 10th frame allows a bonus throw */
	@Test
	public void testTenthFrameSpareBonus() throws BowlingException {
		for (int i = 0; i < 9; i++) {
			game.addFrame(new Frame(4, 5));
		}
		game.addFrame(new Frame(7, 3)); // Spare
		game.setBonus(5, 0);
		assertEquals(96, game.score());
	}

	/** Req 12: Strike in the 10th frame allows two bonus throws */
	@Test
	public void testTenthFrameStrikeBonus() throws BowlingException {
		for (int i = 0; i < 9; i++) {
			game.addFrame(new Frame(4, 5));
		}
		game.addFrame(new Frame(10, 0)); // Strike
		game.setBonus(6, 3);
		assertEquals(100, game.score());
	}

	/** Req 13: Ensure bonus throws are added to the total score */
	// PROBLEM FOUND HERE: 🚨
	// 	When the 10th frame is a strike, the player gets two bonus throws.
	//	However, the Frame constructor rejects setBonus(10, 10),
	//	because 10 + 10 > 10, violating its rule.
	//	But in the 10th frame, a bonus frame can have a total of 20 points.
	@Test
	public void testBonusThrowAddedToScore() throws BowlingException {
		for (int i = 0; i < 9; i++) {
			game.addFrame(new Frame(5, 4));
		}
		game.addFrame(new Frame(10, 0)); // Strike
		game.setBonus(10, 10);
		assertEquals(111, game.score());
	}

	/** Edge case: Perfect game (all strikes) */
	// SAME PROBLEM FROM REQ 13 FOUND HERE TOO 🚨
	@Test
	public void testPerfectGame() throws BowlingException {
		for (int i = 0; i < 10; i++) {
			game.addFrame(new Frame(10, 0));
		}
		game.setBonus(10, 10);
		assertEquals(300, game.score());
	}

	/** Edge case: Gutter game (all zeroes) */
	@Test
	public void testGutterGame() throws BowlingException {
		for (int i = 0; i < 10; i++) {
			game.addFrame(new Frame(0, 0));
		}
		assertEquals(0, game.score());
	}

	/** Invalid input: Throws greater than 10 should throw an exception */
	@Test
	public void testInvalidFirstThrow() {
		assertThrows(BowlingException.class, () -> new Frame(11, 0));
	}

	@Test
	public void testInvalidSecondThrow() {
		assertThrows(BowlingException.class, () -> new Frame(5, 6)); // Exceeds 10 total
	}

	/** Invalid input: Negative throws should throw an exception */
	@Test
	public void testNegativeThrow() {
		assertThrows(BowlingException.class, () -> new Frame(-1, 5));
	}

	/** Asserts Strike and Spare are not incorrectly identified */
	@Test
	public void testFrameIsNotStrikeOrSpare() throws Exception {
		Frame f = new Frame(3, 4);
		assertFalse(f.isStrike());
		assertFalse(f.isSpare());
	}

	/** Tests spare in last frame + bonus throw */
	@Test
	public void testGameScoreWithSpareInLastFrame() throws Exception {
		for (int i = 0; i < 9; i++) {
			game.addFrame(new Frame(2, 3));
		}
		game.addFrame(new Frame(5, 5)); // Spare in last frame
		game.setBonus(3, 0); // Bonus throw
		assertEquals(58, game.score());
	}

	/** Tests spare in last frame + bonus throws -> should not be allowed */
	// PROBLEM FOUND HERE: 🚨
	//	Should not be able to add 2 bonus throws if the last frame was a spare,
	//	but it fails to throw an exception.
	@Test
	public void testGameScoreWithExtraBonusThrows() throws Exception {
		for (int i = 0; i < 9; i++) {
			game.addFrame(new Frame(2, 3));
		}
		game.addFrame(new Frame(5, 5)); // Spare in last frame
		assertThrows(BowlingException.class, () -> game.setBonus(3, 4)); // 2 bonus throws...
	}

	/** Should not be able to add bonus if last frame was open */
	// PROBLEM FOUND HERE: 🚨
	//	Should not be able to add bonus if last frame was not a spare or strike,
	//	but does not throw an exception.
	@Test
	public void testInvalidBonusFrame() throws BowlingException {
		for (int i = 0; i < 9; i++) {
			game.addFrame(new Frame(5, 4));
		}
		game.addFrame(new Frame(3, 6)); // Open frame (not a strike or spare)

		// Setting bonus should **fail** because 10th frame was not a strike/spare
		assertThrows(BowlingException.class, () -> game.setBonus(10, 10));
	}

	/** Tests if next frame is a bonus frame */
	@Test
	public void testIsNextFrameBonus() throws BowlingException {
		for (int i = 0; i < 9; i++) {
			game.addFrame(new Frame(5, 4));
		}
		game.addFrame(new Frame(7, 2)); // Open frame in 10th

		// `isNextFrameBonus()` should return **false** since the 10th frame was NOT a strike/spare
		assertFalse(game.isNextFrameBonus());
	}

	/** Tests if an exception is thrown for duplicate bonus frames */
	// PROBLEM FOUND HERE: 🚨
	//	Should not be able to add multiple bonus frames, but implementation fails to throw an exception
	@Test
	public void testDuplicateBonusFrame() throws BowlingException {
		for (int i = 0; i < 9; i++) {
			game.addFrame(new Frame(5, 4));
		}
		game.addFrame(new Frame(10, 0)); // Strike in 10th frame

		game.setBonus(2, 3); // First bonus throw
		assertThrows(BowlingException.class, () -> game.setBonus(4, 5)); // Second attempt should fail
	}

	/** Tests strike in last frame + bonus throws */
	@Test
	public void testGameScoreWithStrikeInLastFrame() throws Exception {
		for (int i = 0; i < 9; i++) {
			game.addFrame(new Frame(2, 3));
		}
		game.addFrame(new Frame(10, 0)); // Strike in last frame
		game.setBonus(3, 4); // Bonus throws
		assertEquals(62, game.score());
	}

	/** Mixed game with strikes, spares, and open frames */
	@Test
	public void testMixedGame() throws BowlingException {
		game.addFrame(new Frame(10, 0));  // Strike
		game.addFrame(new Frame(7, 3));   // Spare
		game.addFrame(new Frame(4, 2));   // Open frame
		game.addFrame(new Frame(10, 0));  // Strike
		game.addFrame(new Frame(6, 2));   // Open frame
		game.addFrame(new Frame(10, 0));  // Strike
		game.addFrame(new Frame(5, 5));   // Spare
		game.addFrame(new Frame(9, 0));   // Open frame
		game.addFrame(new Frame(7, 2));   // Open frame
		game.addFrame(new Frame(10, 0));  // Strike
		game.setBonus(2, 5);            // Bonus throws

		assertEquals(140, game.score());
	}
}
