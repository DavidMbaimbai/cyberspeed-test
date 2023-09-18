package com.david.mbaimbai;

import static java.util.List.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.*;

public class ScratchGameTest {

    @Test
    public void testParseConfigurationValidFile() {
        Configuration configuration = ScratchGame.parseConfiguration("src/test/resources/valid_config.json");
        assertNotNull(configuration);
        assertEquals(2, configuration.getSymbols().size());
        assertEquals(2, configuration.getProbabilities().getStandardSymbols().size());
        assertEquals(1, configuration.getProbabilities().getBonusSymbols().size());
        assertEquals(1, configuration.getProbabilities().getWinCombinations().size());
    }

    @Test
    public void testParseConfigurationInvalidFile() {
        Configuration configuration = ScratchGame.parseConfiguration("src/test/resources/non_existent_config.json");
        assertNull(configuration);
    }

    @Test
    public void testGenerateMatrix() {
        Map<String, Integer> standardSymbolProbabilities = new HashMap<>();
        standardSymbolProbabilities.put("A", 10);
        standardSymbolProbabilities.put("B", 20);
        Map<String, Integer> bonusSymbolProbabilities = new HashMap<>();
        bonusSymbolProbabilities.put("X", 5);
        bonusSymbolProbabilities.put("Y", 15);
        Probability probabilities = new Probability(standardSymbolProbabilities, bonusSymbolProbabilities);
        Map<String, Integer> standardSymbols = new HashMap<>();
        standardSymbols.put("A", 50);
        standardSymbols.put("B", 50);
        probabilities.setStandardSymbols(standardSymbols);
        List<List<String>> matrix = ScratchGame.generateMatrix(3, 3, probabilities);
        assertNotNull(matrix);
        assertEquals(3, matrix.size());
        assertEquals(3, matrix.get(0).size());
    }

    @Test
    public void testCalculateRewards() {
        Map<String, Integer> standardSymbolProbabilities = new HashMap<>();
        standardSymbolProbabilities.put("A", 10);
        standardSymbolProbabilities.put("B", 20);
        Map<String, Integer> bonusSymbolProbabilities = new HashMap<>();
        bonusSymbolProbabilities.put("X", 5);
        bonusSymbolProbabilities.put("Y", 15);
        Probability probabilities = new Probability(standardSymbolProbabilities, bonusSymbolProbabilities);
        Map<String, Integer> standardSymbols = new HashMap<>();
        standardSymbols.put("A", 50);
        standardSymbols.put("B", 50);
        probabilities.setStandardSymbols(standardSymbols);

        Map<String, Symbol> symbols = new HashMap<>();
        Symbol symbolA = new Symbol("A", 2, "standard", 0, "");
        symbols.put("A", symbolA);
        Symbol symbolB = new Symbol("B", 3, "standard", 0, "");
        symbols.put("B", symbolB);

        Map<String, WinCombination> winCombinations = new HashMap<>();
        WinCombination winCombination = new WinCombination("same_symbol_3_times", 2, 3, "A", new ArrayList<>(), "same_symbols");
        winCombinations.put("same_symbol_3_times", winCombination);

        List<List<String>> matrix = new ArrayList<>();
        List<String> row1 = new ArrayList<>();
        row1.add("A");
        row1.add("A");
        row1.add("A");
        matrix.add(row1);

        int reward = ScratchGame.calculateRewards(matrix, probabilities, symbols, winCombinations);
        assertEquals(18, reward);
    }

    @Test
    public void testSelectRandomBonusSymbol() {
        Map<String, Integer> bonusSymbolProbabilities = new HashMap<>();
        bonusSymbolProbabilities.put("X", 30);
        bonusSymbolProbabilities.put("Y", 40);
        bonusSymbolProbabilities.put("Z", 30);

        String selectedBonusSymbol = ScratchGame.selectRandomBonusSymbol(bonusSymbolProbabilities);
        assertNotNull(selectedBonusSymbol);
        assertTrue(bonusSymbolProbabilities.containsKey(selectedBonusSymbol));
    }

    @Test
    public void testSelectRandomBonusSymbolEmptyProbabilities() {
        Map<String, Integer> bonusSymbolProbabilities = new HashMap<>();

        String selectedBonusSymbol = ScratchGame.selectRandomBonusSymbol(bonusSymbolProbabilities);
        assertNull(selectedBonusSymbol);
    }

    @Test
    public void testSelectRandomBonusSymbolNullProbabilities() {
        String selectedBonusSymbol = ScratchGame.selectRandomBonusSymbol(null);
        assertNull(selectedBonusSymbol);
    }

    @Test
    public void testApplyBonusSymbolEffectsMultiplyReward() {
        Map<String, Symbol> symbols = new HashMap<>();
        List<List<String>> matrix = new ArrayList<>();
        List<String> row = new ArrayList<>();
        row.add("A");
        matrix.add(row);
        Symbol symbolA = new Symbol("A", 2, "standard", 0, "multiply_reward");
        symbols.put("A", symbolA);

        ScratchGame.applyBonusSymbolEffects("A", symbols, matrix);
        assertEquals("4", matrix.get(0).get(0));
    }

    @Test
    public void testApplyBonusSymbolEffectsExtraBonus() {
        Map<String, Symbol> symbols = new HashMap<>();
        List<List<String>> matrix = new ArrayList<>();
        List<String> row = new ArrayList<>();
        row.add("A");
        matrix.add(row);
        Symbol symbolA = new Symbol("A", 2, "standard", 3, "extra_bonus");
        symbols.put("A", symbolA);

        ScratchGame.applyBonusSymbolEffects("A", symbols, matrix);
        assertEquals("5", matrix.get(0).get(0));
    }

    @Test
    public void testApplyBonusSymbolEffectsNoEffect() {
        Map<String, Symbol> symbols = new HashMap<>();
        List<List<String>> matrix = new ArrayList<>();
        List<String> row = new ArrayList<>();
        row.add("A");
        matrix.add(row);
        Symbol symbolA = new Symbol("A", 2, "standard", 0, "no_effect");
        symbols.put("A", symbolA);

        ScratchGame.applyBonusSymbolEffects("A", symbols, matrix);
        assertEquals("2", matrix.get(0).get(0));
    }

    @Test
    public void testMatrixMatchesCombinationSameSymbols() {
        WinCombination winCombination = new WinCombination("same_symbol_3_times", 2, 3, "A", new ArrayList<>(), "same_symbols");

        List<List<String>> matrix = new ArrayList<>();
        List<String> row1 = new ArrayList<>();
        row1.add("A");
        row1.add("A");
        row1.add("A");
        matrix.add(row1);

        boolean result = ScratchGame.matrixMatchesCombination(matrix, "A", winCombination);
        assertTrue(result);
    }

    @Test
    public void testMatrixMatchesCombinationLinearSymbols() {
        List<List<String>> coveredAreas = new ArrayList<>();
        coveredAreas.add(Arrays.asList("0:0", "0:1"));
        WinCombination winCombination = new WinCombination("horizontal_2_in_a_row", 3, 2, "A", coveredAreas, "linear_symbols");
        List<List<String>> matrix = new ArrayList<>();
        List<String> row1 = new ArrayList<>();
        row1.add("A");
        row1.add("A");
        row1.add("B");
        matrix.add(row1);
        List<String> row2 = new ArrayList<>();
        row2.add("B");
        row2.add("A");
        row2.add("A");
        matrix.add(row2);

        boolean result = ScratchGame.matrixMatchesCombination(matrix, "A", winCombination);
        assertTrue(result);
    }
}
